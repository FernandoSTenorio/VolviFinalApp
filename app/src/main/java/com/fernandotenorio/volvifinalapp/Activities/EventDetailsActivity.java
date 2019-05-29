package com.fernandotenorio.volvifinalapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fernandotenorio.volvifinalapp.Listeners.OnEventLoadListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnUserLoadListener;
import com.fernandotenorio.volvifinalapp.Model.Event;
import com.fernandotenorio.volvifinalapp.Model.Events;
import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.Model.Users;
import com.fernandotenorio.volvifinalapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView mEventImage;
    private ImageView mProfileImage;
    private TextView mVetting;
    private TextView mEventTitleText;
    private TextView mEventDescriptionText;
    private TextView mEventUserText;
    private Button mApplyEvent;
    private Event events;
    private TextView mEventDate;
    private TextView mEventFromTime;
    private TextView mEventToTime;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        setTitle("Event Detail");


        mEventTitleText = findViewById(R.id.eventTitleDetails);
        mEventImage = findViewById(R.id.eventImageViewDetails);
        mEventDescriptionText = findViewById(R.id.eventDescriptionDetailsRow);
        mEventUserText = findViewById(R.id.eventDetailUser);
        mApplyEvent  = findViewById(R.id.sendButton);
        mEventDate = findViewById(R.id.eventDate);
        mEventFromTime = findViewById(R.id.startFrom);
        mEventToTime = findViewById(R.id.endTime);
        mVetting = findViewById(R.id.vetting);


        // Creating a new Users, i am able to get all the event that the current user has created and then create new Activity where a full detailed
        // version of the event will be displayed.
        final String eventId = getIntent().getStringExtra("eventId");
        new Users().loadCurrentUser(new OnUserLoadListener() {
            @Override
            public void onLoad(User user) {
                currentUser = user;

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Volvi_Events");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()){

                            new Events(currentUser).load(eventId, new OnEventLoadListener() {
                                @Override
                                public void onLoad(Event event) {

                                    events = event;

                                    mEventTitleText.setText(events.getEventTitle());
                                    Picasso.get().load(events.getEventImage()).into(mEventImage);
                                    mEventDescriptionText.setText(events.getDesc());
                                    mEventUserText.setText(events.getAuthor());
                                    mEventDate.setText(events.getDate());
                                    mEventFromTime.setText(events.getFromTime());
                                    mEventToTime.setText(events.getToTime());
                                    mVetting.setText(events.getVetting());

                                }

                                @Override
                                public void onFailed(String error) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onFailed(String error) {

                currentUser = null;
                Toast.makeText(EventDetailsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });


        findViewById(R.id.btnReturn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(EventDetailsActivity.this, EventListActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
        mApplyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailsActivity.this, FormActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }



}
