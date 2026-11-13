package com.management.librarymanagementcrudsystem.libraryDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class LibraryRequestDTO {
    @NotBlank(message = "Book Name Is Required.")
    private String name;
    @Positive(message = "ISBN NO. Must be Positive.")
    private int isbnNo;
    @Positive(message = "Price must be positive.")
    private float price;
    @Min(value = 0, message = "Stock Must be Positive")
    private int availableStock;

    public LibraryRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsbnNo() {
        return isbnNo;
    }

    public void setIsbnNo(int isbnNo) {
        this.isbnNo = isbnNo;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

}