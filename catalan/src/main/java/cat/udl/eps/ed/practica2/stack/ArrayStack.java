package cat.udl.eps.ed.practica2.stack;

import java.util.NoSuchElementException;

/**
 * An implementation of a stack using an extensible array
 *
 * @param <E> the type of elements in the stack
 * @see Stack
 */
public class ArrayStack<E> implements Stack<E> {

    private static final int default_size = 10;
    private int size;
    private E[] stack;
    private int topPos;
    private int elemCount;

    /**
     * Creates an empty stack
     * @implNote
     * Uses a default size of 10 to create the array
     */
    public ArrayStack() {
        this.topPos = -1;
        this.elemCount = 0;
        this.size = default_size;
        this.stack = (E[]) new Object[size];
    }

    /**
     * Adds an element to the top of the stack.
     *
     * @implNote
     *  If there is no room in the stack, the size of the array is doubled
     *
     * @param elem the element to be added
     */
    @Override
    public void push(E elem) {
        if (elemCount == size) resize();
        this.stack[topPos + 1] = elem;
        this.elemCount++;
        this.topPos++;
    }

    /**
     * Returns the element at the top of the stack.
     *
     * @return the element at the top of the stack
     * @throws NoSuchElementException if the stack is empty
     */
    @Override
    public E top() {
        if (isEmpty()) throw new NoSuchElementException("The stack is empty.");
        return stack[topPos];
    }

    /**
     * Removes the element at the top of the stack.
     *
     * @throws NoSuchElementException if the stack is empty
     */
    @Override
    public void pop() {
        if (isEmpty()) throw new NoSuchElementException("The stack is empty.");
        this.stack[topPos] = null;
        this.elemCount--;
        this.topPos--;
    }

    /**
     * Returns true if the stack is empty, false otherwise.
     *
     * @return true if the stack is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return elemCount == 0;
    }

    private void resize() {
        int newSize = size * 2;
        E[] resizedStack = (E[]) new Object[newSize];
        for (int i = 0; i < elemCount; i++)
            resizedStack[i] = stack[i];
        this.stack = resizedStack;
        this.size = newSize;
    }

    void clear() {
        while(elemCount > 0) pop();
        this.size = default_size;
    }

    public int getSize() {
        return size;
    }

    public int getTopPos() {
        return topPos;
    }

    public int getElemCount() {
        return elemCount;
    }
}
