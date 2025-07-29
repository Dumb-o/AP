package Tourist;

import Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import Session.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TouristDashboardMainController implements Initializable {

    @FXML
    private ComboBox<String> languageCombo;

    @FXML
    private Label userAvatar;

    @FXML
    private Label userNameLabel;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button profileBtn;

    @FXML
    private Button exploreBtn;

    @FXML
    private Button bookingsBtn;

    @FXML
    private StackPane contentArea;

    // Current active button for styling
    private Button activeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupLanguageCombo();
        setupEventHandlers();
        setActiveButton(dashboardBtn);
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFirstName());
        } else {
            userNameLabel.setText("Guest");
        }
        showDashboard();
    }

    private void setupLanguageCombo() {
        languageCombo.getItems().addAll("English", "Nepali");
        languageCombo.setValue("English");

        languageCombo.setOnAction(e -> {
            String selectedLanguage = languageCombo.getValue();
            System.out.println("Language changed to: " + selectedLanguage);
        });
    }

    private void setupEventHandlers() {
    }

    @FXML
    private void showDashboard() {
        loadContent("/Tourist/TouristDashboardContent.fxml");
        setActiveButton(dashboardBtn);
    }

    @FXML
    private void showProfile() {
        loadContent("/Tourist/touristProfile.fxml");
        setActiveButton(profileBtn);
    }

    @FXML
    private void showExplore() {
        loadContent("/Tourist/ExploreContent.fxml");
        setActiveButton(exploreBtn);
    }

    @FXML
    private void showBookings() {
        loadContent("/Tourist/touristsBooking.fxml");
        setActiveButton(bookingsBtn);
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorContent("Failed to load content: " + fxmlPath);
        }
    }

    private void showErrorContent(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(errorLabel);
    }

    private void setActiveButton(Button button) {
        resetButtonStyles();
        button.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-padding: 12; -fx-font-size: 14; -fx-background-radius: 8;");
        activeButton = button;
    }

    private void resetButtonStyles() {
        String defaultStyle = "-fx-background-color: transparent; -fx-padding: 12; -fx-font-size: 14;";
        dashboardBtn.setStyle(defaultStyle);
        profileBtn.setStyle(defaultStyle);
        exploreBtn.setStyle(defaultStyle);
        bookingsBtn.setStyle(defaultStyle);
    }
}
