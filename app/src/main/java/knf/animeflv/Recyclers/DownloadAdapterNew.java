package knf.animeflv.Recyclers;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import knf.animeflv.R;
import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 08/08/2015.
 */
public class DownloadAdapterNew extends RecyclerView.Adapter<DownloadAdapterNew.ViewHolder> {

    final int COMPLETADO = 0;
    final int DESCARGANDO = 1;
    final int ERROR = 3;
    final int CANCELADO = 4;
    List<String> titulo;
    List<String> capitulo;
    List<Long> id;
    List<String> file;
    List<String> eid;
    String ext_storage_state = Environment.getExternalStorageState();
    Timer t = new Timer();
    Boolean downloading = true;
    private Context context;
    public DownloadAdapterNew(Context context, List<String> titulos, List<String> capitulos, List<Long> dids, List<String> files, List<String> eids) {
        this.context = context;
        this.titulo = titulos;
        this.capitulo = capitulos;
        this.id = dids;
        this.file = files;
        this.eid = eids;
    }

    @Override
    public DownloadAdapterNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_descarga, parent, false);
        return new DownloadAdapterNew.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DownloadAdapterNew.ViewHolder holder, final int position) {
        try {
            holder.tv_titulo.setText(titulo.get(position));
            holder.tv_numero.setText("Cap " + capitulo.get(position));
            holder.ib_des.setClickable(true);
            holder.ib_ver.setClickable(true);
        } catch (Exception e) {
            holder.cardView.setVisibility(View.GONE);
        }
        if (titulo.get(0).equals("Sin Descargas")) {
            holder.tv_numero.setVisibility(View.GONE);
            holder.ib_des.setVisibility(View.GONE);
            holder.ib_ver.setVisibility(View.GONE);
        }
        holder.ib_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prog = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(eid.get(holder.getAdapterPosition()) + "prog", "null");
                if (!prog.equals("null")) {
                    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(id.get(position));
                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        case DownloadManager.STATUS_FAILED:
                            String fileT = file.get(position);
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://subidas.com/files/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4"));
                            Log.d("DURL", "http://subidas.com/files/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            //request.setTitle(fileName.substring(0, fileName.indexOf(".")));
                            request.setTitle(titulo.get(position));
                            request.setDescription("Capitulo " + capitulo.get(position));
                            request.setMimeType("video/mp4");
                            request.setDestinationInExternalPublicDir("Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")), fileT + ".mp4");
                            DownloadManager managerR = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                            managerR.remove(id.get(position));
                            long l = managerR.enqueue(request);
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid.get(position), Long.toString(l)).apply();
                            holder.progress.setIndeterminate(false);
                            holder.progress.setProgress(0);
                            holder.ib_des.setImageResource(R.drawable.ic_block_r);
                            holder.ib_ver.setImageResource(R.drawable.ic_rep_r);
                            holder.tv_capitulo.setText("DESCARGANDO");
                            cursor.close();
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            Borrar(position, holder.cardView);
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            Borrar(position, holder.cardView);
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            Borrar(position, holder.cardView);
                            break;
                        case DownloadManager.STATUS_PENDING:
                            Borrar(position, holder.cardView);
                            break;
                    }
                } else {
                    switch (context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt(eid + "status", CANCELADO)) {
                        case ERROR:
                            Borrar(position, holder.cardView);
                            Toast.makeText(context, "No disponible", Toast.LENGTH_SHORT).show();
                            break;
                        case COMPLETADO:
                            Borrar(position, holder.cardView);
                            break;
                        case DESCARGANDO:
                            Borrar(position, holder.cardView);
                            break;
                        case CANCELADO:
                            Borrar(position, holder.cardView);
                            break;
                    }
                }
            }
        });
        holder.ib_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prog = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(eid.get(holder.getAdapterPosition()) + "prog", "null");
                if (!prog.equals("null")) {
                    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(id.get(position));
                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    String fileT = file.get(position);
                    switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        case DownloadManager.STATUS_FAILED:
                            String descargados = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eid.get(position) + ":::", "")).apply();
                            String tits = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                            String epID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits.replace(titulo.get(position) + ":::", "")).apply();
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(file.get(position) + ":::", "")).apply();
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            File fileD = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                            if (fileD.exists()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(fileD));
                                intent.setDataAndType(Uri.fromFile(fileD), "video/mp4");
                                context.startActivity(intent);
                            } else {
                                String descargados1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados1.replace(eid.get(position) + ":::", "")).apply();
                                String tits1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                                String epID1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits1.replace(titulo.get(position) + ":::", "")).apply();
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID1.replace(file.get(position) + ":::", "")).apply();
                                Toast.makeText(context, "La descarga ya no existe", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            File file1 = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                            if (file1.exists()) {
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file1));
                                intent1.setDataAndType(Uri.fromFile(file1), "video/mp4");
                                context.startActivity(intent1);
                            } else {
                                String descargados1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados1.replace(eid.get(position) + ":::", "")).apply();
                                String tits1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                                String epID1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits1.replace(titulo.get(position) + ":::", "")).apply();
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID1.replace(file.get(position) + ":::", "")).apply();
                                Toast.makeText(context, "La descarga ya no existe", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            File file2 = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                            if (file2.exists()) {
                                Toast.makeText(context, "Descarga pausada, puede que no se reproduzca completo", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file2));
                                intent1.setDataAndType(Uri.fromFile(file2), "video/mp4");
                                context.startActivity(intent1);
                            } else {
                                String descargados1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados1.replace(eid.get(position) + ":::", "")).apply();
                                String tits1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                                String epID1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits1.replace(titulo.get(position) + ":::", "")).apply();
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID1.replace(file.get(position) + ":::", "")).apply();
                                Toast.makeText(context, "La descarga ya no existe", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                    cursor.close();
                } else {
                    String fileT = file.get(position);
                    switch (context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt(eid + "status", CANCELADO)) {
                        case ERROR:
                            String descargados = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eid.get(position) + ":::", "")).apply();
                            String tits = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                            String epID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits.replace(titulo.get(position) + ":::", "")).apply();
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(file.get(position) + ":::", "")).apply();
                            break;
                        case CANCELADO:
                            String descargados2 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados2.replace(eid.get(position) + ":::", "")).apply();
                            String tits2 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                            String epID2 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits2.replace(titulo.get(position) + ":::", "")).apply();
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID2.replace(file.get(position) + ":::", "")).apply();
                            break;
                        case DESCARGANDO:
                            File fileD = new File(FileUtil.getSDPath() + "/Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                            if (fileD.exists()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(fileD));
                                intent.setDataAndType(Uri.fromFile(fileD), "video/mp4");
                                context.startActivity(intent);
                            } else {
                                String descargados1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados1.replace(eid.get(position) + ":::", "")).apply();
                                String tits1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                                String epID1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits1.replace(titulo.get(position) + ":::", "")).apply();
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID1.replace(file.get(position) + ":::", "")).apply();
                                Toast.makeText(context, "La descarga ya no existe", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case COMPLETADO:
                            File file1 = new File(FileUtil.getSDPath() + "/Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                            if (file1.exists()) {
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file1));
                                intent1.setDataAndType(Uri.fromFile(file1), "video/mp4");
                                context.startActivity(intent1);
                            } else {
                                String descargados1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados1.replace(eid.get(position) + ":::", "")).apply();
                                String tits1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                                String epID1 = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits1.replace(titulo.get(position) + ":::", "")).apply();
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID1.replace(file.get(position) + ":::", "")).apply();
                                Toast.makeText(context, "La descarga ya no existe", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            }
        });
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String prog = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(eid.get(position) + "prog", "null");
                if (!prog.equals("null")) {
                    final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Query q = new DownloadManager.Query();
                    String fileT = "";
                    Long l = Long.parseLong("0");
                    try {
                        fileT = file.get(position);
                        l = id.get(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    q.setFilterById(l);
                    Cursor cursor = manager.query(q);
                    if (cursor != null && cursor.moveToFirst() && l != 0) {
                        try {
                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                File file1 = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                                if (file1.exists()) {
                                    holder.progress.setVisibility(View.GONE);
                                    holder.ib_des.setImageResource(R.drawable.ic_borrar_r);
                                    holder.tv_capitulo.setText("COMPLETADO");
                                } else {
                                    Borrar(position, holder.cardView);
                                }
                            } else {
                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_RUNNING) {
                                    int bytes_downloaded = cursor.getInt(cursor
                                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                                    holder.progress.setProgress(dl_progress);
                                    holder.ib_des.setImageResource(R.drawable.ic_block_r);
                                    holder.tv_capitulo.setText("DESCARGANDO");
                                } else {
                                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                                        holder.progress.setIndeterminate(true);
                                        holder.tv_capitulo.setText("ERROR");
                                        holder.ib_des.setImageResource(R.drawable.ic_refresh);
                                        holder.ib_ver.setImageResource(R.drawable.ic_block_r);
                                    } else {
                                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_PENDING) {
                                            holder.progress.setIndeterminate(true);
                                            holder.tv_capitulo.setText("PENDIENTE");
                                            holder.ib_des.setImageResource(R.drawable.ic_block_r);
                                            holder.ib_ver.setVisibility(View.INVISIBLE);
                                        } else {
                                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_PAUSED) {
                                                File file1 = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                                                if (file1.exists()) {
                                                    holder.progress.setIndeterminate(true);
                                                    holder.tv_capitulo.setText("REANUNDANDO");
                                                    holder.ib_des.setImageResource(R.drawable.ic_block_r);
                                                } else {
                                                    Borrar(position, holder.cardView);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    String fileT = "";
                    try {
                        fileT = file.get(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    switch (context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt(eid + "status", CANCELADO)) {
                        case COMPLETADO:
                            File file1 = new File(FileUtil.getSDPath() + "/Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                            if (file1.exists()) {
                                holder.progress.setVisibility(View.GONE);
                                holder.ib_des.setImageResource(R.drawable.ic_borrar_r);
                                holder.tv_capitulo.setText("COMPLETADO");
                            } else {
                                Borrar(position, holder.cardView);
                            }
                            break;
                        case DESCARGANDO:
                            holder.progress.setProgress(Integer.parseInt(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(eid.get(holder.getAdapterPosition()) + "prog", "null")));
                            holder.ib_des.setImageResource(R.drawable.ic_block_r);
                            holder.tv_capitulo.setText("DESCARGANDO");
                            break;
                        case ERROR:
                            holder.progress.setIndeterminate(true);
                            holder.tv_capitulo.setText("ERROR");
                            holder.ib_des.setImageResource(R.drawable.ic_refresh);
                            holder.ib_ver.setImageResource(R.drawable.ic_block_r);
                            break;
                        case CANCELADO:
                            holder.progress.setIndeterminate(true);
                            holder.tv_capitulo.setText("CANCELADO");
                            holder.ib_des.setImageResource(R.drawable.ic_refresh);
                            holder.ib_ver.setImageResource(R.drawable.ic_block_r);
                            break;
                    }

                }
            }
        }, 0, 1000);
    }

    public void hideProgress(ProgressBar progress, ImageButton ib_des, TextView tv_capitulo) {
        progress.setVisibility(View.GONE);
        ib_des.setImageResource(R.drawable.ic_borrar_r);
        tv_capitulo.setText("COMPLETADO");
    }

    public void Borrar(final int position, final CardView card) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("ELIMINAR")
                .titleGravity(GravityEnum.CENTER)
                .content("Desea eliminar la descarga del capitulo " + capitulo.get(position) + " de " + titulo.get(position) + "?")
                .positiveText("ACEPTAR")
                .negativeText("CANCELAR")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        card.setVisibility(View.GONE);
                        String fileT = file.get(position);
                        DownloadManager manager0 = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        manager0.remove(id.get(position));
                        String descargados = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eid.get(position) + ":::", "")).apply();
                        String tits = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
                        String epID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits.replace(titulo.get(position) + ":::", "")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(file.get(position) + ":::", "")).apply();
                        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + fileT.substring(0, fileT.indexOf("_")) + "/" + fileT + ".mp4");
                        if (file.delete()) {
                            Toast.makeText(context, "Descarga Eliminada", Toast.LENGTH_SHORT).show();
                        }
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(0, titulo.size());
                        notifyDataSetChanged();
                    }
                })
                .build();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return titulo.size();
    }

    public String getSD1() {
        String sSDpath = null;
        File fileCur = null;
        for (String sPathCur : Arrays.asList("MicroSD", "external_SD", "sdcard1", "ext_card", "external_sd", "ext_sd", "external", "extSdCard", "externalSdCard", "8E84-7E70")) {
            fileCur = new File("/mnt/", sPathCur);
            if (fileCur.isDirectory() && fileCur.canWrite()) {
                sSDpath = fileCur.getAbsolutePath();
                break;
            }
            if (sSDpath == null) {
                fileCur = new File("/storage/", sPathCur);
                if (fileCur.isDirectory() && fileCur.canWrite()) {
                    sSDpath = fileCur.getAbsolutePath();
                    break;
                }
            }
            if (sSDpath == null) {
                fileCur = new File("/storage/emulated", sPathCur);
                if (fileCur.isDirectory() && fileCur.canWrite()) {
                    sSDpath = fileCur.getAbsolutePath();
                    Log.e("path", sSDpath);
                    break;
                }
            }
        }
        return sSDpath;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_titulo;
        public TextView tv_capitulo;
        public TextView tv_numero;
        public ImageButton ib_ver;
        public ImageButton ib_des;
        public ProgressBar progress;
        public CardView cardView;
        public RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_titulo = (TextView) itemView.findViewById(R.id.tv_cardDownload_titulo);
            this.tv_capitulo = (TextView) itemView.findViewById(R.id.tv_cardDownload_capitulo);
            this.tv_numero = (TextView) itemView.findViewById(R.id.tv_numero);
            this.ib_ver = (ImageButton) itemView.findViewById(R.id.ib_ver_download);
            this.ib_des = (ImageButton) itemView.findViewById(R.id.ib_descargar_download);
            this.progress = (ProgressBar) itemView.findViewById(R.id.progress_download);
            this.cardView = (CardView) itemView.findViewById(R.id.card_descargas);
        }
    }

}
