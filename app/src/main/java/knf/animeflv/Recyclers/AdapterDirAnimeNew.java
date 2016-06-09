package knf.animeflv.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.Directorio;
import knf.animeflv.Parser;
import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.info.Helper.InfoHelper;

/**
 * Created by Jordy on 22/08/2015.
 */
public class AdapterDirAnimeNew extends RecyclerView.Adapter<AdapterDirAnimeNew.ViewHolder> {

    List<AnimeClass> Animes;
    private Context context;
    public AdapterDirAnimeNew(Context context, List<AnimeClass> animes) {
        this.context = context;
        this.Animes = animes;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    @Override
    public AdapterDirAnimeNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_fav, parent, false);
        return new AdapterDirAnimeNew.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterDirAnimeNew.ViewHolder holder, final int position) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.prim));
            holder.tv_tit.setTextColor(context.getResources().getColor(R.color.blanco));
        }
        PicassoCache.getPicassoInstance(context).load(new Parser().getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + Animes.get(holder.getAdapterPosition()).getImagen()).error(R.drawable.ic_block_r).into(holder.iv_rel);
        holder.tv_tit.setText(Animes.get(holder.getAdapterPosition()).getNombre());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoHelper.open(
                        ((Directorio) context),
                        new InfoHelper.SharedItem(holder.iv_rel, "img"),
                        Intent.FLAG_ACTIVITY_NEW_TASK,
                        new InfoHelper.BundleItem("aid", Animes.get(holder.getAdapterPosition()).getAid()),
                        new InfoHelper.BundleItem("title", Animes.get(holder.getAdapterPosition()).getNombre()),
                        new InfoHelper.BundleItem("link", new Parser().getUrlAnimeCached(Animes.get(holder.getAdapterPosition()).getAid()))
                );
            }
        });
    }

    private String getCertificateSHA1Fingerprint() {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    @Override
    public int getItemCount() {
        return Animes.size();
    }

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
}
