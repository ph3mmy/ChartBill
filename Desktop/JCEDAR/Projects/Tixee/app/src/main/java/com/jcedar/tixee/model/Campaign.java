package com.jcedar.tixee.model;

/**
 * Created by OLUWAPHEMMY on 2/4/2017.
 */

public class Campaign {

    private String Id;
    private String name;
    private int numberOfTickets;
    private int ticketLeft;
    private int adminUsed;
    private String imageUrl;

    public Campaign () {

    }

    public Campaign(String id, String name, int numberOfTickets, int ticketLeft, int adminUsed, String imageUrl) {
        Id = id;
        this.name = name;
        this.numberOfTickets = numberOfTickets;
        this.ticketLeft = ticketLeft;
        this.adminUsed = adminUsed;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public int getTicketLeft() {
        return ticketLeft;
    }

    public void setTicketLeft(int ticketLeft) {
        this.ticketLeft = ticketLeft;
    }

    public int getAdminUsed() {
        return adminUsed;
    }

    public void setAdminUsed(int adminUsed) {
        this.adminUsed = adminUsed;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
