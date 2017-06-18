package knf.animeflv.AdminControl;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import knf.animeflv.R;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 11/05/2017.
 */

public class PushManager extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.card)
    CardView cardView;
    @BindView(R.id.card_fast)
    CardView cardView_fast;

    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.title_fast)
    TextView title_fast;
    @BindView(R.id.options)
    AppCompatSpinner spinner;
    @BindView(R.id.test)
    SwitchCompat test;
    @BindView(R.id.title)
    TextInputEditText title;
    @BindView(R.id.subtitle)
    TextInputEditText subtitle;
    @BindView(R.id.dialog_text)
    TextInputEditText dialog_text;
    @BindView(R.id.input_web)
    TextInputEditText web_url;
    @BindView(R.id.title_input)
    TextInputLayout title_input;
    @BindView(R.id.subtitle_input)
    TextInputLayout subtitle_input;
    @BindView(R.id.dialog_text_input)
    TextInputLayout layout_dialog;
    @BindView(R.id.dialog_web_input)
    TextInputLayout layout_web;

    @BindView(R.id.send)
    Button send;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_push_manager);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notificaciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        customViews();
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getTypes()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout_dialog.setVisibility(View.GONE);
                        layout_web.setVisibility(View.GONE);
                        switch (getTypes()[position]) {
                            case "DIALOG":
                                layout_dialog.setVisibility(View.VISIBLE);
                                break;
                            case "WEB":
                                layout_web.setVisibility(View.VISIBLE);
                                break;
                            case "DIALOG-WEB":
                                layout_dialog.setVisibility(View.VISIBLE);
                                layout_web.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title_input.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        subtitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                subtitle_input.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout_dialog.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable()) {
                    if (isInfoValid()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                send.setEnabled(false);
                            }
                        });
                        send();
                    } else {
                        Toaster.toast("Informacion invalida!!!");
                    }
                } else {
                    Toaster.toast("Se necesita internet!!!");
                }
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @ColorInt
    private int getTextColor(ThemeUtils.Theme theme) {
        return theme.secondaryTextColor;
    }

    private String[] getTypes() {
        return new String[]{
                "MAIN",
                "DIALOG",
                "WEB",
                "DIALOG-WEB"
        };
    }

    private void customViews() {
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        toolbar.setBackgroundColor(theme.primary);
        toolbar.getRootView().setBackgroundColor(theme.background);
        cardView.setCardBackgroundColor(theme.card_normal);
        cardView_fast.setCardBackgroundColor(theme.card_normal);
        type.setTextColor(getTextColor(theme));
        test.setTextColor(getTextColor(theme));
        title_fast.setTextColor(getTextColor(theme));
        title.setHintTextColor(getTextColor(theme));
        subtitle.setHintTextColor(getTextColor(theme));
        dialog_text.setHintTextColor(getTextColor(theme));
        web_url.setHintTextColor(getTextColor(theme));
    }

    public void setFast(View view) {
        switch (view.getId()) {
            case R.id.fast_main:
                setFastMain();
                break;
            case R.id.fast_dialog:
                setFastDialog();
                break;
            case R.id.fast_web:
                setFastWeb();
                break;
        }
    }

    private void setFastMain() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setSelection(0, true);
                title.setText("Aviso importante");
                layout_dialog.setVisibility(View.GONE);
                layout_web.setVisibility(View.GONE);
                subtitle.requestFocus();
            }
        });
    }

    private void setFastDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setSelection(1, true);
                title.setText("Aviso importante");
                subtitle.setText("Click para leer");
                layout_dialog.setVisibility(View.VISIBLE);
                dialog_text.requestFocus();
            }
        });
    }

    private void setFastWeb() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setSelection(2, true);
                title.setText("Publicacion importante");
                subtitle.setText("Click para abrir");
                layout_web.setVisibility(View.VISIBLE);
                web_url.requestFocus();
            }
        });
    }

    private boolean isInfoValid() {
        return (!isEmpty(title, title_input) && !isEmpty(subtitle, subtitle_input) && isExtraValid());
    }

    private boolean isEmpty(EditText editText, TextInputLayout layout) {
        Log.e("PushManager", "Empty Edit Text");
        boolean empty = editText.getText().toString().trim().equals("");
        if (empty)
            layout.setError("No debe estar vacio");
        return empty;
    }

    private boolean isExtraValid() {
        switch (getTypes()[spinner.getSelectedItemPosition()]) {
            case "DIALOG":
                boolean isEmpty = dialog_text.getText().toString().trim().equals("");
                Log.e("PushManager", "Dialog empty " + isEmpty);
                if (isEmpty)
                    layout_dialog.setError("EL mensaje no debe estar vacio");
                return !isEmpty;
            case "WEB":
                boolean isValid = Patterns.WEB_URL.matcher(web_url.getText().toString().trim()).matches();
                if (!isValid) {
                    layout_web.setError("URL invalida");
                } else {
                    String url = web_url.getText().toString().trim();
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        web_url.setText("http://" + url);
                }
                return isValid;
            case "DIALOG-WEB":
                boolean dialog_valid = !dialog_text.getText().toString().trim().equals("");
                boolean web_valid = Patterns.WEB_URL.matcher(web_url.getText().toString().trim()).matches();
                if (!dialog_valid)
                    layout_dialog.setError("EL mensaje no debe estar vacio");
                if (!web_valid) {
                    layout_web.setError("URL invalida");
                } else {
                    String url = web_url.getText().toString().trim();
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        web_url.setText("http://" + url);
                }
                return dialog_valid && web_valid;
            default:
                return true;
        }
    }

    private JSONObject getActions() throws Exception {
        String action = getTypes()[spinner.getSelectedItemPosition()];
        JSONObject object = new JSONObject();
        object.put("action", action);
        switch (action) {
            case "DIALOG":
                object.put("text", dialog_text.getText().toString());
                break;
            case "WEB":
                object.put("url", web_url.getText().toString());
                break;
            case "DIALOG-WEB":
                object.put("text", dialog_text.getText().toString());
                object.put("url", web_url.getText().toString());
                break;
        }
        return object;
    }

    private JSONObject getNotificationTitle() throws Exception {
        JSONObject object = new JSONObject();
        object.put("en", title.getText().toString().trim());
        return object;
    }

    private JSONObject getNotificationSubTitle() throws Exception {
        JSONObject object = new JSONObject();
        object.put("en", subtitle.getText().toString().trim());
        return object;
    }

    private Header[] getHeaders() {
        return new Header[]{
                new BasicHeader("Content-Type", "application/json; charset=UTF-8"),
                new BasicHeader("Authorization", "Basic ZWNkNjQ3YmMtOGVmNC00NjY1LWIwZDQtMmQ2N2NlMzYyMmQ5")
        };
    }

    private JSONArray getSegments() {
        JSONArray array = new JSONArray();
        if (test.isChecked()) {
            array.put("Beta");
        } else {
            array.put("All");
        }
        return array;
    }

    private void send() {
        try {
            JSONObject object = new JSONObject();
            object
                    .put("app_id", "81c836a4-a6ff-48b7-b696-c411fa59ab39")
                    .put("included_segments", getSegments())
                    .put("data", getActions())
                    .put("headings", getNotificationTitle())
                    .put("contents", getNotificationSubTitle());
            Log.e("Request Push", object.toString());
            StringEntity entity = new StringEntity(object.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(this, "https://onesignal.com/api/v1/notifications", getHeaders(), entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);
                    Log.e("Push", "Code: " + statusCode + " Response: \n" + responseString);
                    if (statusCode == 200) {
                        Toaster.toast("Notificacion enviada!!!");
                    } else {
                        Toaster.toast("Error al enviar notificacion");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            send.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.e("Push", "Code: " + statusCode + " Response: \n" + response.toString());
                    if (statusCode == 200) {
                        Toaster.toast("Notificacion enviada!!!");
                    } else {
                        Toaster.toast("Error al enviar notificacion");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            send.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toaster.toast("Error al enviar notificacion");
                    Log.e("Push", "Code: " + statusCode + " Response: \n" + errorResponse.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            send.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("Push", "Code: " + statusCode + " Response: \n" + errorResponse.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            send.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("Push", "Code: " + statusCode + " Response: \n" + responseString);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            send.setEnabled(true);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.toast("Error al enviar");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    send.setEnabled(true);
                }
            });
        }
    }
}