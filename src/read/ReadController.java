package read;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import base.BaseController;
import bean.YoudaoResponse;
import configure.Configure;
import configure.ShareKeys;
import dialog.AlertDialog;
import dialog.EditDialog;
import download.DownloadMangaManager;
import enums.SourceType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import listener.EditResultListener;
import listener.JsoupCallBack;
import okhttp.HttpBack;
import okhttp.HttpService;
import spider.FileSpider;
import spider.SpiderBase;
import utils.ImgUtil;
import utils.TextUtils;

public class ReadController extends BaseController implements Initializable {
    private String path, title;
    public ImageView mIv;
    public ScrollPane mScrollPane;
    public Label currentInputLb, currentPageLb;
    public MenuItem jumpMi;
    public MenuItem imgPercentageMi;
    public CheckMenuItem closeTranslateCm;
    private String currentInput = "";
    private Clipboard mClipboard;
    private ArrayList<String> paths = new ArrayList<>();
    private int currentPosition = 0;
    private Preferences mPreferences;
    private SourceType mSourceType;
    private HashMap<Integer, Image> cacheList = new HashMap<>();
    private int chapterPos = 0;
    private ContextMenu imageCm;
    private MenuItem magnifyMi, shrinkMi, recoverMi, deteleMi, saveMi, refreshMi;
    private double currentMouseY = 0d, currentMouseYPercent = 0d;
    private boolean startAnchorZoom = false;
    private Image loadingImg, failedImg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        mPreferences = Preferences.userRoot();
        loadingImg = new Image("/drawable/loading.png");
        failedImg = new Image("/drawable/loadfailed.png");
        initUI();
    }


    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
    }

    @Override
    public void setScene(Scene scene) {
        super.setScene(scene);
        scene.getStylesheets().add("/css/read.css");
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println(event.getCode());
                if (event.getCode().toString().length() == 1) {
                    currentInput += event.getCode().toString().toLowerCase();
                } else if (event.getCode().toString().equals("ENTER")) {
                    mClipboard.setContents(new StringSelection(currentInput), null);
                    translateWord(currentInput);
                    currentInput = "";
                } else if (event.getCode().toString().equals("BACK_SPACE")) {
                    if (currentInput.length() > 0) {
                        currentInput = currentInput.substring(0, currentInput.length() - 1);
                    }
                } else if (event.getCode().toString().equals("SPACE")) {
                    event.consume();
                    currentInput += " ";
                } else if (event.getCode().toString().equals("RIGHT")) {
                    nextPage();
                } else if (event.getCode().toString().equals("LEFT")) {
                    previousPage();
                } else if (event.getCode().toString().equals("DELETE")) {
                    jsoup();
                } else if (event.isControlDown() && event.getCode().toString().equals("UP")) {
                    anchorZoomImg(1.1);
                } else if (event.isControlDown() && event.getCode().toString().equals("DOWN")) {
                    anchorZoomImg(0.9);
                } else if (!event.isControlDown() && event.getCode().toString().equals("UP")) {
                    mScrollPane.setVvalue(mScrollPane.getVvalue() - 0.3);
                } else if (!event.isControlDown() && event.getCode().toString().equals("DOWN")) {
                    mScrollPane.setVvalue(mScrollPane.getVvalue() + 0.3);
                }

                if (TextUtils.isEmpty(currentInput)) {
                    currentInputLb.setText("直接输入单词或句子,然后回车即可翻译单词或句子.");
                } else {
                    currentInputLb.setText(currentInput);
                }
            }
        });
        mScrollPane.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("RIGHT")) {
                nextPage();
            } else if (event.getCode().toString().equals("LEFT")) {
                previousPage();
            } else if (event.getCode().toString().equals("SPACE")) {
                event.consume();
                currentInput += " ";
            }
        });
        mIv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    imageCm.show(stage, event.getScreenX(), event.getScreenY());
                } else {
                    imageCm.hide();
                }
            }
        });

        mIv.setOnMouseMoved(event -> {
            currentMouseY = event.getY();
        });

        mScrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.isControlDown() && event.getDeltaY() > 0) {
                    anchorZoomImg(1.1);
                    event.consume();
                } else if (event.isControlDown() && event.getDeltaY() < 0) {
                    anchorZoomImg(0.9);
                    event.consume();
                } else if (!event.isControlDown() && event.getDeltaY() > 0) {
                    mScrollPane.setVvalue(mScrollPane.getVvalue() - 0.3);
                    event.consume();
                } else if (!event.isControlDown() && event.getDeltaY() < 0) {
                    mScrollPane.setVvalue(mScrollPane.getVvalue() + 0.3);
                    event.consume();
                }
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode().toString().equals("CONTROL")) {
                System.out.println("released control");
                startAnchorZoom = false;
            }
        });
    }

    private void anchorZoomImg(double percent) {
        if (!startAnchorZoom) {
            double totalY = mIv.getFitHeight();
            currentMouseYPercent = currentMouseY / totalY;
            System.out.println(currentMouseY + "," + totalY + "," + currentMouseYPercent);
            startAnchorZoom = true;
        }

        mIv.setFitHeight(mIv.getFitHeight() * percent);
        mScrollPane.setVvalue(currentMouseYPercent);
    }

    private void initUI() {
        mScrollPane.getStylesheets().add("/css/read.css");
        mIv.setPreserveRatio(true);
        jumpMi.setOnAction(event -> {
            EditDialog.display("跳转到", "请输入跳转位置(页码)", "确定", new EditResultListener() {
                @Override
                public void onResult(String result) {
                    try {
                        toPage(getCorrectPage(Integer.valueOf(result)) - 1);
                    } catch (NumberFormatException e) {
                        AlertDialog.display("错误", "请输入数字,并且是整数!", "知道了");
                    }
                }
            });
        });
        mScrollPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double result = mScrollPane.getWidth() / 2;
                double threshold = mIv.getImage().getWidth() / 2;
                if (event.getSceneX() < (result) && event.getSceneX() > (result - threshold) && event.getButton().toString().equals("PRIMARY")) {
                    previousPage();
                } else if (event.getSceneX() > (result) && event.getSceneX() < (result + threshold) && event.getButton().toString().equals("PRIMARY")) {
                    nextPage();
                }
            }
        });
        closeTranslateCm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mPreferences.putBoolean(ShareKeys.CLOSE_TRANSLATE, closeTranslateCm.isSelected());
            }
        });
        closeTranslateCm.setSelected(mPreferences.getBoolean(ShareKeys.CLOSE_TRANSLATE, false));
        imgPercentageMi.setOnAction(event -> {
            EditDialog.display("设置图片缩放比例", "请输入缩放比例,如:1.2表示按1.2倍大小缩放", "确定", new EditResultListener() {
                @Override
                public void onResult(String result) {
                    try {
                        double rate = Double.valueOf(result);
                        mIv.setFitHeight(mIv.getFitHeight() * rate);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertDialog.display("请按格式输入");
                    }
                }
            });
        });
        mIv.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight() * 0.9);
        imageCm = new ContextMenu();
        refreshMi = new MenuItem("刷新");
        refreshMi.setOnAction(event -> {
            cacheList.remove(currentPosition);
            toPage(currentPosition);
        });
        magnifyMi = new MenuItem("放大");
        magnifyMi.setOnAction(event -> {
            mIv.setFitHeight(mIv.getFitHeight() * 1.1);
        });
        shrinkMi = new MenuItem("缩小");
        shrinkMi.setOnAction(event -> {
            mIv.setFitHeight(mIv.getFitHeight() * 0.9);
        });
        recoverMi = new MenuItem("恢复大小");
        recoverMi.setOnAction(event -> {
            mIv.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight() * 0.9);
        });
        deteleMi = new MenuItem("删除图片");
        deteleMi.setOnAction(event -> {
            FileSpider.deleteFile(new File(paths.get(currentPosition)));
            paths.remove(currentPosition);
            toPage(currentPosition);
        });
        saveMi = new MenuItem("保存图片");
        saveMi.setOnAction(event -> {
            Long time = new Date().getTime();
            String timeString = time + "";
            timeString = timeString.substring(5);
            DownloadMangaManager.getInstance().downloadImg(paths.get(currentPosition), "UserSaved/userSaved(" + timeString + ").png");
            AlertDialog.display("已保存,返回后刷新可见.");
        });
    }

    private void translateWord(final String word) {
        if (mPreferences.getBoolean(ShareKeys.CLOSE_TRANSLATE, false)) {
            return;
        }
        String url = Configure.YOUDAO + word;
        HttpService.getInstance().requestGetData(url, YoudaoResponse.class, new HttpBack<YoudaoResponse>() {
            @Override
            public void loadSucceed(YoudaoResponse result) {
                if (null != result && result.getErrorCode() == 0) {
                    YoudaoResponse.BasicBean item = result.getBasic();
                    String t = "";
                    if (null != item) {
                        for (int i = 0; i < item.getExplains().size(); i++) {
                            t = t + item.getExplains().get(i) + ";";
                        }
                        AlertDialog.display(word, result.getQuery() + "  [" + item.getPhonetic() + "]: " + "\n" + t, "确定");
                    } else {
//                        baseToast.showToast("没查到该词");
                    }
                } else {
//                    baseToast.showToast("没查到该词");
                }
            }

            @Override
            public void loadFailed(String error) {

            }
        });
    }

    private void toPage(final int page) {
        stage.setTitle(title + Configure.LOADING);
        mScrollPane.setVvalue(0);
        currentPosition = page;
        currentPageLb.setText((page + 1) + "/" + paths.size());
        saveProgress();
        switch (mSourceType) {
            case LOCAL:
                try {
                    mIv.setImage(new Image(new File(paths.get(page)).toURI().toURL().toString()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                stage.setTitle(title);
                break;
            case ONLINE:
                mIv.setFitHeight(50);
                mIv.setImage(loadingImg);
                //预加载
                int preLoadCount = 1;
                for (int i = 0; i < preLoadCount; i++) {
                    final int prePos = page + i + 1;
                    if (null == cacheList.get(prePos)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("thread: " + prePos);
                                cacheList.put(prePos, loadingImg);
                                try {
                                    final Image image;
                                    image = ImgUtil.createImage(paths.get(prePos));
                                    if (null != image) {
                                        cacheList.put(prePos, image);
                                        System.out.println("thread: " + prePos + " done");
                                    }
                                    showImg(prePos, image);
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                //如有缓存 直接用
                if (null != cacheList.get(page)) {
                    showImg(page, cacheList.get(page));
                    return;
                }
                //加载当前页
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("thread current: " + page);
                        cacheList.put(page, loadingImg);
                        final Image image;
                        image = ImgUtil.createImage(paths.get(page));
                        if (null != image) {
                            cacheList.put(page, image);
                        }
                        System.out.println("thread current: " + page + " done");
                        showImg(page, image);
                    }
                }).start();
                break;
        }
    }

    private void showImg(int page, Image image) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (page == currentPosition) {
                    if (null != image) {
                        if (image.equals(loadingImg)) {
                            mIv.setFitHeight(50);
                            mIv.setImage(loadingImg);
                            stage.setTitle(title + Configure.LOADING);
                        } else {
                            mIv.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight() * 0.9);
                            mIv.setImage(image);
                            stage.setTitle(title);
                        }
                    } else {
                        mIv.setFitHeight(50);
                        mIv.setImage(failedImg);
                        stage.setTitle(title + Configure.LOAD_FAILED);
                    }
                }
            }
        });
    }

    private void nextPage() {
        if (currentPosition < paths.size() - 1) {
            toPage(currentPosition + 1);
        }
    }

    private void previousPage() {
        if (currentPosition > 0) {
            toPage(currentPosition - 1);
        }
    }

    private int getCorrectPage(int page) {
        if (page < 0) {
            page = 0;
        }
        if (page > paths.size() - 1) {
            page = paths.size() - 1;
        }
        return page;
    }

    //TODO
    public void handleZoom() {
        System.out.println("handle zoom");
    }

    private void saveProgress() {
        mPreferences.putInt(title + chapterPos + "lastRead", currentPosition);
    }

    private void receiveProgress() {
        currentPosition = mPreferences.getInt(title + chapterPos + "lastRead", 0);
    }

    public void setLocalPath(String name, ArrayList<String> urls) {
        mSourceType = SourceType.LOCAL;
        imageCm.getItems().addAll(magnifyMi, shrinkMi, recoverMi, deteleMi);
        path = name;
        try {
            title = name.substring(name.lastIndexOf("\\") + 1);
            if (TextUtils.isEmpty(title)) {
                title = name.substring(name.lastIndexOf("/") + 1);
            }
            if (title.contains("_")) {
                title = title.substring(0, title.lastIndexOf("_"));
            }
        } catch (Exception e) {
            title = name;
        }
        stage.setTitle(title);

        receiveProgress();
        paths = urls;
        toPage(currentPosition);
    }

    public void setOnlinePath(String url, String mangaName, int chapterPosition, SpiderBase spider) {
        mSourceType = SourceType.ONLINE;
        imageCm.getItems().addAll(refreshMi, magnifyMi, shrinkMi, recoverMi, saveMi);
        path = url;
        title = mangaName + "(" + (chapterPosition + 1) + ")";
        chapterPos = chapterPosition;
        stage.setTitle(title + Configure.LOADING);

        receiveProgress();
        spider.getMangaChapterPics(url, new JsoupCallBack<ArrayList<String>>() {
            @Override
            public void loadSucceed(final ArrayList<String> result) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        paths = result;
                        toPage(currentPosition);
                    }
                });
            }

            @Override
            public void loadFailed(String error) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.display("错误", error, "确定");
                    }
                });
            }
        });
    }

    private void jsoup() {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://jandan.net/duan/page-91#comments")
                    .timeout(10000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null != doc) {
            Element mangaListElements = doc.body().getElementById("wrapper").getElementById("body")
                    .getElementById("content").getElementById("comments");
            Elements eles = mangaListElements.getElementsByClass("commentlist").last().
                    getElementsByTag("li");
            String result = "";
            for (Element ele : eles) {
//                System.out.println(ele.select("div.text").text());
                result += ele.select("div.text").text() + "\n";
            }
            currentPageLb.setText(result);
        }
    }
}
