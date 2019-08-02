package knf.animeflv;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import knf.animeflv.Utils.ThemeUtils;

public class Configuracion extends AppCompatActivity {
    public static final int OPEN_SOUNDS = 1;
    public static final int GET_WRITE_PERMISSIONS = 2;

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracion);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.conf_toolbar);
        toolbar.setBackgroundColor(theme.primary);
        toolbar.getRootView().setBackgroundColor(theme.background);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Configuracion");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(theme.textColorToolbar);
        ThemeUtils.setNavigationColor(toolbar, theme.toolbarNavigation);
        /*final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_r);
        upArrow.setColorFilter(getResources().getColor(R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.container_conf, new Conf_fragment()).commitAllowingStateLoss();
        if (getIntent().getExtras() != null) {
            setResult(getIntent().getIntExtra("return", -1));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }
}
