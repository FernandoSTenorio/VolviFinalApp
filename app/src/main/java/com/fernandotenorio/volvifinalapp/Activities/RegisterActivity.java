package com.fernandotenorio.volvifinalapp.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {


    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";

    private EditText firstName;
    private EditText lastName;
    private EditText emmail;
    private EditText password;
    private static EditText confirmPassword;
    private EditText mDateOfBirth;
    private Button registerButton;
    private ImageButton profilePic;
    private Uri resultUri = null;
    private final static int GALLERY_CODE = 1;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mFirebaseStorage;
    private ProgressDialog mProgressDialog;
    private DatePickerDialog.OnDateSetListener mDatePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Become A Volvi Visitor");
        setTitleColor(R.color.colorPurpleRed);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("Profile_Pics");

        mProgressDialog = new ProgressDialog(this);

        profilePic = findViewById(R.id.prifilePic);
        firstName = findViewById(R.id.firstNameReg);
        lastName = findViewById(R.id.lastNameReg);
        emmail = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        mDateOfBirth = findViewById(R.id.age);

        mDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
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

                mDateOfBirth.setText(date);

            }
        };
        registerButton = findViewById(R.id.registerButton);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });


                registerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attemptRegistration();
                    }
                });

    }

    public static boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        String confirmPasswordUser = confirmPassword.getText().toString();
        //TODO: Add own logic to check for a valid password (minimum 6 characters)
        return confirmPasswordUser.equals(password) && password.length() > 4;
    }

    void attemptRegistration() {

        // Reset errors displayed in the form.
        emmail.setError(null);
        password.setError(null);

        // Store values at the time of the login attempt.
        String email = emmail.getText().toString();
        String pwd = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(pwd) || !isPasswordValid(pwd)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emmail.setError(getString(R.string.error_field_required));
            focusView = emmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emmail.setError(getString(R.string.error_invalid_email));
            focusView = emmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            createNewAccount();

        }
    }


    private void createNewAccount() {

        final String name = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        final String email = emmail.getText().toString().trim();
        final String dateOfBirth = mDateOfBirth.getText().toString().trim();
        final String accountType = "Volunteer";
        String psw = password.getText().toString().trim();
        String confirmPsw  = confirmPassword.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lname)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(dateOfBirth)
                && !TextUtils.isEmpty(accountType)
                && !TextUtils.isEmpty(psw)
                && !TextUtils.isEmpty(confirmPsw)){

            mAuth.createUserWithEmailAndPassword(email, psw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    if (authResult != null){

                       final StorageReference imagePath = mFirebaseStorage.child(resultUri.getLastPathSegment());

                        imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        final Uri downloadUrl = uri;
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    String userId = mAuth.getCurrentUser().getUid();
                                                    DatabaseReference currentUserDB = mDatabaseReference.child(userId);

                                                    saveDisplayName();

                                                    User user = new User(mAuth.getUid(), name, lname, email, dateOfBirth, accountType, downloadUrl.toString());
                                                    currentUserDB.child(mAuth.getUid()).setValue(user);
                                                    //currentUserDB.child(user.profilePicture()).child("profilePicture").setValue(downloadUrl.toString());

                                                    Toast.makeText(RegisterActivity.this, "Registered successfully. Please check your email account for verification", Toast.LENGTH_LONG).show();

                                                    mProgressDialog.dismiss();

                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                }else {

                                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                                    }
                                });

                            }
                        });


                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){

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
                resultUri = result.getUri();

                profilePic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void saveDisplayName(){

        FirebaseUser user = mAuth.getCurrentUser();

        String name = firstName.getText().toString()+ " " + lastName.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS, 0);
        prefs.edit().putString(DISPLAY_NAME_KEY, name).apply();

        if (user !=null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Volvi App", "User name updated.");
                            }
                        }
                    });

        }

    }

}
