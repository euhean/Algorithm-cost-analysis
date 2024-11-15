package cat.udl.eps.ed.practica1.sorting;

/**
 * This class contains methods relates to the sorting of arrays of integers.
 *
 * @author jmgimeno
 */

public class IntArraySorter {

    private final int[] array;

    /**
     * Constructs an instance of the class.
     *
     * @param array the array to be sorted.
     */
    public IntArraySorter(int[] array) {
        this.array = array;
    }

    /**
     * Utility method to check is the array is sorted.
     * <p>
     * Only to be used in tests.
     *
     * @return a boolean telling if the array is sorted or not.
     */
    public boolean isSorted() {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Swaps the values at two positions in the array.
     * <p>
     * If either {@code i} or {@code j} are invalid positions in the array,
     * it throws {@code java.lang.ArrayIndexOutOfBoundsException}
     *
     * @param i index of one of the positions
     * @param j index of the other
     */
    public void swap(int i, int j) {
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    /**
     * Sorts the array using the insertion sort algorithm.
     */
    public void insertionSort() {

        // Invariant: The prefix [0, end) is a sorted array
        // Decreasing: array.length - end

        // We insert element at end into this prefix
        for (int end = 1; end < array.length; end++) {

            // Invariant: arrays sorted in the ranges [0, insert)
            // and [insert, end] and all elements in [0, insert)
            // are lower than or equal to those in [insert+1, end]
            // Decreasing: insert

            for (int insert = end; insert >= 1; insert--) {
                if (array[insert - 1] > array[insert]) {
                    // When elements at insert and insert+1 are not sorted,
                    // we swap them and continue to fulfill the (internal)
                    // invariant.
                    swap(insert - 1, insert);
                } else {
                    // When elements at insert and insert+1 are sorted, the
                    // whole [insert, end] is sorted and we can safely increment
                    // end and continue to fulfill the (external) invariant.
                    break;
                }
            }
        }
    }

    /**
     * Sorts the array using the bubble sort algorithm.
     */
    public void bubbleSort() {
        boolean hasSwapped = false;
        for (int i = 0; i < array.length; i++) {
            for (int j = array.length - 1; j > i; j--) {
                if (array[j] < array[j - 1]) {
                    swap(j, j - 1);
                    hasSwapped = true;
                }
            }
            if (!hasSwapped) return;
        }
    }

    /**
     * A slightly modified version that considers the possibility of
     * the subarray defined in [{@code lastSwap}, length - 1] becoming
     * sorted, so we store in {@code currentSwap} the position where
     * the condition meets and iterate over it.
     * Notice this way the unsorted part can shrink faster.
     *
     * @author euhean
     */
    public void bubbleSortOptimized() {
        int lastSwap = array.length - 1;
        int currentSwap = -1;
        for (int i = 1; i < array.length; i++) {
            boolean isSorted = true;
            for (int j = 0; j < lastSwap; j++) {
                if (array[j] > array[j + 1]) {
                    swap(j, j + 1);
                    isSorted = false;
                    currentSwap = j;
                }
            }
            if (isSorted) return;
            lastSwap = currentSwap;
        }
    }

    /**
     * Sorts the array using the selection sort algorithm.
     */
    public void selectionSort() {
        for (int i = 1; i < array.length; i++) {
            boolean isSorted = true;
            for (int j = i; j < array.length; j++) {
                if (array[j] < array[i - 1]) {
                    swap(i - 1, j);
                    isSorted = false;
                }
            }
            if (isSorted) return;
        }
    }

    public void biSelectionSort() {
        for (int i = 0, j = array.length; i < j - 1; i++, j--) {
            int minValue = array[i], maxValue = array[j - 1];
            int minIndex = i, maxIndex = j - 1;
            for (int k = i; k < j; k++) {
                if (array[k] < minValue) {
                    minValue = array[k];
                    minIndex = k;
                }
                else if (array[k] > maxValue) {
                    maxValue = array[k];
                    maxIndex = k;
                }
            }
            swap(i, minIndex);
            if (array[minIndex] == maxValue) swap(j - 1, minIndex);
            else swap(j - 1, maxIndex);
        }
    }

    /**
     * {@code minPos} and {@code maxPos} variables store first
     * {@code i} and {@code j} indexes respectively at array
     * left/right bounds (pos: 0 and pos: L-1).
     * <p></p>
     * These variables will store lower and higher value positions
     * if the condition of the inner loop is met.
     * Iterates the array in {@code i} <= {@code k} <= {@code j}
     * and updates {@code minPos} or {@code maxPos}
     * <p></p>
     * Swaps nums in left margin and found pos for lowest value and
     * the same goes for the right margin and highest value positions.
     * The condition applies to the cases where {@code i} pos was
     * assigned to {@code maxPos} and as they've been previously swapped,
     * {@code minPos} and {@code maxPos} must exchange indexes.
     * <p></p>
     * The unsorted array shrinks as we decrement right
     * and increment left bounds.
     * It's sorted when {@code i} >= {@code j}
     *
     * @author euhean, jmgimeno
     */
    public void biSelectionSortOptimized() {
        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            int minPos = i, maxPos = j;
            for (int k = i; k <= j; k++) {
                if (array[k] < array[minPos]) minPos = k;
                else if (array[k] > array[maxPos]) maxPos = k;
            }
            swap(i, minPos);
            if (i == maxPos) maxPos = minPos;
            swap(j, maxPos);
        }
    }

    /**
     * This is a failed attempt to find a different, more efficient
     * solution in the selection sort method as the execution times
     * exceed the others by far.
     * Nonetheless, we've learned to find new approaches and then
     * analyze and compare their behaviour.
     * <p></p>
     * I don't think there's much to tell about this goofy method.
     * The idea was traversing left to right and vice-versa
     * simultaneously since I contemplated this specific case
     * for odd lengths where arr[{@code minPos}} > arr[mid] < arr[{@code maxPos}]
     * <p></p>
     * I'm not responsible for any sort of permanent brain damage
     * watching this code may cause to colleagues and/or professors.
     *
     * @author euhean
     */
    public void biSelectionSortV2() {
        if (array.length == 2) {
            if (leftSwap(0, 1)) swap(0, 1);
            return;
        }
        for (int minPos = 0, maxPos = array.length - 1; minPos < maxPos; minPos++, maxPos--) {
            for (int i = 1; i < maxPos; i++) {
                if (leftSwap(minPos, i) && rightSwap(maxPos, i)
                        && (minPos + i) == (maxPos - i)
                        && array.length % 2 != 0) swap(minPos, maxPos);
                else {
                    if (leftSwap(minPos, i)) swap(minPos, minPos + i);
                    if (rightSwap(maxPos, i)) swap(maxPos - i, maxPos);
                }
            }
        }
    }

    private boolean leftSwap(int minPos, int i) {
        return array[minPos] > array[minPos + i];
    }

    private boolean rightSwap(int maxPos, int i) {
        return array[maxPos - i] > array[maxPos];
    }

    /**
     * Sorts the array using the quicksort algorithm.
     */
    public void quickSort() {
        quickSort(0, array.length);
    }

    private void quickSort(int left, int right) {
        // DO NOT MODIFY !!!!
        // 0 <= left <= right <= array.length
        if (right - left > 1) {
            int pivotPos = left + (right - left) / 2;
            int pivotValue = array[pivotPos];
            swap(left, pivotPos);
            int pos = partitionIterative(pivotValue, left + 1, right);
            swap(left, pos - 1);
            quickSort(left, pos - 1);
            quickSort(pos, right);
        }
    }

    private int partitionRecursive(int pivotValue, int inf, int sup) {
        // DO NOT USE -> create an iterative version which does the same
        // 0 <= left <= inf <= sup <= right <= v.length
        if (inf == sup) {
            return inf;
        } else if (array[inf] <= pivotValue) {
            return partitionRecursive(pivotValue, inf + 1, sup);
        } else if (array[sup - 1] > pivotValue) {
            return partitionRecursive(pivotValue, inf, sup - 1);
        } else {
            swap(inf, sup - 1);
            return partitionRecursive(pivotValue, inf + 1, sup - 1);
        }
    }

    /**
     * Traverses the array left to right if given value is lower
     * than {@code pivotValue} and right to left if it's higher.
     * <p></p>
     * If none of these conditions are met swap values in
     * {@code inf} and {@code sup} - 1 (since right bound is the
     * actual length of the array) and go both ways.
     * <p></p>
     * Stop when margins intersect and return either one of those.
     *
     * @param pivotValue The first element. Acts as a comparator.
     * @param inf The second element and left index.
     * @param sup The last element and right index.
     * @return Returns the position where {@code inf} = {@code sup}
     *
     * @author euhean
     */
    private int partitionIterative(int pivotValue, int inf, int sup) {
        while (inf < sup) {
            if (array[inf] <= pivotValue) inf++;
            else if (array[sup - 1] > pivotValue) sup--;
            else {
                swap(inf, sup - 1);
                inf++;
                sup--;
            }
        }
        return inf;
    }
}
