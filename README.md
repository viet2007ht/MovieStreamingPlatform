# Movie Streaming Management System

Console-based, file-storage-only Netflix-style movie management system.
Java 17+, Maven, no external frameworks, MVC architecture.

## Build & Run

```bash
mvn clean package
java -jar target/movie-streaming-system.jar
```

(Or without Maven, since there are no dependencies:
`javac -d out $(find src -name "*.java") && java -cp out com.vanvat.moviestream.Main`)

Data files are created automatically under `data/` on first run
(`movies.txt`, `categories.txt`, `watchlist.txt`, `favorites.txt`, `history.txt`).
Create at least one Category before adding a Movie (movies reference a
category id).

## Architecture

```
model/       plain data classes + file (de)serialization (toFileLine/fromFileLine)
repository/  file I/O only — AbstractFileRepository<T,ID> does load/save,
             concrete repos just plug in a serializer + id extractor
service/     business logic: CRUD rules, search, sort, ranking, filtering,
             history/statistics, undo-redo wiring
command/     Command pattern (Add/RemoveFromWatchlistCommand + CommandManager)
             backing the watchlist Undo/Redo requirement
controller/  thin glue between view and service — no file access, no rules
view/        console menus (ConsoleView) + input validation loop (InputHelper)
util/        FileUtils (the only class touching java.nio), Validator, IdGenerator
exception/   ValidationException, NotFoundException, DataAccessException
Main.java    composition root — wires repository -> service -> controller -> view
```

## File format

Each entity file is one record per line, pipe-delimited (`|`). Any `|`,
backslash or newline inside a field is escaped (`\p`, `\\`, `\n`) so the
format stays parseable — see `escape`/`unescape` in `Movie`/`Category`.

```
movies.txt:     id|title|director|actor1,actor2|categoryId|year|rating|durationMin|viewCount|favCount|description
categories.txt: id|name|description
watchlist.txt / favorites.txt: userId|movieId|addedAt(ISO datetime)
history.txt:    id|userId|movieId|watchedAt(ISO datetime)|positionSeconds|totalSeconds
```

## Requirement -> implementation map

| Requirement | Where |
|---|---|
| Movie/Category CRUD | `MovieService`/`CategoryService` + repos |
| Search by title/actor/director/genre | `MovieService.search()` |
| Sort by title/rating/year/popularity | `MovieService.sort()` |
| Watchlist / Favorites | `WatchlistService`, `FavoriteService` |
| Watching history / continue watching / recently watched | `WatchHistoryService` |
| Browse by category | `MovieService.browseByCategory()` |
| Viewing statistics / trending categories | `WatchHistoryService` |
| Undo/Redo watchlist | `command/` package + `WatchlistService` |
| Movie ranking (rating+views+favorites) | `RankingService` (normalized weighted score) |
| Advanced multi-condition filtering | `MovieFilter` (composable predicates) |
| Viewing reports | `ReportService` |

## Known simplifications (flag these in your report, don't hide them)

- **Single hardcoded user** (`CURRENT_USER = "U-1"` in `ConsoleView`). Every
  service already takes a `userId` parameter, so adding a login/User CRUD
  screen is additive, not a redesign — see "Next steps" below.
- **Load-all-then-rewrite** persistence (`AbstractFileRepository`) — fine
  for course-project data volumes; a production system would append/index
  instead of rewriting the whole file on every write.
- No automated tests yet.

## Suggested next steps

1. Add a `User` model + `UserRepository` + a login prompt in `ConsoleView`
   (replaces the hardcoded `CURRENT_USER`).
2. Unit tests for `RankingService`, `MovieFilter`, and `CommandManager`
   (these are pure logic, no file I/O, easiest to test in isolation).
3. Optionally split `ConsoleView`'s menus into smaller per-feature view
   classes if it keeps growing.
