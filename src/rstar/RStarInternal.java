package rstar;

import rstar.dto.NodeDTO;
import rstar.interfaces.IRStarNode;
import rstar.spatial.HyperRectangle;
import rstar.spatial.SpatialPoint;
import util.Constants;

import java.util.ArrayList;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 2:55 AM
 */
public class RStarInternal extends RStarNode {
    private transient ArrayList<RStarNode> children;

    public RStarInternal(int dimension) {
        _dimension = dimension;
        children = new ArrayList<RStarNode>(Constants.MAX_CHILDREN);
        childPointers = new ArrayList<Long>(Constants.MAX_CHILDREN);
        mbr = new HyperRectangle(dimension);
    }

    public RStarInternal(NodeDTO dto, long nodeId) {
        this.nodeId = nodeId;
        this.setParentId(dto.parentId);
        this.childPointers = dto.children;
        this.mbr = new HyperRectangle(dto.mbr);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isNotFull() {
        return children.size() < Constants.MAX_CHILDREN;
    }

    @Override
    public <T> int insert(T newChild) {
        if (this.isNotFull() && newChild instanceof RStarNode) {
            ((RStarNode) newChild).setParentId(this.nodeId);
            childPointers.add(((RStarNode) newChild).getNodeId());
            mbr.update(((RStarNode) newChild).getMBR());
            return 1;
        }
        else return -1;
    }

    @Override
    public HyperRectangle getMBR() {
        return mbr;
    }

    public double deltaV_onInclusion(SpatialPoint newPoint) {
        HyperRectangle pointmbr = new HyperRectangle(_dimension);
        pointmbr.update(newPoint);
        return mbr.deltaV_onInclusion(pointmbr);
    }

    @Override
    public NodeDTO toDTO() {
        return new NodeDTO(getParentId(), false, mbr.toDTO(), childPointers);
    }
}
