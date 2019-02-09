package configure;

import java.util.prefs.Preferences;

import utils.TextUtils;


/**
 * Created by Administrator on 2017/11/11.
 */

public class BaseParameterUtil {
    private Preferences mPreferences;

    private BaseParameterUtil() {
        mPreferences = Preferences.userRoot();
    }

    private static volatile BaseParameterUtil instance = null;

    public static BaseParameterUtil getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (BaseParameterUtil.class) {
                //双重锁定
                if (instance == null) {
                    instance = new BaseParameterUtil();
                }
            }
        }
        return instance;
    }

    public String getCurrentWebSite() {
        String website = mPreferences.get(ShareKeys.CURRENT_WEBSITE, "");
        if (TextUtils.isEmpty(website)) {
            website = Configure.websList[0];
            mPreferences.put(ShareKeys.CURRENT_WEBSITE, website);
        }
        return website;
    }

    public void saveCurrentWebSite(String website) {
        mPreferences.put(ShareKeys.CURRENT_WEBSITE, website);
    }

    public String getCurrentType() {
        String res = mPreferences.get(ShareKeys.CURRENT_TYPE, "");
        if (TextUtils.isEmpty(res)) {
            res = "all";
            mPreferences.put(ShareKeys.CURRENT_TYPE, res);
        }
        return res;
    }

    public void saveCurrentType(String string) {
        mPreferences.put(ShareKeys.CURRENT_TYPE, string);
    }

    public int getCurrentPage() {
        int res = mPreferences.getInt(ShareKeys.CURRENT_PAGE, 1);
        return res;
    }

    public void saveCurrentPage(int res) {
        mPreferences.putInt(ShareKeys.CURRENT_PAGE, res);
    }
}
