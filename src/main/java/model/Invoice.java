package model;

import java.util.Date;

public class Invoice {
    private int id;
    private float deposit;
    private float totalAmount;
    private Date rentalDate;
    private Date returnDate;
    private int managerId;
    private String typeInvoice;
    private int customerId;

    // Để hiển thị trong UI
    private String managerName;
    private String customerName;

    public Invoice() {
    }

    public Invoice(int id, float deposit, float totalAmount, Date rentalDate, Date returnDate, int managerId, String typeInvoice, int customerId) {
        this.id = id;
        this.deposit = deposit;
        this.totalAmount = totalAmount;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.managerId = managerId;
        this.typeInvoice = typeInvoice;
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getDeposit() {
        return deposit;
    }

    public void setDeposit(float deposit) {
        this.deposit = deposit;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(Date rentalDate) {
        this.rentalDate = rentalDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getTypeInvoice() {
        return typeInvoice;
    }

    public void setTypeInvoice(String typeInvoice) {
        this.typeInvoice = typeInvoice;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}