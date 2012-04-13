package rstar.spatial;

import java.util.Comparator;

/**
 * User: Lokesh
 * Date: 13/4/12
 * Time: 9:34 PM
 */
public class SpatialComparator implements Comparator {
    private int dimension;
    private int order;

    /**
     * construct a new comparator for SpatialPoints
     * @param dimension the dimension along which the points
     *                  are to be sorted
     * @param cordToSort the value(higher or lower) used for sorting
     *                   the given dimension
     *                   @see HyperRectangle#MAX_CORD
     *                   @see HyperRectangle#MIN_CORD
     */
    public SpatialComparator(int dimension, int cordToSort) {
        this.dimension = dimension;
        this.order = cordToSort;
    }

    /**
    * Compares the two specified spatialpoints according to
    * the sorting dimension and the sorting value for the dimension
     * of this Comparator.
    *
    * @param o1 the first spatialpoint
    * @param o2 the second spatialpoint
    * @return a negative integer, zero, or a positive integer as the
    *         first argument is less than, equal to, or greater than the
    *         second.
    */
    @Override
    public int compare(Object o1, Object o2) {
        SpatialPoint point1 = (SpatialPoint)o1;
        SpatialPoint point2 = (SpatialPoint)o2;

        int answer = 1;
        if (point1.getCords()[order] < point2.getCords()[order])
            answer = -1;

        if (point1.getCords()[order] > point2.getCords()[order])
            answer =  1;

        return answer;
    }
}
