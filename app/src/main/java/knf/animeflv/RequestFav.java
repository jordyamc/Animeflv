package knf.animeflv;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.JsonFactory.OfflineGetter;
import knf.animeflv.Utils.FileUtil;

public class RequestFav extends AsyncTask<String,String,String> {
    InputStream is;
    String _response;
    callback call;
    TaskType taskType;
    Parser parser=new Parser();
    HttpURLConnection c = null;
    URL u;
    Activity context;
    MaterialDialog dialog;
    Boolean running;
    int prog = 0;
    List<String> aids;
    public RequestFav(Activity con, TaskType taskType, MaterialDialog d, List<String> aids) {
        call=(callback) con;
        this.context = con;
        this.taskType=taskType;
        this.dialog = d;
        this.aids=aids;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setContent("Actualizando Favoritos\n"+"("+prog+"/"+aids.size()+")");
            }
        });
        running = true;
    }

    @Override
    protected String doInBackground(String... params) {
        final List<String> list = new ArrayList<String>();
        Log.e("Loading Favs", "Start");
        for (final String i : aids) {
            String off_json = OfflineGetter.getAnime(new ANIME(Integer.parseInt(i)));
            if (!FileUtil.isJSONValid(off_json)) {
                BaseGetter.getJson(context, new ANIME(Integer.parseInt(i)), new BaseGetter.AsyncInterface() {
                    @Override
                    public void onFinish(String json) {
                        if (!json.equals("null")) {
                            list.add(parser.getTit(json));
                        }
                        updateDialog();
                    }
                });
            }else {
                list.add(parser.getTit(off_json));
                updateDialog();
            }
        }
        String[] favoritos=new String[list.size()];
        list.toArray(favoritos);
        StringBuilder builder = new StringBuilder();
        for(String i : favoritos) {
            builder.append(":::" + i);
        }
        _response=builder.toString();
        return _response;
    }

    private void updateDialog(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prog++;
                dialog.setContent("Actualizando Favoritos\n"+"("+prog+"/"+aids.size()+")");
            }
        });
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        call.favCall(s, taskType);
    }

    public interface callback {
        void favCall(String data, TaskType taskType);
    }
}
