package knf.animeflv.Tutorial;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanfuentes.hintcase.HintCase;
import com.joanfuentes.hintcase.RectangularShape;
import com.joanfuentes.hintcase.ShapeAnimator;
import com.joanfuentes.hintcaseassets.hintcontentholders.SimpleHintContentHolder;
import com.joanfuentes.hintcaseassets.shapeanimators.RevealCircleShapeAnimator;
import com.joanfuentes.hintcaseassets.shapeanimators.RevealRectangularShapeAnimator;
import com.joanfuentes.hintcaseassets.shapeanimators.UnrevealCircleShapeAnimator;
import com.joanfuentes.hintcaseassets.shapeanimators.UnrevealRectangularShapeAnimator;
import com.joanfuentes.hintcaseassets.shapes.CircularShape;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;


public class TutorialActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    Toolbar toolbar;
    @BindView(R.id.linear)
    LinearLayout layout;
    @BindView(R.id.card_tut_1)
    CardView card1;
    @BindView(R.id.card_tut_2)
    CardView card2;
    @BindView(R.id.card_tut_3)
    CardView card3;
    @BindView(R.id.img_tut_1)
    ImageView image1;
    @BindView(R.id.img_tut_2)
    ImageView image2;
    @BindView(R.id.img_tut_3)
    ImageView image3;
    @BindView(R.id.ib_tut_1_1)
    ImageButton buttonv1;
    @BindView(R.id.ib_tut_2_1)
    ImageButton buttonv2;
    @BindView(R.id.ib_tut_3_1)
    ImageButton buttonv3;
    @BindView(R.id.ib_tut_1_2)
    ImageButton buttond1;
    @BindView(R.id.ib_tut_2_2)
    ImageButton buttond2;
    @BindView(R.id.ib_tut_3_2)
    ImageButton buttond3;
    @BindView(R.id.tv_tut_tit_1)
    TextView txt1;
    @BindView(R.id.tv_tut_tit_2)
    TextView txt2;
    @BindView(R.id.tv_tut_tit_3)
    TextView txt3;
    @BindView(R.id.tv_tut_cap_1)
    TextView txtCap1;
    @BindView(R.id.tv_tut_cap_2)
    TextView txtCap2;
    @BindView(R.id.tv_tut_cap_3)
    TextView txtCap3;
    @BindView(R.id.titulo2)
    TextView tit2;
    @BindView(R.id.titulo3)
    TextView tit3;
    @BindView(R.id.titulo4)
    TextView tit4;
    @BindView(R.id.action_list)
    FloatingActionButton button;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.botones1)
    View botones;

    View startButton;

    private ImageView[] images;
    private TextView[] tits;
    private TextView[] caps;
    private CardView[] cards;
    private ImageButton[] dButtons;

    private Parser parser = new Parser();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);
        toolbar = (Toolbar) findViewById(R.id.toolbar_tuto);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tutorial");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ButterKnife.bind(this);
        setUpViews();
        setUpRandomAnimes(getRandomaids());

    }

    private View getDecor() {
        return getWindow().getDecorView();
    }

    private void setUpViews() {
        images = new ImageView[]{image1, image2, image3};
        tits = new TextView[]{txt1, txt2, txt3};
        caps = new TextView[]{txtCap1, txtCap2, txtCap3};
        cards = new CardView[]{card1, card2, card3};
        dButtons = new ImageButton[]{buttond1, buttond2, buttond3};
        changeTextsColor();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("is_amoled", false)) {
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            card1.setCardBackgroundColor(ColorsRes.Prim(this));
            buttond1.setColorFilter(ColorsRes.Blanco(this));
            buttond2.setColorFilter(ColorsRes.Blanco(this));
            buttond3.setColorFilter(ColorsRes.Blanco(this));
            buttonv1.setColorFilter(ColorsRes.Blanco(this));
            buttonv2.setColorFilter(ColorsRes.Blanco(this));
            buttonv3.setColorFilter(ColorsRes.Blanco(this));
            txt1.setTextColor(ColorsRes.SecondaryTextDark(this));
            txt2.setTextColor(ColorsRes.SecondaryTextDark(this));
            txt3.setTextColor(ColorsRes.SecondaryTextDark(this));
        } else {
            buttond1.setColorFilter(ColorsRes.Holo_Light(this));
            buttond2.setColorFilter(ColorsRes.Holo_Light(this));
            buttond3.setColorFilter(ColorsRes.Holo_Light(this));
            buttonv1.setColorFilter(ColorsRes.Holo_Light(this));
            buttonv2.setColorFilter(ColorsRes.Holo_Light(this));
            buttonv3.setColorFilter(ColorsRes.Holo_Light(this));
            txt1.setTextColor(ColorsRes.SecondaryTextLight(this));
            txt2.setTextColor(ColorsRes.SecondaryTextLight(this));
            txt3.setTextColor(ColorsRes.SecondaryTextLight(this));
        }
        txtCap1.setTextColor(ThemeUtils.getAcentColor(this));
        txtCap2.setTextColor(ThemeUtils.getAcentColor(this));
        txtCap3.setTextColor(ThemeUtils.getAcentColor(this));
        tit2.setTextColor(ThemeUtils.getAcentColor(this));
        tit3.setTextColor(ThemeUtils.getAcentColor(this));
        tit4.setTextColor(ThemeUtils.getAcentColor(this));
        card2.setCardBackgroundColor(Color.argb(100, 253, 250, 93));
        card3.setCardBackgroundColor(Color.argb(100, 26, 206, 246));
        button.setBackgroundColor(ThemeUtils.getAcentColor(this));
        MainStates.setListing(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainStates.isListing()) {
                    button.setImageResource(R.drawable.ic_add_list);
                    MainStates.setListing(false);
                } else {
                    button.setImageResource(R.drawable.ic_done);
                    MainStates.setListing(true);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
    }

    private void setUpRandomAnimes(final List<Integer> random) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                for (int aid : random) {
                    Log.d("Aid", "-" + aid + "-");
                    final int position = random.indexOf(aid);
                    final String id = String.valueOf(aid).trim();
                    String type = Parser.getTypeCached(id);
                    if (type.equals("Anime")) type = "Capitulo";
                    final String number = !type.equals("Pelicula") ? " 1" : "";
                    final String result = type + number;
                    final String title = parser.getTitCached(id);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
                            new CacheManager().mini(TutorialActivity.this, id, images[position]);
                            tits[position].setText(title);
                            caps[position].setText(result);
                            String eid = id + "_1E";
                            if (MainStates.init(TutorialActivity.this).WaitContains(eid)) {
                                dButtons[position].setImageResource(R.drawable.ic_waiting);
                            }else {
                                dButtons[position].setImageResource(R.drawable.ic_get_r);
                            }
                            cards[position].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (MainStates.isListing()) {
                                        String eid = id + "_1E";
                                        if (MainStates.init(TutorialActivity.this).WaitContains(eid)) {
                                            MainStates.init(TutorialActivity.this).delFromWaitList(eid);
                                            dButtons[position].setImageResource(R.drawable.ic_get_r);
                                        } else {
                                            MainStates.init(TutorialActivity.this).addToWaitList(eid);
                                            dButtons[position].setImageResource(R.drawable.ic_waiting);
                                        }
                                    } else {
                                        InfoHelper.open(
                                                TutorialActivity.this,
                                                new InfoHelper.SharedItem(images[position], "img"),
                                                new InfoHelper.BundleItem(InfoHelper.BundleItem.KEY_AID, id),
                                                new InfoHelper.BundleItem(InfoHelper.BundleItem.KEY_TITLE, title)
                                        );
                                    }
                                }
                            });
                            cards[position].setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    if (!MainStates.isListing()) {
                                        String eid = id + "_1E";
                                        if (MainStates.init(TutorialActivity.this).WaitContains(eid)) {
                                            MainStates.init(TutorialActivity.this).delFromWaitList(eid);
                                            dButtons[position].setImageResource(R.drawable.ic_get_r);
                                        } else {
                                            MainStates.init(TutorialActivity.this).addToWaitList(eid);
                                            dButtons[position].setImageResource(R.drawable.ic_waiting);
                                        }
                                    }
                                    return true;
                                }
                            });
                            Animation animation = AnimationUtils.loadAnimation(TutorialActivity.this, R.anim.in_animation);
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    cards[position].setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            cards[position].startAnimation(animation);
                        }
                    });
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private void showStartHint() {
        new HintCase(getDecor())
                .setHintBlock(
                        new SimpleHintContentHolder.Builder(this)
                                .setContentTitle("")
                                .setContentText("Pulsa este boton para comenzar el tutorial")
                                .setContentStyle(R.style.ContentStyle)
                                .setTitleStyle(R.style.TitleStyle)
                                .build()
                )
                .setBackgroundColor(Color.argb(230, 65, 65, 65))
                .setTarget(startButton, new CircularShape(), HintCase.TARGET_IS_CLICKABLE)
                .setShapeAnimators(new RevealCircleShapeAnimator(), new UnrevealCircleShapeAnimator())
                .setCloseOnTouchView(true)
                .show();
    }

    private void showCardHint() {
        new HintCase(getDecor())
                .setHintBlock(
                        new SimpleHintContentHolder.Builder(this)
                                .setContentTitle("Esta es una tarjeta")
                                .setContentText("Contiene una miniatura, el titulo, y el numero de capitulo del anime\n\nAl precionar se abrira la informacion completa del anime")
                                .setContentStyle(R.style.ContentStyle)
                                .setTitleStyle(R.style.TitleStyle)
                                .build()
                )
                .setBackgroundColor(Color.argb(230, 65, 65, 65))
                .setTarget(card1, new RectangularShape(), HintCase.TARGET_IS_NOT_CLICKABLE)
                .setShapeAnimators(new RevealRectangularShapeAnimator(), new UnrevealRectangularShapeAnimator())
                .setCloseOnTouchView(true)
                .setOnClosedListener(new HintCase.OnClosedListener() {
                    @Override
                    public void onClosed() {
                        showButtonsHint();
                    }
                }).show();
    }

    private void showButtonsHint() {
        new HintCase(getDecor())
                .setHintBlock(
                        new SimpleHintContentHolder.Builder(this)
                                .setContentTitle("Botones de la tarjeta")
                                .setContentText("El boton Izquierdo es para Streaming/Reproducir\n\nEl boton Derecho es para descargar el capitulo")
                                .setContentStyle(R.style.ContentStyle)
                                .setTitleStyle(R.style.TitleStyle)
                                .build()
                )
                .setBackgroundColor(Color.argb(230, 65, 65, 65))
                .setTarget(botones, new RectangularShape(), HintCase.TARGET_IS_NOT_CLICKABLE)
                .setShapeAnimators(new RevealRectangularShapeAnimator(), new UnrevealRectangularShapeAnimator())
                .setCloseOnTouchView(true)
                .setOnClosedListener(new HintCase.OnClosedListener() {
                    @Override
                    public void onClosed() {
                        showColorHint();
                    }
                }).show();
    }

    private void showColorHint() {
        new HintCase(getDecor())
                .setHintBlock(
                        new SimpleHintContentHolder.Builder(this)
                                .setContentTitle("Colores de tarjetas")
                                .setContentText("Las tarjetas pueden tener 3 colores...")
                                .setContentStyle(R.style.ContentStyle)
                                .setTitleStyle(R.style.TitleStyle)
                                .build()
                )
                .setBackgroundColor(Color.argb(230, 65, 65, 65))
                .setTarget(layout, new RectangularShape(), HintCase.TARGET_IS_NOT_CLICKABLE)
                .setShapeAnimators(new RevealRectangularShapeAnimator(), ShapeAnimator.NO_ANIMATOR)
                .setCloseOnTouchView(true)
                .setOnClosedListener(new HintCase.OnClosedListener() {
                    @Override
                    public void onClosed() {
                        showWaitHint();
                    }
                }).show();
    }

    private void showWaitHint() {
        new HintCase(getDecor())
                .setHintBlock(
                        new SimpleHintContentHolder.Builder(this)
                                .setContentTitle("Lista de espera")
                                .setContentText("Los capitulos se pueden agregar a lista de espera, ya sea haciendo click largo en las tarjetas")
                                .setContentStyle(R.style.ContentStyle)
                                .setTitleStyle(R.style.TitleStyle)
                                .build()
                )
                .setBackgroundColor(Color.argb(230, 65, 65, 65))
                .setTarget(layout, new RectangularShape(), HintCase.TARGET_IS_NOT_CLICKABLE)
                .setShapeAnimators(ShapeAnimator.NO_ANIMATOR, new UnrevealRectangularShapeAnimator())
                .setCloseOnTouchView(true)
                .setOnClosedListener(new HintCase.OnClosedListener() {
                    @Override
                    public void onClosed() {
                        showWaitButtonHint();
                    }
                }).show();
    }

    private void showWaitButtonHint() {
        new HintCase(getDecor())
                .setHintBlock(
                        new SimpleHintContentHolder.Builder(this)
                                .setContentTitle("")
                                .setContentText("O haciendo click en el boton flotante, y despues clicks sencillos en las tarjetas\n(Solo en informacion de anime)")
                                .setContentStyle(R.style.ContentStyle)
                                .setTitleStyle(R.style.TitleStyle)
                                .build()
                )
                .setBackgroundColor(Color.argb(230, 65, 65, 65))
                .setTarget(button, new CircularShape(), HintCase.TARGET_IS_NOT_CLICKABLE)
                .setShapeAnimators(new RevealCircleShapeAnimator(), new UnrevealCircleShapeAnimator())
                .setCloseOnTouchView(true)
                .setOnClosedListener(new HintCase.OnClosedListener() {
                    @Override
                    public void onClosed() {
                        showFinalHint();
                    }
                }).show();
    }

    private void showFinalHint() {
        new HintCase(getDecor())
                .setHintBlock(
                        new SimpleHintContentHolder.Builder(this)
                                .setContentTitle("Tutorial Completado")
                                .setContentText("Listo! Acabas de completar el tutorial, espero que ahora entiendas un poco mas de esta app")
                                .setContentStyle(R.style.ContentStyle)
                                .setTitleStyle(R.style.TitleStyle)
                                .build()
                )
                .setShapeAnimators(new RevealRectangularShapeAnimator(), new UnrevealRectangularShapeAnimator())
                .setBackgroundColor(Color.argb(230, 65, 65, 65))
                .show();
    }

    private List<Integer> getRandomaids() {
        List<Integer> integers = new ArrayList<>();
        integers.add(getRandomNumber());
        integers.add(getRandomNumber());
        integers.add(getRandomNumber());
        return integers;
    }

    private int getRandomNumber() {
        return new Random().nextInt(Parser.getLastAidCached() - 1) + 1;
    }

    private void changeTextsColor() {
        @ColorInt int color = ThemeUtils.isAmoled(this) ? ColorsRes.SecondaryTextDark(this) : ColorsRes.SecondaryTextLight(this);
        int count = layout.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = layout.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startButton = findViewById(R.id.start);
                if (PreferenceManager.getDefaultSharedPreferences(TutorialActivity.this).getBoolean("f_tutorial", true)) {
                    showStartHint();
                    PreferenceManager.getDefaultSharedPreferences(TutorialActivity.this).edit().putBoolean("f_tutorial", false).apply();
                }
            }
        },1500);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        showCardHint();
        return true;
    }

    @Override
    public void onRefresh() {
        for (final CardView cardView : cards) {
            Animation out = AnimationUtils.loadAnimation(this, R.anim.out_animation);
            out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    cardView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            cardView.startAnimation(out);

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpRandomAnimes(getRandomaids());
            }
        }, 1000);
    }
}
