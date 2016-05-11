package knf.animeflv.AdminControl;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import knf.animeflv.AdminControl.ControlEnum.AdminBundle;
import knf.animeflv.AdminControl.ControlEnum.Control;
import knf.animeflv.R;

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
    }

    @Override
    public void onAttach(Activity activity) {
        fmanager = ((FragmentActivity) activity).getSupportFragmentManager();
        super.onAttach(activity);

    }
}
