package main;

import java.net.URL;
import java.util.ResourceBundle;


import base.BaseController;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainItemController extends BaseController implements Initializable {
    public ImageView iconIv;
    public Label optionLb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setIconIv() {
        iconIv.setPreserveRatio(true);
        iconIv.setImage(new Image("/drawable/local_icon.png"));
    }

    public void setOptionText(String text) {
        optionLb.setText(text);
    }
}
