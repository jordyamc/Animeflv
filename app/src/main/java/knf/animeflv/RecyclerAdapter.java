package knf.animeflv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jordy on 08/08/2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView tv_titulo;
        public TextView tv_capitulo;

        public ViewHolder(View itemView) {
            super(itemView);
            this.img = (ImageView) itemView.findViewById(R.id.imgCardD1);
            this.tv_titulo = (TextView) itemView.findViewById(R.id.tv_cardD_titulo);
            this.tv_capitulo = (TextView) itemView.findViewById(R.id.tv_cardD_capitulo);
        }
    }
    private ArrayList<AnimeCardDescarga> anime;
    private Context context;

    public RecyclerAdapter(Context context, ArrayList<AnimeCardDescarga> anime) {
        this.anime = anime;
        this.context = context;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.anime_inicio, parent, false);
        return new RecyclerAdapter.ViewHolder(itemView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        AnimeCardDescarga animeC = anime.get(position);
        holder.img.setImageBitmap(animeC.img);
        holder.tv_titulo.setText(recnombre(animeC.nombre));
        holder.tv_capitulo.setText("Capitulo "+animeC.Ncapitulo);
    }

    public String recnombre(String nombre){
        String onombre=nombre;
        String rnombre;
        if (nombre.length()>15){
            rnombre=onombre.substring(0,15);
        }else {
            rnombre=onombre;
        }
        return rnombre;
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return anime.size();
    }
}
