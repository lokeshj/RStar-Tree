package rstar.nodes;

import rstar.StorageManager;
import rstar.dto.PointDTO;
import rstar.spatial.HyperRectangle;
import rstar.spatial.SpatialComparator;
import rstar.spatial.SpatialPoint;
import util.Constants;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import static java.util.Arrays.sort;

/**
 * Provides functionality for splitting of RStarNodes
 */
public class RStarSplit {
    private int dimension;
    public int bestSortOrder;
    private StorageManager disk;

    public RStarSplit(int dimension, StorageManager storageManager) {
        this.dimension = dimension;
        this.disk = storageManager;
        this.bestSortOrder = -1;
    }

    public RStarLeaf chooseLeaf(RStarNode startNode, HyperRectangle newMbr) {
        if(startNode.isLeaf()) {
            return (RStarLeaf)startNode;
        }

        else {
            ArrayList<Long> childPointers = startNode.childPointers;
            assert childPointers.size() > 0;
            ArrayList<RStarNode> children = new ArrayList<RStarNode>(childPointers.size());
            //load all children
            for (long childId : childPointers) {
                try {
                    children.add(disk.loadNode(childId));
                } catch (FileNotFoundException e) {
                    System.err.println("Exception while loading node from disk. message = "+e.getMessage());
                }
            }

            //check whether children are leaves
            if (children.get(0).isLeaf()) {
                //check for least overlap increment
                ArrayList<Double> minOverlap = new ArrayList<Double>();
                // the candidate nodes for next recursive step
                ArrayList<RStarNode> cands = new ArrayList<RStarNode>();

                for (RStarNode child : children) {
                    HyperRectangle union = child.getMBR().union(newMbr);
                    //find union's overlap with all other children
                    double deltaOverlap = 0;

                    for (RStarNode otherChild : children) {
                        if (otherChild == child) {
                            continue;
                        }

                        deltaOverlap += union.overlap(otherChild.getMBR()) -
                                child.getMBR().overlap(otherChild.getMBR());

                    }

                    if (minOverlap.size() == 0) {
                        cands.add(child);
                        minOverlap.add(deltaOverlap);
                    } else {
                        if (minOverlap.get(0) > deltaOverlap) {
                            minOverlap.removeAll(minOverlap);
                            cands.removeAll(cands);
                            minOverlap.add(deltaOverlap);
                            cands.add(child);
                        }
                        else if (minOverlap.get(0) == deltaOverlap) {
                            minOverlap.add(deltaOverlap);
                            cands.add(child);
                        }
                    }
                }

                if(cands.size() == 1)
                    return chooseLeaf(cands.get(0), newMbr);
                    //break ties
                else{
                    ArrayList<Double> minAreas = new ArrayList<Double>();
                    ArrayList<RStarNode> cands2 = new ArrayList<RStarNode>();

                    double deltaV;
                    for (RStarNode candNode : cands) {
                        deltaV = candNode.getMBR().deltaV_onInclusion(newMbr);
                        if(minAreas.size() == 0 || minAreas.get(0) > deltaV) {
                            minAreas.removeAll(minAreas);
                            cands2.removeAll(cands2);
                            minAreas.add(deltaV);
                            cands2.add(candNode);
                        }
                        else if (minAreas.get(0) == deltaV) {
                            minAreas.add(deltaV);
                            cands2.add(candNode);
                        }
                    }

                    if(cands2.size() == 1)
                        return chooseLeaf(cands2.get(0), newMbr);
                    else {
                        //again break ties
                        double minArea = Double.MAX_VALUE;
                        RStarNode candidate = null;
                        for (RStarNode candNode : cands2) {
                            double vol = candNode.getMBR().volume();
                            if( vol < minArea ){
                                minArea = vol;
                                candidate = candNode;
                            }
                        }
                        return chooseLeaf(candidate, newMbr);
                    }
                }
            } else {
                //check for least volume increment
                ArrayList<Double> minAreas = new ArrayList<Double>();
                ArrayList<RStarNode> cands = new ArrayList<RStarNode>();

                double deltaV;
                for (RStarNode candNode : children) {
                    deltaV = candNode.getMBR().deltaV_onInclusion(newMbr);
                    if(minAreas.size() == 0 || minAreas.get(0) > deltaV) {
                        minAreas.removeAll(minAreas);
                        cands.removeAll(cands);
                        minAreas.add(deltaV);
                        cands.add(candNode);
                    }
                    else if (minAreas.get(0) == deltaV) {
                        minAreas.add(deltaV);
                        cands.add(candNode);
                    }
                }

                if(cands.size() == 1)
                    return chooseLeaf(cands.get(0), newMbr);
                else {
                    //again break ties
                    double minArea = Double.MAX_VALUE;
                    RStarNode candidate = null;
                    for (RStarNode candNode : cands) {
                        double vol = candNode.getMBR().volume();
                        if( vol < minArea ){
                            minArea = vol;
                            candidate = candNode;
                        }
                    }
                    return chooseLeaf(candidate, newMbr);
                }
            }
        }
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
        int splitPoint;
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
        int splitPoint;
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

    public RStarLeaf splitLeaf(RStarLeaf splittingLeaf, SpatialPoint newPoint) throws AssertionError{
        ArrayList<Long> childPointers = splittingLeaf.childPointers;
        if (childPointers.size() <= 0) {
            throw new AssertionError();
        }

        ArrayList<SpatialPoint> children = new ArrayList<SpatialPoint>(childPointers.size());
        //load all children
        for (long childId : childPointers) {
            PointDTO dto = disk.loadPoint(childId);
            children.add(new SpatialPoint(dto));
        }

        children.add(newPoint);
        int splitAxis = chooseLeafSplitAxis(children);
        int splitPoint = chooseLeafSplitpoint(children, splitAxis);

        Object[] sorting = children.toArray();
        final SpatialComparator comp = new SpatialComparator(splitAxis, bestSortOrder);
        sort(sorting, comp);

        splittingLeaf.loadedChildren = new ArrayList<SpatialPoint>();
        splittingLeaf.childPointers = new ArrayList<Long>();
        RStarLeaf newChild = new RStarLeaf(dimension);

        HyperRectangle newMbr1 = new HyperRectangle(dimension);     //adjusted mbr for splittingLeaf
        HyperRectangle newMbr2 = new HyperRectangle(dimension);     //adjusted mbr for newChild

        for (int i = 0; i < sorting.length; i++) {
            SpatialPoint spatialPoint = (SpatialPoint) sorting[i];
            if (i < splitPoint) {
                if (spatialPoint == newPoint) {
                    splittingLeaf.loadedChildren.add(spatialPoint);
                } else {
                    splittingLeaf.childPointers.add(childPointers.get(children.indexOf(spatialPoint)));
                }
                newMbr1.update(spatialPoint);
            } else {
                if (spatialPoint == newPoint) {
                    newChild.loadedChildren.add(spatialPoint);
                } else {
                    newChild.childPointers.add(childPointers.get(children.indexOf(spatialPoint)));
                }
                newMbr2.update(spatialPoint);
            }
        }
        splittingLeaf.setMbr(newMbr1);
        newChild.setMbr(newMbr2);

        disk.saveNode(splittingLeaf);
        return newChild;
    }

    public RStarNode splitInternalNode(RStarInternal splittingNode, RStarNode node) throws FileNotFoundException {
        //load all children of target
        ArrayList<Long> childPointers = splittingNode.childPointers;
        if (childPointers.size() <= 0) {
            throw new AssertionError();
        }

        ArrayList<RStarNode> children = new ArrayList<RStarNode>(childPointers.size());
        //load all children
        for (long childNodeId : childPointers) {
            children.add(disk.loadNode(childNodeId));
        }

        children.add(node);
        int splitAxis = chooseInternalSplitAxis(children);
        int splitPoint = chooseInternalSplitpoint(children, splitAxis);

        Object[] sorting = children.toArray();
        final SpatialComparator comp = new SpatialComparator(splitAxis, bestSortOrder);
        sort(sorting, comp);

        splittingNode.childPointers = new ArrayList<Long>();
        RStarInternal createdNode = new RStarInternal(dimension);

        HyperRectangle newMbr1 = new HyperRectangle(dimension);
        HyperRectangle newMbr2 = new HyperRectangle(dimension);

        for (int i = 0; i < sorting.length; i++) {
            RStarNode childNode = (RStarNode) sorting[i];
            if (i < splitPoint) {
                splittingNode.childPointers.add(childNode.getNodeId());
                childNode.setParentId(splittingNode.getNodeId());
                newMbr1.update(childNode.getMBR());
            } else {
                createdNode.childPointers.add(childNode.getNodeId());
                childNode.setParentId(createdNode.getNodeId());
                newMbr2.update(childNode.getMBR());
            }
            disk.saveNode(childNode);            //record the updates to disk
        }

        splittingNode.setMbr(newMbr1);
        createdNode.setMbr(newMbr2);

        disk.saveNode(splittingNode);
        return createdNode;
    }
}
