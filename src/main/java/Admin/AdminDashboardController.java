package Admin;

import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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
    @FXML private Button logoutBtn; // NEW: Logout button
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

    // NEW: Logout functionality
    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Logout Confirmation");
        confirmAlert.setHeaderText("Are you sure you want to logout?");
        confirmAlert.setContentText("You will be redirected to the login screen.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Clear user session
                    UserSession.getInstance().logout();

                    // Load login screen
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/First/Login.fxml"));
                    Scene loginScene = new Scene(loader.load());

                    // Get current stage and set login scene
                    Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
                    currentStage.setScene(loginScene);
                    currentStage.setTitle("GHUMGHAM - Login");
                    currentStage.centerOnScreen();

                    System.out.println("Admin logged out successfully");

                } catch (IOException e) {
                    System.err.println("Error loading login screen: " + e.getMessage());
                    e.printStackTrace();
                    showAlert("Error", "Could not load login screen. Please restart the application.");
                }
            }
        });
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
            showAlert("Error", "Could not load content: " + fxmlPath);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}