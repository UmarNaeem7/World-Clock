package com.example.worldclock;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> implements Filterable{
    List<String> zones;
    List<String> completeList;
    Context mContext;
    boolean[] isCheckedArr;
    ValueFilter valueFilter;

    public MyAdapter2(Context c, List<String> cities, boolean[] checkedArr){
        mContext = c;
        zones = cities;
        isCheckedArr = checkedArr;
        completeList = cities;
    }

    private String extractCity(String timezone) {
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
        int index = completeList.indexOf(zones.get(position));
        if (zones.get(position).equals(completeList.get(position))) //no filter applied
            holder.mCheckBox.setChecked(isCheckedArr[position]);
        else    //filter applied so update index instead of position
            holder.mCheckBox.setChecked(isCheckedArr[index]);
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("World Clock", "onClick: ");
                isCheckedArr[index] = !isCheckedArr[index];
            }
        });
    }

    @Override
    public int getItemCount() {
        return zones.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<String> filterList = new ArrayList<>();
                for (int i = 0; i < completeList.size(); i++) {
                    if ((completeList.get(i).toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(completeList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = completeList.size();
                results.values = completeList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            zones = (List<String>) results.values;
            notifyDataSetChanged();
        }
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
