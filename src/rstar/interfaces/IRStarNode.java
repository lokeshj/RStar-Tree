package rstar.interfaces;

import rstar.spatial.HyperRectangle;

public interface IRStarNode{

    public boolean isLeaf();

    public boolean isNotFull();

    public <T> int insert(T newChild);

    public HyperRectangle getMBR();

    public void setMbr(HyperRectangle mbr);

    void createId();

    long getNodeId();

    void setNodeId(long nodeId);
}
