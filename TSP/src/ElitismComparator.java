import java.util.Comparator;

/**
 * Created by Xuhao Chen on 2016/11/2.
 */
public class ElitismComparator implements Comparator<Integer[]> {
    @Override
    public int compare(Integer[] o1, Integer[] o2) {

        int i=0;

        try{
            int d1 = TSP.getTotalDistance(o1);
            int d2 = TSP.getTotalDistance(o2);
            i = d1-d2;
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;

    }
}
