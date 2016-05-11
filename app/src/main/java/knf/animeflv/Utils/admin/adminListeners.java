package knf.animeflv.Utils.admin;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import knf.animeflv.AdminControl.ControlActivity;
import knf.animeflv.ServerReload.manualServerReload;
import knf.animeflv.Utils.NetworkUtils;
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

}
