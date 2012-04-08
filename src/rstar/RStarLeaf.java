package rstar;

import rstar.dto.NodeDTO;
import rstar.spatial.HyperRectangle;
import rstar.spatial.SpatialPoint;
import util.Constants;

import java.util.ArrayList;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 2:22 AM
 */
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
        _dimension = Constants.DIMENSION;
        childPointers = dto.children;
        loadedChildren = new ArrayList<SpatialPoint>();
//        mbr = dto.mbr
        // TODO mbr
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

   /* @Override
    public ArrayList<SpatialPoint> getOverlappingChildren(HyperRectangle searchRegion) {
        HyperRectangle intersection = mbr.getIntersection(searchRegion);
        return pointsInRegion(intersection);
    }

    private ArrayList<SpatialPoint> pointsInRegion(HyperRectangle region) {
        //TODO pointsInRegion
        return loadedChildren;
    }*/

    @Override
    public NodeDTO toDTO() {
        return new NodeDTO(childPointers, mbr.toDTO(), true);
    }

    public boolean hasUnsavedPoints(){
        return loadedChildren.size() > 0;
    }
}
