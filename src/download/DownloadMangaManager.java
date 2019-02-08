
package download;


import com.sun.imageio.plugins.common.ImageUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import bean.DownloadBean;
import bean.DownloadChapterBean;
import bean.DownloadPageBean;
import bean.MangaBean;
import configure.Configure;
import configure.ShareKeys;
import dialog.AlertDialog;
import javafx.scene.image.Image;
import listener.JsoupCallBack;
import okhttp.HttpBack;
import okhttp.HttpService;
import spider.SpiderBase;
import utils.ImgUtil;
import utils.ShareObjUtil;

/**
 * userInfo管理类
 * <p>
 *
 * @author Administrator
 */
public class DownloadMangaManager {
    private DownloadChapterBean currentChapter;
    private SpiderBase spider;
    private boolean stopDownload = false;
    private String mangaName;

    private DownloadMangaManager() {
    }

    private static volatile DownloadMangaManager instance = null;

    public static DownloadMangaManager getInstance() {
        if (instance == null) {
            synchronized (DownloadMangaManager.class) {
                if (instance == null) {
                    instance = new DownloadMangaManager();
                }
            }
        }
        return instance;
    }


    public void doDownload() {
        stopDownload = false;
        if ((null == DownloadBean.getInstance().getDownload_chapters() ||
                DownloadBean.getInstance().getDownload_chapters().size() <= 0) &&
                (null == currentChapter || null == currentChapter.getPages())) {
            //没有章节了
            //下载完成
            reset();
//            EventBus.getDefault().post(new DownLoadEvent(EventBusEvent.DOWNLOAD_FINISH_EVENT));
            return;
        }
        if (null == currentChapter || null == currentChapter.getPages()) {
            //当前章节空了的时候 当前章节赋值为新的章节 移除空章节
            currentChapter = DownloadBean.getInstance().getDownload_chapters().get(0);
            saveCurrentChapter();
            ArrayList<DownloadChapterBean> temp = DownloadBean.getInstance().getDownload_chapters();
            temp.remove(0);
            DownloadBean.getInstance().setDownload_chapters(temp);

            MangaBean tempMangaBean = DownloadBean.getInstance().getCurrentManga();
            //mangabean也得remove
            tempMangaBean.getChapters().remove(0);
            DownloadBean.getInstance().setMangaBean(tempMangaBean);
//            EventBus.getDefault().post(new DownLoadEvent(EventBusEvent.DOWNLOAD_CHAPTER_START_EVENT));
        }

        mangaName = DownloadBean.getInstance().initMangaFileName();
        if (null == spider) {
            initSpider();
        }
        if (null != currentChapter.getPages() && currentChapter.getPages().size() > 0) {
            //说明上次那一话还没下载完  继续上次下载
            download1Img(currentChapter.getPages().get(0).getPage_url(), mangaName + "/" +
                    currentChapter.getChapter_child_folder_name() + "/" + currentChapter.getPages().get(0).getPage_file_name());
        } else {
            //下载新的章节
            spider.getMangaChapterPics(currentChapter.getChapter_url(), new JsoupCallBack<ArrayList<String>>() {
                @Override
                public void loadSucceed(ArrayList<String> result) {
                    currentChapter.setChapter_size(result.size());
                    ArrayList<DownloadPageBean> pages = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {//循环启动任务
                        DownloadPageBean item = new DownloadPageBean();
                        item.setPage_file_name(mangaName + "_" +
                                currentChapter.getChapter_title()
                                + "_" + i + ".png");
                        item.setPage_url(result.get(i));
                        pages.add(item);
                    }
                    currentChapter.setPages(pages);
                    download1Img(currentChapter.getPages().get(0).getPage_url(), mangaName + "/" +
                            currentChapter.getChapter_child_folder_name() + "/" + currentChapter.getPages().get(0).getPage_file_name());
                }

                @Override
                public void loadFailed(String error) {
                }
            });
        }
    }

    public void download1Img(String imgUrl, String fileName) {
        if (stopDownload) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Image image;
                    image = ImgUtil.createImage(imgUrl);
                    ImgUtil.saveToFile(image, Configure.DOWNLOAD_PATH + "/"+fileName);
                    refreshCurrentPagesInfo(imgUrl);
                    download1Img(currentChapter.getPages().get(0).getPage_url(), mangaName + "/" +
                            currentChapter.getChapter_child_folder_name() + "/" + currentChapter.getPages().get(0).getPage_file_name());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initSpider() {
        try {
            spider = (SpiderBase) Class.forName("spider." + DownloadBean.getInstance().getWebSite() + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


    public void refreshCurrentPagesInfo(String url) {
        try {
//            EventBus.getDefault().post(new DownLoadEvent(EventBusEvent.DOWNLOAD_PAGE_FINISH_EVENT));
            if (currentChapter.getPages().size() == 1 && currentChapter.getPages().get(0).getPage_url().equals(url)) {
                //下载完一个章节
                currentChapter = null;
                doDownload();
                return;
            }
            for (int i = 0; i < currentChapter.getPages().size(); i++) {
                if (url.equals(currentChapter.getPages().get(i).getPage_url())) {
                    currentChapter.getPages().remove(i);
                    saveCurrentChapter();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stopDownload() {
        stopDownload = true;
    }

    public void reset() {
        stopDownload();
        DownloadBean.getInstance().clean();
        currentChapter = null;
        ShareObjUtil.deleteFile(ShareKeys.CURRENT_CHAPTER_KEY);
    }

    //这个最好只有在application类里调用一次(即刚进入应用时调用一次),其他情况直接用单例就好,调用这个效率太低了
    //因为我一直在不断的给currentChapter赋值和置空并以他是否为空做进一步判断 而这个方法又会给currentChapter赋值 所以这个东西只允许App调用一次
    public DownloadChapterBean getCurrentChapter() {
        if (null != currentChapter) {
            return currentChapter;
        }
        currentChapter = (DownloadChapterBean) ShareObjUtil.getObject(ShareKeys.CURRENT_CHAPTER_KEY);
        return currentChapter;
    }

    public void saveCurrentChapter() {
        ShareObjUtil.saveObject(currentChapter, ShareKeys.CURRENT_CHAPTER_KEY);
    }
}
