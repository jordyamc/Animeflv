package knf.animeflv.AdminControl;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.AdminControl.ControlEnum.AdminBundle;
import knf.animeflv.AdminControl.ControlEnum.Control;
import knf.animeflv.BackEncryption;
import knf.animeflv.ColorsRes;
import knf.animeflv.Interfaces.EncryptionListener;
import knf.animeflv.LoginActivity.LoginUser;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class AdminControlDialog extends DialogFragment {
    EditText et_1;
    EditText et_2;
    MaterialDialog dialog;
    String base_url;

    public static AdminControlDialog create() {
        AdminControlDialog dialog = new AdminControlDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customView;
        try {
            AdminBundle.control.value();
            customView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_admi_users, null);
        } catch (InflateException e) {
            Toaster.toast("Error");
            getActivity().finish();
            throw new IllegalStateException("This device does not support Web Views.");
        }
        base_url = new Parser().getBaseUrl(TaskType.NORMAL, getActivity()) + "admin-control.php?certificate=" + new Parser().getCertificateSHA1Fingerprint(getActivity());
        dialog = new MaterialDialog.Builder(getActivity())
                .title(getTitle())
                .titleGravity(GravityEnum.CENTER)
                .customView(customView, false)
                .autoDismiss(false)
                .cancelable(false)
                .positiveText("COMENZAR")
                .negativeText("CANCELAR")
                .backgroundColor(ThemeUtils.isAmoled(getActivity()) ? ColorsRes.Prim(getActivity()) : ColorsRes.Blanco(getActivity()))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.getActionButton(DialogAction.POSITIVE).setText("PROCESANDO...");
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        Steps();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();
        et_1 = (EditText) dialog.getCustomView().findViewById(R.id.et_admin_1);
        et_2 = (EditText) dialog.getCustomView().findViewById(R.id.et_admin_2);
        setUpEdit();
        return dialog;
    }

    private void Steps() {
        final String email;
        final String n_email;
        switch (AdminBundle.control.value()) {
            case 0:
                email = et_1.getText().toString();
                if (isEmailValid(email)) {
                    setTextOnUi(et_2, "");
                    setHintOnUi(et_2, "Codificando Email...");
                    new BackEncryption(BackEncryption.Type.ENCRYPT, email)
                            .setOnFinishEncryptListener(new EncryptionListener() {
                                @Override
                                public void onFinish(String finalString) {
                                    Start(finalString, (String) null);
                                }
                            })
                            .executeOnExecutor(ExecutorManager.getExecutor());
                } else {
                    dialog.getActionButton(DialogAction.POSITIVE).setText("COMENZAR");
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    et_1.setError("Correo no valido");
                }
                break;
            case 1:
                email = et_1.getText().toString();
                n_email = et_2.getText().toString();
                if (isEmailValid(email) && isEmailValid(n_email)) {
                    setTextOnUi(et_1, n_email);
                    setTextOnUi(et_2, "");
                    setHintOnUi(et_2, "Codificando Email...");
                    new BackEncryption(BackEncryption.Type.ENCRYPT, email)
                            .setOnFinishEncryptListener(new EncryptionListener() {
                                @Override
                                public void onFinish(final String org_email) {
                                    setTextOnUi(et_2, "");
                                    setHintOnUi(et_2, "Codificando Nuevo Email...");
                                    new BackEncryption(BackEncryption.Type.ENCRYPT, n_email)
                                            .setOnFinishEncryptListener(new EncryptionListener() {
                                                @Override
                                                public void onFinish(String finalString) {
                                                    Start(org_email, finalString);
                                                }
                                            })
                                            .executeOnExecutor(ExecutorManager.getExecutor());
                                }
                            })
                            .executeOnExecutor(ExecutorManager.getExecutor());
                } else {
                    if (!isEmailValid(email)) {
                        et_1.setError("Correo no valido");
                        et_1.requestFocus();
                    }
                    if (!isEmailValid(n_email)) {
                        et_2.setError("Correo no valido");
                        et_2.requestFocus();
                    }
                    dialog.getActionButton(DialogAction.POSITIVE).setText("COMENZAR");
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
                break;
            case 2:
                email = et_1.getText().toString();
                final String n_pass = et_2.getText().toString();
                if (isEmailValid(email) && n_pass.trim().length() >= 4) {
                    setTextOnUi(et_2, "");
                    setHintOnUi(et_2, "Codificando Email...");
                    new BackEncryption(BackEncryption.Type.ENCRYPT, email)
                            .setOnFinishEncryptListener(new EncryptionListener() {
                                @Override
                                public void onFinish(final String org_email) {
                                    setTextOnUi(et_2, "");
                                    setHintOnUi(et_2, "Codificando Nueva Contraseña...");
                                    new BackEncryption(BackEncryption.Type.ENCRYPT, n_pass.trim())
                                            .setOnFinishEncryptListener(new EncryptionListener() {
                                                @Override
                                                public void onFinish(String finalString) {
                                                    Start(org_email, finalString);
                                                }
                                            })
                                            .executeOnExecutor(ExecutorManager.getExecutor());
                                }
                            })
                            .executeOnExecutor(ExecutorManager.getExecutor());
                } else {
                    if (!isEmailValid(email)) {
                        et_1.setError("Correo no valido");
                        et_1.requestFocus();
                    }
                    if (n_pass.trim().length() < 4) {
                        et_2.setError("Contraseña muy corta");
                        et_2.requestFocus();
                    }
                    dialog.getActionButton(DialogAction.POSITIVE).setText("COMENZAR");
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
                break;
            case 3:
                email = et_1.getText().toString();
                if (isEmailValid(email)) {
                    new BackEncryption(BackEncryption.Type.ENCRYPT, email)
                            .setOnFinishEncryptListener(new EncryptionListener() {
                                @Override
                                public void onFinish(String finalString) {
                                    Start(LoginUser.stringServer(finalString), email);
                                }
                            })
                            .executeOnExecutor(ExecutorManager.getExecutor());
                } else {
                    dialog.getActionButton(DialogAction.POSITIVE).setText("COMENZAR");
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    et_1.setError("Correo no valido");
                }
                break;
        }
    }

    private void Start(String email_coded, @Nullable String... extras) {
        Log.d("Final url", getUrl(email_coded, extras));
        setTextOnUi(et_2, "");
        setHintOnUi(et_2, "Cargando servidor...");
        new SyncHttpClient().get(getUrl(email_coded, extras), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    logResponse(statusCode, response.getString("response"), "OK");
                    if (statusCode == 200 && response.getString("response") != null) {
                        if (response.getBoolean("isCoded")) {
                            setTextOnUi(et_2, "");
                            setHintOnUi(et_2, "Decodificando respuesta...");
                            new BackEncryption(BackEncryption.Type.DECRYPT, response.getString("response"))
                                    .setOnFinishEncryptListener(new EncryptionListener() {
                                        @Override
                                        public void onFinish(String finalString) {
                                            setTextOnUi(et_2, finalString);
                                            setHintOnUi(et_2, "Resultado:");
                                            showStart();
                                        }
                                    })
                                    .executeOnExecutor(ExecutorManager.getExecutor());
                        } else {
                            if (AdminBundle.control == Control.DELETE) {
                                setTextOnUi(et_1, "");
                                showStart();
                                Toaster.toast("Cuenta Eliminada");
                            } else {
                                et_2.requestFocus();
                                setTextOnUi(et_2, "");
                                setHintOnUi(et_2, response.getString("response"));
                                showStart();
                            }
                        }
                    } else {
                        if (AdminBundle.control == Control.DELETE) {
                            Toaster.toast("Error en operacion");
                        } else {
                            et_2.requestFocus();
                            et_2.setError("Error en operacion");
                            showStart();
                        }
                    }
                } catch (Exception e) {
                    et_2.requestFocus();
                    et_2.setError("Error en operacion");
                    showStart();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                logResponse(statusCode, responseString, "Error");
                if (responseString != null) {
                    if (AdminBundle.control == Control.DELETE) {
                        Toaster.toast(responseString);
                    } else {
                        et_2.requestFocus();
                        et_2.setError(responseString);
                    }
                }
                showStart();
            }
        });
    }

    private void setTextOnUi(final EditText et, final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et.requestFocus();
                et.setText(text);
            }
        });
    }

    private void setHintOnUi(final EditText et, final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et.requestFocus();
                et.setHint(text);
            }
        });
    }

    private void showStart() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.getActionButton(DialogAction.POSITIVE).setText("COMENZAR");
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
            });
        } catch (Exception e) {
            return;
        }

    }

    private void logResponse(int statusCode, String responseString, String status) {
        try {
            Log.d(status + " Code " + statusCode, responseString);
        } catch (Exception e) {
            if (responseString != null) {
                Log.d(status + " Code " + statusCode, responseString);
            } else {
                Log.d(status + " Code " + statusCode, "null");
            }
        }
    }


    private String getUrl(String email_coded, @Nullable String... extras) {
        switch (AdminBundle.control.value()) {
            case 0:
                return base_url + "&email_coded=" + email_coded + "&g_pass";
            case 1:
                return base_url + "&email_coded=" + email_coded + "&new_email_coded=" + extras[0] + "&f_c_email";
            case 2:
                return base_url + "&email_coded=" + email_coded + "&new_pass_coded=" + extras[0] + "&f_c_pass";
            case 3:
                return base_url + "&email_coded=" + email_coded + "&email_normal=" + extras[0] + "&delete";
            default:
                return "Error";
        }
    }

    private String getTitle() {
        switch (AdminBundle.control.value()) {
            case 0:
                return "OBTENER CONTRASEÑA";
            case 1:
                return "CAMBIAR CORREO";
            case 2:
                return "CAMBIAR CONTRASEÑA";
            case 3:
                return "ELIMINAR CUENTA";
            default:
                return "No Data";
        }
    }

    private void setUpEdit() {
        switch (AdminBundle.control.value()) {
            case 0:
                et_1.setHint("Email:");
                et_1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                et_2.setHint("Resultado:");
                et_2.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 1:
                et_1.setHint("Email:");
                et_1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                et_2.setHint("Nuevo Email:");
                et_2.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case 2:
                et_1.setHint("Email:");
                et_1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                et_2.setHint("Nueva Contraseña:");
                et_2.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 3:
                et_1.setHint("Email:");
                et_1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                et_2.setVisibility(View.GONE);
                break;
        }
        if (ThemeUtils.isAmoled(getActivity())) {
            et_1.setHintTextColor(ThemeUtils.getAcentColor(getActivity()));
            et_2.setHintTextColor(ThemeUtils.getAcentColor(getActivity()));
        }
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
