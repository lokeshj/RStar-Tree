package rstar;

import java.io.Serializable;
import java.util.ArrayList;

public interface IRStarNode extends Serializable {
    public boolean isLeaf();
    public boolean isNotFull();
    public <T> int insert(T newChild);
    public MBR getMBR();
    public <T> ArrayList<T> getOverlappingChildren(MBR searchRegion);
    public long getNodeId();
}
