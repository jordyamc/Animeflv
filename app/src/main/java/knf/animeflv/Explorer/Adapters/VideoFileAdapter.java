package knf.animeflv.Explorer.Adapters;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Callback;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.munix.multidisplaycast.CastControlsActivity;
import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.Explorer.ExplorerInterfaces;
import knf.animeflv.Explorer.Models.ModelFactory;
import knf.animeflv.Explorer.Models.VideoFile;
import knf.animeflv.PicassoCache;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.R;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class VideoFileAdapter extends RecyclerView.Adapter<VideoFileAdapter.ViewHolder> implements SectionTitleProvider {

    private final int UPDATE_FREQUENCY = 1000;
    List<VideoFile> list;
    private Activity context;
    private ExplorerInterfaces interfaces;
    private File current;
    private Handler handler;
    private DirectoryAdapter.OnFinishListListener listListener;
    private ThemeUtils.Theme theme;
    private boolean castMode;

    public VideoFileAdapter(Activity context, File file, List<VideoFile> list, boolean castMode, DirectoryAdapter.OnFinishListListener listListener) {
        this.context = context;
        this.list = list;
        this.interfaces = (ExplorerInterfaces) context;
        this.current = file;
        this.castMode = castMode;
        this.listListener = listListener;
        this.theme = ThemeUtils.Theme.create(context);
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
            holder.cap.setTextColor(theme.accent);
            holder.titulo.setTextColor(theme.textColor);
            holder.ver.setColorFilter(theme.iconFilter);
            holder.del.setColorFilter(theme.iconFilter);
            if (castMode)
                holder.ver.setImageDrawable(getCastDrawable(isCasting(list.get(holder.getAdapterPosition()).getEID())));
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                holder.progress.getProgressDrawable().setColorFilter(theme.accent, PorterDuff.Mode.SRC_ATOP);
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
                    if (isCasting(list.get(holder.getAdapterPosition()).getEID())) {
                        context.startActivity(new Intent(context, CastControlsActivity.class));
                    } else if (castMode) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.ver.setImageDrawable(getCastDrawable(true));
                            }
                        });
                        interfaces.OnCastFile(list.get(holder.getAdapterPosition()).getFile(), list.get(holder.getAdapterPosition()).getEID());
                        notifyData();
                    } else {
                        try {
                            StreamManager.Play(context, list.get(holder.getAdapterPosition()).getEID());
                        } catch (Exception e) {
                            Toaster.toast("Error al reproducir");
                        }
                    }
                }
            });
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
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
                                                try {
                                                    interfaces.OnDirectoryEmpty(list.get(0).getID());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                list.remove(holder.getAdapterPosition());
                                                notifyItemRemoved(holder.getAdapterPosition());
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (ManageDownload.isDownloading(context, list.get(holder.getAdapterPosition()).getEID())) {
                holder.progress.setMax(100);
                getProgressHandler().postDelayed(getSyncroRunnable(holder), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyData() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public String getSectionTitle(int i) {
        return "";
    }

    public void setMode(boolean isCastMode) {
        this.castMode = isCastMode;
    }

    private Handler getProgressHandler() {
        if (handler == null) handler = new Handler();
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
        ModelFactory.createVideosListAsync(context, current, new ModelFactory.AsyncFileListener() {
            @Override
            public void onCreated(List<VideoFile> l) {
                list = l;
                notifyData();
                listListener.onFinish(l.size());
            }
        });
    }

    private boolean isCasting(String eid) {
        return CastPlayBackManager.get(context).isCasting(eid);
    }

    private Drawable getCastDrawable(boolean isCasting) {
        return new IconicsDrawable(context)
                .icon(isCasting ? CommunityMaterial.Icon.cmd_cast_connected : CommunityMaterial.Icon.cmd_cast)
                .color(theme.iconFilter)
                .paddingDp(3)
                .sizeDp(24);
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
