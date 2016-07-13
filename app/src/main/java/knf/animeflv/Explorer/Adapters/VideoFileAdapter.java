package knf.animeflv.Explorer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.Explorer.ExplorerInterfaces;
import knf.animeflv.Explorer.Models.ModelFactory;
import knf.animeflv.Explorer.Models.VideoFile;
import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 22/08/2015.
 */
public class VideoFileAdapter extends RecyclerView.Adapter<VideoFileAdapter.ViewHolder> {

    List<VideoFile> list;
    private Activity context;
    private ExplorerInterfaces interfaces;
    private File current;

    public VideoFileAdapter(Activity context, File file) {
        this.context = context;
        this.list = ModelFactory.createVideosList(file);
        this.interfaces = (ExplorerInterfaces) context;
        this.current = file;
    }

    @Override
    public VideoFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.explorer_item, parent, false);
        return new VideoFileAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoFileAdapter.ViewHolder holder, final int position) {
        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PicassoCache.getPicassoInstance(context).load(list.get(holder.getAdapterPosition()).getThumbImage()).error(R.drawable.ic_block_r).into(holder.img);
                    }
                });
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
        holder.titulo.setText(list.get(holder.getAdapterPosition()).getTitle());
        holder.cap.setText(list.get(holder.getAdapterPosition()).getDuration(context));
        holder.cap.setTextColor(ThemeUtils.getAcentColor(context));
        if (ThemeUtils.isAmoled(context)) {
            holder.titulo.setTextColor(ColorsRes.Holo_Dark(context));
            holder.ver.setColorFilter(ColorsRes.Holo_Dark(context));
            holder.del.setColorFilter(ColorsRes.Holo_Dark(context));
        } else {
            holder.titulo.setTextColor(ColorsRes.Holo_Light(context));
            holder.ver.setColorFilter(ColorsRes.Holo_Light(context));
            holder.del.setColorFilter(ColorsRes.Holo_Light(context));
        }
        holder.ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StreamManager.Play(context, list.get(holder.getAdapterPosition()).getEID());
            }
        });
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .content("Esta seguro que desea eliminar el " + list.get(holder.getAdapterPosition()).getTitle().toLowerCase() + "?")
                        .positiveText("Eliminar")
                        .negativeText("cancelar")
                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ManageDownload.cancel(context, list.get(holder.getAdapterPosition()).getEID());
                                FileUtil.DeleteAnime(list.get(holder.getAdapterPosition()).getEID());
                                if (!list.get(holder.getAdapterPosition()).getFile().exists()) {
                                    Toaster.toast("Archivo eliminado");
                                    list = ModelFactory.createVideosList(current);
                                    notifyDataSetChanged();
                                } else {
                                    Toaster.toast("Error al eliminar");
                                    list = ModelFactory.createVideosList(current);
                                    notifyDataSetChanged();
                                }
                            }
                        }).build().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.root)
        public RelativeLayout root;
        @Bind(R.id.img)
        public ImageView img;
        @Bind(R.id.titulo)
        public TextView titulo;
        @Bind(R.id.cap)
        public TextView cap;
        @Bind(R.id.ib_ver)
        public ImageButton ver;
        @Bind(R.id.ib_del)
        public ImageButton del;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
