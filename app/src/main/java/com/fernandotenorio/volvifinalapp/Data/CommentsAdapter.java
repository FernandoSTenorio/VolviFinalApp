package com.fernandotenorio.volvifinalapp.Data;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fernandotenorio.volvifinalapp.Model.Comment;
import com.fernandotenorio.volvifinalapp.R;
import com.fernandotenorio.volvifinalapp.Utils.HumanDate;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private final Activity activity;
    private final Context mContext;
    private List<Comment> comments;

    public CommentsAdapter(Context context, Activity activity, List<Comment> comments) {
        this.mContext = context;
        this.comments = comments;
        this.activity = activity;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_item, parent, false);

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int i) {
        Comment comment = comments.get(i);
        holder.commentAuthorTextView.setText(comment.author());
        holder.dateTextView.setText(new HumanDate(comment.createdDate()).toSting());
        holder.commentTextView.setText(comment.message());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView commentAuthorTextView;
        public TextView dateTextView;
        public TextView commentTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentTextView = itemView.findViewById(R.id.commentMessage);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            commentAuthorTextView = itemView.findViewById(R.id.commentAuthor);
        }
    }
}

