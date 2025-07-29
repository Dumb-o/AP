package Admin;

import Models.Booking;
import Models.Trek;
import Models.Attraction;
import Models.User;
import Models.Guide;
import Storage.AdminJSONHandler;
import Storage.JSONHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AdminBookingsController implements Initializable {

    @FXML
    private TableView<BookingDisplayData> bookingsTable;

    @FXML
    private TableColumn<BookingDisplayData, String> bookingIdColumn;

    @FXML
    private TableColumn<BookingDisplayData, String> touristColumn;

    @FXML
    private TableColumn<BookingDisplayData, String> attractionColumn;

    @FXML
    private TableColumn<BookingDisplayData, String> guideColumn;

    @FXML
    private TableColumn<BookingDisplayData, String> dateColumn;

    @FXML
    private TableColumn<BookingDisplayData, String> amountColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button refreshButton;

    @FXML
    private Label totalBookingsLabel;

    private ObservableList<BookingDisplayData> allBookings;
    private ObservableList<BookingDisplayData> filteredBookings;
    private AdminJSONHandler jsonHandler;
    private JSONHandler userJsonHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jsonHandler = new AdminJSONHandler();
        userJsonHandler = new JSONHandler();

        setupTable();
        setupEventHandlers();
        loadAllBookingsInitial(); // Initial load without notification

        System.out.println("AdminBookingsController initialized - loading all bookings");
    }

    private void setupTable() {
        // Set up table columns
        bookingIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookingId()));

        touristColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTouristName()));

        attractionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAttractionName()));

        guideColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGuideName()));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFormattedDate()));

        amountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmount()));
    }

    private void setupEventHandlers() {
        // Refresh button shows notification
        refreshButton.setOnAction(e -> loadAllBookingsWithNotification());

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    // Initial loading without notification (called during initialization)
    private void loadAllBookingsInitial() {
        loadBookingsData(false);
    }

    // Refresh with notification (called when refresh button is pressed)
    private void loadAllBookingsWithNotification() {
        loadBookingsData(true);
    }

    // Core method that loads data with optional notification
    private void loadBookingsData(boolean showNotification) {
        try {
            System.out.println("Loading all bookings from JSON file...");

            // Get ALL bookings from JSON file
            List<Booking> allBookingsList = jsonHandler.loadBookings();
            System.out.println("Found " + allBookingsList.size() + " total bookings");

            // Convert to display data
            allBookings = FXCollections.observableArrayList();

            for (Booking booking : allBookingsList) {
                BookingDisplayData displayData = createDisplayData(booking);
                if (displayData != null) {
                    allBookings.add(displayData);
                }
            }

            updateBookingsDisplay(allBookings);
            applyFilters();

            // Only show success notification if requested (i.e., when refresh button is pressed)
            if (showNotification) {
                showSuccess();
            }

        } catch (Exception e) {
            System.err.println("Error loading all bookings: " + e.getMessage());
            e.printStackTrace();
            showAlert();
        }
    }

    private BookingDisplayData createDisplayData(Booking booking) {
        try {
            // Get trek information
            Trek trek = jsonHandler.getTrekById(booking.getTrekId());
            if (trek == null) {
                System.err.println("Trek not found for booking: " + booking.getBookingId());
                return null;
            }

            // Get attraction information
            Attraction attraction = jsonHandler.getAttractionById(trek.getAttractionId());
            String attractionName = (attraction != null) ? attraction.getName() : "Unknown Attraction";

            // Get tourist name from user email
            String touristName = getTouristNameByEmail(booking.getUserEmail());

            // Extract guide name from email
            String guideName = extractGuideName(booking.getGuideEmail());

            // Format date
            String formattedDate = formatDate(booking.getTrekStartDate());

            // Format amount
            String amount = String.format("$%.0f", trek.getCost());

            return new BookingDisplayData(
                    booking,
                    booking.getBookingId(),
                    touristName,
                    attractionName,
                    guideName,
                    formattedDate,
                    amount,
                    trek.getTrekName()
            );

        } catch (Exception e) {
            System.err.println("Error creating display data for booking: " + booking.getBookingId());
            e.printStackTrace();
            return null;
        }
    }

    private String getTouristNameByEmail(String userEmail) {
        try {
            if (userEmail == null || userEmail.isEmpty()) {
                return "Unknown Tourist";
            }

            // Load users from JSON file
            List<User> users = userJsonHandler.loadUsers();

            // Find user by email
            User user = users.stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(userEmail))
                    .findFirst()
                    .orElse(null);

            if (user != null) {
                return user.getFullName();
            } else {
                // If user not found, extract name from email
                return extractNameFromEmail(userEmail);
            }

        } catch (Exception e) {
            System.err.println("Error getting tourist name for email: " + userEmail);
            e.printStackTrace();
            return extractNameFromEmail(userEmail);
        }
    }

    private String extractNameFromEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "Unknown Tourist";
        }

        String name = email.split("@")[0];
        // Capitalize first letter and replace dots/underscores with spaces
        name = name.replace(".", " ").replace("_", " ");
        String[] parts = name.split(" ");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(part.substring(0, 1).toUpperCase())
                        .append(part.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    private String extractGuideName(String email) {
        if (email == null || email.isEmpty()) {
            return "Not assigned";
        }

        try {
            // Try to get guide from JSON first
            Guide guide = jsonHandler.getGuideByEmail(email);
            if (guide != null) {
                return guide.getFullName();
            }
        } catch (Exception e) {
            System.err.println("Error getting guide info: " + e.getMessage());
        }

        // Fallback to extracting from email
        return extractNameFromEmail(email);
    }

    private String formatDate(java.time.LocalDate date) {
        if (date == null) return "Not set";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return date.format(formatter);
    }

    private void applyFilters() {
        if (allBookings == null) return;

        String searchText = searchField.getText().toLowerCase().trim();

        filteredBookings = allBookings.filtered(booking -> {
            // Search filter
            boolean searchMatch = searchText.isEmpty() ||
                    booking.getBookingId().toLowerCase().contains(searchText) ||
                    booking.getTouristName().toLowerCase().contains(searchText) ||
                    booking.getAttractionName().toLowerCase().contains(searchText) ||
                    booking.getGuideName().toLowerCase().contains(searchText);

            return searchMatch;
        });

        bookingsTable.setItems(filteredBookings);
        updateTotalBookingsLabel();
    }

    private void updateBookingsDisplay(ObservableList<BookingDisplayData> bookings) {
        allBookings = bookings;
        bookingsTable.setItems(bookings);
        updateTotalBookingsLabel();
    }

    private void updateTotalBookingsLabel() {
        int totalCount = (filteredBookings != null) ? filteredBookings.size() :
                (allBookings != null) ? allBookings.size() : 0;
        totalBookingsLabel.setText("Total Bookings: " + totalCount);
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Failed to load bookings. Please try again.");
        alert.showAndWait();
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Data refreshed successfully!");
        alert.showAndWait();
    }


    public void refreshBookings() {
        loadAllBookingsWithNotification();
    }

    public static class BookingDisplayData {
        private final Booking originalBooking;
        private final String bookingId;
        private final String touristName;
        private final String attractionName;
        private final String guideName;
        private final String formattedDate;
        private final String amount;
        private final String trekName;

        public BookingDisplayData(Booking originalBooking, String bookingId, String touristName,
                                  String attractionName, String guideName, String formattedDate,
                                  String amount, String trekName) {
            this.originalBooking = originalBooking;
            this.bookingId = bookingId;
            this.touristName = touristName;
            this.attractionName = attractionName;
            this.guideName = guideName;
            this.formattedDate = formattedDate;
            this.amount = amount;
            this.trekName = trekName;
        }

        // Getters
        public String getBookingId() { return bookingId; }
        public String getTouristName() { return touristName; }
        public String getAttractionName() { return attractionName; }
        public String getGuideName() { return guideName; }
        public String getFormattedDate() { return formattedDate; }
        public String getAmount() { return amount; }
        public String getTrekName() { return trekName; }
    }
}
