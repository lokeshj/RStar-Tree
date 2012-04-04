package rstar;

import rstar.dto.TreeDTO;
import rstar.interfaces.IRStarNode;
import rstar.interfaces.ISpatialQuery;
import rstar.spatial.SpatialPoint;
import util.Constants;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:29 AM
 */
public class RStarTree implements ISpatialQuery {

    public RStarTree() {
        dimension = Constants.DIMENSION;
        pagesize = Constants.PAGESIZE;
        saveFile = new File(Constants.TREE_FILE);
        storage = new StorageManager();
        initStorage();
        setCapacities();
    }

    public RStarTree(String saveFile, int dimension, int pagesize) {
        this.dimension = dimension;
        this.pagesize = pagesize;
        this.saveFile = new File(saveFile);
        this.storage = new StorageManager();
        initStorage();
        setCapacities();
    }

    public RStarTree(String saveFile) {
        dimension = Constants.DIMENSION;
        pagesize = Constants.PAGESIZE;
        this.saveFile = new File(saveFile);
        this.storage = new StorageManager();
        initStorage();
        setCapacities();
    }

    private void initStorage() {
        if (saveFile.exists() && saveFile.length() != 0) {
            try {
                TreeDTO treeData = storage.loadTree(saveFile);
                if (treeData != null) {             //update tree fields from saveFile
                    dimension = treeData.dimension;
                    pagesize = treeData.pagesize;
                    rootPointer = treeData.rootPointer;
                }
            } catch (FileNotFoundException e) {
                System.err.println("Failed to load R* Tree from "+saveFile.getName());
            }
        }
    }

    private void setCapacities(){
        //TODO calculate leaf and directory capacities and update Constants.
    }

    /*
        QUERY FUNCTIONS
     */
    @Override
    public int insert(SpatialPoint point) {
        loadRoot();
        //TODO
        System.out.println("inserting point with oid="+point.getOid());
        return 1;
    }

    @Override
    public float pointSearch(SpatialPoint point) {
        //TODO
        System.out.println("searching point :"+point);
        return 1;
    }

    @Override
    public SpatialPoint[] rangeSearch(SpatialPoint center, double range) {
        //TODO
        System.out.println("range search in range "+range+" of point: "+center);
        return null;
    }

    @Override
    public SpatialPoint[] knnSearch(SpatialPoint center, int k) {
        //TODO
        System.out.println("knn search with k = "+k+" and point: "+center);
        return null;
    }

    /*
     ***** DISK RELATED FUNCTIONS ****
     */
    private void loadRoot() {
        if (root == null) {
            //empty tree
            root = loadNode(rootPointer);
            if (root == null)            // still null -> empty tree
            {
                root = new RStarLeaf(dimension);
            }
            rootPointer = root.getNodeId();
        }
    }

    private IRStarNode loadNode(long nodeId) {
        //check for valid nodeId
        if (nodeId != -1) {
            try {
                return storage.load(nodeId);
            } catch (FileNotFoundException e) {
                System.err.println("Error while loading R* Tree node from file " + storage.constructFilename(nodeId));
            }
        }
        return null;
    }

    public int saveTree() {
        TreeDTO save = new TreeDTO(dimension, pagesize, rootPointer);
        return storage.saveTree(save, saveFile);
    }

    private int dimension;
    private int pagesize;
    private File saveFile;
    private StorageManager storage;
    private IRStarNode root;
    private long rootPointer = -1;

}
