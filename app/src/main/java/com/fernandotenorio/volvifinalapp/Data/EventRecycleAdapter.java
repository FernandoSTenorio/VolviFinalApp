package com.fernandotenorio.volvifinalapp.Data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fernandotenorio.volvifinalapp.Activities.EventDetailsActivity;
import com.fernandotenorio.volvifinalapp.Model.Event;
import com.fernandotenorio.volvifinalapp.Model.User;
import com.fernandotenorio.volvifinalapp.R;
import com.fernandotenorio.volvifinalapp.Utils.FormattedDate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class EventRecycleAdapter extends RecyclerView.Adapter<EventRecycleAdapter.ViewHolder> {

    Context context;
    ArrayList<Event> events;
    Activity activity;
    User user;

    public EventRecycleAdapter(Context context, Activity activity, User user,  ArrayList<Event> events) {
        this.context = context;
        this.events = events;
        this.activity = activity;
        this.user = user;
    }

    /**
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = (LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.eventcardview,viewGroup, false));
        EventRecycleAdapter.ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    /**
     *
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final EventRecycleAdapter.ViewHolder viewHolder, final int position) {

        Event event = events.get(position);
        String eventImage;
        String posterProfileImage;

        viewHolder.user.setText(event.getAuthor());
        viewHolder.eventTitle.setText(event.getEventTitle());
        viewHolder.date.setText( event.getDate());
        viewHolder.address.setText(event.getAddress());
        try{
            viewHolder.dateAdded.setText(new FormattedDate(event.getDateAdded()).toSting());
        }catch(NumberFormatException ex){

            System.out.println("Error occurred with during conversion");
        }
        eventImage = event.getEventImage();
        posterProfileImage = event.getProfileImage();
        Picasso.get().load(eventImage).into(viewHolder.eventImage);
        Picasso.get().load(posterProfileImage).into(viewHolder.getProfileEventImage);


        //sends the user to a new View layout, which contains Event Detail.
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String eventId = events.get(position).id();
                Intent i = new Intent(activity, EventDetailsActivity.class);
                i.putExtra("eventId", eventId);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(i);
            }
        });
    }


    /**
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView user;
        public TextView eventTitle;
        public TextView date;
        public TextView dateAdded;
        public ImageView eventImage;
        public ImageView getProfileEventImage;
        public TextView address;


        /**
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user = itemView.findViewById(R.id.eventUserPost);
            eventTitle = itemView.findViewById(R.id.eventTitlelist);
            date = itemView.findViewById(R.id.postDateList);
            dateAdded = itemView.findViewById(R.id.datedPosted);
            eventImage = itemView.findViewById(R.id.eventImage);
            getProfileEventImage = itemView.findViewById(R.id.eventProfileImageView);
            address = itemView.findViewById(R.id.eventAddressPost);





        }
    }
}
