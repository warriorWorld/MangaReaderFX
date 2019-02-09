package mangadetail;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import base.BaseController;
import bean.ChapterBean;
import bean.DownloadBean;
import bean.MangaBean;
import configure.BaseParameterUtil;
import configure.Configure;
import dialog.AlertDialog;
import dialog.EditDialog;
import download.DownloadMangaManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import listener.EditResultListener;
import listener.JsoupCallBack;
import listener.OnItemClickListener;
import mangalist.ItemMangaController;
import read.ReadController;
import spider.SpiderBase;
import utils.ImgUtil;

public class OnlineMangaDetailController extends BaseController implements Initializable {
    public Label nameLb, authorLb, typeLb, lastUpdateLb;
    public Button collectBtn, downloadAllBtn, downloadBtn;
    public ImageView mangaIv;
    public GridPane chapterGp;
    private MangaBean currentManga;
    private int stackPaneWidth = 0;
    private SpiderBase spider;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initUI();
    }

    private void initUI() {
        collectBtn.setOnAction(event -> {
        });
        downloadAllBtn.setOnAction(event -> {
            downloadAll();
        });
        downloadBtn.setOnAction(event -> {
            EditDialog.display("选择下载", "请输入要下载的章节,用-分隔.如:12-158", "确定", new EditResultListener() {
                @Override
                public void onResult(String result) {
                    try {
                        String[] res = result.split("\\-");
                        doDownload(Integer.valueOf(res[0])-1, Integer.valueOf(res[1])-1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertDialog.display("请按格式输入");
                    }
                }
            });
        });
    }

    public void setUrl(String url, SpiderBase spider) {
        stage.setTitle(Configure.NAME + Configure.LOADING);
        this.spider = spider;
        spider.getMangaDetail(url, new JsoupCallBack<MangaBean>() {
            @Override
            public void loadSucceed(final MangaBean result) {
                currentManga = result;
                refreshUI();
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

    private void refreshUI() {
        if (null == currentManga) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                    final Image image;
                    image = ImgUtil.createImage(currentManga.getWebThumbnailUrl());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mangaIv.setImage(image);
                        }
                    });
            }
        }).start();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.setTitle(Configure.NAME);
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
        int column = (int) (stackPaneWidth / 90);
        for (int i = 0; i < currentManga.getChapters().size(); i++) {
            Button button = new Button();
            button.setPrefWidth(90);
            ChapterBean item = currentManga.getChapters().get(i);
            button.setText("第" + item.getChapterPosition() + "话");
            final int pos = i;
            button.setOnAction(event -> {
                openReadManga(pos);
            });
            chapterGp.add(button, (i % column), (int) (i / column));
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
            controller.setOnlinePath(currentManga.getChapters().get(position).getChapterUrl(), currentManga.getName(), position, spider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadAll() {
        doDownload(0, currentManga.getChapters().size() - 1);
    }

    private void doDownload(int start, int end) {
        DownloadMangaManager.getInstance().reset();
        MangaBean temp = currentManga;
        ArrayList<ChapterBean> chapters = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            ChapterBean item = new ChapterBean();
            item = currentManga.getChapters().get(i);
            chapters.add(item);
        }
        temp.setChapters(chapters);
        DownloadBean.getInstance().setMangaBean(temp);
        DownloadBean.getInstance().initDownloadChapters();
        DownloadBean.getInstance().setWebSite(BaseParameterUtil.getInstance().getCurrentWebSite());
        DownloadMangaManager.getInstance().doDownload();
    }

    public void setStackPaneWidth(int stackPaneWidth) {
        this.stackPaneWidth = stackPaneWidth;
    }
}
