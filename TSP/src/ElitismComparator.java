import java.util.Comparator;

/**
 * Created by Xuhao Chen on 2016/11/2.
 */
public class ElitismComparator implements Comparator<Integer[]> {
    @Override
    public int compare(Integer[] o1, Integer[] o2) {

        return TSP.getTotalDistance(o1) - TSP.getTotalDistance(o2);

    }
}
