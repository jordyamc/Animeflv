package knf.animeflv.Emision.Section;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import knf.animeflv.Emision.DateCompare;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterEmision;

/**
 * Created by Jordy on 05/03/2016.
 */
public class DayFragment extends Fragment {
    View rootview;
    int diaCode;
    SharedPreferences preferences;
    RecyclerView recyclerView;
    AdapterEmision emision;
    boolean shouldExecuteOnResume;
    String favs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.directorio_animes, container, false);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("is_amoled", false))
            rootview.setBackgroundColor(getResources().getColor(android.R.color.black));
        recyclerView = (RecyclerView) rootview.findViewById(R.id.rv_animes);
        preferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        favs = preferences.getString("favoritos", "");
        shouldExecuteOnResume = false;
        Bundle bundle = getArguments();
        if (bundle != null) {
            diaCode = bundle.getInt("code", 0);
        } else {
            diaCode = 0;
        }
        Set<String> ongoing = preferences.getStringSet("ongoingSet", new HashSet<String>());
        List<TimeCompareModel> list = new ArrayList<>();
        if (!ongoing.isEmpty()) {
            for (String aid : ongoing) {
                TimeCompareModel compareModel = new TimeCompareModel(aid, getActivity());
                boolean isthisday = preferences.getInt(aid + "onday", 0) == diaCode + 1 && compareModel.getTime().contains("AM") && UTCtoLocal(compareModel.getTime()).contains("PM");
                boolean isotherday = compareModel.getTime().contains("AM") && UTCtoLocal(compareModel.getTime()).contains("PM");
                if (preferences.getInt(aid + "onday", 0) == diaCode) {
                    if (!isotherday) {
                        list.add(compareModel);
                    }
                }
                if (isthisday) {
                    list.add(compareModel);
                }
            }
            if (list.isEmpty()) {
                list.add(new TimeCompareModel());
            } else {
                Collections.sort(list, new DateCompare());
            }
        } else {
            list.add(new TimeCompareModel());
        }
        emision = new AdapterEmision(getActivity(), list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(emision);
        return rootview;
    }

    private String UTCtoLocal(String utc) {
        String convert = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("~hh:mmaa", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(utc);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            convert = simpleDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
            convert = utc + "-UTC--->" + e.getMessage();
        }
        return convert;
    }

    @Override
    public void onResume() {
        super.onResume();
        String postfavs = preferences.getString("favoritos", "");
        if (shouldExecuteOnResume && !favs.equals(postfavs)) {
            Set<String> ongoing = preferences.getStringSet("ongoingSet", new HashSet<String>());
            List<TimeCompareModel> list = new ArrayList<>();
            if (!ongoing.isEmpty()) {
                for (String aid : ongoing) {
                    if (preferences.getInt(aid + "onday", 0) == diaCode) {
                        list.add(new TimeCompareModel(aid, getActivity()));
                    }
                }
                if (list.isEmpty()) {
                    list.add(new TimeCompareModel());
                } else {
                    Collections.sort(list, new DateCompare());
                }
            } else {
                list.add(new TimeCompareModel());
            }
            emision = new AdapterEmision(getActivity(), list);
            recyclerView.setAdapter(emision);
            favs = postfavs;
        } else {
            shouldExecuteOnResume = true;
        }
    }
}
