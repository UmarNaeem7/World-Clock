package com.example.worldclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    String[] zones;
    Context mContext;
    TypedArray images;
    boolean[] isCheckedArr;
    private final String DATE_FORMAT = "dd-M-yyyy hh:mm:ss a z";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public MyAdapter(Context c, String[] cities, TypedArray img, boolean[] checkedArr){
        mContext = c;
        zones = cities;
        images = img;
        isCheckedArr = checkedArr;
    }

    private String extractCity(String timezone)
    {
        String s = "";
        for (int i=timezone.indexOf('/')+1;i<timezone.length();i++)
        {
            s += timezone.charAt(i);
        }
        return s;
    }

    private String getTime(String timezone)
    {
        String s1 = "";
        String s2 = "";
        int spaceCount = 0;
        for (int i=0;i<timezone.length();i++)
        {
            if (timezone.charAt(i)==' ')
            {
                spaceCount++;
                continue;
            }

            if (spaceCount==1)
                s1 += timezone.charAt(i);

            if (spaceCount==2)
                s2 += timezone.charAt(i);
        }
        int j = 0;
        while (j<3)
        {
            s1 = s1.substring(0, s1.length()-1);
            j++;
        }
        return s1 + "" + s2;
    }

    private String getDate(String timezone){
        String s = "";
        for (int i=0;i<timezone.length();i++)
        {
            if (timezone.charAt(i)==' ')
                break;

            s += timezone.charAt(i);
        }
        return s;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //city is not checked in other activity
        if (!isCheckedArr[position]) {
            holder.rootView.setLayoutParams(holder.params);
            return;
        }

        holder.city.setText(extractCity(zones[position]));

        ZoneId fromTimeZone = ZoneId.of("Asia/Karachi");    //Source timezone
        ZoneId toTimeZone = ZoneId.of(zones[position]);  //Target timezone

        LocalDateTime today = LocalDateTime.now();          //Current time

        //Zoned date time at source timezone
        ZonedDateTime currentISTime = today.atZone(fromTimeZone);
        ZonedDateTime currentETime = null;
        //Zoned date time at target timezone
        boolean flag = false;
        try {
            currentETime = currentISTime.withZoneSameInstant(toTimeZone);
        }catch (Exception e){
            flag = true;
            holder.time.setText("NA");
            holder.date.setText("NA");
        }

        if (!flag){
            holder.time.setText(getTime(formatter.format(currentETime)));
            holder.date.setText(getDate(formatter.format(currentETime)));
            holder.flag.setImageResource(images.getResourceId(position,0));
        }

    }

    @Override
    public int getItemCount() {
        return zones.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public FrameLayout.LayoutParams params;
        public ConstraintLayout rootView;
        TextView city, time, date;
        ImageView flag;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            city = itemView.findViewById(R.id.CityName);
            time = itemView.findViewById(R.id.Time);
            date = itemView.findViewById(R.id.Date);
            flag = itemView.findViewById(R.id.flagView);

            params = new FrameLayout.LayoutParams(0,0);
            rootView = itemView.findViewById(R.id.outerLayout);
        }
    }
}
