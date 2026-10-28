public class RingBuffer {
    private Node head;
    private Node tail;

    public RingBuffer(int capacity) {
        if (capacity < 1){
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        head = new Node();
        Node current = head;
        for (int i = 0; i < capacity; i++){
            current.next = new Node();
            current = current.next;
        }
        current.next = head;
        tail = head;
    }

    public synchronized void put(String message) throws InterruptedException {
        while(tail.next == head){
            wait();
        }
        tail.item = message;
        tail = tail.next;
        notifyAll();
    }

    public synchronized String take() throws InterruptedException {
        while (head == tail) {
            wait();
        }
        String message = head.item;
        head.item = null;
        head = head.next;
        notifyAll();
        return message;
    }

    private static class Node {
        private Node next;
        private String item;
    }
}
