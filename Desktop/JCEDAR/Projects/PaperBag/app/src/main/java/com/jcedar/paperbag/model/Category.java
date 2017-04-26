package com.jcedar.paperbag.model;

/**
 * Created by OLUWAPHEMMY on 3/31/2017.
 */

public class Category {

    private String categoryId;
    private String categoryTitle;
    private String categoryImage;
    private String categoryDateAdded;

    public Category() {
    }

    public Category(String categoryId, String categoryTitle, String categoryImage, String categoryDateAdded) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.categoryImage = categoryImage;
        this.categoryDateAdded = categoryDateAdded;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryDateAdded() {
        return categoryDateAdded;
    }

    public void setCategoryDateAdded(String categoryDateAdded) {
        this.categoryDateAdded = categoryDateAdded;
    }
}
