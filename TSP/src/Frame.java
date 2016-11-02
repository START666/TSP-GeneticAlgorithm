import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by Xuhao Chen on 2016/10/30.
 */
public class Frame extends JFrame {
    public Frame(int width, int height, City[] citiesList,Integer[] edgesList){
        this.getContentPane().add(new Panel(citiesList,edgesList));
        setTitle("Display Panel");
        setSize(width,height);
        setVisible(true);
    }

    private class Panel extends JPanel{

        private City[] citiesList;
        private Integer[] edgesList;

        public Panel(City[] citiesList, Integer[] edgesList){
            setBackground(Color.white);
            this.citiesList = citiesList;
            this.edgesList = edgesList;

        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setColor(Color.black);
            for(int i=0;i<citiesList.length;i++){  //draw points with tags
                City tmp = citiesList[i];
                int displayX = tmp.x/10;
                int displayY = tmp.y/10;
                if(tmp.tag>=10) g.drawString(tmp.tag.toString(),displayX+55,displayY+30);
                else g.drawString(tmp.tag.toString(),displayX+58,displayY+30);
                drawPoint(displayX+60,displayY+30,g);
            }

            System.out.println("Number of cities: " + citiesList.length);

            Integer numOfEdges = edgesList.length;
            for(int i=0;i<edgesList.length;i++){    //draw edges which are in the edgesList
                City tmp1 = citiesList[edgesList[i]-1];
                City tmp2;
                if(i == edgesList.length-1) tmp2 = citiesList[edgesList[0]-1];
                else tmp2 = citiesList[edgesList[i+1]-1];
                if(TSP.debugOutput) System.out.println("From City " + tmp1.tag + " to City " + tmp2.tag);
                g.drawLine(tmp1.x/10+60, tmp1.y/10+30, tmp2.x/10+60, tmp2.y/10+30);

            }
            System.out.println("edgesList: "+TSP.integerArrayToString(edgesList));
            System.out.println("Number of edges: " + numOfEdges);

        }

    }

    private void drawPoint(int x, int y, Graphics g){
        g.fillOval(x,y,5,5);
    }
}
