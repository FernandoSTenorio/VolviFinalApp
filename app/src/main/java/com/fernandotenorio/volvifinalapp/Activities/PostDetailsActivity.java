/*
 *
 * Copyright 2019 OURA Olivier Baudouin, Software Architect at Minlessika (Abidjan, Côte d'Ivoire)
 * https://www.minlessika.com
 * Email Pro : baudolivier.oura@minlessika.com
 * Home email : baudolivier.oura@gmail.com
 * Phone number : +225 07622999
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.fernandotenorio.volvifinalapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fernandotenorio.volvifinalapp.Data.CommentsAdapter;
import com.fernandotenorio.volvifinalapp.Listeners.OnBlogLoadListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnCommentListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnCommentLoadListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnHasLikeListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnLikeListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnUserLoadListener;
import com.fernandotenorio.volvifinalapp.Model.Blog;
import com.fernandotenorio.volvifinalapp.Model.Blogs;
import com.fernandotenorio.volvifinalapp.Model.Comment;
import com.fernandotenorio.volvifinalapp.Model.Like;
import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.Model.Users;
import com.fernandotenorio.volvifinalapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    private ImageView mLikesImage;
    private ImageView mImage;
    private TextView nbLikesText;
    private TextView titleText;
    private TextView descriptionText;
    private TextView userText;
    private TextView commentsCountText;
    private TextView commentMessageText;
    private TextView commentTitleText;
    private Blog blog;
    private User currentUser;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        setTitleColor(R.color.colorPurpleRed);



        recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        CommentsAdapter adapter = new CommentsAdapter(PostDetailsActivity.this.getApplicationContext(), PostDetailsActivity.this, new ArrayList<Comment>());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        final String blogId = getIntent().getStringExtra("postId");
        new Users().loadCurrentUser(new OnUserLoadListener() {
            @Override
            public void onLoad(User user) {
                currentUser = user;

                new Blogs(currentUser).load(blogId, new OnBlogLoadListener() {
                    @Override
                    public void onLoad(Blog loaded) {
                        blog = loaded;
                        titleText.setText(blog.title());
                        descriptionText.setText(blog.description());
                        userText.setText(String.format(blog.author()));
                        nbLikesText.setText(String.format("%s", blog.nbLikes()));
                        commentsCountText.setText(String.format("%s", blog.nbComments()));
                        Picasso.get().load(blog.image()).into(mImage);

//                        mProgress.hide();

                        blog.hasLike(new OnHasLikeListener() {
                            @Override
                            public void onExists(boolean exists, Like like) {
                                if(exists){
                                    mLikesImage.setImageResource(R.drawable.ic_like_active);
                                    mLikesImage.setTag(R.drawable.ic_like_active);
                                }else{
                                    mLikesImage.setImageResource(R.drawable.ic_like);
                                    mLikesImage.setTag(R.drawable.ic_like);
                                }
                            }

                            @Override
                            public void onFailed(String error) {
                                Toast.makeText(PostDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        });

                        refreshComments();
                    }

                    @Override
                    public void onFailed(String error) {
                        mProgress.hide();
                        Toast.makeText(PostDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                currentUser = null;
                Toast.makeText(PostDetailsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        titleText = findViewById(R.id.eventTitleDetails);
        mImage = findViewById(R.id.postImageDetails);
        descriptionText = findViewById(R.id.postDescriptionDetails);
        userText = findViewById(R.id.postUserDetails);
        mLikesImage = findViewById(R.id.likesImageView);
        nbLikesText = findViewById(R.id.likeCounterTextView);
        commentsCountText = findViewById(R.id.commentsCountTextView);
        commentMessageText = findViewById(R.id.commentEditText);
        commentTitleText = findViewById(R.id.postCommentTitleDetails);

        findViewById(R.id.sendButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blog.comment(commentMessageText.getText().toString(), new OnCommentListener() {
                            @Override
                            public void onComment(long nbComments) {
                                commentMessageText.setText("");
                                commentsCountText.setText(String.format("%s", nbComments));

                                refreshComments();

                                Toast.makeText(PostDetailsActivity.this, "Comment posted.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailed(String error) {
                                commentMessageText.setText("");
                                Toast.makeText(PostDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

        findViewById(R.id.btnReturn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(PostDetailsActivity.this, PostListActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

        mLikesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((int)mLikesImage.getTag() == R.drawable.ic_like){
                    mLikesImage.setImageResource(R.drawable.ic_like_active);
                    mLikesImage.setTag(R.drawable.ic_like_active);

                    blog.like(new OnLikeListener() {
                        @Override
                        public void onLike(long nbLikes) {
                            nbLikesText.setText(String.format("%s", nbLikes));
                        }

                        @Override
                        public void onFailed(String error) {
                            Toast.makeText(PostDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    mLikesImage.setImageResource(R.drawable.ic_like);
                    mLikesImage.setTag(R.drawable.ic_like);

                    blog.unlike(new OnLikeListener() {
                        @Override
                        public void onLike(long nbLikes) {
                            nbLikesText.setText(String.format("%s", nbLikes));
                        }

                        @Override
                        public void onFailed(String error) {
                            Toast.makeText(PostDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void refreshComments(){
        blog.loadComments(new OnCommentLoadListener() {
            @Override
            public void onLoad(List<Comment> comments) {
                commentTitleText.setText(String.format("Comments (%s)", comments.size()));
                CommentsAdapter adapter = new CommentsAdapter(PostDetailsActivity.this.getApplicationContext(), PostDetailsActivity.this, comments);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(PostDetailsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}