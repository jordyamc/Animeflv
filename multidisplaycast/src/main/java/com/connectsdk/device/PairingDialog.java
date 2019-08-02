/*
 * PairingDialog
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

import android.app.Activity;
import android.text.InputType;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.connectsdk.service.DeviceService;

import androidx.annotation.NonNull;
import es.munix.multidisplaycast.CastManager;


public class PairingDialog {

    Activity activity;
    ConnectableDevice device;

    public PairingDialog(Activity activity, ConnectableDevice device) {
        this.activity = activity;
        this.device = device;
    }

    public MaterialDialog getSimplePairingDialog(int titleResId, int messageResId) {
        return new MaterialDialog.Builder(activity)
                .title(titleResId)
                .theme(CastManager.getTheme() == DevicePicker.Theme.DARK ? Theme.DARK : Theme.LIGHT)
                .content(messageResId)
                .positiveText(android.R.string.cancel)
                .build();
    }

    public MaterialDialog getPairingDialog(int resId) {
        return getPairingDialog(activity.getString(resId));
    }

    public MaterialDialog getPairingDialog(String message) {
        final EditText input = new EditText(activity);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        return new MaterialDialog.Builder(activity)
                .title(message)
                .theme(CastManager.getTheme() == DevicePicker.Theme.DARK ? Theme.DARK : Theme.LIGHT)
                .customView(input, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String value = input.getText().toString().trim();
                        for (DeviceService service : device.getServices())
                            service.sendPairingKey(value);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();
    }
}
