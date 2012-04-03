package rstar;

import java.util.ArrayList;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 2:55 AM
 */
public class RStarInternal extends RStarNode implements IRStarNode {
    private transient ArrayList<IRStarNode> children;

    public RStarInternal(int dimension) {
        _dimension = dimension;
        children = new ArrayList<IRStarNode>(CAPACITY);
        mbr = new MBR(dimension);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isNotFull() {
        return children.size() < CAPACITY;
    }

    @Override
    public <T> int insert(T newChild) {
        if (this.isNotFull() && newChild instanceof IRStarNode) {
            children.add((IRStarNode)newChild);
            mbr.update(((IRStarNode) newChild).getMBR());
            return 1;
        }
        else return -1;
    }

    @Override
    public MBR getMBR() {
        return mbr;
    }

    @Override
    public ArrayList<IRStarNode> getOverlappingChildren(MBR searchRegion) {
        //TODO
        return children;
    }

    @Override
    public long getNodeId() {
        createId();
        return nodeId;
    }

    public long changeInVolume(SpatialPoint newPoint) {
        SpatialPoint[] pt = new SpatialPoint[1];
        pt[0] = newPoint;
        MBR pointmbr = new MBR(_dimension, pt);
        return mbr.deltaV_onInclusion(pointmbr);
    }
}
