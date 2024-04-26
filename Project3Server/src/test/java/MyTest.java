import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

class MyTest {

	// Testing clientHandler methods
	@Test
	public void isLetterInWordTest() {
		ClientHandler handler = new ClientHandler();
		handler.currentWord = "apple";
		char userGuess = 'a';
		ArrayList<Integer> foundPositions = handler.isLetterInWord(userGuess);
		assert foundPositions.get(0).equals(0);
	}
	@Test
	public void isLetterInWordTest2(){
		ClientHandler handler = new ClientHandler();
		handler.currentWord = "apple";
		char userGuess = 'x';
		ArrayList<Integer> foundPositions = handler.isLetterInWord(userGuess);
		assert foundPositions.isEmpty();
	}
	@Test
	public void isLetterInWordTest3(){
		ClientHandler handler = new ClientHandler();
		handler.currentWord = "apple";
		char userGuess = ' ';
		ArrayList<Integer> foundPositions = handler.isLetterInWord(userGuess);
		assert foundPositions.isEmpty();
	}
	@Test
	public void isWordGuessedTest(){
		ClientHandler handler = new ClientHandler();
		Data data = handler.getData();
		handler.currentWord = "apple";
		data.userGuesses.add('a');
		data.userGuesses.add('p');
		data.userGuesses.add('l');
		data.userGuesses.add('e');
		handler.setData(data);
		assert handler.isWordGuessed();
	}

	@Test
	public void isWordGuessedTest2(){
		ClientHandler handler = new ClientHandler();
		Data data = handler.getData();
		handler.currentWord = "apple";
		data.userGuesses.add('a');
		handler.setData(data);
		assert !handler.isWordGuessed();
	}

	@Test
	public void isWordGuessedTest3(){
		ClientHandler handler = new ClientHandler();
		Data data = handler.getData();
		handler.currentWord = "apple";
		data.userGuesses.add(' ');
		handler.setData(data);
		assert !handler.isWordGuessed();
	}

	@Test
	public void getRandomWordTest(){
		ClientHandler handler = new ClientHandler();
		String random = handler.getRandomWord("NFL");
		assert handler.nflTeams.contains(random);
	}
	@Test
	public void getRandomWordTest2(){
		ClientHandler handler = new ClientHandler();
		String random = handler.getRandomWord("MLB");
		assert handler.mlbTeams.contains(random);
	}
	@Test
	public void getRandomWordTest3(){
		ClientHandler handler = new ClientHandler();
		String random = handler.getRandomWord("NBA");
		assert handler.nbaTeams.contains(random);
	}
	@Test
	public void getRandomWordTest4(){
		// Make sure you do not get duplicate words
		ClientHandler handler = new ClientHandler();
		String random = handler.getRandomWord("NBA");
		String random2 = handler.getRandomWord("NBA");
		assert !random2.equals(random);
		assert handler.nbaTeams.contains(random);
		assert handler.nbaTeams.contains(random2);
	}
}
