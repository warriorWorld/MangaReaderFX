package test;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import bean.MangaBean;
import bean.MangaListBean;
import utils.TextUtils;

public class Test {
    protected static org.jsoup.nodes.Document doc;
    protected static int timeoutV = 60000;

    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("https://toefl.kmf.com/reading/exercise/11m4mj/158122903473161388/11m4mj")
                            .timeout(timeoutV).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (null != doc) {
                    Elements mangaListElements = doc.select("div.section-container");
                    MangaBean item;
                    ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
                    for (int i = 0; i < mangaListElements.size(); i++) {
                        item = new MangaBean();
                        item.setName(mangaListElements.get(i).getElementsByTag("img").last().attr("alt"));
                        item.setUrl(mangaListElements.get(i).select("a").first().attr("href"));
                        item.setWebThumbnailUrl(mangaListElements.get(i).getElementsByTag("img").last().attr("src"));
                        mangaList.add(item);
                    }
                    MangaListBean mangaListBean = new MangaListBean();
                    mangaListBean.setMangaList(mangaList);
                }
            }
        }.start();
    }
}
