package rstar.interfaces;

import rstar.RStarNode;
import rstar.dto.PointDTO;
import rstar.dto.TreeDTO;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * User: Lokesh
 * Date: 4/4/12
 * Time: 9:07 PM
 */
public interface IDiskQuery {
    void save(IRStarNode node);

    RStarNode load(long nodeId) throws FileNotFoundException;

    long savePoint(PointDTO pointDTO);

    PointDTO loadPoint(long pointer);

    int saveTree(TreeDTO tree, File saveFile);

    TreeDTO loadTree(File saveFile) throws FileNotFoundException;
}
