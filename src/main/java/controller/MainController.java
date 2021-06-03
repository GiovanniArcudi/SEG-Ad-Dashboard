package controller;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.MainModel;
import model.models.GraphOptions;
import model.models.enums.*;
import model.utilities.MetricService;
import model.utilities.PreferencesService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {

    @FXML
    StackPane mainPane;

    @FXML
    private VBox totalMetrics;

    @FXML
    private HBox filteredMetrics;

    @FXML
    private ComboBox<String> intervalChoiceBox;

    @FXML
    private CheckBox isCumulativeCheckBox;

    @FXML
    private VBox progressIndicatorContainer;

    @FXML
    private TextField campaignName;

    @FXML
    private TabPane tabPane;

    @FXML
    private ComboBox<String> bouncesChoiceBox;

    @FXML
    private HBox bouncesThresholdContainer;

    @FXML
    private TextField bouncesThreshold;

    @FXML
    private Button updateBtn;

    @FXML
    private VBox bounceSettings;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private VBox ageBracket;

    @FXML
    private VBox context;

    @FXML
    private VBox gender;

    @FXML
    private VBox income;

    @FXML
    private ScrollPane filtersScrollpane;

    @FXML
    private VBox granularityControls;

    private MainModel model;
    private DoubleProperty textSize = new SimpleDoubleProperty(12);
    private final HashMap<Tab, Boolean> visitedTabMap = new HashMap<>();
    private final HashMap<Tab, Graph> tabGraphMap = new HashMap<>();
    private final HashMap<Tab, GraphOptions> tabOptionsMap = new HashMap<>();
    private final HashMap<Tab, String[]> filteredMetricsMap = new HashMap<>();

    public void saveGraphAsPNG() throws IOException {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        tabGraphMap.get(currentTab).saveAsPng(mainPane.getScene());
    }

    public void openInWindow() {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        StringBuilder s = new StringBuilder();
        s.append("\n");
        GraphOptions options = tabOptionsMap.get(currentTab);
        String name = currentTab.getText();

        if (options.getStart() != null && options.getEnd() != null) {
            s.append("Start: ").append(options.getStart()).append("\n");
            s.append("End: ").append(options.getEnd()).append("\n");
        }

        s.append("Cumulative: ").append(options.isCumulative()).append("\n");
        s.append("Graph Granularity: ").append(intervalChoiceBox.getValue()).append("\n");

        if (name.equals("Bounce Rate") || name.equals("Bounces")) {
            s.append("Bounce Definition: ").append(options.getBounceDefinition()).append("\n");
        }

        if (name.equals("Impressions") || name.equals("CPM") || name.equals("CTR") || name.equals("Cost")) {
            s.append("Age Brackets Selected: ").append(options.getAgeFilter()).append("\n");
            s.append("Contexts Selected: ").append(options.getContextFilter()).append("\n");
            s.append("Genders Selected: ").append(options.getGenderFilter()).append("\n");
            s.append("Income Brackets Selected: ").append(options.getIncomeFilter());
        }

        tabGraphMap.get(currentTab).openInWindow(intervalChoiceBox.getValue() + " " + currentTab.getText(), s.toString().replace("[", "").replace("]", "").replace("_", " "), mainPane.getScene());
    }

    @FXML
    public void initialize() {

        buildTabGraphMapping();
        // Add listeners to update the options based on the current tab
        tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, number, t1) -> {
            GraphOptions options = tabOptionsMap.get(t1);

            // Disable certain controls based on the current tab
            switch (t1.getText()) {
                case "Bounces":
                case "Bounce Rate":
                    bounceSettings.setDisable(false);
                    bouncesChoiceBox.setValue(BounceDefinition.toString(options.getBounceDefinition()));
                    bouncesThreshold.setText(options.getBounceThreshold().toString());
                    filtersScrollpane.setDisable(true);
                    granularityControls.setDisable(false);
                    break;
                case "Impressions":
                case "CPM":
                case "CTR":
                case "Cost":
                    filtersScrollpane.setDisable(false);
                    granularityControls.setDisable(false);
                    break;
                case "Click Cost Histogram":
                    granularityControls.setDisable(true);
                    filtersScrollpane.setDisable(true);
                    break;
                default:
                    granularityControls.setDisable(false);
                    filtersScrollpane.setDisable(true);
                    break;
            }

            if (t1.getText().equals("Bounces") || t1.getText().equals("Bounce Rate")) {
                // nothing
            } else {
                bounceSettings.setDisable(true);
            }

            isCumulativeCheckBox.setSelected(options.isCumulative());
            intervalChoiceBox.setValue(TimeInterval.toString(options.getGranularity()));

            if (options.getStart() != null) {
                startDate.setValue(options.getStart().toLocalDate());
            } else {
                startDate.setValue(null);
            }
            if (options.getEnd() != null) {
                startDate.setValue(options.getEnd().toLocalDate());
            } else {
                endDate.setValue(null);
            }

            // They haven't visited this tab before so create the graph

            progressIndicatorContainer.setVisible(true);
            new Thread(() -> {
                if (!visitedTabMap.containsKey(t1)) {
                    visitedTabMap.put(t1, true);
                    updateGraph();
                }
                updateFilterGUI();
                loadFilteredMetrics();
                Platform.runLater(() -> {
                    progressIndicatorContainer.setVisible(false);
                });
            }).start();

        });
        isCumulativeCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).setCumulative(t1);
        });

        // Listeners to update the mapping of tab to graph options
        startDate.getEditor().focusedProperty().addListener((observableValue, s, t1) -> {
            try {
                LocalDate selection = startDate.getConverter().fromString(startDate.getEditor().getText());
                startDate.setValue(startDate.getConverter().fromString(startDate.getEditor().getText()));
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).setStart(selection);
            } catch (DateTimeParseException ignored) {

            }
        });

        endDate.getEditor().focusedProperty().addListener((observableValue, s, t1) -> {
            try {
                LocalDate selection = endDate.getConverter().fromString(endDate.getEditor().getText());
                endDate.setValue(selection);
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).setEnd(selection);
            } catch (DateTimeParseException ignored) {

            }
        });

        bouncesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            // Check if they have selected to define bounces with a threshold
            if (t1.equals(0)) {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).setBounceDefinition(BounceDefinition.SINGLE_PAGE);
            } else {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).setBounceDefinition(BounceDefinition.THRESHOLD);
            }
            bouncesThresholdContainer.setDisable(t1.equals(0));
            updateBtn.setDisable(!t1.equals(0) && bouncesThreshold.getText().length() == 0);
        });

        bouncesThreshold.textProperty().addListener((observableValue, oldValue, newValue) -> {
            System.out.println(newValue);
            try {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).setBounceThreshold(Double.parseDouble(newValue));
            } catch (NumberFormatException ignored) {

            }

            updateBtn.setDisable(bouncesThreshold.getText().length() == 0);
        });
        intervalChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).setGranularity(TimeInterval.fromString(t1));
        });
    }

    private void buildTabGraphMapping() {
        tabGraphMap.clear();
        tabOptionsMap.clear();
        tabPane.getTabs().forEach(tab -> {
            tabOptionsMap.put(tab, new GraphOptions());
            if (tab.getText().equals("Click Cost Histogram")) {
                tabGraphMap.put(tab, new Histogram());
            } else {
                tabGraphMap.put(tab, new LineChart(tab.getText(), "Date"));
            }
        });
        visitedTabMap.clear();
        visitedTabMap.put(tabPane.getSelectionModel().getSelectedItem(), true);
        Platform.runLater(() -> {
            for (Map.Entry<Tab, Graph> entry : tabGraphMap.entrySet()) {
                Tab tab = entry.getKey();
                Graph graph = entry.getValue();
                Label graphLabel = new Label(TimeInterval.toString(tabOptionsMap.get(tab).getGranularity()) + " " + tab.getText());
                graphLabel.setStyle("-fx-font-size: 16px");
                VBox vbox = new VBox(graphLabel, graph.initialiseGraph());
                vbox.setAlignment(Pos.CENTER);
                vbox.setPadding(new Insets(10));
                tab.setContent(vbox);
            }
        });
    }

    public void updateGraphLabel(Tab tab) {
        VBox vb = (VBox) tab.getContent();
        Label graphLabel = (Label) vb.getChildren().get(0);
        Platform.runLater(
                () -> {
                    graphLabel.setText(TimeInterval.toString(tabOptionsMap.get(tab).getGranularity()) + " " + tab.getText());
                }
        );
    }

    @FXML
    public void resetDateRange() {
        startDate.setValue(null);
        startDate.getEditor().setText("");
        endDate.getEditor().setText("");
        endDate.setValue(null);
        GraphOptions options = tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem());
        options.setStart(null);
        options.setEnd(null);
        graphOptionsChanged();
    }

    public void createModelFromFilepath(String name, String filepath) {
        progressIndicatorContainer.setVisible(true);
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CompletableFuture.supplyAsync(() -> new MainModel(name, filepath + "/click_log.csv", filepath + "/impression_log.csv", filepath + "/server_log.csv"), pool)
                .thenAccept(this::setModel)
                .thenRun(() ->
                {
                    if (!model.isError()) {
                        PreferencesService.saveCampaign(name, filepath + "\\click_log.csv", filepath + "\\impression_log.csv", filepath + "\\server_log.csv");
                        buildTabGraphMapping();
                        if (name == null || name.length() < 1) {
                            campaignName.setText(filepath);
                        } else {
                            campaignName.setText(name);
                        }
                    }
                });

        pool.shutdownNow();
    }

    public static void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(message);
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image("file:src/code/resources/Error.png"));
            alert.showAndWait();
        });

    }


    public void createModelFromFiles(String name, String click, String impression, String server) {
        String filepath = (click.substring(0, click.length() - 13));

        progressIndicatorContainer.setVisible(true);

        ExecutorService pool = Executors.newFixedThreadPool(10);
        CompletableFuture.supplyAsync(() -> new MainModel(name, click, impression, server), pool).thenAccept(this::setModel).thenRun(() -> {
            if (!model.isError()) {
                PreferencesService.saveCampaign(name, click, impression, server);
                buildTabGraphMapping();
                if (name == null || name.length() < 1) {
                    campaignName.setText(filepath);
                } else {
                    campaignName.setText(name);
                }
            }
        });
        pool.shutdownNow();
    }

    /**
     * Connects the {@link MainController} to {@link MainModel}.
     *
     * @param model The "Model" of our MVC architecture.
     */
    public void setModel(MainModel model) {
        this.model = model;
        if (model.isError()) {
            showErrorAlert("One or more files have incorrect format");
        }
        initialiseGUI();
    }

    private void initialiseGUI() {
        showProgressIndicator(true);
        new Thread(() -> {
            initialiseFilteredMetrics();
            updateTotalMetrics();
            updateGraph();
            Platform.runLater(() -> {
                showProgressIndicator(false);
            });
        }).start();
    }

    public void showProgressIndicator(boolean show) {
        progressIndicatorContainer.setVisible(show);
    }

    public void setCampaignName(String text) {
        campaignName.setText(text);
    }

    /**
     * These functions load metrics in both the side bar and top panel. They should be run after changing graph settings and at initialisation.
     * The order is the order they appear in the FXML. Ensure they are the same order in both the side bar and top panel.
     */

    private void updateTotalMetrics() {
        GraphOptions options = new GraphOptions();
        Platform.runLater(() -> {
            for (int i = 0; i < totalMetrics.getChildren().size(); i++) {
                HBox hb = (HBox) totalMetrics.getChildren().get(i);
                try {
                    Label l = (Label) hb.getChildren().get(1);
                    l.setText(MetricService.getMetricFromIndex(model, options, i, false));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialiseFilteredMetrics() {
        GraphOptions options = new GraphOptions();
        String[] values = new String[filteredMetrics.getChildren().size()];
        Platform.runLater(() -> {
            for (int i = 0; i < filteredMetrics.getChildren().size(); i++) {
                try {
                    VBox vb = (VBox) filteredMetrics.getChildren().get(i);
                    Label l = (Label) vb.getChildren().get(1);
                    values[i] = MetricService.getMetricFromIndex(model, options, i, true);
                    l.setText(values[i]);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        tabPane.getTabs().forEach(tab -> {
            filteredMetricsMap.put(tab, values);
        });

    }

    private void loadFilteredMetrics() {
        String[] values = filteredMetricsMap.get(tabPane.getSelectionModel().getSelectedItem());
        Platform.runLater(() -> {
            for (int i = 0; i < filteredMetrics.getChildren().size(); i++) {
                VBox vb = (VBox) filteredMetrics.getChildren().get(i);
                Label l = (Label) vb.getChildren().get(1);
                l.setText(values[i]);
            }
        });
    }

    private void updateFilteredMetrics() {
        String[] values = new String[filteredMetrics.getChildren().size()];
        for (int i = 0; i < filteredMetrics.getChildren().size(); i++) {
            try {
                VBox vb = (VBox) filteredMetrics.getChildren().get(i);
                Label l = (Label) vb.getChildren().get(1);
                values[i] = MetricService.getMetricFromIndex(model, tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()), i, true);
                int finalI = i;
                Platform.runLater(() -> {
                    l.setText(values[finalI]);
                });

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        filteredMetricsMap.put(tabPane.getSelectionModel().getSelectedItem(), values);
    }

    public void updateGraph() {
        try {
            Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
            GraphOptions options = tabOptionsMap.get(currentTab);


            Metric metric = Metric.fromString(currentTab.getText());
            Map data;
            if (metric == Metric.CLICK_COST_HISTOGRAM) {
                data = model.getHistogramData(options);
            } else {
                data = model.getDataOverTime(metric, options);
            }

            Graph g = tabGraphMap.get(currentTab);
            Platform.runLater(() -> {
                g.updateGraph(data);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    // If you click the preferences tab in the menu bar. It opens the preferences dialog.
    public void openPreferencesDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PreferencesDialog.fxml"));
        Parent root = fxmlLoader.load();

        Dialog preferencesDialog = new Dialog();
        DialogPane prefDialogPane = preferencesDialog.getDialogPane();


        preferencesDialog.setTitle("Preferences");
        prefDialogPane.setContent(root);
        prefDialogPane.getStylesheets().add("/styles.css");
        prefDialogPane.getStyleClass().add("prefDialog");

        PreferencesDialogController preferencesController = fxmlLoader.getController();
        preferencesController.setMainController(this);
        preferencesController.setPreferencesDialogTextSize(textSize);

        Stage preferencesDialogStage = (Stage) prefDialogPane.getScene().getWindow();
        preferencesDialogStage.getIcons().add(new Image("file:src/main/resources/PreferencesIcon.png"));
        prefDialogPane.getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = prefDialogPane.lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        preferencesDialog.show();
    }

    public void openAboutDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AboutDialog.fxml"));
        Parent root = fxmlLoader.load();

        Dialog aboutDialog = new Dialog();
        DialogPane aboutDialogPane = aboutDialog.getDialogPane();

        aboutDialog.setTitle("About");
        aboutDialogPane.setContent(root);
        aboutDialogPane.getStylesheets().add("/styles.css");

        AboutDialogController aboutDialogController = fxmlLoader.getController();
        aboutDialogController.setAboutDialogTextSize(textSize);

        Stage aboutDialogStage = (Stage) aboutDialogPane.getScene().getWindow();
        aboutDialogStage.getIcons().add(new Image("file:src/main/resources/aboutIcon.png"));
        aboutDialogPane.getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = aboutDialogPane.lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        aboutDialog.show();
    }


    @FXML
    // If you click Change Campaign in the Campaign Manager, it opens de campaign importer dialog.
    public void openCampaignImporterDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CampaignImporterDialog.fxml"));
        Parent root = fxmlLoader.load();

        Dialog campaignImporterDialog = new Dialog();
        campaignImporterDialog.setTitle("Campaign Importer");
        campaignImporterDialog.getDialogPane().setContent(root);

        CampaignImporterStageController campaignController = fxmlLoader.getController();
        campaignController.setMainController(this);
        campaignController.setCampaignImporterDialogTextSize(textSize);

        Stage campaignImporterStage = (Stage) campaignImporterDialog.getDialogPane().getScene().getWindow();
        campaignImporterStage.getIcons().add(new Image("file:src/main/resources/CampaignImporterIcon.png"));
        campaignImporterDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = campaignImporterDialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        campaignImporterDialog.show();
    }

    private String validateInput() {
        String response = "";
        Tab t = tabPane.getSelectionModel().getSelectedItem();
        GraphOptions options = tabOptionsMap.get(t);

        if (options.getStart() != null && options.getEnd() != null) {
            if (options.getStart().isAfter(options.getEnd())) {
                response += "The campaign start date must be before the end date\n";
            }
        }

        if (options.getBounceThreshold() < 1) {
            response += "The bounce threshold must be greater than zero\n";
        }

        return response;
    }

    @FXML
    public void graphOptionsChanged() {
        String errorMessage = validateInput();

        if (!errorMessage.equals("")) {
            showErrorAlert(errorMessage);
        } else {
            Platform.runLater(() -> {
                progressIndicatorContainer.setVisible(true);
            });
            new Thread(() -> {
                applyFilters();
                updateFilteredMetrics();
                updateGraph();
                updateGraphLabel(tabPane.getSelectionModel().getSelectedItem());
                Platform.runLater(() -> {
                    progressIndicatorContainer.setVisible(false);
                });
            }).start();
        }
    }

    private void updateFilterGUI() {

        for (int i = 1; i < ageBracket.getChildren().size(); i++) {
            // This relies on the structure of the code in the FXML file to programmatically interact with these values
            HBox hb = (HBox) ageBracket.getChildren().get(i);
            CheckBox cb = (CheckBox) hb.getChildren().get(0);
            Label t = (Label) hb.getChildren().get(1);
            cb.setSelected(tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).getAgeFilter().contains(AgeBracket.fromIdentifier(t.getText())));
        }

        for (int i = 1; i < context.getChildren().size(); i++) {
            HBox hb = (HBox) context.getChildren().get(i);
            CheckBox cb = (CheckBox) hb.getChildren().get(0);
            Label t = (Label) hb.getChildren().get(1);
            cb.setSelected(tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).getContextFilter().contains(Context.fromIdentifier(t.getText())));
        }

        for (int i = 1; i < income.getChildren().size(); i++) {
            HBox hb = (HBox) income.getChildren().get(i);
            CheckBox cb = (CheckBox) hb.getChildren().get(0);
            Label t = (Label) hb.getChildren().get(1);
            cb.setSelected(tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).getIncomeFilter().contains(Income.fromIdentifier(t.getText())));
        }

        for (int i = 1; i < gender.getChildren().size(); i++) {
            HBox hb = (HBox) gender.getChildren().get(i);
            CheckBox cb = (CheckBox) hb.getChildren().get(0);
            Label t = (Label) hb.getChildren().get(1);
            cb.setSelected(tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).getGenderFilter().contains(Gender.fromIdentifier(t.getText())));
        }
    }

    private void applyFilters() {

        // Start incrementing from 1 since the label is at index 0. The rest of the indices are HBoxes with a label and a checkbox
        for (int i = 1; i < ageBracket.getChildren().size(); i++) {
            HBox hb = (HBox) ageBracket.getChildren().get(i);
            CheckBox cb = (CheckBox) hb.getChildren().get(0);
            Label t = (Label) hb.getChildren().get(1);

            if (cb.isSelected()) {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).addAgeBracket(AgeBracket.fromIdentifier(t.getText()));
            } else {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).removeAgeBracket(AgeBracket.fromIdentifier(t.getText()));
            }
        }

        for (int i = 1; i < context.getChildren().size(); i++) {
            HBox hb = (HBox) context.getChildren().get(i);
            CheckBox cb = (CheckBox) hb.getChildren().get(0);
            Label t = (Label) hb.getChildren().get(1);

            if (cb.isSelected()) {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).addContext(Context.fromIdentifier(t.getText()));
            } else {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).removeContext(Context.fromIdentifier(t.getText()));
            }
        }

        for (int i = 1; i < income.getChildren().size(); i++) {
            HBox hb = (HBox) income.getChildren().get(i);
            CheckBox cb = (CheckBox) hb.getChildren().get(0);
            Label t = (Label) hb.getChildren().get(1);

            if (cb.isSelected()) {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).addIncome(Income.fromIdentifier(t.getText()));
            } else {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).removeIncome(Income.fromIdentifier(t.getText()));
            }
        }

        for (int i = 1; i < gender.getChildren().size(); i++) {
            HBox hb = (HBox) gender.getChildren().get(i);
            CheckBox cb = (CheckBox) hb.getChildren().get(0);
            Label t = (Label) hb.getChildren().get(1);

            if (cb.isSelected()) {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).addGender(Gender.fromIdentifier(t.getText()));
            } else {
                tabOptionsMap.get(tabPane.getSelectionModel().getSelectedItem()).removeGender(Gender.fromIdentifier(t.getText()));
            }
        }
    }

    // Refreshes Main Panel, updating its font size.
    public void updateGlobalFontSize(DoubleProperty sliderValue) {
        setGlobalTextSize(sliderValue);

        for (Node node : mainPane.lookupAll(".root")) {
            node.setStyle("-fx-font-size: " + sliderValue.getValue() + "px");
        }
    }

    // Sets global text size variable.
    public void setGlobalTextSize(DoubleProperty textSize) {
        this.textSize = textSize;
    }

    // Closes the application
    @FXML
    public void exitApplication() {
        Platform.exit();
    }
}
