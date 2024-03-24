package com.example.mehranm5.ui.month;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mehranm5.databinding.CalendarMonthPageBinding;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {


    @NonNull
    @Override
    public MonthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CalendarMonthPageBinding binding = CalendarMonthPageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthAdapter.ViewHolder holder, int position) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,position-10000);
        holder.binding.calendar.setCalendar(calendar.getTimeInMillis());
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CalendarMonthPageBinding binding;

        public ViewHolder(@NonNull CalendarMonthPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
