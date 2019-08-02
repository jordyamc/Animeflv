package knf.animeflv.info.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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
import knf.animeflv.CustomViews.TextViewExpandableAnimation;
import knf.animeflv.JsonFactory.MALGetter;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterRel;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.AnimeDetail;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

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
    @BindView(R.id.rating_bar)
    MaterialRatingBar ratingBar;
    @BindView(R.id.info_rate_count)
    TextView txt_rate_count;
    @BindView(R.id.rv_relacionados)
    RecyclerView rv_rel;

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
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(getActivity());
        view.setBackgroundColor(theme.background);
        txt_sinopsis.setTextColor(theme.textColorNormal);
        TextView tit0 = view.findViewById(R.id.info_titles0);
        TextView tit1 = view.findViewById(R.id.info_titles1);
        TextView tit2 = view.findViewById(R.id.info_titles2);
        TextView tit3 = view.findViewById(R.id.info_titles3);
        TextView tit4 = view.findViewById(R.id.info_titles4);
        TextView tit5 = view.findViewById(R.id.info_titles5);
        tit0.setTextColor(theme.textColorNormal);
        tit1.setTextColor(theme.textColorNormal);
        tit2.setTextColor(theme.textColorNormal);
        tit3.setTextColor(theme.textColorNormal);
        tit4.setTextColor(theme.textColorNormal);
        tit5.setTextColor(theme.textColorNormal);
        int color = theme.accent;
        txt_sinopsis.setStateColorFilter(color);
        txt_titulo.setTextColor(color);
        txt_tipo.setTextColor(color);
        txt_fsalida.setTextColor(color);
        txt_estado.setTextColor(color);
        txt_generos.setTextColor(color);
        txt_debug.setTextColor(color);
        txt_rate_count.setTextColor(color);
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
                ratingBar.setRating(animeDetail.getRate());
                ratingBar.setIsIndicator(true);
                txt_rate_count.setText(animeDetail.getRate_count());
                /*ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChanged(MaterialRatingBar materialRatingBar, final float v) {
                        if (!RateDB.get(getActivity()).isRated(aid))
                            RateHelper.rate(aid, Math.round(v), new RateHelper.RateResponse() {
                                @Override
                                public void onResponse(@Nullable final String votes, final double rating, boolean success) {
                                    if (success) {
                                        RateDB.get(getActivity()).addRate(aid, String.valueOf(v));
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ratingBar.setRating((float) rating);
                                                txt_rate_count.setText(votes);
                                            }
                                        });
                                    } else {
                                        Toaster.toast("Error al enviar!!!");
                                    }
                                }
                            });
                    }
                });*/
                final String[] urls = parser.urlsRel(json);
                if (urls.length == 0) {
                    rv_rel.setVisibility(View.GONE);
                } else {
                    final List<String> titulos = parser.parseTitRel(json);
                    final List<String> tipos = parser.parseTiposRel(json);
                    final String[] aids = parser.parseAidRel(json);
                    //rv_rel.setHasFixedSize(true);
                    rv_rel.setLayoutManager(new LinearLayoutManager(getActivity()));
                    AdapterRel adapter = new AdapterRel(getActivity(), titulos, tipos, urls, aids);
                    rv_rel.setAdapter(adapter);
                }
            }
        });
        MALGetter.getAnimeSearch(animeDetail.getTitulo(), new MALGetter.SearchInterface() {
            @Override
            public void onFinishSearch(String result, boolean success) {
                if (success) {
                    final String date = MALGetter.parseStartDate(result, animeDetail.getTitulo());
                    if (!date.startsWith("_error")) {
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt_fsalida.setText(date);
                                }
                            });
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("MAL Error", date);
                    }
                }
            }
        });
        startAnimation(bundle.getInt("position", -1));
    }

    public void startAnimation(final int position) {
        try {
            activity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (position == -1) {
                            Animation bottomUp = AnimationUtils.loadAnimation(activity(), R.anim.slide_from_bottom);
                            nestedScrollView.startAnimation(bottomUp);
                        }
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (nestedScrollView == null)
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startAnimation(position);
                                }
                            }, 500);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
