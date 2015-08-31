package knf.animeflv.Directorio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Requests;
import knf.animeflv.TaskType;
import knf.animeflv.info.AnimeInfo;
import knf.animeflv.info.Info;
import knf.animeflv.info.InfoCap;

/**
 * Created by Jordy on 29/08/2015.
 */
public class Directorio extends AppCompatActivity implements Requests.callback {
    Toolbar toolbarS;
    Menu menuGlobal;
    EditText editText;
    Parser parser=new Parser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directorio);
        toolbarS=(Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbarS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbarS.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getVisibility() == View.GONE) {
                    finish();
                } else {
                    if (editText.getText().length() >= 0) {
                        editText.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        menuGlobal.clear();
                        getMenuInflater().inflate(R.menu.menu_main, menuGlobal);
                        getSupportActionBar().setTitle("Directorio");
                    }
                }
            }
        });
        toolbarS.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.buscar_borrar:
                        if (editText.getText().length() > 0) {
                            editText.setText("");
                            menuGlobal.clear();
                            getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                        }
                        break;
                    case R.id.buscar_cancelar:
                        if (editText.getText().length() == 0) {
                            editText.setVisibility(View.GONE);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            menuGlobal.clear();
                            getMenuInflater().inflate(R.menu.menu_main, menuGlobal);
                            getSupportActionBar().setTitle("Directorio");
                        }
                        break;
                    case R.id.search:
                        getSupportActionBar().setTitle("");
                        editText.setVisibility(View.VISIBLE);
                        editText.setText("");
                        editText.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, 0);
                        menuGlobal.clear();
                        getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                        break;
                }
                return true;
            }
        });
        editText=(EditText) findViewById(R.id.et_busqueda);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    menuGlobal.clear();
                    getMenuInflater().inflate(R.menu.menu_buscar_borrar, menuGlobal);
                } else {
                    menuGlobal.clear();
                    getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
            }
        });
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("ANIMES", Animes.class)
                .add("OVAS", Ovas.class)
                .add("PELICULAS", Peliculas.class)
                .create());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager2);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab2);
        viewPagerTab.setViewPager(viewPager);
        //new Requests(this, TaskType.DIRECTORIO).execute("http://animeflv.net/api.php?accion=directorio");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuGlobal=menu;
        Bundle bundle=getIntent().getExtras();
        if (bundle==null){
            editText.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            menuGlobal.clear();
            getMenuInflater().inflate(R.menu.menu_main, menuGlobal);
            getSupportActionBar().setTitle("Directorio");
        }else {
            getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menu);
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public void sendtext1(String data,TaskType taskType){
        List<String> titulosAnime=parser.DirTitulosAnime(data);
        List<String> indexes=parser.DirIntsAnime(data);
        List<String> titOrdAnime= parser.DirTitulosAnime(data);
        List<Integer> indexOrd=new ArrayList<Integer>();
        Collections.sort(titOrdAnime,String.CASE_INSENSITIVE_ORDER);
        for (String s:titOrdAnime){
            int index=Integer.parseInt(indexes.get(titulosAnime.indexOf(s)));
            Log.d("IntsN","IntDir: "+Integer.toString(index)+" IntDirOrg: "+Integer.toString(titOrdAnime.indexOf(s)));
            indexOrd.add(index);
        }
        Log.d("Dir Titulos", Integer.toString(titulosAnime.size()));
        Log.d("Dir Indexes", Integer.toString(indexes.size()));
        Log.d("Dir Tit 1 ORIGINAL",titulosAnime.get(0));
        Log.d("Dir Tit 1 ALPH",titOrdAnime.get(0));
        Log.d("Dir Int 1 ALPH",Integer.toString(indexOrd.get(0)));
    }
}
