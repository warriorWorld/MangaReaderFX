package read;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import base.BaseController;
import bean.YoudaoResponse;
import configure.Configure;
import configure.ShareKeys;
import dialog.AlertDialog;
import dialog.EditDialog;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import listener.EditResultListener;
import listener.JsoupCallBack;
import okhttp.HttpBack;
import okhttp.HttpService;
import spider.FileSpider;
import spider.SpiderBase;
import utils.ImgUtil;
import utils.ReplaceUtil;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        mPreferences = Preferences.userRoot();
        initUI();
    }


    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
    }

    @Override
    public void setScene(Scene scene) {
        super.setScene(scene);
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
    }

    private void initUI() {
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
        mIv.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight() * 0.92);
        imageCm = new ContextMenu();
        MenuItem magnifyMi = new MenuItem("放大");
        magnifyMi.setOnAction(event -> {
            mIv.setFitHeight(mIv.getFitHeight() * 1.1);
        });
        MenuItem shrinkMi =  new MenuItem("缩小");
        shrinkMi.setOnAction(event -> {
            mIv.setFitHeight(mIv.getFitHeight() * 0.9);
        });
        MenuItem recoverMi =  new MenuItem("恢复大小");
        recoverMi.setOnAction(event -> {
            mIv.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight() * 0.92);
        });
        imageCm.getItems().addAll(magnifyMi, shrinkMi, recoverMi, new MenuItem("保存图片"), new MenuItem("删除图片"));
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
                if (page < paths.size() - 2) {
                    //预加载下一页
                    if (null == cacheList.get(page + 1)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Image image;
                                image = ImgUtil.createImage(paths.get(page + 1));
                                cacheList.put(page + 1, image);
                            }
                        }).start();
                    }
                }
                if (null != cacheList.get(page)) {
                    mIv.setImage(cacheList.get(page));
                    stage.setTitle(title);
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Image image;
                        image = ImgUtil.createImage(paths.get(page));
                        cacheList.put(page, image);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (page == currentPosition) {
                                    mIv.setImage(image);
                                    stage.setTitle(title);
                                }
                            }
                        });
                    }
                }).start();
                break;
        }
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
        path = url;
        title = mangaName;
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
