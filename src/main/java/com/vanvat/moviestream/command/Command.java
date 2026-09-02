package com.vanvat.moviestream.command;

/**
 * Command pattern used for the Undo/Redo requirement. Every mutating
 * watchlist operation is wrapped in a Command so it can be replayed
 * (redo) or reversed (undo) without the service caring what the
 * concrete operation was.
 */
public interface Command {
    void execute();
    void undo();

    /** Human-readable description for a "history of actions" display. */
    String describe();
}
