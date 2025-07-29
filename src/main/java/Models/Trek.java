package Models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Trek {
    private int id;
    private String trekName;
    private String duration;
    private transient LocalDate startDate;  // won't be serialized directly
    private String startDateStr;            // serialized as string
    private String difficulty;
    private String maxAltitude;
    private double cost;
    private String bestSeason;
    private String guideEmail;
    private int attractionId;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Trek() {}

    public Trek(String trekName, String duration, LocalDate startDate, String difficulty, String maxAltitude,
                double cost, String bestSeason, String guideEmail, int attractionId) {
        this.trekName = trekName;
        this.duration = duration;
        this.startDate = startDate;
        this.startDateStr = startDate.format(FORMATTER);  // Store formatted date string
        this.difficulty = difficulty;
        this.maxAltitude = maxAltitude;
        this.cost = cost;
        this.bestSeason = bestSeason;
        this.guideEmail = guideEmail;
        this.attractionId = attractionId;
    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTrekName() { return trekName; }
    public String getDuration() { return duration; }
    public LocalDate getStartDate() {
        if (startDate == null && startDateStr != null) {
            startDate = LocalDate.parse(startDateStr, FORMATTER);
        }
        return startDate;
    }

    public String getDate() {
        return (getStartDate() != null) ? getStartDate().format(FORMATTER) : "";
    }

    public void setDate(String dateStr) {
        this.startDateStr = dateStr;
        this.startDate = LocalDate.parse(dateStr, FORMATTER);
    }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getMaxAltitude() { return maxAltitude; }

    public double getCost() { return cost; }

    public String getBestSeason() { return bestSeason; }

    public String getGuideEmail() { return guideEmail; }
    public void setGuideEmail(String guideEmail) { this.guideEmail = guideEmail; }

    public int getAttractionId() { return attractionId; }

    @Override
    public String toString() {
        return "Trek{" +
                "id=" + id +
                ", trekName='" + trekName + '\'' +
                ", startDate=" + getStartDate() +
                ", difficulty='" + difficulty + '\'' +
                ", attractionId=" + attractionId +
                ", guideEmail='" + guideEmail + '\'' +
                '}';
    }
}
