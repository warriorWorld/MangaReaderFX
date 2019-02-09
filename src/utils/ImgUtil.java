package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ImgUtil {
    /**
     * 这不是https问题。看起来cdn.discordapp.com需要HTTP请求中的一个User-Agent头。此外，该网站似乎只接受用户代理的特定格式。原来，由wget供给被接受，用户代理：
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static Image createImage(String url) {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)");

            try (InputStream stream = conn.getInputStream()) {
                return new Image(stream);
            }
        } catch (MalformedURLException e) {
            return createImage("http://ww3.sinaimg.cn/mw600/006XNEY7gy1g002effyj2j30u0141qv5.jpg");
        } catch (IOException e) {
            return null;
        }
    }

    public static void saveToFile(Image image, String path) {
        File outputFile = new File(path);
        if (!outputFile.exists()) {
            // 如果不存在 就创建
            outputFile.mkdirs();
        }
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
