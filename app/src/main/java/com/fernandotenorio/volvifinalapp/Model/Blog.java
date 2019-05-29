package com.fernandotenorio.volvifinalapp.Model;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fernandotenorio.volvifinalapp.Listeners.OnBlogSavedListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnCommentListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnCommentLoadListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnHasLikeListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnLikeListener;
import com.fernandotenorio.volvifinalapp.Utils.DataBaseConstants;
import com.fernandotenorio.volvifinalapp.Utils.DataSnapshotPrinter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Blog {

    private final String id;
    private final String title;
    private final String description;
    private final String profileImage;
    private final String image;
    private final long createdDate;
    private final String author;
    private final String uid;
    private final long nbLikes;
    private final long nbComments;
    private final User user;




    public Blog(final DataSnapshot snapshot, final User user) {

        this.id = snapshot.getKey();
        this.user = user;

        DataSnapshotPrinter printer = new DataSnapshotPrinter(snapshot);

        this.title = printer.print("title", String.class);
        this.description = printer.print("description", String.class);
        this.image = printer.print("image", String.class);
        this.createdDate = printer.print("createdDate", Long.class);
        this.uid = printer.print( "uid", String.class);
        this.author = printer.print("author", String.class);
        this.profileImage = printer.print("profileImage", String.class);
        this.nbLikes = printer.print("nbLikes", Long.class);
        this.nbComments = printer.print("nbComments", Long.class);




    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String image() {
        return image;
    }

    public long createdDate() {
        return createdDate;
    }

    public String author() {
        return author;
    }

    public String uid() {
        return uid;
    }

    public String profileImage() {
        return profileImage;
    }

    public long nbLikes() {
        return nbLikes;
    }

    public long nbComments() {
        return nbComments;
    }

    public void loadComments(final OnCommentLoadListener listener){

        FirebaseDatabase.getInstance()
                .getReference()
                .child(DataBaseConstants.POST_KEY)
                .child(id())
                .child(DataBaseConstants.POST_COMMENT_KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Comment> comments = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            comments.add(new Comment(data));
                        }

                        Collections.reverse(comments);

                        listener.onLoad(comments);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onFailed(databaseError.getMessage());
                    }
                });
    }

    public void comment(String message, final OnCommentListener listener){

        FirebaseUser userAuth = FirebaseAuth.getInstance().getCurrentUser();
        if(TextUtils.isEmpty(message)){
            listener.onFailed("You should write a message !");
        } else {
            String id = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(DataBaseConstants.POST_KEY)
                    .child(id())
                    .child(DataBaseConstants.POST_COMMENT_KEY)
                    .push()
                    .getKey();

            Comment newComment = new Comment(id, id(), user.uid(), userAuth.getDisplayName(), message, System.currentTimeMillis());
            Map<String, Object> dataToSave = new HashMap<>();
            dataToSave.put(String.format("%s/%s/%s/%s", DataBaseConstants.POST_KEY, id(), DataBaseConstants.POST_COMMENT_KEY, id), newComment.toMap());

            FirebaseDatabase.getInstance()
                    .getReference()
                    .updateChildren(dataToSave, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                DatabaseReference postRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child(DataBaseConstants.POST_KEY + "/" + id() + "/nbComments");

                                incrementCommentsCount(postRef, listener);
                            } else {
                                System.err.println(databaseError.getMessage());
                                listener.onFailed(databaseError.getMessage());
                            }
                        }

                        private void incrementCommentsCount(DatabaseReference postRef, final OnCommentListener listener) {
                            postRef.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    Long currentValue = mutableData.getValue(Long.class);
                                    if (currentValue == null) {
                                        mutableData.setValue(1);
                                    } else {
                                        mutableData.setValue(currentValue + 1);
                                    }

                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                    System.out.println("Updating comments count transaction is completed.");
                                    listener.onComment(dataSnapshot.getValue(Long.class));
                                }
                            });
                        }

                    });
        }
    }

    public void like(final OnLikeListener listener){
        hasLike(new OnHasLikeListener(){

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            @Override
            public void onExists(boolean exists, Like like) {
                if(exists){
                    listener.onLike(nbLikes);
                }else{
                    String id = FirebaseDatabase.getInstance()
                            .getReference()
                            .child(DataBaseConstants.POST_KEY)
                            .child(id())
                            .child(DataBaseConstants.POST_LIKE_KEY)
                            .push()
                            .getKey();

                    Like newLike = new Like(id, id(),firebaseUser.getUid() , System.currentTimeMillis());
                    Map<String, Object> dataToSave = new HashMap<>();
                    dataToSave.put(String.format("%s/%s/%s/%s", DataBaseConstants.POST_KEY, id(), DataBaseConstants.POST_LIKE_KEY, id), newLike.toMap());

                    FirebaseDatabase.getInstance()
                            .getReference()
                            .updateChildren(dataToSave, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        DatabaseReference postRef = FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child(DataBaseConstants.POST_KEY + "/" + id() + "/nbLikes");

                                        incrementLikesCount(postRef, listener);
                                    } else {
                                        System.err.println(databaseError.getMessage());
                                        listener.onFailed(databaseError.getMessage());
                                    }
                                }

                                private void incrementLikesCount(DatabaseReference postRef, final OnLikeListener listener) {
                                    postRef.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            Long currentValue = mutableData.getValue(Long.class);
                                            if (currentValue == null) {
                                                mutableData.setValue(1);
                                            } else {
                                                mutableData.setValue(currentValue + 1);
                                            }

                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            System.out.println("Updating likes count transaction is completed.");
                                            listener.onLike(dataSnapshot.getValue(Long.class));
                                        }
                                    });
                                }

                            });
                }
            }

            @Override
            public void onFailed(String error) {
                listener.onFailed(error);
            }
        });
    }

    public void unlike(final OnLikeListener listener){
        hasLike(new OnHasLikeListener() {
            @Override
            public void onExists(boolean exists, Like like) {
                if(exists){
                    DatabaseReference mLikesReference = FirebaseDatabase.getInstance()
                            .getReference()
                            .child(DataBaseConstants.POST_KEY)
                            .child(id())
                            .child(DataBaseConstants.POST_LIKE_KEY)
                            .child(like.id());

                    mLikesReference.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                DatabaseReference postRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child(DataBaseConstants.POST_KEY + "/" + id() + "/nbLikes");
                                decrementLikesCount(postRef, listener);
                            } else {
                                listener.onFailed(databaseError.getMessage());
                            }
                        }

                        private void decrementLikesCount(DatabaseReference postRef, final OnLikeListener listener) {
                            postRef.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    Long currentValue = mutableData.getValue(Long.class);
                                    if (currentValue == null) {
                                        mutableData.setValue(0);
                                    } else {
                                        mutableData.setValue(currentValue - 1);
                                    }

                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                    System.out.println("Updating likes count transaction is completed.");
                                    listener.onLike(dataSnapshot.getValue(Long.class));
                                }
                            });
                        }
                    });
                } else {
                    listener.onLike(nbLikes);
                }
            }

            @Override
            public void onFailed(String error) {
                listener.onFailed(error);
            }
        });
    }

    public void hasLike(final OnHasLikeListener listener){

        DatabaseReference likeRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(DataBaseConstants.POST_KEY)
                .child(id())
                .child(DataBaseConstants.POST_LIKE_KEY);

        likeRef.orderByChild("uid")
                .equalTo(uid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Like like = new Like(data);
                                listener.onExists(true, like);
                                break;
                            }
                        } else {
                            listener.onExists(false, null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailed(databaseError.getMessage());
                    }
                });
    }

    void update(
            final String title,
            final String description,
            final String image,
            final String profileImage,
            final OnBlogSavedListener listener
    ){

        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description);
        map.put("image", image);
        map.put("profileImage", profileImage);

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
                        listener.onFailed("Failed during blog saving !");
                    }
                });
    }
}
