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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
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
    String id;
    List<String> eids;

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
        String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1);
        final File file=new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/"+id+"/"+id+"_"+item+".mp4");
        if (file.exists()){
            holder.ib_des.setImageResource(R.drawable.ic_borrar_r);
        }else {
            holder.ib_ver.setImageResource(R.drawable.ic_ver_no);
        }
        holder.tv_capitulo.setText(capitulo.get(position));
        holder.ib_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!file.exists()) {
                    if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("streaming", false)) {
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
                        context.getSharedPreferences("data", context.MODE_PRIVATE).edit().putString(eids.get(position), Long.toString(l)).apply();
                        holder.ib_des.setImageResource(R.drawable.ic_borrar_r);
                        holder.ib_ver.setImageResource(R.drawable.ic_rep_r);
                    } else {
                        MaterialDialog dialog = new MaterialDialog.Builder(context)
                                .title("Descargar?")
                                .titleGravity(GravityEnum.CENTER)
                                .content("Desea descargar el capitulo?")
                                .autoDismiss(false)
                                .cancelable(false)
                                .positiveText("DESCARGAR")
                                .negativeText("STREAMING")
                                .neutralText("ATRAS")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
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
                                        context.getSharedPreferences("data", context.MODE_PRIVATE).edit().putString(eids.get(position), Long.toString(l)).apply();
                                        holder.ib_des.setImageResource(R.drawable.ic_borrar_r);
                                        holder.ib_ver.setImageResource(R.drawable.ic_rep_r);
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
                                        String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1);
                                        switch (pack) {
                                            case "com.mxtech.videoplayer.pro":
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                Uri videoUri = Uri.parse("http://subidas.com/files/" + id + "/" + item + ".mp4");
                                                intent.setDataAndType(videoUri, "application/mp4");
                                                intent.setPackage("com.mxtech.videoplayer.pro");
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                                dialog.dismiss();
                                                break;
                                            case "com.mxtech.videoplayer.ad":
                                                Intent intentad = new Intent(Intent.ACTION_VIEW);
                                                Uri videoUriad = Uri.parse("http://subidas.com/files/" + id + "/" + item + ".mp4");
                                                intentad.setDataAndType(videoUriad, "application/mp4");
                                                intentad.setPackage("com.mxtech.videoplayer.ad");
                                                intentad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intentad);
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
                    String item = capitulo.get(position).substring(capitulo.get(position).lastIndexOf(" ") + 1);
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
                                        long l = Long.parseLong(context.getSharedPreferences("data", context.MODE_PRIVATE).getString(eids.get(position), "0"));
                                        if (l != 0) {
                                            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                            manager.remove(l);
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
        String link="http://animeflv.net/ver/"+ftitulo+"-"+capitulo+".html";
        return link;
    }

    @Override
    public int getItemCount() {
        return capitulo.size();
    }

}
