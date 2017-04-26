package com.jcedar.paperbag.model;

/**
 * Created by OLUWAPHEMMY on 3/27/2017.
 */

public class Product {

    private String productID;
    private String productName;
    private String productPhoto;
    private String productDesc;
    private String productPrice;
    private String productQty;
    private String productQtyOrdered;
    private String productDateAdded;
    private String productSellerId;
    private String productCommentCount;
    private String productCategoryTitle;
    private String productCategoryId;

    public Product() {

    }

    public Product(String productID, String productName, String productPhoto, String productDesc, String productPrice,
                   String productQty, String productQtyOrdered, String productDateAdded, String productSellerId, String productCommentCount,
                   String productCategoryTitle, String productCategoryId) {
        this.productID = productID;
        this.productName = productName;
        this.productPhoto = productPhoto;
        this.productDesc = productDesc;
        this.productPrice = productPrice;
        this.productQty = productQty;
        this.productQtyOrdered = productQtyOrdered;
        this.productDateAdded = productDateAdded;
        this.productSellerId = productSellerId;
        this.productCommentCount = productCommentCount;
        this.productCategoryTitle = productCategoryTitle;
        this.productCategoryId= productCategoryId;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public void setProductPhoto(String productPhoto) {
        this.productPhoto = productPhoto;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getProductQtyOrdered() {
        return productQtyOrdered;
    }

    public void setProductQtyOrdered(String productQtyOrdered) {
        this.productQtyOrdered = productQtyOrdered;
    }

    public String getProductDateAdded() {
        return productDateAdded;
    }

    public void setProductDateAdded(String productDateAdded) {
        this.productDateAdded = productDateAdded;
    }

    public String getProductSellerId() {
        return productSellerId;
    }

    public void setProductSellerId(String productSellerId) {
        this.productSellerId = productSellerId;
    }

    public String getProductCommentCount() {
        return productCommentCount;
    }

    public void setProductCommentCount(String productCommentCount) {
        this.productCommentCount = productCommentCount;
    }

    public String getProductCategoryTitle() {
        return productCategoryTitle;
    }

    public void setProductCategoryTitle(String productCategoryTitle) {
        this.productCategoryTitle = productCategoryTitle;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }
}
