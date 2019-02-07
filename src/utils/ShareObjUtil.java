package utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ShareObjUtil {
    /**
     * @param name 文件名
     * @return
     * @description 获取object
     * @author Skyin_wd
     * 上下文
     */
    public static Object getObject(String name) {
        Object obj = null;
        try {
            FileInputStream fis = new FileInputStream(name);
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj = ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * @param obj  对象
     * @param name 文件名
     * @description 保存 object
     * @author Skyin_wd
     * 上下文
     */
    public static void saveObject(Object obj, String name) {
        try {
            FileOutputStream fos = new FileOutputStream(name);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param name 文件名
     * @return
     * @description 删除文件
     * @author Skyin_wd
     */
    public static void deleteFile(String name) {
        try {
            if (TextUtils.isEmpty(name)) {
                return;
            }
            File f = new File(name);
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
