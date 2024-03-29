package gameClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gameClasses.UI.EzButton;
import gameClasses.UI.EzPopup;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameState {
    public static Card dealtCard = null;
    public static Stage stage;
    public static Deck deck;
    public static Deck discardDeck = new Deck();
    public static Map<Integer, Integer> worth = new HashMap<Integer, Integer>();
    public static Scene scene;
    public static List<Player> players = new ArrayList<Player>();
    public static Player currentPlayer;

    public static void drawCardsForAllPlayers(int amount) {
        for (int i = 0; i < amount; i++) {
            for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
                players.get(playerIndex).addCardToHand(deck.drawCard());
            }
        }
    }

    public static Void handleTurnDoneBtn(ActionEvent e) {
        TurnManager.nextTurn();
        return null;
    }

    public static Void handleConfirmBtn(ActionEvent event) {
        currentPlayer.hand.swapCards();
        discardDeck.addCard(GameState.dealtCard);
        GameState.dealtCard = null;
        currentPlayer.hand.selectedCard = null;
        Player.turnDone = true;
        renderPlayer(currentPlayer);
        return null;
    }

    public static Void handleDrawBtn(ActionEvent event) {
        // System.out.println(deck);

        if (deck.getIsEmpty()) {
            deck.addCards(discardDeck.cards);
            discardDeck.cards.clear();
            deck.shuffle();

            new EzPopup(stage).show(stage);

            renderPlayer(currentPlayer);
        } else {
            GameState.dealtCard = GameState.deck.drawCard();
            currentPlayer.hand.selectedCard = null;
            renderPlayer(currentPlayer);
        }
        return null;
    }

    public static void renderPlayer(Player player) {
        currentPlayer = player;
        HBox hbox;
        if (Player.turnDone) {

            Button turnDoneButton = new EzButton((e) -> handleTurnDoneBtn(e), "Complete Turn");
            hbox = new HBox(turnDoneButton);
        } else {
            Button drawButton = new EzButton((e) -> handleDrawBtn(e), "Draw");

            Button confirmButton = new EzButton((e) -> handleConfirmBtn(e), "Confirm");

            if (GameState.dealtCard == null) {
                confirmButton.setDisable(true);
            }
            if (GameState.dealtCard != null && (player.hand.selectedCard == null || player.hand.selectedCard == -1)) {
                confirmButton.setText("Skip");
            }

            if (GameState.dealtCard == null) {
                hbox = new HBox(drawButton);
            } else {
                hbox = new HBox(confirmButton);
            }
        }

        hbox.setAlignment(Pos.CENTER);

        Text playerNameText = new Text(player.name);
        Text maxSumText = new Text("Current score: " + Integer.toString(player.hand.sum()));
        HBox hBox2;

        Button endGameButton = new Button();
        endGameButton.setText("End Game");

        endGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GameState.endGame();
            }
        });

        if (GameState.dealtCard == null) {
            Rectangle rectangle = new Rectangle(150, 200);
            hBox2 = new HBox(endGameButton, rectangle, maxSumText);

        } else {
            ImageView image = new ImageView(GameState.dealtCard.getImage());
            image.setFitHeight(200);
            image.setPreserveRatio(true);
            hBox2 = new HBox(endGameButton, image, maxSumText);
        }
        hBox2.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(playerNameText, hBox2, player.hand.render(player), hbox);
        vbox.setAlignment(Pos.CENTER);
        scene = new Scene(vbox, 1250, 500);

        // scene = new Scene(loadFXML("primary"), 640, 480);
        GameState.stage.setScene(scene);
        GameState.stage.show();
    }

    public static void renderBlackout(Player player) {
        Button turnDoneButton = new Button();
        turnDoneButton.setText("I'm " + player.name);

        turnDoneButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TurnManager.nextTurn();
            }
        });

        Text playerNameText = new Text("Look away and let " + player.name + " take over");
        VBox vbox = new VBox(playerNameText, turnDoneButton);
        vbox.setAlignment(Pos.CENTER);
        scene = new Scene(vbox, 1250, 500);
        GameState.stage.setScene(scene);
        GameState.stage.show();
    }

    public static void renderWin(Player player) {
        Text playerNameText = new Text(player.name + " Won!!");
        VBox vbox = new VBox(playerNameText);
        vbox.setAlignment(Pos.CENTER);
        scene = new Scene(vbox, 1250, 500);
        GameState.stage.setScene(scene);
        GameState.stage.show();
    }

    public static void renderWins(List<Player> players) {
        String playerText = "";
        for (Player player : players) {
            playerText += player.name + "\n";
        }
        Text playerNameText = new Text("Winners:\n" + playerText);
        VBox vbox = new VBox(playerNameText);
        vbox.setAlignment(Pos.CENTER);
        scene = new Scene(vbox, 1250, 500);
        GameState.stage.setScene(scene);
        GameState.stage.show();
    }

    public static void endGame() {
        int highest = 0;
        List<Player> winners = new ArrayList<Player>();
        for (Player player : players) {
            int sum = player.hand.sum();
            System.out.println("Sum: " + sum + " : highest: " + highest);
            if (highest == sum) {
                System.out.println("AddSecond");
                winners.add(player);
            } else if (highest < sum) {
                highest = sum;
                winners.clear();
                winners.add(player);
            }
        }
        if (winners.size() > 1) {
            renderWins(winners);
        } else {
            renderWin(winners.get(0));
        }
    }
}
