package rstar.nodes;

import rstar.interfaces.IDtoConvertible;
import rstar.interfaces.IRStarNode;
import rstar.spatial.HyperRectangle;
import util.Utils;

import java.util.ArrayList;

public abstract class RStarNode implements IDtoConvertible, IRStarNode{
    protected long nodeId = -1;
    protected static int _dimension;
    protected HyperRectangle mbr;
    public ArrayList<Long> childPointers;   //ids of all children = file names are derivable from this.

    private Long parentId;

    public Long getParentId() {
        return parentId;
    }

    @Override
    public void createId() {
        if (nodeId == -1) {
            nodeId = Utils.getRandomId();
            if(nodeId < 0)
                nodeId = -1 * nodeId;
        }
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public long getNodeId() {
        createId();
        return nodeId;
    }

    @Override
    public void setNodeId(long nodeId1){
        this.nodeId = nodeId1;
    }
}