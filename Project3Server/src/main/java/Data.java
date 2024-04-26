import java.io.Serializable;
import java.util.ArrayList;

// Data members are public, so I don't need to write numerous getters/setters lol.
public class Data implements Serializable {
    String catOrLet; // Type of input for server

    // Overall game mechanics
    public boolean gameWon = false;
    public boolean gameLost =false;
    public int failedAttempts=0;
    public int wordsCleared=0;

    // Current word game mechanics
    public int wordLength;
    public char userGuess; // When client changes userGuess error check to make sure it's a char
    public String userCategory; // Can be "NFL" ,"MLB", or "NBA"
    public boolean curWordFail = false;
    public boolean curWordSuccess = false;

    public int numGuesses=6;  // The 6 lives the user gets

    public ArrayList<Character> userGuesses = new ArrayList<>(); // Words guessed by the user. Needed to see if they won and to avoid repeats.

    public ArrayList<Integer> letterPos = new ArrayList<>(); // Position(s) of user guess in the current word

    public String whatHappened; // For client GUI so I can print out what happened
}
