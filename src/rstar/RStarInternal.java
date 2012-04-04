package rstar;

import rstar.dto.NodeDTO;
import rstar.interfaces.IRStarNode;
import rstar.spatial.HyperRectangle;
import rstar.spatial.SpatialPoint;

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
        mbr = new HyperRectangle(dimension);
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
    public HyperRectangle getMBR() {
        return mbr;
    }

    @Override
    public ArrayList<IRStarNode> getOverlappingChildren(HyperRectangle searchRegion) {
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
        HyperRectangle pointmbr = new HyperRectangle(_dimension, pt);
        return mbr.deltaV_onInclusion(pointmbr);
    }

    @Override
    public NodeDTO toDTO() {
        //TODO
        return super.toDTO();
    }
}
