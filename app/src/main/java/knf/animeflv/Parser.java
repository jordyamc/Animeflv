package knf.animeflv;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

    public String[] parseTitulos(String json){
        List<String> titulosArray=new ArrayList<String>();
        String[] titulos;
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray = jsonObj.getJSONArray("lista");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                String titulo = childJSONObject.getString("titulo");
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
                }else {
                    capitulosArray.add("OVA "+numero);
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
}
