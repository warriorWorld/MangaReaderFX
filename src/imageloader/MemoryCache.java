package imageloader;

import java.util.HashMap;

import javafx.scene.image.Image;

public class MemoryCache implements ImageCache {
    private HashMap<String, Image> mMemeryCache = new HashMap<>();

    @Override
    public Image get(String url) {
        return mMemeryCache.get(url);
    }

    @Override
    public void put(String url, Image img) {
        mMemeryCache.put(url, img);
    }
}
