package ca.mcgill.ecse420.a3;

public class HohList<T> {
    private final Node<T> head;
    private final Node<T> tail;

    public HohList() {
        head = new Node<>(Integer.MIN_VALUE);
        tail = new Node<>(Integer.MAX_VALUE);
        head.next = tail;
    }

    public boolean add(T item) {
        int key = item.hashCode();
        Node<T> pred = null, curr = null;

        head.lock.lock();                    // lock head first
        try {
            pred = head;
            curr = pred.next;
            curr.lock.lock();                // lock the next node
            try {
                while (curr.key < key) {     // traverse until we reach or pass key
                    pred.lock.unlock();      // release pred
                    pred = curr;             // advance pred (noted that curr is still locked)
                    curr = curr.next;        // advance curr
                    curr.lock.lock();        // lock new curr
                }
                // Now curr.key >= key
                if (curr.key == key) {       // key already present
                    return false;
                } else {                     // key not present, insert new node
                    Node<T> newNode = new Node<>();
                    newNode.item = item;
                    newNode.key = key;
                    newNode.next = curr;
                    pred.next = newNode;
                    return true;
                }
            } finally {
                curr.lock.unlock();          // always unlock curr
            }
        } finally {
            pred.lock.unlock();              // always unlock pred
        }
    }   
    public boolean remove(T item) {
        int key = item.hashCode();
        Node<T> pred = null, curr = null;

        head.lock.lock();                    // lock head first
        try {
            pred = head;
            curr = pred.next;
            curr.lock.lock();                // lock the next node
            try {
                while (curr.key < key) {     // traverse until we reach or pass key
                    pred.lock.unlock();      // release pred
                    pred = curr;             // advance pred
                    curr = curr.next;        // advance curr
                    curr.lock.lock();        // lock new curr
                }
                // Now curr.key >= key
                if (curr.key == key) {       // key found
                    pred.next = curr.next;   // remove curr
                    return true;
                } else {                     // key not found
                    return false;
                }
            } finally {
                curr.lock.unlock();          // always unlock curr
            }
        } finally {
            pred.lock.unlock();              // always unlock pred
        }
    }
    public boolean contains(T item) {
        int key = item.hashCode();
        Node<T> pred = null, curr = null;

        head.lock.lock();                    // lock head first
        try {
            pred = head;
            curr = pred.next;
            curr.lock.lock();                // lock the next node
            try {
                while (curr.key < key) {     // traverse until we reach or pass key
                    pred.lock.unlock();      // release pred
                    pred = curr;             // advance pred
                    curr = curr.next;        // advance curr
                    curr.lock.lock();        // lock new curr
                }
                // Now curr.key >= key
                return (curr.key == key); 
            } finally {
                curr.lock.unlock();          // always unlock curr
            }
        } finally {
            pred.lock.unlock();              // always unlock pred
        }
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<T> curr = head.next;
        sb.append("HohList: [");
        while (curr != tail) {
            sb.append(curr.item);
            curr = curr.next;
            if (curr != tail) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
