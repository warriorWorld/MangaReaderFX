package mangalist;

import java.net.URL;
import java.util.ResourceBundle;

import base.BaseController;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemMangaController extends BaseController implements Initializable {
    public ImageView mangaIv;
    public Label mangaNameLb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        mangaIv.setPreserveRatio(true);
    }

    public void setMangaThumbil(String img) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Image image = new Image(img);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mangaIv.setImage(image);
                    }
                });
            }
        }).start();
    }

    public void setMangaName(String text) {
        mangaNameLb.setText(text);
    }
}
