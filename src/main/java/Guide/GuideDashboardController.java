package Guide;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import Session.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GuideDashboardController implements Initializable {

    @FXML private Label guideNameLabel;
    @FXML private Button dashboardBtn;
    @FXML private Button myTripsBtn;
    @FXML private Button touristsBtn;
    @FXML private Button profileBtn;
    @FXML private Button emergencyBtn;

    @FXML private StackPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Set guide name from session
        if (UserSession.getInstance().getCurrentUser() != null) {
            guideNameLabel.setText(UserSession.getInstance().getCurrentUserFullName());
        }

        // Load dashboard by default
        showDashboard(null);
    }

    @FXML
    private void showDashboard(ActionEvent event) {
        setActiveButton(dashboardBtn);
        loadContent("/Guide/guide_dashboard_content.fxml");
    }

    @FXML
    private void showMyTrips(ActionEvent event) {
        setActiveButton(myTripsBtn);
        loadContent("/Guide/guide_trips_content.fxml");
    }

    @FXML
    private void showTourists(ActionEvent event) {
        setActiveButton(touristsBtn);
        loadContent("/Guide/guide_tourist_content.fxml");
    }


    @FXML
    private void showProfile(ActionEvent event) {
        setActiveButton(profileBtn);
        loadContent("/Guide/guide_profile_content.fxml");
    }

    @FXML
    private void showEmergency(ActionEvent event) {
        setActiveButton(emergencyBtn);
        loadContent("/Guide/emergencyManagement.fxml");
    }

    private void setActiveButton(Button activeButton) {
        // Reset all buttons
        dashboardBtn.setStyle("-fx-background-color: transparent; -fx-padding: 10; -fx-font-size: 14;");
        myTripsBtn.setStyle("-fx-background-color: transparent; -fx-padding: 10; -fx-font-size: 14;");
        touristsBtn.setStyle("-fx-background-color: transparent; -fx-padding: 10; -fx-font-size: 14;");
        profileBtn.setStyle("-fx-background-color: transparent; -fx-padding: 10; -fx-font-size: 14;");
        emergencyBtn.setStyle("-fx-background-color: transparent; -fx-padding: 10; -fx-font-size: 14;");

        // Set active button style
        activeButton.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 14; -fx-background-radius: 5;");
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Could not load content: " + fxmlPath);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
