package trees;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CopyTest extends AbstractLinkedBinaryTreeTest {
    @Test
    void copied_tree_should_be_equal() {
        var copied = new LinkedBinaryTree<>(tree);
        assertEquals(tree, copied);
    }

    @Test
    void copy_constructor_should_deep_copy() {
        var copied = new LinkedBinaryTree<>(tree);
        forEachSubtree(copied, subtree -> subtree.setRoot(subtree.root() + 1));
        assertNotEquals(tree, copied);
    }

    @Test
    void copy_of_empty_tree_should_be_empty() {
        var copied = new LinkedBinaryTree<>(empty);
        assertTrue(copied.isEmpty());
    }

    @Test
    void copy_constructor_should_throw_npe_on_null() {
        assertThrows(NullPointerException.class, () -> new LinkedBinaryTree<>(null));
    }


    @Test
    void copy_should_keep_element_references() {
        var tree = new LinkedBinaryTree<>(
                new LinkedBinaryTree<>(
                        null,
                        new ArrayList<>(),
                        null
                ),
                new ArrayList<>(),
                new LinkedBinaryTree<>(
                        null,
                        new ArrayList<>(),
                        null
                )
        );

        var copied = new LinkedBinaryTree<>(tree);
        forEachSubtree(tree, subtree -> subtree.root().add(42));

        assertEquals(tree, copied);
    }
}
