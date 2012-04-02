package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Trace {
	
	public static int VERBOSE_MODE = 1;
	public static int WRITE_LOG = 0;
	
	private static FileChannel fc;
	private RandomAccessFile logfile;
	
	public static Trace getLogger(String classname){
		Trace logger = new Trace();
		if(WRITE_LOG == 1){
			try {
				File f = new File(classname+"_Log.txt");
				logger.logfile = new RandomAccessFile(f, "rw");
				fc = logger.logfile.getChannel();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return logger;
	}
	public void trace(String str)  {
		if(VERBOSE_MODE == 1)
			System.out.println(str);

		if(WRITE_LOG == 1){
			try {
				fc.write(ByteBuffer.wrap((str+"\n").getBytes()));
			} catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	public void traceInline(String str) {
		if(VERBOSE_MODE == 1)
			System.out.print(str);
		
		if(WRITE_LOG == 1){
			try {
				fc.write(ByteBuffer.wrap((str).getBytes()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void traceError(String str) {
		if(VERBOSE_MODE == 1)
			System.err.println(str);

        if(WRITE_LOG == 1){
            try {
                fc.write(ByteBuffer.wrap(("Error: "+str+"\n").getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
}

