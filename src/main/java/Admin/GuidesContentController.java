package Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import Models.Guide;
import Storage.JSONHandler;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GuidesContentController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<GuideTableData> guidesTable;
    @FXML private Label totalGuidesLabel;

    private JSONHandler fileManager;
    private ObservableList<GuideTableData> guideData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileManager = new JSONHandler();
        guideData = FXCollections.observableArrayList();

        setupTable();
        loadGuideData();
        setupSearch();
    }

    private void setupTable() {
        guidesTable.setItems(guideData);

        guidesTable.setRowFactory(tv -> {
            TableRow<GuideTableData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    GuideTableData selectedGuide = row.getItem();
                    showGuideDetails(selectedGuide);
                }
            });
            return row;
        });
    }

    private void loadGuideData() {
        guideData.clear();
        List<Guide> guides = fileManager.getGuides();

        for (Guide guide : guides) {
            GuideTableData data = new GuideTableData();
            data.setFullName(guide.getFullName());
            data.setLanguages(guide.getProficiencyLanguage());
            data.setPhone(guide.getPhone());
            data.setExperienceYears(extractExperienceYears(guide.getExperience()));
            data.setEmail(guide.getEmail());
            guideData.add(data);
        }

        updateStatistics();
    }

    private String extractExperienceYears(String experience) {
        if (experience != null && experience.contains("years")) {
            try {
                String[] parts = experience.split(" ");
                for (String part : parts) {
                    if (part.matches("\\d+")) {
                        return part + " yrs";
                    }
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
        return "N/A";
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                guidesTable.setItems(guideData);
            } else {
                ObservableList<GuideTableData> filteredData = guideData.stream()
                        .filter(guide ->
                                guide.getFullName().toLowerCase().contains(newValue.toLowerCase()) ||
                                        guide.getEmail().toLowerCase().contains(newValue.toLowerCase()) ||
                                        guide.getLanguages().toLowerCase().contains(newValue.toLowerCase()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                guidesTable.setItems(filteredData);
            }
        });
    }

    @FXML
    private void refreshData(ActionEvent event) {
        loadGuideData();
        showAlert();
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Data refreshed successfully!");
        alert.showAndWait();
    }

    private void updateStatistics() {
        int total = guideData.size();

        totalGuidesLabel.setText("Total Guides: " + total);
    }

    private void showGuideDetails(GuideTableData guide) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guide Details");
        alert.setHeaderText(guide.getFullName());
        alert.setContentText(
                "Email: " + guide.getEmail() + "\n" +
                        "Phone: " + guide.getPhone() + "\n" +
                        "Languages: " + guide.getLanguages() + "\n" +
                        "Experience: " + guide.getExperienceYears()
        );
        alert.showAndWait();
    }

    // Inner class for table data
    public static class GuideTableData {
        private String fullName;
        private String languages;
        private String phone;
        private String experienceYears;
        private String email;

        // Getters and setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getLanguages() { return languages; }
        public void setLanguages(String languages) { this.languages = languages; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getExperienceYears() { return experienceYears; }
        public void setExperienceYears(String experienceYears) { this.experienceYears = experienceYears; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}