package rstar.dto;

import java.util.ArrayList;

/**
 * User: Lokesh
 * Date: 4/4/12
 * Time: 9:06 PM
 */
public class NodeDTO extends AbstractDTO {
    public ArrayList<Long> children;
    public MbrDTO mbr;
    public boolean isLeaf;
    public long parentId;

    public NodeDTO(long parentId, boolean leaf, MbrDTO mbr, ArrayList<Long> children) {
        this.parentId = parentId;
        isLeaf = leaf;
        this.mbr = mbr;
        this.children = children;
    }
}
