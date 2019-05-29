package com.fernandotenorio.volvifinalapp.Model;


import android.support.annotation.NonNull;

import com.fernandotenorio.volvifinalapp.Listeners.OnUserLoadListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public final class Users {

    public void loadCurrentUser(final OnUserLoadListener listener){

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        if(fbUser == null){
            listener.onFailed("No user connected !");
        } else {
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(fbUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listener.onLoad(new User(dataSnapshot));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            listener.onFailed(databaseError.getMessage());
                        }
                    });
        }
    }
}
