package download;

import java.net.URL;
import java.util.ResourceBundle;

import base.BaseController;
import bean.DownloadBean;
import enums.DownloadState;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.ImgUtil;
import utils.TextUtils;

public class DownloadController extends BaseController implements Initializable {
    public ImageView mangaIv;
    public Label nameLb;
    public ListView downloadLv;
    public Button downloadBtn;
    private DownloadState downloadState = DownloadState.STOPED;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initUI();
        refreshUI();
    }

    private void initUI(){
        downloadBtn.setOnAction(event -> {
            switch (downloadState){
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
    private void refreshUI() {
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
        } catch (Exception e) {
            toEmptyUI();
        }
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

    private void toEmptyUI() {
        mangaIv.setImage(ImgUtil.createImage(""));
        nameLb.setText("没有正在下载的漫画");
    }
}
