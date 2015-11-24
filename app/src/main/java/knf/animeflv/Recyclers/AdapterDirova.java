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

import knf.animeflv.Parser;
import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.info.RelActInfo;

/**
 * Created by Jordy on 22/08/2015.
 */
public class AdapterDirova extends RecyclerView.Adapter<AdapterDirova.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_rel;
        public TextView tv_tit;
        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            this.iv_rel = (ImageView) itemView.findViewById(R.id.imgCardInfoRel);
            this.tv_tit = (TextView) itemView.findViewById(R.id.tv_info_rel_tit);
            this.card = (CardView) itemView.findViewById(R.id.cardRel);
        }
    }

    private Context context;
    List<String> titulosCard;
    List<String> aids;
    List<String> links;
    String j;

    public AdapterDirova(Context context, List<String> titulos, List<String> aid, List<String> aidlinks, String json) {
        this.context = context;
        this.titulosCard = titulos;
        this.aids = aid;
        this.links = aidlinks;
        this.j = json;
    }

    @Override
    public AdapterDirova.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_fav, parent, false);
        return new AdapterDirova.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterDirova.ViewHolder holder, final int position) {
        PicassoCache.getPicassoInstance(context).load(links.get(position)).error(R.drawable.ic_block_r).into(holder.iv_rel);
        holder.tv_tit.setText(titulosCard.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("aid", aids.get(position));
                bundle.putString("link", new Parser().getUrlOva(j, aids.get(position)));
                Intent intent = new Intent(context, RelActInfo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                SharedPreferences.Editor sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                sharedPreferences.putString("aid", aids.get(position)).commit();
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return titulosCard.size();
    }
}
