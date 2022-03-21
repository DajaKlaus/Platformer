package com.example.platformer;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HelloApplication extends Application {
    HashMap<KeyCode, Boolean> keys = new HashMap<>();

    ArrayList<Node> platforms = new ArrayList<>();
    ArrayList<Node> coins = new ArrayList<>();
    ArrayList<Node> rods = new ArrayList<>();

    private Label win = new Label("Congratulation!");
    private Label coinsCollected = new Label("0");
    private Label cCollected = new Label("Coins Collected:");
    private int coinsC = 0;
    private boolean contWin = false;

    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();

    private Node player;
    private Point2D playerVelocity = new Point2D(0, 0);
    private boolean canJump = true;

    private int levelWidth, levelHight;

    private boolean running = true, death = false, cont = false;

    private void initContent() {
        Rectangle bg = new Rectangle(1280, 720);

        coinsCollected.setTextFill(Color.WHITE);
        coinsCollected.setFont(new Font("Roboto", 30));
        coinsCollected.setLayoutX(220);
        coinsCollected.setLayoutY(3);

        cCollected.setTextFill(Color.WHITE);
        cCollected.setFont(new Font("Roboto", 30));
        cCollected.setLayoutX(0);
        cCollected.setLayoutY(3);

        levelWidth = LevelData.LEVEL1[0].length() * 60;
        levelHight = LevelData.LEVEL1.length * 60;

        for (int i = 0; i < LevelData.LEVEL1.length; i++) {
            String line = LevelData.LEVEL1[i];
            for (int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {
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

        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();

            if(offset > 640 && offset < levelWidth - 640) {
                gameRoot.setLayoutX(-(offset - 640));
            }
        });
        appRoot.getChildren().addAll(bg, cCollected, coinsCollected, gameRoot, uiRoot);
    }

    private void update() {
        if (isPressed(KeyCode.W) && player.getTranslateY() >= 5) {
            jumpPlayer();
        }
        if (isPressed(KeyCode.SPACE) && player.getTranslateY() >= 5) {
            jumpPlayer();
        }
        if (isPressed(KeyCode.UP) && player.getTranslateY() >= 5) {
            jumpPlayer();
        }
        if (isPressed(KeyCode.A) && player.getTranslateX() >= 5) {
            movePlayerX(-6);
        }
        if (isPressed(KeyCode.LEFT) && player.getTranslateX() >= 5) {
            movePlayerX(-6);
        }
        if (isPressed(KeyCode.D) && player.getTranslateX() + 40 <= levelWidth - 5) {
            movePlayerX(6);
        }
        if (isPressed(KeyCode.RIGHT) && player.getTranslateX() + 40 <= levelWidth - 5) {
            movePlayerX(6);
        }
        if (playerVelocity.getY() < 10) {
            playerVelocity = playerVelocity.add(0, 1);
        }

        movePlayerY((int)playerVelocity.getY());

        for (Node coin : coins) {
            if (player.getBoundsInParent().intersects(coin.getBoundsInParent())) {
                coin.getProperties().put("alive", false);
                coinsC++;
                coinsCollected.setText(String.valueOf(coinsC));
            }
        }

        for (Iterator<Node> it = coins.iterator(); it.hasNext();) {
            Node coin = it.next();
            if (!(Boolean)coin.getProperties().get("alive")) {
                it.remove();
                gameRoot.getChildren().remove(coin);
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
        boolean movingRight = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX()) {
                            return;
                        }
                    } else {
                        if (player.getTranslateX() == platform.getTranslateX() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1));
        }
    }

    private void movePlayerY(int value) {
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingDown) {
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
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
        entity.getProperties().put("alive", true);

        gameRoot.getChildren().add(entity);
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

        Scene scene = new Scene(appRoot);
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
        primaryStage.setTitle("Platformer");
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    update();
                }
                if (death) {
                    GameDialog gameDialog = new GameDialog();
                    gameDialog.open();
                    death = false;
                }
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}