package imageloader;

import javafx.scene.image.Image;

public class DoubleCache implements ImageCache {
    private ImageCache mMemoryCache = new MemoryCache();
    private ImageCache mDiskCache = new DiskCache();

    @Override
    public Image get(String url) {
        Image img = mMemoryCache.get(url);
        if (null != img) {
            return img;
        }
        img = mDiskCache.get(url);
        return img;
    }

    @Override
    public void put(String url, Image img) {
        mMemoryCache.put(url, img);
        mDiskCache.put(url, img);
    }
}
