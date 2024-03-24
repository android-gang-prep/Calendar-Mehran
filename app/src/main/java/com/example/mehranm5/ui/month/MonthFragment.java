package com.example.mehranm5.ui.month;

import static com.example.mehranm5.MainActivity.updateWidget;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mehranm5.EventsWidget;
import com.example.mehranm5.MainActivity;
import com.example.mehranm5.MonthWidget;
import com.example.mehranm5.R;
import com.example.mehranm5.ShareHelper;
import com.example.mehranm5.database.AppDatabase;
import com.example.mehranm5.databinding.DiloagAddEventBinding;
import com.example.mehranm5.databinding.FragmentMonthBinding;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MonthFragment extends Fragment {

    private FragmentMonthBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMonthBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.viewPager.setAdapter(new MonthAdapter());
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                try {
                    Calendar calendar = new GregorianCalendar();
                    calendar.set(Calendar.DAY_OF_MONTH,1);
                    calendar.add(Calendar.MONTH,position-10000);
                    ((MainActivity) getActivity()).setTitle(calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault()));

                }catch (Exception e){}
            }
        });
        binding.viewPager.setCurrentItem(10000, false);
        binding.viewPager.setOffscreenPageLimit(1);
        binding.fab.setOnClickListener(v -> addEvent());

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main, menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_today) {
                    binding.viewPager.setCurrentItem(10000);
                    return true;
                }else if (menuItem.getItemId() == R.id.action_search) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("search",true);
                    try {
                        Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_nav_events,bundle);
                    }catch (Exception e){}
                    return true;
                }
                return false;
            }
        },getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        new Handler().postDelayed(() -> {
            try {
                Calendar calendar = new GregorianCalendar();
                ((MainActivity) getActivity()).setTitle(calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault()));
            }catch (Exception e){}
        },100);
    }


    private long date;

    private void addEvent() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = new GregorianCalendar();
        date = calendar.getTimeInMillis();


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        DiloagAddEventBinding eventBinding = DiloagAddEventBinding.inflate(getLayoutInflater());
        eventBinding.date.setText(dateFormat.format(new Date(calendar.getTimeInMillis())));
        eventBinding.time.setText(timeFormat.format(new Date(calendar.getTimeInMillis())));

        builder.setView(eventBinding.getRoot());
        CateModel[] cats = new CateModel[]{new CateModel("job", Color.parseColor("#FFAF45")), new CateModel("birthday", Color.parseColor("#A5DD9B")), new CateModel("reminder", Color.parseColor("#59D5E0")), new CateModel("education", Color.parseColor("#9195F6"))};

        eventBinding.appCompatSpinner.setAdapter(new CateAdapter(getContext(), R.layout.item_spinner, cats));
        eventBinding.selectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                eventBinding.date.setText(dateFormat.format(new Date(calendar.getTimeInMillis())));
                date = calendar.getTimeInMillis();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        eventBinding.selectTime.setOnClickListener(v -> {
            new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                eventBinding.time.setText(timeFormat.format(new Date(calendar.getTimeInMillis())));
                date = calendar.getTimeInMillis();
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
        builder.setPositiveButton("Create", (dialog, which) -> {
            if (eventBinding.edit.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Fill the title", Toast.LENGTH_SHORT).show();
                return;
            }
            String key = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH);
            AppDatabase.getAppDatabase(getContext()).addEvent(new EventModel(eventBinding.edit.getText().toString().trim(), cats[eventBinding.appCompatSpinner.getSelectedItemPosition()].getTitle(), cats[eventBinding.appCompatSpinner.getSelectedItemPosition()].getColor(), key, date));
            updateWidget(getContext(), MonthWidget.class);
            updateWidget(getContext(), EventsWidget.class);
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}