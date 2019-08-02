package knf.animeflv.ServerReload.Adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.ServerReload.manualServerReload;
import knf.animeflv.ServerReload.manualServerReload.State;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 17/08/2015.
 */
public class AdapterAdminRecs extends RecyclerView.Adapter<AdapterAdminRecs.ViewHolder> {

    private Context context;
    private List<RecObject> objects;
    private boolean isCached;

    public AdapterAdminRecs(Context context, List<RecObject> objects, State state) {
        this.context = context;
        this.objects = objects;
        this.isCached = state == State.CACHE;
    }

    @Override
    public AdapterAdminRecs.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_rec_animes, parent, false);
        return new AdapterAdminRecs.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterAdminRecs.ViewHolder holder, final int position) {
        if (ThemeUtils.isAmoled(context)){
            holder.titulo.setTextColor(ColorsRes.SecondaryTextDark(context));
        }else {
            holder.titulo.setTextColor(ColorsRes.SecondaryTextLight(context));
        }
        holder.root.setBackgroundColor(ColorsRes.Transparent(context));
        holder.titulo.setText(objects.get(holder.getAdapterPosition()).getName());
        holder.state.setTextColor(ThemeUtils.isAmoled(context)?ColorsRes.Blanco(context):ColorsRes.Prim(context));
        holder.state.setText("Cargando...");
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.state.setTextColor(ThemeUtils.isAmoled(context)?ColorsRes.Blanco(context):ColorsRes.Prim(context));
                holder.state.setText("Cargando...");
                updateState(holder.state, objects.get(holder.getAdapterPosition()), RecObject.Type.BYPASS);
            }
        });
        updateState(holder.state, objects.get(holder.getAdapterPosition()), RecObject.Type.NORMAL);
    }

    private void updateState(final TextView state, RecObject object, RecObject.Type type) {
        Log.d("Load url", object.getUrl(type));
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(15000);
        client.get(object.getUrl(type), null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ((manualServerReload) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        state.setText("Error!");
                        state.setTextColor(ColorsRes.Rojo(context));
                    }
                });
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (!FileUtil.isJSONValid(responseString)) {
                    ((manualServerReload) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            state.setText("Error!");
                            state.setTextColor(ColorsRes.Rojo(context));
                        }
                    });
                } else {
                    ((manualServerReload) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isCached) {
                                state.setText("CACHE");
                                state.setTextColor(ColorsRes.Amarillo(context));
                            } else {
                                state.setText("OK");
                                state.setTextColor(ColorsRes.Verde(context));
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout root;
        public TextView titulo;
        public TextView state;

        public ViewHolder(View itemView) {
            super(itemView);
            this.root = (RelativeLayout) itemView.findViewById(R.id.parent);
            this.titulo = (TextView) itemView.findViewById(R.id.titulo);
            this.state = (TextView) itemView.findViewById(R.id.estado);
        }
    }

}