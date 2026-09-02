package com.vanvat.moviestream.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Classic two-stack Undo/Redo manager. Running a new command clears the
 * redo stack (standard editor semantics — you can't redo a branch that's
 * been abandoned).
 */
public class CommandManager {

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public void run(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public String undo() {
        if (!canUndo()) {
            return null;
        }
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return command.describe();
    }

    public String redo() {
        if (!canRedo()) {
            return null;
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        return command.describe();
    }
}
