package Models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Trek {
    private int id;
    private String trekName;
    private String duration;
    private transient LocalDate startDate;
    private String startDateStr;
    private String difficulty;
    private String maxAltitude;
    private double cost;              // This will store the final cost (after discount)
    private String bestSeason;
    private String guideEmail;
    private int attractionId;

    // NEW: Discount-related fields
    private boolean hasDiscount;      // Boolean flag to indicate if trek has discount
    private double originalCost;      // Original cost before discount
    private double discountPercent;   // Discount percentage applied

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Trek() {
        this.hasDiscount = false;
        this.discountPercent = 0.0;
    }

    public Trek(String trekName, String duration, LocalDate startDate, String difficulty, String maxAltitude,
                double cost, String bestSeason, String guideEmail, int attractionId) {
        this.trekName = trekName;
        this.duration = duration;
        this.startDate = startDate;
        this.startDateStr = startDate.format(FORMATTER);
        this.difficulty = difficulty;
        this.maxAltitude = maxAltitude;
        this.cost = cost;  // This is the final cost
        this.bestSeason = bestSeason;
        this.guideEmail = guideEmail;
        this.attractionId = attractionId;

        // Initialize discount fields
        this.hasDiscount = false;
        this.originalCost = cost;  // Default original cost to final cost
        this.discountPercent = 0.0;
    }

    // Existing getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTrekName() { return trekName; }
    public void setTrekName(String trekName) { this.trekName = trekName; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public LocalDate getStartDate() {
        if (startDate == null && startDateStr != null) {
            startDate = LocalDate.parse(startDateStr, FORMATTER);
        }
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.startDateStr = startDate.format(FORMATTER);
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
    public void setMaxAltitude(String maxAltitude) { this.maxAltitude = maxAltitude; }

    public double getCost() { return cost; }  // Returns final cost
    public void setCost(double cost) { this.cost = cost; }

    public String getBestSeason() { return bestSeason; }
    public void setBestSeason(String bestSeason) { this.bestSeason = bestSeason; }

    public String getGuideEmail() { return guideEmail; }
    public void setGuideEmail(String guideEmail) { this.guideEmail = guideEmail; }

    public int getAttractionId() { return attractionId; }
    public void setAttractionId(int attractionId) { this.attractionId = attractionId; }

    // NEW: Discount-related getters/setters
    public boolean hasDiscount() { return hasDiscount; }
    public boolean getHasDiscount() { return hasDiscount; }  // For JSON serialization
    public void setHasDiscount(boolean hasDiscount) { this.hasDiscount = hasDiscount; }

    public double getOriginalCost() { return originalCost; }
    public void setOriginalCost(double originalCost) { this.originalCost = originalCost; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = Math.max(0, Math.min(100, discountPercent)); // Clamp between 0-100
    }

    // Utility methods
    public double getDiscountAmount() {
        if (hasDiscount) {
            return originalCost * (discountPercent / 100.0);
        }
        return 0.0;
    }

    public double getFinalCost() {
        return cost;  // The cost field stores the final cost
    }

    // Formatted cost methods
    public String getFormattedCost() {
        return String.format("$%.2f", cost);
    }

    public String getFormattedOriginalCost() {
        return String.format("$%.2f", originalCost);
    }

    public String getFormattedDiscountAmount() {
        return String.format("$%.2f", getDiscountAmount());
    }

    // Method to get cost display string
    public String getCostDisplayString() {
        if (hasDiscount) {
            return String.format("$%.2f (%.1f%% off from $%.2f)",
                    cost, discountPercent, originalCost);
        } else {
            return String.format("$%.2f", cost);
        }
    }

    @Override
    public String toString() {
        return "Trek{" +
                "id=" + id +
                ", trekName='" + trekName + '\'' +
                ", startDate=" + getStartDate() +
                ", difficulty='" + difficulty + '\'' +
                ", finalCost=" + cost +
                ", hasDiscount=" + hasDiscount +
                (hasDiscount ? ", originalCost=" + originalCost + ", discountPercent=" + discountPercent : "") +
                ", attractionId=" + attractionId +
                ", guideEmail='" + guideEmail + '\'' +
                '}';
    }
}