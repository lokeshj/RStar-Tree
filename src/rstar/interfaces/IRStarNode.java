package rstar.interfaces;

import rstar.spatial.HyperRectangle;

import java.io.Serializable;
import java.util.ArrayList;

public interface IRStarNode extends Serializable {
    public boolean isLeaf();
    public boolean isNotFull();
    public <T> int insert(T newChild);
    public HyperRectangle getMBR();
    public <T> ArrayList<T> getOverlappingChildren(HyperRectangle searchRegion);
    public long getNodeId();
}
