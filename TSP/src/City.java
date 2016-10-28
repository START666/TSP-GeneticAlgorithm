/**
 * Created by Xuhao Chen on 2016/10/27.
 */
public class City {
    public final Integer tag;
    public final Integer x;
    public final Integer y;

    public City(int aTag, int xCoord, int yCoord){
        tag = aTag;
        x = xCoord;
        y = yCoord;
    }

    public boolean equals(City c){
        if(c.tag == this.tag) return true;
        return false;
    }
}
