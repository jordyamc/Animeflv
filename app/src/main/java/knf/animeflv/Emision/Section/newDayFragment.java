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
import java.util.List;

import knf.animeflv.Emision.EmisionChecker;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterEmision;

/**
 * Created by Jordy on 05/03/2016.
 */
public class newDayFragment extends Fragment {
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
        } else {
            diaCode = 0;
        }
        List<TimeCompareModel> list = getList();
        if (list.isEmpty()) {
            list.add(new TimeCompareModel());
        }
        emision = new AdapterEmision(getActivity(), list, diaCode == getActualDayCode());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(emision);
        return rootview;
    }

    private List<TimeCompareModel> getList() {
        List<TimeCompareModel> list = new ArrayList<>();
        switch (diaCode) {
            case 1:
                list = EmisionChecker.getLcode1();
                break;
            case 2:
                list = EmisionChecker.getLcode2();
                break;
            case 3:
                list = EmisionChecker.getLcode3();
                break;
            case 4:
                list = EmisionChecker.getLcode4();
                break;
            case 5:
                list = EmisionChecker.getLcode5();
                break;
            case 6:
                list = EmisionChecker.getLcode6();
                break;
            case 7:
                list = EmisionChecker.getLcode7();
                break;
        }
        return list;
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
            emision.updatefavs();
            favs = postfavs;
        } else {
            shouldExecuteOnResume = true;
        }
    }
}
