package spider;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import bean.ChapterBean;
import bean.MangaBean;
import bean.MangaListBean;
import configure.Configure;
import listener.JsoupCallBack;
import utils.TextUtils;

/**
 * http://www.mangareader.net/
 */
public class OwlSpider extends SpiderBase {
    private String webUrl = "https://mangaowl.com/";
    private String webUrlNoLastLine = "https://mangaowl.com";

    @Override
    public <ResultObj> void getMangaList(final String type, final String page, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(type) || type.equals("all")) {
                        doc = Jsoup.connect(webUrl + "list/" + page)
                                .timeout(timeoutV).get();
                    } else {
                        doc = Jsoup.connect(webUrl + "manga_list?type=topview&category=" + type + "&alpha=all&page=" + page + "&state=all")
                                .timeout(timeoutV).get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    Elements mangaListElements = doc.getElementById("table-breakpoint").getElementsByTag("tr");
                    MangaBean item;
                    ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
                    for (int i = 1; i < mangaListElements.size(); i++) {
                        item = new MangaBean();
                        Elements mangaElements = mangaListElements.get(i).children();
                        item.setName(mangaElements.get(3).text());
                        item.setUrl(mangaElements.get(2).select("a").attr("href"));
                        item.setWebThumbnailUrl(mangaElements.get(2).select("a").first().child(0).attr("data-background-image"));
                        mangaList.add(item);
                    }
                    MangaListBean mangaListBean = new MangaListBean();
                    mangaListBean.setMangaList(mangaList);
                    jsoupCallBack.loadSucceed((ResultObj) mangaListBean);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
    }

    @Override
    public <ResultObj> void getMangaDetail(final String mangaURL, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(mangaURL)
                            .timeout(timeoutV).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                try {
                    if (null != doc) {
//                        Elements mangaPicDetailElements = doc.select("div.content-body").
//                                select("div.single-page-agile-main").select("div.container").
//                                select("div.single-page-agile-info").
//                                select("div.show-top-grids-w3lagile");
//                        Elements mangaTextDetailElements = doc.select("div.col-xs-12 col-md-8 single-right-grid-right");
//                        Element mangaTitleElement = doc.select("div.story-info-right h1").first();
//                        Elements mangaTagDetailElements = mangaTextDetailElements.get(3).select("td.table-value a");
//                        Element lastUpdateElement = doc.select("span.stre-value").first();

                        MangaBean item = new MangaBean();
                        item.setUrl(mangaURL);
//                        item.setWebThumbnailUrl(mangaPicDetailElements.first().getElementsByTag("img").last().attr("src"));
//                        item.setName(mangaTextDetailElements.get(0).text());
//                        if (null != mangaTextDetailElements && mangaTextDetailElements.size() >= 4) {
//                            String authors = mangaTextDetailElements.get(1).text();
//                            authors = authors.replaceAll("Author\\(s\\) : ", "");
//                            authors = authors.substring(0, authors.length() - 1);//这个网站的作者最后一位总是有个逗号
//                            item.setAuthor(authors);
//                            //Tag
//                            String[] types = new String[mangaTagDetailElements.size()];
//                            String[] typeCodes = new String[mangaTagDetailElements.size()];
//                            for (int i = 0; i < mangaTagDetailElements.size(); i++) {
//                                String typeCode = mangaTagDetailElements.get(i).attr("href");
//                                //加个\\转义字符
//                                typeCode = typeCode.replaceAll("https://mangakakalot.com/manga_list\\?type=newest&category=", "");
//                                typeCode = typeCode.replaceAll("&alpha=all&page=1&state=all", "");
//                                typeCode = typeCode.replaceAll("https://manganelo.com", "");
//                                typeCodes[i] = typeCode;
//                                types[i] = mangaTagDetailElements.get(i).text();
//                            }
//                            item.setTypes(types);
//                            item.setTypeCodes(typeCodes);
//                        }
//                        //last update
//                        String lastUpadte = lastUpdateElement.text();
//                        item.setLast_update(lastUpadte);
                        //chapter
                        Elements chapterElements =doc.getElementsByClass("table-striped table-responsive-md").first().getElementsByTag("tr");
                        ArrayList<ChapterBean> chapters = new ArrayList<ChapterBean>();
                        ChapterBean chapterBean;
                        int chapterPosition = 0;
                        for (int i = chapterElements.size() - 1; i >= 0; i--) {
                            //原网站是倒叙排列的
                            chapterBean = new ChapterBean();
                            chapterPosition++;
                            chapterBean.setChapterPosition(chapterPosition + "");
                            chapterBean.setChapterUrl(chapterElements.get(i).select("a").first().attr("href"));
                            chapters.add(chapterBean);
                        }
                        item.setChapters(chapters);
                        jsoupCallBack.loadSucceed((ResultObj) item);
                    } else {
                        jsoupCallBack.loadFailed("doc load failed");
                    }
                } catch (Exception e) {
                    jsoupCallBack.loadFailed(Configure.WRONG_WEBSITE_EXCEPTION);
                }
            }
        }.start();
    }


    @Override
    public boolean isOneShot() {
        return false;
    }

    @Override
    public String[] getMangaTypes() {
        String[] mangaTypeCodes = {"all", "action", "adult", "adventure", "comedy", "cooking", "doujinshi", "drama", "ecchi", "fantasy", "gender-bender",
                "harem", "historical", "horror", "josei", "manhua", "manhwa", "martial-arts", "mature", "mecha", "medical", "mystery", "one-shot",
                "psychological", "romance", "school-life", "sci-fi", "seinen", "shoujo", "shoujoai", "shounen", "shounenai", "slice-of-life", "smut", "sports",
                "supernatural", "tragedy", "webtoons", "yaoi", "yuri"};
        return mangaTypeCodes;
    }

    @Override
    public String[] getMangaTypeCodes() {
        String[] mangaTypeCodes = {"all", "2", "3", "4", "6", "7", "9", "10", "11", "12", "13",
                "14", "15", "16", "17", "44", "43", "19", "20", "21", "22", "24", "25",
                "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
                "38", "39", "40", "41", "42"};
        return mangaTypeCodes;
    }

    @Override
    public <ResultObj> void getMangaChapterPics(final String chapterUrl, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(chapterUrl)
                            .timeout(timeoutV).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    Elements mangaPicsElements = doc.select("div.container-chapter-reader").first().getElementsByTag("img");
                    ArrayList<String> pathList = new ArrayList<String>();
                    for (int i = 0; i < mangaPicsElements.size(); i++) {
                        pathList.add(mangaPicsElements.get(i).attr("src"));
                    }
                    jsoupCallBack.loadSucceed((ResultObj) pathList);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
    }

    @Override
    public <ResultObj> void getSearchResultList(final SearchType type, final String keyWord, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String keyW = keyWord.replaceAll(" ", "_");
                    switch (type) {
                        case BY_MANGA_AUTHOR:
                            doc = Jsoup.connect(webUrl + "search_author/" + keyW)
                                    .timeout(timeoutV).get();
                            break;
                        case BY_MANGA_NAME:
                            doc = Jsoup.connect(webUrl + "search/" + keyW)
                                    .timeout(timeoutV).get();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    Elements mangaListElements = doc.select("div.story_item_right h3");
                    Elements mangaListHrefs = mangaListElements.select("a");
                    MangaBean item;
                    ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
                    for (int i = 0; i < mangaListElements.size(); i++) {
                        item = new MangaBean();
                        item.setName(mangaListElements.get(i).text());
                        item.setUrl(mangaListHrefs.get(i).attr("href"));
                        mangaList.add(item);
                    }
                    MangaListBean mangaListBean = new MangaListBean();
                    mangaListBean.setMangaList(mangaList);
                    jsoupCallBack.loadSucceed((ResultObj) mangaListBean);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
    }

    @Override
    public int nextPageNeedAddCount() {
        return 1;
    }

    @Override
    public String getWebUrl() {
        return webUrl;
    }
}
