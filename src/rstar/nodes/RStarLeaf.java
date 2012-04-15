package rstar.nodes;

import rstar.dto.NodeDTO;
import rstar.spatial.HyperRectangle;
import rstar.spatial.SpatialPoint;
import util.Constants;

import java.util.ArrayList;

public class RStarLeaf extends RStarNode {
    public ArrayList<SpatialPoint> loadedChildren;

    public RStarLeaf(int dimension) {
        createId();
        _dimension = dimension;
        loadedChildren = new ArrayList<SpatialPoint>();
        childPointers = new ArrayList<Long>();
        mbr = new HyperRectangle(dimension);
    }

    public RStarLeaf(NodeDTO dto, long nodeId) {
        this.nodeId = nodeId;
        this.setParentId(dto.parentId);
        _dimension = Constants.DIMENSION;
        childPointers = dto.children;
        loadedChildren = new ArrayList<SpatialPoint>();
        mbr = new HyperRectangle(dto.mbr);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isNotFull() {
        return ((childPointers.size() + loadedChildren.size()) < Constants.MAX_CHILDREN);
    }

    @Override
    public <T> int insert(T newChild) {
        if (this.isNotFull()) {
            loadedChildren.add((SpatialPoint) newChild);
            mbr.update((SpatialPoint) newChild);
            return 1;
        }
        else return -1;
    }

    @Override
    public HyperRectangle getMBR() {
        return mbr;
    }

    @Override
    public void setMbr(HyperRectangle mbr) {
        this.mbr = mbr;
    }

    @Override
    public NodeDTO toDTO() {
        return new NodeDTO(getParentId(), true, mbr.toDTO(), childPointers);
    }

    public boolean hasUnsavedPoints(){
        return loadedChildren.size() > 0;
    }
}
