package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Chart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

public abstract class Graph extends Node {

    public String epochToDateString(Number epoch) {
        LocalDate date =
                Instant.ofEpochSecond(epoch.longValue()).atZone(ZoneId.systemDefault()).toLocalDate();
        return date.toString();
    }

    public Chart initialiseGraph() {
        Chart graph = getChart();
        graph.setLegendVisible(false);
        HBox.setHgrow(graph, Priority.ALWAYS);
        VBox.setVgrow(graph, Priority.ALWAYS);
        graph.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        graph.setStyle("-fx-padding: 0 15 0 0;");
        return graph;
    }

    public void openInWindow(String title, String s, Scene scene) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #23272a;");
        root.getStylesheets().add("/styles.css");
        Chart chart = getClone();
        chart.setLegendVisible(false);
        chart.setTitle(chart.getTitle());
        chart.setStyle("-fx-padding: 10 25 0 0;");
        root.setCenter(chart);
        Label l = new Label("Filters selected: " + s);
        l.setStyle("-fx-padding: 0 0 10 10; -fx-font-size: 13px;");
        root.setBottom(l);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(new Image("file:src/main/resources/DashboardIcon.png"));
        stage.setScene(new Scene(root, scene.getWidth()*0.75, scene.getHeight()*0.75));
        stage.show();
    }

    public abstract void updateGraph(Map data);

    public abstract Chart getChart();

    public abstract Chart getClone();

    public void saveAsPng(Scene scene) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files (*.png)", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files (*.jpg)", "*.jpg"));

        File file = fileChooser.showSaveDialog(scene.getWindow());

        if (file != null) {
            try {
                WritableImage image = getChart().snapshot(new SnapshotParameters(), null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public Node getStyleableNode() {
        return null;
    }

    static class HoverValue extends StackPane {
        public HoverValue() {
            setPrefSize(12, 12);
            setOnMouseEntered(mouseEvent -> setStyle("-fx-background-color: #5669a8;"));
            setOnMouseExited(mouseEvent -> setStyle(null));
        }
    }
}