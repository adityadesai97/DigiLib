package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 12/07/17.
 */

public class Book {
    private String name;
    private String author;
    private String salePrice;
    private String rentalPrice;
    private String coverImageURL;
    private String genreName;

    public Book(String name, String author, String salePrice, String rentalPrice, String coverImageURL, String genreName) {
        this.name = name;
        this.author = author;
        this.salePrice = salePrice;
        this.rentalPrice = rentalPrice;
        this.coverImageURL = coverImageURL;
        this.genreName = genreName;
    }

    public Book() {
        this.name = "dummy";
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

    public String getCoverImageURL() {
        return coverImageURL;
    }

    public String getGenreName() {
        return genreName;
    }
}
