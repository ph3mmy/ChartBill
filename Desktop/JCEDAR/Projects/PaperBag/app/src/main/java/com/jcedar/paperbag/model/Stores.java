package com.jcedar.paperbag.model;

/**
 * Created by OLUWAPHEMMY on 3/27/2017.
 */

public class Stores {

    private String storeID;
    private String storeName;
    private String storeAddress;
    private String storeContactPhone;
    private String storeSellerId;
    private String storeSellerName;
    private String storeSellerEmail;
    private String storeSellerToken;

    public Stores() {
    }

    public Stores(String storeID, String storeName, String storeAddress, String storeContactPhone, String storeSellerId, String storeSellerName,
                  String storeSellerEmail, String storeSellerToken) {
        this.storeID = storeID;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeContactPhone = storeContactPhone;
        this.storeSellerId = storeSellerId;
        this.storeSellerName = storeSellerName;
        this.storeSellerEmail = storeSellerEmail;
        this.storeSellerToken = storeSellerToken;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreContactPhone() {
        return storeContactPhone;
    }

    public void setStoreContactPhone(String storeContactPhone) {
        this.storeContactPhone = storeContactPhone;
    }

    public String getStoreSellerId() {
        return storeSellerId;
    }

    public void setStoreSellerId(String storeSellerId) {
        this.storeSellerId = storeSellerId;
    }

    public String getStoreSellerName() {
        return storeSellerName;
    }

    public void setStoreSellerName(String storeSellerName) {
        this.storeSellerName = storeSellerName;
    }

    public String getStoreSellerEmail() {
        return storeSellerEmail;
    }

    public void setStoreSellerEmail(String storeSellerEmail) {
        this.storeSellerEmail = storeSellerEmail;
    }

    public String getStoreSellerToken() {
        return storeSellerToken;
    }

    public void setStoreSellerToken(String storeSellerToken) {
        this.storeSellerToken = storeSellerToken;
    }
}
