package spider;

import listener.SpiderCallBack;

/**
 * Created by Administrator on 2017/7/18.
 */

public abstract class SpiderBase {
    protected org.jsoup.nodes.Document doc;

    public enum SearchType {
        BY_MANGA_NAME,
        BY_MANGA_AUTHOR,
        BY_COMMENT,
        BY_GRADE
    }

    public abstract <ResultObj> void getMangaList(String type, String page, final SpiderCallBack<ResultObj> spiderCallBack);

    public abstract <ResultObj> void getMangaDetail(final String mangaURL, final SpiderCallBack<ResultObj> spiderCallBack);

    public abstract boolean isOneShot();

    public abstract String[] getMangaTypes();

    public abstract String[] getMangaTypeCodes();

    public abstract <ResultObj> void getMangaChapterPics
            (final String chapterUrl, final SpiderCallBack<ResultObj> spiderCallBack);

    public abstract <ResultObj> void getSearchResultList(SearchType type, String keyWord, final SpiderCallBack<ResultObj> spiderCallBack);

    //很多网页的下一页并不是在网址后+1 而是+n
    public abstract int nextPageNeedAddCount();

    public abstract String getWebUrl();
}
