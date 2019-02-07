package mangadetail;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import base.BaseController;
import bean.ChapterBean;
import bean.MangaBean;
import dialog.AlertDialog;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import listener.JsoupCallBack;
import listener.OnItemClickListener;
import mangalist.ItemMangaController;
import spider.SpiderBase;
import utils.ImgUtil;

public class OnlineMangaDetailController extends BaseController implements Initializable {
    public Label nameLb, authorLb, typeLb, lastUpdateLb;
    public Button collectBtn;
    public ImageView mangaIv;
    public GridPane chapterGp;
    private MangaBean currentManga;
    private int stackPaneWidth = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setUrl(String url, SpiderBase spider) {
        spider.getMangaDetail(url, new JsoupCallBack<MangaBean>() {
            @Override
            public void loadSucceed(final MangaBean result) {
                currentManga = result;
                refreshUI();
            }

            @Override
            public void loadFailed(String error) {
                AlertDialog.display("错误", error, "确定");
            }
        });
    }

    private void refreshUI() {
        if (null == currentManga) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Image image;
                    image = ImgUtil.createImage(currentManga.getWebThumbnailUrl());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mangaIv.setImage(image);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                nameLb.setText("漫画名称:" + currentManga.getName());
                authorLb.setText("作者:" + currentManga.getAuthor());

                String mangaTags = "";
                for (int i = 0; i < currentManga.getTypes().length; i++) {
                    //漫画类型
                    mangaTags = mangaTags + " " + currentManga.getTypes()[i];
                }
                typeLb.setText("类型:" + mangaTags);
                lastUpdateLb.setText("最后更新:" + currentManga.getLast_update());

                initGridView();
            }
        });
    }

    private void initGridView() {
        chapterGp.getChildren().clear();
        int column = (int) (stackPaneWidth/ 90);
        for (int i = 0; i < currentManga.getChapters().size(); i++) {
            Button button = new Button();
            button.setPrefWidth(90);
            ChapterBean item = currentManga.getChapters().get(i);
            button.setText("第" + item.getChapterPosition() + "话");
            button.setOnAction(event -> {
                AlertDialog.display("tit", item.getChapterUrl(), "ddd");
            });
            chapterGp.add(button, (i % column), (int) (i / column));
        }
    }

    public void setStackPaneWidth(int stackPaneWidth) {
        this.stackPaneWidth = stackPaneWidth;
    }
}
