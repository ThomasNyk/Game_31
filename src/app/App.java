package app;

import resources.app.marker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import gameClasses.Deck;
import gameClasses.DeckGenerator;
import gameClasses.Player;
import gameClasses.GameState;
import gameClasses.TurnManager;

public class App extends Application {

    @Override
    public void start(Stage Stage) throws IOException {

        GameState.stage = Stage;
        GameState.worth.put(11, 10);
        GameState.worth.put(12, 10);
        GameState.worth.put(13, 10);
        GameState.worth.put(1, 11);

        GameState.deck = new Deck();
        GameState.deck.addCards(DeckGenerator.StandardDeck());
        GameState.deck.shuffle();

        GameState.players.add(new Player("Thomas"));
        GameState.players.add(new Player("Tobias"));
        GameState.players.add(new Player("Gamma"));

        GameState.drawCardsForAllPlayers(3);

        TurnManager.setPlayers(GameState.players);
        TurnManager.nextTurn();

    }

    static void setRoot(String fxml) throws IOException {
        GameState.scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(marker.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}