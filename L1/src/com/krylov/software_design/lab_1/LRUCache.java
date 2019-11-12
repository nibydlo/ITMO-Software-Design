package com.krylov.software_design.lab_1;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    private static final int DEFAULT_CAPACITY = 10;
    private final int capacity;
    private Map<Integer, Node> cache;
    private Node head;
    private Node tail;

    public LRUCache() {
        this(DEFAULT_CAPACITY);

        assert this.capacity == DEFAULT_CAPACITY;
        assert cache.isEmpty();
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>(capacity);

        assert this.capacity == capacity;
        assert cache.isEmpty();
    }


    public int get(int key) {
        assert cache != null;

        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            moveToFront(node);

            assert cache.containsKey(key);

            return node.getValue();
        }

        assert !cache.containsKey(key);

        return -1;
    }

    public void put(int key, int value) {
        assert cache != null;

        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.setValue(value);
            moveToFront(node);
            return;
        }

        Node node = new Node(key, value);

        if (cache.size() == capacity) {
            cache.remove(tail.getKey());
            removeNode(tail);
        }

        cache.put(key, node);
        addFirst(node);

        assert cache.containsKey(key);
        assert cache.get(key).getValue() == value;
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addFirst(node);
    }

    private void removeNode(Node node) {
        Node prevNode = node.getPrev();
        Node nextNode = node.getNext();

        if (prevNode != null) {
            prevNode.setNext(nextNode);
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.setPrev(prevNode);
        } else {
            tail = prevNode;
        }
    }

    private void addFirst(Node node) {
        node.setNext(head);
        node.setPrev(null);

        if (head != null) {
            head.setPrev(node);
        }
        head = node;

        if (tail == null) {
            tail = node;
        }
    }
}
