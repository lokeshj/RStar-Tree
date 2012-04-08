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

    public NodeDTO(ArrayList<Long> children, MbrDTO mbr, boolean leaf) {
        this.children = children;
        this.mbr = mbr;
        isLeaf = leaf;
    }
}
