package knf.animeflv.Recyclers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import knf.animeflv.ColorsRes;
import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.Directorio;
import knf.animeflv.Parser;
import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;

/**
 * Created by Jordy on 17/08/2015.
 */
public class AdapterBusquedaNew extends RecyclerView.Adapter<AdapterBusquedaNew.ViewHolder> {

    List<AnimeClass> Animes;
    private Context context;

    public AdapterBusquedaNew(Context context, List<AnimeClass> animes) {
        this.context = context;
        this.Animes = animes;
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
    public AdapterBusquedaNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_rel, parent, false);
        return new AdapterBusquedaNew.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterBusquedaNew.ViewHolder holder, final int position) {
        restartViews(holder);
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.prim));
            holder.tv_tit.setTextColor(context.getResources().getColor(R.color.blanco));
            holder.tv_noC.setTextColor(context.getResources().getColor(R.color.blanco));
        }
        holder.tv_tipo.setTextColor(ThemeUtils.getAcentColor(context));
        PicassoCache.getPicassoInstance(context).load(new Parser().getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + Animes.get(holder.getAdapterPosition()).getImagen()).error(R.drawable.ic_block_r).into(holder.iv_rel);
        holder.tv_tit.setText(Animes.get(holder.getAdapterPosition()).getNombre());
        holder.tv_tipo.setText(Animes.get(holder.getAdapterPosition()).getTipo());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Animes.get(holder.getAdapterPosition()).getTipo().equals("none")) {
                    String file = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
                    String json = getStringFromFile(file);
                    SharedPreferences.Editor sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                    sharedPreferences.putString("aid", Animes.get(holder.getAdapterPosition()).getAid()).apply();
                    InfoHelper.open(
                            ((Directorio) context),
                            new InfoHelper.SharedItem(holder.iv_rel, "img"),
                            Intent.FLAG_ACTIVITY_NEW_TASK,
                            new InfoHelper.BundleItem("aid", Animes.get(holder.getAdapterPosition()).getAid()),
                            new InfoHelper.BundleItem("title", Animes.get(holder.getAdapterPosition()).getNombre()),
                            new InfoHelper.BundleItem("link", new Parser().getUrlFavs(json, Animes.get(holder.getAdapterPosition()).getAid()))
                    );
                }
            }
        });
    }

    public void restartViews(AdapterBusquedaNew.ViewHolder holder) {
        if (Animes.get(holder.getAdapterPosition()).getTipo().equals("none")) {
            holder.tv_noC.setText("Sin Coincidencias");
        }
        if (Animes.get(holder.getAdapterPosition()).getTipo().equals("_aid_")) {
            holder.tv_noC.setText("Escribe un ID");
        }
        if (Animes.get(holder.getAdapterPosition()).getTipo().equals("_NoNum_")) {
            holder.tv_noC.setText(Animes.get(holder.getAdapterPosition()).getNombre() + " no es un ID");
        }
        String tipo = Animes.get(holder.getAdapterPosition()).getTipo();
        if (tipo.equals("none") || tipo.equals("_aid_") || tipo.equals("_NoNum_")) {
            holder.iv_rel.setVisibility(View.GONE);
            holder.tv_tipo.setVisibility(View.GONE);
            holder.tv_tit.setVisibility(View.GONE);
            holder.tv_noC.setVisibility(View.VISIBLE);
        } else {
            holder.iv_rel.setVisibility(View.VISIBLE);
            holder.tv_tipo.setVisibility(View.VISIBLE);
            holder.tv_tit.setVisibility(View.VISIBLE);
            holder.tv_noC.setVisibility(View.GONE);
        }
    }

    private int getColor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(context));
        int color = ColorsRes.Naranja(context);
        if (accent == ColorsRes.Rojo(context)) {
            color = ColorsRes.Rojo(context);
        }
        if (accent == ColorsRes.Naranja(context)) {
            color = ColorsRes.Naranja(context);
        }
        if (accent == ColorsRes.Gris(context)) {
            color = ColorsRes.Gris(context);
        }
        if (accent == ColorsRes.Verde(context)) {
            color = ColorsRes.Verde(context);
        }
        if (accent == ColorsRes.Rosa(context)) {
            color = ColorsRes.Rosa(context);
        }
        if (accent == ColorsRes.Morado(context)) {
            color = ColorsRes.Morado(context);
        }
        return color;
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
        public TextView tv_tipo;
        public TextView tv_noC;
        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            this.iv_rel = (ImageView) itemView.findViewById(R.id.imgCardInfoRel);
            this.tv_tit = (TextView) itemView.findViewById(R.id.tv_info_rel_tit);
            this.tv_tipo = (TextView) itemView.findViewById(R.id.tv_info_rel_tipo);
            this.tv_noC = (TextView) itemView.findViewById(R.id.tv_b_noC);
            this.card = (CardView) itemView.findViewById(R.id.cardRel);
        }
    }

}