package Tourist;

import Models.Booking;
import Models.Trek;
import Models.Attraction;
import Models.User;
import Session.UserSession;
import Storage.AdminJSONHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TouristBookingController implements Initializable {

    @FXML
    private TableView<BookingDisplayData> bookingsTable;

    @FXML
    private TableColumn<BookingDisplayData, String> touristColumn;

    @FXML
    private TableColumn<BookingDisplayData, String> guideColumn;

    @FXML
    private TableColumn<BookingDisplayData, String> attractionColumn;

    @FXML
    private TableColumn<BookingDisplayData, String> dateColumn;

    @FXML
    private VBox emptyStateContainer;

    private AdminJSONHandler jsonHandler;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jsonHandler = new AdminJSONHandler();
        UserSession userSession = UserSession.getInstance();
        currentUser = userSession.getCurrentUser();

        setupTable();
        loadUserBookings();

        if (currentUser != null) {
            System.out.println("TouristBookingController initialized for user: " + currentUser.getEmail());
        } else {
            System.err.println("WARNING: No user found in session!");
        }
    }

    private void setupTable() {
        touristColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTouristName()));

        guideColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGuideName()));

        attractionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAttractionName()));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFormattedDate()));
    }

    private void loadUserBookings() {
        try {
            if (currentUser == null) {
                System.err.println("No user logged in");
                showEmptyState();
                return;
            }

            String userEmail = currentUser.getEmail();
            System.out.println("Loading bookings for user: " + userEmail);

            List<Booking> userBookings = jsonHandler.getBookingsByUserEmail(userEmail);
            System.out.println("Found " + userBookings.size() + " bookings for user");

            ObservableList<BookingDisplayData> allBookings = FXCollections.observableArrayList();

            for (Booking booking : userBookings) {
                BookingDisplayData displayData = createDisplayData(booking);
                if (displayData != null) {
                    allBookings.add(displayData);
                }
            }

            bookingsTable.setItems(allBookings);

            if (allBookings.isEmpty()) {
                showEmptyState();
            } else {
                hideEmptyState();
            }

        } catch (Exception e) {
            System.err.println("Error loading user bookings: " + e.getMessage());
            e.printStackTrace();
            showAlert();
        }
    }

    private BookingDisplayData createDisplayData(Booking booking) {
        try {
            Trek trek = jsonHandler.getTrekById(booking.getTrekId());
            if (trek == null) {
                System.err.println("Trek not found for booking: " + booking.getBookingId());
                return null;
            }

            Attraction attraction = jsonHandler.getAttractionById(trek.getAttractionId());
            String attractionName = (attraction != null) ? attraction.getName() : "Unknown Attraction";

            String guideName = extractGuideName(booking.getGuideEmail());
            String formattedDate = formatDate(booking.getTrekStartDate());

            return new BookingDisplayData(
                    booking,
                    currentUser.getFullName(),
                    guideName,
                    attractionName,
                    formattedDate,
                    trek.getTrekName()
            );

        } catch (Exception e) {
            System.err.println("Error creating display data for booking: " + booking.getBookingId());
            e.printStackTrace();
            return null;
        }
    }

    private String extractGuideName(String email) {
        if (email == null || email.isEmpty()) {
            return "Not assigned";
        }
        String name = email.split("@")[0];
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String formatDate(java.time.LocalDate date) {
        if (date == null) return "Not set";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return date.format(formatter);
    }

    private void showEmptyState() {
        bookingsTable.setVisible(false);
        emptyStateContainer.setVisible(true);
    }

    private void hideEmptyState() {
        bookingsTable.setVisible(true);
        emptyStateContainer.setVisible(false);
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Failed to load bookings. Please try again.");
        alert.showAndWait();
    }

    // Inner class for table display data
    public static class BookingDisplayData {
        private final String touristName;
        private final String guideName;
        private final String attractionName;
        private final String formattedDate;

        public BookingDisplayData(Booking originalBooking, String touristName, String guideName,
                                  String attractionName, String formattedDate, String trekName) {
            this.touristName = touristName;
            this.guideName = guideName;
            this.attractionName = attractionName;
            this.formattedDate = formattedDate;
        }

        public String getTouristName() { return touristName; }
        public String getGuideName() { return guideName; }
        public String getAttractionName() { return attractionName; }
        public String getFormattedDate() { return formattedDate; }
    }
}
