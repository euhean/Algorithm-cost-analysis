package cat.udl.eps.ed.practica2.stack;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayStackTest {

    @Test
    void isEmpty_should_return_true_for_empty_stacks() {
        var emptyStack = new ArrayStack<>();
        assertTrue(emptyStack.isEmpty());
    }

    @Test
    void pop_on_empty_stack_should_throw_nse_exception() {
        var emptyStack = new ArrayStack<>();
        assertThrows(NoSuchElementException.class, emptyStack::pop);
    }

    @Test
    void top_on_empty_stack_should_throw_nse_exception() {
        var emptyStack = new ArrayStack<>();
        assertThrows(NoSuchElementException.class, emptyStack::pop);
    }

    @Test
    void push_on_empty_stack_should_have_one_elem_and_top_return_it() {
        var potatoStack = new ArrayStack<>();
        potatoStack.push("Potato");
        assertEquals(1, potatoStack.getElemCount());
        assertEquals("Potato", potatoStack.top());
    }

    @Test
    void fill_stack_till_proper_resize_and_empty_it_should_maintain_lifo_order() {
        var potatoStack = new ArrayStack<>();
        int init_size = potatoStack.getSize();
        for (int i = 0; i <= init_size; i++) {
            potatoStack.push("Potato" + i);
            assertEquals("Potato" + i, potatoStack.top());
        }
        assertEquals(init_size * 2, potatoStack.getSize());
        for (int i = potatoStack.getTopPos(); i >= 0; i--) {
            assertEquals("Potato" + i, potatoStack.top());
            potatoStack.pop();
        }
        assertTrue(potatoStack.isEmpty());
    }

    @Test
    void push_null_element_should_be_allowed_and_returned() {
        var stack = new ArrayStack<>();
        stack.push(null);
        assertFalse(stack.isEmpty());
        assertNull(stack.top());
        stack.pop();
        assertTrue(stack.isEmpty());
    }

    @Test
    void clear_should_empty_and_downsize_the_stack() {
        var stack = new ArrayStack<>();
        stack.push("Element1");
        stack.push("Element2");
        stack.clear();
        assertTrue(stack.isEmpty());
        assertEquals(-1, stack.getTopPos());
    }
}
