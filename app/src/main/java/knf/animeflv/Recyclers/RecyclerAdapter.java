package knf.animeflv.Recyclers;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import knf.animeflv.LoginServer;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.WebDescarga;

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

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_capitulo = (TextView) itemView.findViewById(R.id.tv_cardD_capitulo);
            this.ib_ver = (ImageButton) itemView.findViewById(R.id.ib_ver_rv);
            this.ib_des = (ImageButton) itemView.findViewById(R.id.ib_descargar_rv);
            this.card = (CardView) itemView.findViewById(R.id.card_descargas_info);
        }
    }
    private Context context;
    List<String> capitulo;
    String id;
    List<String> eids;
    String ext_storage_state = Environment.getExternalStorageState();

    public RecyclerAdapter(Context context, List<String> capitulos,String aid,List<String> eid) {
        this.capitulo = capitulos;
        this.context = context;
        this.id=aid;
        this.eids=eid;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_descarga, parent, false);
        return new RecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, final int position) {
        String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1).trim();
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
                        File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + id);
                        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                            if (!Dstorage.exists()) {
                                Dstorage.mkdirs();
                            }
                        }
                        String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1).trim();
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://subidas.com/files/" + id + "/" + item + ".mp4"));
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        //request.setTitle(fileName.substring(0, fileName.indexOf(".")));
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
                        holder.ib_des.setImageResource(R.drawable.ic_borrar_r);
                        holder.ib_ver.setImageResource(R.drawable.ic_rep_r);
                        holder.ib_ver.setEnabled(true);
                        Boolean vistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
                        if (!vistos){
                            context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
                            String Svistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
                            Svistos=Svistos+";;;"+"visto" + id + "_" + item;
                            context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                            String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                            if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                                new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute("http://animeflv-app.ultimatefreehost.in/fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
                            }
                            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
                        }
                    } else {
                        MaterialDialog dialog = new MaterialDialog.Builder(context)
                                .title("Descargar?")
                                .titleGravity(GravityEnum.CENTER)
                                .content("Desea descargar el capitulo?")
                                .autoDismiss(false)
                                .cancelable(true)
                                .positiveText("DESCARGAR")
                                .negativeText("STREAMING")
                                .neutralText("ATRAS")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + id);
                                        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                                            if (!Dstorage.exists()) {
                                                Dstorage.mkdirs();
                                            }
                                        }
                                        String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1);
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://subidas.com/files/" + id + "/" + item + ".mp4"));
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        //request.setTitle(fileName.substring(0, fileName.indexOf(".")));
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
                                        holder.ib_des.setImageResource(R.drawable.ic_borrar_r);
                                        holder.ib_ver.setImageResource(R.drawable.ic_rep_r);
                                        holder.ib_ver.setEnabled(true);
                                        Boolean vistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
                                        if (!vistos){
                                            context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
                                            String Svistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
                                            Svistos=Svistos+";;;"+"visto" + id + "_" + item;
                                            context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                                            String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                            if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                                                new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute("http://animeflv-app.ultimatefreehost.in/fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
                                            }
                                            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
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
                                        String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1).trim();
                                        switch (pack) {
                                            case "com.mxtech.videoplayer.pro":
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                Uri videoUri = Uri.parse("http://subidas.com/files/" + id + "/" + item + ".mp4");
                                                intent.setDataAndType(videoUri, "application/mp4");
                                                intent.setPackage("com.mxtech.videoplayer.pro");
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                                Boolean vistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
                                                if (!vistos){
                                                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
                                                    String Svistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
                                                    Svistos=Svistos+";;;"+"visto" + id + "_" + item;
                                                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                                                    String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                                    if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                                                        new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute("http://animeflv-app.ultimatefreehost.in/fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
                                                    }
                                                    holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
                                                }
                                                dialog.dismiss();
                                                break;
                                            case "com.mxtech.videoplayer.ad":
                                                Intent intentad = new Intent(Intent.ACTION_VIEW);
                                                Uri videoUriad = Uri.parse("http://subidas.com/files/" + id + "/" + item + ".mp4");
                                                intentad.setDataAndType(videoUriad, "application/mp4");
                                                intentad.setPackage("com.mxtech.videoplayer.ad");
                                                intentad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intentad);
                                                Boolean vistosad=context.getSharedPreferences("data",Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
                                                if (!vistosad){
                                                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
                                                    String Svistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
                                                    Svistos=Svistos+";;;"+"visto" + id + "_" + item;
                                                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                                                    String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                                    if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                                                        new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute("http://animeflv-app.ultimatefreehost.in/fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
                                                    }
                                                    holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
                                                }
                                                dialog.dismiss();
                                                break;
                                            default:
                                                Toast.makeText(context, "MX player no instalado", Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onNeutral(MaterialDialog dialog) {
                                        super.onNeutral(dialog);
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
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
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

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    dialog.dismiss();
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
                if (file.exists()){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
                    intent.setDataAndType(Uri.fromFile(file), "video/mp4");
                    context.startActivity(intent);
                }else {
                    if (sd.exists()){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(sd));
                        intent.setDataAndType(Uri.fromFile(sd), "video/mp4");
                        context.startActivity(intent);
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
                if (!vistos){
                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
                    String Svistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
                    Svistos=Svistos+";;;"+"visto" + id + "_" + item;
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                    String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                    if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                        //new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute("http://animeflv-app.ultimatefreehost.in/fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos+":;:"+Svistos);
                    }
                    holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.rojo));
                }else {
                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, false).apply();
                    String Svistos=context.getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
                    Svistos=Svistos.replace(";;;"+"visto" + id + "_" + item,"");
                    context.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putString("vistos", Svistos).apply();
                    String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                    if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                        //new LoginServer(context, TaskType.GET_FAV_SL, null, null, null, null).execute("http://animeflv-app.ultimatefreehost.in/fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos+":;:"+Svistos);
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

}
