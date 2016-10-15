package knf.animeflv.AdminControl;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import knf.animeflv.AdminControl.ControlEnum.AdminBundle;
import knf.animeflv.AdminControl.ControlEnum.Control;
import knf.animeflv.LoginActivity.LoginServer;
import knf.animeflv.LoginActivity.LoginUser;
import knf.animeflv.R;
import knf.animeflv.Utils.EncryptionHelper;
import xdroid.toaster.Toaster;

public class ControlFragment extends PreferenceFragment {
    FragmentManager fmanager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.admin_users);
        getPreferenceScreen().findPreference("admin_get_pass").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AdminBundle.control = Control.PASS_BY_EMAIL;
                AdminControlDialog.create().show(fmanager, "dialog");
                return false;
            }
        });
        getPreferenceScreen().findPreference("admin_force_mail").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AdminBundle.control = Control.FORCE_EMAIL;
                AdminControlDialog.create().show(fmanager, "dialog");
                return false;
            }
        });
        getPreferenceScreen().findPreference("admin_force_pass").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AdminBundle.control = Control.FORCE_PASS;
                AdminControlDialog.create().show(fmanager, "dialog");
                return false;
            }
        });
        getPreferenceScreen().findPreference("admin_del_user").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AdminBundle.control = Control.DELETE;
                AdminControlDialog.create().show(fmanager, "dialog");
                return false;
            }
        });
        getPreferenceScreen().findPreference("admin_add_email").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                        .input("Correo", null, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(final @NonNull MaterialDialog dialog, final CharSequence input) {
                                dialog.dismiss();
                                final MaterialDialog d = new MaterialDialog.Builder(getActivity())
                                        .content("Agregando...")
                                        .progress(true, 0)
                                        .cancelable(false)
                                        .build();
                                d.show();
                                EncryptionHelper.asyncEncrypt(input.toString(), new EncryptionHelper.EncryptionListenerSingle() {
                                    @Override
                                    public void onFinish(final String result) {
                                        LoginServer.addEmail(getActivity(), input.toString(), LoginUser.stringServer(result), new LoginServer.ServerInterface() {
                                            @Override
                                            public void onServerResponse(JSONObject object) {
                                                try {
                                                    if (object.getString("response").equals("ok")) {
                                                        Toaster.toast("Agregado!!!");
                                                        d.dismiss();
                                                    } else {
                                                        dialog.getInputEditText().setText(input.toString());
                                                        dialog.getInputEditText().setError("Error!!!");
                                                    }
                                                } catch (Exception e) {
                                                    d.dismiss();
                                                    Toaster.toast("Error");
                                                }
                                            }

                                            @Override
                                            public void onServerError() {
                                                d.dismiss();
                                                Toaster.toast("Error");
                                            }
                                        });
                                    }
                                });
                            }
                        }).build().show();

                return false;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        fmanager = ((FragmentActivity) activity).getSupportFragmentManager();
        super.onAttach(activity);

    }
}
