package dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listener.EditResultListener;

public class EditDialog {
    public static void display(String title, String message, String okText, EditResultListener listener) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        TextField inputField=new TextField();
        Label messageLb = new Label(message);
//        inputField.setPromptText(message);
        Button closeBtn = new Button(okText);
        closeBtn.setOnAction(event -> {
            if (null!=listener){
                listener.onResult(inputField.getText());
            }
            window.close();
        });

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(messageLb,inputField, closeBtn);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(20,20,20,20));

        Scene scene = new Scene(vBox);
        window.setScene(scene);
        window.show();
    }
}
