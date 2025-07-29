package Tourist;

import Models.Trek;
import Models.Booking;
import Models.User;
import Session.UserSession;
import Storage.AdminJSONHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExploreContentController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private VBox treksContainer;

    private List<Trek> allTreks;
    private List<Trek> filteredTreks;
    private AdminJSONHandler jsonHandler;

    // Keep this for backward compatibility, but we'll override it with session data
    private String currentUserEmail = "tourist@example.com"; // Default for testing

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jsonHandler = new AdminJSONHandler();
        setupSearchField();
        loadTreksFromJSON();
        displayTreks(allTreks);

        // Try to get user email from session
        updateUserEmailFromSession();
    }

    // Add this new method to get email from session
    private void updateUserEmailFromSession() {
        try {
            UserSession userSession = UserSession.getInstance();
            User currentUser = userSession.getCurrentUser();

            if (currentUser != null && currentUser.getEmail() != null) {
                currentUserEmail = currentUser.getEmail();
                System.out.println("Updated user email from session: " + currentUserEmail);
            } else {
                System.out.println("No user in session, using default email: " + currentUserEmail);
            }
        } catch (Exception e) {
            System.err.println("Error getting user from session: " + e.getMessage());
            System.out.println("Using default email: " + currentUserEmail);
        }
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTreks(newValue.trim().toLowerCase());
        });
    }

    private void loadTreksFromJSON() {
        try {
            allTreks = jsonHandler.loadTreks();
            System.out.println("Loaded " + allTreks.size() + " treks from JSON");
        } catch (Exception e) {
            System.err.println("Error loading treks from JSON: " + e.getMessage());
            e.printStackTrace();
            allTreks = List.of(); // Empty list as fallback
        }
    }

    private void filterTreks(String searchText) {
        if (searchText.isEmpty()) {
            filteredTreks = allTreks;
        } else {
            filteredTreks = allTreks.stream()
                    .filter(trek ->
                            trek.getTrekName().toLowerCase().contains(searchText) ||
                                    trek.getDifficulty().toLowerCase().contains(searchText) ||
                                    trek.getBestSeason().toLowerCase().contains(searchText) ||
                                    trek.getMaxAltitude().toLowerCase().contains(searchText)
                    )
                    .collect(Collectors.toList());
        }
        displayTreks(filteredTreks);
    }

    private void displayTreks(List<Trek> treks) {
        treksContainer.getChildren().clear();

        if (treks.isEmpty()) {
            Label noResultsLabel = new Label("No treks found matching your search criteria.");
            noResultsLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 16px; -fx-padding: 20;");
            treksContainer.getChildren().add(noResultsLabel);
            return;
        }

        for (Trek trek : treks) {
            VBox trekCard = createTrekCard(trek);
            treksContainer.getChildren().add(trekCard);
        }
    }

    private VBox createTrekCard(Trek trek) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);

        // Trek Header
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Trek icon and name
        VBox nameSection = new VBox(5);
        Label trekIcon = new Label("🏔️");
        trekIcon.setStyle("-fx-font-size: 24px;");

        Label nameLabel = new Label(trek.getTrekName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #333333;");

        nameSection.getChildren().addAll(trekIcon, nameLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Badges container (difficulty + discount if applicable)
        HBox badgesBox = new HBox(8);
        badgesBox.setAlignment(Pos.CENTER_RIGHT);

        // NEW: Add discount badge if trek has discount
        if (trek.hasDiscount()) {
            Label discountBadge = createDiscountBadge(trek);
            badgesBox.getChildren().add(discountBadge);
        }

        // Difficulty badge
        Label difficultyLabel = new Label(trek.getDifficulty());
        difficultyLabel.setStyle(getDifficultyStyle(trek.getDifficulty()));
        badgesBox.getChildren().add(difficultyLabel);

        headerBox.getChildren().addAll(nameSection, spacer, badgesBox);

        // Trek Details
        VBox detailsBox = new VBox(8);

        // Duration and Date
        HBox durationDateBox = new HBox(20);
        Label durationLabel = new Label("⏰ Duration: " + trek.getDuration());
        durationLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        Label dateLabel = new Label("📅 Start Date: " + trek.getStartDate().format(formatter));
        dateLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        durationDateBox.getChildren().addAll(durationLabel, dateLabel);

        // Altitude and Season
        HBox altitudeSeasonBox = new HBox(20);
        Label altitudeLabel = new Label("⛰️ Max Altitude: " + trek.getMaxAltitude());
        altitudeLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        Label seasonLabel = new Label("🌤️ Best Season: " + trek.getBestSeason());
        seasonLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        altitudeSeasonBox.getChildren().addAll(altitudeLabel, seasonLabel);

        // Cost and Guide - UPDATED to show discount information
        HBox costGuideBox = new HBox(20);

        // NEW: Enhanced cost display with discount information
        VBox costSection = createCostSection(trek);

        Label guideLabel = new Label("👨‍🦲 Guide: " + extractGuideName(trek.getGuideEmail()));
        guideLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        costGuideBox.getChildren().addAll(costSection, guideLabel);

        detailsBox.getChildren().addAll(durationDateBox, altitudeSeasonBox, costGuideBox);

        // Action Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-background-color: transparent; -fx-border-color: #e53e3e; " +
                "-fx-border-radius: 20; -fx-text-fill: #e53e3e; -fx-padding: 8 16; " +
                "-fx-font-size: 14px; -fx-cursor: hand;");
        viewDetailsButton.setOnAction(e -> viewTrekDetails(trek));

        Button bookNowButton = new Button("Book Now");
        bookNowButton.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; " +
                "-fx-background-radius: 20; -fx-padding: 8 16; -fx-font-size: 14px; " +
                "-fx-cursor: hand; -fx-font-weight: bold;");
        bookNowButton.setOnAction(e -> bookTrek(trek));

        buttonBox.getChildren().addAll(viewDetailsButton, bookNowButton);

        // Add hover effects
        addHoverEffect(viewDetailsButton);
        addHoverEffect(bookNowButton);

        card.getChildren().addAll(headerBox, detailsBox, buttonBox);
        return card;
    }

    // NEW: Create discount badge
    private Label createDiscountBadge(Trek trek) {
        String discountText = String.format("🏷️ %.0f%% OFF", trek.getDiscountPercent());
        Label discountBadge = new Label(discountText);

        // Catchy discount badge styling
        discountBadge.setStyle(
                "-fx-background-color: linear-gradient(to right, #FF6B35, #F7931E); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px; " +
                        "-fx-padding: 4 8; " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: #FF4500; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(255,107,53,0.4), 4, 0, 0, 1);"
        );

        // Add pulsing animation effect
        addPulseEffect(discountBadge);

        return discountBadge;
    }

    // NEW: Create enhanced cost section with discount info
    private VBox createCostSection(Trek trek) {
        VBox costSection = new VBox(2);

        if (trek.hasDiscount()) {
            // Show original cost with strikethrough
            Label originalCostLabel = new Label("💰 Was: $" + String.format("%.0f", trek.getOriginalCost()));
            originalCostLabel.setStyle("-fx-text-fill: #999999; -fx-font-size: 12px; " +
                    "-fx-strikethrough: true;");

            // Show discounted cost prominently
            Label finalCostLabel = new Label("💰 Now: $" + String.format("%.0f", trek.getCost()));
            finalCostLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-weight: bold; -fx-font-size: 16px;");

            costSection.getChildren().addAll(originalCostLabel, finalCostLabel);
        } else {
            // Regular cost display
            Label costLabel = new Label("💰 Cost: $" + String.format("%.0f", trek.getCost()));
            costLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-weight: bold; -fx-font-size: 16px;");
            costSection.getChildren().add(costLabel);
        }

        return costSection;
    }

    // NEW: Add subtle pulse effect to discount badge
    private void addPulseEffect(Label discountBadge) {
        javafx.animation.ScaleTransition scaleTransition =
                new javafx.animation.ScaleTransition(javafx.util.Duration.seconds(1.5), discountBadge);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.setCycleCount(javafx.animation.Animation.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    private String getDifficultyStyle(String difficulty) {
        String baseStyle = "-fx-background-radius: 15; -fx-padding: 6 12; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-font-weight: bold;";

        return switch (difficulty.toLowerCase()) {
            case "easy" -> baseStyle + " -fx-background-color: #4CAF50;";
            case "moderate" -> baseStyle + " -fx-background-color: #FF9800;";
            case "hard" -> baseStyle + " -fx-background-color: #F44336;";
            case "extreme" -> baseStyle + " -fx-background-color: #9C27B0;";
            default -> baseStyle + " -fx-background-color: #9E9E9E;";
        };
    }

    private String extractGuideName(String email) {
        if (email == null || email.isEmpty()) {
            return "Not assigned";
        }
        // Extract name from email (before @)
        String name = email.split("@")[0];
        // Capitalize first letter
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void addHoverEffect(Button button) {
        String originalStyle = button.getStyle();

        button.setOnMouseEntered(e -> {
            if (button.getText().equals("Book Now")) {
                button.setStyle(originalStyle + " -fx-background-color: #d32f2f;");
            } else {
                button.setStyle(originalStyle + " -fx-background-color: #e53e3e; -fx-text-fill: white;");
            }
        });

        button.setOnMouseExited(e -> button.setStyle(originalStyle));
    }

    private void viewTrekDetails(Trek trek) {
        Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
        detailsAlert.setTitle("Trek Details");
        detailsAlert.setHeaderText(trek.getTrekName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        // NEW: Enhanced details with discount information
        String details;
        if (trek.hasDiscount()) {
            details = String.format(
                    """
                    🏔️ Trek: %s
                    
                    ⏰ Duration: %s
                    📅 Start Date: %s
                    ⚡ Difficulty: %s
                    ⛰️ Maximum Altitude: %s
                    
                    💰 SPECIAL OFFER! 🏷️
                    Original Price: $%.0f
                    Discount: %.1f%% OFF
                    You Save: $%.0f
                    Final Price: $%.0f
                    
                    🌤️ Best Season: %s
                    👨‍🦲 Guide: %s
                    🆔 Trek ID: %d
                    🎯 Attraction ID: %d""",
                    trek.getTrekName(),
                    trek.getDuration(),
                    trek.getStartDate().format(formatter),
                    trek.getDifficulty(),
                    trek.getMaxAltitude(),
                    trek.getOriginalCost(),
                    trek.getDiscountPercent(),
                    trek.getDiscountAmount(),
                    trek.getCost(),
                    trek.getBestSeason(),
                    extractGuideName(trek.getGuideEmail()),
                    trek.getId(),
                    trek.getAttractionId()
            );
        } else {
            details = String.format(
                    """
                    🏔️ Trek: %s
                    
                    ⏰ Duration: %s
                    📅 Start Date: %s
                    ⚡ Difficulty: %s
                    ⛰️ Maximum Altitude: %s
                    💰 Cost: $%.0f
                    🌤️ Best Season: %s
                    👨‍🦲 Guide: %s
                    🆔 Trek ID: %d
                    🎯 Attraction ID: %d""",
                    trek.getTrekName(),
                    trek.getDuration(),
                    trek.getStartDate().format(formatter),
                    trek.getDifficulty(),
                    trek.getMaxAltitude(),
                    trek.getCost(),
                    trek.getBestSeason(),
                    extractGuideName(trek.getGuideEmail()),
                    trek.getId(),
                    trek.getAttractionId()
            );
        }

        detailsAlert.setContentText(details);
        detailsAlert.getDialogPane().setPrefWidth(400);
        detailsAlert.getDialogPane().setStyle("-fx-font-family: 'System'; -fx-font-size: 13px;");

        detailsAlert.showAndWait();
    }

    private void bookTrek(Trek trek) {
        // Update user email from session before booking
        updateUserEmailFromSession();

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Book Trek");
        confirmAlert.setHeaderText("Confirm Booking");

        // NEW: Enhanced booking confirmation with discount info
        String bookingDetails;
        if (trek.hasDiscount()) {
            bookingDetails = String.format(
                    "You are about to book:\n\n" +
                            "🏔️ %s\n" +
                            "📅 Starting: %s\n" +
                            "⏰ Duration: %s\n" +
                            "🏷️ DISCOUNT APPLIED: %.1f%% OFF!\n" +
                            "💰 Original Price: $%.0f\n" +
                            "💰 Your Price: $%.0f (You save $%.0f!)\n" +
                            "👨‍🦲 Guide: %s\n\n" +
                            "Do you want to proceed with this discounted booking?",
                    trek.getTrekName(),
                    trek.getStartDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                    trek.getDuration(),
                    trek.getDiscountPercent(),
                    trek.getOriginalCost(),
                    trek.getCost(),
                    trek.getDiscountAmount(),
                    extractGuideName(trek.getGuideEmail())
            );
        } else {
            bookingDetails = String.format(
                    "You are about to book:\n\n" +
                            "🏔️ %s\n" +
                            "📅 Starting: %s\n" +
                            "⏰ Duration: %s\n" +
                            "💰 Total Cost: $%.0f\n" +
                            "👨‍🦲 Guide: %s\n\n" +
                            "Do you want to proceed with the booking?",
                    trek.getTrekName(),
                    trek.getStartDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                    trek.getDuration(),
                    trek.getCost(),
                    extractGuideName(trek.getGuideEmail())
            );
        }

        confirmAlert.setContentText(bookingDetails);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = createBooking(trek);

                if (success) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Booking Confirmed");
                    successAlert.setHeaderText("Success!");

                    String successMessage;
                    if (trek.hasDiscount()) {
                        successMessage = String.format(
                                "Your booking for %s has been confirmed!\n\n" +
                                        "🎉 Congratulations! You saved $%.0f with our %.1f%% discount!\n" +
                                        "Final Amount Paid: $%.0f\n\n" +
                                        "Booking Status: Pending\n" +
                                        "You will receive a confirmation email shortly with detailed information " +
                                        "about your trek, including packing lists, meeting points, and contact details.\n\n" +
                                        "The guide will contact you soon to finalize the details.",
                                trek.getTrekName(),
                                trek.getDiscountAmount(),
                                trek.getDiscountPercent(),
                                trek.getCost()
                        );
                    } else {
                        successMessage = String.format(
                                "Your booking for %s has been confirmed!\n\n" +
                                        "Booking Status: Pending\n" +
                                        "You will receive a confirmation email shortly with detailed information " +
                                        "about your trek, including packing lists, meeting points, and contact details.\n\n" +
                                        "The guide will contact you soon to finalize the details.",
                                trek.getTrekName()
                        );
                    }

                    successAlert.setContentText(successMessage);
                    successAlert.showAndWait();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Booking Failed");
                    errorAlert.setHeaderText("Error");
                    errorAlert.setContentText("Sorry, there was an error processing your booking. Please try again later.");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    private boolean createBooking(Trek trek) {
        try {
            // Get the latest user email from session right before creating booking
            String userEmailToUse = getCurrentUserEmailFromSession();

            System.out.println("Creating booking with user email: " + userEmailToUse);

            // Create new booking with the correct user email
            Booking booking = new Booking(trek.getId(), userEmailToUse, trek.getGuideEmail(), trek.getStartDate());

            // Save to JSON file
            boolean success = jsonHandler.addBooking(booking);

            if (success) {
                System.out.println("Booking created successfully: " + booking);
                return true;
            } else {
                System.err.println("Failed to save booking to JSON file");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Add this helper method to get current user email from session
    private String getCurrentUserEmailFromSession() {
        try {
            UserSession userSession = UserSession.getInstance();
            User currentUser = userSession.getCurrentUser();

            if (currentUser != null && currentUser.getEmail() != null) {
                return currentUser.getEmail();
            } else {
                System.err.println("No user found in session, using fallback email");
                return currentUserEmail; // fallback to the field value
            }
        } catch (Exception e) {
            System.err.println("Error getting user email from session: " + e.getMessage());
            return currentUserEmail; // fallback to the field value
        }
    }
}