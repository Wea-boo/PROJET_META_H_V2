package mkp;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import mkp.MultipleKnapsackProblem.*;
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
    private Spinner<Integer> maxDepthSpinner;
    private Button selectFileButton, startButton;
    private Label executionTimeLabel, nodesExploredLabel, maxNodesInMemoryLabel, totalValueLabel, unplacedItemsLabel;
    private FileChooser fileChooser;
    private File selectedFile;
    private AtomicReference<State> initialState = new AtomicReference<>();
    private ScrollPane knapsackDisplayScroll; // Scroll pane to contain the knapsack display
    private HBox knapsackContainer; // HBox to hold VBox columns for knapsacks

    @Override
    public void start(Stage primaryStage) {
        // Dropdown for algorithm selection
        Label algorithmLabel = new Label("Select Resolution Algorithm:");
        algorithmComboBox = new ComboBox<>(FXCollections.observableArrayList("DFS", "BFS", "A*"));

        VBox algorithmSelectionLayout = new VBox(algorithmLabel, algorithmComboBox);
        algorithmComboBox.getSelectionModel().selectFirst(); // Default selection

        // Button to open file chooser for selecting test files
        
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        selectFileButton = new Button("Select Test File");
        selectFileButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile = file;
                State state = MultipleKnapsackProblem.CSVToState(selectedFile.getAbsolutePath());
                if (state != null) {
                    initialState.set(state);
                    maxDepthSpinner.setDisable(false);
                    int maxDepth = initialState.get().items.size();
                    SpinnerValueFactory<Integer> spinnerValueFactory = 
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxDepth, maxDepth);
                    maxDepthSpinner.setValueFactory(spinnerValueFactory);
                }
            }
        });
        
        maxDepthSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0); // Default range 0-100, initial value 0
        maxDepthSpinner.setValueFactory(valueFactory);
        maxDepthSpinner.setDisable(true);

        Label maxDepthLabel = new Label("Max depth:");
        VBox maxDepthLayout = new VBox(maxDepthLabel, maxDepthSpinner);
        // Labels for performance criteria
        executionTimeLabel = new Label("Execution Time: ");
        nodesExploredLabel = new Label("Nodes Explored: ");
        maxNodesInMemoryLabel = new Label("Max Number Of Nodes in Memory: ");
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
        VBox controlPanel = new VBox(10, algorithmSelectionLayout, selectFileButton, maxDepthLayout, startButton);
        controlPanel.setPadding(new Insets(10));
        controlPanel.fillWidthProperty().setValue(true);

        // Performance criteria layout
        VBox metricsLayout = new VBox(10, executionTimeLabel, nodesExploredLabel, maxNodesInMemoryLabel, totalValueLabel, unplacedItemsLabel);
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
            maxDepthSpinner.setDisable(initialState.get() == null);
        });
    }

    private void disableUI() { // Disable UI elements while the search is running, the search can take a while, the user should not be able to launch a new search while the current one is running
        Platform.runLater(() -> {
            startButton.setDisable(true);
            algorithmComboBox.setDisable(true);
            selectFileButton.setDisable(true);
            maxDepthSpinner.setDisable(true);
        });
    }

    private void runSearch() {
        if (initialState.get() == null) {
            showAlert("Please select a test file first.", AlertType.WARNING);
            return; // Don't proceed with the search
        }

        disableUI();

        // Run search in a background thread
        Task<SearchResult> searchTask = new Task<>() {
            @Override
            protected SearchResult call() {
                int maxDepth = maxDepthSpinner.getValue();
                switch (algorithmComboBox.getValue()) {
                    case "DFS":
                        return MultipleKnapsackProblem.dfsSearchTesting(initialState.get(), maxDepth);
                    case "BFS":
                        return MultipleKnapsackProblem.bfsSearchTesting(initialState.get(), maxDepth);
                    case "A*":
                        return MultipleKnapsackProblem.aStarSearchTesting(initialState.get(), maxDepth);
                    default:
                        return null; // Invalid selection
                }
            }
        };

        searchTask.setOnSucceeded(e -> {
            SearchResult result = searchTask.getValue();
            Platform.runLater(() -> { // Update UI with the necessary information from the search result
                updatePerformanceLabels(result);
                updateKnapsackDisplay(result);
                enableUI();
            });
        });

        searchTask.setOnFailed(e -> {
            // Launch an alarm in case of an exception/error especially OutOfMemoryError for BFS
            Throwable throwable = searchTask.getException();
            Platform.runLater(() -> {
                String errorMessage = throwable instanceof OutOfMemoryError ? 
                    "OutOfMemoryError during search. Consider increasing heap size or using a shallower depth." : 
                    "Error during search: " + throwable.getMessage();
                showAlert(errorMessage, Alert.AlertType.ERROR);
                enableUI();
            });
        });

        new Thread(searchTask).start();
    }

    private void updateKnapsackDisplay(SearchResult result) {
        State bestState = result.bestState;
        knapsackContainer.getChildren().clear(); // Clear current display

        // Iterate through knapsacks and create a column (VBox) for each
        for (int i = 0; i < bestState.knapsacks.size(); i++) {
            VBox knapsackColumn = new VBox();
            knapsackColumn.setPadding(new Insets(5));
            Label knapsackLabel = new Label("Knapsack " + (i + 1));
            knapsackColumn.getChildren().add(knapsackLabel);

            // Add items to the column as small boxes (Rectangles or Labels)
            for (int j = 0; j < bestState.items.size(); j++) {
                if (bestState.itemInKnapsack[j] == i) {
                    Label itemLabel = new Label("Item " + j);
                    //hover effect on item label displaying the weight and value of the item
                    itemLabel.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-margin: 5;"); // Style as needed
                    Tooltip itemTooltip = new Tooltip("Weight: " + bestState.items.get(j).weight + "\nValue: " + bestState.items.get(j).value); // Hover for weight and value
                    Tooltip.install(itemLabel, itemTooltip);
                    knapsackColumn.getChildren().add(itemLabel);
                }
            }

            // Calculate and add the fill percentage at the bottom
            double weight = bestState.knapsackWeights[i];
            double capacity = bestState.knapsacks.get(i).capacity;
            double fillPercentage = (weight / capacity) * 100;
            Label fillLabel = new Label(String.format("%.1f%% full", fillPercentage));
            knapsackColumn.getChildren().add(fillLabel);

            knapsackContainer.getChildren().add(knapsackColumn);
        }
    }

    private void updatePerformanceLabels(SearchResult result) {
        // Update each label with the specific piece of information from the SearchResult
        executionTimeLabel.setText("Execution Time: " + result.executionTime + " ms");
        nodesExploredLabel.setText("Nodes Explored: " + result.nodesExplored);
        maxNodesInMemoryLabel.setText("Max Number Of Nodes in Memory: " + result.maxOpenSize);
        totalValueLabel.setText("Total Value of All Knapsacks: " + result.bestValue);
        StringBuilder unplacedItems = new StringBuilder("Unplaced Items: {");
        int unplacedCount = 0;
        for (int i = 0; i < result.bestState.itemInKnapsack.length; i++) {
            if (result.bestState.itemInKnapsack[i] == -1) {
                 unplacedItems.append("item ").append(i).append(", ");
                unplacedCount++;
            }
        }
        
        if(unplacedCount > 0) unplacedItems.delete(unplacedItems.length() - 2, unplacedItems.length());
        unplacedItems.append("}");
        unplacedItemsLabel.setText(unplacedItems.toString());
    }

    private void showAlert(String message, AlertType alertType) { // show alert for errors, warnings, etc...
        Alert alert = new Alert(alertType);
        alert.setTitle("Search Notification");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
