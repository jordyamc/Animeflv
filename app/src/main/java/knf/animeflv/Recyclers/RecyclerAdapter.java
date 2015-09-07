package knf.animeflv.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import knf.animeflv.R;
import knf.animeflv.WebDescarga;

/**
 * Created by Jordy on 08/08/2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_capitulo;
        public ImageButton ib_ver;
        public ImageButton ib_des;
        public RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_capitulo = (TextView) itemView.findViewById(R.id.tv_cardD_capitulo);
            this.ib_ver = (ImageButton) itemView.findViewById(R.id.ib_ver_rv);
            this.ib_des = (ImageButton) itemView.findViewById(R.id.ib_descargar_rv);
        }
    }
    private Context context;
    List<String> capitulo;

    public RecyclerAdapter(Context context, List<String> capitulos) {
        this.capitulo = capitulos;
        this.context = context;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_descarga, parent, false);
        return new RecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {
        holder.tv_capitulo.setText(capitulo.get(position));
        holder.ib_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1);
                SharedPreferences sharedPreferences=context.getSharedPreferences("data", Context.MODE_PRIVATE);
                String titulo=sharedPreferences.getString("titInfo","Error");
                String url=getUrl(titulo, item);
                Intent intent=new Intent(context,WebDescarga.class);
                Bundle bundle=new Bundle();
                bundle.putString("url",url);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }
    public String getUrl(String titulo,String capitulo){
        String ftitulo="";
        String atitulo=titulo.toLowerCase();
        atitulo=atitulo.replace("*","-");
        atitulo=atitulo.replace(":","");
        atitulo=atitulo.replace(",","");
        atitulo=atitulo.replace(" \u2606 ","-");
        atitulo=atitulo.replace("\u2606","-");
        atitulo=atitulo.replace("  ","-");
        atitulo=atitulo.replace("@","a");
        atitulo=atitulo.replace("/","-");
        atitulo=atitulo.replace(".","");
        for (int x=0; x < atitulo.length(); x++) {
            if (atitulo.charAt(x) != ' ') {
                ftitulo += atitulo.charAt(x);
            }else {
                if (atitulo.charAt(x) == ' ') {
                    ftitulo += "-";
                }
            }
        }
        ftitulo=ftitulo.replace("!!!","-3");
        ftitulo=ftitulo.replace("!", "");
        ftitulo=ftitulo.replace("°", "");
        ftitulo=ftitulo.replace("&deg;", "");
        ftitulo=ftitulo.replace("(","");
        ftitulo=ftitulo.replace(")","");
        if (ftitulo.trim().equals("gintama")){ftitulo=ftitulo+"-2015";}
        String link="http://animeflv.net/ver/"+ftitulo+"-"+capitulo+".html";
        return link;
    }

    @Override
    public int getItemCount() {
        return capitulo.size();
    }

}
