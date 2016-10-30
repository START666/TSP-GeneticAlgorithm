/**
 * Created by Xuhao Chen on 2016/10/30.
 */
public class Edge {
    public final Integer tag1;
    public final Integer tag2;
    public final Integer x1;
    public final Integer y1;
    public final Integer x2;
    public final Integer y2;
    public Edge(Integer tag1,Integer x1, Integer y1, Integer tag2, Integer x2, Integer y2){
        this.tag1 = tag1;
        this.x1 = x1;
        this.y1 = y1;
        this.tag2 = tag2;
        this.x2 = x2;
        this.y2 = y2;
    }

}
