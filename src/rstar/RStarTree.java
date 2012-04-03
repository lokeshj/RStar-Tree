package rstar;

import rstar.dto.TreeFile;
import util.Constants;

import java.io.File;

/**
 * User: Lokesh
 * Date: 3/4/12
 * Time: 1:29 AM
 */
public class RStarTree implements ISpatialQuery {

    public RStarTree() {
        dimension = Constants.DIMENSION;
        pagesize = Constants.PAGESIZE;
        storage = new StorageManager();
        saveFile = new File(Constants.TREE_FILE);
        initStorage();
        setCapacities();
    }

    public RStarTree(String saveFile, int dimension, int pagesize) {
        this.dimension = dimension;
        this.pagesize = pagesize;
        this.storage = new StorageManager();
        this.saveFile = new File(saveFile);
        initStorage();
        setCapacities();
    }

    public RStarTree(String saveFile) {
        this.saveFile = new File(saveFile);
        this.storage = new StorageManager();
        initStorage();
        setCapacities();
    }

    private void initStorage() {
        if (saveFile.exists() && saveFile.length() != 0) {
            TreeFile treeData = storage.loadTree(saveFile);
            if(treeData != null){
                dimension = treeData.dimension;
                pagesize = treeData.pagesize;
                rootPointer = treeData.rootPointer;
            }
        }
    }

    private void setCapacities(){
        //TODO calculate leaf and directory capacities and update Constants.
    }

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

    private void loadRoot() {
        if (root == null) {
            //empty tree
            root = storage.load(rootPointer);
            if(root == null)            // still null -> empty tree
            {
                root = new RStarLeaf(dimension);
            }
            rootPointer = root.getNodeId();
        }
    }

    private IRStarNode loadNode(long nodeId) {
        return storage.load(nodeId);
    }

    public int saveTree() {
        TreeFile save = new TreeFile(dimension, pagesize, rootPointer);
        return storage.saveTree(save, saveFile);
    }

    private int dimension;
    private int pagesize;
    private File saveFile;
    private StorageManager storage;
    private IRStarNode root;
    private long rootPointer;

}
