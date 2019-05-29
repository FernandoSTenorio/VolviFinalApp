package com.fernandotenorio.volvifinalapp.Model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fernandotenorio.volvifinalapp.Listeners.OnEventLoadListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnEventSavedListener;
import com.fernandotenorio.volvifinalapp.Utils.DataBaseConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Events {

    private final User user;

    public Events(final User user){
        this.user = user;
    }

    /**
     * Methos responsible to load the event into de database by setting a Reference Path
     * @param id takes the event id
     * @param listener takes the on load interface
     */
    public void load(String id, final OnEventLoadListener listener){

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child(DataBaseConstants.EVENT_KEY)
                .child(id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onLoad(new Event(dataSnapshot, user));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError.getMessage());
            }
        });
    }

    /**
     * This method is responsible to get all the information needed to add an Event into the app. The user will have some fields that must be completed.
     * @param title takes an input for the event title
     * @param description takes an input for the event description
     * @param eventDate takes an input for the event date
     * @param imageUrl takes a selected picture for the event's image
     * @param fromTime takes an inout for the event start time
     * @param toTime takes an input for the event ending time
     * @param vetting takes a selection of Yes/No for the event garda vetting
     * @param listener takes the on Saved listener, responsible to get the Event's information
     */
    public void add(final String title, final String description, final String eventDate, final Uri imageUrl, final String fromTime, final String toTime, final String vetting, final OnEventSavedListener listener) {
        final FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        if(imageUrl == null) {
            listener.onFailed("You should specify an image !");
        } else if(TextUtils.isEmpty(title)){
            listener.onFailed("You should specify a title !");
        } else if(TextUtils.isEmpty(description)){
            listener.onFailed("You should specify a description !");
        } else if(TextUtils.isEmpty(eventDate)){
            listener.onFailed("You should specify a date");
        }else if(TextUtils.isEmpty(fromTime)){
            listener.onFailed("You should specify a start time");

        }else if(TextUtils.isEmpty(toTime)){
            listener.onFailed("You should specify end time");

        }else if(TextUtils.isEmpty(vetting)){
            listener.onFailed("Specify if event require Vetting");

        }else {

            // Creates a connection to the FireBase Storage
            final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                    .child("Volvi_Events_Images")
                    .child(imageUrl.getLastPathSegment());

            filePath.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {

                            //Creates a connection to the FireBase and gets the current user
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()){
                                        if(data.exists()){

                                            String profilePic = data.child("profilePicture").getValue(String.class);
                                            String address = data.child("address").getValue(String.class);

                                            final Map<String, Object> map = new HashMap<>();
                                            map.put("eventTitle", title);
                                            map.put("desc", description);
                                            map.put("eventImage", uri.toString());
                                            map.put("date", eventDate);
                                            map.put("createdDate", System.currentTimeMillis());
                                            map.put("uid", user.uid());
                                            map.put("user", firebaseAuth.getDisplayName());
                                            map.put("profileImage", profilePic);
                                            map.put("emailPoster", firebaseAuth.getEmail());
                                            map.put("uid", user.uid());
                                            map.put("fromTime", fromTime);
                                            map.put("toTime", toTime);
                                            map.put("vetting", vetting);
                                            map.put("address", address);


                                            DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference()
                                                    .child(DataBaseConstants.EVENT_KEY)
                                                    .push();

                                            final String key = eventReference.getKey();
                                            eventReference.setValue(map)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child(DataBaseConstants.EVENT_KEY)
                                                                    .child(key)
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            listener.onSaved(new Event(dataSnapshot, user));
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                            listener.onFailed("Failed to load event saved !");
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            listener.onFailed("Failed during saving blog !");
                                                        }
                                                    });

                                        }

                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onFailed("Could not upload photo");
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    listener.onPhotoLoadProgress(currentProgress);
                }
            });
        }
    }

}
