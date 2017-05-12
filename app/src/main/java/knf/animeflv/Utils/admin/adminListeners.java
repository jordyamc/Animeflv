package knf.animeflv.Utils.admin;

import android.content.Context;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import knf.animeflv.newMain;

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
}
