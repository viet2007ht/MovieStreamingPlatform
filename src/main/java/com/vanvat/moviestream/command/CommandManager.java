package com.vanvat.moviestream.command;

import com.vanvat.moviestream.structures.CustomStack;

/**
 * Classic two-stack Undo/Redo manager. Running a new command clears the
 * redo stack (standard editor semantics — you can't redo a branch that's
 * been abandoned).
 */
public class CommandManager {

    private final CustomStack<Command> undoStack = new CustomStack<>();
    private final CustomStack<Command> redoStack = new CustomStack<>();

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
