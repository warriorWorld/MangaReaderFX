package main;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import base.BaseController;
import bean.MangaBean;
import bean.MangaListBean;
import configure.ShareKeys;
import dialog.AlertDialog;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import listener.JsoupCallBack;
import listener.OnItemClickListener;
import mangadetail.OnlineMangaDetailController;
import mangalist.ItemMangaController;
import read.ReadController;
import spider.SpiderBase;
import utils.ShareObjUtil;

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
    private Parent optionsRoot,mangaDetailRoot;
    private ScrollPane onlineScrollPane;
    private GridPane onlineGrid;
    public Button previousBtn;
    public Button nextBtn;
    public TextField pageTf;
    private ArrayList<MangaBean> currentMangaList = new ArrayList<>();
    private SpiderBase spider;
    private int currentPage = 1;
    private int stackPaneWidth = 0;
    private  OnlineMangaDetailController onlineMangaDetailcontroller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/options.fxml"));
            optionsRoot = fxmlLoader.load();
            initOnlinePaneUI();
            FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("/fxml/manga_detail.fxml"));
             mangaDetailRoot = fxmlLoader1.load();
            //如果使用 Parent root = FXMLLoader.load(...) 静态读取方法，无法获取到Controller的实例对象
            onlineMangaDetailcontroller = fxmlLoader1.getController(); //获取Controller的实例对象
            onlineMangaDetailcontroller.setStage(stage);
            onlineMangaDetailcontroller.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initUI();
        initSpider("KaKaLot");
    }

    private void initSpider(String spiderName) {
        try {
            spider = (SpiderBase) Class.forName("spider." + spiderName + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
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
                        toggleContent(menuLv.getSelectionModel().getSelectedIndex());
                    }
                }
            });
            nextBtn.setOnAction(event -> {
                currentPage++;
                pageTf.setText(currentPage + "");
                doGetData(currentPage);
            });
            previousBtn.setOnAction(event -> {
                if (currentPage > 1) {
                    currentPage--;
                    pageTf.setText(currentPage + "");
                    doGetData(currentPage);
                }
            });
            pageTf.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode().toString().equals("ENTER")) {
                        int page = Integer.valueOf(pageTf.getText());
                        if (page > 0) {
                            currentPage = page;
                            doGetData(currentPage);
                        }
                    }
                }
            });
            backBtn.setOnAction(event -> {
                toggleContent(0);
            });
            toggleContent(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleContent(int position) {
        previousBtn.setVisible(false);
        pageTf.setVisible(false);
        nextBtn.setVisible(false);
        switch (position) {
            case 0:
                previousBtn.setVisible(true);
                pageTf.setVisible(true);
                nextBtn.setVisible(true);
                mStackPane.getChildren().clear();
                mStackPane.getChildren().add(onlineScrollPane);
                break;
            case 6:
                openReadManga();
                break;
            case 8:
                mStackPane.getChildren().clear();
                mStackPane.getChildren().add(optionsRoot);
                break;
        }
    }

    @Override
    public void setScene(Scene scene) {
        super.setScene(scene);
        if (null != ShareObjUtil.getObject(ShareKeys.MAIN_PAGE_CHCHE)) {
            try {
                currentMangaList = (ArrayList<MangaBean>) ShareObjUtil.getObject(ShareKeys.MAIN_PAGE_CHCHE);
                initOnlineList();
            } catch (Exception e) {
                doGetData(1);
            }
        } else {
            doGetData(1);
        }
    }

    private void doGetData(int page) {
        spider.getMangaList("all",
                page + "", new JsoupCallBack<MangaListBean>() {
                    @Override
                    public void loadSucceed(final MangaListBean result) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                currentMangaList = result.getMangaList();
                                ShareObjUtil.saveObject(currentMangaList, ShareKeys.MAIN_PAGE_CHCHE);
                                initOnlineList();
                            }
                        });
                    }

                    @Override
                    public void loadFailed(String error) {
                        AlertDialog.display("错误", error, "确定");
                    }
                });
    }

    private void initOnlinePaneUI() {
        onlineScrollPane = new ScrollPane();
        onlineGrid = new GridPane();
        onlineGrid.setPadding(new Insets(10, 10, 10, 10));
        onlineGrid.setVgap(8);
        onlineGrid.setHgap(10);
        onlineScrollPane.setContent(onlineGrid);
    }

    private void initOnlineList() {
        try {
            onlineGrid.getChildren().clear();
            stackPaneWidth = (int) (scene.getWidth() - menuLv.getWidth());
            int column = (int) (stackPaneWidth / 200) - 1;
            for (int i = 0; i < currentMangaList.size(); i++) {
                FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("/fxml/item_manga_list.fxml"));
                Parent item = fxmlLoader1.load();
                ItemMangaController itemController = fxmlLoader1.getController();
                MangaBean mangaItem = currentMangaList.get(i);
                itemController.setMangaThumbil(mangaItem.getWebThumbnailUrl());
                itemController.setMangaName(mangaItem.getName());
                itemController.setOnClickListener(i, new OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        mStackPane.getChildren().clear();
                        mStackPane.getChildren().add(mangaDetailRoot);
                        onlineMangaDetailcontroller.setStackPaneWidth(stackPaneWidth);
                        onlineMangaDetailcontroller.setUrl(currentMangaList.get(position).getUrl(),spider);
                    }
                });
                onlineGrid.add(item, (i % column), (int) (i / column));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openReadManga() {
        try {
            Stage window = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/read.fxml"));
            Parent root = fxmlLoader.load();
            //如果使用 Parent root = FXMLLoader.load(...) 静态读取方法，无法获取到Controller的实例对象
            ReadController controller = fxmlLoader.getController(); //获取Controller的实例对象
            Scene scene = new Scene(root, 800, 500);

            window.setTitle("英文漫画阅读器");
            window.setMaximized(true);
            window.setScene(scene);
            window.show();
            controller.setStage(window);
            controller.setScene(scene);
            controller.setPath("E:\\Manga\\testmanga");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
