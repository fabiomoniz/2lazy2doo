package com.example.a2lazy.models;

public class ProductModel {
    private String productId, productName;
    private Boolean izCompleted;

    public ProductModel() {}

    public ProductModel(String productId, String productName, Boolean izCompleted) {
        this.productId = productId;
        this.productName = productName;
        this.izCompleted = izCompleted;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Boolean getIzCompleted() {
        return izCompleted;
    }
}
