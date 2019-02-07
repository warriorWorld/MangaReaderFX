package mangalist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

import base.BaseController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import listener.OnClickListener;
import listener.OnItemClickListener;

public class ItemMangaController extends BaseController implements Initializable {
    public ImageView mangaIv;
    public Label mangaNameLb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mangaIv.setPreserveRatio(true);
    }

    public void setMangaThumbil(String img) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Image image = new Image(img);
                try {
                    final Image image;
                    image = createImage(img);

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
    }

    /**
     * 这不是https问题。看起来cdn.discordapp.com需要HTTP请求中的一个User-Agent头。此外，该网站似乎只接受用户代理的特定格式。原来，由wget供给被接受，用户代理：
     *
     * @param url
     * @return
     * @throws IOException
     */
    private Image createImage(String url)
            throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)");

        try (InputStream stream = conn.getInputStream()) {
            return new Image(stream);
        }
    }

    public void setMangaName(String text) {
        mangaNameLb.setText(text);
    }

    public void setOnClickListener(int position, OnItemClickListener listener) {
        mangaIv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().toString().equals("PRIMARY")) {
                    if (null != listener) {
                        listener.onClick(position);
                    }
                }
            }
        });
    }
}
