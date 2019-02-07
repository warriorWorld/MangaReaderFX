package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import base.BaseController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
    private Parent optionsRoot;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/options.fxml"));
            optionsRoot = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initUI();
    }

    private void initUI() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/item_main_options.fxml"));
            Parent item = fxmlLoader.load();
            MainItemController itemController = fxmlLoader.getController();
            itemController.setIconIv("/drawable/online_icon.png");
            itemController.setOptionText("在线漫画");

            FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("/fxml/item_main_options.fxml"));
            Parent item1 = fxmlLoader1.load();
            MainItemController itemController1 = fxmlLoader1.getController();
            itemController1.setIconIv("/drawable/local_icon.png");
            itemController1.setOptionText("本地漫画");

            FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("/fxml/item_main_options.fxml"));
            Parent item2 = fxmlLoader2.load();
            MainItemController itemController2 = fxmlLoader2.getController();
            itemController2.setIconIv("/drawable/recommended.png");
            itemController2.setOptionText("我的收藏");

            FXMLLoader fxmlLoader3 = new FXMLLoader(getClass().getResource("/fxml/item_main_options.fxml"));
            Parent item3 = fxmlLoader3.load();
            MainItemController itemController3 = fxmlLoader3.getController();
            itemController3.setIconIv("/drawable/user_icon.png");
            itemController3.setOptionText("正在追更");

            FXMLLoader fxmlLoader4 = new FXMLLoader(getClass().getResource("/fxml/item_main_options.fxml"));
            Parent item4 = fxmlLoader4.load();
            MainItemController itemController4 = fxmlLoader4.getController();
            itemController4.setIconIv("/drawable/online_icon.png");
            itemController4.setOptionText("我已看完");

            FXMLLoader fxmlLoader5 = new FXMLLoader(getClass().getResource("/fxml/item_main_options.fxml"));
            Parent item5 = fxmlLoader5.load();
            MainItemController itemController5 = fxmlLoader5.getController();
            itemController5.setIconIv("/drawable/online_icon.png");
            itemController5.setOptionText("数据统计");

            FXMLLoader fxmlLoader6 = new FXMLLoader(getClass().getResource("/fxml/item_main_options.fxml"));
            Parent item6 = fxmlLoader6.load();
            MainItemController itemController6 = fxmlLoader6.getController();
            itemController6.setIconIv("/drawable/online_icon.png");
            itemController6.setOptionText("生词本");

            FXMLLoader fxmlLoader7 = new FXMLLoader(getClass().getResource("/fxml/item_main_options.fxml"));
            Parent item7 = fxmlLoader7.load();
            MainItemController itemController7 = fxmlLoader7.getController();
            itemController7.setIconIv("/drawable/online_icon.png");
            itemController7.setOptionText("正在下载");

            FXMLLoader fxmlLoader8 = new FXMLLoader(getClass().getResource("/fxml/item_main_options.fxml"));
            Parent item8 = fxmlLoader8.load();
            MainItemController itemController8 = fxmlLoader8.getController();
            itemController8.setIconIv("/drawable/online_icon.png");
            itemController8.setOptionText("设置");

            menuLv.getItems().addAll(item, item1, item2, item3, item4, item5, item6, item7, item8);
            menuLv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            menuLv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    if (event.getButton().toString().equals("PRIMARY")) {
                        switch (menuLv.getSelectionModel().getSelectedIndex()) {
                            case 8:
                                if (!mStackPane.getChildren().equals(optionsRoot)) {
                                    mStackPane.getChildren().clear();
                                    mStackPane.getChildren().add(optionsRoot);
                                }
                                break;
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
