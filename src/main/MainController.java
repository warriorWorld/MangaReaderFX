package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import base.BaseController;
import bean.MangaBean;
import bean.MangaListBean;
import configure.Configure;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import listener.JsoupCallBack;
import listener.OnItemClickListener;
import mangadetail.OnlineMangaDetailController;
import mangalist.ItemMangaController;
import read.ReadController;
import sort.FileComparator;
import sort.FileComparatorAllNum;
import sort.FileComparatorDirectory;
import sort.FileComparatorWithBracket;
import spider.FileSpider;
import spider.SpiderBase;
import utils.ReplaceUtil;
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
    public MenuItem directoryChooserMi;
    private Parent optionsRoot, mangaDetailRoot;
    private ScrollPane onlineScrollPane, localScrollPane;
    private GridPane onlineGrid, localGrid;
    public Button previousBtn;
    public Button nextBtn;
    public TextField pageTf;
    private ArrayList<MangaBean> currentMangaList = new ArrayList<>(), localMangaList = new ArrayList<>();
    private SpiderBase spider;
    private int currentPage = 1;
    private int stackPaneWidth = 0;
    private OnlineMangaDetailController onlineMangaDetailcontroller;
    private ArrayList<String> pathList;//本地图片地址
    private int currentScenePos = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //设置
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/options.fxml"));
            optionsRoot = fxmlLoader.load();
            //线上漫画
            initOnlinePaneUI();
            //线上漫画详情
            FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("/fxml/manga_detail.fxml"));
            mangaDetailRoot = fxmlLoader1.load();
            //如果使用 Parent root = FXMLLoader.load(...) 静态读取方法，无法获取到Controller的实例对象
            onlineMangaDetailcontroller = fxmlLoader1.getController(); //获取Controller的实例对象
            //本地漫画
            initLocalPaneUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initUI();
        initSpider("KaKaLot");
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        onlineMangaDetailcontroller.setStage(stage);
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
            directoryChooserMi.setOnAction(event -> {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setInitialDirectory(new File("C:\\Users\\Administrator"));   //设置初始路径，默认为我的电脑
                chooser.setTitle("选择漫画保存文件夹");                //设置窗口标题
                try {
                    Preferences mPreferences = Preferences.userRoot();
                    mPreferences.put(ShareKeys.MANGA_DIRECTORY_KEY, chooser.showDialog(stage).toString().replaceAll("\\\\", "/"));
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
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
                toggleContent(currentScenePos);
                if (currentScenePos==1){
                    doGetLocalManga(Configure.getMangaDirectory());
                }
            });
            toggleContent(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleContent(int position) {
        currentScenePos = position;
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
            case 1:
                mStackPane.getChildren().clear();
                mStackPane.getChildren().add(localScrollPane);
                break;
            case 6:
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
        onlineMangaDetailcontroller.setScene(scene);
        //刷新线上漫画列表
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
        //刷新本地漫画地址
        doGetLocalManga(Configure.getMangaDirectory());
    }

    private void doGetData(int page) {
        stage.setTitle(Configure.NAME + Configure.LOADING);
        spider.getMangaList("all",
                page + "", new JsoupCallBack<MangaListBean>() {
                    @Override
                    public void loadSucceed(final MangaListBean result) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                stage.setTitle(Configure.NAME);
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
                        onlineMangaDetailcontroller.setUrl(currentMangaList.get(position).getUrl(), spider);
                    }
                });
                onlineGrid.add(item, (i % column), (int) (i / column));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLocalPaneUI() {
        localScrollPane = new ScrollPane();
        localGrid = new GridPane();
        localGrid.setPadding(new Insets(10, 10, 10, 10));
        localGrid.setVgap(8);
        localGrid.setHgap(10);
        localScrollPane.setContent(localGrid);
    }

    private void doGetLocalManga(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                localMangaList = FileSpider.getInstance().getMangaList(url);
                sortFiles();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        initLocalList();
                    }
                });
            }
        }).start();
    }

    //给本地图片文件夹及图片排序
    private void sortFiles() {
        pathList = new ArrayList<>();
        for (int i = 0; i < localMangaList.size(); i++) {
            pathList.add(localMangaList.get(i).getLocalThumbnailUrl());
        }

        if (!isNextDirectory(localMangaList.get(0).getUrl())) {
            //阅读页的前一页
            //获取第一张图片的路径
            String firstImgName = pathList.get(0);
            if (firstImgName.contains(".jpg") || firstImgName.contains(".png") || firstImgName.contains(".bmp")) {
                firstImgName = firstImgName.substring(0, firstImgName.length() - 1 - 3);
            } else if (firstImgName.contains(".jpeg")) {
                firstImgName = firstImgName.substring(0, firstImgName.length() - 1 - 4);
            }
            String[] arr = firstImgName.split("_");
            if (arr.length == 0) {
                arr = firstImgName.split("-");
            }

            if (pathList.get(0).contains("_") ||
                    pathList.get(0).contains("-")) {
                //正常的漫画
                if (arr.length != 3) {
                    return;
                }
                FileComparator comparator = new FileComparator();
                Collections.sort(pathList, comparator);
            } else if (pathList.get(0).contains("(")) {
                FileComparatorWithBracket comparator1 = new FileComparatorWithBracket();
                Collections.sort(pathList, comparator1);
            } else {
                String[] arri = firstImgName.split("/");
                //最终获得图片名字
                firstImgName = arri[arri.length - 1];
                try {
                    //用于判断是否位数字的异教徒写法
                    int isInt = Integer.valueOf(firstImgName);
                    //没抛出异常 所以是纯数字
                    FileComparatorAllNum comparator2 = new FileComparatorAllNum();
                    Collections.sort(pathList, comparator2);
                } catch (NumberFormatException e) {

                }
            }
            //将得到的排序结果给mangaList
            for (int i = 0; i < pathList.size(); i++) {
                localMangaList.get(i).setLocalThumbnailUrl(pathList.get(i));
                localMangaList.get(i).setName((i + 1) + "");
            }
        } else {
            //有很多话的漫画的文件夹层
            try {
                String firstDirectoryName = pathList.get(0);
                String[] arri = firstDirectoryName.split("/");

                firstDirectoryName = arri[arri.length - 2];
                String[] arri1 = firstDirectoryName.split("-");
                firstDirectoryName = arri1[0];
                firstDirectoryName = ReplaceUtil.onlyNumber(firstDirectoryName);

                //用于判断是否位数字的异教徒写法
                int isInt = Integer.valueOf(firstDirectoryName);
                //没抛出异常 所以是纯数字
                FileComparatorDirectory comparator4 = new FileComparatorDirectory();
                Collections.sort(localMangaList, comparator4);
            } catch (Exception e) {
                //假设有异常就不是有很多话的漫画的文件夹层
            }
        }
    }

    private boolean isNextDirectory(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    private void initLocalList() {
        try {
            localGrid.getChildren().clear();
            stackPaneWidth = (int) (scene.getWidth() - menuLv.getWidth());
            int column = (int) (stackPaneWidth / 200) - 1;
            for (int i = 0; i < localMangaList.size(); i++) {
                FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("/fxml/item_manga_list.fxml"));
                Parent item = fxmlLoader1.load();
                ItemMangaController itemController = fxmlLoader1.getController();
                MangaBean mangaItem = localMangaList.get(i);
                itemController.setLocalThumbil(new File(mangaItem.getLocalThumbnailUrl()).toURI().toURL().toString());
                itemController.setMangaName(mangaItem.getName());
                itemController.setOnClickListener(i, new OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        if (isNextDirectory(localMangaList.get(position).getUrl())) {
                            doGetLocalManga(localMangaList.get(position).getUrl());
                        } else {
                            openReadManga(position);
                        }
                    }
                });
                localGrid.add(item, (i % column), (int) (i / column));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openReadManga(int position) {
        try {
            Stage window = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/read.fxml"));
            Parent root = fxmlLoader.load();
            //如果使用 Parent root = FXMLLoader.load(...) 静态读取方法，无法获取到Controller的实例对象
            ReadController controller = fxmlLoader.getController(); //获取Controller的实例对象
            Scene scene = new Scene(root, 800, 500);

            window.setTitle(Configure.NAME);
            window.setMaximized(true);
            window.setScene(scene);
            window.show();
            controller.setStage(window);
            controller.setScene(scene);
            controller.setLocalPath(localMangaList.get(position).getUrl(),pathList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
