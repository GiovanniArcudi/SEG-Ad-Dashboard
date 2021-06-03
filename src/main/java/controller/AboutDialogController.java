package controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class AboutDialogController {

    @FXML
    AnchorPane anchorPane;

    public void setAboutDialogTextSize(DoubleProperty textSize) {
        anchorPane.styleProperty().bind(Bindings.format("-fx-font-size: %.0f;", textSize));
        anchorPane.styleProperty().unbind();
    }
}
