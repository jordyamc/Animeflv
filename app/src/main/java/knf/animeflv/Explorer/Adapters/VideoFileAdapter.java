package knf.animeflv.Explorer.Adapters;

import android.app.Activity;
import android.graphics.Color;
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
import com.squareup.picasso.Callback;

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

public class VideoFileAdapter extends RecyclerView.Adapter<VideoFileAdapter.ViewHolder> {

    List<VideoFile> list;
    private Activity context;
    private ExplorerInterfaces interfaces;
    private File current;
    private DirectoryAdapter.OnFinishListListener listListener;

    public VideoFileAdapter(Activity context, File file, List<VideoFile> list, DirectoryAdapter.OnFinishListListener listListener) {
        this.context = context;
        this.list = list;
        this.interfaces = (ExplorerInterfaces) context;
        this.current = file;
        this.listListener = listListener;
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
                        PicassoCache.getPicassoInstance(context).load(list.get(holder.getAdapterPosition()).getThumbImage()).error(R.drawable.ic_block_r).resize(90, 100).centerInside().into(holder.img, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (FileUtil.isInSeen(list.get(holder.getAdapterPosition()).getEID())) {
                                    showAsSeen(holder);
                                }
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }
                });
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
        holder.iV_visto.setColorFilter(ThemeUtils.getAcentColor(context));
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
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (FileUtil.isInSeen(list.get(holder.getAdapterPosition()).getEID())) {
                    hideAsSeen(holder);
                    FileUtil.setSeenState(list.get(holder.getAdapterPosition()).getEID(), false);
                } else {
                    showAsSeen(holder);
                    FileUtil.setSeenState(list.get(holder.getAdapterPosition()).getEID(), true);
                }
                return false;
            }
        });
        holder.ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAsSeen(holder);
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
                                if (FileUtil.DeleteAnime(list.get(holder.getAdapterPosition()).getEID())) {
                                    if (list.size() - 1 < 1) {
                                        try {
                                            interfaces.OnDirectoryEmpty(list.get(0).getID());
                                        } catch (Exception e) {
                                        }
                                    }
                                    Toaster.toast("Archivo eliminado");
                                    notifyItemRemoved(holder.getAdapterPosition());
                                    recreateList();
                                } else {
                                    Toaster.toast("Error al eliminar");
                                    recreateList();
                                }
                            }
                        }).build().show();
            }
        });
    }

    private void showAsSeen(ViewHolder holder) {
        holder.img.setColorFilter(Color.argb(150, 138, 138, 138));
        holder.iV_visto.setVisibility(View.VISIBLE);
    }

    private void hideAsSeen(ViewHolder holder) {
        holder.img.setColorFilter(null);
        holder.iV_visto.setVisibility(View.GONE);
    }

    private void recreateList() {
        interfaces.OnDirectoryFileChange();
        ModelFactory.createVideosListAsync(current, new ModelFactory.AsyncFileListener() {
            @Override
            public void onCreated(List<VideoFile> l) {
                list = l;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
                listListener.onFinish(l.size());
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
        @Bind(R.id.seen)
        public ImageView iV_visto;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
