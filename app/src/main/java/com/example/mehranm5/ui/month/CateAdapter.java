package com.example.mehranm5.ui.month;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mehranm5.R;

import java.util.List;

public class CateAdapter extends ArrayAdapter<CateModel> {


    LayoutInflater flater;

    public CateAdapter(@NonNull Context context, int resource, @NonNull CateModel[] objects) {
        super(context, resource, objects);
        flater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = flater.inflate(R.layout.item_spinner, parent, false);
        }
        TextView txt = convertView.findViewById(R.id.text);
        ImageView img = convertView.findViewById(R.id.pic);
        txt.setText(getItem(position).getTitle());
        img.setColorFilter(getItem(position).getColor());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = flater.inflate(R.layout.item_spinner, parent, false);
        }
        TextView txt = convertView.findViewById(R.id.text);
        ImageView img = convertView.findViewById(R.id.pic);
        txt.setText(getItem(position).getTitle());
        img.setColorFilter(getItem(position).getColor());
        return convertView;
    }
}
