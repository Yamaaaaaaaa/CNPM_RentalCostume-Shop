package model;

public class Costume {
    private int id;
    private String name;
    private String description;
    private int stockQuantity;
    private float rentalPrice;
    private float originalCost;
    private int styleCostumeId;
    private int typeCostumeId;
    // Để hiển thị trong UI
    private String styleCostumeName;
    private String typeCostumeName;
    private float totalRented;

    public Costume(int id, String name, String description, int stockQuantity, float rentalPrice, float originalCost, int styleCostumeId, int typeCostumeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.rentalPrice = rentalPrice;
        this.originalCost = originalCost;
        this.styleCostumeId = styleCostumeId;
        this.typeCostumeId = typeCostumeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public float getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(float rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public float getOriginalCost() {
        return originalCost;
    }

    public void setOriginalCost(float originalCost) {
        this.originalCost = originalCost;
    }

    public int getStyleCostumeId() {
        return styleCostumeId;
    }

    public void setStyleCostumeId(int styleCostumeId) {
        this.styleCostumeId = styleCostumeId;
    }

    public int getTypeCostumeId() {
        return typeCostumeId;
    }

    public void setTypeCostumeId(int typeCostumeId) {
        this.typeCostumeId = typeCostumeId;
    }

    public String getStyleCostumeName() {
        return styleCostumeName;
    }

    public void setStyleCostumeName(String styleCostumeName) {
        this.styleCostumeName = styleCostumeName;
    }

    public String getTypeCostumeName() {
        return typeCostumeName;
    }

    public void setTypeCostumeName(String typeCostumeName) {
        this.typeCostumeName = typeCostumeName;
    }

    @Override
    public String toString() {
        return name;
    }
}