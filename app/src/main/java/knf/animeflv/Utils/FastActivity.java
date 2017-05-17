package knf.animeflv.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import knf.animeflv.Configuracion;

public class FastActivity extends AppCompatActivity {
    public static final int STOP_SOUND = 1;
    public static final int OPEN_CONF_SOUNDS = 2;
    public static final int SHOW_DIALOG = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                switch (bundle.getInt("key")) {
                    case STOP_SOUND:
                        UtilSound.getCurrentMediaPlayer().stop();
                        if (UtilSound.isNotSoundShow) {
                            UtilSound.toogleNotSound(-1);
                        }
                        break;
                    case OPEN_CONF_SOUNDS:
                        FragmentExtras.KEY = Configuracion.OPEN_SOUNDS;
                        startActivity(new Intent(this, Configuracion.class));
                        break;
                    case SHOW_DIALOG:
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                                .content(bundle.getString("content", "No content"))
                                .theme(ThemeUtils.isAmoled(this) ? Theme.DARK : Theme.LIGHT)
                                .positiveText("OK")
                                .cancelable(false)
                                .positiveColor(ThemeUtils.getAcentColor(this))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        finish();
                                    }
                                });
                        final String url = bundle.getString("web", null);
                        if (url != null) {
                            builder.neutralText("ABRIR WEB")
                                    .neutralColor(ThemeUtils.getAcentColor(this))
                                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    });
                        }
                        builder.build().show();
                        break;
                }
                if (bundle.getInt("key") != SHOW_DIALOG)
                    finish();
            } else {
                finish();
            }
        } catch (Exception e) {
            finish();
        }
    }
}
