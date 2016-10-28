import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Xuhao Chen on 2016/10/26.
 */
public class TSP {

    public final static String fileName = "/Users/START_Eric/myCode/IntellijCode/TSPGeneticAlgorithm/TSP/TSP/src/att48.tsp";
    public Integer numOfCities = 0;
    public static City[] citiesList;
    public EdgeWeightType edgeWeightType;


    private int cityStartLine;
    private ArrayList<String> file = new ArrayList<>();

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


    public TSP(){

        FileProcessor fp = new FileProcessor();

        fp.readFile(fileName);

        buildCityList();



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
            System.out.println("City "+tag+" has been saved" );

        }
    }

    public static void main(String args[]){new TSP();}
}

