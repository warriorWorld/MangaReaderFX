package imageloader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.ImgUtil;

public class ImageLoader {
    private ImageCache mImageCache;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool
            (Runtime.getRuntime().availableProcessors());
    private Image loadingImg = new Image("/drawable/loading.png");
    private Image failedImg = new Image("/drawable/loadfailed.png");

    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    public void displayImage(String imgUrl, ImageView imageView) {
        imageView.setImage(loadingImg);
        Image image = mImageCache.get(imgUrl);
        if (null != image) {
            imageView.setImage(image);
            return;
        }
        submitLoadRequest(imgUrl, imageView);
    }

    private void submitLoadRequest(String imgUrl, ImageView imageView) {
        imageView.setId(imgUrl);
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                Image image = downloadImage(imgUrl);
                if (null == image) {
                    return;
                }
                if (imageView.getId().equals(imgUrl)) {
                    if (null == image) {
                        imageView.setImage(failedImg);
                    } else {
                        imageView.setImage(image);
                    }
                }
                mImageCache.put(imgUrl, image);
            }
        });
    }

    private Image downloadImage(String imgUrl) {
        return ImgUtil.createImage(imgUrl);
    }
}
