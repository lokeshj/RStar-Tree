package rstar;

import rstar.dto.TreeDTO;
import rstar.interfaces.IDtoConvertible;
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
public class RStarTree implements ISpatialQuery, IDtoConvertible {

    private int dimension;
    private int pagesize;
    private File saveFile;
    private StorageManager storage;
    private RStarNode root;
    private long rootPointer = -1;

    public RStarTree() {
        init(Constants.TREE_FILE, Constants.DIMENSION, Constants.PAGESIZE);
    }

    public RStarTree(int dimension) {
        init(Constants.TREE_FILE, dimension, Constants.PAGESIZE);
    }

    public RStarTree(String saveFile, int dimension, int pagesize) {
        init(saveFile, dimension, pagesize);
    }

    private void init(String saveFile, int dimension, int pagesize){
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
        loadTree();
        createDataDir();
    }

    private void setCapacities(){
        Constants.DIMENSION = dimension;
        //TODO add cost for mbr
        Constants.MAX_CHILDREN = Constants.PAGESIZE/8;          // M = (pagesize - mbr_size)/ (size of Long = 8)
        Constants.MIN_CHILDREN = Constants.MAX_CHILDREN/3;      // m = M/3
    }

    /* QUERY FUNCTIONS */

    /**
     * inserts a point in the tree and saves it on disk
     * @param point the point to be inserted
     * @return 1 if successful, else -1
     */
    @Override
    public int insert(SpatialPoint point) {
        //TODO insert
        System.out.println("inserting point with oid=" + point.getOid());
        loadRoot();
        if (root.isLeaf()) {
            // root is leaf
            if (root.isNotFull()) {
                //insert in root
                int status = root.insert(point);
                if(status == 1) {
                    storage.saveNode(root);
                } else {
                    System.out.println("failed to insert");
                }

                return status;
            } else {
                System.out.println("node full");
                return -1;
            }
        } else {
            System.out.println("root is not leaf");
            return -1;
        }
    }

    /**
     * searches for a spatial point in the tree and
     * returns its oid if its found.
     * @param point the point to be searched
     * @return oid of the point if found, else -1.
     */
    @Override
    public float pointSearch(SpatialPoint point) {
        //TODO pointSearch
        System.out.println("searching point :"+point);
        return -1;
    }

    /**
     * searches for points in the given range of the center point
     * @param center center point of the search region.
     * @param range radius of the search region.
     * @return array of all the points found in the range
     */
    @Override
    public SpatialPoint[] rangeSearch(SpatialPoint center, double range) {
        //TODO rangeSearch
        System.out.println("range search in range "+range+" of point: "+center);
        return null;
    }

    /**
     * searches for the k nearest neighbours of a center point
     * @param center SpatialPoint
     * @param k number of nearest neighbours required
     * @return array of the k nearest neighbours of center.
     */
    @Override
    public SpatialPoint[] knnSearch(SpatialPoint center, int k) {
        //TODO knnsearch
        System.out.println("knn search with k = "+k+" and point: "+center);
        return null;
    }

    /*
     ***** DISK RELATED FUNCTIONS ****
     */

    /**
     * loads root from disk if exists
     * otherwise creates a new LeafNode and
     * assigns it root.
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

    /**
     * loads Nodes from disk using their nodeId
     * @param nodeId the nodeId attribute of the Node
     *               to be loaded
     * @return the Node required, null uf it doesn't exist
     */
    private RStarNode loadNode(long nodeId) {
        //check for valid nodeId
        if (nodeId != -1) {
            try {
                return storage.loadNode(nodeId);
            } catch (FileNotFoundException e) {
                System.err.println("Error while loading R* Tree node from file " + storage.constructFilename(nodeId));
            }
        }
        return null;
    }

    /**
     * saves the tree details to disk
     * @return 1 if successful, -1 otherwise
     */
    public int save() {
        return storage.saveTree(this.toDTO(), saveFile);
    }

    /**
     * converts this tree to its DTO representation
     * which in turn can be saved to disk.
     * @return TreeDTO object which is the DTO form of
     * this tree
     */
    @Override
    public TreeDTO toDTO() {
        return new TreeDTO(dimension, Constants.PAGESIZE, rootPointer);
    }

    private void createDataDir() {
        // check for the node-data directory. create one if doesn't exist
        File dataDir = new File(saveFile.getParentFile(), Constants.TREE_DATA_DIRECTORY);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            if (!dataDir.mkdir()) {
                System.err.println("Failed to create data directory of the tree. Exiting..");
                System.exit(1);
            }
            System.out.println("Data directory created");
        }
    }

    private void loadTree() {
        if (saveFile.exists() && saveFile.length() != 0) {
            try {
                TreeDTO treeData = storage.loadTree(saveFile);
                if (treeData != null) {             //update tree fields from saveFile
                    this.dimension = treeData.dimension;
                    this.pagesize = treeData.pagesize;
                    this.rootPointer = treeData.rootPointer;
                    System.out.printf("Tree loaded successfully from %s. dimension = %d and pagesize = %d bytes%n",
                            saveFile.getName(), dimension, pagesize);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Failed to load R* Tree from "+saveFile.getName());
            }

        }
    }
}
