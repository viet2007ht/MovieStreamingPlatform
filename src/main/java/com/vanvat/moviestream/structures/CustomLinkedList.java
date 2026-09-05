package com.vanvat.moviestream.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Hand-rolled generic doubly-linked list.
 *
 * <p>Used by {@code WatchHistoryService} to maintain an ordered, de-duplicated
 * window of recently-watched entries: new items are appended at the tail,
 * and the full sequence is read head-to-tail for display. A doubly-linked
 * structure is well-suited here because both ends may be touched (addFirst
 * for prepending, addLast for appending) and traversal is always forward.
 *
 * <p>Deliberately does NOT extend or wrap any {@code java.util} collection.
 *
 * @param <T> element type
 */
public class CustomLinkedList<T> {

    // ---------------------------------------------------------------- internals

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;  // first element; null when empty
    private Node<T> tail;  // last element;  null when empty
    private int size;

    // ---------------------------------------------------------------- public API

    /** Appends {@code item} to the end (tail) of this list. O(1). */
    public void addLast(T item) {
        Node<T> node = new Node<>(item);
        if (tail == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /** Prepends {@code item} to the front (head) of this list. O(1). */
    public void addFirst(T item) {
        Node<T> node = new Node<>(item);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    /**
     * Returns and removes the first element of this list.
     *
     * @throws NoSuchElementException if the list is empty
     */
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }
        size--;
        return data;
    }

    /**
     * Returns the first element without removing it.
     *
     * @throws NoSuchElementException if the list is empty
     */
    public T peekFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        return head.data;
    }

    /**
     * Returns all elements from head to tail as a new {@link List}.
     * O(n) — traverses every node exactly once.
     */
    public List<T> toList() {
        List<T> result = new ArrayList<>(size);
        Node<T> cur = head;
        while (cur != null) {
            result.add(cur.data);
            cur = cur.next;
        }
        return result;
    }

    /** Returns the number of elements in this list. */
    public int size() {
        return size;
    }

    /** Returns {@code true} if this list contains no elements. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Removes all elements from this list. */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CustomLinkedList[");
        Node<T> cur = head;
        while (cur != null) {
            sb.append(cur.data);
            if (cur.next != null) sb.append(" <-> ");
            cur = cur.next;
        }
        return sb.append("]").toString();
    }
}
