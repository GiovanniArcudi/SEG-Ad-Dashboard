package controller;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.utilities.GraphDataService;
import org.gillius.jfxutils.chart.ChartPanManager;
import org.gillius.jfxutils.chart.JFXChartUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

public class LineChart extends Graph {
    private NumberAxis x = new NumberAxis();
    private NumberAxis y = new NumberAxis();
    private String xLabel;
    private String yLabel;
    private javafx.scene.chart.LineChart<Number, Number> graph = new javafx.scene.chart.LineChart<Number, Number>(x, y);
    private XYChart.Series<Number, Number> series = new XYChart.Series<>();

    public LineChart(String yLabel, String xLabel) {
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        x.setLabel(xLabel);
        x.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return epochToDateString(number);
            }

            @Override
            public Long fromString(String s) {
                return LocalDate.parse(s).atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
        });
        x.setForceZeroInRange(false);
        y.setLabel(yLabel);
    }

    @Override
    public void updateGraph(Map data) {
        series.getData().clear();
        series = GraphDataService.getLineChartSeries(data);
        for (XYChart.Data<Number, Number> entry : series.getData()) {
            entry.setNode(new HoverValue());
            BigDecimal bd = BigDecimal.valueOf((double) entry.getYValue());
            // Round to 4 sig figs
            bd = bd.round(new MathContext(4));
            installTooltip(entry, bd);
        }
        graph.getData().clear();
        graph.setAnimated(true);
        graph.getData().add(series);
        graph.setAnimated(false);

        JFXChartUtil.setupZooming(graph, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() != MouseButton.PRIMARY ||
                        mouseEvent.isShortcutDown())
                    mouseEvent.consume();
            }
        });

        JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(graph);

        ChartPanManager panner = new ChartPanManager(graph);
        panner.setMouseFilter(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY ||
                        (mouseEvent.getButton() == MouseButton.PRIMARY &&
                                mouseEvent.isShortcutDown())) {
                } else {
                    mouseEvent.consume();
                }
            }
        });
        panner.start();
    }

    @Override
    public Chart getChart() {
        return graph;
    }

    @Override
    public Chart getClone() {
        NumberAxis xTemp = new NumberAxis();
        xTemp.setLabel(xLabel);
        xTemp.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return epochToDateString(number);
            }

            @Override
            public Long fromString(String s) {
                return LocalDate.parse(s).atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
        });
        xTemp.setForceZeroInRange(false);
        NumberAxis yTemp = new NumberAxis();
        yTemp.setLabel(yLabel);

        XYChart.Series<Number, Number> copy = new XYChart.Series<>(series.getName(), series.getData().stream().map(data -> getEntry(data.getXValue(), data.getYValue())).collect(Collectors.toCollection(FXCollections::observableArrayList)));

        javafx.scene.chart.LineChart<Number, Number> temp = new javafx.scene.chart.LineChart<Number, Number>(xTemp, yTemp);
        temp.getData().add(copy);

        return temp;
    }

    public XYChart.Data<Number, Number> getEntry(Number n1, Number n2) {
        XYChart.Data<Number, Number> entry = new XYChart.Data<Number, Number>(n1, n2);
        entry.setNode(new HoverValue());
        BigDecimal bd = BigDecimal.valueOf((double) entry.getYValue());
        installTooltip(entry, bd);

        return entry;
    }

    public void installTooltip(XYChart.Data<Number, Number> entry, BigDecimal bd) {
        Tooltip t = new Tooltip(bd.toPlainString() + "\n" + epochToDateString(entry.getXValue()));
        t.setTextAlignment(TextAlignment.CENTER);
        t.setStyle("-fx-background: rgba(30,30,30); -fx-text-fill: black; -fx-font-weight: bold; -fx-background-color: ALICEBLUE; " +
                "-fx-background-radius: 6px; -fx-background-insets: 0; -fx-padding: 3 3 3 3;" +
                "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.5) , 10, 0.0 , 0 , 3 ); -fx-font-size: 1em;");
        t.setShowDelay(Duration.seconds(0));
        Tooltip.install(entry.getNode(), t);
    }
}
