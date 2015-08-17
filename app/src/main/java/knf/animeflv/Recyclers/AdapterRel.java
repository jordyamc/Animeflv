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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.WebDescarga;

/**
 * Created by Jordy on 17/08/2015.
 */
public class AdapterRel extends RecyclerView.Adapter<AdapterRel.ViewHolder> {

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
    String[] url;

    public AdapterRel(Context context, List<String> titulos, List<String> tipos, String[] urls) {
        this.context = context;
        this.titulosCard = titulos;
        this.tiposCard = tipos;
        this.url = urls;
    }

    @Override
    public AdapterRel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_rel, parent, false);
        return new AdapterRel.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterRel.ViewHolder holder, final int position) {
        PicassoCache.getPicassoInstance(context).load(url[position]).error(R.drawable.ic_block_r).into(holder.iv_rel);
        holder.tv_tit.setText(titulosCard.get(position));
        holder.tv_tipo.setText(tiposCard.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Relacionados",Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return titulosCard.size();
    }

}