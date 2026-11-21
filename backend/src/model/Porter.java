package model;

import java.math.BigDecimal;

public class Porter {
    private int porterId;
    private int userId;
    private String fullName;
    private int experienceYears;
    private String profilePhoto;
    private String verificationStatus;
    private BigDecimal averageRating;
    private int totalRatings;
    private boolean available;
    private Integer currentStationId;
    private String stationName;
    private String email;
    private String phone;

    public Porter() {}

    public int getPorterId() { return porterId; }
    public void setPorterId(int porterId) { this.porterId = porterId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Integer getCurrentStationId() { return currentStationId; }
    public void setCurrentStationId(Integer currentStationId) { this.currentStationId = currentStationId; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}