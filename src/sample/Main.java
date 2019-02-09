package sample;

import java.io.IOException;

import base.BaseController;
import bean.DownloadBean;
import configure.BaseParameterUtil;
import configure.Configure;
import download.DownloadMangaManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import read.ReadController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        initAllInstance();
//        Parent root = FXMLLoader.load(getClass().getResource("read\\read.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = fxmlLoader.load();
        //如果使用 Parent root = FXMLLoader.load(...) 静态读取方法，无法获取到Controller的实例对象
        BaseController controller = fxmlLoader.getController(); //获取Controller的实例对象
        Scene scene = new Scene(root, 800, 500);

        primaryStage.setTitle(Configure.NAME);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
        controller.setStage(primaryStage);
        controller.setScene(scene);
    }

    private void initAllInstance() {
        DownloadMangaManager.getInstance().getCurrentChapter();
        DownloadBean.getInstance().setDownloadInfo(DownloadBean.getInstance().getDownloadInfo());
        BaseParameterUtil.getInstance();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
