package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 12/07/17.
 */

public class Book {
    private String name;
    private String author;
    private String category;

    public Book(String name, String author, String category) {
        this.name = name;
        this.author = author;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
