package com.vanvat.moviestream.view;

import com.vanvat.moviestream.controller.CategoryController;
import com.vanvat.moviestream.controller.FavoriteController;
import com.vanvat.moviestream.controller.HistoryController;
import com.vanvat.moviestream.controller.MovieController;
import com.vanvat.moviestream.controller.WatchlistController;
import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.exception.ValidationException;
import com.vanvat.moviestream.model.Category;
import com.vanvat.moviestream.model.Movie;
import com.vanvat.moviestream.model.UserMovieLink;
import com.vanvat.moviestream.model.WatchHistoryEntry;
import com.vanvat.moviestream.service.MovieFilter;
import com.vanvat.moviestream.service.MovieService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console-based View. Only responsible for printing and reading input —
 * every action is delegated to a Controller.
 *
 * NOTE: this demo runs as a single logged-in user (see CURRENT_USER)
 * since the requirement sheet doesn't specify an auth/session system.
 * All the per-user services already key off userId, so wiring in a real
 * login screen later is a small addition, not a redesign.
 */
public class ConsoleView {

    private static final String CURRENT_USER = "U-1";

    private final MovieController movieController;
    private final CategoryController categoryController;
    private final WatchlistController watchlistController;
    private final FavoriteController favoriteController;
    private final HistoryController historyController;
    private final ExportController exportController;
    private final InputHelper input;

    public ConsoleView(MovieController movieController,
                        CategoryController categoryController,
                        WatchlistController watchlistController,
                        FavoriteController favoriteController,
                        HistoryController historyController,
                        ExportController exportController,
                        Scanner scanner) {
        this.movieController = movieController;
        this.categoryController = categoryController;
        this.watchlistController = watchlistController;
        this.favoriteController = favoriteController;
        this.historyController = historyController;
        this.exportController = exportController;
        this.input = new InputHelper(scanner);
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Movie Streaming Management System ===");
            System.out.println("1. Movies");
            System.out.println("2. Categories");
            System.out.println("3. Watchlist");
            System.out.println("4. Favorites");
            System.out.println("5. Watching History & Reports");
            System.out.println("6. Export Data to CSV");
            System.out.println("0. Exit");
            String choice = input.readLine("Choose: ");
            try {
                switch (choice) {
                    case "1" -> movieMenu();
                    case "2" -> categoryMenu();
                    case "3" -> watchlistMenu();
                    case "4" -> favoriteMenu();
                    case "5" -> historyMenu();
                    case "6" -> exportMenu();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (ValidationException | NotFoundException e) {
                // These are expected, user-facing error conditions, not bugs —
                // print the message and keep the app running.
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                // Anything unexpected still gets caught so one bad menu
                // action never crashes the whole console session.
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    // ---------------------------------------------------------------- Movies

    private void movieMenu() throws ValidationException, NotFoundException {
        System.out.println("\n-- Movies --");
        System.out.println("1. Add movie");
        System.out.println("2. Edit movie");
        System.out.println("3. Delete movie (soft)");
        System.out.println("4. Restore deleted movie");
        System.out.println("5. View movie details");
        System.out.println("6. List all movies");
        System.out.println("7. Search movies");
        System.out.println("8. Sort movies");
        System.out.println("9. Browse by category");
        System.out.println("10. Advanced filter");
        System.out.println("11. Generate ranking");
        String choice = input.readLine("Choose: ");
        switch (choice) {
            case "1" -> addMovie();
            case "2" -> editMovie();
            case "3" -> softDeleteMovie();
            case "4" -> restoreMovie();
            case "5" -> viewMovieDetails();
            case "6" -> printMovies(movieController.listAll());
            case "7" -> searchMovies();
            case "8" -> sortMovies();
            case "9" -> browseByCategory();
            case "10" -> advancedFilter();
            case "11" -> printMovies(movieController.generateRanking());
            default -> System.out.println("Invalid choice.");
        }
    }

    private void softDeleteMovie() throws NotFoundException {
        String id = input.readLine("Movie id to soft-delete: ");
        movieController.softDeleteMovie(id);
        System.out.println("Soft deleted.");
    }

    private void restoreMovie() throws NotFoundException {
        System.out.println("\n-- Deleted Movies --");
        List<Movie> deleted = movieController.listDeleted();
        if (deleted.isEmpty()) {
            System.out.println("No deleted movies.");
            return;
        }
        printMovies(deleted);
        String id = input.readLine("Movie id to restore: ");
        movieController.restoreMovie(id);
        System.out.println("Restored.");
    }

    // ---------------------------------------------------------------- Export

    private void exportMenu() {
        System.out.println("\n-- Export Data to CSV --");
        System.out.println("1. Export all movies to CSV");
        System.out.println("2. Export watchlist to CSV");
        System.out.println("3. Export watch history to CSV");
        String choice = input.readLine("Choose: ");
        switch (choice) {
            case "1" -> {
                String path = input.readLine("Output filepath (default: data/movies_export.csv): ");
                if (path.isBlank()) path = "data/movies_export.csv";
                exportController.exportMovies(movieController.listAll(), path);
                System.out.println("Exported movies to " + path);
            }
            case "2" -> {
                String path = input.readLine("Output filepath (default: data/watchlist_export.csv): ");
                if (path.isBlank()) path = "data/watchlist_export.csv";
                exportController.exportWatchlist(watchlistController.list(CURRENT_USER), path);
                System.out.println("Exported watchlist to " + path);
            }
            case "3" -> {
                String path = input.readLine("Output filepath (default: data/history_export.csv): ");
                if (path.isBlank()) path = "data/history_export.csv";
                exportController.exportHistory(historyController.list(CURRENT_USER), path);
                System.out.println("Exported watch history to " + path);
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void addMovie() throws ValidationException {
        Movie m = readMovieFields();
        Movie created = movieController.addMovie(m);
        System.out.println("Created: " + created);
    }

    private void editMovie() throws ValidationException, NotFoundException {
        String id = input.readLine("Movie id to edit: ");
        Movie updated = readMovieFields();
        movieController.editMovie(id, updated);
        System.out.println("Updated.");
    }

    private void deleteMovie() throws NotFoundException {
        String id = input.readLine("Movie id to delete: ");
        movieController.deleteMovie(id);
        System.out.println("Deleted.");
    }

    private void viewMovieDetails() throws NotFoundException {
        String id = input.readLine("Movie id: ");
        Movie m = movieController.viewMovieDetails(id);
        System.out.println(describeMovie(m));
    }

    private void searchMovies() {
        String field = input.readLine("Search by (title/actor/director/genre): ");
        String query = input.readLine("Query: ");
        printMovies(movieController.search(field, query));
    }

    private void sortMovies() {
        String field = input.readLine("Sort by (title/rating/year/popularity): ");
        boolean asc = input.readLine("Ascending? (y/n): ").equalsIgnoreCase("y");
        MovieService.SortField sortField = switch (field.toLowerCase()) {
            case "rating" -> MovieService.SortField.RATING;
            case "year" -> MovieService.SortField.RELEASE_YEAR;
            case "popularity" -> MovieService.SortField.POPULARITY;
            default -> MovieService.SortField.TITLE;
        };
        printMovies(movieController.sort(movieController.listAll(), sortField, asc));
    }

    private void browseByCategory() {
        String categoryId = input.readLine("Category id: ");
        printMovies(movieController.browseByCategory(categoryId));
    }

    private void advancedFilter() {
        String titleSub = input.readLine("Title contains (blank = any): ");
        String categoryId = input.readLine("Category id (blank = any): ");
        String minRatingStr = input.readLine("Min rating (blank = any): ");
        String maxRatingStr = input.readLine("Max rating (blank = any): ");
        String yearFromStr = input.readLine("From year (blank = any): ");
        String yearToStr = input.readLine("To year (blank = any): ");
        String maxDurStr = input.readLine("Max duration minutes (blank = any): ");
        String actor = input.readLine("Actor contains (blank = any): ");

        MovieFilter filter = new MovieFilter()
                .withTitleContains(titleSub.isBlank() ? null : titleSub)
                .withCategory(categoryId.isBlank() ? null : categoryId)
                .withMinRating(minRatingStr.isBlank() ? null : Double.parseDouble(minRatingStr))
                .withMaxRating(maxRatingStr.isBlank() ? null : Double.parseDouble(maxRatingStr))
                .withYearRange(
                        yearFromStr.isBlank() ? null : Integer.parseInt(yearFromStr),
                        yearToStr.isBlank() ? null : Integer.parseInt(yearToStr))
                .withMaxDuration(maxDurStr.isBlank() ? null : Integer.parseInt(maxDurStr))
                .withActor(actor.isBlank() ? null : actor);

        printMovies(movieController.filter(filter));
    }

    private Movie readMovieFields() {
        String title = input.readLine("Title: ");
        String director = input.readLine("Director: ");
        String actorsRaw = input.readLine("Actors (comma-separated): ");
        String categoryId = input.readLine("Category id: ");
        int year = input.readInt("Release year: ");
        double rating = input.readDouble("Rating (0-10): ");
        int duration = input.readInt("Duration (minutes): ");
        String description = input.readLine("Description: ");

        return new Movie(null, title, director,
                Arrays.stream(actorsRaw.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList(),
                categoryId, year, rating, duration, description);
    }

    private void printMovies(List<Movie> movies) {
        if (movies.isEmpty()) {
            System.out.println("(no movies)");
            return;
        }
        movies.forEach(m -> System.out.println(describeMovie(m)));
    }

    private String describeMovie(Movie m) {
        return String.format("[%s] %-25s | %-15s | %d | %.1f* | views=%d favs=%d | cat=%s",
                m.getId(), m.getTitle(), m.getDirector(), m.getReleaseYear(),
                m.getRating(), m.getViewCount(), m.getFavoriteCount(), m.getCategoryId());
    }

    // ------------------------------------------------------------- Categories

    private void categoryMenu() throws ValidationException, NotFoundException {
        System.out.println("\n-- Categories --");
        System.out.println("1. Add category");
        System.out.println("2. Edit category");
        System.out.println("3. Delete category (soft)");
        System.out.println("4. Restore deleted category");
        System.out.println("5. List categories");
        String choice = input.readLine("Choose: ");
        switch (choice) {
            case "1" -> {
                String name = input.readLine("Name: ");
                String desc = input.readLine("Description: ");
                Category c = categoryController.addCategory(name, desc);
                System.out.println("Created: " + c);
            }
            case "2" -> {
                String id = input.readLine("Category id: ");
                String name = input.readLine("New name: ");
                String desc = input.readLine("New description: ");
                categoryController.editCategory(id, name, desc);
                System.out.println("Updated.");
            }
            case "3" -> {
                String id = input.readLine("Category id to soft-delete: ");
                categoryController.softDeleteCategory(id);
                System.out.println("Soft-deleted.");
            }
            case "4" -> {
                List<Category> deleted = categoryController.listDeleted();
                if (deleted.isEmpty()) {
                    System.out.println("No deleted categories.");
                } else {
                    System.out.println("\n-- Deleted Categories --");
                    deleted.forEach(c -> System.out.println("  " + c));
                    String id = input.readLine("Category id to restore: ");
                    categoryController.restoreCategory(id);
                    System.out.println("Restored.");
                }
            }
            case "5" -> categoryController.listAll().forEach(c -> System.out.println("  " + c));
            default -> System.out.println("Invalid choice.");
        }
    }

    // -------------------------------------------------------------- Watchlist

    private void watchlistMenu() throws NotFoundException {
        System.out.println("\n-- Watchlist --");
        System.out.println("1. Add movie");
        System.out.println("2. Remove movie");
        System.out.println("3. List watchlist");
        System.out.println("4. Undo last change");
        System.out.println("5. Redo last change");
        String choice = input.readLine("Choose: ");
        switch (choice) {
            case "1" -> {
                String movieId = input.readLine("Movie id: ");
                watchlistController.add(CURRENT_USER, movieId);
                System.out.println("Added.");
            }
            case "2" -> {
                String movieId = input.readLine("Movie id: ");
                watchlistController.remove(CURRENT_USER, movieId);
                System.out.println("Removed.");
            }
            case "3" -> printLinks(watchlistController.list(CURRENT_USER));
            case "4" -> {
                String desc = watchlistController.undo();
                System.out.println(desc != null ? "Undone: " + desc : "Nothing to undo.");
            }
            case "5" -> {
                String desc = watchlistController.redo();
                System.out.println(desc != null ? "Redone: " + desc : "Nothing to redo.");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    // -------------------------------------------------------------- Favorites

    private void favoriteMenu() throws NotFoundException {
        System.out.println("\n-- Favorites --");
        System.out.println("1. Add movie");
        System.out.println("2. Remove movie");
        System.out.println("3. List favorites");
        String choice = input.readLine("Choose: ");
        switch (choice) {
            case "1" -> {
                String movieId = input.readLine("Movie id: ");
                favoriteController.add(CURRENT_USER, movieId);
                System.out.println("Added.");
            }
            case "2" -> {
                String movieId = input.readLine("Movie id: ");
                favoriteController.remove(CURRENT_USER, movieId);
                System.out.println("Removed.");
            }
            case "3" -> printLinks(favoriteController.list(CURRENT_USER));
            default -> System.out.println("Invalid choice.");
        }
    }

    private void printLinks(List<UserMovieLink> links) {
        if (links.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        links.forEach(l -> System.out.println(l.getMovieId() + " (added " + l.getAddedAt() + ")"));
    }

    // --------------------------------------------------------------- History

    private void historyMenu() throws NotFoundException {
        System.out.println("\n-- Watching History & Reports --");
        System.out.println("1. Record a watch");
        System.out.println("2. View full history");
        System.out.println("3. Recently watched");
        System.out.println("4. Continue watching");
        System.out.println("5. Trending categories");
        System.out.println("6. Generate viewing report");
        String choice = input.readLine("Choose: ");
        switch (choice) {
            case "1" -> {
                String movieId = input.readLine("Movie id: ");
                int position = input.readInt("Position reached (seconds): ");
                historyController.recordWatch(CURRENT_USER, movieId, position);
                System.out.println("Recorded.");
            }
            case "2" -> printHistory(historyController.getHistory(CURRENT_USER));
            case "3" -> printHistory(historyController.getRecentlyWatched(CURRENT_USER, 10));
            case "4" -> printHistory(historyController.getContinueWatching(CURRENT_USER));
            case "5" -> {
                List<Map.Entry<String, Long>> trending = historyController.getTrendingCategories();
                if (trending.isEmpty()) {
                    System.out.println("(no data yet)");
                } else {
                    trending.forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue() + " watches"));
                }
            }
            case "6" -> System.out.println(historyController.generateReport(CURRENT_USER));
            default -> System.out.println("Invalid choice.");
        }
    }

    private void printHistory(List<WatchHistoryEntry> entries) {
        if (entries.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        entries.forEach(e -> System.out.println(String.format("%s | movie=%s | %d/%d sec",
                e.getWatchedAt(), e.getMovieId(), e.getPositionSeconds(), e.getTotalSeconds())));
    }
}
