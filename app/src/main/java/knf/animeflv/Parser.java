package knf.animeflv;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jordy on 10/08/2015.
 */
public class Parser {

    public String[] parseLinks(String json){
        List<String> linkArray=new ArrayList<String>();
        String[] urls;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String link = "http://cdn.animeflv.net/img/portada/thumb_80/"+childJSONObject.getString("aid")+".jpg";
                linkArray.add(link);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            urls = new String[linkArray.size()];
            linkArray.toArray(urls);
        }
        return urls;
    }

    public String getUrlPortada(String json){
        String url="";
        try {
            JSONObject jsonObj = new JSONObject(json);
            url = "http://cdn.animeflv.net/img/portada/"+jsonObj.getString("aid")+".jpg";

        }catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
    public String getInfoSinopsis(String json){
        String url="";
        try {
            JSONObject jsonObj = new JSONObject(json);
            url = jsonObj.getString("sinopsis");
            url=url.replace("&aacute;", "\u00E1");
            url=url.replace("&eacute;", "\u00E9");
            url=url.replace("&iacute;","\u00ED");
            url=url.replace("&oacute;","\u00F3");
            url=url.replace("&uacute;","\u00FA");
            url=url.replace("&Aacute;","\u00C1");
            url=url.replace("&Eacute;","\u00C9");
            url=url.replace("&Iacute;","\u00CD");
            url=url.replace("&Oacute;","\u00D3");
            url=url.replace("&Uacute;","\u00DA");
            url=url.replace("&ntilde;","\u00F1");
            url=url.replace("&ldquo;","\u201C");
            url=url.replace("&rdquo;","\u201D");
            url=url.replace("&rsquo;","\u2019");
            url=url.replace("&iquest;","\u00BF");
            url=url.replace("&hellip;","\u2026");
            url=url.replace("&#333;","\u014D");
            url = url.replace("&uuml;", "\u00FC");
            url=url.replace("&iexcl;","¡");
            url=url.replace("&quot;","\"");
            if (url.trim().equals("")){
                url="Sin Sinopsis.";
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getInfotitulo(String json){
        String url="";
        try {
            JSONObject jsonObj = new JSONObject(json);
            url = jsonObj.getString("titulo");
            url=url.replace("&#039;", "\'");
            url=url.replace("&iacute;","í");
            url=url.replace("&deg;","°");
            url=url.replace("&amp;","&");
            url=url.replace("&acirc;", "\u00C2");
            url=url.replace("&egrave;","\u00E8");
            url=url.replace("&middot;","\u00B7");
            url=url.replace("&#333;", "\u014D");
            url=url.replace("&#9834;", "\u266A");
            url=url.replace("&aacute;","á");
            url=url.replace("&oacute;","ó");
            url=url.replace("&quot;","\"");
            url=url.replace("&uuml;","\u00FC");
            url=url.replace("&szlig;","\u00DF");
            url=url.replace("&radic;","\u221A");
            url=url.replace("&dagger;", "\u2020");
            url=url.replace("&hearts;","\u2665");
            url = url.replace("&Delta;", "\u0394");
            url=url.replace("\u00c3\u0097","\u00D7");
            url=url.replace("\u00c2\u00bd","\u00BD");
            url=url.replace("\u00c3\u00b1","\u00F1");
            url=url.replace("\u00c3\u00a4","\u00E4");
            url=url.replace("\u00c3\u00a9","\u00E9");
            url=url.replace("\u00c2\u0096","\u0096");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
    public String getInfoTipo(String json){
        String url="";
        try {
            JSONObject jsonObj = new JSONObject(json);
            url = jsonObj.getString("tid");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
    public String getInfoEstado(String json){
        String url="";
        try {
            JSONObject jsonObj = new JSONObject(json);
            url = jsonObj.getString("fecha_fin");
            if (url.trim().equals("0000-00-00")){
                url="En emision";
            }else {
                url="Terminado";
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
    public String getInfoGeneros(String json){
        String url="";
        try {
            JSONObject jsonObj = new JSONObject(json);
            url = jsonObj.getString("generos");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public String[] parseTitulos(String json){
        List<String> titulosArray=new ArrayList<String>();
        String[] titulos;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String titulo = childJSONObject.getString("titulo");
                titulo=titulo.replace("&#039;","\'");
                titulo=titulo.replace("&iacute;","í");
                titulo=titulo.replace("&deg;","°");
                titulo=titulo.replace("&amp;","&");
                titulo=titulo.replace("&acirc;","\u00C2");
                titulo=titulo.replace("&egrave;","\u00E8");
                titulo=titulo.replace("&middot;","\u00B7");
                titulo=titulo.replace("&#333;", "\u014D");
                titulo=titulo.replace("&#9834;", "\u266A");
                titulo=titulo.replace("&aacute;","á");
                titulo=titulo.replace("&oacute;","ó");
                titulo=titulo.replace("&quot;","\"");
                titulo = titulo.replace("&Delta;", "\u0394");
                titulo=titulo.replace("&uuml;","\u00FC");
                titulo=titulo.replace("&szlig;","\u00DF");
                titulo=titulo.replace("&radic;","\u221A");
                titulo=titulo.replace("&dagger;", "\u2020");
                titulo=titulo.replace("&hearts;","\u2665");
                titulosArray.add(titulo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            titulos = new String[titulosArray.size()];
            titulosArray.toArray(titulos);
        }
        return titulos;
    }

    public String[] parseCapitulos(String json){
        List<String> capitulosArray=new ArrayList<String>();
        String[] capitulos;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String tid = childJSONObject.getString("tid");
                String numero = childJSONObject.getString("numero");
                if ("Anime".equals(tid)){
                    capitulosArray.add("Capitulo "+numero);
                }else if ("OVA".equals(tid)){
                    capitulosArray.add("OVA "+numero);
                }else {
                    capitulosArray.add("Pelicula");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            capitulos = new String[capitulosArray.size()];
            capitulosArray.toArray(capitulos);
        }
        return capitulos;
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

    public String[] parsenumeros(String json){
        List<String> numerosArray=new ArrayList<String>();
        String[] numeros;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String numero = childJSONObject.getString("numero");
                numerosArray.add(numero);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            numeros = new String[numerosArray.size()];
            numerosArray.toArray(numeros);
        }
        return numeros;
    }

    public String[] parseEID(String json){
        List<String> eidsArray=new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String eid = childJSONObject.getString("eid");
                eidsArray.add(eid);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            eids = new String[eidsArray.size()];
            eidsArray.toArray(eids);
        }
        return eids;
    }
    public List<String> parseNumerobyEID(String json){
        List<String> eidsArray=new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("episodios");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String eid = childJSONObject.getString("num");
                eidsArray.add("Capitulo "+eid);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return eidsArray;
    }
    public List<String> parseEidsbyEID(String json){
        List<String> eidsArray=new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("episodios");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String eid = childJSONObject.getString("eid");
                eidsArray.add(eid);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return eidsArray;
    }
    public List<String> parseTitRel(String json){
        List<String> eidsArray=new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("relacionados");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String tituloRel = childJSONObject.getString("titulo");
                tituloRel=tituloRel.replace("&#039;","\'");
                tituloRel=tituloRel.replace("&iacute;","í");
                tituloRel=tituloRel.replace("&deg;","°");
                tituloRel=tituloRel.replace("&amp;","&");
                tituloRel=tituloRel.replace("&acirc;","\u00C2");
                tituloRel=tituloRel.replace("&egrave;","\u00E8");
                tituloRel=tituloRel.replace("&middot;","\u00B7");
                tituloRel=tituloRel.replace("&#333;", "\u014D");
                tituloRel=tituloRel.replace("&#9834;", "\u266A");
                tituloRel=tituloRel.replace("&aacute;","á");
                tituloRel=tituloRel.replace("&oacute;","ó");
                tituloRel=tituloRel.replace("&quot;","\"");
                tituloRel = tituloRel.replace("&Delta;", "\u0394");
                tituloRel=tituloRel.replace("&uuml;","\u00FC");
                tituloRel=tituloRel.replace("&szlig;","\u00DF");
                tituloRel=tituloRel.replace("&radic;","\u221A");
                tituloRel=tituloRel.replace("&dagger;", "\u2020");
                tituloRel=tituloRel.replace("&hearts;","\u2665");
                eidsArray.add(tituloRel);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return eidsArray;
    }
    public List<String> parseTiposRel(String json){
        List<String> eidsArray=new ArrayList<String>();
        String[] eids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("relacionados");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String rel_tipo = childJSONObject.getString("rel_tipo");
                String tid = childJSONObject.getString("tid");
                eidsArray.add(rel_tipo+" - "+tid);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return eidsArray;
    }
    public String[] urlsRel(String json){
        List<String> urlArray=new ArrayList<String>();
        String[] urls;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("relacionados");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String url = "http://cdn.animeflv.net/img/portada/thumb_80/"+childJSONObject.getString("aid")+".jpg";
                urlArray.add(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            urls = new String[urlArray.size()];
            urlArray.toArray(urls);
        }
        return urls;
    }
    public String[] parseAidRel(String json){
        List<String> urlArray=new ArrayList<String>();
        String[] urls;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("relacionados");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String url = childJSONObject.getString("aid");
                urlArray.add(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            urls = new String[urlArray.size()];
            urlArray.toArray(urls);
        }
        return urls;
    }
    public String getAID (String json){
        String aid="";
        try {
            JSONObject jsonObj = new JSONObject(json);
            aid=jsonObj.getString("aid");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return aid;
    }
    public String getTit(String json){
        String aid="";
        try {
            JSONObject jsonObj = new JSONObject(json);
            aid=jsonObj.getString("titulo");
            aid=aid.replace("&#039;", "\'");
            aid=aid.replace("&iacute;", "í");
            aid=aid.replace("&deg;","°");
            aid=aid.replace("&amp;","&");
            aid=aid.replace("&acirc;","\u00C2");
            aid=aid.replace("&egrave;","\u00E8");
            aid=aid.replace("&middot;","\u00B7");
            aid=aid.replace("&#333;", "\u014D");
            aid=aid.replace("&#9834;", "\u266A");
            aid = aid.replace("&Delta;", "\u0394");
            aid=aid.replace("&aacute;","á");
            aid=aid.replace("&oacute;","ó");
            aid=aid.replace("&quot;","\"");
            aid=aid.replace("&uuml;","\u00FC");
            aid=aid.replace("&szlig;","\u00DF");
            aid=aid.replace("&radic;","\u221A");
            aid=aid.replace("&dagger;", "\u2020");
            aid=aid.replace("&hearts;","\u2665");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return aid;
    }

    public String[] parseAID(String json){
        List<String> aidsArray=new ArrayList<String>();
        String[] aids;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String eid = childJSONObject.getString("aid");
                aidsArray.add(eid);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            aids = new String[aidsArray.size()];
            aidsArray.toArray(aids);
        }
        return aids;
    }
    public List<String> DirTitulosAnime(String json,int genero){
        List<String> linkArray=new ArrayList<String>();
        switch (genero){
            case 0:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        array=array.replace("\\/","/");
                        array=array.replace("&#039;","\'");
                        array=array.replace("&iacute;","í");
                        array=array.replace("&deg;","°");
                        array=array.replace("&amp;","&");
                        array=array.replace("&acirc;","\u00C2");
                        array=array.replace("&egrave;","\u00E8");
                        array=array.replace("&middot;","\u00B7");
                        array=array.replace("&#333;", "\u014D");
                        array=array.replace("&#9834;", "\u266A");
                        array=array.replace("&aacute;","á");
                        array=array.replace("&oacute;","ó");
                        array=array.replace("&quot;","\"");
                        array=array.replace("&uuml;","\u00FC");
                        array=array.replace("&szlig;","\u00DF");
                        array=array.replace("&radic;","\u221A");
                        array=array.replace("&dagger;", "\u2020");
                        array=array.replace("&hearts;", "\u2665");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("Anime")) {
                            linkArray.add(sarray[1]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        array=array.replace("\\/","/");
                        array=array.replace("&#039;","\'");
                        array=array.replace("&iacute;","í");
                        array=array.replace("&deg;","°");
                        array=array.replace("&amp;","&");
                        array=array.replace("&acirc;","\u00C2");
                        array=array.replace("&egrave;","\u00E8");
                        array=array.replace("&middot;","\u00B7");
                        array=array.replace("&#333;", "\u014D");
                        array=array.replace("&#9834;", "\u266A");
                        array=array.replace("&aacute;","á");
                        array=array.replace("&oacute;","ó");
                        array=array.replace("&quot;","\"");
                        array=array.replace("&uuml;","\u00FC");
                        array=array.replace("&szlig;","\u00DF");
                        array=array.replace("&radic;","\u221A");
                        array=array.replace("&dagger;", "\u2020");
                        array=array.replace("&hearts;", "\u2665");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("Anime")&&sarray[3].contains(Integer.toString(genero))) {
                            linkArray.add(sarray[1]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }

    public List<String> DirTitulosAnimeA(String json, int genero) {
        List<String> linkArray = new ArrayList<String>();
        switch (genero) {
            case 0:
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = jsonArray.getJSONObject(i);
                        String array = j.getString("b");
                        array = array.replace("[\"", "");
                        array = array.replace("\"]", "");
                        array = array.replace("\",\"", ":::");
                        array = array.replace("\\/", "/");
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
                        array = array.replace("&Delta;", "\u0394");
                        array = array.replace("&uuml;", "\u00FC");
                        array = array.replace("&szlig;", "\u00DF");
                        array = array.replace("&radic;", "\u221A");
                        array = array.replace("&dagger;", "\u2020");
                        array = array.replace("&hearts;", "\u2665");
                        String[] sarray = array.split(":::");
                        String tipo = j.getString("c");
                        if (tipo.equals("Anime")) {
                            linkArray.add(array);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }
    public List<String> DirIntsAnime(String json,int genero){
        List<String> linkArray=new ArrayList<String>();
        switch (genero){
            case 0:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("Anime")) {
                            linkArray.add(sarray[0]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("Anime")&&sarray[3].contains(Integer.toString(genero))) {
                            linkArray.add(sarray[0]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }

    public List<String> DirIntsAnimeA(String json, int genero) {
        List<String> linkArray = new ArrayList<String>();
        switch (genero) {
            case 0:
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = jsonArray.getJSONObject(i);
                        if (j.getString("c").trim().equals("Anime")) {
                            linkArray.add(j.getString("a"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }
    public List<String> DirTitulosOvas(String json,int genero){
        List<String> linkArray=new ArrayList<String>();
        switch (genero){
            case 0:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        array=array.replace("\\/","/");
                        array=array.replace("&#039;","\'");
                        array=array.replace("&iacute;","í");
                        array=array.replace("&deg;","°");
                        array=array.replace("&amp;","&");
                        array=array.replace("&acirc;","\u00C2");
                        array=array.replace("&egrave;","\u00E8");
                        array=array.replace("&middot;","\u00B7");
                        array=array.replace("&#333;", "\u014D");
                        array=array.replace("&#9834;", "\u266A");
                        array=array.replace("&aacute;","á");
                        array=array.replace("&oacute;","ó");
                        array=array.replace("&quot;","\"");
                        array=array.replace("&uuml;","\u00FC");
                        array=array.replace("&szlig;","\u00DF");
                        array=array.replace("&radic;","\u221A");
                        array=array.replace("&dagger;", "\u2020");
                        array=array.replace("&hearts;","\u2665");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("OVA")) {
                            linkArray.add(sarray[1]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        array=array.replace("\\/","/");
                        array=array.replace("&#039;","\'");
                        array=array.replace("&iacute;","í");
                        array=array.replace("&deg;","°");
                        array=array.replace("&amp;","&");
                        array=array.replace("&acirc;","\u00C2");
                        array=array.replace("&egrave;","\u00E8");
                        array=array.replace("&middot;","\u00B7");
                        array=array.replace("&#333;", "\u014D");
                        array=array.replace("&#9834;", "\u266A");
                        array=array.replace("&aacute;","á");
                        array=array.replace("&oacute;","ó");
                        array=array.replace("&quot;","\"");
                        array=array.replace("&uuml;","\u00FC");
                        array=array.replace("&szlig;","\u00DF");
                        array=array.replace("&radic;","\u221A");
                        array=array.replace("&dagger;", "\u2020");
                        array=array.replace("&hearts;","\u2665");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("OVA")&&sarray[3].contains(Integer.toString(genero))) {
                            linkArray.add(sarray[1]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }

        return linkArray;
    }

    public List<String> DirTitulosOvasA(String json, int genero) {
        List<String> linkArray = new ArrayList<String>();
        switch (genero) {
            case 0:
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = jsonArray.getJSONObject(i);
                        String array = j.getString("b");
                        array = array.replace("[\"", "");
                        array = array.replace("\"]", "");
                        array = array.replace("\",\"", ":::");
                        array = array.replace("\\/", "/");
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
                        array = array.replace("&Delta;", "\u0394");
                        array = array.replace("&uuml;", "\u00FC");
                        array = array.replace("&szlig;", "\u00DF");
                        array = array.replace("&radic;", "\u221A");
                        array = array.replace("&dagger;", "\u2020");
                        array = array.replace("&hearts;", "\u2665");
                        String[] sarray = array.split(":::");
                        String tipo = j.getString("c");
                        if (tipo.equals("OVA")) {
                            linkArray.add(array);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }
    public List<String> DirIntsOvas(String json,int genero){
        List<String> linkArray=new ArrayList<String>();
        switch (genero){
            case 0:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("OVA")) {
                            linkArray.add(sarray[0]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("OVA")&&sarray[3].contains(Integer.toString(genero))) {
                            linkArray.add(sarray[0]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }

    public List<String> DirIntsOvasA(String json, int genero) {
        List<String> linkArray = new ArrayList<String>();
        switch (genero) {
            case 0:
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = jsonArray.getJSONObject(i);
                        if (j.getString("c").trim().equals("OVA")) {
                            linkArray.add(j.getString("a"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }
    public List<String> DirTitulosPelicula(String json,int genero){
        List<String> linkArray=new ArrayList<String>();
        switch (genero){
            case 0:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        array=array.replace("\\/","/");
                        array=array.replace("&#039;","\'");
                        array=array.replace("&iacute;","í");
                        array=array.replace("&deg;","°");
                        array=array.replace("&amp;","&");
                        array=array.replace("&acirc;","\u00C2");
                        array=array.replace("&egrave;","\u00E8");
                        array=array.replace("&middot;","\u00B7");
                        array=array.replace("&#333;", "\u014D");
                        array=array.replace("&#9834;", "\u266A");
                        array=array.replace("&aacute;","á");
                        array=array.replace("&oacute;","ó");
                        array=array.replace("&quot;","\"");
                        array=array.replace("&uuml;","\u00FC");
                        array=array.replace("&szlig;","\u00DF");
                        array=array.replace("&radic;","\u221A");
                        array=array.replace("&dagger;", "\u2020");
                        array=array.replace("&hearts;","\u2665");
                        array=array.replace("\u00c3\u00b1","ñ");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("Pelicula")) {
                            linkArray.add(sarray[1]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        array=array.replace("\\/","/");
                        array=array.replace("&#039;","\'");
                        array=array.replace("&iacute;","í");
                        array=array.replace("&deg;","°");
                        array=array.replace("&amp;","&");
                        array=array.replace("&quot;","\"");
                        array=array.replace("&acirc;","\u00C2");
                        array=array.replace("&egrave;","\u00E8");
                        array=array.replace("&middot;","\u00B7");
                        array=array.replace("&#333;", "\u014D");
                        array=array.replace("&#9834;", "\u266A");
                        array=array.replace("&aacute;","á");
                        array=array.replace("&oacute;","ó");
                        array=array.replace("&uuml;","\u00FC");
                        array=array.replace("&szlig;","\u00DF");
                        array=array.replace("&radic;","\u221A");
                        array=array.replace("&dagger;", "\u2020");
                        array=array.replace("&hearts;","\u2665");
                        array=array.replace("\u00c3\u00b1","ñ");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("Pelicula")&&sarray[3].contains(Integer.toString(genero))) {
                            linkArray.add(sarray[1]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }

    public List<String> DirTitulosPeliculaA(String json, int genero) {
        List<String> linkArray = new ArrayList<String>();
        switch (genero) {
            case 0:
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = jsonArray.getJSONObject(i);
                        String array = j.getString("b");
                        array = array.replace("[\"", "");
                        array = array.replace("\"]", "");
                        array = array.replace("\",\"", ":::");
                        array = array.replace("\\/", "/");
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
                        array = array.replace("&Delta;", "\u0394");
                        array = array.replace("&uuml;", "\u00FC");
                        array = array.replace("&szlig;", "\u00DF");
                        array = array.replace("&radic;", "\u221A");
                        array = array.replace("&dagger;", "\u2020");
                        array = array.replace("&hearts;", "\u2665");
                        String[] sarray = array.split(":::");
                        String tipo = j.getString("c");
                        if (tipo.equals("Pelicula")) {
                            linkArray.add(array);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }
    public List<String> DirIntsPelicula(String json,int genero){
        List<String> linkArray=new ArrayList<String>();
        switch (genero){
            case 0:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("Pelicula")) {
                            linkArray.add(sarray[0]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String array = jsonArray.getString(i);
                        array=array.replace("[\"","");
                        array=array.replace("\"]","");
                        array=array.replace("\",\"",":::");
                        String[] sarray=array.split(":::");
                        if (sarray[2].trim().equals("Pelicula")&&sarray[3].contains(Integer.toString(genero))) {
                            linkArray.add(sarray[0]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }

    public List<String> DirIntsPeliculaA(String json, int genero) {
        List<String> linkArray = new ArrayList<String>();
        switch (genero) {
            case 0:
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = jsonArray.getJSONObject(i);
                        if (j.getString("c").trim().equals("Pelicula")) {
                            linkArray.add(j.getString("a"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return linkArray;
    }
    public List<String> DirTitulosBusqueda(String json,@Nullable String busqueda){
        List<String> linkArray=new ArrayList<String>();
        if (busqueda==null) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array=array.replace("[\"","");
                    array=array.replace("\"]","");
                    array=array.replace("\",\"",":::");
                    array=array.replace("\\/","/");
                    array=array.replace("&#039;","\'");
                    array=array.replace("&iacute;","í");
                    array=array.replace("&deg;","°");
                    array=array.replace("&amp;","&");
                    array=array.replace("&quot;","\"");
                    array=array.replace("&acirc;","\u00C2");
                    array=array.replace("&egrave;","\u00E8");
                    array=array.replace("&middot;","\u00B7");
                    array=array.replace("&#333;", "\u014D");
                    array=array.replace("&#9834;", "\u266A");
                    array=array.replace("&aacute;","á");
                    array=array.replace("&oacute;","ó");
                    array=array.replace("&uuml;","\u00FC");
                    array=array.replace("&szlig;","\u00DF");
                    array=array.replace("&radic;","\u221A");
                    array=array.replace("&dagger;", "\u2020");
                    array=array.replace("&hearts;","\u2665");
                    String[] sarray = array.split(":::");
                    linkArray.add(sarray[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array=array.replace("[\"","");
                    array=array.replace("\"]","");
                    array=array.replace("\",\"",":::");
                    array=array.replace("\\/","/");
                    array=array.replace("&#039;","\'");
                    array=array.replace("&iacute;","í");
                    array=array.replace("&iacute;","í");
                    array=array.replace("&deg;","°");
                    array=array.replace("&amp;","&");
                    array=array.replace("&quot;","\"");
                    array=array.replace("&acirc;","\u00C2");
                    array=array.replace("&egrave;","\u00E8");
                    array=array.replace("&middot;","\u00B7");
                    array=array.replace("&#333;", "\u014D");
                    array=array.replace("&#9834;", "\u266A");
                    array=array.replace("&aacute;","á");
                    array=array.replace("&oacute;","ó");
                    array=array.replace("&uuml;","\u00FC");
                    array=array.replace("&szlig;","\u00DF");
                    array=array.replace("&radic;","\u221A");
                    array=array.replace("&dagger;", "\u2020");
                    array=array.replace("&hearts;","\u2665");
                    String[] sarray = array.split(":::");
                    if (sarray[1].trim().toLowerCase().contains(busqueda.toLowerCase())){
                        linkArray.add(sarray[1]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return linkArray;
    }

    public List<String> DirTitulosBusquedaA(String json, @Nullable String busqueda) {
        List<String> linkArray = new ArrayList<String>();
        if (busqueda == null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = new JSONObject(jsonArray.getString(i));
                    String array = j.getString("b");
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    array = array.replace("&#039;", "\'");
                    array = array.replace("&iacute;", "í");
                    array = array.replace("&deg;", "°");
                    array = array.replace("&amp;", "&");
                    array = array.replace("&quot;", "\"");
                    array = array.replace("&acirc;", "\u00C2");
                    array = array.replace("&egrave;", "\u00E8");
                    array = array.replace("&middot;", "\u00B7");
                    array = array.replace("&#333;", "\u014D");
                    array = array.replace("&#9834;", "\u266A");
                    array = array.replace("&Delta;", "\u0394");
                    array = array.replace("&aacute;", "á");
                    array = array.replace("&oacute;", "ó");
                    array = array.replace("&uuml;", "\u00FC");
                    array = array.replace("&szlig;", "\u00DF");
                    array = array.replace("&radic;", "\u221A");
                    array = array.replace("&dagger;", "\u2020");
                    array = array.replace("&hearts;", "\u2665");
                    String[] sarray = array.split(":::");
                    linkArray.add(array);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = new JSONObject(jsonArray.getString(i));
                    String array = j.getString("b");
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    array = array.replace("&#039;", "\'");
                    array = array.replace("&iacute;", "í");
                    array = array.replace("&iacute;", "í");
                    array = array.replace("&deg;", "°");
                    array = array.replace("&amp;", "&");
                    array = array.replace("&quot;", "\"");
                    array = array.replace("&acirc;", "\u00C2");
                    array = array.replace("&egrave;", "\u00E8");
                    array = array.replace("&middot;", "\u00B7");
                    array = array.replace("&#333;", "\u014D");
                    array = array.replace("&#9834;", "\u266A");
                    array = array.replace("&aacute;", "á");
                    array = array.replace("&oacute;", "ó");
                    array = array.replace("&uuml;", "\u00FC");
                    array = array.replace("&szlig;", "\u00DF");
                    array = array.replace("&radic;", "\u221A");
                    array = array.replace("&dagger;", "\u2020");
                    array = array.replace("&hearts;", "\u2665");
                    String[] sarray = array.split(":::");
                    if (array.toLowerCase().contains(busqueda.toLowerCase())) {
                        linkArray.add(array);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return linkArray;
    }
    public List<String> DirIndexBusqueda(String json,@Nullable String busqueda){
        List<String> linkArray=new ArrayList<String>();
        if (busqueda==null) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    String[] sarray = array.split(":::");
                    linkArray.add(sarray[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    String[] sarray = array.split(":::");
                    if (sarray[1].trim().toLowerCase().contains(busqueda.toLowerCase())){
                        linkArray.add(sarray[0]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return linkArray;
    }

    public List<String> DirIndexBusquedaA(String json, @Nullable String busqueda) {
        List<String> linkArray = new ArrayList<String>();
        if (busqueda == null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    JSONObject j = jsonArray.getJSONObject(i);
                    String index = j.getString("a");
                    String[] sarray = array.split(":::");
                    linkArray.add(index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    String[] sarray = array.split(":::");
                    JSONObject j = jsonArray.getJSONObject(i);
                    if (j.getString("b").toLowerCase().contains(busqueda.toLowerCase())) {
                        linkArray.add(j.getString("a"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return linkArray;
    }
    public List<String> DirTiposBusqueda(String json,@Nullable String busqueda){
        List<String> linkArray=new ArrayList<String>();
        if (busqueda==null) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    String[] sarray = array.split(":::");
                    linkArray.add(sarray[2]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    String[] sarray = array.split(":::");
                    if (sarray[1].trim().toLowerCase().contains(busqueda.toLowerCase())){
                        linkArray.add(sarray[2]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return linkArray;
    }

    public List<String> DirTiposBusquedaA(String json, @Nullable String busqueda) {
        List<String> linkArray = new ArrayList<String>();
        if (busqueda == null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    String[] sarray = array.split(":::");
                    JSONObject j = jsonArray.getJSONObject(i);
                    linkArray.add(j.getString("c"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String array = jsonArray.getString(i);
                    array = array.replace("[\"", "");
                    array = array.replace("\"]", "");
                    array = array.replace("\",\"", ":::");
                    array = array.replace("\\/", "/");
                    String[] sarray = array.split(":::");
                    JSONObject j = jsonArray.getJSONObject(i);
                    if (j.getString("b").trim().toLowerCase().contains(busqueda.toLowerCase())) {
                        linkArray.add(j.getString("c"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return linkArray;
    }

    public String getUrlAnime(String json, String nombre) {
        String ret = "";
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject nombreJ = jsonArray.getJSONObject(i);
                String n = nombreJ.getString("a");
                if (n.trim().equals(nombre)) {
                    return "http://animeflv.net/anime/" + nombreJ.getString("d") + ".html";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String getUrlOva(String json, String nombre) {
        String ret = "";
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject nombreJ = jsonArray.getJSONObject(i);
                String n = nombreJ.getString("a");
                if (n.trim().equals(nombre)) {
                    return "http://animeflv.net/ova/" + nombreJ.getString("d") + ".html";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String getUrlPelicula(String json, String nombre) {
        String ret = "";
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject nombreJ = jsonArray.getJSONObject(i);
                String n = nombreJ.getString("a");
                if (n.trim().equals(nombre)) {
                    return "http://animeflv.net/pelicula/" + nombreJ.getString("d") + ".html";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
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
        String ret = "";
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

    public int checkStatus(String json) {
        int status = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            status = Integer.parseInt(jsonObject.getString("cache"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public String getUserCont(String json) {
        String response = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            response = jsonObject.getString("cont");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public String getUserFavs(String json) {
        String response = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            response = jsonObject.getString("favs");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
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

    public void saveBackup(Context context) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email_login", PreferenceManager.getDefaultSharedPreferences(context).getString("login_email", "null"));
            jsonObject.put("email_coded", PreferenceManager.getDefaultSharedPreferences(context).getString("login_email_coded", "null"));
            jsonObject.put("pass_coded", PreferenceManager.getDefaultSharedPreferences(context).getString("login_pass_coded", "null"));
            jsonObject.put("color", context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("color", 0));
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
            conx.put("value", PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "0"));
            JSONObject tbus = new JSONObject();
            tbus.put("name", "t_busqueda");
            tbus.put("value", PreferenceManager.getDefaultSharedPreferences(context).getString("t_busqueda", "0"));
            JSONObject stream = new JSONObject();
            stream.put("name", "streaming");
            stream.put("value", PreferenceManager.getDefaultSharedPreferences(context).getBoolean("streaming", false));
            JSONObject resaltar = new JSONObject();
            resaltar.put("name", "resaltar");
            resaltar.put("value", PreferenceManager.getDefaultSharedPreferences(context).getBoolean("resaltar", true));
            JSONObject autodes = new JSONObject();
            autodes.put("name", "autoDesc");
            autodes.put("value", PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoDesc", false));
            JSONObject autoUp = new JSONObject();
            autoUp.put("name", "autoUpdate");
            autoUp.put("value", PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoUpdate", false));
            jsonArray.put(not);
            jsonArray.put(tmp);
            jsonArray.put(sonido);
            jsonArray.put(conx);
            jsonArray.put(tbus);
            jsonArray.put(stream);
            jsonArray.put(resaltar);
            jsonArray.put(autodes);
            jsonArray.put(autoUp);
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
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("color", j.getInt("color")).apply();
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
    public Boolean igual(String json, String paid){
        String aid="";
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            JSONObject childJSONObject = jsonArray.getJSONObject(0);
            aid = childJSONObject.getString("aid");
                    }catch (Exception e) {
            e.printStackTrace();
        }
        return paid.equals(aid);
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
