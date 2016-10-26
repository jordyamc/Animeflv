package knf.animeflv;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Environment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;

public class RequestFavSort extends AsyncTask<String, List<AnimeClass>, List<AnimeClass>> {
    InputStream is;
    List<AnimeClass> _response;
    callback call;
    TaskType taskType;
    Parser parser = new Parser();
    StringBuilder builder = new StringBuilder();
    HttpURLConnection c = null;
    Activity context;
    MaterialDialog dialog;
    String[] favs;
    int prog = 0;

    public RequestFavSort(Activity con, TaskType taskType,String[] favs) {
        call = (callback) con;
        this.context = con;
        this.taskType = taskType;
        this.favs=favs;
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
    protected void onPreExecute() {
        super.onPreExecute();
        dialog=new MaterialDialog.Builder(context)
                .content("Reacomodando...\n"+"("+prog+"/"+favs.length+")")
                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                .progress(true,0)
                .build();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    @Override
    protected List<AnimeClass> doInBackground(String... params) {
        final List<AnimeClass> list = new ArrayList<AnimeClass>();
        for (final String i : favs) {
            final File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt");
            if (!file.exists() || !isJSONValid(getStringFromFile(file.getPath()))) {
                new SyncHttpClient().get(new Parser().getInicioUrl(TaskType.NORMAL, context) + "?url=" + parser.getUrlAnimeCached(i) + "&certificate=" + getCertificateSHA1Fingerprint(), null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        writeToFile(response.toString(), file);
                        list.add(new AnimeClass(response.toString()));
                        updateDialog();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        File file1 = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt");
                        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt";
                        if (file1.exists()) {
                            String json = getStringFromFile(file_loc);
                            if (FileUtil.isJSONValid(json)) {
                                list.add(new AnimeClass(json));
                            } else {
                                file1.delete();
                            }
                        }
                        updateDialog();
                    }
                });
            } else {
                String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt";
                if (file.exists()) {
                    String json = getStringFromFile(file_loc);
                    if (FileUtil.isJSONValid(json)) {
                        list.add(new AnimeClass(json));
                    } else {
                        file.delete();
                    }
                }
                updateDialog();
            }
        }
        return list;
    }

    private void updateDialog(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prog++;
                dialog.setContent("Reacomodando...\n"+"("+prog+"/"+favs.length+")");
            }
        });
    }

    public void writeToFile(String body, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(List<AnimeClass> s) {
        super.onPostExecute(s);
        call.favCallSort(s, taskType, dialog);
    }

    public interface callback {
        void favCallSort(List<AnimeClass> list, TaskType taskType, MaterialDialog dialog);
    }
}
