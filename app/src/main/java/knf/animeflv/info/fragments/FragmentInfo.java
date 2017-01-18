package knf.animeflv.info.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.CustomViews.TextViewExpandableAnimation;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterRel;
import knf.animeflv.ServerReload.Adapter.CustomRecycler;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.AnimeDetail;

public class FragmentInfo extends Fragment {
    @BindView(R.id.info_descripcion)
    TextViewExpandableAnimation txt_sinopsis;
    @BindView(R.id.titulo)
    TextView txt_titulo;
    @BindView(R.id.tipo)
    TextView txt_tipo;
    @BindView(R.id.fsalida)
    TextView txt_fsalida;
    @BindView(R.id.estado)
    TextView txt_estado;
    @BindView(R.id.generos)
    TextView txt_generos;
    @BindView(R.id.debug_info)
    TextView txt_debug;
    @BindView(R.id.rv_relacionados)
    CustomRecycler rv_rel;

    @BindView(R.id.nested)
    NestedScrollView nestedScrollView;

    private WeakReference<Activity> activityWeakReference;

    public FragmentInfo() {
    }

    public static FragmentInfo get(String aid, String json) {
        Bundle bundle = new Bundle();
        bundle.putString("aid", aid);
        bundle.putString("json", json);
        FragmentInfo fragment = new FragmentInfo();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_info_f_info, container, false);
        ButterKnife.bind(this, view);
        if (ThemeUtils.isAmoled(getActivity())) {
            view.setBackgroundColor(ColorsRes.Negro(getActivity()));
            txt_sinopsis.setTextColor(getResources().getColor(R.color.blanco));
            TextView tit0 = (TextView) view.findViewById(R.id.info_titles0);
            TextView tit1 = (TextView) view.findViewById(R.id.info_titles1);
            TextView tit2 = (TextView) view.findViewById(R.id.info_titles2);
            TextView tit3 = (TextView) view.findViewById(R.id.info_titles3);
            TextView tit4 = (TextView) view.findViewById(R.id.info_titles4);
            TextView tit5 = (TextView) view.findViewById(R.id.info_titles5);
            tit0.setTextColor(ColorsRes.Blanco(getActivity()));
            tit1.setTextColor(ColorsRes.Blanco(getActivity()));
            tit2.setTextColor(ColorsRes.Blanco(getActivity()));
            tit3.setTextColor(ColorsRes.Blanco(getActivity()));
            tit4.setTextColor(ColorsRes.Blanco(getActivity()));
            tit5.setTextColor(ColorsRes.Blanco(getActivity()));
        }
        int color = ThemeUtils.getAcentColor(getActivity());
        txt_sinopsis.setStateColorFilter(color);
        txt_titulo.setTextColor(color);
        txt_tipo.setTextColor(color);
        txt_fsalida.setTextColor(color);
        txt_estado.setTextColor(color);
        txt_generos.setTextColor(color);
        txt_debug.setTextColor(color);
        setInfo();
        return view;
    }

    public void setReference(Activity activity) {
        activityWeakReference = new WeakReference<Activity>(activity);
    }

    private Activity activity() {
        try {
            return activityWeakReference.get();
        } catch (NullPointerException e) {
            return getActivity();
        }
    }

    private void setInfo() {
        Bundle bundle = getArguments();
        final String aid = bundle.getString("aid");
        final String json = bundle.getString("json");
        final Parser parser = new Parser();
        final AnimeDetail animeDetail = new AnimeDetail(json);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_sinopsis.setText(animeDetail.getSinopsis());
                txt_titulo.setText(animeDetail.getTitulo());
                txt_tipo.setText(animeDetail.getTid());
                txt_fsalida.setText(animeDetail.getFsalida());
                txt_estado.setText(animeDetail.getEstado());
                txt_generos.setText(animeDetail.getGeneros());
                txt_debug.setText(aid);
                final String[] urls = parser.urlsRel(json);
                if (urls.length == 0) {
                    rv_rel.setVisibility(View.GONE);
                } else {
                    final List<String> titulos = parser.parseTitRel(json);
                    final List<String> tipos = parser.parseTiposRel(json);
                    final String[] aids = parser.parseAidRel(json);
                    rv_rel.setHasFixedSize(true);
                    rv_rel.setLayoutManager(new LinearLayoutManager(getActivity()));
                    AdapterRel adapter = new AdapterRel(getActivity(), titulos, tipos, urls, aids);
                    rv_rel.setAdapter(adapter);
                }
            }
        });
    }

    public void startAnimation(final int position) {
        try {
            activity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (position == -1) {
                            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_from_bottom);
                            nestedScrollView.startAnimation(bottomUp);
                        }
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scrollTop() {
        activity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (nestedScrollView != null)
                    nestedScrollView.scrollTo(0, 1 * -1000);
            }
        });
    }
}
