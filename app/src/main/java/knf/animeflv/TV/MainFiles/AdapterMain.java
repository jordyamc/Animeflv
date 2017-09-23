package knf.animeflv.TV.MainFiles;

import android.app.Activity;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Recientes.MainAnimeModel;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ThemeUtils;


public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewHolder> {

    private Activity context;
    private List<MainAnimeModel> Animes = new ArrayList<>();
    private ThemeUtils.Theme theme;

    public AdapterMain(Activity context, List<MainAnimeModel> data) {
        this.context = context;
        this.Animes = data;
        this.theme = ThemeUtils.Theme.create(context);
    }

    @Override
    public AdapterMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("ImageCardView", "Create view");
        ImageCardView imageCardView = new ImageCardView(parent.getContext());
        imageCardView.setFocusable(true);
        imageCardView.setId(R.id.card);
        imageCardView.setFocusableInTouchMode(true);
        return new AdapterMain.ViewHolder(imageCardView);
    }

    @Override
    public void onBindViewHolder(final AdapterMain.ViewHolder holder, final int position) {
        new CacheManager().portada(context, Animes.get(getP(holder, position)).getAid(), holder.cardView.getMainImageView());
        holder.cardView.setTitleText(Animes.get(getP(holder, position)).getTitulo());
        holder.cardView.setContentText(Animes.get(getP(holder, position)).getNumero());

    }

    private int getP(AdapterMain.ViewHolder holder, int position) {
        return holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return Animes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card)
        ImageCardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}