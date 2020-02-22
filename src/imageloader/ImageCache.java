package imageloader;

import javafx.scene.image.Image;

public interface ImageCache {
    Image get(String url);

    void put(String url, Image img);
}
