package trees;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class InOrderIteratorTest extends AbstractLinkedBinaryTreeTest {
    @Test
    void hasNext_should_return_false_on_empty_tree() {
        assertFalse(empty.inOrderIterator().hasNext());
    }

    @Test
    void hasNext_should_return_true_on_nonempty_tree() {
        assertTrue(tree.inOrderIterator().hasNext());
    }

    @Test
    void next_should_throw_nse_on_empty_tree() {
        assertThrows(NoSuchElementException.class, empty.inOrderIterator()::next);
    }

    @Test
    void set_without_next_should_throw_ise() {
        assertThrows(IllegalStateException.class, () -> empty.inOrderIterator().set(0));
    }

    @Test
    void root_tree() {
        var tree = new LinkedBinaryTree<>(null, 1, null);
        var it = tree.inOrderIterator();

        assertEquals(it.next(), 1);
        assertFalse(it.hasNext());
    }

    @Test
    void complete_tree_4() {
        var tree = completeTree(4);
        var expected = List.of(8, 4, 9, 2, 10, 5, 11, 1, 12, 6, 13, 3, 14, 7, 15);
        assertEquals(expected, iterate(tree.inOrderIterator()));
    }

    @Test
    void degenerate_tree_5() {
        var tree = leftDegenerateTree(5);
        assertEquals(List.of(5, 4, 3, 2, 1), iterate(tree.inOrderIterator()));
    }

    @Test
    void modify_complete_tree_4() {
        var tree = completeTree(4);
        var it = tree.inOrderIterator();

        while (it.hasNext())
            it.set(it.next() * 2);

        var expected = List.of(16, 8, 18, 4, 20, 10, 22, 2, 24, 12, 26, 6, 28, 14, 30);
        assertEquals(expected, iterate(tree.inOrderIterator()));
    }
}
