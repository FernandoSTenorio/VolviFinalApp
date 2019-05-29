package com.fernandotenorio.volvifinalapp.Data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fernandotenorio.volvifinalapp.Activities.PostDetailsActivity;
import com.fernandotenorio.volvifinalapp.Listeners.OnHasLikeListener;
import com.fernandotenorio.volvifinalapp.Listeners.OnLikeListener;
import com.fernandotenorio.volvifinalapp.Model.Blog;
import com.fernandotenorio.volvifinalapp.Model.Like;
import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.R;
import com.fernandotenorio.volvifinalapp.Utils.FormattedDate;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BlogsAdapter extends RecyclerView.Adapter<BlogsAdapter.ViewHolder> {

    private final Activity activity;
    private final Context mContext;
    private final List<Blog> blogs;
    private final User currentUser;

    public BlogsAdapter(Context context, Activity activity, User user, List<Blog> blogs) {
        this.currentUser = user;
        this.mContext = context;
        this.blogs = blogs;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        Blog blog = blogs.get(i);
        viewHolder.uid.setText(String.format(blog.author()));
        viewHolder.title.setText(blog.title());
        viewHolder.desc.setText(blog.description());
        viewHolder.createdDate.setText(new FormattedDate(blog.createdDate()).toSting());
        viewHolder.likeText.setText(String.format("%s", blog.nbLikes()));
        viewHolder.commentsCountText.setText(String.format("%s", blog.nbComments()));

        Picasso.get().load(blog.profileImage()).into(viewHolder.profileImage);
        Picasso.get().load(blog.image()).into(viewHolder.image);

        blog.hasLike(new OnHasLikeListener() {
            @Override
            public void onExists(boolean exists, Like like) {
                if(exists){
                    viewHolder.likeImage.setImageResource(R.drawable.ic_like_active);
                    viewHolder.likeImage.setTag(R.drawable.ic_like_active);
                } else {
                    viewHolder.likeImage.setImageResource(R.drawable.ic_like);
                    viewHolder.likeImage.setTag(R.drawable.ic_like);
                }
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }
        });

        viewHolder.btnSeeMore
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        final String blogId = blogs.get(i).id();
                        Intent i = new Intent(activity, PostDetailsActivity.class);
                        i.putExtra("postId", blogId);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(i);
                    }
                });

        viewHolder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((int)viewHolder.likeImage.getTag() == R.drawable.ic_like){
                    viewHolder.likeImage.setImageResource(R.drawable.ic_like_active);
                    viewHolder.likeImage.setTag(R.drawable.ic_like_active);

                    blogs.get(i).like(new OnLikeListener() {
                        @Override
                        public void onLike(long nbLikes) {
                            viewHolder.likeText.setText(String.format("%s", nbLikes));
                        }

                        @Override
                        public void onFailed(String error) {
                            Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    viewHolder.likeImage.setImageResource(R.drawable.ic_like);
                    viewHolder.likeImage.setTag(R.drawable.ic_like);

                    blogs.get(i).unlike(new OnLikeListener() {
                        @Override
                        public void onLike(long nbLikes) {
                            viewHolder.likeText.setText(String.format("%s", nbLikes));
                        }

                        @Override
                        public void onFailed(String error) {
                            Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView desc;
        public ImageView profileImage;
        public TextView createdDate;
        public ImageView image;
        public TextView uid;
        public Button btnSeeMore;
        public ImageView likeImage;
        public TextView likeText;
        public TextView commentsCountText;
        public ImageView commentsImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.postImageList);
            title = itemView.findViewById(R.id.postTitleList);
            desc = itemView.findViewById(R.id.postTextList);
            createdDate = itemView.findViewById(R.id.timestampList);
            profileImage = itemView.findViewById(R.id.profileImageView);
            uid = itemView.findViewById(R.id.postUserList);
            btnSeeMore = itemView.findViewById(R.id.btnSeeMore);
            likeImage = itemView.findViewById(R.id.likesImageView);
            likeText = itemView.findViewById(R.id.likeCounterTextView);
            commentsCountText = itemView.findViewById(R.id.commentsCountTextView);
            commentsImage = itemView.findViewById(R.id.commentsCountImageView);
        }
    }
}
