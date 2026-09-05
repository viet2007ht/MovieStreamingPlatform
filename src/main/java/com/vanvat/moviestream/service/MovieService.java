package com.vanvat.moviestream.service;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.exception.ValidationException;
import com.vanvat.moviestream.model.Movie;
import com.vanvat.moviestream.repository.MovieRepository;
import com.vanvat.moviestream.algorithms.MovieSearcher;
import com.vanvat.moviestream.algorithms.MovieSorter;
import com.vanvat.moviestream.util.IdGenerator;
import com.vanvat.moviestream.util.Validator;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MovieService {

    public enum SortField { TITLE, RATING, RELEASE_YEAR, POPULARITY }

    private final MovieRepository repository;
    private final CategoryService categoryService;
    private final IdGenerator idGenerator;

    public MovieService(MovieRepository repository, CategoryService categoryService) {
        this.repository = repository;
        this.categoryService = categoryService;
        int maxId = repository.findAll().stream()
                .map(m -> m.getId().replace("MV-", ""))
                .mapToInt(s -> s.matches("\\d+") ? Integer.parseInt(s) : 0)
                .max().orElse(0);
        this.idGenerator = new IdGenerator("MV", maxId);
    }

    public Movie create(Movie movie) throws ValidationException {
        validate(movie);
        movie.setId(idGenerator.next());
        movie.setViewCount(0);
        movie.setFavoriteCount(0);
        return repository.save(movie);
    }

    public Movie update(String id, Movie updated) throws ValidationException, NotFoundException {
        validate(updated);
        Movie existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDirector(updated.getDirector());
        existing.setActors(updated.getActors());
        existing.setCategoryId(updated.getCategoryId());
        existing.setReleaseYear(updated.getReleaseYear());
        existing.setRating(updated.getRating());
        existing.setDurationMinutes(updated.getDurationMinutes());
        existing.setDescription(updated.getDescription());
        // viewCount/favoriteCount are NOT overwritten here — they're
        // maintained separately by WatchHistoryService/FavoriteService.
        return repository.save(existing);
    }

    public void delete(String id) throws NotFoundException {
        getById(id);
        repository.deleteById(id);
    }

    public void softDelete(String id) throws NotFoundException {
        Movie movie = getById(id);
        movie.setDeleted(true);
        repository.save(movie);
    }

    public void restore(String id) throws NotFoundException {
        Movie movie = repository.findAllIncludingDeleted().stream()
                .filter(m -> m.getId().equals(id) && m.isDeleted())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Deleted movie not found: " + id));
        movie.setDeleted(false);
        repository.save(movie);
    }

    public List<Movie> getDeleted() {
        return repository.findAllIncludingDeleted().stream()
                .filter(Movie::isDeleted)
                .toList();
    }

    public Movie getById(String id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie not found: " + id));
    }

    public List<Movie> getAll() {
        return repository.findAll();
    }

    /** Single-field search, case-insensitive substring match (manual linear scan). */
    public List<Movie> search(String field, String query) {
        return MovieSearcher.linearSearch(repository.findAll(), field, query);
    }

    public List<Movie> browseByCategory(String categoryId) {
        return repository.findAll().stream()
                .filter(m -> m.getCategoryId().equals(categoryId))
                .toList();
    }

    public List<Movie> filter(MovieFilter filter) {
        return filter.apply(getAll());
    }

    public List<Movie> sort(List<Movie> movies, SortField field, boolean ascending) {
        Comparator<Movie> comparator = switch (field) {
            case TITLE        -> Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER);
            case RATING       -> Comparator.comparingDouble(Movie::getRating);
            case RELEASE_YEAR -> Comparator.comparingInt(Movie::getReleaseYear);
            case POPULARITY   -> Comparator.comparingInt(Movie::getPopularity);
        };
        if (!ascending) {
            comparator = comparator.reversed();
        }
        // Manual merge sort — satisfies the "manually implemented algorithms" requirement.
        return MovieSorter.mergeSort(movies, comparator);
    }

    private void validate(Movie m) throws ValidationException {
        Validator.requireNonBlank(m.getTitle(), "Title");
        Validator.requireNonBlank(m.getDirector(), "Director");
        Validator.requireNonBlank(m.getCategoryId(), "Category");
        requireExistingCategory(m.getCategoryId());
        Validator.requireValidYear(m.getReleaseYear());
        Validator.requireRange(m.getRating(), 0.0, 10.0, "Rating");
        Validator.requirePositive(m.getDurationMinutes(), "Duration");
    }

    /**
     * Foreign-key check: a movie must reference a category that actually
     * exists, or browsing/deleting by category later silently loses movies
     * (or a category delete can leave dangling references).
     */
    private void requireExistingCategory(String categoryId) throws ValidationException {
        try {
            categoryService.getById(categoryId);
        } catch (NotFoundException e) {
            throw new ValidationException("Category does not exist: " + categoryId);
        }
    }

    MovieRepository getRepository() {
        return repository;
    }
}
