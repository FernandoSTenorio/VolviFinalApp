package com.fernandotenorio.volvifinalapp.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.fernandotenorio.volvifinalapp.Listeners.OnEventSavedListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnUserLoadListener;
import com.fernandotenorio.volvifinalapp.Model.Event;
import com.fernandotenorio.volvifinalapp.Model.Events;
import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.Model.Users;
import com.fernandotenorio.volvifinalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageButton mEventImage;
    private EditText mEventTitle;
    private EditText mEventDesc;
    private Button mAddEventButton;
    private EditText mEventDate;
    private EditText mEventStartTime;
    private EditText mEventEndTime;
    private Spinner mVetting;
    private double mProgressUpload = 0;

    private User currentUser;


    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private static final int GALLERY_CODE = 1;

    private ProgressDialog mProgress;
    private DatePickerDialog.OnDateSetListener mDatePickerDialog;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setTitle("Add Event");
        setTitleColor(R.color.colorPurpleRed);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mProgress = new ProgressDialog(this);

        mEventImage = findViewById(R.id.eventImage);
        mEventTitle = findViewById(R.id.addEventTitle);
        mEventDesc = findViewById(R.id.addDescEvent);
        mEventDate = findViewById(R.id.setDateEvent);
        mEventStartTime = findViewById(R.id.fromTime);
        mEventEndTime = findViewById(R.id.toTime);
        mVetting = findViewById(R.id.vetin_spinner);

        new Users().loadCurrentUser(new OnUserLoadListener() {
            @Override
            public void onLoad(User user) {
                currentUser = user;
            }

            @Override
            public void onFailed(String error) {
                currentUser = null;
                Toast.makeText(AddEventActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        // Create an adapter for the spinner, where the user will have the option to decide to Yes/No when asked about the garda vetting
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(AddEventActivity.this, R.array.vetin_array, android.R.layout.simple_spinner_item );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        mVetting.setAdapter(arrayAdapter);
        mVetting.setOnItemSelectedListener(AddEventActivity.this);


        mEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDatePickerDialog, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        mDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //month = month + 1;

                final String date = dayOfMonth + "/" + (month + 1) + "/" + year;

                mEventDate.setText(date);

            }
        };

        mAddEventButton = findViewById(R.id.addEventButton);

        mEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //When this image button is clicked, then the app opens the directory for the user to select a picture
                Intent galleryItent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryItent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryItent,"Select Image"),GALLERY_CODE);
            }
        });
        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){

            Uri mImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

            CropImage.activity(mImageUri)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                mEventImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    /**
     * Method to start posting an Event event into the app.
     */
    public void startPosting(){

        mProgress.setMessage("Creating Event...");
        mProgress.show();

        final String titleVal = mEventTitle.getText().toString().trim();
        final String descVal = mEventDesc.getText().toString().trim();
        final String date = mEventDate.getText().toString().trim();
        final String from = mEventStartTime.getText().toString().trim();
        final String end = mEventEndTime.getText().toString().trim();
        final String vet = mVetting.getSelectedItem().toString();



        if(currentUser == null) {
            Toast.makeText(AddEventActivity.this, "Need authentication to continue !", Toast.LENGTH_LONG).show();
        }else{
            new Events(currentUser).add(titleVal, descVal, date, mImageUri,from, end, vet, new OnEventSavedListener() {
                @Override
                public void onSaved(Event event) {
                    Toast.makeText(AddEventActivity.this, "Event added", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(AddEventActivity.this, EventListActivity.class));
                }

                @Override
                public void onPhotoLoadProgress(double percent) {
                    if(percent > (mProgressUpload + 15)){
                        mProgressUpload = percent;
                        Log.d("Volvi", "onProgress: u[load is" + mProgressUpload + "& done");
                        Toast.makeText(AddEventActivity.this, mProgressUpload + "%", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailed(String error) {
                    Toast.makeText(AddEventActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * Method created to get an user type, and deny permission. But this method is still to be improved.
     * @return
     */
    public FirebaseUser getuserTpe(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference= firebaseDatabase.getReference("Users");

        if (mUser != null){

            databaseReference.child("accountType").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        String profilePic = data.child("Volunteer").getValue(String.class);
                        if(profilePic.equals("Volunteer")){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddEventActivity.this);
                            builder.setTitle("Attention");
                            builder.setMessage("User not allowed to add event");
                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        return mUser;

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String vetting = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), vetting, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
