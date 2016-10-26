package knf.animeflv;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
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
        for (final String i : aids) {
            final File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt");
            if (!file.exists() || !FileUtil.isJSONValid(FileUtil.getStringFromFile(file.getPath()))) {
                new SyncHttpClient().get(new Parser().getInicioUrl(TaskType.NORMAL, context) + "?url=" + parser.getUrlAnimeCached(i) + "&certificate=" + Parser.getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        FileUtil.writeToFile(response.toString(), file);
                        list.add(parser.getTit(response.toString()));
                        updateDialog();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        File file1 = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt");
                        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt";
                        if (file1.exists()) {
                            String json = FileUtil.getStringFromFile(file_loc);
                            if (FileUtil.isJSONValid(json)) {
                                list.add(parser.getTit(json));
                            } else {
                                file1.delete();
                            }
                        }
                        updateDialog();
                    }
                });
            }else {
                Log.d("Link", "Loaded "+i);
                String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt";
                if (file.exists()) {
                    String json = FileUtil.getStringFromFile(file_loc);
                    if (FileUtil.isJSONValid(json)) {
                        list.add(parser.getTit(json));
                    } else {
                        file.delete();
                    }
                }
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
