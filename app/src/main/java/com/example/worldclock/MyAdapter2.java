package com.example.worldclock;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {
    List<String> zones = new ArrayList<>();
    List<String> temp;
    Context mContext;
    boolean[] isCheckedArr;

    public MyAdapter2(Context c, List<String> cities, boolean[] checkedArr){
        mContext = c;
        zones = cities;
        isCheckedArr = checkedArr;
        temp = cities;
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.my_row2, parent, false);
        return new MyAdapter2.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mCheckBox.setText(extractCity(zones.get(position)));
        holder.mCheckBox.setChecked(isCheckedArr[position]);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("world clock", "onCheckedChanged: clicked");
                isCheckedArr[position] = !isCheckedArr[position];
            }
        });
    }

    @Override
    public int getItemCount() {
        return zones.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox mCheckBox;
        RecyclerView mRecyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.checkBox);
            mRecyclerView = itemView.findViewById(R.id.recylerView);
        }

    }

}
