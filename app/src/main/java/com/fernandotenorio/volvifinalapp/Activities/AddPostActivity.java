package com.fernandotenorio.volvifinalapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fernandotenorio.volvifinalapp.Listeners.OnBlogSavedListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnUserLoadListener;
import com.fernandotenorio.volvifinalapp.Model.Blog;
import com.fernandotenorio.volvifinalapp.Model.Blogs;
import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.Model.Users;
import com.fernandotenorio.volvifinalapp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private double mProgressUpload = 0;
    private static final int GALLERY_CODE = 1;

    private ProgressDialog mProgress;
    private Uri mImageUri;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        setTitle("Add a New Post");
        setTitleColor(R.color.colorPurpleRed);


        new Users().loadCurrentUser(new OnUserLoadListener() {
            @Override
            public void onLoad(User user) {
                currentUser = user;
            }

            @Override
            public void onFailed(String error) {
                currentUser = null;
                Toast.makeText(AddPostActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        mProgress = new ProgressDialog(this);

        mPostImage = findViewById(R.id.imageButton6);
        mPostTitle = findViewById(R.id.postTitle);
        mPostDesc = findViewById(R.id.postDescription);
        mSubmitButton = findViewById(R.id.submitPost);

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryItente = new Intent(Intent.ACTION_GET_CONTENT);
                galleryItente.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryItente,"Select Image"),GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

                mPostImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    /**
     * Just like the startPosting from the Event Activity, this method is responsible to post a news on the main page of the app
     */
    public void startPosting(){

        mProgress.setMessage("Posting to Volvi Blog...");
        mProgress.show();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();

        if(currentUser == null) {
            Toast.makeText(AddPostActivity.this, "Need authentication to continue !", Toast.LENGTH_LONG).show();
        }else{
            new Blogs(currentUser).add(titleVal, descVal, mImageUri, new OnBlogSavedListener() {
                @Override
                public void onSaved(Blog blog) {
                    Toast.makeText(AddPostActivity.this, "Post added", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                }

                @Override
                public void onPhotoLoadProgress(double percent) {
                    if(percent > (mProgressUpload + 15)){
                        mProgressUpload = percent;
                        Log.d("Volvi", "onProgress: u[load is" + mProgressUpload + "& done");
                        Toast.makeText(AddPostActivity.this, mProgressUpload + "%", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailed(String error) {
                    Toast.makeText(AddPostActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}