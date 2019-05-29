package com.fernandotenorio.volvifinalapp.Model;

import com.fernandotenorio.volvifinalapp.Utils.DataSnapshotPrinter;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class User {

    public String firstName;
    public String lastName;
    public String email;
    public String dateOfBirth;
    public String accountType;
    public String profilePicture;
    public String uid;

    public User(final DataSnapshot snapshot) {
        this.uid = snapshot.getKey();

        DataSnapshotPrinter printer = new DataSnapshotPrinter(snapshot);
        this.firstName = printer.print("firstName", String.class);
        this.lastName = printer.print("lastName", String.class);
        this.email = printer.print("email", String.class);
        this.dateOfBirth = printer.print( "dateOfBirth", String.class);
        this.accountType = printer.print("accountType", String.class);
        this.profilePicture = printer.print("profilePicture", String.class);
    }

    public User(String uid, String firstName, String lastName, String email, String dateOfBirth, String accountType, String profilePicture) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.accountType = accountType;
        this.profilePicture = profilePicture;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String email() {
        return email;
    }

    public String dateOfBirth() {
        return dateOfBirth;
    }

    public String accountType() {
        return accountType;
    }

    public String profilePicture() {
        return profilePicture;
    }

    public String uid() {
        return uid;
    }

    public String displayName() {
        return String.format("%s %s", firstName, lastName);
    }

    public Map<String, Object> toMap(){

        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("email", email);
        map.put("dateOfBirth", dateOfBirth);
        map.put("accountType", accountType);
        map.put("profilePicture", profilePicture);

        return map;
    }
}
