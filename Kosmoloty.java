import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.lang.Math;

class Point2D{
    private Integer X;
    private Integer Y;

    public Point2D(Integer x, Integer y) {
        X = x;
        Y = y;
    }
    public Point2D() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point2D points2D = (Point2D) o;
        return Objects.equals(X, points2D.X) && Objects.equals(Y, points2D.Y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y);
    }

    public Integer getX() {
        return X;
    }

    public void setX(Integer x) {
        X = x;
    }

    public Integer getY() {
        return Y;
    }

    public void setY(Integer y) {
        Y = y;
    }
}

public class Kosmoloty {

    static Integer torusX;
    static Integer torusY;
    static Map<String, List<Integer>> listaKosmolotow = new HashMap<>();
    static ArrayList<List<Integer>> startingPositions = new ArrayList<List<Integer>>();
    static Map<String, Integer> survivors = new HashMap<>();

    private static void checkInputParameters(String[] parameters){
        if(parameters.length != 2){
            System.out.println("klops");
            System.exit(0);
        }

        for(int i = 0 ; i < parameters.length; i++){
            try{
                Integer.parseInt(parameters[i]);
            }catch(NumberFormatException e){
                System.out.println("klops");
                System.exit(0);
            }
        }

        //dla przypadku gdy rozmiar torusa to np. 01000 zamiast 1000
        if(parameters[0].charAt(0) == '0' || parameters[1].charAt(0) == '0'){
            System.out.println("klops");
            System.exit(0);
        }

        torusX = Integer.parseInt(parameters[0]);
        torusY = Integer.parseInt(parameters[1]);

        if( torusX < 1 || torusX > 100000 || torusY < 1 ||  torusY > 100000){
            System.out.println("klops");
            System.exit(0);
        }
    }

    private static void validateInputData(List<String> Kosmolocik){

        if(Kosmolocik.size() != 5) {
            System.out.println("klops");
            System.exit(0);
        }

        validateName(Kosmolocik.get(0));
        validateVelocity(Kosmolocik.get(1), Kosmolocik.get(2));
        validateStartingPosition(Kosmolocik.get(3), Kosmolocik.get(4));

    }

    private static void validateName(String name){
        if(name.length() > 10 ) {
            System.out.println("klops");
            System.exit(0);
        }
        try {
            boolean foundMatch = name.matches("^[\\w.-]+$");
            if(foundMatch==false){
                System.out.println("klops");
                System.exit(0);
            }
        } catch (PatternSyntaxException ex) {
            System.out.println("klops");
            System.exit(0);
        }
    }

    private static void validateVelocity(String Vx, String Vy){

//       Dla wariantu gdy pierwszy znak to minus
        if(Vx.charAt(0)=='-'){
            if(Vx.charAt(1)=='0'){
                System.out.println("klops");
                System.exit(0);
            }
        }
        if(Vy.charAt(0)=='-'){
            if(Vy.charAt(1)=='0'){
                System.out.println("klops");
                System.exit(0);
            }
        }

        //pierwszy znak może być minusem, nie musi, a ciąg znaków musi zawierać tylko cyfry [0-9]
        String regexInteger = "^\\-?+\\d+";
        boolean isVxInt = Vx.matches(regexInteger);
        boolean isVyInt = Vy.matches(regexInteger);

        if(isVxInt == false || isVyInt == false ) {
            System.out.println("klops");
            System.exit(0);
        }else{
            if ( Integer.parseInt(Vx) < -10000 || Integer.parseInt(Vx) > 10000){
                System.out.println("klops");
                System.exit(0);
            }
            if ( Integer.parseInt(Vy) < -10000 || Integer.parseInt(Vy) > 10000){
                System.out.println("klops");
                System.exit(0);
            }
        }
    }

    private static void validateStartingPosition(String x, String y){

        String regexInteger = "\\d+";

//      Dla przypadku gdy prędkoscX lub Y w pliku ma zero na początku -> Vx = 02
        if (x.length()>1){
            if (x.charAt(0) == '0' ){
                System.out.println("klops");
                System.exit(0);
            }
        }
        if (y.length()>1){
            if (y.charAt(0) == '0' ){
                System.out.println("klops");
                System.exit(0);
            }
        }

        boolean isXInt = x.matches(regexInteger);
        boolean isYInt = y.matches(regexInteger);

        if(isXInt == false || isYInt == false ) {
            System.out.println("klops");
            System.exit(0);
        }else {
            if (Integer.parseInt(x) < 0 || Integer.parseInt(x) > torusX - 1) {
                System.out.println("klops");
                System.exit(0);
            }
            if (Integer.parseInt(y) < 0 || Integer.parseInt(y) > torusY - 1) {
                System.out.println("klops");
                System.exit(0);
            }
        }

    }

    private static Point2D calculateNewPosition(Integer Vx, Integer Vy, Integer X, Integer Y){

        if(Vx > 0){
            X = ( X+Vx ) % torusX;
        }
        if(Vy > 0){
            Y = ( Y+Vy ) % torusY;
        }
        if(Vx < 0){
            X =  ( torusX - (-Vx%torusX ) + X ) % torusX;
        }
        if(Vy < 0){
            Y = ( torusY - (-Vy%torusY ) + Y ) % torusY;
        }

        return new Point2D(X, Y);
    }

    private static String checkWinner(Map<String, List<Integer>> listaKosmolotow){

        String winner = new String();
        Integer velocity = 0;
        Integer Vx = 0;
        Integer Vy = 0;

        if(listaKosmolotow.size()==0){
            winner = "remis";
        }else{
            for (Map.Entry<String, List<Integer>> set :
                    listaKosmolotow.entrySet()) {
                    Vx = Math.abs(set.getValue().get(0));
                    Vy = Math.abs(set.getValue().get(1));
                    survivors.put(set.getKey(), Vx+Vy);
            }

            LinkedHashMap<String, Integer> survivorsDescendingSpeed = new LinkedHashMap<>();
            survivors.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> survivorsDescendingSpeed.put(x.getKey(), x.getValue()));

            Integer count = 1;
            Integer firstJetSpeed = 0;
            Integer secondJetSpeed = 0;
            String firstJetName = new String();

            for (Map.Entry<String, Integer> it :
                    survivorsDescendingSpeed.entrySet()) {

                if (count == 1 && survivorsDescendingSpeed.size()==1) {
                    winner = it.getKey();
                    break;
                }
                if (count == 1){
                    firstJetSpeed = it.getValue();
                    firstJetName = it.getKey();
                }
                if (count == 2){
                    secondJetSpeed = it.getValue();
                    break;
                }
                count++;
            }

            if(firstJetSpeed == secondJetSpeed){
                winner = "remis";
            }else{
                winner = firstJetName;
            }

        }

        return winner;
    }

    private static void startingList(){

        Scanner sc = new Scanner(System.in);
        if(!sc.hasNextLine()){
            System.out.println("klops");
            System.exit(0);
        }
        while(sc.hasNextLine()){
            String input = sc.nextLine();
            List<String> kosmolot = Arrays.asList(input.split(","));
            validateInputData(kosmolot);

            List<Integer> cords = new ArrayList<>();

            //Nie mogą istnieć dwa takie same statki
            if(listaKosmolotow.containsKey(kosmolot.get(0))){
                System.out.println("klops");
                System.exit(0);
            }

            cords.add(Integer.parseInt(kosmolot.get(1)));
            cords.add(Integer.parseInt(kosmolot.get(2)));
            cords.add(Integer.parseInt(kosmolot.get(3)));
            cords.add(Integer.parseInt(kosmolot.get(4)));

            //Upewnienie się, że żadnen kosmolot nie startuje z tej samej pozycji XY
            if(startingPositions.contains(cords)){
                System.out.println("klops");
                System.exit(0);
            }
            startingPositions.add(cords);

            //lista kosmolotów gotowych do startu [nazwa, [Vx, Vy, X, Y]]
            listaKosmolotow.put(kosmolot.get(0), cords);
        }
    }

    private static void startRace(){

        //kolejne tury wyścigu
        for (int i = 0; i < 86400; i++) {

            HashMap<Point2D, List<String>> moves = new HashMap<Point2D, List<String>>();
            List<List<Integer>> collisions = new ArrayList<>();

            for (Map.Entry<String, List<Integer>> set :
                    listaKosmolotow.entrySet()) {

                List<Integer> values = new ArrayList<>();
                String name = set.getKey();
                values = set.getValue();

                Integer Vx = values.get(0);
                Integer Vy = values.get(1);
                Integer X = values.get(2);
                Integer Y = values.get(3);

                Point2D newPosition = new Point2D();
                newPosition = calculateNewPosition(Vx, Vy, X, Y);

                List<Integer> newPositions = new ArrayList<>();

                newPositions.add(values.get(0));
                newPositions.add(values.get(1));
                newPositions.add(newPosition.getX());
                newPositions.add(newPosition.getY());
                listaKosmolotow.put(name, newPositions);

                List<Integer> positions = new ArrayList<>();

                if (moves.containsKey(newPosition)){

                    moves.get(newPosition).add(name);
                    positions.add(newPosition.getX());
                    positions.add(newPosition.getY());
                    collisions.add(positions);
                }else{
                    List<String> listOfNames = new ArrayList<>();
                    listOfNames.add(name);
                    moves.put(newPosition,listOfNames);
                }

            }

            if(collisions.size()>0){
                Integer x = collisions.get(0).get(0);
                Integer y = collisions.get(0).get(1);
                List<String> kosmolotyToDelete = new ArrayList<>();
                kosmolotyToDelete = moves.get(new Point2D(x, y));
                for (String name: kosmolotyToDelete) {
                    listaKosmolotow.remove(name);
                }
            }

            if(listaKosmolotow.size()==0){
                System.out.println("remis");
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {

        checkInputParameters(args);
        startingList();
        startRace();
        String winner = checkWinner(listaKosmolotow);
        System.out.println(winner);
        System.exit(0);

    }
}
