package read;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ResourceBundle;


import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import utils.TextUtils;

public class ReadController implements Initializable {
    public ImageView mIv;
    public ScrollPane mScrollPane;
    public Label currentInputLb;
    private String currentInput = "";
    private Clipboard mClipboard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        initUI();
    }

    private void initUI() {
        mIv.setPreserveRatio(true);
        mIv.setImage(new Image("http://ww3.sinaimg.cn/mw600/0073ob6Pgy1fzvjk6zrlkj30u0140x6p.jpg"));
        mScrollPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                mIv.setImage(new Image("http://ws4.sinaimg.cn/mw600/7ca81805gy1fzvloygezfj20m80ci0uo.jpg"));
                System.out.println(event.getCode());
                if (event.getCode().toString().length() == 1) {
                    currentInput += event.getCode().toString();
                } else if (event.getCode().toString().equals("ENTER")) {
                    mClipboard.setContents(new StringSelection(currentInput), null);
                    currentInput = "";
                } else if (event.getCode().toString().equals("BACK_SPACE")) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                } else if (event.getCode().toString().equals("SPACE")) {
                    event.consume();
                    currentInput += " ";
                }
                if (TextUtils.isEmpty(currentInput)) {
                    currentInputLb.setText("输入单词");
                } else {
                    currentInputLb.setText(currentInput);
                }
            }
        });
    }

    public void handleZoom() {
        System.out.println("handle zoom");
    }
}
