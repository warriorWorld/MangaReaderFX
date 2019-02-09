package listener;

import javafx.scene.input.MouseEvent;

public interface OnItemClickListener {
    void onClick(int position);

    void onRightClick(int position);

    void onMouseEvent(int position,MouseEvent event);
}
