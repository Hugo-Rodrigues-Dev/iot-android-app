package com.example.tdandroid.ui.devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tdandroid.R;

import java.util.List;

/**
 * Adapter personnalisé pour l'affichage des devices dans la liste
 */
public class DeviceAdapter extends ArrayAdapter<Integer> {

    public DeviceAdapter(Context context, List<Integer> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer deviceId = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
        }

        TextView label = convertView.findViewById(R.id.device_item_label);
        label.setText("micro:bit ID : " + deviceId);

        return convertView;
    }
}