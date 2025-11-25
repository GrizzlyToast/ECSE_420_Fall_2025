package ca.mcgill.ecse420.a3;

public class LazyList<T> {
    private Node<T> head = new Node<>();

    public void add(T item) {
        int key = item.hashCode();
        Node<T> newNode = new Node<>();
        newNode.item = item;
        newNode.key = key;

        Node<T> pred = head;
        Node<T> curr = head.next;

        while (curr != null && curr.key < key) {
            pred = curr;
            curr = curr.next;
        }

        newNode.next = curr;
        pred.next = newNode;
    }

    // private boolean validate(Node<T> pred, Node<T> curr) {
    //     return !pred.marked && !curr.marked && pred.next == curr;
    // }

    // public boolean remove(T item) {
    //     // Search for the node to remove
    //     int key = item.hashCode();
    //     while(true) {
    //         Node<T> pred = head;
    //         Node<T> curr = head.next;
    //         while(curr.key <= key && curr != null) {
    //             if(item .equals(curr.item)) {
    //                 break;
    //             }
    //             pred = curr;
    //             curr = curr.next;
    //         }
    //         if(curr == null || curr.key != key) {
    //             return false; // Item not found
    //         }
            
    //         try {
    //             pred.lock();
    //             curr.lock();
    //             if(validate(pred, curr)) {
    //                 if (curr.key == key) {
    //                     curr.marked = true;
    //                     pred.next = curr.next;
    //                     return true;
    //                 } else {
    //                     return false; // Item not found
    //                 } }} finally {
    //             curr.unlock();
    //             pred.unlock();
    //         }
    //     }
    // }

    public void markNode(T item) {
        int key = item.hashCode();
        Node<T> curr = head;
        while (curr.key < key) {
            curr = curr.next;
        }
        if (curr.key == key) {
            curr.marked = true;
        }
    }

    public boolean contains(T item) {           
        int key = item.hashCode();
        Node<T> curr = head;
        while (curr.key < key) {
            curr = curr.next;
        }
        return (curr.key == key) && !curr.marked;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<T> curr = head.next;
        while (curr != null) {
            sb.append(curr.item).append(" -> ");
            curr = curr.next;
        }
        sb.append("null");
        return sb.toString();
    }
}
