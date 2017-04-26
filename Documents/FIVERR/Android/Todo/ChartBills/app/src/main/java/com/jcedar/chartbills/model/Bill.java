package com.jcedar.chartbills.model;

/**
 * Created by OLUWAPHEMMY on 3/22/2017.
 */

public class Bill {

    private String Id;
    private String dueDate;
    private String description;
    private String repeatFreq;
    private String amount;
    private String memo;
    private String status;
    private String week;
    private String month;
    private String year;
    private String alarmId;

    public Bill(String id, String dueDate, String description, String repeatFreq, String amount,
                String memo, String status, String week, String month, String year, String alarmId) {
        this.Id = id;
        this.dueDate = dueDate;
        this.description = description;
        this.repeatFreq = repeatFreq;
        this.amount = amount;
        this.memo = memo;
        this.status = status;
        this.week = week;
        this.year = year;
        this.month = month;
        this.alarmId = alarmId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepeatFreq() {
        return repeatFreq;
    }

    public void setRepeatFreq(String repeatFreq) {
        this.repeatFreq = repeatFreq;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }
}
