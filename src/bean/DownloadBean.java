package bean;/**
 * Created by Administrator on 2016/11/2.
 */


import java.util.ArrayList;

import configure.ShareKeys;
import spider.FileSpider;
import utils.ShareObjUtil;
import utils.TextUtils;


/**
 * 作者：苏航 on 2016/11/2 15:52
 * 邮箱：772192594@qq.com
 */
public class DownloadBean extends BaseBean {
    private MangaBean currentManga;
    private ArrayList<DownloadChapterBean> download_chapters;
    private String web_site;

    private DownloadBean() {
    }

    private static volatile DownloadBean instance = null;

    public static DownloadBean getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (DownloadBean.class) {
                //双重锁定
                if (instance == null) {
                    instance = new DownloadBean();
                }
            }
        }
        return instance;
    }

    public void setDownloadInfo(DownloadBean downloadInfo) {
        instance = downloadInfo;
        saveDownloadInfo(downloadInfo);
    }

    //这个最好只有在application类里调用一次(即刚进入应用时调用一次),其他情况直接用单例就好,调用这个效率太低了
    public DownloadBean getDownloadInfo() {
        DownloadBean res = (DownloadBean) ShareObjUtil.getObject(ShareKeys.DOWNLOAD_KEY);
        if (null != res) {
            return res;
        } else {
            return instance;
        }
    }

    private void saveDownloadInfo(DownloadBean downloadInfo) {
        ShareObjUtil.saveObject(downloadInfo, ShareKeys.DOWNLOAD_KEY);
    }

    public void clean() {
        instance = null;
        ShareObjUtil.deleteFile(ShareKeys.DOWNLOAD_KEY);
    }

    public MangaBean getCurrentManga() {
        return currentManga;
    }

    public void setMangaBean(MangaBean mangaBean) {
        this.currentManga = mangaBean;
        saveDownloadInfo(instance);
    }

    public String getWebSite() {
        return web_site;
    }

    public void setWebSite(String webSite) {
        this.web_site = webSite;
        saveDownloadInfo(instance);
    }

    public ArrayList<DownloadChapterBean> getDownload_chapters() {
        return download_chapters;
    }

    public void initDownloadChapters() {
        this.download_chapters = getDownloadChapters();
    }

    public void setDownload_chapters(ArrayList<DownloadChapterBean> down_chapters) {
        this.download_chapters = down_chapters;
        saveDownloadInfo(instance);
    }

    private ArrayList<DownloadChapterBean> getDownloadChapters() {
        try {
            ArrayList<DownloadChapterBean> list = new ArrayList<>();
            for (int i = 0; i < currentManga.getChapters().size(); i++) {
                DownloadChapterBean item = new DownloadChapterBean();
                item.setChapter_url(currentManga.getChapters().get(i).getChapterUrl());
                item.setChapter_child_folder_name(FileSpider.getInstance().getChildFolderName(
                        Integer.valueOf(currentManga.getChapters().get(i).getChapterPosition()), 3));
                item.setChapter_title(currentManga.getChapters().get(i).getChapterPosition());
                list.add(item);
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public String initMangaFileName() {
        String mangaFileName = currentManga.getName();
        mangaFileName = getWordAgain(mangaFileName);
        if (mangaFileName.length() > 32) {
            mangaFileName = mangaFileName.substring(0, 32);
        }
        return mangaFileName;
    }

    private String getWordAgain(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】\"‘；：”“’。，、？|]", "");
        //留着空格
//        str = str.replaceAll("\n", "");
//        str = str.replaceAll("\r", "");
//        str = str.replaceAll("\\s", "");
        str = str.replaceAll("_", "");
        return str;
    }
}
