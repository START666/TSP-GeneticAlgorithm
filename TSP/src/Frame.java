import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by Xuhao Chen on 2016/10/30.
 */
public class Frame extends JFrame {
    public Frame(int width, int height, City[] citiesList, Queue<Edge> edgesList){
        this.getContentPane().add(new Panel(citiesList,edgesList));
        setTitle("Display Panel");
        setSize(width,height);
        setVisible(true);
    }

    private class Panel extends JPanel{

        private City[] citiesList;
        private Queue<Edge> edgesList;

        public Panel(City[] citiesList, Queue<Edge> edgesList){
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
            Integer numOfEdges = edgesList.size();
            while(!edgesList.isEmpty()){
                Edge tmp = edgesList.poll();
                System.out.println("From City " + tmp.tag1 + " to City " + tmp.tag2);
                g.drawLine(tmp.x1/10+60, tmp.y1/10+30, tmp.x2/10+60, tmp.y2/10+30);

            }
            System.out.println("Number of edges: " + numOfEdges);

        }

    }

    private void drawPoint(int x, int y, Graphics g){
        g.fillOval(x,y,5,5);
    }
}
