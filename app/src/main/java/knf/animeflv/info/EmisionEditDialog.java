package knf.animeflv.info;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
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

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.AutoEmision.AutoEmisionHelper;
import knf.animeflv.AutoEmision.EmObj;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class EmisionEditDialog extends DialogFragment {
    @BindView(R.id.switch_emision)
    SwitchCompat switchCompat;
    @BindView(R.id.spinner_emision)
    AppCompatSpinner spinner;
    @BindView(R.id.more_configs)
    LinearLayout configs;
    @BindView(R.id.daytext)
    TextView day;
    private View root;
    private MaterialDialog dialog;
    private EmObj object;
    private int daycode = -1;
    private boolean exist = false;
    private boolean existStart = false;

    public static EmisionEditDialog create(String aid) {
        Bundle bundle = new Bundle();
        bundle.putString("aid", aid);
        EmisionEditDialog d = new EmisionEditDialog();
        d.setArguments(bundle);
        return d;
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
                        if (daycode != object.getDaycode() || exist != existStart) {
                            d.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            if (!exist) {
                                AutoEmisionHelper.removeAnimeFromList(getActivity(), object.getAid(), object.getDaycode(), new SearchListener() {
                                    @Override
                                    public void OnResponse(EmObj obj) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                            }
                                        });
                                        getActivity().sendBroadcast(new Intent(InfoFragments.ACTION_EDITED));
                                        Toaster.toast("Editado!!!");
                                        d.dismiss();
                                    }

                                    @Override
                                    public void OnError() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                            }
                                        });
                                        Toaster.toast("Error al editar");
                                    }
                                });
                            } else {
                                AutoEmisionHelper.editAnimetoList(getActivity(), object, new EmObj(object.getAid(), daycode), new SearchListener() {
                                    @Override
                                    public void OnResponse(EmObj obj) {
                                        object = obj;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                            }
                                        });
                                        getActivity().sendBroadcast(new Intent(InfoFragments.ACTION_EDITED));
                                        Toaster.toast("Editado!!!");
                                        d.dismiss();
                                    }

                                    @Override
                                    public void OnError() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                            }
                                        });
                                        Toaster.toast("Error al editar");
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
        AutoEmisionHelper.getAnimeInfo(getActivity(), getArguments().getString("aid"), new SearchListener() {
            @Override
            public void OnResponse(final EmObj obj) {
                object = obj;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        if (obj != null) {
                            exist = true;
                            existStart = true;
                            switchCompat.setChecked(true);
                            daycode = obj.getDaycode();
                            spinner.setSelection(daycode, true);
                            configs.setVisibility(View.VISIBLE);
                        } else {
                            object = new EmObj(getArguments().getString("aid"), getActualDayCode());
                            exist = false;
                            existStart = false;
                            switchCompat.setChecked(false);
                            daycode = getActualDayCode();
                            spinner.setSelection(getActualDayCode(), true);
                        }
                        switchCompat.setEnabled(true);
                    }
                });
            }

            @Override
            public void OnError() {
                dialog.dismiss();
                Toaster.toast("Error en Busqueda!!!");
            }
        });
        return dialog;
    }

    private void setUpTheme() {
        boolean amoled = ThemeUtils.isAmoled(getActivity());
        switchCompat.setTextColor(amoled ? ColorsRes.SecondaryTextDark(getActivity()) : ColorsRes.SecondaryTextLight(getActivity()));
        day.setTextColor(amoled ? ColorsRes.SecondaryTextDark(getActivity()) : ColorsRes.SecondaryTextLight(getActivity()));
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
                return 1;
        }
    }

    public interface SearchListener {
        void OnResponse(EmObj obj);

        void OnError();
    }
}
