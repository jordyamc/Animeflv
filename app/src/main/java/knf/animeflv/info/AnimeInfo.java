package knf.animeflv.info;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import knf.animeflv.Recyclers.AdapterRel;
import knf.animeflv.Recyclers.RecyclerAdapter;
import knf.animeflv.Requests;
import knf.animeflv.TaskType;

/**
 * Created by Jordy on 12/08/2015.
 */
public class AnimeInfo extends Fragment{
    public AnimeInfo(){}
    Parser parser=new Parser();

    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache");

    ImageView imageView;
    TextView txt_sinopsis;
    TextView txt_titulo;
    TextView txt_tipo;
    TextView txt_estado;
    TextView txt_generos;
    LinearLayout layout;
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
        //SharedPreferences sharedPreferences=getActivity().getSharedPreferences("data",Context.MODE_PRIVATE);
        //String aid=sharedPreferences.getString("aid","");
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        String aid=sharedPreferences.getString("aid","");
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/"+aid+".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/.Animeflv/cache/"+aid+".txt";
            if (file.exists()) {
                Log.d("Archivo", "Existe");
                String infile = getStringFromFile(file_loc);
                setInfo(infile);
            }
    }
    public String getJsonfromFile(Boolean bool){
        String json="{}";
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        String aid=sharedPreferences.getString("aid","");
        Log.d("Info Aid",aid);
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/"+aid+".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/.Animeflv/cache/"+aid+".txt";
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
        layout=(LinearLayout) view.findViewById(R.id.lay_info);
    }
    public void setInfo(String json){
        final Context context=getActivity().getApplicationContext();
        final String jinfo=json;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PicassoCache.getPicassoInstance(context).load(parser.getUrlPortada(jinfo)).error(R.drawable.ic_block_r).fit().into(imageView);
                txt_sinopsis.setText(parser.getInfoSinopsis(jinfo));
                txt_titulo.setText(parser.getInfotitulo(jinfo));
                txt_tipo.setText(parser.getInfoTipo(jinfo));
                txt_estado.setText(parser.getInfoEstado(jinfo));
                txt_generos.setText(parser.getInfoGeneros(jinfo));
                layout.setVisibility(View.VISIBLE);
            }
        });
    }
}
