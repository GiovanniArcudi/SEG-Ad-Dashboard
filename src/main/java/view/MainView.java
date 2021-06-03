package view;

import controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.MainModel;
import model.utilities.PreferencesService;

import java.io.IOException;

public class MainView extends Application {

    public Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Setting up FXML loader and style's css file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = fxmlLoader.load();
        root.getStylesheets().add("/styles.css");
        MainController controller = fxmlLoader.getController();

        // Connecting MainController to MainModel and MainView
        if(PreferencesService.hasPreviousCampaign()) {
            controller.showProgressIndicator(true);
            new Thread(() -> {
                try {
                    controller.setModel(new MainModel(PreferencesService.getPreviousName(), PreferencesService.getClickLog(), PreferencesService.getImpressionLog(), PreferencesService.getServerLog()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    controller.showProgressIndicator(false);
                });
            }).start();
            if(!PreferencesService.getPreviousName().equals("")) {
                controller.setCampaignName(PreferencesService.getPreviousName());
            }
        }

        // Configuring primary stage style
        this.primaryStage = primaryStage;
        primaryStage.setTitle(" Ad Dashboard");
        primaryStage.getIcons().add(new Image("file:src/main/resources/DashboardIcon.png"));
        primaryStage.setScene(new Scene(root, 300, 300));

        // Configuring primary stage's dimensions
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setMaximized(true);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(900);

        primaryStage.show();
    }
}