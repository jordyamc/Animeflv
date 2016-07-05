package knf.animeflv.Explorer.Adapters;

import android.app.Activity;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.Explorer.ExplorerInterfaces;
import knf.animeflv.Explorer.ExplorerRoot;
import knf.animeflv.Explorer.Models.Directory;
import knf.animeflv.Explorer.Models.ModelFactory;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 22/08/2015.
 */
public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    List<Directory> list;
    private Activity context;
    private ExplorerInterfaces interfaces;

    public DirectoryAdapter(Activity context) {
        this.context = context;
        this.list = ModelFactory.createDirectoryList(context);
        this.interfaces = (ExplorerInterfaces) context;
    }

    @Override
    public DirectoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.explorer_item, parent, false);
        return new DirectoryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DirectoryAdapter.ViewHolder holder, final int position) {
        //PicassoCache.getPicassoInstance(context).load(Uri.parse(list.get(holder.getAdapterPosition()).getImageUrl(context))).error(R.drawable.ic_block_r).into(holder.img);
        new CacheManager().mini(context,list.get(holder.getAdapterPosition()).getID(),holder.img);
        holder.titulo.setText(list.get(holder.getAdapterPosition()).getTitle());
        String caps = list.get(holder.getAdapterPosition()).getFilesNumber();
        if (caps.startsWith("1 ")) {
            caps = caps.replace("archivos", "archivo");
        }
        holder.cap.setText(caps);
        holder.ver.setVisibility(View.GONE);
        holder.cap.setTextColor(ThemeUtils.getAcentColor(context));
        if (ThemeUtils.isAmoled(context)) {
            holder.titulo.setTextColor(ColorsRes.Holo_Dark(context));
            holder.del.setColorFilter(ColorsRes.Holo_Dark(context));
        } else {
            holder.titulo.setTextColor(ColorsRes.Holo_Light(context));
            holder.del.setColorFilter(ColorsRes.Holo_Light(context));
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaces.OnDirectoryClicked(list.get(holder.getAdapterPosition()).getFile(), list.get(holder.getAdapterPosition()).getTitle());
            }
        });
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .content("Esta seguro que desea eliminar todos los capitulos descargados de " + list.get(holder.getAdapterPosition()).getTitle() + "?")
                        .positiveText("Eliminar")
                        .negativeText("cancelar")
                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                for (String file : list.get(holder.getAdapterPosition()).getFile().list()) {
                                    ManageDownload.cancel(context, file.replace(".mp4", "E"));
                                    FileUtil.DeleteAnime(file.replace(".mp4", "E"));
                                }
                                if (list.get(holder.getAdapterPosition()).getFile().list().length == 0) {
                                    Toaster.toast("Archivos eliminados");
                                    list = ModelFactory.createDirectoryList(context);
                                    notifyDataSetChanged();
                                } else {
                                    Toaster.toast("Error al eliminar");
                                    list = ModelFactory.createDirectoryList(context);
                                    notifyDataSetChanged();
                                }
                            }
                        }).build().show();
            }
        });
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                InfoHelper.open(
                        ((ExplorerRoot) context),
                        new InfoHelper.SharedItem(holder.img, "img"),
                        new InfoHelper.BundleItem("aid", list.get(holder.getAdapterPosition()).getID()),
                        new InfoHelper.BundleItem("title", list.get(holder.getAdapterPosition()).getTitle())
                );
                return true;
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
