package knf.animeflv.info;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
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
    private static onEditListener listener;
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

    public static EmisionEditDialog create(String aid, boolean count, onEditListener l) {
        Bundle bundle = new Bundle();
        bundle.putString("aid", aid);
        bundle.putBoolean("count", count);
        EmisionEditDialog d = new EmisionEditDialog();
        d.setArguments(bundle);
        listener = l;
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        root = LayoutInflater.from(getActivity()).inflate(R.layout.edit_emision, null);
        dialog = new MaterialDialog.Builder(getActivity())
                .title("Seguir anime")
                .customView(root, true)
                .positiveText("Aceptar")
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog d, @NonNull DialogAction which) {
                        d.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        AutoEmisionHelper.editAnimetoList(getActivity(), object, new EmObj(getActivity(), object.getAid(), daycode), new SearchListener() {
                            @Override
                            public void OnResponse(EmObj obj) {
                                if (getArguments().getBoolean("count")) {
                                    int count = AutoEmisionHelper.getListCount(getActivity());
                                    switch (count) {
                                        case 4:
                                            Toaster.toast("Que vicioso...");
                                            break;
                                        case 10:
                                            Toaster.toast("Estas loco?");
                                            break;
                                        case 15:
                                            Toaster.toast("Bueno, es tu vida, ya no te dire nada....");
                                            break;

                                    }
                                }
                                object = obj;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                    }
                                });
                                getActivity().sendBroadcast(new Intent(InfoFragments.ACTION_EDITED));
                                if (listener != null)
                                    listener.onEdit();
                                d.dismiss();
                            }

                            @Override
                            public void OnError() {
                                try {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog != null)
                                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                        }
                                    });
                                    Toaster.toast("Error al editar");
                                    if (listener != null)
                                        listener.onEdit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
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
        AutoEmisionHelper.getAnimeInfo(getActivity(), getArguments().getString("aid"), new SearchListener() {
            @Override
            public void OnResponse(final EmObj obj) {
                object = obj;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        if (obj != null) {
                            daycode = obj.getDaycode();
                            spinner.setSelection(daycode, true);
                        } else {
                            object = new EmObj(getActivity(), getArguments().getString("aid"), getActualDayCode());
                            daycode = getActualDayCode();
                            spinner.setSelection(getActualDayCode(), true);
                        }
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

    interface onEditListener {
        void onEdit();
    }
}
