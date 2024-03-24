package com.example.mehranm5;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.animation.Animator;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Menu;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mehranm5.database.AppDatabase;
import com.example.mehranm5.databinding.DiloagAddEventBinding;
import com.example.mehranm5.ui.month.CateAdapter;
import com.example.mehranm5.ui.month.CateModel;
import com.example.mehranm5.ui.month.EventModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mehranm5.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ShareHelper.getShareHelper().preferences.getBoolean("first", true)) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DAY_OF_MONTH, -10);

            CateModel[] cats = new CateModel[]{new CateModel("job", Color.parseColor("#FFAF45")), new CateModel("birthday", Color.parseColor("#A5DD9B")), new CateModel("reminder", Color.parseColor("#59D5E0")), new CateModel("education", Color.parseColor("#9195F6"))};
            for (int i = 0; i < 600; i++) {
                CateModel cateModel = cats[new Random().nextInt(cats.length)];
                String key = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH);
                AppDatabase.getAppDatabase(this).addEvent(new EventModel("Test " + i, cateModel.getTitle(), cateModel.getColor(), key, calendar.getTimeInMillis()));

                if (i % 4 == 0)
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            ShareHelper.getShareHelper().editor.putBoolean("first", false).apply();
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_events)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        if (getIntent()!=null && getIntent().getAction().equals("ADD"))
            addEvent();
        updateWidget(this,MonthWidget.class);
        updateWidget(this,EventsWidget.class);
    }
    public static void updateWidget(Context context,Class clas){
        Intent intent = new Intent(context, clas);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, clas));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
    private long date;

    private void addEvent() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = new GregorianCalendar();
        date = calendar.getTimeInMillis();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DiloagAddEventBinding eventBinding = DiloagAddEventBinding.inflate(getLayoutInflater());
        eventBinding.date.setText(dateFormat.format(new Date(calendar.getTimeInMillis())));
        eventBinding.time.setText(timeFormat.format(new Date(calendar.getTimeInMillis())));

        builder.setView(eventBinding.getRoot());
        CateModel[] cats = new CateModel[]{new CateModel("job", Color.parseColor("#FFAF45")), new CateModel("birthday", Color.parseColor("#A5DD9B")), new CateModel("reminder", Color.parseColor("#59D5E0")), new CateModel("education", Color.parseColor("#9195F6"))};

        eventBinding.appCompatSpinner.setAdapter(new CateAdapter(this, R.layout.item_spinner, cats));
        eventBinding.selectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                eventBinding.date.setText(dateFormat.format(new Date(calendar.getTimeInMillis())));
                date = calendar.getTimeInMillis();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        eventBinding.selectTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                eventBinding.time.setText(timeFormat.format(new Date(calendar.getTimeInMillis())));
                date = calendar.getTimeInMillis();
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
        builder.setPositiveButton("Create", (dialog, which) -> {
            if (eventBinding.edit.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Fill the title", Toast.LENGTH_SHORT).show();
                return;
            }
            String key = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH);
            AppDatabase.getAppDatabase(this).addEvent(new EventModel(eventBinding.edit.getText().toString().trim(), cats[eventBinding.appCompatSpinner.getSelectedItemPosition()].getTitle(), cats[eventBinding.appCompatSpinner.getSelectedItemPosition()].getColor(), key, date));
            updateWidget(this,MonthWidget.class);
            updateWidget(this,EventsWidget.class);
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }
    public void setTitle(String txt){
   /*     TextView textView = null;

        for (int i = 0; i < binding.appBarMain.toolbar.getChildCount(); i++) {
            if (binding.appBarMain.toolbar.getChildAt(i) instanceof TextView)
                textView= (TextView) binding.appBarMain.toolbar.getChildAt(i);
        }*/
   getSupportActionBar().setTitle(txt);

    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}