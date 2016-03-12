package knf.animeflv.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import knf.animeflv.Parser;
import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterRel;
import knf.animeflv.TaskType;

/**
 * Created by Jordy on 12/08/2015.
 */
public class AnimeInfo extends Fragment{
    public AnimeInfo(){}
    Parser parser=new Parser();

    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");

    ImageView imageView;
    TextView txt_sinopsis;
    TextView txt_titulo;
    TextView txt_tipo;
    TextView txt_estado;
    TextView txt_generos;
    TextView txt_debug;
    LinearLayout layout;
    LinearLayout layout_debug;
    RecyclerView rv_rel;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.anime_info, container, false);
        rv_rel=(RecyclerView) view.findViewById(R.id.rv_relacionados);
        rv_rel.setHasFixedSize(true);
        rv_rel.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        String json=getJsonfromFile(true);
        List<String> titulos=parser.parseTitRel(json);
        List<String> tipos=parser.parseTiposRel(json);
        String[] urls=parser.urlsRel(json);
        String[] aids=parser.parseAidRel(json);
        if (urls.length==0){
            rv_rel.setVisibility(View.GONE);
        }
        AdapterRel adapter = new AdapterRel(getActivity().getApplicationContext(), titulos,tipos,urls,aids);
        rv_rel.setAdapter(adapter);
        setLoad();
        getJsonfromFile();
        return view;
    }
    public void getJsonfromFile(){
        String aid = getArguments().getString("aid");
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt";
            if (file.exists()) {
                Log.d("Archivo", "Existe");
                String infile = getStringFromFile(file_loc);
                setInfo(infile);
            }
    }
    public String getJsonfromFile(Boolean bool){
        String json="{}";
        String aid = getArguments().getString("aid");
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt";
        if (file.exists()) {
            Log.d("Archivo", "Existe");
            json = getStringFromFile(file_loc);
        }
        return json;
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
    public static String getStringFromFile (String filePath) {
        String ret="";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        }catch (IOException e){}catch (Exception e){}
        return ret;
    }

    public void setLoad(){
        imageView=(ImageView) view.findViewById(R.id.info_img);
        txt_sinopsis=(TextView) view.findViewById(R.id.info_descripcion);
        txt_titulo=(TextView) view.findViewById(R.id.titulo);
        txt_tipo=(TextView) view.findViewById(R.id.tipo);
        txt_estado=(TextView) view.findViewById(R.id.estado);
        txt_generos=(TextView) view.findViewById(R.id.generos);
        txt_debug = (TextView) view.findViewById(R.id.debug_info);
        layout=(LinearLayout) view.findViewById(R.id.lay_info);
        layout_debug = (LinearLayout) view.findViewById(R.id.lay_debug);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("is_amoled", false)) {
            view.setBackgroundColor(getResources().getColor(android.R.color.black));
            txt_sinopsis.setTextColor(getResources().getColor(R.color.blanco));
            txt_titulo.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            txt_tipo.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            txt_estado.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            txt_generos.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            txt_debug.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            TextView tit1 = (TextView) view.findViewById(R.id.info_titles1);
            TextView tit2 = (TextView) view.findViewById(R.id.info_titles2);
            TextView tit3 = (TextView) view.findViewById(R.id.info_titles3);
            TextView tit4 = (TextView) view.findViewById(R.id.info_titles4);
            TextView tit5 = (TextView) view.findViewById(R.id.info_titles5);
            tit1.setTextColor(getResources().getColor(R.color.blanco));
            tit2.setTextColor(getResources().getColor(R.color.blanco));
            tit3.setTextColor(getResources().getColor(R.color.blanco));
            tit4.setTextColor(getResources().getColor(R.color.blanco));
            tit5.setTextColor(getResources().getColor(R.color.blanco));
        }
    }
    public void setInfo(String json){
        final Context context=getActivity().getApplicationContext();
        final String jinfo=json;
        final String aid = getArguments().getString("aid", "null");
        final Boolean isDebuging = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("debug", false);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&portada=" + parser.getUrlPortada(jinfo)).error(R.drawable.ic_block_r).fit().into(imageView);
                txt_sinopsis.setText(parser.getInfoSinopsis(jinfo));
                txt_titulo.setText(parser.getInfotitulo(jinfo));
                txt_tipo.setText(parser.getInfoTipo(jinfo));
                txt_estado.setText(parser.getInfoEstado(jinfo));
                txt_generos.setText(parser.getInfoGeneros(jinfo));
                if (isDebuging) {
                    txt_debug.setText(aid);
                    layout_debug.setVisibility(View.VISIBLE);
                }
                layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private String getCertificateSHA1Fingerprint() {
        PackageManager pm = getActivity().getPackageManager();
        String packageName = getActivity().getPackageName();
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
}
