import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Xuhao Chen on 2016/10/26.
 */
public class TSP {

    public static String fileName ;
    public Integer numOfCities = 0;
    public static City[] citiesList;
    public static Integer[] edgesList;
    public EdgeWeightType edgeWeightType;


    private boolean debugFileChooser = false;
    private int cityStartLine;
    private ArrayList<String> file = new ArrayList<>();
    private Thread readerThread;
    private Thread outputThread;
    private Thread displayThread;
    private Thread calculateThread;
    private Object lock = new Object();

    private boolean getOutput = false;

    public enum EdgeWeightType{
        ATT,
        EXPLICIT;
    }

//    private enum Tag{
//        NAME ,
//        COMMENT ,
//        TYPE ,
//        DIMENSION ,
//        EDGE_WEIGHT_TYPE,
//        NODE_COORD_SECTION,
//        EOF;
//
//        public static Tag toEnum(String tag){
//
//            if(tag.equals("NAME")) return NAME;
//            if(tag.equals("COMMENT")) return COMMENT;
//            if(tag.equals("TYPE")) return TYPE;
//            if(tag.equals("DIMENSION")) return DIMENSION;
//            if(tag.equals("EDGE_WEIGHT_TYPE")) return EDGE_WEIGHT_TYPE;
//            if(tag.equals("NODE_COORD_SECTION")) return NODE_COORD_SECTION;
//            if(tag.equals("EOF")) return EOF;
//
//            return null;
//        }
//    }

    private class FileProcessor{

        public void readFile(String filePath) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                int lineNumber=0;

                boolean EOF = false;
                while(!EOF){
                    line = br.readLine();
                    file.add(line);

                    setMetadata(line,lineNumber);

                    if(line.contains("EOF")) EOF = true;
                    lineNumber++;
                }

            }catch (FileNotFoundException fileNotFoundException){
                System.out.println("FileNotFound Exception");
                fileNotFoundException.printStackTrace();

            }catch (IOException iOException){
                System.out.println("IO Exception");
                iOException.printStackTrace();
            }
        }

        private void setMetadata(String aLine, int lineNumber){

            if(aLine.contains("DIMENSION")){  //pair DIMENSION
                int position = aLine.length()-1;
                while(!(aLine.charAt(position) == (char)32 && position != aLine.length()-1)){
                    if(position < 0) break;
                    position --;
                }
                position++;
                numOfCities = Integer.parseInt(aLine.substring(position,aLine.length()));   //numOfCities set
                edgesList = new Integer[numOfCities];
            }
            else if(aLine.contains("EDGE_WEIGHT_TYPE")){  //pair EDGE_WEIGHT_TYPE
                int position = aLine.length()-1;
                while(!(aLine.charAt(position) == (char)32 && position != aLine.length()-1)){
                    if(position < 0) break;
                    position --;
                }
                position++;
                String aLineSub = aLine.substring(position,aLine.length());
                if(aLineSub.equals("ATT")) edgeWeightType = EdgeWeightType.ATT;
                else if(aLineSub.equals("EXPLICIT")) edgeWeightType = EdgeWeightType.EXPLICIT;
            }
            else if(aLine.contains("NODE_COORD_SECTION")){
                cityStartLine = lineNumber+1;
            }
        }
    }

    private class FileReaderRunnable implements Runnable{

        @Override
        public void run() {
            synchronized (lock) {
                FileProcessor fp = new FileProcessor();
                if(!debugFileChooser){
                    JFileChooser fileChooser = new JFileChooser();
                    try{
                        fileChooser.showOpenDialog(null);
                    }catch (HeadlessException hE){
                        System.err.println("Open File Dialog Error");
                        hE.printStackTrace();
                    }
                    File tmp = fileChooser.getSelectedFile();

                    fileName = tmp.getAbsolutePath();
                }else fileName = "/Users/START_Eric/myCode/IntellijCode/TSPGeneticAlgorithm/TSP/TSP/src/att48.tsp";
                System.out.println("Open file: " + fileName);

                fp.readFile(fileName);

                buildCityList();
                lock.notify();

                System.out.println("Total "+numOfCities+" number of cities have been saved.");

                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Thread done.");
            }

        }
    }

    private class OutputRunnable implements Runnable{

        @Override
        public void run() {
            synchronized (lock) {
                lock.notify();
                for (int i = 0; i < citiesList.length; i++) {
                    City tmp = citiesList[i];
                    System.out.println("City " + tmp.tag + " :(" + tmp.x + ", " + tmp.y + ")");
                }

            }

        }
    }

    private class DisplayRunnable implements Runnable{

        @Override
        public void run() {
            synchronized (lock){
                Frame frame = new Frame(900,600,citiesList,edgesList);
            }
        }
    }

    private class CalculateRunnable implements Runnable{

        @Override
        public void run() {
            synchronized (lock){
                /*********** Random Generate a edgeList(TEST ONLY) ***********/
                int numOfEdgeSaved=0;
                Random rand = new Random();
                boolean[] set = new boolean[numOfCities];
                for(int i=0;i<set.length;i++)  set[i] = false;

                while(numOfEdgeSaved != numOfCities){
                    Integer i = rand.nextInt(numOfCities);
                    if(!set[i]){
                        edgesList[numOfEdgeSaved] = i + 1;
                        set[i]=true;
                        numOfEdgeSaved++;
                    }
                }
                /***********************************************************/
            }
        }
    }


    public TSP(){

        readerThread = new Thread(new FileReaderRunnable());
        if(getOutput) outputThread = new Thread(new OutputRunnable());
        displayThread = new Thread(new DisplayRunnable());
        calculateThread = new Thread(new CalculateRunnable());

        readerThread.start();
        if(getOutput) outputThread.start();
        calculateThread.start();
        displayThread.start();


//        for(int i=0;i<file.size();i++){
//            System.out.println(file.get(i));
//        }
//
//        System.out.println("numOfCities = " + numOfCities);
//        System.out.println("edgeWeightType = " + edgeWeightType.toString());


    }

    private void buildCityList(){
        citiesList = new City[numOfCities];
        for(int i = cityStartLine;i<file.size()-1;i++){
            String line = file.get(i);

            int tag;
            int xCoord;
            int yCoord;

            Queue<Integer> spaceLocation = new LinkedList<>();
            for(int j = 0;j<line.length();j++){
                if(line.charAt(j) == (char)32){
                    spaceLocation.offer(j);
                }
            }

            int first=spaceLocation.poll();
            int second=spaceLocation.poll();

            tag =  Integer.parseInt(line.substring(0,first));
            xCoord = Integer.parseInt(line.substring(first+1,second));
            yCoord = Integer.parseInt(line.substring(second+1,line.length()));

            citiesList[tag-1] = new City(tag,xCoord,yCoord);
//            System.out.println("City "+tag+" has been saved" );

        }
    }

    public static void main(String args[]){new TSP();}
}

