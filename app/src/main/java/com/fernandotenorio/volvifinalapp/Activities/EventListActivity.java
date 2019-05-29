package com.fernandotenorio.volvifinalapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fernandotenorio.volvifinalapp.Data.EventRecycleAdapter;
import com.fernandotenorio.volvifinalapp.Listeners.OnUserLoadListener;
import com.fernandotenorio.volvifinalapp.Model.Event;
import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.Model.Users;
import com.fernandotenorio.volvifinalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    ArrayList<Event> events;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDataBase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private User currentUser;
    EventRecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        setTitle("Events");
        setTitleColor(R.color.colorPurpleRed);

        recyclerView = findViewById(R.id.eventListactivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(EventListActivity.this));
        recyclerView.setHasFixedSize(true);

        events = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDataBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDataBase.getReference().child("Volvi_Events");
        mDatabaseReference.keepSynced(true);

        new Users().loadCurrentUser(new OnUserLoadListener() {
            @Override
            public void onLoad(User user) {
                currentUser = user;
            }

            @Override
            public void onFailed(String error) {
                currentUser = null;
                Toast.makeText(EventListActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.event_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_addEvent:
                if(mUser !=null && mAuth != null){

                    startActivity(new Intent(EventListActivity.this, AddEventActivity.class));
                    finish();

                }
                break;

            case R.id.action_signout:

                if(mUser !=null && mAuth != null){
                    mAuth.signOut();

                    startActivity(new Intent(EventListActivity.this, MainActivity.class));
                    finish();

                }
                break;

            case R.id.action_calendar:
                if(mUser !=null && mAuth != null){

                    startActivity(new Intent(EventListActivity.this, CalendarActivity.class));
                    finish();

                }
                break;

            case R.id.action_bloglis:
                if(mUser !=null && mAuth != null){

                    startActivity(new Intent(EventListActivity.this, PostListActivity.class));
                    finish();

                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ValueEventListener valueEventListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot data : dataSnapshot.getChildren()){

                            try {
                                Event event = new Event(data, currentUser);
                                events.add(event);
                                Collections.reverse(events);
                            }catch (DatabaseException e){
                                data.getKey();
                            }
                        }
                        adapter = new EventRecycleAdapter(EventListActivity.this.getApplicationContext(),EventListActivity.this, currentUser, events);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                };
                mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
