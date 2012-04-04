package rstar;

import rstar.spatial.HyperRectangle;

public class RStarNode {
    protected long nodeId = -1;
    protected static int _dimension;
    protected static int CAPACITY;
    protected HyperRectangle mbr;
    protected long[] childPointers;   //ids of all children = filenames of children

    public RStarNode() {
    }

    protected void createId() {
        if(nodeId != -1)
            nodeId = this.hashCode();       //create nodeId only once.
    }

}