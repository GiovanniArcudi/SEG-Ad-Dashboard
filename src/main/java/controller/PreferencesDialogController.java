package controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import static java.lang.Math.round;

public class PreferencesDialogController {

    @FXML
    Slider slider;

    @FXML
    TextField textField;

    @FXML
    Button apply;

    @FXML
    AnchorPane anchorPane;

    private MainController mainController;

    @FXML
    //text size change if a new size is typed in the text field
    public void changeToTypedFontSize(Event event) {
        slider.setValue(Double.parseDouble(textField.getText()));

        textField.styleProperty().bind(Bindings.format("-fx-font-size: %.0f;", slider.valueProperty()));
        textField.styleProperty().unbind();

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.sizeToScene();
    }

    @FXML
    //text size change while the slider is dragged
    public void changeToSliderDragged(Event event) {
        Double value = (double) round(slider.getValue());
        textField.setText(String.valueOf(value));
        textField.styleProperty().bind(Bindings.format("-fx-font-size: %.0f;", slider.valueProperty()));
        textField.styleProperty().unbind();

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.sizeToScene();
    }

    @FXML
    // text change change while points on the slider bar are clicked
    public void changeToSliderClicked(Event event) {
        textField.setText(String.valueOf(slider.getValue()));
        textField.styleProperty().bind(Bindings.format("-fx-font-size: %.0f;", slider.valueProperty()));
        textField.styleProperty().unbind();

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.sizeToScene();
    }

    @FXML
    //click the cancel can close the window
    public void closeDialog(Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    //change the font size at the dialog
    public void applyAndConfirm(MouseEvent mouseEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        fxmlLoader.load();

        mainController.updateGlobalFontSize(slider.valueProperty());

        closeDialog(mouseEvent);
    }

    public void setPreferencesDialogTextSize(DoubleProperty textSize) {
        anchorPane.styleProperty().bind(Bindings.format("-fx-font-size: %.0f;", textSize));
        anchorPane.styleProperty().unbind();

        // Shows current text size value in the slider and the text field
        slider.setValue(textSize.getValue());
        textField.setText(textSize.getValue().toString());
    }

    // Connects PreferencesDialogController to MainController.
    public void setMainController (MainController mainController) {
        this.mainController = mainController;
    }
}
