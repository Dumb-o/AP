package Admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import Session.UserSession;
import Storage.JSONHandler;
import Storage.AdminJSONHandler;
import Models.Emergency;
import Models.Booking;
import Models.Trek;
import Models.Attraction;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardContentController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label totalTouristsLabel;
    @FXML private Label activeGuidesLabel;
    @FXML private Label totalEmergenciesLabel;
    @FXML private VBox activityList;
    @FXML private BarChart<String, Number> bookingTrendsChart;

    private JSONHandler fileManager;
    private AdminJSONHandler adminJsonHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileManager = new JSONHandler();
        adminJsonHandler = new AdminJSONHandler();

        updateCurrentDate();
        loadDashboardData();
        setupSimplifiedBookingTrendsChart();
        loadSimplifiedRecentActivities();
    }

    private void updateCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        currentDateLabel.setText(now.format(formatter) + " - Dashboard Overview");
    }

    private void loadDashboardData() {
        // Set welcome message
        if (UserSession.getInstance().getCurrentUser() != null) {
            welcomeLabel.setText("Namaste " + UserSession.getInstance().getCurrentUserFullName() + ",");
        }

        // Load statistics
        totalTouristsLabel.setText(String.valueOf(getTotalTourists()));
        activeGuidesLabel.setText(String.valueOf(getTotalGuides()));
        totalEmergenciesLabel.setText(String.valueOf(getTotalEmergencies()));
    }

    private int getTotalTourists() {
        try {
            return fileManager.loadUsers().size();
        } catch (Exception e) {
            System.err.println("Error loading tourists count: " + e.getMessage());
            return 0;
        }
    }

    private int getTotalGuides() {
        try {
            return adminJsonHandler.loadGuides().size();
        } catch (Exception e) {
            System.err.println("Error loading guides count: " + e.getMessage());
            return 0;
        }
    }

    private int getTotalEmergencies() {
        try {
            return adminJsonHandler.loadEmergencies().size();
        } catch (Exception e) {
            System.err.println("Error loading emergencies count: " + e.getMessage());
            return 0;
        }
    }

    private void setupSimplifiedBookingTrendsChart() {
        try {
            // Load bookings and related data
            List<Booking> allBookings = adminJsonHandler.loadBookings();
            List<Trek> allTreks = adminJsonHandler.loadTreks();
            List<Attraction> allAttractions = adminJsonHandler.loadAttractions();

            // Create a map of trek ID to attraction name
            Map<Integer, String> trekToAttractionMap = allTreks.stream()
                    .collect(Collectors.toMap(
                            Trek::getId,
                            trek -> {
                                Attraction attraction = allAttractions.stream()
                                        .filter(a -> a.getId() == trek.getAttractionId())
                                        .findFirst()
                                        .orElse(null);
                                return attraction != null ? attraction.getName() : "Unknown";
                            }
                    ));

            // Count bookings by attraction
            Map<String, Long> attractionBookingCounts = allBookings.stream()
                    .collect(Collectors.groupingBy(
                            booking -> trekToAttractionMap.getOrDefault(booking.getTrekId(), "Unknown"),
                            Collectors.counting()
                    ));

            // Clear existing data
            bookingTrendsChart.getData().clear();

            // Create chart series with better colors
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Bookings");

            // Add data to series (limit to top 8 attractions for readability)
            attractionBookingCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(8)
                    .forEach(entry -> {
                        // Truncate long attraction names for better display
                        String displayName = entry.getKey().length() > 15
                                ? entry.getKey().substring(0, 12) + "..."
                                : entry.getKey();
                        series.getData().add(new XYChart.Data<>(displayName, entry.getValue()));
                    });

            // Add series to chart
            bookingTrendsChart.getData().add(series);

            // Better chart styling with custom colors
            bookingTrendsChart.setAnimated(true);
            bookingTrendsChart.setLegendVisible(false);

            // Apply custom CSS for better colors
            bookingTrendsChart.setStyle(
                    ".chart-bar { " +
                            "    -fx-bar-fill: linear-gradient(to top, #667eea, #764ba2); " +
                            "} " +
                            ".chart-plot-background { " +
                            "    -fx-background-color: transparent; " +
                            "}"
            );

            System.out.println("Simplified booking trends chart updated with " + attractionBookingCounts.size() + " attractions");

        } catch (Exception e) {
            System.err.println("Error setting up simplified booking trends chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSimplifiedRecentActivities() {
        try {
            activityList.getChildren().clear();

            // Load recent emergencies
            List<Emergency> recentEmergencies = adminJsonHandler.loadEmergencies().stream()
                    .sorted((e1, e2) -> e2.getReportedAt().compareTo(e1.getReportedAt()))
                    .limit(6)
                    .toList();

            for (Emergency emergency : recentEmergencies) {
                VBox activityItem = createSimplifiedActivityItem(
                        getEmergencyIcon(emergency.getEmergencyType()),
                        "Emergency: " + emergency.getEmergencyType(),
                        "Reported by " + emergency.getGuideName() + " - " + emergency.getDescription(),
                        formatTimeAgo(emergency.getReportedAt()),
                        getEmergencyStatusColor(emergency.getEmergencyType())
                );
                activityList.getChildren().add(activityItem);
            }

            // Add some sample non-emergency activities for variety
            if (recentEmergencies.size() < 6) {
                activityList.getChildren().add(createSimplifiedActivityItem(
                        "👤", "New User Registration",
                        "Tourist registered for Everest Base Camp trek",
                        "2 hours ago", "#4CAF50"));

                activityList.getChildren().add(createSimplifiedActivityItem(
                        "📅", "Trek Booking",
                        "Annapurna Circuit trek booking confirmed",
                        "4 hours ago", "#2196F3"));
            }

        } catch (Exception e) {
            System.err.println("Error loading simplified recent activities: " + e.getMessage());
            e.printStackTrace();

            // Add error activity item
            activityList.getChildren().add(createSimplifiedActivityItem(
                    "❌", "System Error",
                    "Failed to load recent activities",
                    "Just now", "#F44336"));
        }
    }

    private VBox createSimplifiedActivityItem(String icon, String title, String description, String time, String statusColor) {
        VBox activityItem = new VBox(12);
        activityItem.setStyle(
                "-fx-background-color: #fafbfc; " +
                        "-fx-padding: 20; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #e1e5e9; " +
                        "-fx-border-radius: 15; " +
                        "-fx-border-width: 1; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );

        // Header with icon, title and time
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");

        VBox titleSection = new VBox(3);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");

        titleSection.getChildren().addAll(titleLabel, timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status indicator
        Label statusDot = new Label("●");
        statusDot.setStyle("-fx-font-size: 12px; -fx-text-fill: " + statusColor + ";");

        header.getChildren().addAll(iconLabel, titleSection, spacer, statusDot);

        // Description
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666; -fx-wrap-text: true;");
        descLabel.setWrapText(true);

        activityItem.getChildren().addAll(header, descLabel);
        return activityItem;
    }

    private String getEmergencyIcon(String emergencyType) {
        return switch (emergencyType.toLowerCase()) {
            case "medical emergency", "medical" -> "🏥";
            case "lost tourist", "lost" -> "🔍";
            case "weather emergency", "weather" -> "🌩️";
            case "equipment failure", "equipment" -> "⚙️";
            default -> "🚨";
        };
    }

    private String getEmergencyStatusColor(String emergencyType) {
        return switch (emergencyType.toLowerCase()) {
            case "medical emergency", "medical" -> "#F44336";
            case "lost tourist", "lost" -> "#FF9800";
            case "weather emergency", "weather" -> "#2196F3";
            default -> "#9C27B0";
        };
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        // Simplified time formatting - you can enhance this
        return "Recently";
    }
}