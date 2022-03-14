package com.example.fertisales;

import java.util.Map;

public class Stock {
    private String supplierName;
    private int supplierNumber;
    private String productName;
    private String chemicalName;
    private int quantity;
    private int paidPrice;
    private int sellPrice;
    private int mrp;
    private int finalAmount;
    private Map<String,String> timeStamp;
    private int numSales;

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public int getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(int supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getChemicalName() {
        return chemicalName;
    }

    public void setChemicalName(String chemicalName) {
        this.chemicalName = chemicalName;
    }

    public int getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(int paidPrice) {
        this.paidPrice = paidPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(int finalAmount) {
        this.finalAmount = finalAmount;
    }

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public Map<String, String> getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Map<String, String> timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getNumSales() {
        return numSales;
    }

    public void setNumSales(int numSales) {
        this.numSales = numSales;
    }

    public Stock(String supplierName
            , int supplierNumber
            , String productName
            , String chemicalName
            , int quantity
            , int paidPrice
            , int sellPrice
            , int mrp
            , int finalAmount
            , Map<String, String> timeStamp
            ) {
        this.supplierName = supplierName;
        this.supplierNumber = supplierNumber;
        this.productName = productName;
        this.chemicalName = chemicalName;
        this.paidPrice = paidPrice;
        this.quantity = quantity;
        this.sellPrice = sellPrice;
        this.finalAmount = finalAmount;
        this.mrp = mrp;
        this.timeStamp = timeStamp;
        numSales = 0;
    }

    public Stock() {
    }


}