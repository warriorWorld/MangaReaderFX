package mangalist;

import java.net.URL;
import java.util.ResourceBundle;

import base.BaseController;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemMangaController extends BaseController implements Initializable {
    public ImageView mangaIv;
    public Label mangaNameLb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mangaIv.setPreserveRatio(true);
    }
    public void setMangaThumbil(String img) {
        mangaIv.setImage(new Image(img));
    }

    public void setMangaName(String text) {
        mangaNameLb.setText(text);
    }
}
