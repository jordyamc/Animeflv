package knf.animeflv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

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
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.objects.MainObject;

/**
 * Created by Jordy on 10/08/2015.
 */
public class Parser {

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

    public static List<MainObject> parseMainList(JSONObject object) {
        List<MainObject> objects = new ArrayList<>();
        try {
            JSONArray array = object.getJSONArray("lista");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                objects.add(new MainObject(jsonObject.getString("aid"), jsonObject.getString("numero"), FileUtil.corregirTit(jsonObject.getString("titulo")), jsonObject.getString("eid")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    public static List<MainObject> parseMainList(String s) {
        List<MainObject> objects = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(s);
            JSONArray array = object.getJSONArray("lista");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                objects.add(new MainObject(jsonObject.getString("aid"), jsonObject.getString("numero"), FileUtil.corregirTit(jsonObject.getString("titulo")), jsonObject.getString("eid")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
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

    public static String ValidateSinopsis(String sin) {
        if (sin.trim().equals("")) {
            return "Sin Sinopsis.";
        }
        String url = sin;
        url = url.replace("&aacute;", "\u00E1");
        url = url.replace("&eacute;", "\u00E9");
        url = url.replace("&iacute;", "\u00ED");
        url = url.replace("&oacute;", "\u00F3");
        url = url.replace("&uacute;", "\u00FA");
        url = url.replace("&Aacute;", "\u00C1");
        url = url.replace("&Eacute;", "\u00C9");
        url = url.replace("&Iacute;", "\u00CD");
        url = url.replace("&Oacute;", "\u00D3");
        url = url.replace("&Uacute;", "\u00DA");
        url = url.replace("&ntilde;", "\u00F1");
        url = url.replace("&ldquo;", "\u201C");
        url = url.replace("&rdquo;", "\u201D");
        url = url.replace("&rsquo;", "\u2019");
        url = url.replace("&iquest;", "\u00BF");
        url = url.replace("&hellip;", "\u2026");
        url = url.replace("&#333;", "\u014D");
        url = url.replace("&uuml;", "\u00FC");
        url = url.replace("&iexcl;", "¡");
        url = url.replace("&quot;", "\"");
        url = url.replace("&#039;", "\'");
        url = url.replace("&lt;", "<");
        url = url.replace("&gt;", ">");
        return url;
    }

    public static List<String> getEids(JSONObject jsonObj) {
        List<String> eidsArray = new ArrayList<String>();
        try {
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String eid = childJSONObject.getString("eid");
                eidsArray.add(eid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eidsArray;
    }

    public static String getCertificateSHA1Fingerprint(Context context) {
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

    public static String getTitleCached(String aid) {
        String ret = "null";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String n = nombreJ.getString("a");
                    if (n.trim().equals(aid)) {
                        return FileUtil.corregirTit(nombreJ.getString("b"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return FileUtil.corregirTit(ret);
    }

    public static String getTypeCached(String aid) {
        String ret = "null";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String n = nombreJ.getString("a");
                    if (n.trim().equals(aid)) {
                        return FileUtil.corregirTit(nombreJ.getString("c"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static int getLastAidCached() {
        int aid = 2500;
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                JSONObject nombreJ = jsonArray.getJSONObject(jsonArray.length() - 1);
                return Integer.parseInt(nombreJ.getString("a"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return aid;
    }

    public static String getTrimedList(String body, String separator) {
        if (body.trim().length() > 0) {
            StringBuilder builder = new StringBuilder();
            String[] trimed = body.split(separator);
            for (String part : trimed) {
                if (!part.trim().equals("") && !part.equals("null")) {
                    builder.append(part);
                    builder.append(separator);
                }
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    public String[] parseTitulos(String json) {
        List<String> titulosArray = new ArrayList<String>();
        String[] titulos;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String titulo = childJSONObject.getString("titulo");
                titulo = titulo.replace("&#039;", "\'");
                titulo = titulo.replace("&iacute;", "í");
                titulo = titulo.replace("&deg;", "°");
                titulo = titulo.replace("&amp;", "&");
                titulo = titulo.replace("&acirc;", "\u00C2");
                titulo = titulo.replace("&egrave;", "\u00E8");
                titulo = titulo.replace("&middot;", "\u00B7");
                titulo = titulo.replace("&#333;", "\u014D");
                titulo = titulo.replace("&#9834;", "\u266A");
                titulo = titulo.replace("&aacute;", "á");
                titulo = titulo.replace("&oacute;", "ó");
                titulo = titulo.replace("&quot;", "\"");
                titulo = titulo.replace("&Delta;", "\u0394");
                titulo = titulo.replace("&uuml;", "\u00FC");
                titulo = titulo.replace("&szlig;", "\u00DF");
                titulo = titulo.replace("&radic;", "\u221A");
                titulo = titulo.replace("&dagger;", "\u2020");
                titulo = titulo.replace("&hearts;", "\u2665");
                titulosArray.add(titulo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            titulos = new String[titulosArray.size()];
            titulosArray.toArray(titulos);
        }
        return titulos;
    }

    public String[] parseTipos(String json) {
        List<String> capitulosArray = new ArrayList<String>();
        String[] capitulos;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String tid = childJSONObject.getString("tid");
                String numero = childJSONObject.getString("numero");
                if ("Anime".equals(tid)) {
                    capitulosArray.add("anime");
                } else if ("OVA".equals(tid)) {
                    capitulosArray.add("ova");
                } else {
                    capitulosArray.add("pelicula");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            capitulos = new String[capitulosArray.size()];
            capitulosArray.toArray(capitulos);
        }
        return capitulos;
    }

    public String[] parsenumeros(String json) {
        List<String> numerosArray = new ArrayList<String>();
        String[] numeros;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String numero = childJSONObject.getString("numero");
                numerosArray.add(numero);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            numeros = new String[numerosArray.size()];
            numerosArray.toArray(numeros);
        }
        return numeros;
    }

    public String[] parseEID(String json) {
        List<String> eidsArray = new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String eid = childJSONObject.getString("eid");
                eidsArray.add(eid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eids = new String[eidsArray.size()];
            eidsArray.toArray(eids);
        }
        return eids;
    }

    public List<String> parseNumerobyEID(String json) {
        List<String> eidsArray = new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("episodios");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String eid = childJSONObject.getString("num");
                eidsArray.add("Capitulo " + eid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eidsArray;
    }

    public List<String> parseEidsbyEID(String json) {
        List<String> eidsArray = new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("episodios");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String eid = childJSONObject.getString("eid");
                eidsArray.add(eid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eidsArray;
    }

    public List<String> parseTitRel(String json) {
        List<String> eidsArray = new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("relacionados");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String tituloRel = childJSONObject.getString("titulo");
                tituloRel = tituloRel.replace("&#039;", "\'");
                tituloRel = tituloRel.replace("&iacute;", "í");
                tituloRel = tituloRel.replace("&deg;", "°");
                tituloRel = tituloRel.replace("&amp;", "&");
                tituloRel = tituloRel.replace("&acirc;", "\u00C2");
                tituloRel = tituloRel.replace("&egrave;", "\u00E8");
                tituloRel = tituloRel.replace("&middot;", "\u00B7");
                tituloRel = tituloRel.replace("&#333;", "\u014D");
                tituloRel = tituloRel.replace("&#9834;", "\u266A");
                tituloRel = tituloRel.replace("&aacute;", "á");
                tituloRel = tituloRel.replace("&oacute;", "ó");
                tituloRel = tituloRel.replace("&quot;", "\"");
                tituloRel = tituloRel.replace("&Delta;", "\u0394");
                tituloRel = tituloRel.replace("&uuml;", "\u00FC");
                tituloRel = tituloRel.replace("&szlig;", "\u00DF");
                tituloRel = tituloRel.replace("&radic;", "\u221A");
                tituloRel = tituloRel.replace("&dagger;", "\u2020");
                tituloRel = tituloRel.replace("&hearts;", "\u2665");
                eidsArray.add(tituloRel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eidsArray;
    }

    public List<String> parseTiposRel(String json) {
        List<String> eidsArray = new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("relacionados");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String rel_tipo = childJSONObject.getString("rel_tipo");
                String tid = childJSONObject.getString("tid");
                eidsArray.add(rel_tipo + " - " + tid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eidsArray;
    }

    public String[] urlsRel(String json) {
        List<String> urlArray = new ArrayList<String>();
        String[] urls;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("relacionados");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String url = "http://cdn.animeflv.net/img/portada/thumb_80/" + childJSONObject.getString("aid") + ".jpg";
                urlArray.add(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urls = new String[urlArray.size()];
            urlArray.toArray(urls);
        }
        return urls;
    }

    public String[] parseAidRel(String json) {
        List<String> urlArray = new ArrayList<String>();
        String[] urls;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("relacionados");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String url = childJSONObject.getString("aid");
                urlArray.add(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urls = new String[urlArray.size()];
            urlArray.toArray(urls);
        }
        return urls;
    }

    public String getTit(String json) {
        String aid = "";
        try {
            JSONObject jsonObj = new JSONObject(json);
            aid = jsonObj.getString("titulo");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FileUtil.corregirTit(aid);
    }

    public String[] parseAID(String json) {
        List<String> aidsArray = new ArrayList<String>();
        String[] aids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String eid = childJSONObject.getString("aid");
                aidsArray.add(eid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            aids = new String[aidsArray.size()];
            aidsArray.toArray(aids);
        }
        return aids;
    }

    public List<AnimeClass> DirAnimes(String json) {
        List<AnimeClass> linkArray = new ArrayList<AnimeClass>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String tipo = object.getString("c");
                if (tipo.equals("Anime")) {
                    String nombre = corregirTit(object.getString("b"));
                    String aid = object.getString("a");
                    String url = "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
                    linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                }
            }
        } catch (Exception e) {
            Log.e("DirAnimes", e.getMessage(), e.getCause());
        }
        return linkArray;
    }

    public List<AnimeClass> DirOvas(String json) {
        List<AnimeClass> linkArray = new ArrayList<AnimeClass>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String tipo = object.getString("c");
                if (tipo.equals("OVA")) {
                    String nombre = corregirTit(object.getString("b"));
                    String aid = object.getString("a");
                    String url = "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
                    linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                }
            }
        } catch (Exception e) {
            Log.e("DirOvas", e.getMessage(), e.getCause());
        }
        return linkArray;
    }

    public List<AnimeClass> DirPelis(String json) {
        List<AnimeClass> linkArray = new ArrayList<AnimeClass>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String tipo = object.getString("c");
                if (tipo.equals("Pelicula")) {
                    String nombre = corregirTit(object.getString("b"));
                    String aid = object.getString("a");
                    String url = "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
                    linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                }
            }
        } catch (Exception e) {
            Log.e("DirPeliculas", e.getMessage(), e.getCause());
        }
        return linkArray;
    }

    public String corregirTit(String tit) {
        String array = tit;
        array = array.replace("[\"", "");
        array = array.replace("\"]", "");
        array = array.replace("\",\"", ":::");
        array = array.replace("\\/", "/");
        array = array.replace("â\u0098\u0086", "\u2606");
        array = array.replace("&#039;", "\'");
        array = array.replace("&iacute;", "í");
        array = array.replace("&deg;", "°");
        array = array.replace("&amp;", "&");
        array = array.replace("&acirc;", "\u00C2");
        array = array.replace("&egrave;", "\u00E8");
        array = array.replace("&middot;", "\u00B7");
        array = array.replace("&#333;", "\u014D");
        array = array.replace("&#9834;", "\u266A");
        array = array.replace("&aacute;", "á");
        array = array.replace("&oacute;", "ó");
        array = array.replace("&quot;", "\"");
        array = array.replace("&uuml;", "\u00FC");
        array = array.replace("&szlig;", "\u00DF");
        array = array.replace("&radic;", "\u221A");
        array = array.replace("&dagger;", "\u2020");
        array = array.replace("&hearts;", "\u2665");
        return array;
    }

    public String getTipoAnime(String json) {
        String tipo = "";
        try {
            JSONObject j = new JSONObject(json);
            tipo = j.getString("tid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tipo;
    }

    public String getUrlFavs(String json, String aid) {
        String ret = "null";
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject nombreJ = jsonArray.getJSONObject(i);
                String n = nombreJ.getString("a");
                if (n.trim().equals(aid)) {
                    String tipo = nombreJ.getString("c").toLowerCase();
                    return "http://animeflv.net/" + tipo + "/" + nombreJ.getString("d") + ".html";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String getUrlCached(String aid, String numero) {
        String ret = "null";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String n = nombreJ.getString("a");
                    if (n.trim().equals(aid)) {
                        return "http://animeflv.net/ver/" + nombreJ.getString("d") + "-" + numero + ".html";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public String getUrlCached(String eid) {
        String[] data = eid.replace("E", "").split("_");
        String aid = data[0];
        String numero = data[1];
        String ret = "null";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String n = nombreJ.getString("a");
                    if (n.trim().equals(aid)) {
                        return "http://animeflv.net/ver/" + nombreJ.getString("d") + "-" + numero + ".html";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public String getUrlAnimeCached(String aid) {
        String ret = "null";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String n = nombreJ.getString("a");
                    if (n.trim().equals(aid)) {
                        return "http://animeflv.net/" + nombreJ.getString("c").toLowerCase() + "/" + nombreJ.getString("d") + ".html";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public String getTitCached(String aid) {
        String ret = "null";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String n = nombreJ.getString("a");
                    if (n.trim().equals(aid)) {
                        return FileUtil.corregirTit(nombreJ.getString("b"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return FileUtil.corregirTit(ret);
    }

    public String getAidCached(String tipe, String url) {
        String ret = "0";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONArray jsonArray = new JSONArray(getStringFromFile(file_loc));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String tipo = nombreJ.getString("c").toLowerCase();
                    String corto = nombreJ.getString("d");
                    if (tipe.trim().equals(tipo.trim()) && corto.trim().equals(url.trim())) {
                        return corregirTit(nombreJ.getString("a"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public String getAidCached(String url) {
        String ret = "0";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String corto = nombreJ.getString("d");
                    if (corto.trim().equals(url.trim())) {
                        return corregirTit(nombreJ.getString("a"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
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

    public int checkStatus(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return Integer.parseInt(jsonObject.getString("cache"));
        } catch (Exception e) {
            return 0;
        }
    }

    public int checkStatus(JSONObject json) {
        try {
            return Integer.parseInt(json.getString("cache"));
        } catch (Exception e) {
            return 0;
        }
    }

    public String getUserFavs(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("favs");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserVistos(String json) {
        String response = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            response = jsonObject.getString("vistos");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Intent getPrefIntPlayer(Context context) {
        int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_player", "0"));
        Intent intent;
        switch (type) {
            case 0:
                intent = new Intent(context, PlayerSimple.class);
                break;
            case 1:
                intent = new Intent(context, Player.class);
                break;
            default:
                intent = new Intent(context, PlayerExoSimple.class);
                break;
        }
        return intent;
    }

    public void saveBackup(Context context) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email_login", PreferenceManager.getDefaultSharedPreferences(context).getString("login_email", "null"));
            jsonObject.put("email_coded", PreferenceManager.getDefaultSharedPreferences(context).getString("login_email_coded", "null"));
            jsonObject.put("pass_coded", PreferenceManager.getDefaultSharedPreferences(context).getString("login_pass_coded", "null"));
            jsonObject.put("accentColor", PreferenceManager.getDefaultSharedPreferences(context).getInt("accentColor", ColorsRes.Naranja(context)));
            jsonObject.put("favoritos", context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", ""));
            jsonObject.put("vistos", context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", ""));
            JSONArray jsonArray = new JSONArray();
            JSONObject not = new JSONObject();
            not.put("name", "notificaciones");
            not.put("value", PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones", true));
            JSONObject tmp = new JSONObject();
            tmp.put("name", "tiempo");
            tmp.put("value", PreferenceManager.getDefaultSharedPreferences(context).getString("tiempo", "60000"));
            JSONObject sonido = new JSONObject();
            sonido.put("name", "sonido");
            sonido.put("value", PreferenceManager.getDefaultSharedPreferences(context).getString("sonido", "0"));
            JSONObject conx = new JSONObject();
            conx.put("name", "t_conexion");
            conx.put("value", PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "2"));
            JSONObject tbus = new JSONObject();
            tbus.put("name", "t_busqueda");
            tbus.put("value", PreferenceManager.getDefaultSharedPreferences(context).getString("t_busqueda", "0"));
            JSONObject resaltar = new JSONObject();
            resaltar.put("name", "resaltar");
            resaltar.put("value", PreferenceManager.getDefaultSharedPreferences(context).getBoolean("resaltar", true));
            JSONObject autodes = new JSONObject();
            autodes.put("name", "autoDesc");
            autodes.put("value", PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoDesc", false));
            JSONObject autoUp = new JSONObject();
            autoUp.put("name", "autoUpdate");
            autoUp.put("value", PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoUpdate", false));
            JSONObject playVid = new JSONObject();
            playVid.put("name", "t_video");
            playVid.put("value", PreferenceManager.getDefaultSharedPreferences(context).getString("t_video", "0"));
            JSONObject playStream = new JSONObject();
            playStream.put("name", "t_streaming");
            playStream.put("value", PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
            JSONObject isAmoled = new JSONObject();
            isAmoled.put("name", "is_amoled");
            isAmoled.put("value", PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false));
            jsonArray.put(not);
            jsonArray.put(tmp);
            jsonArray.put(sonido);
            jsonArray.put(conx);
            jsonArray.put(tbus);
            jsonArray.put(resaltar);
            jsonArray.put(autodes);
            jsonArray.put(autoUp);
            jsonArray.put(playVid);
            jsonArray.put(playStream);
            jsonArray.put(isAmoled);
            jsonObject.put("preferencias", jsonArray);
            File saveData = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/data.save");
            if (saveData.exists()) {
                writeToFile(jsonObject.toString(), saveData);
            } else {
                if (saveData.createNewFile()) {
                    writeToFile(jsonObject.toString(), saveData);
                } else {
                    Log.e("---->", "Error al crear respaldo");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response restoreBackup(String json, Context context) {
        try {
            JSONObject j = new JSONObject(json);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email", j.getString("email_login")).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email_coded", j.getString("email_coded")).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_pass_coded", j.getString("pass_coded")).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("accentColor", j.getInt("accentColor")).apply();
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("favoritos", j.getString("favoritos")).apply();
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", j.getString("vistos")).apply();
            JSONArray jsonArray = j.getJSONArray("preferencias");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonA = jsonArray.getJSONObject(i);
                String name = jsonA.getString("name");
                Object value = jsonA.get("value");
                if (value instanceof String) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(name, (String) value).apply();
                } else if (value instanceof Boolean) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(name, (Boolean) value).apply();
                }
            }
            return Response.OK;
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ERROR;
        }
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

    public void refreshUrls(Context c) {
        new back(c, TaskType.ACT_LIKNS).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/links.html");
    }

    public String getBaseUrl(TaskType taskType, Context context) {
        String url;
        if (taskType == TaskType.NORMAL) {
            url = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("dir_base", "http://animeflvapps.x10.mx/");
        } else {
            url = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("dir_base_back", "http://animeflvapp.site88.net/");
        }
        return url;
    }

    public String getInicioUrl(TaskType taskType, Context context) {
        String url;
        if (taskType == TaskType.NORMAL) {
            url = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("dir_inicio", "http://animeflvapps.x10.mx/getHtml.php");
        } else {
            url = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("dir_inicio_back", "http://animeflvapp.site88.net/getHtml.php");
        }
        return url;
    }

    public String getDirectorioUrl(TaskType taskType, Context context) {
        String url;
        if (taskType == TaskType.NORMAL) {
            url = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("dir_directorio", "http://animeflvapps.x10.mx/directorio.php");
        } else {
            url = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("dir_directorio_back", "http://animeflvapp.site88.net/directorio.php");
        }
        return url;
    }

    public enum Response {
        OK(0),
        ERROR(1);
        int value;

        Response(int value) {
            this.value = value;
        }
    }

    public class back extends AsyncTask<String, String, String> {
        Context context;
        TaskType taskType;
        String _response;

        public back(Context c, TaskType t) {
            this.context = c;
            this.taskType = t;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();
            HttpURLConnection c = null;
            try {
                String cookies = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("cookies", "");
                URL u = new URL(params[0]);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                c.setRequestProperty("Accept", "*/*");
                c.setRequestProperty("Cookie", cookies.trim().substring(0, cookies.indexOf(";") + 1));
                c.setUseCaches(false);
                c.setConnectTimeout(3000);
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
                if (!c.getURL().toString().contains("fav-server"))
                    if (c.getURL() != u) {
                        if (!c.getURL().toString().trim().startsWith("http://animeflv")) {
                            _response = "error";
                        } else {
                            if (!c.getURL().toString().contains("fav-server"))
                                if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    _response = sb.toString();
                                } else {
                                    _response = "error";
                                }
                        }
                    } else {
                        if (!c.getURL().toString().contains("fav-server"))
                            if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                _response = sb.toString();
                            } else {
                                _response = "error";
                            }
                    }
                //String fullPage = page.asXml();
            } catch (Exception e) {
                _response = "error";
            }
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equals("error") && taskType == TaskType.ACT_LIKNS) {
                try {
                    JSONObject jsonObject = new JSONObject(s.trim());
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_base", jsonObject.getString("base")).apply();
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_base_back", jsonObject.getString("base_back")).apply();
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_inicio", jsonObject.getString("inicio")).apply();
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_inicio_back", jsonObject.getString("inicio_back")).apply();
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_directorio", jsonObject.getString("directorio")).apply();
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_directorio_back", jsonObject.getString("directorio_back")).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
