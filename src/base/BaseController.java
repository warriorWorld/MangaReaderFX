package base;

import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class BaseController {
    protected Stage stage;
    protected Scene scene;
    protected MediaPlayer mediaPlayer;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void createMediaPlayer(String url) {
        mediaPlayer = new MediaPlayer(new Media(url));
    }

    public MediaPlayer getMediaPlayer() {
        return this.mediaPlayer;
    }
}
