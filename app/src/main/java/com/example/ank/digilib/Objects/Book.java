package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 12/07/17.
 */

public class Book {
    private String name;
    private String author;
    private String salePrice;
    private String rentalPrice;

    public Book(String name, String author, String salePrice, String rentalPrice) {
        this.name = name;
        this.author = author;
        this.salePrice = salePrice;
        this.rentalPrice = rentalPrice;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public String getRentalPrice() {
        return rentalPrice;
    }
}
