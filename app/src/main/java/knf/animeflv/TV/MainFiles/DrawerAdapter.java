package knf.animeflv.TV.MainFiles;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private Context context;
    private ThemeUtils.Theme theme;
    private OnDrawerItemClick listener;
    private boolean focused = false;

    public DrawerAdapter(Context context, OnDrawerItemClick listener) {
        this.context = context;
        this.theme = ThemeUtils.Theme.create(context);
        this.listener = listener;
    }

    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_tv_drawer, parent, false);
        return new DrawerAdapter.ViewHolder(itemView);
    }

    private String[] getSections() {
        return new String[]{
                "Recientes",
                "Section 2",
                "Section 3"
        };
    }

    private Drawable[] getIcons() {
        return new Drawable[]{
                new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_home).sizeDp(24).color(Color.WHITE),
                context.getResources().getDrawable(R.drawable.ic_block_r),
                context.getResources().getDrawable(R.drawable.ic_block_r)
        };
    }

    @Override
    public void onBindViewHolder(final DrawerAdapter.ViewHolder holder, final int position) {
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(holder.getAdapterPosition());
            }
        });
        holder.container.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    holder.container.setBackgroundColor(theme.isDark ? theme.primaryDark : theme.primary);
                    holder.name.setTextColor(theme.accent);
                    holder.img.clearColorFilter();
                    holder.img.setColorFilter(theme.accent);
                } else {
                    holder.container.setBackgroundColor(theme.isDark ? theme.primary : theme.primaryDark);
                    holder.name.setTextColor(ColorsRes.SecondaryTextDark(context));
                    holder.img.clearColorFilter();
                    holder.img.setColorFilter(ColorsRes.SecondaryTextDark(context));
                }

            }
        });
        holder.name.setText(getSections()[holder.getAdapterPosition()]);
        holder.img.setImageDrawable(getIcons()[holder.getAdapterPosition()]);
        holder.img.setColorFilter(theme.secondaryTextColor);
        if (!focused && holder.getAdapterPosition() == 0) {
            holder.container.setBackgroundColor(theme.isDark ? theme.primaryDark : theme.primary);
            holder.name.setTextColor(theme.accent);
            holder.img.clearColorFilter();
            holder.img.setColorFilter(theme.accent);
            focused = true;
        } else {
            holder.container.setBackgroundColor(theme.isDark ? theme.primary : theme.primaryDark);
            holder.name.setTextColor(ColorsRes.SecondaryTextDark(context));
            holder.img.clearColorFilter();
            holder.img.setColorFilter(ColorsRes.SecondaryTextDark(context));
        }
    }

    @Override
    public int getItemCount() {
        return getSections().length;
    }

    public interface OnDrawerItemClick {
        void onClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        public TextView name;
        @BindView(R.id.img)
        public ImageView img;
        @BindView(R.id.container)
        public View container;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}