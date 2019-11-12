package com.krylov.software_design.lab_1;

public class Node {

    private int key;
    private int value;
    private Node next;
    private Node prev;

    Node(int key, int value) {
        this.key = key;
        this.value = value;
    }

    Node getNext() {
        return next;
    }

    void setNext(Node next) {
        this.next = next;
    }

    Node getPrev() {
        return prev;
    }

    void setPrev(Node prev) {
        this.prev = prev;
    }

    int getKey() {
        return key;
    }

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
