package rstar.interfaces;

import rstar.nodes.RStarNode;
import rstar.dto.PointDTO;
import rstar.dto.TreeDTO;

import java.io.File;
import java.io.FileNotFoundException;

public interface IDiskQuery {
    void saveNode(RStarNode node);

    RStarNode loadNode(long nodeId) throws FileNotFoundException;

    long savePoint(PointDTO pointDTO);

    PointDTO loadPoint(long pointer);

    int saveTree(TreeDTO tree, File saveFile);

    TreeDTO loadTree(File saveFile);
}
