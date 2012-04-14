package util;

import java.util.List;

/**
 * User: Lokesh
 * Date: 1/4/12
 * Time: 10:00 PM
 */
public class Utils {
    private static long idSeed = 1;

    public static float getMedian(List<Long> list) {
        int size = list.size();
        if(size % 2 == 0)
            return (list.get(size/2) + list.get(size/2 +1))/(float)2;
        else
            return list.get((size+1)/2);
    }

    public static synchronized long getRandomId() {
        return idSeed++;
    }
}
