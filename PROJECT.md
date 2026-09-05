# 🎬 Movie Streaming Management System

A Netflix-style **Movie Streaming Management System** built as a desktop/console application. The project focuses on solid software engineering fundamentals rather than UI polish: clean **MVC architecture**, **hand-rolled data structures**, **file-based persistence** (no database), and **manually implemented algorithms** for searching, sorting, and ranking.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Core Constraints](#core-constraints)
- [Feature Requirements](#feature-requirements)
  - [Basic (B)](#basic-b)
  - [Medium (M)](#medium-m)
  - [Hard (H)](#hard-h)
- [Architecture](#architecture)
- [Suggested Project Structure](#suggested-project-structure)
- [Data Structures & Algorithms](#data-structures--algorithms)
- [Scoring Criteria](#scoring-criteria)
- [Mandatory Requirements](#mandatory-requirements--automatic-fail-conditions)
- [Passing Requirement](#passing-requirement)
- [Getting Started](#getting-started)

---

## Overview

This project simulates the core functionality of a movie streaming platform. Users can browse, search, sort, and organize movies; manage watchlists and favorites; track watch history; and generate insights like trending categories and viewing reports — all without relying on a database or an external framework.

## Core Constraints

These apply across the entire project, regardless of feature tier:

| Constraint | Details |
|---|---|
| **Architecture** | Must follow **MVC** (Model–View–Controller) with clearly separated responsibilities |
| **Persistence** | Data must be stored and loaded from **files only** — no database of any kind |
| **Frameworks** | **No external frameworks** are allowed |
| **Algorithms** | Searching, sorting, and other core logic must be **implemented manually** (not just built-in library calls) |
| **OOP** | Must apply object-oriented principles throughout (encapsulation, inheritance/composition where appropriate, etc.) |
| **Validation** | All user input must be validated |
| **Error Handling** | Exceptions must be handled gracefully |
| **Code Quality** | Code must be clean, readable, and maintainable |

---

## Feature Requirements

### Basic (B)

Core CRUD and browsing functionality. All Basic features must be fully implemented and working for full marks in this category.

- [ ] Movie CRUD (Create, Read, Update, Delete)
- [ ] Category CRUD
- [ ] Search movies by **title**, **actor**, **director**, or **genre**
- [ ] Sort movies by **title**, **rating**, **release year**, or **popularity**
- [ ] Watchlist management
- [ ] Favorite movie management
- [ ] View movie details

### Medium (M)

Adds personalization and analytics on top of the basic catalog features. Scored proportionally — partial credit is given based on the percentage of correctly implemented features.

- [ ] Watching history
- [ ] "Continue watching" feature
- [ ] Browse movies by category
- [ ] Viewing statistics
- [ ] Recently watched movies
- [ ] Trending categories

### Hard (H)

Advanced logic requiring custom data structures and algorithms. Also scored proportionally.

- [ ] Undo/Redo for watchlist modifications
- [ ] Automatic movie ranking generation (based on rating, views, and favorites)
- [ ] Advanced movie filtering with multiple simultaneous conditions
- [ ] Viewing report generation based on user history

---

## Architecture

The project must strictly follow **MVC**:

- **Model** — Data entities (Movie, Category, User, Watchlist, History, etc.) and business logic. No direct file/database access from here beyond well-defined repository/service boundaries.
- **View** — Presentation layer only. Must **not** contain business logic.
- **Controller** — Coordinates between Model and View. Must **not** handle data access directly (that belongs in a data-access/repository layer called by the Controller or Model).

## Suggested Project Structure

A clear, logical folder structure is explicitly graded. A reasonable layout:

```
MovieStreamingSystem/
├── src/
│   ├── models/          # Movie, Category, User, Watchlist, HistoryEntry, etc.
│   ├── views/           # Console/UI rendering, menus, output formatting
│   ├── controllers/     # MovieController, WatchlistController, HistoryController...
│   ├── data/            # File-based persistence layer (readers/writers)
│   ├── structures/      # Custom LinkedList, Stack, Queue, Tree implementations
│   ├── algorithms/      # Custom search & sort implementations
│   └── utils/           # Validation, exception classes, helpers
├── data/                # Persisted .txt/.json/.csv files (movies, users, history, etc.)
├── docs/                # Additional documentation
└── README.md
```

## Data Structures & Algorithms

- At least **two** manually implemented data structures are required, chosen from:
  - Linked List
  - Stack
  - Queue
  - Tree

  These must be used **meaningfully** to solve real problems (e.g., a Stack/Queue pair for Undo/Redo on the watchlist, a Tree for category hierarchies or ranking, a Linked List for history traversal) — not just included for show.

- **Searching & sorting algorithms** must be implemented manually rather than relying solely on built-in library methods, with the algorithm choice suited to the scenario (e.g., a search suited to lookups by field, a sort suited to ranking by multiple criteria).

Example mapping of features to structures/algorithms:

| Feature | Possible Structure/Algorithm |
|---|---|
| Undo/Redo watchlist | Two Stacks |
| Continue watching / recently watched | Queue or Linked List |
| Category hierarchy / trending categories | Tree |
| Movie ranking (rating, views, favorites) | Custom sort (e.g., merge/quick sort on weighted score) |
| Search by title/actor/director/genre | Custom search (e.g., linear/binary search on indexed fields) |
| Advanced multi-condition filtering | Combination of custom search + filter predicates |

---

## Scoring Criteria

| Criteria | Weight | Description |
|---|---|---|
| **MVC Architecture** \* | 10% | Must follow Model–View–Controller architecture with clearly separated responsibilities. Business logic must not live in the View; data access must not be handled directly in the Controller. |
| **Using GitHub** \* | 0% | The project repository must be managed, tracked, and submitted via GitHub. |
| **Data Structure Implementation** \* (min. 2 structures) | 15% | Must manually implement and meaningfully apply at least two required data structures (Linked List, Stack, Queue, or Tree) to solve real problems, not just for demonstration. |
| **Functional Requirements (Basic)** \* | 5% | All Basic features must be fully implemented. Full marks only if every Basic feature works correctly. |
| **Functional Requirements (Medium)** | 18% | Proportional to the percentage of correctly implemented Medium features. |
| **Functional Requirements (Hard)** | 12% | Proportional to the percentage of correctly implemented Hard features. |
| **Algorithms (Searching & Sorting)** | 10% | Manually implemented search/sort algorithms, chosen appropriately per scenario rather than relying solely on built-in methods. |
| **Code Quality** | 10% | Clean coding practices: meaningful naming, no magic numbers or duplicated code, proper input validation, exception handling, encapsulation, maintainable class design. |
| **File Structure & Project Organization** | 15% | Clear folder structure (e.g., Models, Views, Controllers, Data, Utilities) with logically grouped source files. |
| **Creativity** | 10% | Extra useful features, UI improvements, UX enhancements, or thoughtful system improvements beyond the minimum requirements. |

\* = Mandatory requirement (see below).

## Mandatory Requirements — Automatic Fail Conditions

> ⚠️ **Regardless of total score, failing to meet any of the (\*) mandatory requirements results in an AUTOMATIC FAIL.**

The mandatory (\*) items are:
1. MVC Architecture
2. Using GitHub
3. Data Structure Implementation (minimum 2 structures)
4. Functional Requirements (Basic)

## Passing Requirement

To pass the project:
- **Minimum total score of 70 points**, **and**
- **No score of 0** in any criterion (except Creativity, which is optional/bonus).

---

## Getting Started

1. Clone the repository.
2. Set up your language/runtime of choice (no external frameworks allowed).
3. Design your Models first (Movie, Category, User, Watchlist, History).
4. Build the file-based data layer (choose a simple, parseable format — e.g., CSV, JSON, or a custom delimited text format).
5. Implement Basic features end-to-end before moving to Medium/Hard.
6. Layer in the required custom data structures and algorithms where they solve a real problem.
7. Write clean, validated, exception-safe code throughout.
8. Commit and push regularly to GitHub — this is a mandatory, zero-weight-but-required criterion.

---

*This README was generated from the original project specification document.*
