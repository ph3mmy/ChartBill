package com.jcedar.tixee.model;

/**
 * Created by OLUWAPHEMMY on 2/4/2017.
 */

public class Ticket {

    private String tId;
    private String campaignId;
    private String campaignName;
    private String ticketId;
    private String status;
    private String ticketType;
    private String genDate;
    private String genBy;
    private String contactName;
    private String contactPhone;

    public Ticket() {

    }

    public Ticket(String tId, String campaignId, String ticketId, String status, String ticketType, String contactName, String contactPhone, String campaignName, String genBy, String genDate) {
        this.tId = tId;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.ticketId = ticketId;
        this.status = status;
        this.ticketType = ticketType;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.genBy = genBy;
        this.genDate = genDate;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getGenBy() {
        return genBy;
    }

    public void setGenBy(String genBy) {
        this.genBy = genBy;
    }

    public String getGenDate() {
        return genDate;
    }

    public void setGenDate(String genDate) {
        this.genDate = genDate;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
}
