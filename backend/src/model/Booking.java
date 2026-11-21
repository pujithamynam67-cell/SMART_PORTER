package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Booking {
    private int bookingId;
    private String bookingCode;
    private int passengerId;
    private Integer porterId;
    private int stationId;
    private int pickupLocationId;
    private int dropLocationId;
    private int serviceTypeId;
    private String status;
    private BigDecimal estimatedFare;
    private BigDecimal finalFare;
    private String specialInstructions;
    private Timestamp bookedAt;
    private Timestamp acceptedAt;
    private Timestamp startedAt;
    private Timestamp completedAt;
    private Timestamp cancelledAt;
    private String cancellationReason;

    // Joined / display fields
    private String passengerName;
    private String porterName;
    private String stationName;
    private String pickupLocation;
    private String dropLocation;
    private String serviceName;
    private int numberOfBags;
    private String luggageType;
    private boolean heavy;
    private String paymentStatus;
    private String paymentMethod;

    public Booking() {}

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }

    public Integer getPorterId() { return porterId; }
    public void setPorterId(Integer porterId) { this.porterId = porterId; }

    public int getStationId() { return stationId; }
    public void setStationId(int stationId) { this.stationId = stationId; }

    public int getPickupLocationId() { return pickupLocationId; }
    public void setPickupLocationId(int pickupLocationId) { this.pickupLocationId = pickupLocationId; }

    public int getDropLocationId() { return dropLocationId; }
    public void setDropLocationId(int dropLocationId) { this.dropLocationId = dropLocationId; }

    public int getServiceTypeId() { return serviceTypeId; }
    public void setServiceTypeId(int serviceTypeId) { this.serviceTypeId = serviceTypeId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getEstimatedFare() { return estimatedFare; }
    public void setEstimatedFare(BigDecimal estimatedFare) { this.estimatedFare = estimatedFare; }

    public BigDecimal getFinalFare() { return finalFare; }
    public void setFinalFare(BigDecimal finalFare) { this.finalFare = finalFare; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public Timestamp getBookedAt() { return bookedAt; }
    public void setBookedAt(Timestamp bookedAt) { this.bookedAt = bookedAt; }

    public Timestamp getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Timestamp acceptedAt) { this.acceptedAt = acceptedAt; }

    public Timestamp getStartedAt() { return startedAt; }
    public void setStartedAt(Timestamp startedAt) { this.startedAt = startedAt; }

    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }

    public Timestamp getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Timestamp cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getPorterName() { return porterName; }
    public void setPorterName(String porterName) { this.porterName = porterName; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDropLocation() { return dropLocation; }
    public void setDropLocation(String dropLocation) { this.dropLocation = dropLocation; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public int getNumberOfBags() { return numberOfBags; }
    public void setNumberOfBags(int numberOfBags) { this.numberOfBags = numberOfBags; }

    public String getLuggageType() { return luggageType; }
    public void setLuggageType(String luggageType) { this.luggageType = luggageType; }

    public boolean isHeavy() { return heavy; }
    public void setHeavy(boolean heavy) { this.heavy = heavy; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}