package Admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import Session.UserSession;
import Storage.JSONHandler;
import Storage.AdminJSONHandler;
import Models.Emergency;
import Models.Booking;
import Models.Trek;
import Models.Attraction;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardContentController implements Initializable {

    @FXML private Label welcomeLabel;
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
        loadDashboardData();
        setupBookingTrendsChart();
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

        // Load recent activities
        loadRecentActivities();
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

    private void setupBookingTrendsChart() {
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
                                return attraction != null ? attraction.getName() : "Unknown Attraction";
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

            // Create chart series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Tourist Bookings");

            // Add data to series (limit to top 10 attractions)
            attractionBookingCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> {
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    });

            // Add series to chart
            bookingTrendsChart.getData().add(series);

            // Style the chart
            bookingTrendsChart.setTitle("Booking Trends by Attraction");
            bookingTrendsChart.setLegendVisible(false);

            System.out.println("Booking trends chart updated with " + attractionBookingCounts.size() + " attractions");

        } catch (Exception e) {
            System.err.println("Error setting up booking trends chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRecentActivities() {
        try {
            activityList.getChildren().clear();

            // Load recent emergencies
            List<Emergency> recentEmergencies = adminJsonHandler.loadEmergencies().stream()
                    .sorted((e1, e2) -> e2.getReportedAt().compareTo(e1.getReportedAt()))
                    .limit(3)
                    .toList();

            for (Emergency emergency : recentEmergencies) {
                addActivityItem(
                        "🚨 Emergency Reported",
                        emergency.getEmergencyType() + " by " + emergency.getGuideName()
                );
            }
        } catch (Exception e) {
            System.err.println("Error loading recent activities: " + e.getMessage());
            e.printStackTrace();
            addActivityItem("Error", "Failed to load recent activities");
        }
    }

    private void addActivityItem(String title, String description) {
        Label item = new Label(title + "\n" + description);
        item.setWrapText(true);
        activityList.getChildren().add(item);
    }

}
