package knf.animeflv.CustomViews;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 14/12/2017.
 */

public class MemoryClearView extends RelativeLayout {

    @BindView(R.id.img)
    ImageView imageView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.clear)
    Button clear;

    public MemoryClearView(Context context) {
        super(context);
        inflateView(context);
    }

    public MemoryClearView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public MemoryClearView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memory_control_clear_all, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
    }

    public void setTheme(ThemeUtils.Theme theme) {
        imageView.setColorFilter(theme.iconFilter, PorterDuff.Mode.SRC_ATOP);
        title.setTextColor(theme.secondaryTextColor);
        description.setTextColor(theme.secondaryTextColor);
    }

    private void clear() {

    }
}
