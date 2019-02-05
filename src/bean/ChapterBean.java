package bean;

/**
 * Created by Administrator on 2017/7/18.
 */

public class ChapterBean extends BaseBean {
    private String chapterUrl;//章节的地址
    private String chapterPosition;
    private String localThumbnailUrl;
    private String webThumbnailUrl;

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getChapterPosition() {
        return chapterPosition;
    }

    public void setChapterPosition(String chapterPosition) {
        this.chapterPosition = chapterPosition;
    }

    public String getLocalThumbnailUrl() {
        return localThumbnailUrl;
    }

    public void setLocalThumbnailUrl(String localThumbnailUrl) {
        this.localThumbnailUrl = localThumbnailUrl;
    }

    public String getWebThumbnailUrl() {
        return webThumbnailUrl;
    }

    public void setWebThumbnailUrl(String webThumbnailUrl) {
        this.webThumbnailUrl = webThumbnailUrl;
    }
}
