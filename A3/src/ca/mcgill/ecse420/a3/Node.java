package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.ReentrantLock;

public class Node<T> {
    public T item;
    public int key;
    public volatile Node<T> next;
    public final ReentrantLock lock = new ReentrantLock();

    // Sentinel constructor
    public Node(int key) {
        this.key = key;
    }

    // Regular node
    public Node(T item) {
        this.item = item;
        this.key = item.hashCode();
    }

    // Empty constructor for creating head artificially
    public Node() {}
}
