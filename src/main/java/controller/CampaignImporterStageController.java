package controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.utilities.PreferencesService;
import java.io.File;

public class CampaignImporterStageController {

    public Button cancelCampButton;
    private MainController mainController;
    private String filepath;

    @FXML
    AnchorPane anchorPane;
    @FXML
    private TextField campaignFilepath;
    @FXML
    private TextField impressionsLogField;
    @FXML
    private TextField clickLogField;
    @FXML
    private TextField serverLogField;
    @FXML
    private Button submitButton;
    @FXML
    private TextField campaignName;

    private boolean isAdvancedCampaign = false;

    @FXML
    public void initialize() {
        impressionsLogField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonStatus();
            isAdvancedCampaign = true;
        });
        clickLogField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonStatus();
            isAdvancedCampaign = true;
        });
        serverLogField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonStatus();
            isAdvancedCampaign = true;
        });
        campaignFilepath.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(false);
            isAdvancedCampaign = false;
        });
    }

    @FXML
    public void loadCampaign(ActionEvent event) {
        submitButton.setDisable(true);

        if (isAdvancedCampaign && clickLogField.getText() != null && serverLogField.getText() != null && impressionsLogField.getText() != null) {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            loadAdvancedCampaign(stage);
        } else if (filepath != null) {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();

            File clickLog = new File(filepath + "\\click_log.csv");
            File impressionsLog = new File(filepath + "\\impression_log.csv");
            File serverLog = new File(filepath + "\\server_log.csv");
            if (clickLog.exists() && impressionsLog.exists() && serverLog.exists()) {
                stage.close();
                mainController.createModelFromFilepath(campaignName.getText(), filepath);
            } else {
                MainController.showErrorAlert("One or more files were not found");
            }
        }
    }

    public void updateButtonStatus() {
        submitButton.setDisable(filepath == null && (impressionsLogField.getText() == null || clickLogField.getText() == null || serverLogField.getText() == null));
    }

    @FXML
    public void openFileChooser(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));

        try {
            filepath = directoryChooser.showDialog(stage).getPath();
            campaignFilepath.setText(filepath);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        updateButtonStatus();
    }

    @FXML
    public void openClickFile(ActionEvent event) {
        clickLogField.setText(openCsvFileChooser(event));
        updateButtonStatus();
    }

    @FXML
    public void openImpressionsFile(ActionEvent event) {
        impressionsLogField.setText(openCsvFileChooser(event));
        updateButtonStatus();
    }

    @FXML
    public void openServerFile(ActionEvent event) {
        serverLogField.setText(openCsvFileChooser(event));
        updateButtonStatus();
    }

    @FXML
    public void loadAdvancedCampaign(Stage stage) {

        File clickLog = new File(clickLogField.getText());
        File impressionsLog = new File(impressionsLogField.getText());
        File serverLog = new File(serverLogField.getText());

        if (clickLog.exists() && impressionsLog.exists() && serverLog.exists()) {
            stage.close();
            PreferencesService.saveCampaign(campaignName.getText(), clickLogField.getText(), impressionsLogField.getText(), serverLogField.getText());
            mainController.createModelFromFiles(campaignName.getText(), clickLogField.getText(), impressionsLogField.getText(), serverLogField.getText());
        } else {
            MainController.showErrorAlert("One or more files were not found");
        }

    }

    @FXML
    public void clearFolder() {
        if (campaignFilepath != null) {
            campaignFilepath.setText(null);
        }
    }

    @FXML
    public void clearFiles() {

        if (impressionsLogField != null) {
            impressionsLogField.setText(null);
        }

        if (clickLogField != null) {
            clickLogField.setText(null);
        }

        if (serverLogField != null) {
            serverLogField.setText(null);
        }
    }


    public String openCsvFileChooser(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src"));
        fileChooser.setTitle("Open File to Load");
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text files", "*.csv");
        fileChooser.getExtensionFilters().add(txtFilter);
        return fileChooser.showOpenDialog(stage).getPath();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Sets the text size of the Campaign Importer Dialog.
    public void setCampaignImporterDialogTextSize(DoubleProperty textSize) {
        anchorPane.styleProperty().bind(Bindings.format("-fx-font-size: %.0f;", textSize));
        anchorPane.styleProperty().unbind();
    }

    public void closeDialog() {
    }
}
