package Admin;

import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import Session.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Button settingsButton;
    @FXML private Label adminNameLabel;
    @FXML private Button dashboardBtn;
    @FXML private Button touristsBtn;
    @FXML private Button guidesBtn;
    @FXML private Button attractionsBtn;
    @FXML private Button bookingsBtn;
    @FXML private Button emergencyBtn;
    @FXML private Button treksBtn;
    @FXML private StackPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            adminNameLabel.setText(currentUser.getFirstName());
        } else {
            adminNameLabel.setText("Guest");
        }
        showDashboard(null);
    }

    @FXML
    private void showDashboard(ActionEvent event) {
        setActiveButton(dashboardBtn);
        loadContent("/Admin/dashboard_content.fxml");
    }

    @FXML
    private void showTourists(ActionEvent event) {
        setActiveButton(touristsBtn);
        loadContent("/Admin/tourist_content.fxml");
    }

    @FXML
    private void showGuides(ActionEvent event) {
        setActiveButton(guidesBtn);
        loadContent("/Admin/guides_content.fxml");
    }

    @FXML
    private void showAttractions(ActionEvent event) {
        setActiveButton(attractionsBtn);
        loadContent("/Admin/attractions_content.fxml");
    }

    @FXML
    private void showBookings(ActionEvent event) {
        setActiveButton(bookingsBtn);
        loadContent("/Admin/bookings_content.fxml");
    }

    @FXML
    private void showEmergencyLogs(ActionEvent event) {
        setActiveButton(emergencyBtn);
        loadContent("/Admin/AdminEmergency.fxml");
    }

    @FXML
    private void showTreks(ActionEvent event) {
        setActiveButton(treksBtn);
        loadContent("/Admin/treks_content.fxml");
    }

    private void setActiveButton(Button activeButton) {
        // Reset all buttons
        dashboardBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 0;");
        touristsBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 0;");
        guidesBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 0;");
        attractionsBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 0;");
        bookingsBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 0;");
        emergencyBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 0;");
        treksBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 0;");

        // Set active button style
        activeButton.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-background-radius: 0;");
    }

    private void loadContent(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Node content = loader.load();

            contentArea.getChildren().setAll(content);  // Replace any existing content
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