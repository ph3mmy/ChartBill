package com.jcedar.paperbag.model;

/**
 * Created by OLUWAPHEMMY on 3/27/2017.
 */

public class Comment {

    private String commentID;
    private String productId;
    private String productName;
    private String rating;
    private String comment;
    private String commentor;
    private String approvalCode;

    public Comment() {
    }

    public Comment(String commentID, String productId, String productName, String rating, String comment, String commentor, String approvalCode) {
        this.commentID = commentID;
        this.productId = productId;
        this.productName = productName;
        this.rating = rating;
        this.comment = comment;
        this.commentor = commentor;
        this.approvalCode = approvalCode;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentor() {
        return commentor;
    }

    public void setCommentor(String commentor) {
        this.commentor = commentor;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }
}
