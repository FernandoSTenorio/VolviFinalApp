package com.fernandotenorio.volvifinalapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.fernandotenorio.volvifinalapp.R;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView myDate;
    private Context mContext;
    private Button calendarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        myDate = findViewById(R.id.calendarText);
        calendarButton = findViewById(R.id.calendarButton);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, final int year, int month, int dayOfMonth) {

                final String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                myDate.setText(date);

                calendarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendarEvent = Calendar.getInstance();
                        Intent intent = new Intent(Intent.ACTION_EDIT);

                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", calendarEvent.getTimeInMillis());
                        intent.putExtra("endTime", calendarEvent.getTimeInMillis() + 60 * 60 * 1000);
                        intent.putExtra("title", "Sample Event");
                        intent.putExtra("allDay", true);
                        intent.putExtra("rule", "FREQ=YEARLY");
                        startActivity(intent);
                    }
                });


            }
        });



    }

}
