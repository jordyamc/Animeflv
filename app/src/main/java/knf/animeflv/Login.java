package knf.animeflv;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import se.simbio.encryption.Encryption;

/**
 * Created by Jordy on 04/09/2015.
 */
public class Login extends DialogFragment{
    public interface OnResponseCallback {
        void ResponseCallback(String response);
    }

    public Login create() {
        Login dialog = new Login();
        this.listener = null;
        return dialog;
    }
    EditText email;
    EditText contrasena;
    EditText rcontrasena;
    EditText login_email;
    EditText login_contrasena;
    EditText cCorreo_email;
    EditText cCorreo_contrasena;
    EditText cCorreo_nemail;
    EditText cPass_email;
    EditText cPass_contrasena;
    EditText cPass_ncontrasena;
    EditText cPass_rncontrasena;
    LinearLayout main;
    LinearLayout nCuenta;
    Button login;
    Button nuevo;
    Button cCorreo;
    Button cPass;
    Button izquierda;
    Button derecha;
    LinearLayout buttons_main;
    LinearLayout buttons_loged;
    LinearLayout loginPage;
    LinearLayout cCorreoPage;
    LinearLayout cPassPage;
    int dialogo;
    MaterialDialog dialog;
    private OnResponseCallback listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customView = LayoutInflater.from(getActivity()).inflate(R.layout.lay_login, null);
        dialog = new MaterialDialog.Builder(getActivity())
                .title("Sincronizar")
                .titleGravity(GravityEnum.CENTER)
                .customView(customView, false)
                .autoDismiss(false)
                .build();
                    email = (EditText)dialog.getCustomView().findViewById(R.id.email);
                    email.setTextColor(getResources().getColor(R.color.black));
                    contrasena = (EditText)dialog.getCustomView().findViewById(R.id.contrasena);
                    contrasena.setTextColor(getResources().getColor(R.color.black));
                    rcontrasena = (EditText)dialog.getCustomView().findViewById(R.id.rcontrasena);
                    login_email = (EditText)dialog.getCustomView().findViewById(R.id.email_login);
                    login_contrasena = (EditText)dialog.getCustomView().findViewById(R.id.contrasena_login);
                    login_email.setTextColor(getResources().getColor(R.color.black));
                    login_contrasena.setTextColor(getResources().getColor(R.color.black));
                    rcontrasena.setTextColor(getResources().getColor(R.color.black));
                    main = (LinearLayout) dialog.getCustomView().findViewById(R.id.main);
                    nCuenta = (LinearLayout) dialog.getCustomView().findViewById(R.id.nCuenta);
                    buttons_main=(LinearLayout) dialog.getCustomView().findViewById(R.id.main_buttons);
                    buttons_loged=(LinearLayout) dialog.getCustomView().findViewById(R.id.loged_buttons);
                    loginPage=(LinearLayout) dialog.getCustomView().findViewById(R.id.login_page);
                    cCorreoPage=(LinearLayout)dialog.getCustomView().findViewById(R.id.cCorreo_page);
                    cCorreo_email=(EditText)dialog.getCustomView().findViewById(R.id.email_cCorreo);
                    cCorreo_contrasena=(EditText)dialog.getCustomView().findViewById(R.id.contrasena_cCorreo);
                    cCorreo_nemail=(EditText)dialog.getCustomView().findViewById(R.id.nemail_cCorreo);
                    cCorreo_email.setTextColor(getResources().getColor(R.color.black));
                    cCorreo_contrasena.setTextColor(getResources().getColor(R.color.black));
                    cCorreo_nemail.setTextColor(getResources().getColor(R.color.black));
                    cPassPage=(LinearLayout)dialog.getCustomView().findViewById(R.id.cPass_page);
                    cPass_email=(EditText)dialog.getCustomView().findViewById(R.id.email_cPass);
                    cPass_contrasena=(EditText)dialog.getCustomView().findViewById(R.id.contrasena_cPass);
                    cPass_ncontrasena=(EditText)dialog.getCustomView().findViewById(R.id.nPass_cPass);
                    cPass_rncontrasena=(EditText)dialog.getCustomView().findViewById(R.id.rnPass_cPass);
                    cPass_email.setTextColor(getResources().getColor(R.color.black));
                    cPass_contrasena.setTextColor(getResources().getColor(R.color.black));
                    cPass_ncontrasena.setTextColor(getResources().getColor(R.color.black));
                    cPass_rncontrasena.setTextColor(getResources().getColor(R.color.black));
                    login = (Button) dialog.getCustomView().findViewById(R.id.login);
                    nuevo = (Button) dialog.getCustomView().findViewById(R.id.nuevaCuenta);
                    derecha = (Button) dialog.getCustomView().findViewById(R.id.boton_der);
                    izquierda = (Button) dialog.getCustomView().findViewById(R.id.boton_iz);
                    cCorreo = (Button)dialog.getCustomView().findViewById(R.id.cCorreo);
                    cPass = (Button)dialog.getCustomView().findViewById(R.id.cCont);
                    String actCuenta=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("login_email","null");
                    dialogo=0;
                    if (actCuenta.equals("null")){
                        main.setVisibility(View.VISIBLE);
                        buttons_main.setVisibility(View.VISIBLE);
                        buttons_loged.setVisibility(View.GONE);
                        derecha.setVisibility(View.INVISIBLE);
                        dialogo=0;
                    }else {
                        main.setVisibility(View.VISIBLE);
                        buttons_main.setVisibility(View.GONE);
                        buttons_loged.setVisibility(View.VISIBLE);
                        izquierda.setText("Cerrar Sesion");
                        derecha.setVisibility(View.INVISIBLE);
                        dialogo=3;
                    }
                    login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogo=2;
                            new LoginServer(getActivity(),TaskType.LIST_USERS,null,null,null,null).execute("http://necrotic-neganebulus.hol.es/fav-server.php?tipo=list");
                            main.setVisibility(View.GONE);
                            loginPage.setVisibility(View.VISIBLE);
                            izquierda.setText("ATRAS");
                            derecha.setText("INICIAR SESION");
                            derecha.setVisibility(View.VISIBLE);
                            derecha.setEnabled(true);
                        }
                    });
                    nuevo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new LoginServer(getActivity(),TaskType.LIST_USERS,null,null,null,null).execute("http://necrotic-neganebulus.hol.es/fav-server.php?tipo=list");
                            main.setVisibility(View.GONE);
                            nCuenta.setVisibility(View.VISIBLE);
                            derecha.setText("CREAR");
                            derecha.setVisibility(View.VISIBLE);
                            izquierda.setText("ATRAS");
                            derecha.setEnabled(true);
                            dialogo=1;
                        }
                    });
                    cCorreo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new LoginServer(getActivity(),TaskType.LIST_USERS,null,null,null,null).execute("http://necrotic-neganebulus.hol.es/fav-server.php?tipo=list");
                            main.setVisibility(View.GONE);
                            cCorreoPage.setVisibility(View.VISIBLE);
                            derecha.setText("CAMBIAR");
                            derecha.setVisibility(View.VISIBLE);
                            izquierda.setText("ATRAS");
                            dialogo=4;
                            String login_email=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("login_email","null");
                            cCorreo_email.setText(login_email);
                            cCorreo_contrasena.requestFocus();
                        }
                    });
                    cPass.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new LoginServer(getActivity(),TaskType.LIST_USERS,null,null,null,null).execute("http://necrotic-neganebulus.hol.es/fav-server.php?tipo=list");
                            main.setVisibility(View.GONE);
                            cPassPage.setVisibility(View.VISIBLE);
                            derecha.setText("CAMBIAR");
                            derecha.setVisibility(View.VISIBLE);
                            izquierda.setText("ATRAS");
                            dialogo=5;
                            String login_email=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("login_email","null");
                            cPass_email.setText(login_email);
                            cPass_contrasena.requestFocus();
                        }
                    });
                    izquierda.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (dialogo){
                                case 0:
                                    dialog.dismiss();
                                    break;
                                case 1:
                                    dialogo=0;
                                    nCuenta.setVisibility(View.GONE);
                                    main.setVisibility(View.VISIBLE);
                                    buttons_main.setVisibility(View.VISIBLE);
                                    buttons_loged.setVisibility(View.GONE);
                                    derecha.setVisibility(View.INVISIBLE);
                                    izquierda.setText("CANCELAR");
                                    break;
                                case 2:
                                    dialogo=0;
                                    loginPage.setVisibility(View.GONE);
                                    main.setVisibility(View.VISIBLE);
                                    buttons_main.setVisibility(View.VISIBLE);
                                    buttons_loged.setVisibility(View.GONE);
                                    derecha.setVisibility(View.INVISIBLE);
                                    izquierda.setText("CANCELAR");
                                    break;
                                case 3:
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("login_email", "null").apply();
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("login_email_coded", "null").apply();
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("login_pass_coded", "null").apply();
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(),"Sesion Cerrada",Toast.LENGTH_SHORT).show();
                                    break;
                                case 4:
                                    dialogo=3;
                                    cCorreoPage.setVisibility(View.GONE);
                                    main.setVisibility(View.VISIBLE);
                                    buttons_main.setVisibility(View.GONE);
                                    buttons_loged.setVisibility(View.VISIBLE);
                                    derecha.setVisibility(View.INVISIBLE);
                                    izquierda.setText("Cerrar Sesion");
                                    break;
                                case 5:
                                    dialogo=3;
                                    cPassPage.setVisibility(View.GONE);
                                    main.setVisibility(View.VISIBLE);
                                    buttons_main.setVisibility(View.GONE);
                                    buttons_loged.setVisibility(View.VISIBLE);
                                    derecha.setVisibility(View.INVISIBLE);
                                    izquierda.setText("Cerrar Sesion");
                                    break;
                            }
                        }
                    });
                    derecha.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (dialogo){
                                case 1:
                                    checknuser();
                                    break;
                                case 2:
                                    checklogin();
                                    break;
                                case 4:
                                    checkcCorreo();
                                    break;
                                case 5:
                                    checkcPass();
                                    break;
                            }
                        }
                    });
                    return dialog;
                }
    public void checknuser(){
        String mail=email.getText().toString();
        boolean termina=mail.endsWith("com")||mail.endsWith("net");
        if (mail.contains("@")&&mail.contains(".")&&termina){
            if (contrasena.getText().toString().length()<6){
                contrasena.setError("Contraseña muy corta");
                derecha.setEnabled(true);
            }else {
                if (contrasena.getText().toString().contains(" ")){
                    contrasena.setError("No se admiten espacios");
                    derecha.setEnabled(true);
                }else {
                    if (!contrasena.getText().toString().equals(rcontrasena.getText().toString())){
                        rcontrasena.setError("La contraseña no coincide");
                        derecha.setEnabled(true);
                    }else {
                        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
                        String encriytedmail = encryption.encryptOrNull(email.getText().toString());
                        String encriytedmailfinal=encriytedmail.replace("=","IGUAL").replace("&","AMPERSAND").replace("\"","COMILLA").replace("?","PREGUNTA").replace("+","MAS").replace("/", "SLIDE_DERECHO").replace(",","COMA").trim();
                        String[] lista=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("lista", "null").split(":::");
                        Boolean existe=false;
                        for (String s:lista) {
                            if (s.equals(encriytedmailfinal)){
                                existe=true;
                            }
                        }
                        if (!existe) {
                            String encriytedpass = encryption.encryptOrNull(contrasena.getText().toString());
                            String encriytedpassfinal = encriytedpass.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", getActivity().MODE_PRIVATE);
                            String fav = sharedPreferences.getString("favoritos", "");
                            Log.d("mail", encriytedmailfinal);
                            Log.d("pass", encriytedpassfinal);
                            Log.d("favs", fav);
                            new LoginServer(getActivity(), TaskType.NEW_USER, email.getText().toString(), encriytedmailfinal, encriytedpassfinal, null).execute("http://necrotic-neganebulus.hol.es/fav-server.php?tipo=nCuenta&email_coded=" + encriytedmailfinal + "&pass_coded=" + encriytedpassfinal + "&fav_code=" + fav);
                        }else {
                            Toast.makeText(getActivity(),"El correo ya esta registrado",Toast.LENGTH_SHORT).show();
                            dialogo=2;
                            new LoginServer(getActivity(),TaskType.LIST_USERS,null,null,null,null).execute("http://necrotic-neganebulus.hol.es/fav-server.php?tipo=list");
                            nCuenta.setVisibility(View.GONE);
                            loginPage.setVisibility(View.VISIBLE);
                            izquierda.setText("ATRAS");
                            derecha.setText("INICIAR SESION");
                            derecha.setVisibility(View.VISIBLE);
                            login_email.setText(mail);
                            login_contrasena.requestFocus();
                        }
                    }
                }
            }
        }else {
            email.setError("Correo invalido");
            if (contrasena.getText().toString().length()<4){
                contrasena.setError("Contraseña muy corta");
                derecha.setEnabled(true);
            }else {
                if (contrasena.getText().toString().contains(" ")){
                    contrasena.setError("No se admiten espacios");
                    derecha.setEnabled(true);
                }else {
                    if (!contrasena.getText().toString().equals(rcontrasena.getText().toString())){
                        rcontrasena.setError("La contraseña no coincide");
                        derecha.setEnabled(true);
                    }
                }
            }
        }
    }
    public void checklogin(){
        derecha.setEnabled(false);
        String mail=login_email.getText().toString();
        boolean termina=mail.endsWith("com")||mail.endsWith("net");
        if (mail.indexOf("@")!=0&&mail.contains("@")&&mail.contains(".")&&termina){
            if (login_contrasena.getText().toString().length()<4){
                login_contrasena.setError("Contraseña muy corta");
                derecha.setEnabled(true);
            }else {
                if (login_contrasena.getText().toString().contains(" ")){
                    login_contrasena.setError("No se admiten espacios");
                    derecha.setEnabled(true);
                }else {
                    Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
                    String encriytedmail = encryption.encryptOrNull(login_email.getText().toString());
                    String encriytedmailfinal=encriytedmail.replace("=","IGUAL").replace("&","AMPERSAND").replace("\"","COMILLA").replace("?","PREGUNTA").replace("+","MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
                    String[] lista=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("lista", "null").split(":::");
                    Boolean existe=false;
                    for (String s:lista) {
                        if (s.equals(encriytedmailfinal)){
                            existe=true;
                        }
                    }
                    if (existe) {
                        String encriytedpass = encryption.encryptOrNull(login_contrasena.getText().toString());
                        String encriytedpassfinal = encriytedpass.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
                        Log.d("mail", encriytedmailfinal);
                        Log.d("pass", encriytedpassfinal);
                        new LoginServer(getActivity(), TaskType.GET_FAV, login_email.getText().toString(),encriytedmailfinal,encriytedpassfinal,dialog).execute("http://necrotic-neganebulus.hol.es/fav-server.php?tipo=get&email_coded=" + encriytedmailfinal + "&pass_coded=" + encriytedpassfinal);
                    }else {
                        login_email.setError("Correo no registrado");
                        login_email.requestFocus();
                        derecha.setEnabled(true);
                    }
                }
            }
        }else {
            login_email.setError("Correo invalido");
            if (login_contrasena.getText().toString().length()<6){
                login_contrasena.setError("Contraseña muy corta");
                derecha.setEnabled(true);
            }else {
                if (login_contrasena.getText().toString().contains(" ")){
                    login_contrasena.setError("No se admiten espacios");
                    derecha.setEnabled(true);
                }
            }
        }
    }
    public void checkcPass(){
        derecha.setEnabled(false);
        String mail=cPass_email.getText().toString();
        String npass=cPass_ncontrasena.getText().toString();
        boolean termina=mail.endsWith("com")||mail.endsWith("net");
        if (mail.indexOf("@")!=0&&mail.contains("@")&&mail.contains(".")&&termina){
            if (cPass_contrasena.getText().toString().length()<4){
                cPass_contrasena.setError("Contraseña muy corta");
                derecha.setEnabled(true);
            }else {
                if (cPass_contrasena.getText().toString().contains(" ")) {
                    cPass_contrasena.setError("No se admiten espacios");
                    derecha.setEnabled(true);
                } else {
                    if (cPass_ncontrasena.getText().toString().length() < 4) {
                        cPass_ncontrasena.setError("Contraseña muy corta");
                        derecha.setEnabled(true);
                    } else {
                        if (cPass_ncontrasena.getText().toString().contains(" ")) {
                            cPass_ncontrasena.setError("No se admiten espacios");
                            derecha.setEnabled(true);
                        } else {
                            Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
                            String encriytedmail = encryption.encryptOrNull(cPass_email.getText().toString());
                            String encriytedmailfinal = encriytedmail.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
                            String[] lista = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("lista", "null").split(":::");
                            Boolean existe = false;
                            for (String s : lista) {
                                if (s.equals(encriytedmailfinal)) {
                                    existe = true;
                                }
                            }
                            if (existe) {
                                if (npass.equals(cPass_rncontrasena.getText().toString())) {
                                    String encriytedpass = encryption.encryptOrNull(cCorreo_contrasena.getText().toString());
                                    String encriytedpassfinal = encriytedpass.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
                                    String encriytednpass = encryption.encryptOrNull(cCorreo_contrasena.getText().toString());
                                    String encriytednpassfinal = encriytednpass.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();

                                    Log.d("mail", encriytedmailfinal);
                                    Log.d("pass", encriytedpassfinal);
                                    new LoginServer(getActivity(), TaskType.cPass, null, null, encriytednpassfinal, dialog).execute("http://necrotic-neganebulus.hol.es/fav-server.php?tipo=cPass&email_coded=" + encriytedmailfinal + "&pass_coded=" + encriytedpassfinal + "&new_pass_coded="+encriytednpassfinal);
                                }else {
                                    cPass_rncontrasena.setError("La Contraseña no Coincide");
                                    cPass_rncontrasena.requestFocus();
                                    derecha.setEnabled(true);
                                }
                            } else {
                                cPass_email.setError("Correo no registrado");
                                cPass_email.requestFocus();
                                derecha.setEnabled(true);
                            }
                        }
                    }
                }
            }
        }else {
            cPass_email.setError("Correo invalido");
            if (cPass_contrasena.getText().toString().length()<6){
                cPass_contrasena.setError("Contraseña muy corta");
            }else {
                if (cPass_contrasena.getText().toString().contains(" ")){
                    cPass_contrasena.setError("No se admiten espacios");
                }
            }
        }
    }
    public void checkcCorreo(){
        derecha.setEnabled(false);
        String mail=cCorreo_email.getText().toString();
        String nmail=cCorreo_nemail.getText().toString();
        boolean termina=mail.endsWith("com")||mail.endsWith("net");
        boolean ntermina=nmail.endsWith("com")||nmail.endsWith("net");
        if (!mail.equals("admin")) {
            if (mail.indexOf("@") != 0 && mail.contains("@") && mail.contains(".") && nmail.indexOf("@") != 0 && nmail.contains("@") && nmail.contains(".") && ntermina && termina) {
                if (cCorreo_contrasena.getText().toString().length() < 4) {
                    cCorreo_contrasena.setError("Contraseña muy corta");
                    derecha.setEnabled(true);
                } else {
                    if (cCorreo_contrasena.getText().toString().contains(" ")) {
                        cCorreo_contrasena.setError("No se admiten espacios");
                        derecha.setEnabled(true);
                    } else {
                        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
                        String encriytedmail = encryption.encryptOrNull(cCorreo_email.getText().toString());
                        String encriytedmailfinal = encriytedmail.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
                        String[] lista = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("lista", "null").split(":::");
                        Boolean existe = false;
                        for (String s : lista) {
                            if (s.equals(encriytedmailfinal)) {
                                existe = true;
                            }
                        }
                        if (existe) {
                            String encriytednmail = encryption.encryptOrNull(cCorreo_nemail.getText().toString());
                            String encriytednmailfinal = encriytednmail.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
                            String encriytedpass = encryption.encryptOrNull(cCorreo_contrasena.getText().toString());
                            String encriytedpassfinal = encriytedpass.replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim();
                            Log.d("mail", encriytedmailfinal);
                            Log.d("pass", encriytedpassfinal);
                            new LoginServer(getActivity(), TaskType.cCorreo, cCorreo_nemail.getText().toString(), encriytedmailfinal, null, dialog).execute("http://necrotic-neganebulus.hol.es/fav-server.php?tipo=cCuenta&past_email_coded=" + encriytedmailfinal + "&new_email_coded=" + encriytednmailfinal + "&pass_coded=" + encriytedpassfinal);
                        } else {
                            cCorreo_email.setError("Correo no registrado");
                            cCorreo_email.requestFocus();
                            derecha.setEnabled(true);
                        }
                    }
                }
            } else {
                if (mail.indexOf("@") != 0 && mail.contains("@") && mail.contains(".") && termina) {
                    cCorreo_email.setError("Correo invalido");
                    derecha.setEnabled(true);
                }
                if (nmail.indexOf("@") != 0 && nmail.contains("@") && nmail.contains(".") && ntermina) {
                    cCorreo_nemail.setError("Correo invalido");
                    derecha.setEnabled(true);
                }
                if (cCorreo_contrasena.getText().toString().length() < 6) {
                    cCorreo_contrasena.setError("Contraseña muy corta");
                    derecha.setEnabled(true);
                } else {
                    if (cCorreo_contrasena.getText().toString().contains(" ")) {
                        cCorreo_contrasena.setError("No se admiten espacios");
                        derecha.setEnabled(true);
                    }
                }
            }
        }else {
            Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
            String decEmail=encryption.decryptOrNull(cCorreo_nemail.getText().toString().replace("IGUAL","=").replace("AMPERSAND","&").replace("COMILLA","\"").replace("PREGUNTA","?").replace("MAS","+").replace("SLIDE_DERECHO", "/").replace("COMA", ","));
            String decCont=encryption.decryptOrNull(cCorreo_contrasena.getText().toString().replace("IGUAL","=").replace("AMPERSAND","&").replace("COMILLA","\"").replace("PREGUNTA","?").replace("MAS","+").replace("SLIDE_DERECHO", "/").replace("COMA", ","));
            Toast.makeText(getActivity(),"Modo Admin",Toast.LENGTH_SHORT).show();
            cCorreo_nemail.setText(decEmail);
            cCorreo_contrasena.setText(decCont);
            cCorreo_contrasena.setInputType(InputType.TYPE_CLASS_TEXT);
            derecha.setEnabled(true);
        }
    }
    public void LoginErrors(int cases){
        switch (cases){
            case 1:
                login_contrasena.setError("Contraseña incorrecta");
                login_contrasena.requestFocus();
                derecha.setEnabled(true);
                break;
            case 2:
                login_email.setError("Correo no registrado");
                login_email.requestFocus();
                derecha.setEnabled(true);
                break;
            case 3:
                derecha.setEnabled(true);
                dialog.dismiss();
                Toast.makeText(getActivity(),"Sesion Iniciada!!",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void cCorreoErrors(int cases){
        switch (cases){
            case 1:
                cCorreo_contrasena.setError("Contraseña incorrecta");
                cCorreo_contrasena.requestFocus();
                derecha.setEnabled(true);
                break;
            case 3:
                derecha.setEnabled(true);
                dialog.dismiss();
                Toast.makeText(getActivity(),"Email Cambiado!!",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void cPassErrors(int cases){
        switch (cases){
            case 1:
                cPass_contrasena.setError("Contraseña incorrecta");
                cPass_contrasena.requestFocus();
                derecha.setEnabled(true);
                break;
            case 3:
                derecha.setEnabled(true);
                dialog.dismiss();
                Toast.makeText(getActivity(),"Contraseña Cambiada!!",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setCustomObjectListener(OnResponseCallback listener) {
        this.listener = listener;
    }
}
