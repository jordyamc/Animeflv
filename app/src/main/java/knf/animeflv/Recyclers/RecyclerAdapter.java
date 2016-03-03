package knf.animeflv.Recyclers;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import knf.animeflv.DManager;
import knf.animeflv.Downloader;
import knf.animeflv.LoginServer;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.TaskType;

/**
 * Created by Jordy on 08/08/2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_capitulo;
        public ImageButton ib_ver;
        public ImageButton ib_des;
        public CardView card;
        public RecyclerView recyclerView;
        public WebView web;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_capitulo = (TextView) itemView.findViewById(R.id.tv_cardD_capitulo);
            this.ib_ver = (ImageButton) itemView.findViewById(R.id.ib_ver_rv);
            this.ib_des = (ImageButton) itemView.findViewById(R.id.ib_descargar_rv);
            this.card = (CardView) itemView.findViewById(R.id.card_descargas_info);
            this.web = (WebView) itemView.findViewById(R.id.wv_anime_zippy);
        }
    }
    private Context context;
    List<String> capitulo;
    String id;
    List<String> eids;
    String ext_storage_state = Environment.getExternalStorageState();
    Parser parser = new Parser();
    MaterialDialog dialog;
    MaterialDialog d;
    Boolean streaming = false;
    int posT;

    public RecyclerAdapter(Context context, List<String> capitulos,String aid,List<String> eid) {
        this.capitulo = capitulos;
        this.context = context;
        this.id=aid;
        this.eids=eid;
    }

    public String getTit() {
        return context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titInfo", "");
    }

    public String getNum(int position) {
        return capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1).trim();
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_descarga, parent, false);
        return new RecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, final int position) {
        SetUpWeb(holder.web, holder);
        final String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1).trim();
        final File file=new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/"+id+"/"+id+"_"+item+".mp4");
        final File sd=new File(getSD1() + "/Animeflv/download/"+id+"/"+id+"_"+item+".mp4");
        final String email_coded=PreferenceManager.getDefaultSharedPreferences(context).getString("login_email_coded", "null");
        final String pass_coded=PreferenceManager.getDefaultSharedPreferences(context).getString("login_pass_coded", "null");
        if (file.exists()||sd.exists()){
            holder.ib_des.setImageResource(R.drawable.ic_borrar_r);
        }else {
            holder.ib_ver.setImageResource(R.drawable.ic_ver_no);
            holder.ib_ver.setEnabled(false);
        }
        holder.tv_capitulo.setText(capitulo.get(position));
        Boolean vistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getBoolean("visto"+id + "_" + item, false);
        holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.black));
        if (vistos){
            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
        }
        holder.ib_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!file.exists()&&!sd.exists()) {
                    if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("streaming", false)) {
                        new CheckDown(holder.web, holder.ib_des, holder.ib_ver, holder.tv_capitulo, position).execute(new Parser().getUrlCached(id, item));
                    } else {
                        dialog = new MaterialDialog.Builder(context)
                                .title("Descargar?")
                                .titleGravity(GravityEnum.CENTER)
                                .content("Desea descargar el capitulo?")
                                .autoDismiss(false)
                                .cancelable(true)
                                .positiveText("DESCARGAR")
                                .negativeText("STREAMING")
                                .neutralText("ATRAS")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                        materialDialog.dismiss();
                                        new CheckDown(holder.web, holder.ib_des, holder.ib_ver, holder.tv_capitulo, position).execute(new Parser().getUrlCached(id, item));
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                        materialDialog.dismiss();
                                        new CheckStream(holder.web, holder.tv_capitulo, position, holder).execute(new Parser().getUrlCached(id, item));
                                    }
                                })
                                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .build();
                        dialog.show();
                    }
                } else {
                    final String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1).trim();
                    MaterialDialog borrar = new MaterialDialog.Builder(context)
                            .title("Eliminar")
                            .titleGravity(GravityEnum.CENTER)
                            .content("Desea eliminar el capitulo " + item + "?")
                            .positiveText("Eliminar")
                            .negativeText("Cancelar")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    if (file.delete()) {
                                        holder.ib_des.setImageResource(R.drawable.ic_get_r);
                                        holder.ib_ver.setImageResource(R.drawable.ic_ver_no);
                                        long l = Long.parseLong(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(eids.get(position), "0"));
                                        if (l != 0) {
                                            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                            manager.remove(l);
                                            String descargados = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eids.get(position) + ":::", "")).apply();
                                            String tits = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                                            String epID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits.replace(id + ":::", "")).apply();
                                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(id + "_" + item + ":::", "")).apply();
                                        }
                                        Toast.makeText(context, "Archivo Eliminado", Toast.LENGTH_SHORT).show();
                                    }
                                    if (sd.delete()) {
                                        holder.ib_des.setImageResource(R.drawable.ic_get_r);
                                        holder.ib_ver.setImageResource(R.drawable.ic_ver_no);
                                        long l = Long.parseLong(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(eids.get(position), "0"));
                                        if (l != 0) {
                                            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                            manager.remove(l);
                                            ThinDownloadManager downloadManager = DManager.getManager();
                                            downloadManager.cancel((int) l);
                                            String descargados = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eids.get(position) + ":::", "")).apply();
                                            String tits = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                                            String epID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits.replace(id + ":::", "")).apply();
                                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(id + "_" + item + ":::", "")).apply();
                                        }
                                        Toast.makeText(context, "Archivo Eliminado", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    materialDialog.dismiss();
                                }
                            })
                            .build();
                    borrar.show();
                }
                /*String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1);
                SharedPreferences sharedPreferences=context.getSharedPreferences("data", Context.MODE_PRIVATE);
                String titulo=sharedPreferences.getString("titInfo","Error");
                String url=getUrl(titulo, item);
                Intent intent=new Intent(context,WebDescarga.class);
                Bundle bundle=new Bundle();
                bundle.putString("url",url);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);*/
            }
        });
        holder.ib_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_video", "0"));
                if (file.exists()){
                    if (type == 0) {
                        PlayIntbySrc(file, holder.getAdapterPosition(), holder);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
                        intent.setDataAndType(Uri.fromFile(file), "video/mp4");
                        context.startActivity(intent);
                    }
                }else {
                    if (sd.exists()){
                        if (type == 0) {
                            PlayIntbySrc(file, holder.getAdapterPosition(), holder);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
                            intent.setDataAndType(Uri.fromFile(file), "video/mp4");
                            context.startActivity(intent);
                        }
                    }else {
                        Toast.makeText(context, "El archivo no existe", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1).trim();
                Boolean vistos=context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("cambio", true).apply();
                if (!vistos){
                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
                    String Svistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
                    Svistos=Svistos+";;;"+"visto" + id + "_" + item;
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                    String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                    if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                        //new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute("http://animeflvapp.x10.mx/fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos+":;:"+Svistos);
                    }
                    holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
                }else {
                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, false).apply();
                    String Svistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
                    Svistos=Svistos.replace(";;;"+"visto" + id + "_" + item,"");
                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                    String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                    if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                        //new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute("http://animeflvapp.x10.mx/fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos+":;:"+Svistos);
                    }
                    holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.black));
                }
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
        ftitulo=ftitulo.replace("2nd-season","2");
        ftitulo=ftitulo.replace("'","");
        if (ftitulo.trim().equals("gintama")){ftitulo=ftitulo+"-2015";}
        if (ftitulo.trim().equals("miss-monochrome-the-animation-2")){ftitulo="miss-monochrome-the-animation-2nd-season";}
        String link="http://animeflv.com/ver/"+ftitulo+"-"+capitulo+".html";
        return link;
    }
    public String getSD1(){
        String sSDpath = null;
        File   fileCur = null;
        for( String sPathCur : Arrays.asList("MicroSD", "external_SD", "sdcard1", "ext_card", "external_sd", "ext_sd", "external", "extSdCard", "externalSdCard")) {
            fileCur = new File( "/mnt/", sPathCur);
            if( fileCur.isDirectory() && fileCur.canWrite()) {
                sSDpath = fileCur.getAbsolutePath();
                break;
            }
            if( sSDpath == null)  {
                fileCur = new File( "/storage/", sPathCur);
                if( fileCur.isDirectory() && fileCur.canWrite())
                {
                    sSDpath = fileCur.getAbsolutePath();
                    break;
                }
            }
            if( sSDpath == null)  {
                fileCur = new File( "/storage/emulated", sPathCur);
                if( fileCur.isDirectory() && fileCur.canWrite())
                {
                    sSDpath = fileCur.getAbsolutePath();
                    Log.e("path",sSDpath);
                    break;
                }
            }
        }
        return sSDpath;
    }

    @Override
    public int getItemCount() {
        return capitulo.size();
    }

    public void DescargarSD(String url, ImageButton ib_des, ImageButton ib_ver, TextView tv_capitulo, int position) {
        String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1);
        File f = new File(getSD1() + "/Animeflv/download/" + id, id + "_" + item + ".mp4");
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        new Downloader(context, eids.get(position), id, sharedPreferences.getString("titInfo", "Error"), item, f).execute(url);
        ib_des.setImageResource(R.drawable.ic_borrar_r);
        ib_ver.setImageResource(R.drawable.ic_rep_r);
        ib_ver.setEnabled(true);
        final String email_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_email_coded", "null");
        final String pass_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_pass_coded", "null");
        Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
        if (!vistos) {
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
            String Svistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
            Svistos = Svistos + ";;;" + "visto" + id + "_" + item;
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
            String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
            if (!email_coded.equals("null") && !email_coded.equals("null")) {
                new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
            }
            tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
        }
    }

    public void DownloadByUrl(String url, ImageButton ib_des, ImageButton ib_ver, TextView tv_capitulo, int position) {
        final String email_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_email_coded", "null");
        final String pass_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_pass_coded", "null");
        File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + id);
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!Dstorage.exists()) {
                Dstorage.mkdirs();
            }
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1);
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        String titulo = sharedPreferences.getString("titInfo", "Error");
        request.setTitle(titulo);
        request.setDescription("Capitulo " + item);
        request.setMimeType("video/mp4");
        request.setDestinationInExternalPublicDir("Animeflv/download/" + id, id + "_" + item + ".mp4");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long l = manager.enqueue(request);
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eids.get(position), Long.toString(l)).apply();
        String descargados = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados + eids.get(position) + ":::").apply();
        String tits = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
        String epID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits + id + ":::").apply();
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID + id + "_" + item + ":::").apply();
        ib_des.setImageResource(R.drawable.ic_borrar_r);
        ib_ver.setImageResource(R.drawable.ic_rep_r);
        ib_ver.setEnabled(true);
        Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
        if (!vistos) {
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
            String Svistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
            Svistos = Svistos + ";;;" + "visto" + id + "_" + item;
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
            String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
            if (!email_coded.equals("null") && !email_coded.equals("null")) {
                new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
            }
            tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
        }
    }

    public class CheckDown extends AsyncTask<String, String, String> {
        public CheckDown(WebView w, ImageButton ib_des, ImageButton ib_ver, TextView tv_capitulo, int position) {
            this.des = ib_des;
            this.ver = ib_ver;
            this.cap = tv_capitulo;
            this.pos = position;
            this.web = w;
        }

        ImageButton des;
        ImageButton ver;
        WebView web;
        TextView cap;
        int pos;
        String _response;
        Spinner sp;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(new Parser().getBaseUrl(TaskType.NORMAL, context) + "getHtml.php?certificate=" + getCertificateSHA1Fingerprint() + "&url=" + params[0]);
                Log.d("URL", u.toString());
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
                c.setUseCaches(true);
                c.setConnectTimeout(5000);
                c.setAllowUserInteraction(false);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                String json = sb.toString().trim();
                if (new Parser().isJSONValid(json)) {
                    _response = json;
                } else {
                    _response = "error";
                }
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
                _response = "error";
            }
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("error")) {
                dialog.dismiss();
                Toast.makeText(context, "Error en servidor", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s.trim());
                    JSONArray jsonArray = jsonObject.getJSONArray("downloads");
                    final List<String> nombres = new ArrayList<>();
                    final List<String> urls = new ArrayList<>();
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String u = object.getString("url");
                            if (!u.trim().equals("null")) {
                                nombres.add(object.getString("name"));
                                urls.add(u);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    d = new MaterialDialog.Builder(context)
                            .title("Opciones")
                            .titleGravity(GravityEnum.CENTER)
                            .customView(R.layout.dialog_down, false)
                            .cancelable(true)
                            .autoDismiss(false)
                            .positiveText("Descargar")
                            .negativeText("Cancelar")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction dialogAction) {
                                    String desc = nombres.get(sp.getSelectedItemPosition());
                                    final String ur = urls.get(sp.getSelectedItemPosition());
                                    Log.d("Descargar", "URL -> " + ur);
                                    switch (desc.toLowerCase()) {
                                        case "izanagi":
                                            new Izanagi(des, ver, cap, pos).execute(ur);
                                            d.dismiss();
                                            break;
                                        case "zippyshare":
                                            web.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    web.loadUrl(ur);
                                                }
                                            });
                                            d.dismiss();
                                            break;
                                        case "mega":
                                            d.dismiss();
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                            break;
                                        default:
                                            chooseDownDir(ur, des, ver, cap, pos);
                                            d.dismiss();
                                            break;
                                    }
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    materialDialog.dismiss();
                                }
                            })
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    d.dismiss();
                                }
                            })
                            .build();
                    sp = (Spinner) d.getCustomView().findViewById(R.id.spinner_down);
                    sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                    d.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error en JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class CheckStream extends AsyncTask<String, String, String> {
        public CheckStream(WebView w, TextView tv_capitulo, int position, RecyclerAdapter.ViewHolder holder) {
            this.cap = tv_capitulo;
            this.pos = position;
            this.web = w;
            this.holder = holder;
        }

        WebView web;
        TextView cap;
        int pos;
        String _response;
        Spinner sp;
        RecyclerAdapter.ViewHolder holder;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(new Parser().getBaseUrl(TaskType.NORMAL, context) + "getHtml.php?certificate=" + getCertificateSHA1Fingerprint() + "&url=" + params[0]);
                Log.d("URL", u.toString());
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
                c.setUseCaches(true);
                c.setConnectTimeout(5000);
                c.setAllowUserInteraction(false);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                String json = sb.toString().trim();
                if (new Parser().isJSONValid(json)) {
                    _response = json;
                } else {
                    _response = "error";
                }
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
                _response = "error";
            }
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("error")) {
                dialog.dismiss();
                Toast.makeText(context, "Error en servidor", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s.trim());
                    JSONArray jsonArray = jsonObject.getJSONArray("downloads");
                    final List<String> nombres = new ArrayList<>();
                    final List<String> urls = new ArrayList<>();
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String u = object.getString("url");
                            if (!u.trim().equals("null")) {
                                nombres.add(object.getString("name"));
                                urls.add(u);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    d = new MaterialDialog.Builder(context)
                            .title("Opciones")
                            .titleGravity(GravityEnum.CENTER)
                            .customView(R.layout.dialog_down, false)
                            .cancelable(true)
                            .autoDismiss(false)
                            .positiveText("Reproducir")
                            .negativeText("Cancelar")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction dialogAction) {
                                    String desc = nombres.get(sp.getSelectedItemPosition());
                                    final String ur = urls.get(sp.getSelectedItemPosition());
                                    Log.d("Streaming", "URL -> " + ur);
                                    switch (desc.toLowerCase()) {
                                        case "izanagi":
                                            new IzanagiStream(cap, pos, holder).execute(ur);
                                            d.dismiss();
                                            break;
                                        case "zippyshare":
                                            streaming = true;
                                            posT = pos;
                                            web.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    web.loadUrl(ur);
                                                }
                                            });
                                            d.dismiss();
                                            break;
                                        case "mega":
                                            d.dismiss();
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                            break;
                                        default:
                                            StreamInbyURL(pos, ur, holder);
                                            d.dismiss();
                                            break;
                                    }
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    materialDialog.dismiss();
                                }
                            })
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    d.dismiss();
                                }
                            })
                            .build();
                    sp = (Spinner) d.getCustomView().findViewById(R.id.spinner_down);
                    sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                    d.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error en JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void chooseDownDir(String url, ImageButton ib_des, ImageButton ib_ver, TextView tv_capitulo, int position) {
        Boolean inSD = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("sd_down", false);
        if (inSD) {
            DescargarSD(url, ib_des, ib_ver, tv_capitulo, position);
        } else {
            DownloadByUrl(url, ib_des, ib_ver, tv_capitulo, position);
        }
    }

    public class Izanagi extends AsyncTask<String, String, String> {
        public Izanagi(ImageButton ib_des, ImageButton ib_ver, TextView tv_capitulo, int position) {
            this.des = ib_des;
            this.ver = ib_ver;
            this.cap = tv_capitulo;
            this.pos = position;
        }

        ImageButton des;
        ImageButton ver;
        TextView cap;
        int pos;
        String _response;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(params[0]);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                c.setRequestProperty("Accept", "*/*");
                c.setInstanceFollowRedirects(true);
                c.setUseCaches(false);
                c.setConnectTimeout(10000);
                c.setAllowUserInteraction(false);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                //c.disconnect();
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                _response = sb.toString();
                //String fullPage = page.asXml();
            } catch (Exception e) {
                Log.e("Requests", "Error in http connection " + e.toString());
                _response = "error";
            }
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String furl = s.substring(s.indexOf("URL=") + 4, s.lastIndexOf("\">"));
            chooseDownDir(furl, des, ver, cap, pos);
        }
    }

    public class IzanagiStream extends AsyncTask<String, String, String> {
        public IzanagiStream(TextView tv_capitulo, int position, RecyclerAdapter.ViewHolder holder) {
            this.cap = tv_capitulo;
            this.pos = position;
            this.holder = holder;
        }

        ImageButton des;
        ImageButton ver;
        TextView cap;
        int pos;
        String _response;
        RecyclerAdapter.ViewHolder holder;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(params[0]);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                c.setRequestProperty("Accept", "*/*");
                c.setInstanceFollowRedirects(true);
                c.setUseCaches(false);
                c.setConnectTimeout(10000);
                c.setAllowUserInteraction(false);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                //c.disconnect();
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                _response = sb.toString();
                //String fullPage = page.asXml();
            } catch (Exception e) {
                Log.e("Requests", "Error in http connection " + e.toString());
                _response = "error";
            }
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String furl = s.substring(s.indexOf("URL=") + 4, s.lastIndexOf("\">"));
            StreamInbyURL(pos, furl, holder);
        }
    }

    public void SetUpWeb(final WebView web, final RecyclerAdapter.ViewHolder holder) {
        web.getSettings().setJavaScriptEnabled(true);
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().startSync();
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("zippyshare.com") || url.contains("blank")) {
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("urlD", url).apply();
                    web.loadUrl("javascript:("
                            + "function(){var l=document.getElementById('dlbutton');" + "var f=document.createEvent('HTMLEvents');" + "f.initEvent('click',true,true);" + "l.dispatchEvent(f);}"
                            + ")()");
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        web.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                web.loadUrl("about:blank");
                if (!streaming) {
                    File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")));
                    if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                        if (!Dstorage.exists()) {
                            Dstorage.mkdirs();
                        }
                    }
                    File archivo = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")) + "/" + fileName);
                    if (!archivo.exists()) {
                        final String email_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_email_coded", "null");
                        final String pass_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_pass_coded", "null");
                        String item = capitulo.get(holder.getAdapterPosition()).substring(capitulo.get(holder.getAdapterPosition()).lastIndexOf(" ") + 1).trim();
                        String urlD = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("urlD", null);
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                        String titulo = sharedPreferences.getString("titInfo", "Error");
                        request.setTitle(titulo);
                        request.setDescription("Capitulo " + item);
                        request.addRequestHeader("cookie", cookie);
                        request.addRequestHeader("User-Agent", web.getSettings().getUserAgentString());
                        request.addRequestHeader("Accept", "text/html, application/xhtml+xml, *" + "/" + "*");
                        request.addRequestHeader("Accept-Language", "en-US,en;q=0.7,he;q=0.3");
                        request.addRequestHeader("Referer", urlD);
                        request.setMimeType("video/mp4");
                        request.setDestinationInExternalPublicDir("Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")), fileName);
                        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        long l = manager.enqueue(request);
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eids.get(holder.getAdapterPosition()), Long.toString(l)).apply();
                        String descargados = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados + eids.get(holder.getAdapterPosition()) + ":::").apply();
                        String tits = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                        String epID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits + id + ":::").apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID + id + "_" + item + ":::").apply();
                        holder.ib_des.setImageResource(R.drawable.ic_borrar_r);
                        holder.ib_ver.setImageResource(R.drawable.ic_rep_r);
                        holder.ib_ver.setEnabled(true);
                        Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
                        if (!vistos) {
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
                            String Svistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                            Svistos = Svistos + ";;;" + "visto" + id + "_" + item;
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                            String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                            if (!email_coded.equals("null") && !email_coded.equals("null")) {
                                new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
                            }
                            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
                        }
                    } else {
                        Toast.makeText(context, "El archivo ya existe", Toast.LENGTH_SHORT).show();
                    }
                    d.dismiss();

                } else {
                    int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
                    String urlD = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("urlD", null);
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                    streaming = false;
                    web.loadUrl("about:blank");
                    if (type == 1) {
                        List<ApplicationInfo> packages;
                        PackageManager pm;
                        pm = context.getPackageManager();
                        packages = pm.getInstalledApplications(0);
                        String pack = "null";
                        for (ApplicationInfo packageInfo : packages) {
                            if (packageInfo.packageName.equals("com.mxtech.videoplayer.pro")) {
                                pack = "com.mxtech.videoplayer.pro";
                                break;
                            }
                            if (packageInfo.packageName.equals("com.mxtech.videoplayer.ad")) {
                                pack = "com.mxtech.videoplayer.ad";
                                break;
                            }
                        }
                        switch (pack) {
                            case "com.mxtech.videoplayer.pro":
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri videoUri = Uri.parse(url);
                                intent.setDataAndType(videoUri, "application/mp4");
                                intent.putExtra("title", getTit() + " " + getNum(posT));
                                intent.setPackage("com.mxtech.videoplayer.pro");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                String[] headers = {"cookie", cookie, "User-Agent", web.getSettings().getUserAgentString(), "Accept", "text/html, application/xhtml+xml, *" + "/" + "*", "Accept-Language", "en-US,en;q=0.7,he;q=0.3", "Referer", urlD};
                                intent.putExtra("headers", headers);
                                context.startActivity(intent);
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + getNum(posT), true).apply();
                                String vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                                if (!vistos.contains(eids.get(posT))) {
                                    vistos = vistos + eids.get(posT) + ":::";
                                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistos).apply();
                                }
                                break;
                            case "com.mxtech.videoplayer.ad":
                                Intent intentad = new Intent(Intent.ACTION_VIEW);
                                Uri videoUriad = Uri.parse(url);
                                intentad.setDataAndType(videoUriad, "application/mp4");
                                intentad.setPackage("com.mxtech.videoplayer.ad");
                                intentad.putExtra("title", getTit() + " " + getNum(posT));
                                intentad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                String[] headersad = {"cookie", cookie, "User-Agent", web.getSettings().getUserAgentString(), "Accept", "text/html, application/xhtml+xml, *" + "/" + "*", "Accept-Language", "en-US,en;q=0.7,he;q=0.3", "Referer", urlD};
                                intentad.putExtra("headers", headersad);
                                context.startActivity(intentad);
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + getNum(posT), true).apply();
                                String vistosad = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                                if (!vistosad.contains(eids.get(posT))) {
                                    vistosad = vistosad + eids.get(posT) + ":::";
                                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistosad).apply();
                                }
                                break;
                            default:
                                toast("MX player no instalado");
                                break;
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Bundle bundle = new Bundle();
                            bundle.putString("url", url);
                            bundle.putString("ops", "cookie:::" + cookie + ";;;" + "User-Agent:::" + web.getSettings().getUserAgentString() + ";;;" + "Accept:::text/html, application/xhtml+xml, */*;;;" + "Accept-Language:::en-US,en;q=0.7,he;q=0.3;;;" + "Referer:::" + urlD);
                            Intent intent = parser.getPrefIntPlayer(context);
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            final String email_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_email_coded", "null");
                            final String pass_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_pass_coded", "null");
                            String item = capitulo.get(holder.getAdapterPosition()).substring(capitulo.get(holder.getAdapterPosition()).lastIndexOf(" ") + 1).trim();
                            Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
                            if (!vistos) {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
                                String Svistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                                Svistos = Svistos + ";;;" + "visto" + id + "_" + item;
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                                String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                if (!email_coded.equals("null") && !email_coded.equals("null")) {
                                    new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
                                }
                                holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
                            }
                        } else {
                            if (isMXinstalled()) {
                                toast("Version de android por debajo de lo requerido, reproduciendo en MXPlayer");
                                List<ApplicationInfo> packages;
                                PackageManager pm;
                                pm = context.getPackageManager();
                                packages = pm.getInstalledApplications(0);
                                String pack = "null";
                                for (ApplicationInfo packageInfo : packages) {
                                    if (packageInfo.packageName.equals("com.mxtech.videoplayer.pro")) {
                                        pack = "com.mxtech.videoplayer.pro";
                                        break;
                                    }
                                    if (packageInfo.packageName.equals("com.mxtech.videoplayer.ad")) {
                                        pack = "com.mxtech.videoplayer.ad";
                                        break;
                                    }
                                }
                                switch (pack) {
                                    case "com.mxtech.videoplayer.pro":
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        Uri videoUri = Uri.parse(url);
                                        intent.setDataAndType(videoUri, "application/mp4");
                                        intent.putExtra("title", getTit() + " " + getNum(posT));
                                        intent.setPackage("com.mxtech.videoplayer.pro");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        String[] headers = {"cookie", cookie, "User-Agent", web.getSettings().getUserAgentString(), "Accept", "text/html, application/xhtml+xml, *" + "/" + "*", "Accept-Language", "en-US,en;q=0.7,he;q=0.3", "Referer", urlD};
                                        intent.putExtra("headers", headers);
                                        context.startActivity(intent);
                                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + getNum(posT), true).apply();
                                        String vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                                        if (!vistos.contains(eids.get(posT))) {
                                            vistos = vistos + eids.get(posT) + ":::";
                                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistos).apply();
                                        }
                                        break;
                                    case "com.mxtech.videoplayer.ad":
                                        Intent intentad = new Intent(Intent.ACTION_VIEW);
                                        Uri videoUriad = Uri.parse(url);
                                        intentad.setDataAndType(videoUriad, "application/mp4");
                                        intentad.setPackage("com.mxtech.videoplayer.ad");
                                        intentad.putExtra("title", getTit() + " " + getNum(posT));
                                        intentad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        String[] headersad = {"cookie", cookie, "User-Agent", web.getSettings().getUserAgentString(), "Accept", "text/html, application/xhtml+xml, *" + "/" + "*", "Accept-Language", "en-US,en;q=0.7,he;q=0.3", "Referer", urlD};
                                        intentad.putExtra("headers", headersad);
                                        context.startActivity(intentad);
                                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + getNum(posT), true).apply();
                                        String vistosad = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                                        if (!vistosad.contains(eids.get(posT))) {
                                            vistosad = vistosad + eids.get(posT) + ":::";
                                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistosad).apply();
                                        }
                                        break;
                                    default:
                                        toast("MX player no instalado");
                                        break;
                                }
                            } else {
                                toast("No hay reproductor adecuado disponible");
                            }
                        }
                    }
                }
            }
        });
    }

    public boolean isMXinstalled() {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        String pack = "null";
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.pro")) {
                pack = "com.mxtech.videoplayer.pro";
                break;
            }
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.ad")) {
                pack = "com.mxtech.videoplayer.ad";
                break;
            }
        }
        return !pack.equals("null");
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

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void StreamInbyURL(int position, String url, RecyclerAdapter.ViewHolder holder) {
        if (isNetworkAvailable()) {
            int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
            Log.d("Streaming", PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
            switch (type) {
                case 0:
                    StreamingIntbyUrl(position, url, holder);
                    break;
                case 1:
                    StreamingExtbyURL(position, url);
                    break;
            }
        } else {
            toast("No hay conexion a internet");
        }
    }

    public void StreamingExtbyURL(int position, String url) {
        Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setType("application/mp4"));
        PackageManager pm = context.getPackageManager();
        final ResolveInfo mInfo = pm.resolveActivity(i, 0);
        if (mInfo != null) {
            String id = mInfo.activityInfo.applicationInfo.processName;
            if (id.startsWith("com.mxtech.videoplayer")) {
                StreamMXbyURL(position, url);
            } else {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } else {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void StreamingIntbyUrl(int position, String url, RecyclerAdapter.ViewHolder holder) {
        Intent interno = parser.getPrefIntPlayer(context);
        interno.putExtra("url", url);
        interno.putExtra("title", getTit() + " " + getNum(position));
        interno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(interno);
        final String email_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_email_coded", "null");
        final String pass_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_pass_coded", "null");
        String item = capitulo.get(holder.getAdapterPosition()).substring(capitulo.get(holder.getAdapterPosition()).lastIndexOf(" ") + 1).trim();
        Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
        if (!vistos) {
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
            String Svistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
            Svistos = Svistos + ";;;" + "visto" + id + "_" + item;
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
            String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
            if (!email_coded.equals("null") && !email_coded.equals("null")) {
                new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
            }
            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
        }
    }

    public void PlayIntbySrc(File file, int position, RecyclerAdapter.ViewHolder holder) {
        Intent interno = parser.getPrefIntPlayer(context);
        interno.putExtra("file", file.getAbsolutePath());
        interno.putExtra("title", getTit() + " " + getNum(position));
        interno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(interno);
        final String email_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_email_coded", "null");
        final String pass_coded = PreferenceManager.getDefaultSharedPreferences(context).getString("login_pass_coded", "null");
        String item = capitulo.get(holder.getAdapterPosition()).substring(capitulo.get(holder.getAdapterPosition()).lastIndexOf(" ") + 1).trim();
        Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
        if (!vistos) {
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
            String Svistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
            Svistos = Svistos + ";;;" + "visto" + id + "_" + item;
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
            String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
            if (!email_coded.equals("null") && !email_coded.equals("null")) {
                new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
            }
            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
        }
    }

    public void StreamMXbyURL(int position, String url) {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        String pack = "null";
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.pro")) {
                pack = "com.mxtech.videoplayer.pro";
                break;
            }
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.ad")) {
                pack = "com.mxtech.videoplayer.ad";
                break;
            }
        }
        switch (pack) {
            case "com.mxtech.videoplayer.pro":
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri videoUri = Uri.parse(url);
                intent.setDataAndType(videoUri, "application/mp4");
                intent.setPackage("com.mxtech.videoplayer.pro");
                intent.putExtra("title", getTit() + " " + getNum(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + getNum(position), true).apply();
                String vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                if (!vistos.contains(eids.get(position).trim())) {
                    vistos = vistos + eids.get(position).trim() + ":::";
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistos).apply();
                }
                break;
            case "com.mxtech.videoplayer.ad":
                Intent intentad = new Intent(Intent.ACTION_VIEW);
                Uri videoUriad = Uri.parse(url);
                intentad.setDataAndType(videoUriad, "application/mp4");
                intentad.setPackage("com.mxtech.videoplayer.ad");
                intentad.putExtra("title", getTit() + " " + getNum(position));
                intentad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentad);
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + getNum(position), true).apply();
                String vistosad = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                if (!vistosad.contains(eids.get(position).trim())) {
                    vistosad = vistosad + eids.get(position).trim() + ":::";
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistosad).apply();
                }
                break;
            default:
                toast("MX player no instalado");
                break;
        }
    }

    private boolean isNetworkAvailable() {
        Boolean net = false;
        int Tcon = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "0"));
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        switch (Tcon) {
            case 0:
                NetworkInfo Wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                net = Wifi.isConnected();
                break;
            case 1:
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net = mobile.isConnected();
                break;
            case 2:
                NetworkInfo WifiA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net = WifiA.isConnected() || mobileA.isConnected();
                break;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && net;
    }
}
