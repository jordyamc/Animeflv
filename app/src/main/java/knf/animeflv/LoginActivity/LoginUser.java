package knf.animeflv.LoginActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.EncryptionHelper;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

@SuppressLint("SetTextI18n")
public class LoginUser extends AppCompatActivity {

    public static final int CHANGE_EMAIL_RESPONSE_CODE = 45686215;
    public static final int CHANGE_PASSWORD_RESPONSE_CODE = 456655915;
    public static final int LOGOFF_RESPONSE_CODE = 56875645;
    private final String OK = "ok";
    private final String ERROR_SERVER = "server-error";
    private final String ERROR_PASSWORD = "password";
    private final String ERROR_USER_EXISTS = "exist";
    private final String ERROR_USER_NO_EXISTS = "no-exist";
    private final String ERROR_NO_INFO = "no-info";
    protected String old_email = "";
    protected String old_pass = "";
    protected String en_email = "";
    protected String en_password = "";
    protected String new_en_email = "";
    protected String new_en_password = "";
    protected List<String> userList;
    @BindView(R.id.img_login)
    ImageView image;
    @BindView(R.id.input_email)
    EditText email;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.input_r_password)
    EditText r_password;
    @BindView(R.id.email)
    TextInputLayout input_email;
    @BindView(R.id.password)
    TextInputLayout input_password;
    @BindView(R.id.r_password)
    TextInputLayout input_r_password;
    @BindView(R.id.btn_login)
    AppCompatButton login;
    @BindView(R.id.btn_create)
    AppCompatButton create;
    @BindView(R.id.pass_progress)
    ProgressBar progressBar;
    private MaterialDialog dialog;
    private boolean isEmailOK = false;
    private boolean isPassOK = false;
    private boolean isEmailChanged = false;
    private boolean isPassChanged = false;
    private boolean isPassReady = false;
    private boolean isListReady = false;
    private boolean started = false;

    public static String stringServer(String result) {
        return result.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
    }

    public static String fromStringServer(String result) {
        return result.replace("IGUAL", "=").replace("AMPERSAND", "&").replace("COMILLA", "\"").replace("PREGUNTA", "?").replace("MAS", "+").replace("SLIDE_DERECHO", "/").replace("COMA", ",").trim();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_user);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setUpColors();
        image.setImageResource(getImageDrawable());
        login.setOnClickListener(getListener());
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(LoginUser.this)
                        .content("Desea cerrar la sesion actual?")
                        .positiveText("SI")
                        .negativeText("cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                PreferenceManager.getDefaultSharedPreferences(LoginUser.this).edit().putString("login_email", "null").apply();
                                PreferenceManager.getDefaultSharedPreferences(LoginUser.this).edit().putString("login_email_coded", "null").apply();
                                PreferenceManager.getDefaultSharedPreferences(LoginUser.this).edit().putString("login_pass_coded", "null").apply();
                                dialog.dismiss();
                                new Parser().saveBackup(LoginUser.this);
                                setResult(LOGOFF_RESPONSE_CODE);
                                finish();
                            }
                        }).build().show();
            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        old_email = preferences.getString("login_email", "null");
        en_email = preferences.getString("login_email_coded", "null");
        en_password = preferences.getString("login_pass_coded", "null");
        email.setText(old_email);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    email.setText(s.toString().replace(" ", ""));
                    email.setSelection(s.length() - 1);
                }
                input_email.setError(null);
                if (s.toString().equals(old_email)) {
                    if (!isPassChanged) {
                        disableButton();
                        hideRCont();
                    }
                    isEmailChanged = false;
                } else {
                    if (userList.contains(s.toString())) {
                        input_email.setError("La cuenta ya existe!!");
                    }
                    if (isPassReady) {
                        showRCont();
                        if (isListReady) {
                            enableButton();
                        }
                    }
                    isEmailChanged = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.setOnEditorActionListener(getActionListener());
        r_password.setOnEditorActionListener(getActionListener());
        r_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) r_password.setText(s.toString().replace(" ", ""));
                input_r_password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.setEnabled(false);
        EncryptionHelper.asyncDecrypt(fromStringServer(en_password), new EncryptionHelper.EncryptionListenerSingle() {
            @Override
            public void onFinish(final String result) {
                old_pass = result.trim();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        password.setText(result);
                        password.setEnabled(true);
                        password.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (s.toString().contains(" ")) {
                                    password.setText(s.toString().replace(" ", ""));
                                    password.setSelection(s.length() - 1);
                                }
                                input_password.setError(null);
                                if (s.toString().equals(old_pass)) {
                                    if (!isEmailChanged) {
                                        disableButton();
                                        hideRCont();
                                    }
                                    isPassChanged = false;
                                } else {
                                    if (isPassReady) {
                                        showRCont();
                                        if (isListReady) {
                                            enableButton();
                                        }
                                    }
                                    isPassChanged = true;
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                    }
                });
                isPassReady = true;
                if (isEmailChanged || isPassChanged) {
                    showRCont();
                    if (isListReady) {
                        enableButton();
                    }
                }
            }
        });
        LoginServer.getUserList(this, new LoginServer.ListResponse() {
            @Override
            public void onUserListCreated(@Nullable List<String> list) {
                if (list != null) {
                    userList = list;
                    isListReady = true;
                    if (isEmailChanged || isPassChanged) {
                        if (isPassReady) {
                            enableButton();
                        }
                    }
                } else {
                    userList = new ArrayList<>();
                    isListReady = true;
                    if (isEmailChanged || isPassChanged) {
                        if (isPassReady) {
                            enableButton();
                        }
                    }
                }
            }
        });
    }

    private void setUpColors() {
        if (!ThemeUtils.isAmoled(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(ColorsRes.Blanco(this));
            }
            email.setTextColor(ColorsRes.SecondaryTextLight(this));
            password.setTextColor(ColorsRes.SecondaryTextLight(this));
            r_password.setTextColor(ColorsRes.SecondaryTextLight(this));
            image.getRootView().setBackgroundColor(ColorsRes.Blanco(this));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(ColorsRes.Negro(this));
            }
            email.setTextColor(ColorsRes.SecondaryTextDark(this));
            password.setTextColor(ColorsRes.SecondaryTextDark(this));
            r_password.setTextColor(ColorsRes.SecondaryTextDark(this));
            image.getRootView().setBackgroundColor(ColorsRes.Negro(this));
        }
    }

    private TextView.OnEditorActionListener getActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (isEmailChanged || isPassChanged)
                        login.performClick();
                }
                return false;
            }
        };
    }

    private View.OnClickListener getListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInfoValid()) {
                    startLoading();
                    if (isEmailChanged && isPassChanged) {
                        if (isEmailOK && isPassOK) {
                            changeAccountResponse(new_en_email, new_en_password);
                        } else {
                            if (isEmailOK) {
                                EncryptionHelper.asyncEncrypt(password.getText().toString(), new EncryptionHelper.EncryptionListenerSingle() {
                                    @Override
                                    public void onFinish(String result) {
                                        changeAccountResponse(new_en_email, stringServer(result));
                                    }
                                });
                            } else if (isPassOK) {
                                EncryptionHelper.asyncEncrypt(password.getText().toString(), new EncryptionHelper.EncryptionListenerSingle() {
                                    @Override
                                    public void onFinish(String result) {
                                        changeAccountResponse(stringServer(result), new_en_password);
                                    }
                                });
                            } else {
                                EncryptionHelper.asyncEncryptMultiple(new EncryptionHelper.EncryptionListenerMultiple() {
                                    @Override
                                    public void onFinish(String[] results) {
                                        changeAccountResponse(stringServer(results[0]), stringServer(results[1]));
                                    }
                                }, email.getText().toString(), password.getText().toString());
                            }
                        }
                    } else if (isEmailChanged) {
                        if (isEmailOK) {
                            cEmailResponse(new_en_email);
                        } else {
                            EncryptionHelper.asyncEncrypt(email.getText().toString(), new EncryptionHelper.EncryptionListenerSingle() {
                                @Override
                                public void onFinish(String result) {
                                    cEmailResponse(stringServer(result));
                                }
                            });
                        }
                    } else if (isPassChanged) {
                        if (isPassOK) {
                            cPassResponse(new_en_password);
                        } else {
                            EncryptionHelper.asyncEncrypt(password.getText().toString(), new EncryptionHelper.EncryptionListenerSingle() {
                                @Override
                                public void onFinish(String result) {
                                    cPassResponse(stringServer(result));
                                }
                            });
                        }
                    }
                }
            }
        };
    }

    private void changeAccountResponse(final String ne, final String np) {
        LoginServer.cAccount(this, en_email, en_password, ne, np, old_email, email.getText().toString(), new LoginServer.ServerInterface() {
            @Override
            public void onServerResponse(final JSONObject object) {
                stopLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (object.getString("response")) {
                                case OK:
                                    acceptAccountChange(ne, np, object);
                                    break;
                                case ERROR_USER_EXISTS:
                                    input_email.setError("La cuenta ya existe!!");
                                    email.requestFocus();
                                    new_en_password = np;
                                    isPassOK = true;
                                    en_email = null;
                                    isEmailOK = false;
                                    break;
                                case ERROR_NO_INFO:
                                    Toaster.toast("Error en la Solicitud");
                                    new_en_email = ne;
                                    new_en_password = np;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    break;
                                default:
                                    new_en_email = ne;
                                    new_en_password = np;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    Toaster.toast("Error--" + object.getString("response"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            new_en_email = ne;
                            new_en_password = np;
                            isEmailOK = true;
                            isPassOK = true;
                            Toaster.toast("Error en la respuesta");
                        }
                    }
                });
            }

            @Override
            public void onServerError() {
                stopLoading();
                new_en_email = ne;
                new_en_password = np;
                isEmailOK = true;
                isPassOK = true;
                Toaster.toast("Error en el servidor");
            }
        });
    }

    private void cEmailResponse(final String ne) {
        LoginServer.cEmail(this, en_email, ne, en_password, old_email, email.getText().toString(), new LoginServer.ServerInterface() {
            @Override
            public void onServerResponse(final JSONObject object) {
                stopLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (object.getString("response")) {
                                case OK:
                                    acceptEmailChange(ne, old_pass, object);
                                    break;
                                case ERROR_USER_EXISTS:
                                    input_email.setError("La cuenta ya existe!!");
                                    email.requestFocus();
                                    isPassOK = true;
                                    isEmailOK = false;
                                    new_en_email = null;
                                    break;
                                case ERROR_NO_INFO:
                                    Toaster.toast("Error en la Solicitud");
                                    new_en_email = ne;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    break;
                                default:
                                    new_en_email = ne;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    Toaster.toast("Error--" + object.getString("response"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            new_en_email = ne;
                            isEmailOK = true;
                            isPassOK = true;
                            Toaster.toast("Error en la respuesta");
                        }
                    }
                });
            }

            @Override
            public void onServerError() {
                stopLoading();
                new_en_email = ne;
                isEmailOK = true;
                isPassOK = true;
                Toaster.toast("Error en el servidor");
            }
        });
    }

    private void cPassResponse(final String np) {
        LoginServer.cPass(this, en_email, en_password, np, new LoginServer.ServerInterface() {
            @Override
            public void onServerResponse(final JSONObject object) {
                stopLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (object.getString("response")) {
                                case OK:
                                    acceptPasswordChange(en_email, np, object);
                                    break;
                                case ERROR_USER_NO_EXISTS:
                                    input_email.setError("La cuenta ya existe!!");
                                    email.requestFocus();
                                    new_en_password = np;
                                    isPassOK = true;
                                    en_email = null;
                                    isEmailOK = false;
                                    break;
                                case ERROR_NO_INFO:
                                    Toaster.toast("Error en la Solicitud");
                                    new_en_password = np;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    break;
                                default:
                                    new_en_password = np;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    Toaster.toast("Error--" + object.getString("response"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            new_en_password = np;
                            isEmailOK = true;
                            isPassOK = true;
                            Toaster.toast("Error en la respuesta");
                        }
                    }
                });
            }

            @Override
            public void onServerError() {
                stopLoading();
                new_en_password = np;
                isEmailOK = true;
                isPassOK = true;
                Toaster.toast("Error en el servidor");
            }
        });
    }

    private void acceptEmailChange(String email_c, String pass_c, JSONObject object) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("login_email", email.getText().toString().toLowerCase()).apply();
        editor.putString("login_email_coded", email_c).apply();
        old_email = email.getText().toString();
        en_email = email_c;
        new_en_email = null;
        new_en_password = null;
        isEmailOK = false;
        isPassOK = true;
        new Parser().saveBackup(this);
        setResult(CHANGE_EMAIL_RESPONSE_CODE);
        Toaster.toast("Email cambiado!!");
        hideRCont();
        disableButton();
    }

    private void acceptPasswordChange(String email_c, String pass_c, JSONObject object) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("login_pass_coded", pass_c).apply();
        old_pass = password.getText().toString();
        en_password = pass_c;
        new_en_email = null;
        new_en_password = null;
        isEmailOK = true;
        isPassOK = false;
        new Parser().saveBackup(this);
        setResult(CHANGE_PASSWORD_RESPONSE_CODE);
        Toaster.toast("Contraseña cambiada!!");
        hideRCont();
        disableButton();
    }

    private void acceptAccountChange(String email_c, String pass_c, JSONObject object) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("login_email", email.getText().toString().toLowerCase()).apply();
        editor.putString("login_email_coded", email_c).apply();
        editor.putString("login_pass_coded", pass_c).apply();
        old_email = email.getText().toString();
        old_pass = password.getText().toString();
        en_email = email_c;
        en_password = pass_c;
        new_en_email = null;
        new_en_password = null;
        isEmailOK = false;
        isPassOK = false;
        new Parser().saveBackup(this);
        setResult(CHANGE_EMAIL_RESPONSE_CODE);
        Toaster.toast("Cuenta cambiada!!");
        hideRCont();
        disableButton();
    }

    private void startLoading() {
        dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content(getDialogMessage())
                .cancelable(false)
                .build();
        dialog.show();
    }

    private void enableButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                login.setEnabled(true);
                login.setBackgroundColor(ThemeUtils.getAcentColor(LoginUser.this));
            }
        });
    }

    private void disableButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                login.setEnabled(false);
                login.setBackgroundColor(ColorsRes.Prim(LoginUser.this));
            }
        });
    }

    private String getDialogMessage() {
        if (isEmailChanged && isPassChanged) {
            return "Cambiando cuenta";
        } else {
            if (isEmailChanged) {
                return "Cambiando Email";
            } else {
                return "Cambiando Contraseña";
            }
        }
    }

    private void stopLoading() {
        if (dialog != null) dialog.dismiss();
    }

    private boolean isInfoValid() {
        dismissErrors();
        String e = email.getText().toString().toLowerCase();
        String p = password.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            if (isEmailChanged) {
                if (userList.contains(e)) {
                    input_email.setError("La cuenta ya existe!!");
                    input_email.requestFocus();
                    return false;
                }
            }
            if (!p.contains(" ")) {
                if (p.length() < 4) {
                    input_password.setError("Minimo 4 caracteres");
                    input_password.requestFocus();
                    return false;
                }
            } else {
                input_password.setError("No se pueden usar espacios");
                input_password.requestFocus();
                return false;
            }
        } else {
            input_email.setError("Correo no valido");
            input_email.requestFocus();
            return false;
        }
        if (isEmailChanged || isPassChanged) {
            if (!old_pass.equals(r_password.getText().toString())) {
                input_r_password.setError("Contraseña Incorrecta");
                input_r_password.requestFocus();
                return false;
            }
        }
        return true;
    }

    @DrawableRes
    private int getImageDrawable() {
        if (ThemeUtils.isAmoled(this)) {
            return R.drawable.login_d;
        } else {
            return R.drawable.login_w;
        }
    }

    private void dismissErrors() {
        input_email.setError(null);
        input_password.setError(null);
        input_r_password.setError(null);
    }


    private void showRCont() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                input_r_password.setHint("Contraseña Actual");
                input_r_password.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideRCont() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                input_r_password.setVisibility(View.GONE);
                r_password.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }
}