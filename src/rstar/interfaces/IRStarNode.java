package rstar.interfaces;

import rstar.spatial.HyperRectangle;

import java.util.ArrayList;

public interface IRStarNode{

    public boolean isLeaf();

    public boolean isNotFull();

    public <T> int insert(T newChild);

    public HyperRectangle getMBR();

    public <T> ArrayList<T> getOverlappingChildren(HyperRectangle searchRegion);

    void createId();

    long getNodeId();

    void setNodeId(long nodeId);
}
