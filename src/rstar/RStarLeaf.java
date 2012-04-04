package rstar;

import rstar.dto.NodeDTO;
import rstar.interfaces.IRStarNode;
import rstar.spatial.HyperRectangle;
import rstar.spatial.SpatialPoint;

import java.util.ArrayList;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 2:22 AM
 */
public class RStarLeaf extends RStarNode implements IRStarNode {
    private transient ArrayList<SpatialPoint> children;

    public RStarLeaf(int dimension) {
        createId();
        _dimension = dimension;
        children = new ArrayList<SpatialPoint>(CAPACITY);
        mbr = new HyperRectangle(dimension);
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
    public HyperRectangle getMBR() {
        return mbr;
    }

    @Override
    public long getNodeId() {
        createId();
        return nodeId;
    }

    @Override
    public ArrayList<SpatialPoint> getOverlappingChildren(HyperRectangle searchRegion) {
        HyperRectangle intersection = mbr.getIntersection(searchRegion);
        return pointsInRegion(intersection);
    }

    private ArrayList<SpatialPoint> pointsInRegion(HyperRectangle region) {
        //TODO
        return children;
    }

    @Override
    public NodeDTO toDTO() {
        //TODO
        return super.toDTO();
    }
}
