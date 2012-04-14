package rstar;

import rstar.spatial.HyperRectangle;
import rstar.spatial.SpatialComparator;
import rstar.spatial.SpatialPoint;
import util.Constants;

import java.util.ArrayList;
import java.util.Collections;

/**
 * User: Lokesh
 * Date: 14/4/12
 * Time: 10:48 PM
 */
public class RStarSplit {
    private int dimension;
    public int bestSortOrder;

    public RStarSplit(int dimension) {
        this.dimension = dimension;
        this.bestSortOrder = -1;
    }

    /**
     * computes the split axis for the given list of entries
     * @param entries the points to be split
     * @return the index of the dimension perpendicular to which splitting
     * should be done
     */
    public int chooseLeafSplitAxis(final ArrayList<SpatialPoint> entries) {
        int splitAxis = 0;
        ArrayList<SpatialPoint> maxSorting = (ArrayList<SpatialPoint>) entries.clone();
        ArrayList<SpatialPoint> minSorting = (ArrayList<SpatialPoint>) entries.clone();

        // best value for total margin
        double minMargin = Double.MAX_VALUE;

        for (int i = 0; i < dimension; i++) {
            double margin = 0.0;
            // sort the entries according to their minimal and according to their maximal value
            final SpatialComparator compMin = new SpatialComparator(i, HyperRectangle.MIN_CORD);
            Collections.sort(minSorting, compMin);
            final SpatialComparator compMax = new SpatialComparator(i, HyperRectangle.MAX_CORD);
            Collections.sort(maxSorting, compMax);

            for (int k = 0; k <= (entries.size() - 2 * Constants.MIN_CHILDREN); k++) {
                HyperRectangle mbr1 = new HyperRectangle(dimension, minSorting.subList(0, Constants.MIN_CHILDREN + k));
                HyperRectangle mbr2 = new HyperRectangle(dimension, minSorting.subList(Constants.MIN_CHILDREN + k, entries.size()));

                margin += mbr1.margin() + mbr2.margin();

                mbr1 = new HyperRectangle(dimension, maxSorting.subList(0, Constants.MIN_CHILDREN + k));
                mbr2 = new HyperRectangle(dimension, maxSorting.subList(Constants.MIN_CHILDREN + k, entries.size()));
                margin += mbr1.margin() + mbr2.margin();
            }

            if (margin < minMargin) {
                splitAxis = i;
                minMargin = margin;
            }
        }
        return splitAxis;
    }

    public int chooseInternalSplitAxis(ArrayList<RStarNode> children) {
        int splitAxis = 0;
        ArrayList<RStarNode> maxSorting = (ArrayList<RStarNode>) children.clone();
        ArrayList<RStarNode> minSorting = (ArrayList<RStarNode>) children.clone();

        // best value for total margin
        double minMargin = Double.MAX_VALUE;

        for (int i = 0; i < dimension; i++) {
            double margin = 0.0;
            // sort the entries according to their minimal and according to their maximal value
            final SpatialComparator compMin = new SpatialComparator(i, HyperRectangle.MIN_CORD);
            Collections.sort(minSorting, compMin);
            final SpatialComparator compMax = new SpatialComparator(i, HyperRectangle.MAX_CORD);
            Collections.sort(maxSorting, compMax);

            for (int k = 0; k <= (children.size() - 2 * Constants.MIN_CHILDREN); k++) {
                HyperRectangle mbr1 = new HyperRectangle(dimension, minSorting.subList(0, Constants.MIN_CHILDREN + k));
                HyperRectangle mbr2 = new HyperRectangle(dimension, minSorting.subList(Constants.MIN_CHILDREN + k, children.size()));

                margin += mbr1.margin() + mbr2.margin();

                mbr1 = new HyperRectangle(dimension, maxSorting.subList(0, Constants.MIN_CHILDREN + k));
                mbr2 = new HyperRectangle(dimension, maxSorting.subList(Constants.MIN_CHILDREN + k, children.size()));
                margin += mbr1.margin() + mbr2.margin();
            }

            if (margin < minMargin) {
                splitAxis = i;
                minMargin = margin;
            }
        }
        return splitAxis;
    }

    /**
     * computes the split point for the given list of entries
     * it sets bestSort to 0 or 1 depending upon whether splitting should be done
     * according to maximal or minimal value for the given splitAxis
     * @param entries the points to be split
     * @return the split point
     */
    public int chooseLeafSplitpoint(final ArrayList<SpatialPoint> entries, final int splitAxis)
    {
        int splitPoint = 0;
        // numEntries
        int numEntries = entries.size();

        ArrayList<SpatialPoint> maxSorting = (ArrayList<SpatialPoint>) entries.clone();
        ArrayList<SpatialPoint> minSorting = (ArrayList<SpatialPoint>) entries.clone();

        // sort upper and lower in the right dimension
        final SpatialComparator compMin = new SpatialComparator(splitAxis, HyperRectangle.MIN_CORD);
        Collections.sort(minSorting, compMin);
        final SpatialComparator compMax = new SpatialComparator(splitAxis, HyperRectangle.MAX_CORD);
        Collections.sort(maxSorting, compMax);

        // the split point (first set to minimum entries in the node)
        splitPoint = Constants.MIN_CHILDREN;
        // best value for the overlap
        double minOverlap = Double.MAX_VALUE;
        // the volume of mbr1 and mbr2
        double volume = 0.0;
        int minEntries = Constants.MIN_CHILDREN;

        bestSortOrder = -1;

        for (int i = 0; i <= numEntries - 2 * minEntries; i++) {
            // test the sorting with respect to the minimal values
            HyperRectangle mbr1 = new HyperRectangle(dimension, minSorting.subList(0, minEntries + i));
            HyperRectangle mbr2 = new HyperRectangle(dimension, minSorting.subList(minEntries + i, entries.size()));

            double currentOverlap = mbr1.overlap(mbr2);
            if (currentOverlap < minOverlap || (currentOverlap == minOverlap && (mbr1.volume() + mbr2.volume()) < volume)) {
                minOverlap = currentOverlap;
                splitPoint = minEntries + i;
                bestSortOrder = HyperRectangle.MIN_CORD;
                volume = mbr1.volume() + mbr2.volume();
            }
            // test the sorting with respect to the maximal values
            mbr1 = new HyperRectangle(dimension, maxSorting.subList(0, minEntries + i));
            mbr2 = new HyperRectangle(dimension, maxSorting.subList(minEntries + i, entries.size()));

            currentOverlap = mbr1.overlap(mbr2);
            if (currentOverlap < minOverlap || (currentOverlap == minOverlap && (mbr1.volume() + mbr2.volume()) < volume)) {
                minOverlap = currentOverlap;
                splitPoint = minEntries + i;
                bestSortOrder = HyperRectangle.MAX_CORD;
                volume = mbr1.volume() + mbr2.volume();
            }
        }
        return splitPoint;
    }

    public int chooseInternalSplitpoint(ArrayList<RStarNode> children, int splitAxis) {
        int splitPoint = 0;
        // numEntries
        int numEntries = children.size();

        ArrayList<RStarNode> maxSorting = (ArrayList<RStarNode>) children.clone();
        ArrayList<RStarNode> minSorting = (ArrayList<RStarNode>) children.clone();

        // sort upper and lower in the right dimension
        final SpatialComparator compMin = new SpatialComparator(splitAxis, HyperRectangle.MIN_CORD);
        Collections.sort(minSorting, compMin);
        final SpatialComparator compMax = new SpatialComparator(splitAxis, HyperRectangle.MAX_CORD);
        Collections.sort(maxSorting, compMax);

        // the split point (first set to minimum entries in the node)
        splitPoint = Constants.MIN_CHILDREN;
        // best value for the overlap
        double minOverlap = Double.MAX_VALUE;
        // the volume of mbr1 and mbr2
        double volume = 0.0;
        int minEntries = Constants.MIN_CHILDREN;

        bestSortOrder = -1;

        for (int i = 0; i <= numEntries - 2 * minEntries; i++) {
            // test the sorting with respect to the minimal values
            HyperRectangle mbr1 = new HyperRectangle(dimension, minSorting.subList(0, minEntries + i));
            HyperRectangle mbr2 = new HyperRectangle(dimension, minSorting.subList(minEntries + i, children.size()));

            double currentOverlap = mbr1.overlap(mbr2);
            if (currentOverlap < minOverlap || (currentOverlap == minOverlap && (mbr1.volume() + mbr2.volume()) < volume)) {
                minOverlap = currentOverlap;
                splitPoint = minEntries + i;
                bestSortOrder = HyperRectangle.MIN_CORD;
                volume = mbr1.volume() + mbr2.volume();
            }
            // test the sorting with respect to the maximal values
            mbr1 = new HyperRectangle(dimension, maxSorting.subList(0, minEntries + i));
            mbr2 = new HyperRectangle(dimension, maxSorting.subList(minEntries + i, children.size()));

            currentOverlap = mbr1.overlap(mbr2);
            if (currentOverlap < minOverlap || (currentOverlap == minOverlap && (mbr1.volume() + mbr2.volume()) < volume)) {
                minOverlap = currentOverlap;
                splitPoint = minEntries + i;
                bestSortOrder = HyperRectangle.MAX_CORD;
                volume = mbr1.volume() + mbr2.volume();
            }
        }
        return splitPoint;
    }

}
