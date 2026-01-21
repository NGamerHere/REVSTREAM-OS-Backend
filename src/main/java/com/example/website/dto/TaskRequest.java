package com.example.website.dto;

import java.time.LocalDate;
import java.util.List;

public class TaskRequest {

    private String title;
    private String description;
    private LocalDate dueDate;
    private String urgency;

    private List<String> attachments;
    private String note;

    private double price;          
    private List<String> skills;   

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}
