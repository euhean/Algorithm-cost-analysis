package trees;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LevelOrderIteratorTest extends AbstractLinkedBinaryTreeTest {
    @Test
    void hasNext_should_return_false_on_empty_tree() {
        assertFalse(empty.levelOrderIterator().hasNext());
    }

    @Test
    void hasNext_should_return_true_on_nonempty_tree() {
        assertTrue(tree.levelOrderIterator().hasNext());
    }

    @Test
    void next_should_throw_nse_on_empty_tree() {
        assertThrows(NoSuchElementException.class, empty.levelOrderIterator()::next);
    }

    @Test
    void set_without_next_should_throw_ise() {
        assertThrows(IllegalStateException.class, () -> empty.levelOrderIterator().set(0));
    }

    @Test
    void root_tree() {
        var tree = new LinkedBinaryTree<>(null, 1, null);
        var it = tree.levelOrderIterator();

        assertEquals(it.next(), 1);
        assertFalse(it.hasNext());
    }

    @Test
    void complete_tree_4() {
        var tree = completeTree(4);
        var expected = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        assertEquals(expected, iterate(tree.levelOrderIterator()));
    }

    @Test
    void degenerate_tree_5() {
        var tree = leftDegenerateTree(5);
        assertEquals(List.of(1, 2, 3, 4, 5), iterate(tree.levelOrderIterator()));
    }

    @Test
    void modify_complete_tree_4() {
        var tree = completeTree(4);
        var it = tree.inOrderIterator();

        while (it.hasNext())
            it.set(it.next() * 2);

        var expected = List.of(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30);
        assertEquals(expected, iterate(tree.levelOrderIterator()));
    }
}
