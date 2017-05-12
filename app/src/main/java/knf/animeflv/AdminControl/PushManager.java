package knf.animeflv.AdminControl;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
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
import knf.animeflv.ColorsRes;
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
    @BindView(R.id.title)
    TextInputEditText title;
    @BindView(R.id.subtitle)
    TextInputEditText subtitle;
    @BindView(R.id.dialog_text)
    TextInputEditText dialog_text;
    @BindView(R.id.title_input)
    TextInputLayout title_input;
    @BindView(R.id.subtitle_input)
    TextInputLayout subtitle_input;
    @BindView(R.id.dialog_input)
    TextInputLayout layout;

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
                        switch (getTypes()[position]) {
                            case "DIALOG":
                                layout.setVisibility(View.VISIBLE);
                                layout.setHint("Texto del dialogo");
                                dialog_text.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                                break;
                            case "WEB":
                                layout.setVisibility(View.VISIBLE);
                                layout.setHint("URL");
                                dialog_text.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                                break;
                            default:
                                layout.setVisibility(View.GONE);
                                break;
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
                layout.setError(null);
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
                    }
                } else {
                    Toaster.toast("Se necesita internet!!!");
                }
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @ColorInt
    private int getTextColor() {
        return ThemeUtils.isAmoled(this) ? ColorsRes.SecondaryTextDark(this) : ColorsRes.SecondaryTextLight(this);
    }

    private String[] getTypes() {
        return new String[]{
                "MAIN",
                "DIALOG",
                "WEB"
        };
    }

    private void customViews() {
        if (ThemeUtils.isAmoled(this)) {
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
        }
        cardView.setCardBackgroundColor(ThemeUtils.isAmoled(this) ? ColorsRes.Prim(this) : ColorsRes.Blanco(this));
        cardView_fast.setCardBackgroundColor(ThemeUtils.isAmoled(this) ? ColorsRes.Prim(this) : ColorsRes.Blanco(this));
        type.setTextColor(getTextColor());
        title_fast.setTextColor(getTextColor());
        title.setHintTextColor(getTextColor());
        subtitle.setHintTextColor(getTextColor());
        dialog_text.setHintTextColor(getTextColor());
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
                layout.setVisibility(View.GONE);
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
                layout.setVisibility(View.VISIBLE);
                layout.setHint("Texto del dialogo");
                dialog_text.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
                layout.setVisibility(View.VISIBLE);
                layout.setHint("URL");
                dialog_text.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                dialog_text.requestFocus();
            }
        });
    }

    private boolean isInfoValid() {
        return (!isEmpty(title, title_input) && !isEmpty(subtitle, subtitle_input) && isExtraValid());
    }

    private boolean isEmpty(EditText editText, TextInputLayout layout) {
        boolean empty = editText.getText().toString().trim().equals("");
        if (empty)
            layout.setError("No debe estar vacio");
        return empty;
    }

    private boolean isExtraValid() {
        switch (getTypes()[spinner.getSelectedItemPosition()]) {
            case "DIALOG":
                boolean isEmpty = dialog_text.getText().toString().trim().equals("");
                if (isEmpty)
                    layout.setError("EL mensaje no debe estar vacio");
                return isEmpty;
            case "WEB":
                boolean isValid = Patterns.WEB_URL.matcher(dialog_text.getText().toString().trim()).matches();
                if (!isValid) {
                    layout.setError("URL invalida");
                } else {
                    String url = dialog_text.getText().toString().trim();
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        dialog_text.setText("http://" + url);
                }
                return isValid;
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
                object.put("url", dialog_text.getText().toString());
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
        array.put("All");
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