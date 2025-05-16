package model;

public class InvoiceCostume {
    private int id;
    private int quantity;
    private float pricePerUnit;
    private String status;
    private float penaltyFee;
    private int invoiceId;
    private int costumeId;

    // Để hiển thị trong UI
    private String costumeName;

    public InvoiceCostume() {
    }

    public InvoiceCostume(int id, int quantity, float pricePerUnit, String status, float penaltyFee, int invoiceId, int costumeId) {
        this.id = id;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.status = status;
        this.penaltyFee = penaltyFee;
        this.invoiceId = invoiceId;
        this.costumeId = costumeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(float pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(float penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getCostumeId() {
        return costumeId;
    }

    public void setCostumeId(int costumeId) {
        this.costumeId = costumeId;
    }

    public String getCostumeName() {
        return costumeName;
    }

    public void setCostumeName(String costumeName) {
        this.costumeName = costumeName;
    }
}