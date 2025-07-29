package Admin;

import Models.Attraction;
import Storage.AdminJSONHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AttractionsContentController implements Initializable {


    @FXML private TableView<Attraction> attractionsTable;
    @FXML public TableColumn<Attraction, String> placeColumn;
    @FXML private TableColumn<Attraction, String> locationColumn;
    @FXML private TableColumn<Attraction, String> difficultyColumn;
    @FXML private TableColumn<Attraction, String> typeColumn;
    @FXML private TableColumn<Attraction, String> remarksColumn;
    @FXML private TextField searchField;
    @FXML private Label totalAttractionsLabel;

    private AdminJSONHandler jsonHandler;
    private ObservableList<Attraction> attractionsList;
    private ObservableList<Attraction> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jsonHandler = new AdminJSONHandler();
        attractionsList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();

        setupTableColumns();
        setupSearchFilter();
        loadAttractions();
    }

    private void setupTableColumns() {
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        remarksColumn.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        attractionsTable.setItems(filteredList);
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAttractions(newValue);
        });
    }

    private void filterAttractions(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredList.setAll(attractionsList);
        } else {
            List<Attraction> filtered = attractionsList.stream()
                    .filter(attraction ->
                            attraction.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                    attraction.getLocation().toLowerCase().contains(searchText.toLowerCase()) ||
                                    attraction.getType().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
            filteredList.setAll(filtered);
        }
        updateTotalLabel();
    }

    public void loadAttractions() {
        List<Attraction> attractions = jsonHandler.loadAttractions();
        attractionsList.setAll(attractions);
        filteredList.setAll(attractions);
        updateTotalLabel();
    }

    private void updateTotalLabel() {
        totalAttractionsLabel.setText("Total Attractions: " + filteredList.size());
    }

    @FXML
    private void addNewAttraction() {
        openAddAttractionDialog();
    }

    @FXML
    private void refreshData() {
        loadAttractions();
        showAlert("Success", "Data refreshed successfully!");
    }

    private void openAddAttractionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddAttractionDialog.fxml"));
            Parent root = loader.load();

            AddAttractionDialogController controller = loader.getController();
            controller.setParentController(this);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Attraction");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open Add Attraction dialog.");
        }
    }

    public void addAttraction(Attraction attraction) {
        if (jsonHandler.addAttraction(attraction)) {
            loadAttractions();
            showAlert("Success", "Attraction added successfully!");
        } else {
            showAlert("Error", "Failed to add attraction.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}