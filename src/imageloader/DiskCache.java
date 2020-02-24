package imageloader;

import java.io.File;
import java.net.MalformedURLException;

import configure.Configure;
import javafx.scene.image.Image;
import utils.ImgUtil;

public class DiskCache implements ImageCache {
    @Override
    public Image get(String url) {
        try {
            File file = new File(Configure.getCacheDirectory() + "/" +
                    url.substring(url.lastIndexOf("/")));
            if (!file.exists()) {
                return null;
            }
            return new Image(file.toURI().toURL().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void put(String url, Image img) {
        ImgUtil.saveToFile(img, Configure.getCacheDirectory() + "/" + url.substring(url.lastIndexOf("/")));
    }
}
