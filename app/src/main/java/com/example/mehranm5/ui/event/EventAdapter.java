package com.example.mehranm5.ui.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mehranm5.databinding.EventItemBinding;
import com.example.mehranm5.ui.month.EventModel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    List<EventModel> eventModels;

    public EventAdapter(List<EventModel> eventModels) {
        this.eventModels = eventModels;
    }


    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EventItemBinding binding = EventItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(eventModels.get(position).getDate());
        Calendar calendarPrev = new GregorianCalendar();
        if (position > 0)
            calendarPrev.setTimeInMillis(eventModels.get(position - 1).getDate());
        holder.binding.startMonth.setVisibility((position == 0 || (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun") && !calendarPrev.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun"))) ? View.VISIBLE : View.GONE);
        if ((position == 0 || (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun") && !calendarPrev.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun")))) {
            String startMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            int startDay = calendar.get(Calendar.DAY_OF_MONTH);

            int stopDay = 0;
            String stopMonth = "";

            for (int i = 0; i < 10; i++) {
                if (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sat")) {
                    stopMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
                    stopDay = calendar.get(Calendar.DAY_OF_MONTH);
                    break;
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            String res = "";
            if (startDay == stopDay) {
                res = startMonth + " " + startDay;
            } else if (startMonth.equals(stopMonth)) {
                res = startMonth + " " + startDay + " - " + stopDay;
            } else {
                res = startMonth + " " + startDay + " - " + stopMonth + " " + stopDay;

            }
            holder.binding.startMonth.setText(res);

        }

        holder.binding.title.setText(eventModels.get(position).getTitle());
        holder.binding.time.setText(calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " " + (calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
        holder.binding.back.setCardBackgroundColor(eventModels.get(position).getEventColor());
    }

    @Override
    public int getItemCount() {
        return eventModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EventItemBinding binding;

        public ViewHolder(@NonNull EventItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
