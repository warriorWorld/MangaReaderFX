package bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/18.
 */

public class ChapterBean extends BaseBean {
    private String chapterUrl;//章节的地址
    private String chapterPosition;

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
}
