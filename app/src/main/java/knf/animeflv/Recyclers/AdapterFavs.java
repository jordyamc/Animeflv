package knf.animeflv.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import knf.animeflv.Parser;
import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.info.RelActInfo;

/**
 * Created by Jordy on 22/08/2015.
 */
public class AdapterFavs extends RecyclerView.Adapter<AdapterFavs.ViewHolder> {

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

    public AdapterFavs(Context context, List<String> titulos, List<String> aid, List<String> aidlinks) {
        this.context = context;
        this.titulosCard = titulos;
        this.aids=aid;
        this.links=aidlinks;
    }

    @Override
    public AdapterFavs.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_fav, parent, false);
        return new AdapterFavs.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterFavs.ViewHolder holder, final int position) {
        PicassoCache.getPicassoInstance(context).load(links.get(position)).error(R.drawable.ic_block_r).into(holder.iv_rel);
        holder.tv_tit.setText(titulosCard.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aids.get(position) + ".txt";
                String json = getStringFromFile(file);
                Bundle bundle=new Bundle();
                bundle.putString("aid", aids.get(position));
                File directorio = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
                if (directorio.exists()) {
                    bundle.putString("link", new Parser().getUrlFavs(getStringFromFile(directorio.getPath()), aids.get(position)));
                } else {
                    bundle.putString("link", getUrlInfo(titulosCard.get(position), new Parser().getTipoAnime(json)));
                }
                Intent intent=new Intent(context,RelActInfo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                SharedPreferences.Editor sharedPreferences=context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
                sharedPreferences.putString("aid",aids.get(position)).commit();
                context.startActivity(intent);
            }
        });
    }

    public String getUrlInfo(String titulo, String tipo) {
        String ftitulo = "";
        String atitulo = titulo.toLowerCase();
        atitulo = atitulo.replace("*", "-");
        atitulo = atitulo.replace(":", "");
        atitulo = atitulo.replace(",", "");
        atitulo = atitulo.replace(" \u2606 ", "-");
        atitulo = atitulo.replace("\u2606", "-");
        atitulo = atitulo.replace("  ", "-");
        atitulo = atitulo.replace("@", "a");
        atitulo = atitulo.replace("&", "-");
        atitulo = atitulo.replace("/", "-");
        atitulo = atitulo.replace(".", "");
        atitulo = atitulo.replace("\"", "");
        atitulo = atitulo.replace("♥", "-");
        for (int x = 0; x < atitulo.length(); x++) {
            if (atitulo.charAt(x) != ' ') {
                ftitulo += atitulo.charAt(x);
            } else {
                if (atitulo.charAt(x) == ' ') {
                    ftitulo += "-";
                }
            }
        }
        ftitulo = ftitulo.replace("!!!", "-3");
        ftitulo = ftitulo.replace("!", "");
        ftitulo = ftitulo.replace("°", "");
        ftitulo = ftitulo.replace("&deg;", "");
        ftitulo = ftitulo.replace("(", "");
        ftitulo = ftitulo.replace(")", "");
        ftitulo = ftitulo.replace("2nd-season", "2");
        ftitulo = ftitulo.replace("'", "");
        if (ftitulo.trim().equals("gintama")) {
            ftitulo = ftitulo + "-2015";
        }
        if (ftitulo.trim().equals("miss-monochrome-the-animation-2")) {
            ftitulo = "miss-monochrome-the-animation-2nd-season";
        }
        if (ftitulo.trim().equals("ore-ga-ojousama-gakkou-ni-shomin-sample-toshite-gets-sareta-ken")) {
            ftitulo = "ore-ga-ojousama-gakkou-ni-shomin-sample-toshite-gets-sareta-";
        }
        if (ftitulo.trim().equals("diabolik-lovers-moreblood")) {
            ftitulo = "diabolik-lovers-more-blood";
        }
        String link = "http://animeflv.net/" + tipo.toLowerCase() + "/" + ftitulo + ".html";
        return link;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) {
        String ret = "";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return ret;
    }
    @Override
    public int getItemCount() {
        return titulosCard.size();
    }
}
