/*
 * DevicePickerAdaper
 * Connect SDK
 * 
 * Copyright (c) 2014 LG Electronics.
 * Created by Hyun Kook Khang on 19 Jan 2014
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.connectsdk.device;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectsdk.discovery.DiscoveryManager;

import java.util.HashMap;
import java.util.Map;

import es.munix.multidisplaycast.R;


public class DevicePickerAdapter extends BaseAdapter {

    private Map<String, ConnectableDevice> currentDevices = new HashMap<>();
    private Context context;
    private DevicePicker.Theme theme = DevicePicker.Theme.LIGTH;

    DevicePickerAdapter(Context context) {
        this.context = context;
    }

    DevicePickerAdapter(Context context, DevicePicker.Theme theme) {
        this.context = context;
        this.theme = theme;
    }

    @Override
    public int getCount() {
        if (currentDevices != null) {
            return currentDevices.size();
        } else {
            return 0;
        }
    }

    @Override
    public ConnectableDevice getItem(int position) {
        int pos = 0;
        for (Map.Entry<String, ConnectableDevice> device : currentDevices.entrySet()) {
            if (position == pos) {
                return device.getValue();
            }
            pos++;
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            view = View.inflate(context, R.layout.cast_connect_item, null);
        }

        ConnectableDevice device = getItem(position);
        String text;
        if (device.getFriendlyName() != null) {
            text = device.getFriendlyName();
        } else {
            text = device.getModelName();
        }

        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(text);

        ImageView image = (ImageView) view.findViewById(R.id.icon);
        if (device.getConnectedServiceNames().toLowerCase().contains("chromecast")) {
            image.setImageResource(R.drawable.cast);
        } else if (device.getConnectedServiceNames().toLowerCase().contains("airplay")) {
            image.setImageResource(R.drawable.airplay);
        } else {
            image.setImageResource(R.drawable.tv);
        }


        boolean isDebuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        boolean hasNoFilters = DiscoveryManager.getInstance().getCapabilityFilters().size() == 0;

        String serviceNames = device.getConnectedServiceNames();
        boolean hasServiceNames = (serviceNames != null && serviceNames.length() > 0);

        boolean shouldShowServiceNames = hasServiceNames && (isDebuggable || hasNoFilters);

        TextView subTextView = (TextView) view.findViewById(R.id.subtitle);

        if (shouldShowServiceNames) {
            subTextView.setText(serviceNames.replace("DIAL,", "")
                    .replace("DIAL", "Chromecast")
                    .trim());
        } else {
            subTextView.setText(null);
        }

        if (theme == DevicePicker.Theme.DARK) {
            image.setColorFilter(Color.WHITE);
            textView.setTextColor(ContextCompat.getColor(context, android.R.color.primary_text_dark));
            subTextView.setTextColor(ContextCompat.getColor(context, android.R.color.secondary_text_dark));
        }

        return view;
    }

    public void set(Map<String, ConnectableDevice> currentDevices) {
        this.currentDevices = currentDevices;
        notifyDataSetChanged();
    }
}
