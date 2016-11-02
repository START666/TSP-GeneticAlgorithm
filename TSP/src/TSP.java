import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created by Xuhao Chen on 2016/10/26.
 */
public class TSP {

    public static String fileName ;
    public Integer numOfCities = 0;
    public static City[] citiesList;
    public static Integer[] edgesList;
    public static Integer[][] distanceTable;
    public EdgeWeightType edgeWeightType;

    public Integer populationSize = 5000;
    public HashMap<Integer, Integer[]> populationPool;

    public static boolean debugFileChooser = true;
    public static boolean debugOutput = false;
    private int cityStartLine;
    private ArrayList<String> file = new ArrayList<>();
    private Random random;

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

    private void readFile(){
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
        calculateDistanceTable();

        System.out.println("Total "+numOfCities+" number of cities have been saved.");

    }

    private void outputCities(){
        for (int i = 0; i < citiesList.length; i++) {
            City tmp = citiesList[i];
            System.out.println("City " + tmp.tag + " :(" + tmp.x + ", " + tmp.y + ")");
        }
    }

    private void visualization(){
        if(citiesList != null && edgesList != null){
            Frame frame = new Frame(900,600,citiesList,edgesList);
        }
    }

    private void calculate(){
//        edgesList = randomGenerationAPopulation();

        createPopulationPool();
    }

    private Integer[] randomGenerationAPopulation(){
        int numOfEdgeSaved=0;
        Integer[] population = new Integer[numOfCities];
        random = new Random();
        boolean[] set = new boolean[numOfCities];
        for(int i=0;i<set.length;i++)  set[i] = false;

        while(numOfEdgeSaved != numOfCities){
            Integer i = random.nextInt(numOfCities);
            if(!set[i]){
                population[numOfEdgeSaved] = i + 1;
                set[i]=true;
                numOfEdgeSaved++;
            }
        }
        return population;
    }

    private void createPopulationPool(){
        populationPool = new HashMap<>();

        for(int i = 0; i< populationSize; i++){
            populationPool.put(i,randomGenerationAPopulation());
        }
        System.out.println("Generate the first generation successfully.");

        int getLocation = random.nextInt(populationSize +1);
        edgesList = populationPool.get(getLocation);

        System.out.println("Get the population of " + getLocation);
        System.out.println("Total distance = " + getTotalDistance(edgesList));
    }

    private boolean existsInArray(Integer[] arr, Integer i){

        boolean isNull = (i==null);

        for(Integer m : arr){
            if(isNull){
                if (m==null) return true;
            }else{
                if(m==null) continue;
                if(m.equals(i)) return true;
            }
        }

        return false;

    }
    
    private void crossover(HashMap<Integer, Integer[]> populationPool, double rate){  //crossover and elitism

        PriorityQueue<Integer[]> elitismQueue = new PriorityQueue<>(numOfCities*2,new ElitismComparator());

        for(int i=0;i<populationPool.size()/2;i++){
            if(Math.random() <= rate){

                Random maskGenerator = new Random();

                Integer population1 = random.nextInt(populationPool.size());
                Integer population2 = random.nextInt(populationPool.size());

                while(population1.equals(population2)) population2 = random.nextInt(populationPool.size());

                Integer[] parent1 = populationPool.get(population1);
                Integer[] parent2 = populationPool.get(population2);

                Integer[] child1 = new Integer[numOfCities];
                Integer[] child2 = new Integer[numOfCities];

                for(int c=0;c<numOfCities;c++){   //copy to child if mask is true
                    if(maskGenerator.nextBoolean()){  //if mask is true, copy from parent
                        child1[c] = parent1[c];
                        child2[c] = parent2[c];
                    }
                }


                int iterator1 = 0;
                int iterator2 = 0;
                for(int c=0;c<numOfCities;c++){   //copy from other parent if not exists
                    if(child1[c]==null){   //if child1[c] is null then child2[c] must be null

                        while(existsInArray(child1,parent2[iterator2])){  //if exists in child1, move to next
                            if(iterator2 < numOfCities) iterator2++;
                            else iterator2 = 0;
                        }

                        while(existsInArray(child2,parent1[iterator1])){  //if exists in child2, move to next
                            if(iterator1 < numOfCities) iterator1++;
                            else iterator1 = 0;
                        }

                        child1[c] = parent2[iterator2];
                        child2[c] = parent1[iterator1];

                        iterator1++;
                        iterator2++;

                    }
                }

                elitismQueue.offer(child1);
                elitismQueue.offer(child2);
            }
        }
        for(int i=0;i<numOfCities;i++)  //put all parents to elitismQueue
            elitismQueue.offer(populationPool.get(i));

        populationPool = new HashMap<>();

        for(int i=0;i<numOfCities;i++){   //use the best half of the elitismQueue to create the new populationPool
            populationPool.put(i,elitismQueue.poll());
        }

    }
    
    private boolean mutation(Integer[] population){
        if(population == null) return false;

        Integer location1 = random.nextInt(population.length);
        Integer location2 = random.nextInt(population.length);

        while(location1.equals(location2)) location2 = random.nextInt(population.length);

        //swap 2 cities
        population[location1] += population[location2];
        population[location2] = population[location1] - population[location2];
        population[location1] = population[location1] - population[location2];

        return true;
    }
    
    public static Integer getTotalDistance(Integer[] population){
        Integer distance = 0;

        for(int i=0;i<population.length;i++){
            int city1 = population[i];
            int city2;
            if(i==population.length-1) city2 = 1;
            else city2 = population[i+1];

            distance += distanceTable[city1-1][city2-1];

        }

        return distance;
    }


    public TSP(){   // Main constructor
        readFile();
        if(debugOutput) outputCities();
        calculate();
        visualization();

//        Integer[] test = new Integer[10];
//
//        test[0] = 200;
//        test[3] = 5;
//        test[4] = 127;
//        test[8] = 0;
//
//        if(existsInArray(test,500)) System.out.println("Yes");
//        else System.out.println("no");


    }

    public static String integerArrayToString(Integer[] array){
        if(array==null) return "";
        String result = "[ ";
        for(int i=0;i<array.length;i++){
            if(array[i] != null) result += array[i].toString();
            else result += "null";
            if(i != array.length-1) result += ", ";
            else result += " ]";
        }
        return result;
    }

    public static int calculateDistance(int x1, int y1, int x2, int y2){
        if(x1 == x2){
            if(y1 == y2)
                return 0;
            else
//                return Math.abs(y1 - y2) * Math.abs(y1 - y2);
            return Math.abs(y1-y2);
        }else{
            if(y1 == y2)
//                return Math.abs(x1 - x2) * Math.abs(x1 - x2);
            return Math.abs(x1-x2);
            else
                return (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        }
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

    private void calculateDistanceTable(){
        distanceTable = new Integer[numOfCities][numOfCities];

        for(int i=0;i<numOfCities;i++){
            for(int j=0;j<i;j++){
                if(i==j) distanceTable[i][j]=0;
                else{
                    City tmp1 = citiesList[i];
                    City tmp2 = citiesList[j];
                    int distance =  calculateDistance(tmp1.x,tmp1.y,tmp2.x,tmp2.y);

                    distanceTable[i][j] = distance;
                    distanceTable[j][i] = distance;

                }

            }
        }

    }

    public static void main(String args[]){new TSP();}
}

