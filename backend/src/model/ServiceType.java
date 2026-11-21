package model;

import java.math.BigDecimal;

public class ServiceType {
    private int serviceTypeId;
    private String serviceName;
    private String description;
    private BigDecimal baseFare;
    private BigDecimal perBagCharge;
    private BigDecimal heavyLuggageCharge;
    private boolean active;

    public ServiceType() {}

    public int getServiceTypeId() { return serviceTypeId; }
    public void setServiceTypeId(int serviceTypeId) { this.serviceTypeId = serviceTypeId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }

    public BigDecimal getPerBagCharge() { return perBagCharge; }
    public void setPerBagCharge(BigDecimal perBagCharge) { this.perBagCharge = perBagCharge; }

    public BigDecimal getHeavyLuggageCharge() { return heavyLuggageCharge; }
    public void setHeavyLuggageCharge(BigDecimal heavyLuggageCharge) { this.heavyLuggageCharge = heavyLuggageCharge; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}