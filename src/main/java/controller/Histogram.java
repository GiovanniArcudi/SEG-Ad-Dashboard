package controller;

import javafx.collections.FXCollections;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import model.utilities.GraphDataService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class Histogram extends Graph {
    private final CategoryAxis x = new CategoryAxis();
    private final NumberAxis y = new NumberAxis();
    private final BarChart<String, Number> graph = new BarChart<>(x, y);
    private XYChart.Series<String, Number> series = new XYChart.Series<>();

    public Histogram() {
        x.setLabel("Click Cost");
        y.setLabel("Frequency");
    }

    public String epochToDateString(Number epoch) {
        LocalDate date =
                Instant.ofEpochSecond(epoch.longValue()).atZone(ZoneId.systemDefault()).toLocalDate();
        return date.toString();
    }

    @Override
    public Chart getChart() {
        return graph;
    }

    @Override
    public Chart getClone() {
        CategoryAxis xTemp = new CategoryAxis();
        xTemp.setLabel("Click Cost");
        NumberAxis yTemp = new NumberAxis();
        yTemp.setLabel("Frequency");
        BarChart<String, Number> temp = new BarChart<>(xTemp, yTemp);
        XYChart.Series<String, Number> copy = new XYChart.Series<>(series.getName(), series.getData().stream().map(data -> getEntry(data.getXValue(), data.getYValue())).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        temp.getData().add(copy);

        return temp;
    }

    public XYChart.Data<String, Number> getEntry(String s, Number n) {
        XYChart.Data<String, Number> entry = new XYChart.Data<>(s, n);
        entry.setNode(new HoverValue());
        installTooltip(entry);

        return entry;
    }

    public void installTooltip(XYChart.Data entry) {
        Tooltip t = new Tooltip(entry.getYValue().toString() + "\n" + entry.getXValue());
        t.setTextAlignment(TextAlignment.CENTER);
        t.setStyle("-fx-background: rgba(30,30,30); -fx-text-fill: black; -fx-font-weight: bold; -fx-background-color: ALICEBLUE; " +
                "-fx-background-radius: 6px; -fx-background-insets: 0; -fx-padding: 3 3 3 3;" +
                "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.5) , 10, 0.0 , 0 , 3 ); -fx-font-size: 1em;");
        t.setShowDelay(Duration.seconds(0));
        Tooltip.install(entry.getNode(), t);
    }

    public void updateGraph(Map data) {
        series.getData().clear();
        series = GraphDataService.getHistogramSeries(data);

        for (XYChart.Data<String, Number> entry : series.getData()) {
            entry.setNode(new HoverValue());
            installTooltip(entry);
        }

        series.getData().sort((Comparator<XYChart.Data>) (o1, o2) -> {
            String xValue1 = (String) o1.getXValue();
            String xValue2 = (String) o2.getXValue();
            return Double.compare(Double.parseDouble(xValue1), Double.parseDouble(xValue2));
        });

        graph.getData().clear();
        graph.setAnimated(true);
        graph.getData().add(series);
        graph.setAnimated(false);
    }
}