package com.gnid.social.pincee.utils.collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.AbstractSequentialList;
import java.util.ListIterator;

public class MyLinkedList<E> extends AbstractSequentialList {

    private final Callback mCallback;

    public MyLinkedList(Callback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public ListIterator listIterator(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    private class ListItr implements ListIterator<E> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public E previous() {
            return null;
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return 0;
        }

        @Override
        public void remove() {

        }

        @Override
        public void set(E e) {

        }

        @Override
        public void add(E e) {

        }
    }

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    interface Callback{}
}
