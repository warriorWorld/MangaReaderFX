package mangalist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

import base.BaseController;
import imageloader.DiskCache;
import imageloader.ImageLoader;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import listener.OnClickListener;
import listener.OnItemClickListener;
import utils.ImgUtil;

public class ItemMangaController extends BaseController implements Initializable {
    public ImageView mangaIv;
    public Label mangaNameLb;
    private ImageLoader mImageLoader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mangaIv.setPreserveRatio(true);
        mImageLoader = new ImageLoader();
        mImageLoader.setImageCache(new DiskCache());
    }

    public void setMangaThumbil(String img) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                Image image = new Image(img);
//                final Image image;
//                image = ImgUtil.createImage(img);
//
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        mangaIv.setImage(image);
//                    }
//                });
//            }
//        }).start();
        mImageLoader.displayImage(img, mangaIv);
    }

    public void setLocalThumbil(String img) {
        mangaIv.setImage(new Image(img));
    }

    public void setMangaName(String text) {
        mangaNameLb.setText(text);
    }


    public void setOnClickListener(int position, OnItemClickListener listener) {
        EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (null != listener) {
                    listener.onMouseEvent(position, event);
                }
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (null != listener) {
                        listener.onClick(position);
                    }
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    if (null != listener) {
                        listener.onRightClick(position);
                    }
                }
            }
        };
        mangaIv.setOnMouseClicked(mouseEventEventHandler);
        mangaNameLb.setOnMouseClicked(mouseEventEventHandler);
    }
}
