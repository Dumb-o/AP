package Admin;

import Models.Attraction;
import Models.Guide;
import Models.Trek;
import Storage.AdminJSONHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class AddTrekDialogController {

    @FXML private TextField trekNameField;
    @FXML private TextField durationField;
    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<String> difficultyComboBox;
    @FXML private TextField maxAltitudeField;
    @FXML private TextField costField;
    @FXML private TextField bestSeasonField;
    @FXML private ComboBox<String> attractionComboBox;
    @FXML private ComboBox<String> guideComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private TreksContentController parentController;
    private AdminJSONHandler jsonHandler;

    @FXML
    private void initialize() {
        jsonHandler = new AdminJSONHandler();

        // Setup difficulty options
        difficultyComboBox.getItems().addAll("Easy", "Moderate", "Hard");

        // Set default date to today
        startDatePicker.setValue(LocalDate.now());

        // Load attractions and guides for the combo boxes
        loadAttractions();
        loadGuides();

        // Set button actions
        saveButton.setOnAction(e -> saveTrek());
        cancelButton.setOnAction(e -> closeDialog());
    }

    private void loadAttractions() {
        List<Attraction> attractions = jsonHandler.loadAttractions();
        attractionComboBox.getItems().clear();
        for (Attraction attraction : attractions) {
            attractionComboBox.getItems().add(attraction.getName() + " (ID: " + attraction.getId() + ")");
        }
    }

    private void loadGuides() {
        List<Guide> guides = jsonHandler.loadGuides();
        guideComboBox.getItems().clear();
        for (Guide guide : guides) {
            guideComboBox.getItems().add(guide.getFirstName() + " " + guide.getLastName() + " (" + guide.getEmail() + ")");
        }
    }

    public void setParentController(TreksContentController parentController) {
        this.parentController = parentController;
    }

    private void saveTrek() {
        // Validate input
        if (trekNameField.getText().trim().isEmpty() ||
                durationField.getText().trim().isEmpty() ||
                startDatePicker.getValue() == null ||  // Added date validation
                difficultyComboBox.getValue() == null ||
                maxAltitudeField.getText().trim().isEmpty() ||
                costField.getText().trim().isEmpty() ||
                bestSeasonField.getText().trim().isEmpty() ||
                attractionComboBox.getValue() == null ||
                guideComboBox.getValue() == null) {

            showAlert("Validation Error", "Please fill in all required fields.");
            return;
        }

        try {
            double cost = Double.parseDouble(costField.getText().trim());

            // Extract attraction ID from combo box selection
            String attractionSelection = attractionComboBox.getValue();
            int attractionId = extractAttractionId(attractionSelection);

            // Extract guide email from combo box selection
            String guideSelection = guideComboBox.getValue();
            String guideEmail = extractGuideEmail(guideSelection);

            if (attractionId == -1) {
                showAlert("Error", "Invalid attraction selection.");
                return;
            }

            if (guideEmail == null) {
                showAlert("Error", "Invalid guide selection.");
                return;
            }

            // Create new trek with start date
            Trek trek = new Trek(
                    trekNameField.getText().trim(),
                    durationField.getText().trim(),
                    startDatePicker.getValue(),  // Use the selected date
                    difficultyComboBox.getValue(),
                    maxAltitudeField.getText().trim(),
                    cost,
                    bestSeasonField.getText().trim(),
                    guideEmail,
                    attractionId
            );

            // Add to parent controller
            parentController.addTrek(trek);
            closeDialog();

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid cost amount.");
        }
    }

    private int extractAttractionId(String attractionSelection) {
        try {
            // Extract ID from "Name (ID: X)" format
            int startIndex = attractionSelection.lastIndexOf("ID: ") + 4;
            int endIndex = attractionSelection.lastIndexOf(")");
            String idStr = attractionSelection.substring(startIndex, endIndex);
            return Integer.parseInt(idStr);
        } catch (Exception e) {
            return -1;
        }
    }

    private String extractGuideEmail(String guideSelection) {
        try {
            // Extract email from "Name (email)" format
            int startIndex = guideSelection.lastIndexOf("(") + 1;
            int endIndex = guideSelection.lastIndexOf(")");
            return guideSelection.substring(startIndex, endIndex);
        } catch (Exception e) {
            return null;
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}