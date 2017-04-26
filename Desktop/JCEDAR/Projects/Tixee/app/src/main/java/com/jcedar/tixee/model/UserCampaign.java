package com.jcedar.tixee.model;

/**
 * Created by OLUWAPHEMMY on 2/14/2017.
 */

public class UserCampaign {

    private String campaignName;
    private String campaignId;
    private int userNumberOfTicket;
    private int userUsedTicket;

    public UserCampaign() {
    }

    public UserCampaign(String campaignName, String campaignId, int userNumberOfTicket, int userUsedTicket) {
        this.campaignName = campaignName;
        this.userNumberOfTicket = userNumberOfTicket;
        this.userUsedTicket = userUsedTicket;
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }



    public int getUserNumberOfTicket() {
        return userNumberOfTicket;
    }

    public void setUserNumberOfTicket(int userNumberOfTicket) {
        this.userNumberOfTicket = userNumberOfTicket;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public int getUserUsedTicket() {
        return userUsedTicket;
    }

    public void setUserUsedTicket(int userUsedTicket) {
        this.userUsedTicket = userUsedTicket;
    }
}
