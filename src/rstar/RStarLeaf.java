package rstar;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Lokesh
 * Date: 3/4/12
 * Time: 2:22 AM
 */
public class RStarLeaf implements IRStarNode {
    private int _dimension;
    private MBR mbr;
    private int CAPACITY;
    private ArrayList<SpatialPoint> children;

    public RStarLeaf(int dimension) {
        _dimension = dimension;
        children = new ArrayList<SpatialPoint>(CAPACITY);
        mbr = new MBR(dimension);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isNotFull() {
        return (children.size() < CAPACITY);
    }

    @Override
    public <T> int insert(T newChild) {
        if (this.isNotFull() && (newChild instanceof SpatialPoint)) {
            children.add((SpatialPoint) newChild);
            mbr.update((SpatialPoint) newChild);
            return 1;
        } else return -1;
    }

    @Override
    public MBR getMBR() {
        return mbr;
    }

    @Override
    public ArrayList<SpatialPoint> getOverlappingChildren(MBR searchRegion) {
        MBR intersection = this.mbr.getIntersection(searchRegion);
        return pointsInRegion(intersection);
    }

    private ArrayList<SpatialPoint> pointsInRegion(MBR region) {
        //TODO
        return children;
    }
}
