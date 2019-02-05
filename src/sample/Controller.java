package sample;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class Controller implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize start"+location+"\n"+resources);
    }
}
