package knf.animeflv.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.info.RelActInfo;

/**
 * Created by Jordy on 17/08/2015.
 */
public class AdapterBusqueda extends RecyclerView.Adapter<AdapterBusqueda.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_rel;
        public TextView tv_tit;
        public TextView tv_tipo;
        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            this.iv_rel = (ImageView) itemView.findViewById(R.id.imgCardInfoRel);
            this.tv_tit = (TextView) itemView.findViewById(R.id.tv_info_rel_tit);
            this.tv_tipo = (TextView) itemView.findViewById(R.id.tv_info_rel_tipo);
            this.card = (CardView) itemView.findViewById(R.id.cardRel);
        }
    }
    private Context context;
    List<String> titulosCard;
    List<String> tiposCard;
    List<String> url;
    List<String> aids;

    public AdapterBusqueda(Context context, List<String> titulos, List<String> tipos, List<String> urls, List<String> aid) {
        this.context = context;
        this.titulosCard = titulos;
        this.tiposCard = tipos;
        this.url = urls;
        this.aids=aid;
    }

    @Override
    public AdapterBusqueda.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_rel, parent, false);
        return new AdapterBusqueda.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterBusqueda.ViewHolder holder, final int position) {
        PicassoCache.getPicassoInstance(context).load(url.get(position)).error(R.drawable.ic_block_r).into(holder.iv_rel);
        holder.tv_tit.setText(titulosCard.get(position));
        holder.tv_tipo.setText(tiposCard.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("aid", aids.get(position));
                Intent intent=new Intent(context,RelActInfo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                SharedPreferences.Editor sharedPreferences=context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
                sharedPreferences.putString("aid",aids.get(position)).commit();
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return titulosCard.size();
    }

}