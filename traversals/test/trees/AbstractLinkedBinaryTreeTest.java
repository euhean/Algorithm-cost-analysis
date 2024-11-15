package trees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

abstract class AbstractLinkedBinaryTreeTest {
    final static LinkedBinaryTree<Integer> empty = new LinkedBinaryTree<>();
    final LinkedBinaryTree<Integer> tree =
            new LinkedBinaryTree<>(
                    new LinkedBinaryTree<>(null, 2, new LinkedBinaryTree<>(null, 4, null)),
                    1,
                    new LinkedBinaryTree<>(new LinkedBinaryTree<>(null, 5, null), 3, null)
            );

    // Utility function to simplify tests on iterators
    static <E> List<E> iterate(Iterator<E> it) {
        var list = new ArrayList<E>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    static <E> void forEachSubtree(LinkedBinaryTree<E> tree, Consumer<? super LinkedBinaryTree<E>> fn) {
        if (tree.isEmpty())
            return;

        fn.accept(tree);
        forEachSubtree(tree.left(), fn);
        forEachSubtree(tree.right(), fn);
    }

    LinkedBinaryTree<Integer> completeTree(int height) {
        return completeTree(height, 1);
    }

    LinkedBinaryTree<Integer> completeTree(int height, int n) {
        if (height == 1)
            return new LinkedBinaryTree<>(null, n, null);
        return new LinkedBinaryTree<>(completeTree(height - 1, 2 * n), n, completeTree(height - 1, 2 * n + 1));
    }

    LinkedBinaryTree<Integer> leftDegenerateTree(int height) {
        return leftDegenerateTree(height, 1);
    }

    LinkedBinaryTree<Integer> leftDegenerateTree(int height, int n) {
        if (height == 1)
            return new LinkedBinaryTree<>(null, n, null);
        return new LinkedBinaryTree<>(leftDegenerateTree(height - 1, n + 1), n, null);
    }
}
