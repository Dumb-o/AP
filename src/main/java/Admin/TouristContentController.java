package Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Models.User;
import Storage.JSONHandler;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TouristContentController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<TouristTableData> touristsTable;
    @FXML private TableColumn<TouristTableData, String> nameColumn;
    @FXML private TableColumn<TouristTableData, String> contactColumn;
    @FXML private TableColumn<TouristTableData, String> emailColumn;
    @FXML private Label totalTouristsLabel;

    private ObservableList<TouristTableData> touristData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        touristData = FXCollections.observableArrayList();

        setupColumns();
        setupTable();
        loadTouristData();
        setupSearch();
    }

    private void setupColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void setupTable() {
        touristsTable.setItems(touristData);

        touristsTable.setRowFactory(tv -> {
            TableRow<TouristTableData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    TouristTableData selectedTourist = row.getItem();
                    showTouristDetails(selectedTourist);
                }
            });
            return row;
        });
    }

    private void loadTouristData() {
        touristData.clear();
        List<User> allUsers = JSONHandler.loadUsers();
        List<User> tourists = allUsers.stream()
                .filter(u -> "user".equalsIgnoreCase(u.getUserType()))
                .toList();

        for (User tourist : tourists) {
            TouristTableData data = new TouristTableData();
            data.setFullName(tourist.getFullName());
            data.setEmail(tourist.getEmail());
            data.setPhone(tourist.getPhone());
            touristData.add(data);
        }

        updateStatistics();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                touristsTable.setItems(touristData);
            } else {
                ObservableList<TouristTableData> filteredData = FXCollections.observableArrayList(
                        touristData.stream()
                                .filter(tourist ->
                                        tourist.getFullName().toLowerCase().contains(newValue.toLowerCase()) ||
                                                tourist.getEmail().toLowerCase().contains(newValue.toLowerCase())
                                )
                                .collect(Collectors.toList())
                );
                touristsTable.setItems(filteredData);
            }
        });
    }

    @FXML
    private void refreshData(ActionEvent event) {
        loadTouristData();
        showAlert();
    }

    private void updateStatistics() {
        int total = touristData.size();
        totalTouristsLabel.setText("Total Tourists: " + total);
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Data refreshed successfully!");
        alert.showAndWait();
    }

    private void showTouristDetails(TouristTableData tourist) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tourist Details");
        alert.setHeaderText(tourist.getFullName());
        alert.setContentText(
                "Email: " + tourist.getEmail() + "\n" +
                        "Phone: " + tourist.getPhone()
        );
        alert.showAndWait();
    }

    public static class TouristTableData {
        private String fullName;
        private String phone;
        private String email;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
