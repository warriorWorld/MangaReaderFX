package dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertDialog {
    public static void display(String message) {
        display("通知", message, "确定");
    }

    public static void display(String title, String message) {
        display(title, message, "确定");
    }

    public static void display(String title, String message, String okText) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        Label messageLb = new Label(message);
        Button closeBtn = new Button(okText);
        closeBtn.setPadding(new Insets(5, 10, 5, 10));
        closeBtn.setOnAction(event -> {
            window.close();
        });

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(messageLb, closeBtn);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(20, 20, 20, 20));

        Scene scene = new Scene(vBox);
        window.setScene(scene);
        window.showAndWait();
    }
}
