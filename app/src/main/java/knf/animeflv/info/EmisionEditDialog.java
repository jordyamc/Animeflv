package knf.animeflv.info;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Emision.EmisionManager;
import knf.animeflv.Emision.EmisionObject;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TimeCompare;
import xdroid.toaster.Toaster;

public class EmisionEditDialog extends DialogFragment {
    @BindView(R.id.switch_emision)
    SwitchCompat switchCompat;
    @BindView(R.id.hour_button)
    AppCompatButton button;
    @BindView(R.id.spinner_emision)
    AppCompatSpinner spinner;
    @BindView(R.id.more_configs)
    LinearLayout configs;
    @BindView(R.id.hourtext)
    TextView hour;
    @BindView(R.id.daytext)
    TextView day;
    private View root;
    private MaterialDialog dialog;
    private EmisionObject object;
    private int daycode = -1;
    private boolean exist = false;

    public static EmisionEditDialog create(String aid) {
        Bundle bundle = new Bundle();
        bundle.putString("aid", aid);
        EmisionEditDialog d = new EmisionEditDialog();
        d.setArguments(bundle);
        return d;
    }

    private static String getHour() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("~hh:mmaa", Locale.ENGLISH);
            Date myDate = new Date();
            return simpleDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "~00:00";
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        root = LayoutInflater.from(getActivity()).inflate(R.layout.edit_emision, null);
        dialog = new MaterialDialog.Builder(getActivity())
                .title("Editor")
                .titleGravity(GravityEnum.CENTER)
                .customView(root, true)
                .positiveText("Aceptar")
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog d, @NonNull DialogAction which) {
                        if (daycode != Integer.valueOf(object.daycode) || !button.getText().toString().equals(object.hour) || exist != object.exist) {
                            d.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            if (!exist) {
                                EmisionManager.edit(getActivity(), new EmisionObject(getArguments().getString("aid"), false), new EmisionManager.ServerListener() {
                                    @Override
                                    public void OnServerResponse(EmisionObject emisionObject) {
                                        if (emisionObject.isValid) {
                                            object.exist = false;
                                            Toaster.toast("Eliminado de Emision");
                                        } else {
                                            Toaster.toast("Error al eliminar");
                                        }
                                        d.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                    }
                                });
                            } else {
                                EmisionManager.edit(getActivity(), new EmisionObject(getArguments().getString("aid"), new Parser().getTitCached(getArguments().getString("aid")), button.getText().toString(), String.valueOf(TimeCompare.getFormatedDaycodeFromNormal(button.getText().toString() + "-" + daycode))), new EmisionManager.ServerListener() {
                                    @Override
                                    public void OnServerResponse(EmisionObject emisionObject) {
                                        if (emisionObject.isValid) {
                                            object = emisionObject;
                                            Toaster.toast("Editado!!!");
                                        } else {
                                            Toaster.toast("Error al editar");
                                        }
                                        d.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                    }
                                });
                            }
                        } else {
                            d.dismiss();
                        }
                    }
                }).build();
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        ButterKnife.bind(this, dialog.getCustomView());
        setUpTheme();
        button.setText(getHour());
        button.setOnClickListener(getButtonClick());
        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getActivity().getResources().getStringArray(R.array.days)));
        spinner.setSelection(getActualDayCode(), true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                daycode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exist = isChecked;
                        if (isChecked) {
                            configs.setVisibility(View.VISIBLE);
                        } else {
                            configs.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        EmisionManager.get(getActivity(), getArguments().getString("aid"), new EmisionManager.ServerListener() {
            @Override
            public void OnServerResponse(final EmisionObject emisionObject) {
                object = emisionObject;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        if (emisionObject.isValid) {
                            switchCompat.setEnabled(true);
                            exist = emisionObject.exist;
                            if (emisionObject.exist) {
                                switchCompat.setChecked(true);
                                button.setText(emisionObject.hour);
                                daycode = TimeCompare.getFormatedDaycodeFromUTC(emisionObject.hour + "-" + emisionObject.daycode);
                                spinner.setSelection(daycode, true);
                                configs.setVisibility(View.VISIBLE);
                            } else {
                                daycode = getActualDayCode();
                            }
                        } else {
                            dialog.dismiss();
                            Toaster.toast("Error en Json!!!");
                        }
                    }
                });
            }
        });
        return dialog;
    }

    private void setUpTheme() {
        boolean amoled = ThemeUtils.isAmoled(getActivity());
        switchCompat.setTextColor(amoled ? ColorsRes.SecondaryTextDark(getActivity()) : ColorsRes.SecondaryTextLight(getActivity()));
        hour.setTextColor(amoled ? ColorsRes.SecondaryTextDark(getActivity()) : ColorsRes.SecondaryTextLight(getActivity()));
        day.setTextColor(amoled ? ColorsRes.SecondaryTextDark(getActivity()) : ColorsRes.SecondaryTextLight(getActivity()));
    }

    private View.OnClickListener getButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String format24 = from12To24(button.getText().toString());
                String[] code = format24.replace("~", "").split("-")[0].split(":");
                int h = Integer.parseInt(code[0]);
                int m = Integer.parseInt(code[1]);
                TimePickerDialog dialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                        int hournormal = hourOfDay;
                        if (hourOfDay >= 13) {
                            hournormal = hourOfDay - 12;
                        }
                        String h = hournormal < 10 ? "0" + hournormal : "" + hournormal;
                        String m = minute < 10 ? "0" + minute : "" + minute;
                        String t = hourOfDay >= 12 ? "PM" : "AM";
                        String hour = "~" + h + ":" + m + t;
                        button.setText(hour);
                    }
                }, h, m, false);
                dialog.enableSeconds(false);
                dialog.setAccentColor(ThemeUtils.getAcentColor(getActivity()));
                dialog.setThemeDark(ThemeUtils.isAmoled(getActivity()));
                dialog.show(getActivity().getFragmentManager(), "time");
            }
        };
    }

    private int getActualDayCode() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            case Calendar.SUNDAY:
                return 7;
            default:
                return 0;
        }
    }

    private String from12To24(String hour_12) {
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("~HH:mm", Locale.ENGLISH);
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("~hh:mmaa", Locale.ENGLISH);
            Date _12HourDt = _12HourSDF.parse(hour_12);
            Log.e("Hour", "From: " + hour_12 + " to " + _24HourSDF.format(_12HourDt));
            return _24HourSDF.format(_12HourDt);
        } catch (Exception e) {
            e.printStackTrace();
            return "00:00";
        }
    }
}
