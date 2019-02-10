package download;

import java.net.URL;
import java.util.ResourceBundle;

import base.BaseController;
import bean.ChapterBean;
import bean.DownloadBean;
import enums.DownloadState;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.MainItemController;
import utils.ImgUtil;
import utils.TextUtils;

public class DownloadController extends BaseController implements Initializable {
    public ImageView mangaIv;
    public Label nameLb, explainLb;
    public ListView downloadLv;
    public Button downloadBtn;
    public ProgressBar downloadPb;
    private DownloadState downloadState = DownloadState.STOPED;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initUI();
        DownloadMangaManager.getInstance().setController(this);
        refreshUI();
    }

    private void initUI() {
        downloadBtn.setOnAction(event -> {
            switch (downloadState) {
                case STOPED:
                    DownloadMangaManager.getInstance().doDownload();
                    toggleDownloading(true);
                    break;
                case ON_GOING:
                    DownloadMangaManager.getInstance().stopDownload();
                    toggleDownloading(false);
                    break;
            }
        });
    }

    public void refreshUI() {
        if (null == DownloadBean.getInstance() ||
                null == DownloadBean.getInstance().getCurrentManga() ||
                TextUtils.isEmpty(DownloadBean.getInstance().getWebSite())) {
            toEmptyUI();
            return;
        }
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Image image;
                    image = ImgUtil.createImage(DownloadBean.getInstance().getCurrentManga().getWebThumbnailUrl());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mangaIv.setImage(image);
                        }
                    });
                }
            }).start();
            nameLb.setText("漫画名称:  " + DownloadBean.getInstance().getCurrentManga().getName());
            toggleDownloading(!DownloadMangaManager.getInstance().isStopDownload());
            explainLb.setText("正在下载:  第" +
                    DownloadMangaManager.getInstance().
                            getCurrentChapter().getChapter_title() + "话");
            if (null != DownloadMangaManager.getInstance().
                    getCurrentChapter() && null != DownloadMangaManager.getInstance().
                    getCurrentChapter().getPages()) {
                downloadPb.setProgress((double) (DownloadMangaManager.getInstance().
                        getCurrentChapter().getChapter_size() - DownloadMangaManager.
                        getInstance().getCurrentChapter().getPages().size()) / (double) DownloadMangaManager.getInstance().getCurrentChapter().getChapter_size());
            }
        } catch (Exception e) {
            toEmptyUI();
        }
    }

    public void addDownloaded(String text) {
        downloadLv.getItems().add(text);
    }

    public void setProgress() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                nameLb.setText("漫画名称:  " + DownloadBean.getInstance().getCurrentManga().getName());
                explainLb.setText("正在下载:  第" +
                        DownloadMangaManager.getInstance().
                                getCurrentChapter().getChapter_title() + "话");
                if (null != DownloadMangaManager.getInstance().
                        getCurrentChapter() && null != DownloadMangaManager.getInstance().
                        getCurrentChapter().getPages()) {
                    downloadPb.setProgress((double) (DownloadMangaManager.getInstance().
                            getCurrentChapter().getChapter_size() - DownloadMangaManager.
                            getInstance().getCurrentChapter().getPages().size()) / (double) DownloadMangaManager.getInstance().getCurrentChapter().getChapter_size());
                }
            }
        });
    }

    private void toggleDownloading(boolean ing) {
        if (ing) {
            downloadBtn.setText("停止下载");
            downloadState = DownloadState.ON_GOING;
        } else {
            downloadBtn.setText("开始下载");
            downloadState = DownloadState.STOPED;
        }
    }

    public void toEmptyUI() {
        mangaIv.setImage(ImgUtil.createImage(""));
        nameLb.setText("没有正在下载的漫画");
        explainLb.setText("");
        downloadPb.setProgress(0);
    }
}
