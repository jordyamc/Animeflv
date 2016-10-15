package knf.animeflv.LoginActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.EncryptionHelper;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

@SuppressLint("SetTextI18n")
public class LoginBase extends AppCompatActivity {

    public static final int LOGIN_RESPONSE_CODE = 558758;
    public static final int SIGNUP_RESPONSE_CODE = 558424;
    private final String OK = "ok";
    private final String ERROR_SERVER = "server-error";
    private final String ERROR_PASSWORD = "password";
    private final String ERROR_USER_EXISTS = "exist";
    private final String ERROR_USER_NO_EXISTS = "no-exist";
    private final String ERROR_NO_INFO = "no-info";
    protected String en_email;
    protected String en_password;
    protected List<String> userList;
    @Bind(R.id.img_login)
    ImageView image;
    @Bind(R.id.input_email)
    EditText email;
    @Bind(R.id.input_password)
    EditText password;
    @Bind(R.id.input_r_password)
    EditText r_password;
    @Bind(R.id.email)
    TextInputLayout input_email;
    @Bind(R.id.password)
    TextInputLayout input_password;
    @Bind(R.id.r_password)
    TextInputLayout input_r_password;
    @Bind(R.id.buttons)
    LinearLayout buttons;
    @Bind(R.id.btn_login)
    AppCompatButton login;
    @Bind(R.id.btn_create)
    AppCompatButton create;
    private boolean isInSignUp = false;
    private MaterialDialog dialog;
    private boolean isEmailOK = false;
    private boolean isPassOK = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_base);
        ButterKnife.bind(this);
        setUpColors();
        image.setImageResource(getImageDrawable());
        login.setOnClickListener(getListener());
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transformToSignUp();
            }
        });
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
                en_email = null;
                isEmailOK = false;
                input_email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                en_password = null;
                isPassOK = false;
                input_password.setError(null);
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
        LoginServer.getUserList(this, new LoginServer.ListResponse() {
            @Override
            public void onUserListCreated(@Nullable List<String> list) {
                if (list != null) {
                    userList = list;
                    login.setEnabled(true);
                    create.setEnabled(true);
                    login.setBackgroundColor(ThemeUtils.getAcentColor(LoginBase.this));
                } else {
                    userList = new ArrayList<>();
                    login.setEnabled(true);
                    create.setEnabled(true);
                    login.setBackgroundColor(ThemeUtils.getAcentColor(LoginBase.this));
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
            image.getRootView().setBackgroundColor(ColorsRes.Negro(this));
        }
    }

    private String stringServer(String result) {
        return result.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
    }

    private TextView.OnEditorActionListener getActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
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
                    if (isEmailOK && isPassOK) {
                        if (isInSignUp) {
                            signupResponse(en_email, en_password);
                        } else {
                            loginResponse(en_email, en_password);
                        }
                    } else if (isEmailOK) {
                        if (isInSignUp) {
                            EncryptionHelper.asyncEncrypt(password.getText().toString(), new EncryptionHelper.EncryptionListenerSingle() {
                                @Override
                                public void onFinish(String result) {
                                    signupResponse(en_email, stringServer(result));
                                }
                            });
                        } else {
                            EncryptionHelper.asyncEncrypt(password.getText().toString(), new EncryptionHelper.EncryptionListenerSingle() {
                                @Override
                                public void onFinish(String result) {
                                    loginResponse(en_email, stringServer(result));
                                }
                            });
                        }
                    } else if (isPassOK) {
                        if (isInSignUp) {
                            EncryptionHelper.asyncEncrypt(email.getText().toString(), new EncryptionHelper.EncryptionListenerSingle() {
                                @Override
                                public void onFinish(String result) {
                                    signupResponse(stringServer(result), en_password);
                                }
                            });
                        } else {
                            EncryptionHelper.asyncEncrypt(email.getText().toString(), new EncryptionHelper.EncryptionListenerSingle() {
                                @Override
                                public void onFinish(String result) {
                                    loginResponse(stringServer(result), en_password);
                                }
                            });
                        }
                    } else {
                        if (isInSignUp) {
                            EncryptionHelper.asyncEncryptMultiple(new EncryptionHelper.EncryptionListenerMultiple() {
                                @Override
                                public void onFinish(final String[] results) {
                                    signupResponse(stringServer(results[0]), stringServer(results[1]));
                                }
                            }, email.getText().toString().toLowerCase(), password.getText().toString().toLowerCase());
                        } else {
                            EncryptionHelper.asyncEncryptMultiple(new EncryptionHelper.EncryptionListenerMultiple() {
                                @Override
                                public void onFinish(final String[] results) {
                                    loginResponse(stringServer(results[0]), stringServer(results[1]));
                                }
                            }, email.getText().toString().toLowerCase(), password.getText().toString().toLowerCase());
                        }
                    }
                }
            }
        };
    }

    private void loginResponse(final String e, final String p) {
        LoginServer.login(LoginBase.this, e, p, email.getText().toString(), new LoginServer.ServerInterface() {
            @Override
            public void onServerResponse(final JSONObject object) {
                stopLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (object.getString("response")) {
                                case OK:
                                    acceptLogin(e, p, object);
                                    break;
                                case ERROR_USER_NO_EXISTS:
                                    input_email.setError("Cuenta no encontrada");
                                    email.requestFocus();
                                    en_password = p;
                                    isPassOK = true;
                                    en_email = null;
                                    isEmailOK = false;
                                    break;
                                case ERROR_PASSWORD:
                                    input_password.setError("Contraseña incorrecta");
                                    password.requestFocus();
                                    en_email = e;
                                    isEmailOK = true;
                                    en_password = null;
                                    isPassOK = false;
                                    break;
                                case ERROR_NO_INFO:
                                    Toaster.toast("Error en la Solicitud");
                                    en_email = e;
                                    en_password = p;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    break;
                                default:
                                    en_email = e;
                                    en_password = p;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    Toaster.toast("Error--" + object.getString("response"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            en_email = e;
                            en_password = p;
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
                en_email = e;
                en_password = p;
                isEmailOK = true;
                isPassOK = true;
                Toaster.toast("Error en el servidor");
            }
        });
    }

    private void signupResponse(final String e, final String p) {
        LoginServer.signup(LoginBase.this, stringServer(e), stringServer(p), new LoginServer.ServerInterface() {
            @Override
            public void onServerResponse(final JSONObject object) {
                stopLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (object.getString("response")) {
                                case OK:
                                    acceptSignUp(e, p, object);
                                    break;
                                case ERROR_USER_EXISTS:
                                    input_email.setError("La cuenta ya existe!!");
                                    email.requestFocus();
                                    en_password = p;
                                    isPassOK = true;
                                    en_email = null;
                                    isEmailOK = false;
                                    break;
                                case ERROR_NO_INFO:
                                    Toaster.toast("Error en la Solicitud");
                                    en_email = e;
                                    en_password = p;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    break;
                                default:
                                    en_email = e;
                                    en_password = p;
                                    isEmailOK = true;
                                    isPassOK = true;
                                    Toaster.toast("Error--" + object.getString("response"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            en_email = e;
                            en_password = p;
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
                en_email = e;
                en_password = p;
                isEmailOK = true;
                isPassOK = true;
                Toaster.toast("Error en el servidor");
            }
        });
    }

    private void acceptLogin(String email_c, String pass_c, JSONObject object) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        Parser parser = new Parser();
        editor.putString("login_email", email.getText().toString().toLowerCase()).apply();
        editor.putString("login_email_coded", email_c).apply();
        editor.putString("login_pass_coded", pass_c).apply();
        String favs = parser.getUserFavs(object.toString());
        String vistos = parser.getUserVistos(object.toString());
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("favoritos", favs).apply();
        sharedPreferences.edit().putString("vistos", vistos).apply();
        en_email = null;
        en_password = null;
        isEmailOK = false;
        isPassOK = false;
        new Parser().saveBackup(this);
        setResult(LOGIN_RESPONSE_CODE);
        finish();
    }

    private void acceptSignUp(String email_c, String pass_c, JSONObject object) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("login_email", email.getText().toString().toLowerCase()).apply();
        editor.putString("login_email_coded", email_c).apply();
        editor.putString("login_pass_coded", pass_c).apply();
        en_email = null;
        en_password = null;
        isEmailOK = false;
        isPassOK = false;
        new Parser().saveBackup(this);
        setResult(SIGNUP_RESPONSE_CODE);
        finish();
    }

    private void startLoading() {
        dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content(isInSignUp ? "Creando Usuario" : "Iniciando Sesion")
                .cancelable(false)
                .build();
        dialog.show();
    }

    private void stopLoading() {
        if (dialog != null) dialog.dismiss();
    }

    private boolean isInfoValid() {
        dismissErrors();
        String e = email.getText().toString().toLowerCase();
        String p = password.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            if (isInSignUp) {
                if (userList.contains(e)) {
                    input_email.setError("La cuenta ya existe!!");
                    input_email.requestFocus();
                    return false;
                }
            } else {
                if (!userList.contains(e)) {
                    input_email.setError("Cuenta no encontrada");
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
        if (isInSignUp) {
            if (!p.equals(r_password.getText().toString())) {
                input_r_password.setError("Las contraseñas no coinciden");
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


    private void transformToSignUp() {
        isEmailOK = false;
        isInSignUp = true;
        input_password.setPasswordVisibilityToggleEnabled(false);
        input_r_password.setVisibility(View.VISIBLE);
        create.setVisibility(View.GONE);
        create.animate().alpha(0.0f).setDuration(500).start();
        login.setText("CREAR");
    }

    private void transformToLogin() {
        isInSignUp = false;
        input_password.setPasswordVisibilityToggleEnabled(true);
        input_r_password.setVisibility(View.GONE);
        create.setVisibility(View.VISIBLE);
        create.animate().alpha(1.0f).setDuration(500).start();
        login.setText("ENTRAR");
        r_password.setText("");
    }

    @Override
    public void onBackPressed() {
        if (isInSignUp) {
            transformToLogin();
        } else {
            finish();
        }
    }
}