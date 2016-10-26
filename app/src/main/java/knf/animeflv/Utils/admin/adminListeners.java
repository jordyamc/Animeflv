package knf.animeflv.Utils.admin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.AdminControl.ControlActivity;
import knf.animeflv.Parser;
import knf.animeflv.ServerReload.manualServerReload;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.NoLogInterface;
import knf.animeflv.newMain;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 03/05/2016.
 */
public class adminListeners {
    private Context context;

    public adminListeners(Context context) {
        this.context = context;
    }

    public Drawer.OnDrawerItemClickListener onEncButton() {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                ((newMain) context).result.setSelection(0);
                ((newMain) context).result.closeDrawer();
                ((newMain) context).showEncDialog();
                return false;
            }
        };
    }

    public Drawer.OnDrawerItemClickListener onManualButton() {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                ((newMain) context).result.setSelection(0);
                ((newMain) context).result.closeDrawer();
                if (NetworkUtils.isNetworkAvailable()) {
                    context.startActivity(new Intent(context, manualServerReload.class));
                } else {
                    Toaster.toast("Se necesita internet!");
                }
                return false;
            }
        };
    }

    public Drawer.OnDrawerItemClickListener onAccountsButton() {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                ((newMain) context).result.setSelection(0);
                ((newMain) context).result.closeDrawer();
                if (NetworkUtils.isNetworkAvailable()) {
                    context.startActivity(new Intent(context, ControlActivity.class));
                } else {
                    Toaster.toast("Se necesita internet!");
                }
                return false;
            }
        };
    }

    public Drawer.OnDrawerItemClickListener onFilterList() {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                final MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .progress(true, 0)
                        .content("Buscando en la lista")
                        .build();
                dialog.show();
                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                asyncHttpClient.setLogInterface(new NoLogInterface());
                asyncHttpClient.setLoggingEnabled(false);
                asyncHttpClient.setResponseTimeout(15000);
                asyncHttpClient.get(new Parser().getBaseUrl(TaskType.NORMAL, context) + "emisionlist.php?clean", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        dialog.dismiss();
                        try {
                            if (response.getInt("cleaned") > 0) {
                                new MaterialDialog.Builder(context)
                                        .title("Lista Filtrada")
                                        .titleGravity(GravityEnum.CENTER)
                                        .content("Se han eliminado " + response.getInt("cleaned") + " animes de la lista")
                                        .build().show();
                            } else {
                                new MaterialDialog.Builder(context)
                                        .content("La lista esta limpia!!!")
                                        .build().show();
                            }
                        } catch (Exception e) {
                            Toaster.toast("Error al limpiar lista!!!");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.e("Clean Emision", responseString);
                        Toaster.toast("Error al filtrar lista");
                    }
                });
                return false;
            }
        };
    }

}
