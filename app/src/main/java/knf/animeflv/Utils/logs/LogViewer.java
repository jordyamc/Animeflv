package knf.animeflv.Utils.logs;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileInputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class LogViewer extends AppCompatActivity {
    @Bind(R.id.et_correo)
    EditText correo;
    @Bind(R.id.phone_info)
    TextView phone_info;
    @Bind(R.id.complete_log)
    TextView complete_info;
    @Bind(R.id.action_send_log)
    FloatingActionButton button;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    String correoS;
    File current;

    @SuppressWarnings("all")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_viewer);
        ButterKnife.bind(this);
        button.setColorNormal(ThemeUtils.getAcentColor(this));
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            setContent(intent.getExtras());
        } else if (intent.getData() != null) {
            setContent(intent.getData());
        } else {
            ToastError(new Exception("No Extras"));
            finish();
            return;
        }
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(ColorsRes.Negro(this));
                getWindow().setNavigationBarColor(ColorsRes.Negro(this));
            }
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Informe de Error");
        correoS = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email", "");
        correo.setText(correoS);
    }

    private void ToastError(Throwable e) {
        Toaster.toast("Error en operacion " + e.getMessage());
    }

    private String getPhoneInfo(String path) {
        return "SDK: " + Build.VERSION.SDK_INT + "\n" +
                "MARCA: " + Build.MANUFACTURER + "\n" +
                "MODELO: " + Build.MODEL + "\n" +
                "PRODUCTO: " + Build.PRODUCT + "\n" +
                "ARCHIVO: " + path;
    }

    private String getPhoneInfo() {
        String tmp = "SDK:" + Build.VERSION.SDK_INT + "," +
                "MARCA:" + Build.MANUFACTURER + "," +
                "MODELO:" + Build.MODEL + "," +
                "PRODUCTO:" + Build.PRODUCT;
        tmp = tmp.replace(" ", "");
        return tmp;
    }

    private void setContent(Bundle bundle) {
        try {
            current = new File(bundle.getString("path", null));
            complete_info.setText(FileUtil.getStringFromFile(current));
            phone_info.setText(getPhoneInfo(bundle.getString("path", null)));
        } catch (Exception e) {
            ToastError(e);
            finish();
        }
    }

    private void setContent(Uri uri) {
        try {
            current = new File(uri.getPath());
            complete_info.setText(FileUtil.getStringFromFile(current));
            phone_info.setText(getPhoneInfo(uri.getPath()));
        } catch (Exception e) {
            ToastError(e);
            finish();
        }
    }

    public void send(View view) {
        String mail = correo.getText().toString();
        if (mail.contains("@") && mail.contains(".") && mail.length() > 6) {
            Upload();
        } else {
            correo.setError("Correo Invalido");
        }
    }

    private void Upload() {
        RequestParams params = new RequestParams();
        try {
            params.put("name", current.getName());
            params.put("log", new FileInputStream(current));
        } catch (Exception e) {
            Logger.Error(getClass(), e);
        }
        new AsyncHttpClient().post(new Parser().getBaseUrl(TaskType.NORMAL, this) + "logs.php?correo=" + correoS + "&info=" + getPhoneInfo(), params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Upload", throwable.getMessage());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("Upload", responseString);
                if (responseString.contains("ok")) {
                    Toaster.toast("Enviado");
                } else {
                    Toaster.toast("Error al enviar");
                }
                finish();
            }
        });
    }
}
