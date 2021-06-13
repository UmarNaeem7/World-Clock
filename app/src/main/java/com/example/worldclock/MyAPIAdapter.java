package com.example.worldclock;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAPIAdapter extends RecyclerView.Adapter<MyAPIAdapter.MyViewHolder> {

    private Context mContext;
    private List<TimeZone> timeZoneList;

    public MyAPIAdapter(Context c, List<TimeZone> timeZones){
        mContext = c;
        timeZoneList = timeZones;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.my_api_row, parent, false);
        return new MyAPIAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyAPIAdapter.MyViewHolder holder, int position) {
        holder.city.setText(timeZoneList.get(position).getTimezone());
        holder.time.setText(timeZoneList.get(position).getDatetime());
    }

    @Override
    public int getItemCount() {
        return timeZoneList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView city, time;
        CardView mCardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            city = itemView.findViewById(R.id.TimeZoneName);
            time = itemView.findViewById(R.id.DateTime);
            mCardView = itemView.findViewById(R.id.APICard);


        }
    }
}
