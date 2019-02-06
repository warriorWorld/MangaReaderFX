import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import read.ReadController;
import sample.Controller;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
//        Parent root = FXMLLoader.load(getClass().getResource("read\\read.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("read\\read.fxml"));
        Parent root = fxmlLoader.load();
        //如果使用 Parent root = FXMLLoader.load(...) 静态读取方法，无法获取到Controller的实例对象
        ReadController controller = fxmlLoader.getController(); //获取Controller的实例对象

        primaryStage.setTitle("英文漫画阅读器");
//        primaryStage.setMaximized(true);
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        controller.setStage(primaryStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
