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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterEmision;

/**
 * Created by Jordy on 05/03/2016.
 */
public class DayFragment extends Fragment {
    View rootview;
    int diaCode;
    ArrayList<String> aids;
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
            aids = bundle.getStringArrayList("list");
        } else {
            diaCode = 0;
            aids = new ArrayList<>();
        }
        Set<String> ongoing = preferences.getStringSet("ongoingSet", new HashSet<String>());
        List<TimeCompareModel> list = new ArrayList<>();
        if (!ongoing.isEmpty()) {
            if (!aids.isEmpty()) {
                for (String aid : aids) {
                    list.add(new TimeCompareModel(aid, getActivity()));
                }
            } else {
                list.add(new TimeCompareModel());
            }
        } else {
            list.add(new TimeCompareModel());
        }
        if (diaCode == getActualDayCode()) {
            emision = new AdapterEmision(getActivity(), list, true);
        } else {
            emision = new AdapterEmision(getActivity(), list);
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(emision);
        return rootview;
    }

    private int getActualDayCode() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int code;
        switch (day) {
            case Calendar.MONDAY:
                code = 1;
                break;
            case Calendar.TUESDAY:
                code = 2;
                break;
            case Calendar.WEDNESDAY:
                code = 3;
                break;
            case Calendar.THURSDAY:
                code = 4;
                break;
            case Calendar.FRIDAY:
                code = 5;
                break;
            case Calendar.SATURDAY:
                code = 6;
                break;
            case Calendar.SUNDAY:
                code = 7;
                break;
            default:
                code = 0;
                break;
        }
        return code;
    }

    @Override
    public void onResume() {
        super.onResume();
        String postfavs = preferences.getString("favoritos", "");
        if (shouldExecuteOnResume && !favs.equals(postfavs)) {
            Set<String> ongoing = preferences.getStringSet("ongoingSet", new HashSet<String>());
            List<TimeCompareModel> list = new ArrayList<>();
            if (!ongoing.isEmpty()) {
                if (!aids.isEmpty()) {
                    for (String aid : aids) {
                        list.add(new TimeCompareModel(aid, getActivity()));
                    }
                } else {
                    list.add(new TimeCompareModel());
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
