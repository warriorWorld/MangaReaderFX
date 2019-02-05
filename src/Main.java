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

public class Main extends Application {
    private org.jsoup.nodes.Document doc;
    private Text text;

    @Override
    public void start(Stage primaryStage) throws IOException{
        Parent root= FXMLLoader.load(getClass().getResource("read\\read.fxml"));
        primaryStage.setTitle("标题");
//        primaryStage.setMaximized(true);
        primaryStage.setScene(new Scene(root,800,500));
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void jsoup() {
        try {
            doc = Jsoup.connect("http://jandan.net/duan/page-91#comments")
                    .timeout(10000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null != doc) {
            Element mangaListElements = doc.body().getElementById("wrapper").getElementById("body")
                    .getElementById("content").getElementById("comments");
            Elements eles = mangaListElements.getElementsByClass("commentlist").last().
                    getElementsByTag("li");
            String result="";
            for (Element ele : eles) {
//                System.out.println(ele.select("div.text").text());
                result+=ele.select("div.text").text()+"\n";
            }
            text.setText(result);
        }
    }
}
