package rstar;

import rstar.dto.NodeDTO;
import rstar.interfaces.IDtoConvertible;
import rstar.interfaces.IRStarNode;
import rstar.spatial.HyperRectangle;

public abstract class RStarNode implements IDtoConvertible, IRStarNode{
    protected long nodeId = -1;
    protected static int _dimension;
    protected static int CAPACITY;
    protected HyperRectangle mbr;
    public long[] childPointers;   //ids of all children = filenames of children

    public RStarNode() {
    }

    @Override
    public void createId() {
        if(nodeId != -1)
            nodeId = this.hashCode();       //create nodeId only once.
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

    @Override
    public NodeDTO toDTO() {
        return null;
    }
}