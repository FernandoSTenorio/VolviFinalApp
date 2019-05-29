package com.fernandotenorio.volvifinalapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.MailSettings;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.Setting;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class FormActivity extends AppCompatActivity {

    private EditText mFulName;
    private EditText mPhoneNumber;
    private EditText mEmail;
    private EditText mDescription;
    private Button mSubmitFormButton;

    private User currentUser;


    private DatabaseReference mPostDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        setTitle("Volvi Form");
        setTitleColor(R.color.colorPurpleRed);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Volvi_Events");



        mFulName = findViewById(R.id.formFullName);
        mPhoneNumber = findViewById(R.id.formPhoneNumber);
        mEmail = findViewById(R.id.formEmail);
        mDescription = findViewById(R.id.formAbout);
        mSubmitFormButton = findViewById(R.id.submitForm);



        mSubmitFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendMail();
                        startActivity(new Intent(FormActivity.this, PostListActivity.class));
                        finish();

                        Toast.makeText(getApplicationContext(), "Sending email", Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

    }

    /**
     * Method responsible to get all the form application inputs and turn it into a list, to be sent to the Post poster
     * @return and map in fome of String with all the information set
     */
    public String sendFormViaEmail(){

        final String name = mFulName.getText().toString().trim();
        final String phoneNumber = mPhoneNumber.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();
        final String desc = mDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(desc)){

            Map<String, String> sendEmail = new HashMap<>();

            sendEmail.put("Full Name ", name);
            sendEmail.put("Phone Number ", phoneNumber);
            sendEmail.put("User Email ", email);
            sendEmail.put("User Description ", desc);

            return String.valueOf(sendEmail);

        }else {

            return null;
        }
    }


    /**
     * This method is responsible to send automatic emails Via API, it gets the event poster email and to send the completed form
     */
    public void sendMail() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                mPostDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(final DataSnapshot data : dataSnapshot.getChildren()){
                            if (data.exists()) {
                                try {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            String emailPoster = data.child("emailPoster").getValue().toString();

                                            Email from = new Email("teamvolvi@gmail.com");
                                            String subject = "Volvi Form";

                                            Email to = new Email(emailPoster);
                                            Content content = new Content("text/plain ", sendFormViaEmail());
                                            Mail mail = new Mail(from, subject, to, content);

                                            MailSettings mailSettings = new MailSettings();
                                            Setting sandBoxMode = new Setting();
                                            sandBoxMode.setEnable(true);
                                            mailSettings.setSandboxMode(sandBoxMode);

                                            //For the college tests purpose I decide to keep this api to facilitate the usage during tests.
                                            SendGrid sg = new SendGrid("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");
                                            Request request1 = new Request();

                                            Response response1;
                                            request1.setMethod(Method.POST);
                                            request1.setEndpoint("mail/send");
                                            try {
                                                request1.setBody(mail.build());
                                                response1 = sg.api(request1);
                                                System.out.println(response1.getStatusCode());
                                                System.out.println(response1.getBody());
                                                System.out.println(response1.getHeaders());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }).start();
                                } catch (DatabaseException dataBase) {
                                    data.getKey();
                                    throw new RuntimeException(dataBase);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        thread.start();

    }

}
