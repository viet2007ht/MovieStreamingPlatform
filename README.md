# 🎬 Movie Streaming Management System

> A Netflix-style movie streaming management system — built in **Pure Java** (JDK standard library only), following **MVC** architecture, with **file-based persistence** and hand-rolled data structures/algorithms.

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge)](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)
[![Storage](https://img.shields.io/badge/Storage-File%20I%2FO-green?style=for-the-badge)](https://docs.oracle.com/javase/tutorial/essential/io/)
[![Build](https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack & Constraints](#-tech-stack--constraints)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Build & Run](#-build--run)
- [File Storage Format](#-file-storage-format)
- [Requirement → Implementation Map](#-requirement--implementation-map)
- [Algorithms & Data Structures](#-algorithms--data-structures)
- [Notable Techniques](#-notable-techniques)
- [Known Simplifications](#-known-simplifications)
- [Suggested Next Steps](#-suggested-next-steps)

---

## 📝 Overview

**Movie Streaming Management System** is a console (CLI) application that simulates the core of a Netflix-like platform. It's built entirely in **plain Java** — no frameworks, no third-party libraries, no database — to demonstrate:

- Clean **MVC** layering (Model / View / Controller, plus Service and Repository layers underneath)
- **File-only persistence** (pipe-delimited text files, no DB of any kind)
- **Manually implemented algorithms** (linear search, merge sort — not `Collections.sort()` or streams for the core logic)
- **Hand-rolled data structures** (a generic stack and a generic doubly-linked list, built from linked nodes — not wrapping `java.util`)
- OOP fundamentals, input validation, and custom exception handling throughout

---

## ✨ Features

### Catalog & Browsing
- Movie CRUD (create, update, soft-delete/restore, view details)
- Category CRUD
- Search movies by **title**, **actor**, **director**, or **genre**
- Sort movies by **title**, **rating**, **release year**, or **popularity**
- Browse movies by category
- Advanced multi-condition filtering (category, rating range, year range, actor, director, duration — all AND-composable)

### Personal Library
- Watchlist management, with **Undo/Redo** (Command pattern + custom Stack)
- Favorite movies management
- Watch history tracking, with position/progress per movie
- "Continue watching" (in-progress movies, most recent first)
- Recently watched (deduplicated per movie, built on a custom linked list)

### Insights
- Automatic movie ranking (normalized weighted score from rating, views, and favorites)
- Viewing statistics (watch counts, total watch time)
- Trending categories (ranked by watch-event volume)
- Viewing reports per user
- CSV export (movies, watchlist, history) — RFC 4180 compliant, hand-rolled escaping

---

## 🛠 Tech Stack & Constraints

| Component | Choice |
|:---|:---|
| **Language** | Java 17+ (standard library only) |
| **Architecture** | Model–View–Controller, with Service and Repository layers |
| **Persistence** | Pipe-delimited (`\|`) text files under `data/` |
| **Build** | Maven (`pom.xml`) — compiler + jar plugins only |
| **Encoding** | UTF-8 |

### ❌ Deliberately not used
- Frameworks (Spring, Hibernate, JPA, Lombok, …)
- Any third-party library
- A database of any kind
- `Collections.sort()` / Stream `.sorted()` for the core sort requirement
- `java.util.Stack` / `java.util.LinkedList` for the required custom structures

---

## 🏗 Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                         VIEW                                 │
│         ConsoleView + InputHelper (CLI menus, no rules)       │
├──────────────────────────────────────────────────────────────┤
│                       CONTROLLER                              │
│  MovieController · CategoryController · WatchlistController  │
│      FavoriteController · HistoryController · ExportController│
│         (thin glue — no file access, no business rules)      │
├──────────────────────────────────────────────────────────────┤
│                        SERVICE                                │
│  MovieService · CategoryService · WatchlistService            │
│  FavoriteService · WatchHistoryService · RankingService       │
│  MovieFilter · ReportService · ExportService                  │
│  ┌──────────────────────────────────────────────┐             │
│  │ algorithms/  MovieSearcher · MovieSorter      │             │
│  │ command/     CommandManager · Command impls   │             │
│  │ structures/  CustomStack · CustomLinkedList    │             │
│  └──────────────────────────────────────────────┘             │
├──────────────────────────────────────────────────────────────┤
│                       REPOSITORY                               │
│      AbstractFileRepository<T,ID> + concrete repositories     │
├──────────────────────────────────────────────────────────────┤
│                       PERSISTENCE                              │
│  data/movies.txt · categories.txt · watchlist.txt              │
│  data/favorites.txt · history.txt                              │
└──────────────────────────────────────────────────────────────┘
```

Composition (dependency wiring) happens once, in `Main.java`:
`repository → service → controller → view`.

---

## 📁 Project Structure

```
MovieStreamingPlatform/
├── src/main/java/com/vanvat/moviestream/
│   ├── Main.java            # Composition root — wires everything together
│   ├── model/               # Movie, Category, UserMovieLink, WatchHistoryEntry
│   │                        # (plain data + toFileLine/fromFileLine serialization)
│   ├── view/                # ConsoleView (menus) + InputHelper (validated input loop)
│   ├── controller/          # Thin glue between view and service — no file access
│   ├── service/             # Business logic: CRUD rules, search/sort orchestration,
│   │                        # ranking, filtering, history/stats, undo-redo wiring
│   ├── algorithms/          # MovieSearcher (linear search), MovieSorter (merge sort)
│   ├── command/             # Command pattern backing Watchlist Undo/Redo
│   ├── structures/          # CustomStack, CustomLinkedList — hand-rolled, linked-node
│   ├── repository/          # File I/O only — AbstractFileRepository<T,ID> does load/save
│   ├── util/                # Validator, IdGenerator, FileUtils, CsvExporter, Paginator
│   └── exception/           # ValidationException, NotFoundException, DataAccessException
├── data/                    # movies.txt, categories.txt, watchlist.txt, favorites.txt, history.txt
├── pom.xml
└── README.md
```

---

## 🚀 Build & Run

```bash
mvn clean package
java -jar target/movie-streaming-system.jar
```

Or, without Maven (no external dependencies to resolve):

```bash
javac -d out $(find src -name "*.java") && java -cp out com.vanvat.moviestream.Main
```

Data files are created automatically under `data/` on first run. Create at least one **Category** before adding a **Movie** — movies reference a category id.

---

## 💾 File Storage Format

Each entity file is one record per line, pipe-delimited (`|`). Any `|`, backslash, or newline inside a field is escaped (`\p`, `\\`, `\n`) so the format stays parseable — see `escape`/`unescape` in `Movie`/`Category`.

```
movies.txt:      id|title|director|actor1,actor2|categoryId|year|rating|durationMin|viewCount|favCount|description
categories.txt:  id|name|description
watchlist.txt:   userId|movieId|addedAt(ISO datetime)
favorites.txt:   userId|movieId|addedAt(ISO datetime)
history.txt:     id|userId|movieId|watchedAt(ISO datetime)|positionSeconds|totalSeconds
```

---

## 🔗 Requirement → Implementation Map

| Requirement | Where |
|---|---|
| Movie/Category CRUD | `MovieService` / `CategoryService` + repositories |
| Search by title/actor/director/genre | `MovieService.search()` → `algorithms/MovieSearcher` (manual linear search) |
| Sort by title/rating/year/popularity | `MovieService.sort()` → `algorithms/MovieSorter` (manual merge sort) |
| Watchlist / Favorites | `WatchlistService`, `FavoriteService` |
| Watching history / continue watching / recently watched | `WatchHistoryService` (recently-watched built on `structures/CustomLinkedList`) |
| Browse by category | `MovieService.browseByCategory()` |
| Viewing statistics / trending categories | `WatchHistoryService` |
| Undo/Redo watchlist | `command/` package (`CommandManager` + two `structures/CustomStack`s) + `WatchlistService` |
| Movie ranking (rating + views + favorites) | `RankingService` (min-max normalized weighted score, sorted via `MovieSorter`) |
| Advanced multi-condition filtering | `MovieFilter` (composable predicates) |
| Viewing reports | `ReportService` |
| CSV export | `ExportController` + `ExportService` + `util/CsvExporter` |

---

## 📊 Algorithms & Data Structures

### Algorithms (manually implemented)

| Algorithm | File | Complexity | Notes |
|:---|:---|:---|:---|
| **Linear search** | `algorithms/MovieSearcher.java` | O(n) | Case-insensitive substring match on title, actor, director, or genre |
| **Merge sort** | `algorithms/MovieSorter.java` | O(n log n) | Stable, comparator-driven — reused for both field sorting and weighted ranking |

### Data structures (hand-rolled, ≥2 as required)

| Structure | File | Used for |
|:---|:---|:---|
| **Custom Stack** | `structures/CustomStack.java` | Singly-linked-node LIFO. Backs the two-stack Undo/Redo (`CommandManager`) |
| **Custom Linked List** | `structures/CustomLinkedList.java` | Doubly-linked-node list. Backs the deduplicated "recently watched" window |

Neither extends nor wraps a `java.util` collection.

---

## 💡 Notable Techniques

### Command pattern for Undo/Redo
Every mutating watchlist operation (`AddToWatchlistCommand`, `RemoveFromWatchlistCommand`) is wrapped in a `Command`. `CommandManager` keeps two `CustomStack`s — undo and redo — and running a new command clears the redo stack, matching standard editor semantics.

### Soft delete & restore
Movies support soft-delete/restore rather than hard deletion, so category/watchlist/favorite references stay valid and data can be recovered.

### Weighted ranking with normalization
`RankingService` min-max normalizes rating, view count, and favorite count (which live on very different scales) before applying weights — so raw view counts in the thousands don't drown out a 0–10 rating.

### RFC 4180 CSV export
`util/CsvExporter` implements comma/quote/newline escaping by hand (no library), quoting fields that need it and doubling embedded quotes.


Easter egg: Tung Tung Tung Sahur!
