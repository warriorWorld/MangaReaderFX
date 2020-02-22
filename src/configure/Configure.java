package configure;


import java.io.File;
import java.util.prefs.Preferences;

import utils.TextUtils;

/**
 * Created by Administrator on 2017/7/19.
 */

public class Configure {
    public final static boolean isTest = true;
    public final static String VERSION = "1.0";
    //数据库版本号
    public static final int DB_VERSION = 1;
    public final static String[] websList = {"KaKaLot", "MangaReader", "Owl"};
    public final static String[] masterWebsList = {"MangaReader", "NManga", "KaKaLot", "LManga"};
    public final static String[] VPN_MUST_LIST = {"NOTHING"};
    private final static String DST_FOLDER_NAME = "Manga";
    final private static String DOWNLOAD_PATH = "E:/" + DST_FOLDER_NAME;
    ;
    final public static String WRONG_WEBSITE_EXCEPTION = "wrong_website_exception";
    final public static String YOUDAO = "http://fanyi.youdao.com/openapi.do?keyfrom=mangaeasywa" +
            "tch&key=986400551&type=data&doctype=json&version=1.1&q=";
    final public static String NAME = "英文漫画阅读器";
    final public static String LOADING = "(加载中...)";
    final public static String LOAD_FAILED = "(加载失败)";
    // 3DES加密key
    final public static String key = "iq2szojof6x1ckgejwe52urw";
    //收藏类型
    final public static int COLLECT_TYPE_COLLECT = 0;
    final public static int COLLECT_TYPE_WAIT_FOR_UPDATE = 1;
    final public static int COLLECT_TYPE_FINISHED = 2;

    public static String getMangaDirectory() {
        Preferences mPreferences = Preferences.userRoot();
        if (TextUtils.isEmpty(mPreferences.get(ShareKeys.MANGA_DIRECTORY_KEY, ""))) {
            return DOWNLOAD_PATH;
        } else {
            return mPreferences.get(ShareKeys.MANGA_DIRECTORY_KEY, "");
        }
    }

    public static String getCacheDirectory() {
        return getMangaDirectory() + File.separator + "cache";
    }
}
