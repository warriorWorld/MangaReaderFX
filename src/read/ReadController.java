package read;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import base.BaseController;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import spider.FileSpider;
import utils.TextUtils;

public class ReadController extends BaseController implements Initializable {
    private String path, title;
    private org.jsoup.nodes.Document doc;
    public ImageView mIv;
    public ScrollPane mScrollPane;
    public Label currentInputLb, currentPageLb;
    private String currentInput = "";
    private Clipboard mClipboard;
    private ArrayList<String> paths = new ArrayList<>();
    private int currentPosition = 0;
    private Preferences mPreferences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //这个会比main的start调用还早
        mClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        mPreferences = Preferences.userRoot();
    }

    public void setPath(String url) {
        path = url;
        title = url.substring(url.lastIndexOf("\\") + 1);
        stage.setTitle(url);

        receiveProgress();
        paths = FileSpider.getInstance().getMangaDetail(url);
        initUI();
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        stage.setTitle("具体漫画名称");
    }

    private void initUI() {
        mIv.setPreserveRatio(true);
        mIv.setImage(new Image(paths.get(currentPosition)));
        currentPageLb.setText((currentPosition + 1) + "/" + paths.size());
        mScrollPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println(event.getCode());
                if (event.getCode().toString().length() == 1) {
                    currentInput += event.getCode().toString().toLowerCase();
                } else if (event.getCode().toString().equals("ENTER")) {
                    mClipboard.setContents(new StringSelection(currentInput), null);
                    currentInput = "";
                } else if (event.getCode().toString().equals("BACK_SPACE")) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                } else if (event.getCode().toString().equals("SPACE")) {
                    event.consume();
                    currentInput += " ";
                } else if (event.getCode().toString().equals("RIGHT")) {
                    nextPage();
                } else if (event.getCode().toString().equals("LEFT")) {
                    previousPage();
                }
                if (TextUtils.isEmpty(currentInput)) {
                    currentInputLb.setText("输入单词");
                } else {
                    currentInputLb.setText(currentInput);
                }
            }
        });
        mScrollPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double result=mScrollPane.getWidth()/2;
                double threshold=mIv.getImage().getWidth()/2;
                System.out.println(event.getButton()+"");
                if (event.getSceneX()<(result)&&event.getSceneX()>(result-threshold)&&event.getButton().toString().equals("PRIMARY")){
                    previousPage();
                }else if (event.getSceneX()>(result)&&event.getSceneX()<(result+threshold)&&event.getButton().toString().equals("PRIMARY")){
                    nextPage();
                }
            }
        });
    }

    private void nextPage() {
        if (currentPosition < paths.size() - 1) {
            currentPosition++;
            mIv.setImage(new Image(paths.get(currentPosition)));
            currentPageLb.setText((currentPosition + 1) + "/" + paths.size());
            saveProgress();
        }
    }

    private void previousPage() {
        if (currentPosition > 0) {
            currentPosition--;
            mIv.setImage(new Image(paths.get(currentPosition)));
            currentPageLb.setText((currentPosition + 1) + "/" + paths.size());
            saveProgress();
        }
    }

    //TODO
    public void handleZoom() {
        System.out.println("handle zoom");
    }

    private void saveProgress() {
        mPreferences.putInt(path + "lastReadPosition", currentPosition);
    }

    private void receiveProgress() {
        currentPosition = mPreferences.getInt(path + "lastReadPosition", 0);
    }

    private void jsoup() {
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
        }
    }
}
