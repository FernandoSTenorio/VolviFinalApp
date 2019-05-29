package com.fernandotenorio.volvifinalapp.Model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fernandotenorio.volvifinalapp.Listeners.OnBlogLoadListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnBlogSavedListener;
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

public final class Blogs {

    private final User user;

    public Blogs(final User user){
        this.user = user;
    }

    /**
     * This method is responsible to load the post into the database, by setting a Path child
     * @param id gets the post Id
     * @param listener save the Blog information into de database
     */
    public void load(String id, final OnBlogLoadListener listener){

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child(DataBaseConstants.POST_KEY)
                .child(id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onLoad(new Blog(dataSnapshot, user));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError.getMessage());
            }
        });
    }

    /**
     * Method responsible to add new Post on the Blog, and ensure that all the fields must be completed
     * @param title takes the input for the Post Title
     * @param description takes the inout for the Post Description
     * @param imageUrl takes a image selected for the Post Image
     * @param listener takes the Interface responsible to load the new post to the Blog
     */
    public void add(final String title, final String description, final Uri imageUrl,final OnBlogSavedListener listener) {
        final FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        if(imageUrl == null) {
            listener.onFailed("You should specify an image !");
        } else if(TextUtils.isEmpty(title)){
            listener.onFailed("You should specify a title !");
        } else if(TextUtils.isEmpty(description)){
            listener.onFailed("You should specify a description !");
        } else {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                    .child("Volvi_Blog_Images")
                    .child(imageUrl.getLastPathSegment());

            filePath.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()){
                                        if(data.exists()){

                                            String profilePic = data.child("profilePicture").getValue(String.class);

                                            final Map<String, Object> map = new HashMap<>();
                                            map.put("title", title);
                                            map.put("description", description);
                                            map.put("image", uri.toString());
                                            map.put("createdDate", System.currentTimeMillis());
                                            map.put("uid", user.uid());
                                            map.put("author", firebaseAuth.getDisplayName());
                                            map.put("profileImage", profilePic);
                                            map.put("nbLikes", 0);
                                            map.put("nbComments", 0);

                                            DatabaseReference blogReference = FirebaseDatabase.getInstance().getReference()
                                                    .child(DataBaseConstants.POST_KEY)
                                                    .push();

                                            final String key = blogReference.getKey();
                                            blogReference.setValue(map)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child(DataBaseConstants.POST_KEY)
                                                                    .child(key)
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            listener.onSaved(new Blog(dataSnapshot, user));
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                            listener.onFailed("Failed to load blog saved !");
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
