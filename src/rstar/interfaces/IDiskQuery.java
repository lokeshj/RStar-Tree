package rstar.interfaces;

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

    IRStarNode load(long nodeId) throws FileNotFoundException;

    int saveTree(TreeDTO tree, File saveFile);

    TreeDTO loadTree(File saveFile) throws FileNotFoundException;
}
