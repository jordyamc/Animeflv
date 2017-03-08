package knf.animeflv.Explorer.Adapters;

import android.app.Activity;
import android.app.DownloadManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Callback;

import java.io.File;
import java.util.List;

import butterknife.BindView;
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

    private final int UPDATE_FREQUENCY = 1000;
    List<VideoFile> list;
    private Activity context;
    private ExplorerInterfaces interfaces;
    private File current;
    private Handler handler;
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
        try {
            new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                PicassoCache.getPicassoInstance(context).load(list.get(holder.getAdapterPosition()).getThumbImage()).error(R.drawable.ic_block_r).resize(90, 100).centerInside().into(holder.img, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        try {
                                            if (FileUtil.init(context).isInSeen(list.get(holder.getAdapterPosition()).getEID())) {
                                                showAsSeen(holder);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            } catch (Exception e) {
                            }
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
                    if (FileUtil.init(context).isInSeen(list.get(holder.getAdapterPosition()).getEID())) {
                        hideAsSeen(holder);
                        FileUtil.init(context).setSeenState(list.get(holder.getAdapterPosition()).getEID(), false);
                    } else {
                        showAsSeen(holder);
                        FileUtil.init(context).setSeenState(list.get(holder.getAdapterPosition()).getEID(), true);
                    }
                    return false;
                }
            });
            holder.img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PicassoCache.getPicassoInstance(context).load(list.get(holder.getAdapterPosition()).recreateThumbImage()).error(R.drawable.ic_block_r).resize(90, 100).centerInside().into(holder.img);
                        }
                    });
                    return true;
                }
            });
            holder.ver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAsSeen(holder);
                    try {
                        StreamManager.Play(context, list.get(holder.getAdapterPosition()).getEID());
                    } catch (Exception e) {
                        Toaster.toast("Error al reproducir");
                    }
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
                                    if (FileUtil.init(context).DeleteAnime(list.get(holder.getAdapterPosition()).getEID())) {
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.progress.setVisibility(View.GONE);
                                            }
                                        });
                                        if (list.size() - 1 < 1) {
                                            FileUtil.init(context).DeleteAnimeDir(list.get(holder.getAdapterPosition()).getID());
                                            list.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            try {
                                                interfaces.OnDirectoryEmpty(list.get(0).getID());
                                            } catch (Exception e) {
                                            }
                                        } else {
                                            Toaster.toast("Archivo eliminado");
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            recreateList();
                                        }
                                    } else {
                                        Toaster.toast("Error al eliminar");
                                        recreateList();
                                    }
                                }
                            }).build().show();
                }
            });
            if (ManageDownload.isDownloading(context, list.get(holder.getAdapterPosition()).getEID())) {
                holder.progress.setMax(100);
                getProgressHandler().postDelayed(getSyncroRunnable(holder), 1);
            }
        } catch (Exception e) {
        }
    }

    private Handler getProgressHandler() {
        if (handler != null) return handler;
        handler = new Handler();
        return handler;
    }

    private Runnable getSyncroRunnable(final ViewHolder holder) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                switch (ManageDownload.getDownloadSate(context, list.get(holder.getAdapterPosition()).getEID())) {
                                    case DownloadManager.STATUS_RUNNING:
                                        final int progress = ManageDownload.getProgress(context, list.get(holder.getAdapterPosition()).getEID());
                                        if (progress != -1) {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.progress.setIndeterminate(false);
                                                    holder.progress.setVisibility(View.VISIBLE);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                        holder.progress.setProgress(progress, true);
                                                    } else {
                                                        holder.progress.setProgress(progress);
                                                    }
                                                }
                                            });
                                        } else {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.progress.setIndeterminate(true);
                                                }
                                            });
                                        }
                                        getProgressHandler().postDelayed(getSyncroRunnable(holder), UPDATE_FREQUENCY);
                                        break;
                                    case DownloadManager.STATUS_FAILED:
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.progress.setVisibility(View.GONE);
                                            }
                                        });
                                        FileUtil.init(context).DeleteAnime(list.get(holder.getAdapterPosition()).getEID());
                                        list.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        recreateList();
                                        break;
                                    case DownloadManager.STATUS_SUCCESSFUL:
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.progress.setVisibility(View.GONE);
                                            }
                                        });
                                        break;
                                    default:
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.progress.setIndeterminate(true);
                                            }
                                        });
                                        getProgressHandler().postDelayed(getSyncroRunnable(holder), UPDATE_FREQUENCY);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.executeOnExecutor(ExecutorManager.getExecutor());
                } catch (Exception e) {
                    Log.e("Async Error", list.get(holder.getAdapterPosition()).getEID(), e);
                }
            }
        };
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

    public void performDestroy() {
        getProgressHandler().removeCallbacksAndMessages(null);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.root)
        public RelativeLayout root;
        @BindView(R.id.img)
        public ImageView img;
        @BindView(R.id.titulo)
        public TextView titulo;
        @BindView(R.id.cap)
        public TextView cap;
        @BindView(R.id.ib_ver)
        public ImageButton ver;
        @BindView(R.id.ib_del)
        public ImageButton del;
        @BindView(R.id.seen)
        public ImageView iV_visto;
        @BindView(R.id.progress)
        ProgressBar progress;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
