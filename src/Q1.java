import rstar.RStarTree;
import rstar.spatial.SpatialPoint;
import util.Trace;
import util.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static util.Utils.getMedian;

public class Q1 {

	private RStarTree tree;
    private int dimension;
	private String inputFile;
	private String resultFile;
	private List<Long> insertRunTime;
	private List<Long> searchRunTime;
	private List<Long> rangeRuntime;
	private List<Long> knnRuntime;
    private Trace logger;

    public static void main(String[] args) {
        Q1 controller = new Q1(args);

		System.out.println("Reading input file ...");
		controller.processInput();
		System.out.println("Finished Processing file ...");

        controller.writeRuntimeToFile(controller.insertRunTime, "Insertion_runtime.txt");
        controller.writeRuntimeToFile(controller.searchRunTime, "Search_runtime.txt");
        controller.writeRuntimeToFile(controller.rangeRuntime, "RangeSearch_runtime.txt");
        controller.writeRuntimeToFile(controller.knnRuntime, "KNNSearch_runtime.txt");

		controller.printResults();
	}

	public Q1(String[] args) {
		if(args.length >= 2){
			this.inputFile = args[0];
            this.dimension = Integer.parseInt(args[1]);

			if (args.length >= 3)
				this.resultFile = args[2];
			else
				this.resultFile = this.getClass().getSimpleName()+ "_Results.txt";

		} else {
			this.printUsage();
			System.exit(1);
		}
		tree = new RStarTree(dimension);
		this.insertRunTime = new ArrayList<Long>();
		this.searchRunTime = new ArrayList<Long>();
		this.rangeRuntime = new ArrayList<Long>();
		this.knnRuntime = new ArrayList<Long>();
        logger = Trace.getLogger(this.getClass().getSimpleName());
	}


	protected void processInput() {
        float opType, oid, k;
		double range;
        float[] point;
        long start, end;
        int lineNum = 0;

        try {
            BufferedReader input =  new BufferedReader(new FileReader(this.inputFile));
            String line;
            String[] lineSplit;

			while ((line = input.readLine()) != null) {
				lineNum++;
                lineSplit = line.split(",");
                opType = Float.parseFloat(lineSplit[0]);

				switch ((int)opType) {
				case 0:
				{       //insertion
					try {
                        if (lineSplit.length != (this.dimension + 2)) {
                            throw new AssertionError();
                        }

                        oid = Float.parseFloat(lineSplit[1]);
                        point = extractPoint(lineSplit, 2);

                        start = System.currentTimeMillis();
						tree.insert(new SpatialPoint(point, oid));
                        end = System.currentTimeMillis();

                        this.updateTimeTaken(opType, (end - start));
                        break;

                    } catch (Exception e) {
                        logger.traceError("Exception while processing line " + lineNum +
                                ". Skipped Insertion. message: "+e.getMessage());
                        break;
                    }
                    catch (AssertionError error){
                        logger.traceError("Error while processing line " + lineNum +
                                ".Skipped Insertion. message: "+ error.getMessage());
                        break;
                    }
                }

                    case 1:
				{     //point search
                    try{
                        if (lineSplit.length != this.dimension + 1) {
                            throw new AssertionError();
                        }
                        point = extractPoint(lineSplit, 1);

                        start = System.currentTimeMillis();
                        oid = tree.pointSearch(new SpatialPoint(point));
                        end = System.currentTimeMillis();

                        logger.trace("search result: " + oid);
                        this.updateTimeTaken(opType, (end - start));
                    } catch (Exception e) {
                        logger.traceError("Exception while processing line " + lineNum +
                                ". Skipped Point Search. message: "+e.getMessage());
                    }
                    catch (AssertionError error){
                        logger.traceError("Error while processing line " + lineNum +
                                ". Skipped Point Search. message: "+error.getMessage());
                    }
                    break;
                }

                    case 2:
				{   //range search
                    try{
                        if (lineSplit.length != this.dimension + 2) {
                            throw new AssertionError();
                        }

                        point = extractPoint(lineSplit, 1);
                        range = Double.parseDouble(lineSplit[this.dimension + 1]);
                        SpatialPoint center = new SpatialPoint(point);

                        start = System.currentTimeMillis();
                        List<SpatialPoint> result = tree.rangeSearch(center, range);
                        end = System.currentTimeMillis();

                        logger.trace("Range Search(" + range + ", " + center + "): " + Utils.SpatialPointListToString(result));
                        this.updateTimeTaken(opType, (end - start));
                    } catch (Exception e) {
                        logger.traceError("Exception while processing line " + lineNum +
                                ". Skipped range search. message: "+e.getMessage());
                    }
                    catch (AssertionError error){
                        logger.traceError("Error while processing line " + lineNum +
                                ". Skipped range search. message: "+error.getMessage());
                    }
                    break;
                }

                    case 3:
				{   //knn search
                    try{
                        if (lineSplit.length != this.dimension + 2) {
                            throw new AssertionError();
                        }

                        point = extractPoint(lineSplit, 1);
                        k = Float.parseFloat(lineSplit[this.dimension + 1]);
                        SpatialPoint center = new SpatialPoint(point);

                        start = System.currentTimeMillis();
                        List<SpatialPoint> result = tree.knnSearch(center, (int)k);
                        end = System.currentTimeMillis();

                        logger.trace("Knn Search(" + k + ", " + center + "): " + Utils.SpatialPointListToString(result));
                        this.updateTimeTaken(opType, (end - start));
                    } catch (Exception e) {
                        logger.traceError("Exception while processing line " + lineNum +
                                ". Skipped knn search. message: "+e.getMessage());
                    }
                    catch (AssertionError error){
                        logger.traceError("Error while processing line " + lineNum +
                                ". Skipped knn search. message: "+error.getMessage());
                    }
					break;
				}

				default:
					logger.traceError("Invalid query type " + opType + " at line " + lineNum + ". Skipped .. ");
					break;
				}
			}
			input.close();
            tree.save();
		}
		catch (Exception e) {
			logger.traceError("Error while reading input file. Line " + lineNum + " Skipped\nError Details:");
		}
	}

    private float[] extractPoint(String[] points, int startPos) throws NumberFormatException
    {
        float[] tmp = new float[this.dimension];
        for (int i = startPos, lineSplitLength = points.length;
             ((i < lineSplitLength) && (i < (startPos + this.dimension))); i++)
        {
            tmp[i-startPos] = Float.parseFloat(points[i]);
        }
        return tmp;
    }

    protected void updateTimeTaken(float type, long time) {
		switch ((int)type) {
		case 0:
			insertRunTime.add(time);
			break;
		case 1:
            searchRunTime.add(time);
            break;
        case 2:
			rangeRuntime.add(time);
			break;
		case 3:
			knnRuntime.add(time);
			break;
		default:
			logger.traceError("Invalid Query type encountered. Skipped..");
			break;
		}
	}

	protected void printResults() {
		logger.trace("\nPerforming Run Time calculations..");

		List<Long> combined = new ArrayList<Long>();
		combined.addAll(insertRunTime);
		combined.addAll(searchRunTime);
		combined.addAll(rangeRuntime);
		combined.addAll(knnRuntime);

		String result = "\n"+this.getClass().getSimpleName()+" --RESULTS--";

		String temp = "\n\nInsertion operations:(in milliseconds) "+ generateRuntimeReport(insertRunTime);
        logger.trace(temp);
        result += temp;
		temp = "\n\nSearch operations:(in milliseconds) "+ generateRuntimeReport(searchRunTime);
        logger.trace(temp);
        result += temp;
		temp = "\n\nRange search operations: (in milliseconds) " + generateRuntimeReport(rangeRuntime);
        logger.trace(temp);
        result += temp;
		temp = "\n\nKNN search operations: (in milliseconds) " + generateRuntimeReport(knnRuntime);
        logger.trace(temp);
        result += temp;
		temp = "\n\nCombined operations:(in milliseconds) "+ generateRuntimeReport(combined);
        logger.trace( temp);
        result += temp;

		writeResultToFile(result);
	}
	
	protected String generateRuntimeReport(List<Long> runtime) {
		StringBuilder result = new StringBuilder();
        int size = runtime.size();

        if (size > 0) {
            Collections.sort(runtime);
            try {
                Long percent5th = runtime.get((int) (0.05 * size));
                Long percent95th = runtime.get((int) (0.95 * size));
                float median = getMedian(runtime);
                long sum = 0;
                for (Long aRuntime : runtime) {
                    sum += aRuntime;
                }
                double avg = sum / (double) size;

                result.append("\nTotal ops = ").append(size);
                result.append("\nAvg time: ").append(avg);
                result.append("\n5th percentile: ").append(percent5th);
                result.append("\n95th percentile: ").append(percent95th);
                result.append("\nmedian: ").append(median);

            } catch (Exception e) {
                logger.traceError("Exception while generating runtime results");
                e.printStackTrace();
            }
        }

		return result.toString();
	}

	protected void writeResultToFile(String result) {
		try {
			File outFile = new File(this.resultFile);
			if(outFile.exists()){
				outFile.delete();
			}
			BufferedWriter outBW =  new BufferedWriter(new FileWriter(outFile));
			try{
				logger.trace("\nWriting results to file .. ");
				outBW.write(result);
			}
			finally{
				outBW.close();
				logger.trace("done");
			}
		} 
		catch (IOException e) {
			logger.traceError("IOException while writing results to " + resultFile);
		}
	}

	protected void writeRuntimeToFile(List<Long> runtime, String file) {
		try {
			File f = new File(file);
			if(f.exists())
				f.delete();

			BufferedWriter bf =  new BufferedWriter(new FileWriter(f));
			for(long i : runtime){
				bf.write("" + i + "\n");
			}
			bf.close();
		} catch (IOException e) {
            logger.traceError("IOException while writing runtimes to file.");
//            e.printStackTrace();
        }
	}

	protected void printUsage() {
		System.err.println("Usage: "+ this.getClass().getSimpleName() +
                " <path to input file> <dimension of points> [output file].\noutput file is optional.\n");
	}
}