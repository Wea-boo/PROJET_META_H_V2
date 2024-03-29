package mkp;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import mkp.MultipleKnapsackProblem.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
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
        algorithmComboBox = new ComboBox<>(FXCollections.observableArrayList("DFS", "BFS", "A*"));
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
        
        // TextField for max depth (could also use a Spinner)
        maxDepthSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0); // Default range 0-100, initial value 0
        maxDepthSpinner.setValueFactory(valueFactory);
        maxDepthSpinner.setDisable(true);
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
        VBox algorithmChooserLayout = new VBox(10, algorithmComboBox, selectFileButton, maxDepthSpinner);
        algorithmChooserLayout.setPadding(new Insets(10));

        // Layout for performance criteria
        VBox performanceCriteriaLayout = new VBox(10, executionTimeLabel, nodesExploredLabel, maxNodesInMemoryLabel, totalValueLabel, unplacedItemsLabel);
        performanceCriteriaLayout.setPadding(new Insets(10));

        // Main layout
        HBox mainLayout = new HBox(10, algorithmChooserLayout, performanceCriteriaLayout);
        mainLayout.setPadding(new Insets(10));

        // Bottom layout
        VBox bottomLayout = new VBox(10, startButton, knapsackDisplayScroll);
        bottomLayout.setPadding(new Insets(10));

        // Root layout
        VBox rootLayout = new VBox(10, mainLayout, bottomLayout);

        // Scene and stage setup
        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setTitle("Multiple Knapsack Problem Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void enableUI() {
        Platform.runLater(() -> {
            startButton.setDisable(false);
            algorithmComboBox.setDisable(false);
            selectFileButton.setDisable(false);
            maxDepthSpinner.setDisable(initialState.get() == null);
        });
    }

    private void disableUI() {
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
            return;
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
            Platform.runLater(() -> {
                updatePerformanceLabels(result);
                updateKnapsackDisplay(result);
                enableUI();
            });
        });

        searchTask.setOnFailed(e -> {
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
            knapsackColumn.setPadding(new Insets(5)); // Padding inside the column
            Label knapsackLabel = new Label("Knapsack " + (i + 1));
            knapsackColumn.getChildren().add(knapsackLabel);

            // Add items to the column as small boxes (Rectangles or Labels)
            for (int j = 0; j < bestState.items.size(); j++) {
                if (bestState.itemInKnapsack[j] == i) {
                    Label itemLabel = new Label("Item " + j + " (" + bestState.items.get(j).weight + ", " + bestState.items.get(j).value + ")");
                    itemLabel.setStyle("-fx-border-color: black; -fx-padding: 5;"); // Style as needed
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
        for (int i = 0; i < result.bestState.itemInKnapsack.length; i++) {
            if (result.bestState.itemInKnapsack[i] == -1) {
                 unplacedItems.append("item ").append(i).append(", ");
            }
        }
        unplacedItems.delete(unplacedItems.length() - 2, unplacedItems.length());
        unplacedItems.append("}");
        unplacedItemsLabel.setText(unplacedItems.toString());
    }

    private void showAlert(String message, AlertType alertType) {
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
