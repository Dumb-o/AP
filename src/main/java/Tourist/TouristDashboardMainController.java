package Tourist;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TouristDashboardMainController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private Button notificationButton;

    @FXML
    private Button settingsButton;

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

        // Load dashboard content by default
        showDashboard();
    }

    private void setupLanguageCombo() {
        languageCombo.getItems().addAll("English", "Nepali", "Hindi");
        languageCombo.setValue("English");

        languageCombo.setOnAction(e -> {
            String selectedLanguage = languageCombo.getValue();
            // Handle language change logic here
            System.out.println("Language changed to: " + selectedLanguage);
        });
    }

    private void setupEventHandlers() {
        // Search functionality
        searchField.setOnAction(e -> handleSearch());

        // Notification button
        notificationButton.setOnAction(e -> handleNotifications());

        // Settings button
        settingsButton.setOnAction(e -> handleSettings());
    }

    @FXML
    private void showDashboard() {
        loadContent("/Tourist/TouristDashboardContent.fxml");
        setActiveButton(dashboardBtn);
    }

    @FXML
    private void showProfile() {
        // Load profile content (you'll need to create this FXML)
        loadContent("/Tourist/touristProfile.fxml");
        setActiveButton(profileBtn);
    }

    @FXML
    private void showExplore() {
        // Load explore content (you'll need to create this FXML)
        loadContent("/Tourist/ExploreContent.fxml");
        setActiveButton(exploreBtn);
    }

    @FXML
    private void showBookings() {
        // Load bookings content (you'll need to create this FXML)
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
            // Show error message or load default content
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
        // Reset all buttons to default style
        resetButtonStyles();

        // Set active button style
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

    private void handleSearch() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            System.out.println("Searching for: " + searchText);
            // Implement search logic here
            // You might want to show search results in a popup or navigate to search page
        }
    }

    private void handleNotifications() {
        System.out.println("Notifications clicked");
        // Implement notification logic here
        // You might want to show a popup with notifications
    }

    private void handleSettings() {
        System.out.println("Settings clicked");
        // Implement settings logic here
        // You might want to show settings dialog or navigate to settings page
    }

    // Method to update user information
    public void setUserInfo(String userName, String avatarText) {
        userNameLabel.setText(userName);
        userAvatar.setText(avatarText);
    }

    // Method to get current user name
    public String getCurrentUserName() {
        return userNameLabel.getText();
    }
}
