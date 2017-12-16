package knf.animeflv.Explorer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.Explorer.ExplorerInterfaces;
import knf.animeflv.Explorer.Models.Directory;
import knf.animeflv.Explorer.Models.ModelFactory;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 22/08/2015.
 */
public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> implements SectionTitleProvider {

    List<Directory> list;
    private Activity context;
    private ExplorerInterfaces interfaces;
    private ThemeUtils.Theme theme;

    public DirectoryAdapter(Activity context, List<Directory> list) {
        this.context = context;
        this.list = list;
        this.interfaces = (ExplorerInterfaces) context;
        this.theme = ThemeUtils.Theme.create(context);
    }

    @Override
    public DirectoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.explorer_item, parent, false);
        return new DirectoryAdapter.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final DirectoryAdapter.ViewHolder holder, final int position) {
        if (list.get(Gposition(holder, position)).getFilesNumber().startsWith("0")) {
            holder.root.setVisibility(View.GONE);
        }
        CacheManager.mini(context, list.get(Gposition(holder, position)).getID(), holder.img);
        holder.iV_visto.setVisibility(View.GONE);
        holder.titulo.setText(list.get(Gposition(holder, position)).getTitle());
        String caps = list.get(Gposition(holder, position)).getFilesNumber();
        if (caps.startsWith("1 ")) {
            caps = caps.replace("archivos", "archivo");
        }
        holder.cap.setText(caps);
        holder.ver.setVisibility(View.GONE);
        holder.cap.setTextColor(theme.accent);
        holder.titulo.setTextColor(theme.textColor);
        holder.del.setColorFilter(theme.iconFilter);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaces.OnDirectoryClicked(list.get(Gposition(holder, position)).getFile(context), list.get(Gposition(holder, position)).getTitle());
            }
        });
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .content("Esta seguro que desea eliminar todos los capitulos descargados de " + list.get(Gposition(holder, position)).getTitle() + "?")
                        .positiveText("Eliminar")
                        .negativeText("cancelar")
                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                new AsyncTask<Void, Void, Void>() {
                                    private MaterialDialog dialogProg;
                                    private int progress = 0;
                                    private boolean empty = false;

                                    @Override
                                    protected void onPreExecute() {
                                        try {
                                            int length = list.get(Gposition(holder, position)).getFile(context).list().length;
                                            dialogProg = new MaterialDialog.Builder(context)
                                                    .content("Eliminando...")
                                                    .progress(false, length, true)
                                                    .cancelable(false)
                                                    .build();
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                    dialogProg.show();
                                                    dialogProg.setProgress(progress);
                                                }
                                            });
                                        } catch (Exception e) {
                                            empty = true;
                                        }
                                        super.onPreExecute();
                                    }

                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        if (empty)
                                            return null;
                                        try {
                                            for (String file : list.get(Gposition(holder, position)).getFile(context).list()) {
                                                ManageDownload.cancel(context, file.replace(".mp4", "E"));
                                                FileUtil.init(context).DeleteAnime(file.replace(".mp4", "E"));
                                                progress++;
                                                context.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialogProg.setProgress(progress);
                                                    }
                                                });
                                            }
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        dialogProg.dismiss();
                                                        if (list.get(Gposition(holder, position)).getFile(context).list().length == 0) {
                                                            FileUtil.init(context).DeleteAnimeDir(list.get(Gposition(holder, position)).getID());
                                                            list.remove(Gposition(holder, position));
                                                            notifyItemRemoved(Gposition(holder, position));
                                                            Toaster.toast("Archivos eliminados");
                                                            recreateList();
                                                        } else {
                                                            Toaster.toast("Error al eliminar");
                                                            recreateList();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Toaster.toast("Error al eliminar");
                                                        recreateList();
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialogProg.dismiss();
                                                }
                                            });
                                            Toaster.toast("Error al eliminar");
                                            recreateList();
                                        }
                                        return null;
                                    }
                                }.executeOnExecutor(ExecutorManager.getExecutor());
                            }
                        }).build().show();
            }
        });
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                InfoHelper.open(
                        context,
                        new InfoHelper.SharedItem(holder.img, "img"),
                        new InfoHelper.BundleItem("aid", list.get(Gposition(holder, position)).getID()),
                        new InfoHelper.BundleItem("title", list.get(Gposition(holder, position)).getTitle())
                );
                return true;
            }
        });
    }

    private int Gposition(ViewHolder holder, int position) {
        return holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition();
    }

    private void recreateList() {
        ModelFactory.createDirectoryListAsync(context, new ModelFactory.AsyncDirectoryListener() {
            @Override
            public void onCreated(List<Directory> l) {
                list = l;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void recreateList(Activity activity, final OnFinishListListener listener) {
        context = activity;
        ModelFactory.createDirectoryListAsync(context, new ModelFactory.AsyncDirectoryListener() {
            @Override
            public void onCreated(List<Directory> l) {
                list = l;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
                listener.onFinish(l.size());
            }
        });
    }

    @Override
    public String getSectionTitle(int i) {
        return "";
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnFinishListListener {
        void onFinish(int count);
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

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setCropToPadding(true);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                img.setPadding(0, 0, 0, 0);
        }
    }
}
