package com.fernandotenorio.volvifinalapp.Model;

import com.fernandotenorio.volvifinalapp.Utils.DataSnapshotPrinter;
import com.google.firebase.database.DataSnapshot;

public class Event {

    private String id;
    private String uid;
    private User user;
    private String eventTitle;
    private String desc;
    private String date;
    private Long dateAdded;
    private String eventImage;
    private String emailPoster;
    private String author;
    private String profileImage;
    private String fromTime;
    private String toTime;
    private String vetting;
    private String address;

    public Event() {

    }

    public Event(final DataSnapshot snapshot, final User user) {

        this.id = snapshot.getKey();
        this.user = user;

        DataSnapshotPrinter printer = new DataSnapshotPrinter(snapshot);

        this.uid = printer.print( "uid", String.class);
        this.eventTitle = printer.print("eventTitle", String.class);
        this.desc = printer.print("desc", String.class);
        this.date = printer.print("date", String.class);
        this.eventImage = printer.print("eventImage", String.class);
        this.dateAdded = printer.print("createdDate", Long.class);
        this.author = printer.print("user", String.class);
        this.profileImage = printer.print("profileImage", String.class);
        this.emailPoster = printer.print("emailPoster", String.class);
        this.fromTime = printer.print("fromTime", String.class);
        this.toTime = printer.print("toTime", String.class);
        this.vetting = printer.print("vetting", String.class);
        this.address = printer.print("address", String.class);

    }

    public Event(User user, String eventTitle, String desc, String date, Long dateAdded, String eventImage, String emailPoster) {
        this.user = user;
        this.eventTitle = eventTitle;
        this.desc = desc;
        this.date = date;
        this.dateAdded = dateAdded;
        this.eventImage = eventImage;
        this.eventImage = emailPoster;
    }

    public String id() {
        return id;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getEmailPoster() {
        return emailPoster;
    }

    public void setEmailPoster(String emailPoster) {
        this.emailPoster = emailPoster;
    }

    public String getId() {
        return uid;
    }

    public void setId(String id) {
        this.uid = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getVetting() {
        return vetting;
    }

    public void setVetting(String vetting) {
        this.vetting = vetting;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
