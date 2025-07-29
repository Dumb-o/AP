package Tourist;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TouristDashboardContentController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox upcomingTrekContainer;

    @FXML
    private Label trekNameLabel;

    @FXML
    private Label trekLocationLabel;

    @FXML
    private Label difficultyLabel;

    @FXML
    private Label trekDescriptionLabel;

    @FXML
    private Label trekWarningLabel;

    @FXML
    private Label activeTripsLabel;

    @FXML
    private GridPane calendarGrid;

    private TrekBooking upcomingTrek;
    private int activeBookingsCount = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupWelcomeMessage();
        loadUpcomingTrek();
        updateActiveTripsCount();
        setupCalendar();
    }

    private void setupWelcomeMessage() {
        // You can customize this based on time of day
        LocalDate now = LocalDate.now();
        String timeGreeting = getTimeBasedGreeting();
        welcomeLabel.setText(timeGreeting + " Tourist,");
    }

    private String getTimeBasedGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) {
            return "Good Morning";
        } else if (hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    private void loadUpcomingTrek() {
        // In a real application, you would fetch this from a database
        upcomingTrek = getUpcomingTrekFromDatabase();

        if (upcomingTrek != null) {
            displayTrekInfo(upcomingTrek);
        } else {
            displayNoTrekInfo();
        }
    }

    private void displayTrekInfo(TrekBooking trek) {
        trekNameLabel.setText(trek.getName());
        trekLocationLabel.setText(trek.getLocation());
        trekDescriptionLabel.setText(trek.getDescription());

        // Set difficulty label with appropriate color
        difficultyLabel.setText(trek.getDifficulty());
        setDifficultyLabelStyle(trek.getDifficulty());

        // Hide warning label when trek is available
        trekWarningLabel.setVisible(false);
    }

    private void displayNoTrekInfo() {
        trekNameLabel.setText("No upcoming trek");
        trekLocationLabel.setText("Location not available");
        trekDescriptionLabel.setText("No trek information available.");
        difficultyLabel.setText("N/A");
        difficultyLabel.setStyle("-fx-background-color: #9E9E9E; -fx-background-radius: 15; -fx-padding: 5 10;");
        trekWarningLabel.setVisible(true);
    }

    private void setDifficultyLabelStyle(String difficulty) {
        String style = "-fx-background-radius: 15; -fx-padding: 5 10; -fx-text-fill: white;";

        switch (difficulty.toLowerCase()) {
            case "easy":
                difficultyLabel.setStyle(style + " -fx-background-color: #4CAF50;");
                break;
            case "moderate":
                difficultyLabel.setStyle(style + " -fx-background-color: #FF9800;");
                break;
            case "hard":
                difficultyLabel.setStyle(style + " -fx-background-color: #F44336;");
                break;
            case "extreme":
                difficultyLabel.setStyle(style + " -fx-background-color: #9C27B0;");
                break;
            default:
                difficultyLabel.setStyle(style + " -fx-background-color: #9E9E9E;");
        }
    }

    private void updateActiveTripsCount() {
        // In a real application, you would fetch this from a database
        activeBookingsCount = getActiveBookingsCount();
        activeTripsLabel.setText(String.valueOf(activeBookingsCount));
    }

    private void setupCalendar() {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        int daysInMonth = now.lengthOfMonth();
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0

        // Clear existing calendar
        calendarGrid.getChildren().clear();

        // Add day headers
        String[] dayHeaders = {"S", "M", "T", "W", "T", "F", "S"};
        for (int i = 0; i < 7; i++) {
            Label dayHeader = new Label(dayHeaders[i]);
            dayHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #666666; -fx-font-size: 10px;");
            dayHeader.setAlignment(Pos.CENTER);
            dayHeader.setPrefSize(25, 20);
            calendarGrid.add(dayHeader, i, 0);
        }

        // Add calendar days
        int row = 1;
        int col = startDayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefSize(25, 20);
            dayLabel.setStyle("-fx-font-size: 10px;");

            // Highlight current day
            if (day == now.getDayOfMonth()) {
                dayLabel.setStyle("-fx-font-size: 10px; -fx-background-color: #e53e3e; -fx-text-fill: white; -fx-background-radius: 10;");
            }

            calendarGrid.add(dayLabel, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    // Mock methods - replace with actual database calls
    private TrekBooking getUpcomingTrekFromDatabase() {
        // Return null for now - in real app, query database for upcoming bookings
        // return new TrekBooking("Everest Base Camp", "Nepal", "Easy", "A beautiful trek to the base camp of Mount Everest.");
        return null;
    }

    private int getActiveBookingsCount() {
        // Return 0 for now - in real app, query database for active bookings count
        return 0;
    }

    // Method to refresh dashboard data
    public void refreshDashboard() {
        loadUpcomingTrek();
        updateActiveTripsCount();
        setupCalendar();
    }

    // Method to update trek information when new booking is made
    public void updateTrekInfo(TrekBooking newTrek) {
        this.upcomingTrek = newTrek;
        displayTrekInfo(newTrek);
    }

    // Inner class for trek booking data
    public static class TrekBooking {
        private String name;
        private String location;
        private String difficulty;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;

        public TrekBooking(String name, String location, String difficulty, String description) {
            this.name = name;
            this.location = location;
            this.difficulty = difficulty;
            this.description = description;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }
}
