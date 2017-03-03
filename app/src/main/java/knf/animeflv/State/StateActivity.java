package knf.animeflv.State;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.JsonFactory.ServerGetter;
import knf.animeflv.R;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 03/03/2017.
 */

public class StateActivity extends AppCompatActivity {
    private static final String motivo_503 = "La pagina esta en modo de proteccion anti DDOS (si no sabes de esto buscalo en google) por lo tanto la app no puede obtener la informacion mas actualizada y usa el cache almacenado.";
    private static final String motivo_OFFLINE = "La app no detecta conexion a internet.";
    private static final String motivo_OTHER = "La app no pudo encontrar un motivo para este error.";
    private static final String solucion_503 = "La unica solucion es esperar a que la pagina levante la proteccion.";
    private static final String solucion_OFFLINE = "Si estas seguro que tienes internet, puedes intentar cambiar la configuracion de la app para admitir conexion de WIFI y Datos moviles.";
    private static final String solucion_OTHER = "Reporta este problema en un mensaje directo a la pagina de Facebook.";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.card)
    CardView cardView;
    @BindView(R.id.extras)
    LinearLayout layout_extras;
    @BindView(R.id.conexion)
    TextView conexion;
    @BindView(R.id.codigo)
    TextView codigo;
    @BindView(R.id.estado)
    TextView estado;
    @BindView(R.id.motivo)
    TextView motivo;
    @BindView(R.id.solucion)
    TextView solucion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Estado");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toaster.toast("Recargando...");
                reset();
                return true;
            }
        });
        if (ThemeUtils.isAmoled(this)) {
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            cardView.setCardBackgroundColor(ColorsRes.Prim(this));
        }
        startCheck();
    }

    private void startCheck() {
        if (NetworkUtils.isNetworkAvailable()) {
            ServerGetter.getClient().get("http://animeflv.net", null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("Status Check", "Error: " + statusCode);
                    if (statusCode != 0) {
                        setInfo(statusCode);
                    } else {
                        startCheck();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.e("Status Check", "Correct: " + statusCode);
                    setInfo(statusCode);
                }
            });
        } else {
            setInfo(-2);
        }
    }

    private void reset() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conexion.setText("conectando...");
                codigo.setText("\u221E");
                estado.setText("esperando...");
                motivo.setText("ninguno");
                solucion.setText("ninguna");
                layout_extras.setVisibility(View.GONE);
                conexion.setTextColor(getTextColor(-1));
                codigo.setTextColor(getTextColor(-1));
                estado.setTextColor(getTextColor(-1));
                startCheck();
            }
        });
    }

    private void setInfo(final int code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                codigo.setText(String.valueOf(code));
                conexion.setTextColor(getTextColor(code));
                estado.setTextColor(getTextColor(code));
                codigo.setTextColor(getTextColor(code));
                switch (code) {
                    case -2:
                        conexion.setText("ADVERTENCIA");
                        estado.setText("NO SE DETECTA INTERNET");
                        motivo.setText(motivo_OFFLINE);
                        solucion.setText(solucion_OFFLINE);
                        layout_extras.setVisibility(View.VISIBLE);
                        break;
                    case 200:
                        conexion.setText("OK");
                        estado.setText("CONECTADO");
                        break;
                    case 503:
                        conexion.setText("ADVERTENCIA");
                        estado.setText("NO SE PUEDE CONECTAR");
                        motivo.setText(motivo_503);
                        solucion.setText(solucion_503);
                        layout_extras.setVisibility(View.VISIBLE);
                        break;
                    default:
                        conexion.setText("ERROR");
                        estado.setText("NO SE RECONOCE EL CODIGO");
                        motivo.setText(motivo_OTHER);
                        solucion.setText(solucion_OTHER);
                        layout_extras.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @ColorInt
    private int getTextColor(int status) {
        switch (status) {
            case 200:
                return ColorsRes.Verde(this);
            case -2:
            case 503:
                return ColorsRes.Amarillo(this);
            case -1:
                return (ThemeUtils.isAmoled(this) ? ColorsRes.SecondaryTextDark(this) : ColorsRes.SecondaryTextLight(this));
            default:
                return ColorsRes.Rojo(this);
        }
    }
}
