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
        TableView<Item> solutionTable = new TableView<>();
        

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
        VBox bottomLayout = new VBox(10, startButton, solutionTable);
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
            Platform.runLater(() -> {
                updatePerformanceLabels(result);

                // TODO: Display the solution in a table
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

    private void updatePerformanceLabels(SearchResult result) {
        // Update each label with the specific piece of information from the SearchResult
        executionTimeLabel.setText("Execution Time: " + result.executionTime + " ms");
        nodesExploredLabel.setText("Nodes Explored: " + result.nodesExplored);
        maxNodesInMemoryLabel.setText("Max Number Of Nodes in Memory: " + result.maxOpenSize);
        totalValueLabel.setText("Total Value of All Knapsacks: " + result.bestValue);
        StringBuilder unplacedItems = new StringBuilder("Unplaced Items: {");
        for (int i = 0; i < result.bestState.itemInKnapsack.length; i++) {
            if (result.bestState.itemInKnapsack[i] == -1) {
                if(i != result.bestState.itemInKnapsack.length - 1) {
                    unplacedItems.append("item ").append(i).append(", ");
                } else {
                    unplacedItems.append("item ").append(i);
                }
            }

        }
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
