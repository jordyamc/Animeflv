package knf.animeflv.Utils.logs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.FavSyncro;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class LogViewer extends AppCompatActivity {
    @BindView(R.id.et_correo)
    AppCompatEditText correo;
    @BindView(R.id.text_input)
    TextInputLayout inputLayout;
    @BindView(R.id.phone_info)
    TextView phone_info;
    @BindView(R.id.complete_log)
    TextView complete_info;
    @BindView(R.id.action_send_log)
    FloatingActionButton button;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    String correoS;
    File current;

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @SuppressWarnings("all")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_viewer);
        ButterKnife.bind(this);
        button.setColorNormal(ThemeUtils.getAcentColor(this));
        Intent intent = getIntent();
        if (intent.getData() != null) {
            setContent(intent.getData());
        } else if (intent.getExtras() != null) {
            setContent(intent.getExtras());
        } else {
            ToastError(new Exception("No Extras"));
            finish();
            return;
        }
        if (ThemeUtils.isAmoled(this)) {
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            phone_info.setTextColor(ColorsRes.Holo_Dark(this));
            complete_info.setTextColor(ColorsRes.Holo_Dark(this));
            correo.setTextColor(ColorsRes.Blanco(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(ColorsRes.Negro(this));
                getWindow().setNavigationBarColor(ColorsRes.Negro(this));
            }
        } else {
            correo.setTextColor(ColorsRes.Negro(this));
        }
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Informe de Error");
        correoS = FavSyncro.getEmail(this);
        if (correoS.equals("Animeflv")) {
            correo.requestFocus();
        } else {
            correo.setText(correoS);
            correo.clearFocus();

        }
        correo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void ToastError(Throwable e) {
        Toaster.toast("Error en operacion " + e.getCause());
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
            e.printStackTrace();
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
            e.printStackTrace();
            ToastError(e);
            finish();
        }
    }

    public void send(View view) {
        String mail = correo.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            Upload();
        } else {
            inputLayout.setError("Correo Invalido");
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }
}
