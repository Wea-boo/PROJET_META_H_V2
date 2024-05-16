package mkp;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MKPInterface extends Application {
    private ComboBox<String> algorithmComboBox;
    private Button selectFileButton, startButton;
    private Label executionTimeLabel, totalValueLabel, unplacedItemsLabel;
    private FileChooser fileChooser;
    private File selectedFile;
    private List<Knapsack> knapsacks;
    private List<Item> items;
    private ScrollPane knapsackDisplayScroll;
    private HBox knapsackContainer;
    private Spinner<Integer> numBeesSpinner;
    private Spinner<Integer> flipSpinner;
    private Spinner<Integer> maxChancesSpinner;
    private Spinner<Integer> maxStepsSpinner;
    private Spinner<Integer> maxIterSpinner;

    private Spinner<Integer> populationSizeSpinner;
    private Spinner<Integer> maxGenSpinner;
    private Spinner<Double> crossoverProbSpinner;
    private Spinner<Double> mutationProbSpinner;
    private AtomicLong executionTime = new AtomicLong(0);
    // Parameter panes
    private GridPane bsoParametersPane;
    private GridPane gaParametersPane;

    @Override
    public void start(Stage primaryStage) {
        // Dropdown for algorithm selection
        Label algorithmLabel = new Label("Select Resolution Algorithm:");
        algorithmComboBox = new ComboBox<>(FXCollections.observableArrayList("BSO", "GA"));

        VBox algorithmSelectionLayout = new VBox(algorithmLabel, algorithmComboBox);
        algorithmComboBox.getSelectionModel().selectFirst(); // Default selection

        // BSO parameters pane
        bsoParametersPane = new GridPane();
        bsoParametersPane.setHgap(10);
        bsoParametersPane.setVgap(10);
        bsoParametersPane.setPadding(new Insets(10));

        Label numBeesLabel = new Label("Number of Bees:");
        numBeesSpinner = new Spinner<>(1, 100, 7);
        Label flipLabel = new Label("Flip Value:");
        flipSpinner = new Spinner<>(1, 100, 7);
        Label maxChancesLabel = new Label("Max Chances:");
        maxChancesSpinner = new Spinner<>(1, 100, 3);
        Label maxStepsLabel = new Label("Max Steps:");
        maxStepsSpinner = new Spinner<>(1, Integer.MAX_VALUE, 5000);
        Label maxIterLabel = new Label("Max Iterations:");
        maxIterSpinner = new Spinner<>(1, Integer.MAX_VALUE, 10000);

        bsoParametersPane.add(numBeesLabel, 0, 0);
        bsoParametersPane.add(numBeesSpinner, 1, 0);
        bsoParametersPane.add(flipLabel, 0, 1);
        bsoParametersPane.add(flipSpinner, 1, 1);
        bsoParametersPane.add(maxChancesLabel, 0, 2);
        bsoParametersPane.add(maxChancesSpinner, 1, 2);
        bsoParametersPane.add(maxStepsLabel, 0, 3);
        bsoParametersPane.add(maxStepsSpinner, 1, 3);
        bsoParametersPane.add(maxIterLabel, 0, 4);
        bsoParametersPane.add(maxIterSpinner, 1, 4);

        // GA parameters pane
        gaParametersPane = new GridPane();
        gaParametersPane.setHgap(10);
        gaParametersPane.setVgap(10);
        gaParametersPane.setPadding(new Insets(10));

        Label populationSizeLabel = new Label("Population Size:");
        Label maxGenLabel = new Label("Max Generations:");
        Label crossoverProbLabel = new Label("Crossover Probability:");
        Label mutationProbLabel = new Label("Mutation Probability:");
        populationSizeSpinner = new Spinner<>(1, 1000, 100);
        maxGenSpinner = new Spinner<>(1, 10000, 1000);
        crossoverProbSpinner = new Spinner<>(0.0f, 1.0f, 0.7f, 0.1f);
        mutationProbSpinner = new Spinner<>(0.0f, 1.0f, 0.1f, 0.1f);

        gaParametersPane.add(populationSizeLabel, 0, 0);
        gaParametersPane.add(populationSizeSpinner, 1, 0);
        gaParametersPane.add(maxGenLabel, 0, 1);
        gaParametersPane.add(maxGenSpinner, 1, 1);
        gaParametersPane.add(crossoverProbLabel, 0, 2);
        gaParametersPane.add(crossoverProbSpinner, 1, 2);
        gaParametersPane.add(mutationProbLabel, 0, 3);
        gaParametersPane.add(mutationProbSpinner, 1, 3);


        // Button to open file chooser for selecting test files
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        selectFileButton = new Button("Select Test File");
        selectFileButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile = file;
                knapsacks = MultipleKnapsackProblem.readKnapsacksFromCSV(selectedFile.getAbsolutePath());
                items = MultipleKnapsackProblem.readItemsFromCSV(selectedFile.getAbsolutePath());
            }
        });

        // Labels for performance criteria
        executionTimeLabel = new Label("Execution Time: ");
        totalValueLabel = new Label("Total Value of All Knapsacks: ");
        unplacedItemsLabel = new Label("Unplaced Items: ");

        // Button to start the resolution
        startButton = new Button("START");
        startButton.setOnAction(e -> runSearch());

        // Table to display the knapsacks and items
        knapsackContainer = new HBox(5); // 5 is the spacing between knapsack columns
        knapsackDisplayScroll = new ScrollPane(knapsackContainer);
        knapsackDisplayScroll.setFitToWidth(true);
        knapsackDisplayScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        knapsackDisplayScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Layout for algorithm selection and file chooser
        VBox controlPanel = new VBox(10, algorithmSelectionLayout, selectFileButton, bsoParametersPane, startButton);
        controlPanel.setPadding(new Insets(10));
        controlPanel.fillWidthProperty().setValue(true);

        // Algorithm selection handler
        algorithmComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("BSO")) {
                controlPanel.getChildren().remove(gaParametersPane);
                controlPanel.getChildren().add(bsoParametersPane);
            } else if (newValue.equals("GA")) {
                controlPanel.getChildren().remove(bsoParametersPane);
                controlPanel.getChildren().add(gaParametersPane);
            }
        });

        // Performance criteria layout
        VBox metricsLayout = new VBox(10, executionTimeLabel, totalValueLabel, unplacedItemsLabel);
        metricsLayout.setPadding(new Insets(10));
            // Main split layout with knapsack display on the left and controls and metrics on the right
    GridPane splitLayout = new GridPane();

    // Column constraints for the knapsack display (50% width)
    ColumnConstraints knapsackDisplayColumn = new ColumnConstraints();
    knapsackDisplayColumn.setPercentWidth(50);
    splitLayout.getColumnConstraints().add(knapsackDisplayColumn);

    // Column constraints for the control and metrics panel (50% width)
    ColumnConstraints controlMetricsColumn = new ColumnConstraints();
    controlMetricsColumn.setPercentWidth(50);
    splitLayout.getColumnConstraints().add(controlMetricsColumn);

    // Row constraints for the layout (100% height)
    RowConstraints row = new RowConstraints();
    row.setPercentHeight(100);
    splitLayout.getRowConstraints().add(row);

    // Add the knapsack display and control panel to the grid
    splitLayout.add(knapsackDisplayScroll, 0, 0); // Add to column 0
    splitLayout.add(new VBox(10, controlPanel, metricsLayout), 1, 0); // Add to column 1

    // Set the scene with the new layout
    Scene scene = new Scene(splitLayout, 500, 340); // Make it bigger if the knapsack display is too small
    splitLayout.setPrefSize(1000, 600);
    primaryStage.setTitle("Multiple Knapsack Problem Solver");
    primaryStage.setScene(scene);
    primaryStage.show();
}

private void enableUI() { // Enable UI elements after the search is done
    Platform.runLater(() -> {
        startButton.setDisable(false);
        algorithmComboBox.setDisable(false);
        selectFileButton.setDisable(false);
    });
}

private void disableUI() { // Disable UI elements while the search is running
    Platform.runLater(() -> {
        startButton.setDisable(true);
        algorithmComboBox.setDisable(true);
        selectFileButton.setDisable(true);
    });
}

private void runSearch() {
    if (knapsacks == null || items == null) {
        showAlert("Please select a test file first.", AlertType.WARNING);
        return;
    }

    disableUI();

    Task<Solution> searchTask = new Task<>() {
        @Override
        protected Solution call() {
            long startTime = System.currentTimeMillis();
            long endTime; // End time
            switch (algorithmComboBox.getValue()) {
                case "BSO":
                    BSO bso = new BSO();
                    Solution b = bso.searchBSO(
                        knapsacks,
                        items,
                        (int) numBeesSpinner.getValue(),
                        (int) flipSpinner.getValue(),
                        (int) maxChancesSpinner.getValue(),
                        (int) maxStepsSpinner.getValue(),
                        (int) maxIterSpinner.getValue()
                    );
                    endTime = System.currentTimeMillis(); // End time
                    executionTime.set(endTime - startTime); // Store execution time atomically
                    return b;
                case "GA":
                    Individu ind =  Algo_Genetique.Recherche_AlgoGenetique(
                        knapsacks.size(),
                        items.size(),
                        populationSizeSpinner.getValue(),
                        maxGenSpinner.getValue(),
                        populationSizeSpinner.getValue(),
                        crossoverProbSpinner.getValue().floatValue(),
                        mutationProbSpinner.getValue().floatValue(),
                        items,
                        knapsacks
                    );
                    Solution s = new Solution(items.size());
                    s.knapsackAssignments = ind.Individu;
                    s.calculateValue(items);
                    endTime = System.currentTimeMillis(); // End time
                    executionTime.set(endTime - startTime); // Store execution time atomically
                    return s;
                default:
                    return null;
            }
        }
    };

    searchTask.setOnSucceeded(e -> {
        Solution result = searchTask.getValue();
        Platform.runLater(() -> {
            updatePerformanceLabels(result, items);
            updateKnapsackDisplay(result, knapsacks, items);
            enableUI();
        });
    });

    searchTask.setOnFailed(e -> {
        Throwable throwable = searchTask.getException();
        Platform.runLater(() -> {
            String errorMessage = throwable instanceof OutOfMemoryError ?
                "OutOfMemoryError during search. Consider increasing heap size or using different parameters." :
                "Error during search: " + throwable.getMessage();
            showAlert(errorMessage, Alert.AlertType.ERROR);
            System.out.println(errorMessage);
            enableUI();
        });
    });

    new Thread(searchTask).start();
}

private void updateKnapsackDisplay(Solution result, List<Knapsack> knapsacks, List<Item> items) {
    knapsackContainer.getChildren().clear();

    for (int i = 0; i < knapsacks.size(); i++) {
        VBox knapsackColumn = new VBox();
        knapsackColumn.setPadding(new Insets(5));
        Label knapsackLabel = new Label("Knapsack " + (i + 1));
        knapsackColumn.getChildren().add(knapsackLabel);

        for (int j = 0; j < items.size(); j++) {
            if (result.knapsackAssignments[j] == i) {
                Label itemLabel = new Label("Item " + j);
                itemLabel.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-margin: 5;");
                Tooltip itemTooltip = new Tooltip("Weight: " + items.get(j).weight + "\nValue: " + items.get(j).value);
                Tooltip.install(itemLabel, itemTooltip);
                knapsackColumn.getChildren().add(itemLabel);
            }
        }

        double weight = result.knapsackAssignments[i];
        double capacity = knapsacks.get(i).capacity;
        double fillPercentage = (weight / capacity) * 100;
        Label fillLabel = new Label(String.format("%.1f%% full", fillPercentage));
        knapsackColumn.getChildren().add(fillLabel);

        knapsackContainer.getChildren().add(knapsackColumn);
    }
}

private void updatePerformanceLabels(Solution result, List<Item> items) {
    executionTimeLabel.setText("Execution Time: " + executionTime.get() + " ms");
    totalValueLabel.setText("Total Value of All Knapsacks: " + result.calculateValue(items));

    StringBuilder unplacedItems = new StringBuilder("Unplaced Items: {");
    int unplacedCount = 0;
    for (int i = 0; i < result.knapsackAssignments.length; i++) {
        if (result.knapsackAssignments[i] == -1) {
            unplacedItems.append("item ").append(i).append(", ");
            unplacedCount++;
        }
    }
    if (unplacedCount > 0) unplacedItems.delete(unplacedItems.length() - 2, unplacedItems.length());
    unplacedItems.append("}");
    unplacedItemsLabel.setText(unplacedItems.toString());
}

private void showAlert(String message, AlertType alertType) {
    Alert alert = new Alert(alertType);
    alert.setTitle("Search Notification");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

public static void main(String[] args) {
    launch(args);
}
}