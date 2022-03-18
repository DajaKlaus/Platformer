package com.example.platformer;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameDialog extends Stage {

    private Text gameOver = new Text();

    public GameDialog() {
        VBox vBox = new VBox(10, gameOver);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox);

        setScene(scene);
        initModality(Modality.APPLICATION_MODAL);
    }

    public void open() {
        gameOver.setText("GAME OVER!");
        show();
    }
}
