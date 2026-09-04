package com.vanvat.moviestream;

import com.vanvat.moviestream.controller.CategoryController;
import com.vanvat.moviestream.controller.FavoriteController;
import com.vanvat.moviestream.controller.HistoryController;
import com.vanvat.moviestream.controller.MovieController;
import com.vanvat.moviestream.controller.WatchlistController;
import com.vanvat.moviestream.repository.CategoryRepository;
import com.vanvat.moviestream.repository.MovieRepository;
import com.vanvat.moviestream.repository.UserMovieLinkRepository;
import com.vanvat.moviestream.repository.WatchHistoryRepository;
import com.vanvat.moviestream.service.CategoryService;
import com.vanvat.moviestream.service.FavoriteService;
import com.vanvat.moviestream.service.MovieService;
import com.vanvat.moviestream.service.RankingService;
import com.vanvat.moviestream.service.ReportService;
import com.vanvat.moviestream.service.WatchHistoryService;
import com.vanvat.moviestream.service.WatchlistService;
import com.vanvat.moviestream.view.ConsoleView;

import java.util.Scanner;

/**
 * Composition root: this is the ONLY class that knows about every layer
 * at once. It builds repositories -> services -> controllers -> view,
 * bottom-up, and hands control to the view. Everything else only depends
 * on the layer directly below it.
 */
public class Main {

    private static final String DATA_DIR = "data/";

    public static void main(String[] args) {
        // Repositories (file-backed)
        MovieRepository movieRepository = new MovieRepository(DATA_DIR + "movies.txt");
        CategoryRepository categoryRepository = new CategoryRepository(DATA_DIR + "categories.txt");
        UserMovieLinkRepository watchlistRepository = new UserMovieLinkRepository(DATA_DIR + "watchlist.txt");
        UserMovieLinkRepository favoriteRepository = new UserMovieLinkRepository(DATA_DIR + "favorites.txt");
        WatchHistoryRepository historyRepository = new WatchHistoryRepository(DATA_DIR + "history.txt");

        // Services (business logic)
        MovieService movieService = new MovieService(movieRepository);
        CategoryService categoryService = new CategoryService(categoryRepository);
        WatchlistService watchlistService = new WatchlistService(watchlistRepository);
        FavoriteService favoriteService = new FavoriteService(favoriteRepository, movieService);
        WatchHistoryService watchHistoryService = new WatchHistoryService(historyRepository, movieService);
        RankingService rankingService = new RankingService();
        ReportService reportService = new ReportService(watchHistoryService, movieService);

        // Controllers
        MovieController movieController = new MovieController(movieService, rankingService);
        CategoryController categoryController = new CategoryController(categoryService);
        WatchlistController watchlistController = new WatchlistController(watchlistService);
        FavoriteController favoriteController = new FavoriteController(favoriteService);
        HistoryController historyController = new HistoryController(watchHistoryService, reportService);

        // View
        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleView view = new ConsoleView(movieController, categoryController,
                    watchlistController, favoriteController, historyController, scanner);
            view.start();
        }
    }
}
