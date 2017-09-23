package knf.animeflv.TV.MainFiles;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Recientes.MainAnimeModel;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;

public class MainTvAdapter extends RecyclerView.Adapter<MainTvAdapter.ViewHolder> {

    private Activity context;
    private ThemeUtils.Theme theme;
    private List<MainAnimeModel> list;

    public MainTvAdapter(Activity context, List<MainAnimeModel> list) {
        this.context = context;
        this.theme = ThemeUtils.Theme.create(context);
        this.list = list;
    }

    @Override
    public MainTvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_tv_main, parent, false);
        return new MainTvAdapter.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final MainTvAdapter.ViewHolder holder, final int position) {
        holder.title.setText(list.get(holder.getAdapterPosition()).getTitulo());
        holder.chapter.setText("Capítulo " + list.get(holder.getAdapterPosition()).getNumero());
        holder.chapter.setTextColor(theme.accent);
        new CacheManager().portada(context, list.get(holder.getAdapterPosition()).getAid(), holder.img);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoHelper.open(context,
                        new Pair[]{
                                new Pair<View, String>(holder.img, "img"),
                                new Pair<View, String>(holder.title, "title"),
                                new Pair<View, String>(holder.chapter, "chapter"),
                                new Pair<View, String>(holder.cardView, "card")
                        },
                        new InfoHelper.BundleItem("aid", list.get(holder.getAdapterPosition()).getAid()),
                        new InfoHelper.BundleItem("title", list.get(holder.getAdapterPosition()).getTitulo()),
                        new InfoHelper.BundleItem("chapter", list.get(holder.getAdapterPosition()).getNumero()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        public TextView title;
        @BindView(R.id.chapter)
        public TextView chapter;
        @BindView(R.id.img)
        public ImageView img;
        @BindView(R.id.cardView)
        public CardView cardView;

        public ViewHolder(final View itemView, final Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setFocusable(true);
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Animation anim = AnimationUtils.loadAnimation(context, b ? R.anim.scale_in : R.anim.scale_out);
                    itemView.startAnimation(anim);
                    anim.setFillAfter(true);
                }
            });
        }
    }

}