package com.vanvat.moviestream.structures;

import java.util.EmptyStackException;

/**
 * Hand-rolled generic Stack backed by a singly-linked node chain.
 *
 * <p>Chosen data structure for Undo/Redo in {@code CommandManager}: a Stack
 * gives O(1) push/pop and naturally models LIFO command history without
 * any wasted capacity (unlike an array-backed approach).
 *
 * <p>Deliberately does NOT extend or wrap any {@code java.util} collection.
 */
public class CustomStack<T> {

    // ---------------------------------------------------------------- internals

    private static class Node<T> {
        final T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> top;   // null when empty
    private int size;

    // ---------------------------------------------------------------- public API

    /**
     * Pushes {@code item} onto the top of this stack.
     *
     * @param item element to push (may be null)
     */
    public void push(T item) {
        Node<T> node = new Node<>(item);
        node.next = top;
        top = node;
        size++;
    }

    /**
     * Removes and returns the item at the top of this stack.
     *
     * @throws EmptyStackException if the stack is empty
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    /**
     * Returns, without removing, the item at the top of this stack.
     *
     * @throws EmptyStackException if the stack is empty
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.data;
    }

    /** Returns {@code true} if this stack contains no elements. */
    public boolean isEmpty() {
        return top == null;
    }

    /** Returns the number of elements in this stack. */
    public int size() {
        return size;
    }

    /** Removes all elements from this stack. */
    public void clear() {
        top = null;
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CustomStack[top→");
        Node<T> cur = top;
        while (cur != null) {
            sb.append(cur.data);
            if (cur.next != null) sb.append(", ");
            cur = cur.next;
        }
        return sb.append("]").toString();
    }
}
