import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import static java.util.Collections.shuffle;


public class ClientHandler implements Runnable {

    // Things needed to interact with the server
    private Socket socket;
    private Data data = new Data();
    ObjectInputStream in;
    ObjectOutputStream out;
    private Consumer<Serializable> callback;

    // Game variables
    ArrayList<String> nflTeams = createNflTeams();
    ArrayList<String> nbaTeams = createNbaTeams();
    ArrayList<String> mlbTeams = createMlbTeams();

    // Sets are needed to keep track of words the user already has attempted to avoid repeats
    Set<String> nflUsedTeams = new HashSet<>();
    Set<String> nbaUsedTeams = new HashSet<>();
    Set<String> mlbUsedTeams = new HashSet<>();
    String currentWord;

    // In and out needs better error checking!
    public void writeOutObject(Data data){
        // This method is blocking because output steam is blocking
        try {
            out.writeObject(data);
        }catch (Exception e){
            try {
                out.close();
            } catch (IOException f){
                System.out.println("Stream already closed");
            }
        }
    }

    public void readInObject() {
        // This method is blocking because input steam is blocking
        try{
            data = (Data) in.readObject();
        }
        catch(EOFException f){
            System.out.println("End of object stream reached");
        }
        catch (Exception e){
            // fix later!
            try {
                in.close();
            } catch (IOException f){
                System.out.println("Stream already closed");
            }
        }
    }

    // Empty constructor used for testing ClientHandler offline
    public ClientHandler(){}

    public Data getData(){
        return this.data;
    }

    public void setData(Data data){
        this.data = data;
    }

    public ClientHandler(Socket connection,Consumer<Serializable> call){
        // What if socket is null? Need to error check here, probably.
        this.socket = connection;
        try{
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());
            connection.setTcpNoDelay(true);
            callback=call;
        } catch (IOException e){
            // NEEDS ERROR CHECKING HERE
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                readInObject();
                switch (data.catOrLet){
                    case "Cat":
                        String category = data.userCategory;
                        currentWord = getRandomWord(category);
                        System.out.println(currentWord);
                        data.wordLength = currentWord.length();
                        break;
                    case "Let":
                        char userLetter = data.userGuess;
                        data.userGuesses.add(userLetter); // I do not think this is necessary
                        data.letterPos.clear();
                        data.letterPos.addAll(isLetterInWord(userLetter));
                        if(data.letterPos.isEmpty()){
                            data.whatHappened = "The letter " + userLetter + " is not in the word";
                            data.whatHappened += "\nLives left: " + data.numGuesses;
                        }
                        else{
                            data.whatHappened = "The letter " + userLetter + " is in the word!";
                        }
                        // Check if the cleared or failed the word
                        if(isWordGuessed()){
                            data.curWordSuccess = true;
                            data.whatHappened = "You got the word correct!";
                            data.wordsCleared++;
                        }
                        if(data.numGuesses==0){
                            data.curWordFail = true;
                            data.whatHappened = "You failed this word :(\nPlease exit and pick a new word";
                            data.failedAttempts++;
                        }
                        // Check if the overall game is won or lose
                        if(data.failedAttempts==3){
                            data.gameLost=true;
                            data.whatHappened="You lost the game! :(";
                            // Reset data here!
                        }
                        if(data.wordsCleared==3){
                            data.gameWon=true;
                            data.whatHappened = "YOU WON THE GAME!!!!";
                            // Reset data here!
                        }
                        break;
                    default: // Default would be if the variable is null
                        break;
                } // Closes the switch
                    writeOutObject(data);
            }
            catch (Exception e) {
                callback.accept("OOOOPPs...Something wrong with the socket from client: " + "....closing down!");
                closeAll();
                break;
            }
        }
        closeAll();

    }
    public boolean isWordGuessed() {
        for (int i = 0; i < currentWord.length(); i++) {
            if (!data.userGuesses.contains(currentWord.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public ArrayList<Integer> isLetterInWord(char userGuess){
            // Find position(s) of user letter in the word
            ArrayList<Integer> foundPositions = new ArrayList<>();
            for(int i =0; i<currentWord.length(); i++){
                char currentChar = currentWord.charAt(i);
                if((currentWord.charAt(i)) == userGuess){
                    foundPositions.add(i);
                }
            }
            // If the letter is not in the word...
            if(foundPositions.isEmpty()) data.numGuesses--;
//            data.userGuesses.add(userGuess);
            return foundPositions;
        }


        public String getRandomWord(String userChoice) {
            ArrayList<String> userCategory = new ArrayList<>();
            switch (userChoice) {
                case "NFL":
                    userCategory = nflTeams;
                    break;
                case "NBA":
                    userCategory = nbaTeams;
                    break;
                case "MLB":
                    userCategory = mlbTeams;
                    break;
            }
            // Get random position and shuffle the category
            shuffle(userCategory);
            Random random = new Random();
            int randomPos = random.nextInt(userCategory.size());

            if(userCategory == nflTeams) {
                if (!nflUsedTeams.contains(userCategory.get(randomPos))) {
                    nflUsedTeams.add(userCategory.get(randomPos));
                    return userCategory.get(randomPos);
                }
            }
            else if(userCategory == nbaTeams) {
                if (!nbaUsedTeams.contains(userCategory.get(randomPos))) {
                    nbaUsedTeams.add(userCategory.get(randomPos));
                    return userCategory.get(randomPos);
                }
            }
            else if(userCategory == mlbTeams) {
                if (!mlbUsedTeams.contains(userCategory.get(randomPos))) {
                    mlbUsedTeams.add(userCategory.get(randomPos));
                    return userCategory.get(randomPos);
                }
            }
            else {
                return getRandomWord(userChoice);
            }
            return null;
        }

        public ArrayList<String> createNflTeams(){
        ArrayList<String> nflTeams = new ArrayList<>();
        new ArrayList<String>(30);
        nflTeams.add("cardinals");
        nflTeams.add("falcons");
        nflTeams.add("ravens");
        nflTeams.add("bills");
        nflTeams.add("panthers");
        nflTeams.add("bears");
        nflTeams.add("bengals");
        nflTeams.add("browns");
        nflTeams.add("cowboys");
        nflTeams.add("broncos");
        nflTeams.add("lions");
        nflTeams.add("packers");
        nflTeams.add("texans");
        nflTeams.add("colts");
        nflTeams.add("jaguars");
        nflTeams.add("chiefs");
        nflTeams.add("chargers");
        nflTeams.add("rams");
        nflTeams.add("dolphins");
        nflTeams.add("vikings");
        nflTeams.add("patriots");
        nflTeams.add("saints");
        nflTeams.add("giants");
        nflTeams.add("jets");
        nflTeams.add("raiders");
        nflTeams.add("eagles");
        nflTeams.add("steelers");
        nflTeams.add("fortyniners");
        nflTeams.add("seahawks");
        nflTeams.add("buccaneers");
        nflTeams.add("titans");
        nflTeams.add("redskins");
        return nflTeams;
    }
        public ArrayList<String> createMlbTeams(){
        ArrayList<String> mlbTeams = new ArrayList<>();
        mlbTeams.add("diamondbacks");
        mlbTeams.add("braves");
        mlbTeams.add("orioles");
        mlbTeams.add("redsox");
        mlbTeams.add("whitesox");
        mlbTeams.add("cubs");
        mlbTeams.add("reds");
        mlbTeams.add("indians");
        mlbTeams.add("rockies");
        mlbTeams.add("tigers");
        mlbTeams.add("astros");
        mlbTeams.add("royals");
        mlbTeams.add("angels");
        mlbTeams.add("dodgers");
        mlbTeams.add("marlins");
        mlbTeams.add("brewers");
        mlbTeams.add("twins");
        mlbTeams.add("mets");
        mlbTeams.add("yankees");
        mlbTeams.add("athletics");
        mlbTeams.add("phillies");
        mlbTeams.add("pirates");
        mlbTeams.add("padres");
        mlbTeams.add("giants");
        mlbTeams.add("mariners");
        mlbTeams.add("cardinals");
        mlbTeams.add("rays");
        mlbTeams.add("rangers");
        mlbTeams.add("bluejays");
        mlbTeams.add("nationals");
        return mlbTeams;
    }
        public ArrayList<String> createNbaTeams(){
        ArrayList<String> nbaTeams = new ArrayList<>();
        nbaTeams.add("hawks");
        nbaTeams.add("celtics");
        nbaTeams.add("nets");
        nbaTeams.add("hornets");
        nbaTeams.add("bulls");
        nbaTeams.add("cavaliers");
        nbaTeams.add("mavericks");
        nbaTeams.add("nuggets");
        nbaTeams.add("pistons");
        nbaTeams.add("warriors");
        nbaTeams.add("rockets");
        nbaTeams.add("pacers");
        nbaTeams.add("clippers");
        nbaTeams.add("lakers");
        nbaTeams.add("grizzlies");
        nbaTeams.add("heat");
        nbaTeams.add("bucks");
        nbaTeams.add("timberwolves");
        nbaTeams.add("pelicans");
        nbaTeams.add("knicks");
        nbaTeams.add("thunder");
        nbaTeams.add("magic");
        nbaTeams.add("sixers");
        nbaTeams.add("suns");
        nbaTeams.add("blazers");
        nbaTeams.add("kings");
        nbaTeams.add("spurs");
        nbaTeams.add("raptors");
        nbaTeams.add("jazz");
        nbaTeams.add("wizards");
        return nbaTeams;
    }


        public void closeAll(){
            try{
                if(in != null) in.close();
                if(out != null) out.close();
                if(!socket.isClosed()) socket.close();

                in = null;
                out = null;
            } catch (Exception e){
                // What errors?
            }
        }
}
