package rstar.spatial;

import rstar.nodes.RStarNode;
import util.Constants;

import java.util.Comparator;

/**
 * Comparator for comparing two mbr's in a particular
 * dimension only either the high value or the lower value
 */
public class SpatialComparator implements Comparator {
    private int dimension;
    private int order;

    /**
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
    * Compares the two specified MBRs according to
    * the sorting dimension and the sorting co-ordinate for the dimension
     * of this Comparator.
    *
    * @param o1 the first MBR or SpatialPoint or RStarNode
    * @param o2 the second MBR or SpatialPoint or RStarNode
    * @return a negative integer, zero, or a positive integer as the
    *         first argument is less than, equal to, or greater than the
    *         second.
    */
    @Override
    public int compare(Object o1, Object o2) {
        HyperRectangle mbr1, mbr2;
        if (o1 instanceof SpatialPoint) {
            SpatialPoint[] temp = new SpatialPoint[1];
            temp[0] = (SpatialPoint) o1;
            mbr1 = new HyperRectangle(Constants.DIMENSION, temp);
            temp[0] = (SpatialPoint)o2;
            mbr2 = new HyperRectangle(Constants.DIMENSION, temp);
        }
        else if (o1 instanceof RStarNode) {
             mbr1 = ((RStarNode)o1).getMBR();
             mbr2 = ((RStarNode)o2).getMBR();
        } else {
            mbr1 = (HyperRectangle) o1;
            mbr2 = (HyperRectangle) o2;
        }

        int answer = 0;
        if (mbr1.getPoints()[dimension][order] < mbr2.getPoints()[dimension][order])
            answer = -1;

        if (mbr1.getPoints()[dimension][order] > mbr2.getPoints()[dimension][order])
            answer = 1;

        return answer;
    }
}
