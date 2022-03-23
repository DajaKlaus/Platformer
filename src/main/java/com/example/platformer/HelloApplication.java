package com.example.platformer;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HelloApplication extends Application {
    private HashMap<KeyCode, Boolean> keys = new HashMap<>(); //in base al tasto premuto esso diventa vero

    //suddivisione elementi di gioco
    //Node viene usato per evitare di dover ripescare gli elementi dal Root
    private ArrayList<Node> platforms = new ArrayList<>();
    private ArrayList<Node> coins = new ArrayList<>();
    private ArrayList<Node> rods = new ArrayList<>();
    //

    private Label win = new Label("Congratulation!");
    private Label coinsCollected = new Label("0");
    private Label cCollected = new Label("Coins Collected:");
    private int coinsC = 0;
    private boolean contWin = false;

    private Pane appRoot = new Pane(); //contine tutto (label e oggetti)
    //separazione elementi muovibili tramite tastiera o solo tramite mouse
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();
    //

    private Node player;
    private Point2D playerVelocity = new Point2D(0, 0); //gestisce le velocità del player (x, y)
    private boolean canJump = true;

    private int levelWidth, levelHight;

    private boolean running = true, death = false, cont = false;

    private void initContent() {
        Rectangle bg = new Rectangle(1280, 720); //background

        coinsCollected.setTextFill(Color.WHITE);
        coinsCollected.setFont(new Font("Roboto", 30));
        coinsCollected.setLayoutX(220);
        coinsCollected.setLayoutY(3);

        cCollected.setTextFill(Color.WHITE);
        cCollected.setFont(new Font("Roboto", 30));
        cCollected.setLayoutX(0);
        cCollected.setLayoutY(3);

        levelWidth = LevelData.LEVEL1[0].length() * 60; // lunghezza di una stringa * grandezza di un blocco
        levelHight = LevelData.LEVEL1.length * 60;

        for (int i = 0; i < LevelData.LEVEL1.length; i++) {
            String line = LevelData.LEVEL1[i]; //line è uguale ad una stringa nell'array
            for (int j = 0; j < line.length(); j++) { //controllo su line
                switch (line.charAt(j)) { //in base al carattere che si trova in una posizione della stringa
                    case '0':
                        break;
                    case '1':
                        Node platform = createEntity(j * 60, i * 60, 60, 60, Color.BROWN);
                        platforms.add(platform);
                        break;
                    case '2':
                        Node coin = createEntity(j * 60, i * 60, 60, 60, Color.GOLD);
                        coins.add(coin);
                        break;
                    case '3':
                        Node rod = createEntity(j * 60, i * 60, 10, 60, Color.GREEN);
                        rods.add(rod);
                }
            }
        }
        player = createEntity(0, 600, 40, 40, Color.BLUE);

        //espressione lambda per far muovere la visuale
        player.translateXProperty().addListener((obs, old, newValue) -> { //ogni volta che x cambia
            int offset = newValue.intValue();

            if(offset > 640 && offset < levelWidth - 640) { //esattamente centro schermo
                gameRoot.setLayoutX(-(offset - 640)); //spostamento visuale verso direzione player
            }
        });
        appRoot.getChildren().addAll(bg, cCollected, coinsCollected, gameRoot, uiRoot); //memorizzazione tutti elementi
    }

    private void update() {
        if (isPressed(KeyCode.W) && player.getTranslateY() >= 6) { // controllo tasto premuto + controllo uscita schermo
            jumpPlayer();
        }
        if (isPressed(KeyCode.SPACE) && player.getTranslateY() >= 6) {
            jumpPlayer();
        }
        if (isPressed(KeyCode.UP) && player.getTranslateY() >= 6) {
            jumpPlayer();
        }
        if (isPressed(KeyCode.A) && player.getTranslateX() >= 6) {
            movePlayerX(-6);
        }
        if (isPressed(KeyCode.LEFT) && player.getTranslateX() >= 6) {
            movePlayerX(-6);
        }
        if (isPressed(KeyCode.D) && player.getTranslateX() + 40 <= levelWidth - 6) {
            movePlayerX(6);
        }
        if (isPressed(KeyCode.RIGHT) && player.getTranslateX() + 40 <= levelWidth - 6) {
            movePlayerX(6);
        }
        if (playerVelocity.getY() < 10) { // controllo gravità
            playerVelocity = playerVelocity.add(0, 1); // 1 = accellerazione y
        }

        movePlayerY((int)playerVelocity.getY()); // la y viene controllata dall'applicazione

        for (Node coin : coins) { // per ogni elemento di coins
            if (player.getBoundsInParent().intersects(coin.getBoundsInParent())) {
                coin.getProperties().put("alive", false); // proprietà alive = false
                coinsC++;
                coinsCollected.setText(String.valueOf(coinsC));
            }
        }

        for (Iterator<Node> it = coins.iterator(); it.hasNext();) { // creo un oggetto it che è uguale ad un elemento di coins che continuerà fino a quando ci sarà un elemento successivo in coins
            Node coin = it.next();
            if (!(Boolean)coin.getProperties().get("alive")) { // se alive è false
                it.remove();
                gameRoot.getChildren().remove(coin); // rimuovo un coin dagli oggetti nella mappa
            }
        }

        for (Node rod : rods) {
            if ((player.getTranslateX() > rod.getTranslateX() + 20) && (player.getTranslateY() >= 375)) {
                running = false;
                win.setTextFill(Color.WHITE);
                win.setFont(new Font("Roboto", 50));
                win.setLayoutX(530);
                contWin = true;
            }
        }

        if (contWin) {
            appRoot.getChildren().addAll(win);
        }

        if (!cont) {
            death();
        }
    }

    private void movePlayerX(int value) {
        boolean movingRight = value > 0; // è positivo se un tasto viene premuto

        // il player si muove un unità alla volta per evitare che player e platform si sovrappongono
        for (int i = 0; i < Math.abs(value); i++) { // Math.abs(value) = prende solo il valore assoluto
            for (Node platform : platforms) { // per ogni piattaforma si ottengono dei Bounds
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) { //
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX()) { // controlla se il lato sinistro del player collide con il lato destro di una platform
                            return; // non ci si può muovere verso destra
                        }
                    } else {
                        if (player.getTranslateX() == platform.getTranslateX() + 60) {
                            return; // non ci si può muovere verso sinistra
                        }
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1)); // se si muove a destra allora destra +1 se si muove verso sinistra -1
        }
    }

    private void movePlayerY(int value) {
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingDown) {
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1); // -1 per evitare che il player continui a collidere con la piattaforma
                            canJump = true;
                            return;
                        }
                    } else {
                        if (player.getTranslateY() == platform.getTranslateY() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));
        }
    }

    private void jumpPlayer() {
        if (canJump) {
            playerVelocity = playerVelocity.add(0, -30);
            canJump = false;
        }
    }

    private Node createEntity(int x, int y, int w, int h, Color color) {
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        entity.getProperties().put("alive", true); // proprietà alive = true

        gameRoot.getChildren().add(entity); //getChildren va a prendere un oggetto contenuto nell'arrayList "children"
        return entity;
    }

    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    private boolean death() {
        if (player.getTranslateY() > levelHight) {
            death = true;
            running = false;
            cont = true;
        }

        return death;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initContent();

        Scene scene = new Scene(appRoot); //aggiungo tutto il contenuto alla scena
        //memorizzazione tasto premuto o rilasciato nella tastiera in keys
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true)); //se premuto diventa true
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false)); //se rilasciato diventa false
        //
        primaryStage.setTitle("Platformer");
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) { //loop richiamato 60 volte al sec = 60fps
                if (running) {
                    update();
                }
                if (death) {
                    GameDialog gameDialog = new GameDialog();
                    gameDialog.open();
                    death = false;
                    player.setLayoutX(20);
                    player.setLayoutY(500);
                }
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}