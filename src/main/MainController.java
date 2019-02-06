package main;

import java.net.URL;
import java.util.ResourceBundle;

import base.BaseController;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class MainController extends BaseController implements Initializable {
    public ListView menuLv;
    public Button backBtn;
    public Button loginBtn;
    public Button registerBtn;
    public Button logoutBtn;
    public TextField userNameTf;
    public PasswordField mPasswordField;
    public StackPane mStackPane;
    public Label userNameLb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initUI();
    }

    private void initUI() {
        menuLv.getItems().addAll("在线漫画", "本地漫画", "我的收藏", "正在追更", "我已看完", "数据统计", "生词本", "正在下载", "设置");
        menuLv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        menuLv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().toString().equals("PRIMARY")){
                    userNameLb.setText(menuLv.getSelectionModel().getSelectedIndex()+"");
                }
            }
        });
    }
}
