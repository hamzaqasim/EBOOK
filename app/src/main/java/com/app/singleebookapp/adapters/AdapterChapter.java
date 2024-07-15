package com.app.singleebookapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.singleebookapp.R;
import com.app.singleebookapp.databases.prefs.AdsPref;
import com.app.singleebookapp.databases.prefs.SharedPref;
import com.app.singleebookapp.models.Chapter;
import com.app.singleebookapp.utils.Constant;
import com.solodroid.ads.sdk.format.NativeAdViewHolder;

import java.util.List;

public class AdapterChapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 0;
    private final int VIEW_AD = 1;
    private List<Chapter> items;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private int clickedItemPosition = -1;
    SharedPref sharedPref;
    AdsPref adsPref;

    public interface OnItemClickListener {
        void onItemClick(View view, Chapter obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterChapter(Context context, List<Chapter> items) {
        this.items = items;
        this.context = context;
        this.sharedPref = new SharedPref(context);
        this.adsPref = new AdsPref(context);
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView menuName;
        public ImageView menuIcon;
        public LinearLayout lytItem;
        public RelativeLayout lytParent;

        public OriginalViewHolder(View v) {
            super(v);
            menuName = v.findViewById(R.id.menu_name);
            menuIcon = v.findViewById(R.id.menu_icon);
            lytItem = v.findViewById(R.id.lyt_item);
            lytParent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_AD) {
            View view;
            if (Constant.NATIVE_AD_STYLE.equals("small")) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_radio, parent, false);
            } else if (Constant.NATIVE_AD_STYLE.equals("medium")) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_news, parent, false);
            } else if (Constant.NATIVE_AD_STYLE.equals("large")) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_medium, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_medium, parent, false);
            }
            vh = new NativeAdViewHolder(view);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer, parent, false);
            vh = new OriginalViewHolder(v);
        }
        return vh;
    }

    @SuppressLint({"RecyclerView", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final Chapter obj = items.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.menuName.setText(obj.page_title);
            vItem.menuIcon.setImageResource(R.drawable.ic_selected_page);

            vItem.lytParent.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, obj, position);
                    clickedItemPosition = position;
                    notifyDataSetChanged();
                    if (position == sharedPref.getLastItemPosition()) {
                        Log.d("Drawer", "item already selected");
                    } else {
                        sharedPref.setLastItemPosition(position);
                        Log.d("Drawer", "page : " + obj.page_number);
                    }
                }
            });

            if (clickedItemPosition == position) {
                vItem.lytItem.setBackgroundResource(R.drawable.bg_item_selected);
                vItem.menuName.setTextColor(ContextCompat.getColor(context, R.color.color_primary));
                vItem.menuIcon.setColorFilter(context.getResources().getColor(R.color.color_primary), PorterDuff.Mode.SRC_IN);
            } else {
                vItem.lytItem.setBackgroundResource(R.drawable.bg_item_unselected);
                vItem.menuName.setTextColor(ContextCompat.getColor(context, R.color.color_text_default));
                vItem.menuIcon.setColorFilter(context.getResources().getColor(R.color.color_text_default), PorterDuff.Mode.SRC_IN);
            }
        } else if (holder instanceof NativeAdViewHolder) {

            final NativeAdViewHolder vItem = (NativeAdViewHolder) holder;

            vItem.loadNativeAd(context,
                    adsPref.getAdStatus(),
                    1,
                    adsPref.getMainAds(),
                    adsPref.getBackupAds(),
                    adsPref.getAdMobNativeId(),
                    adsPref.getAdManagerNativeId(),
                    adsPref.getFanNativeId(),
                    adsPref.getAppLovinNativeAdManualUnitId(),
                    adsPref.getAppLovinBannerZoneId(),
                    false,
                    false,
                    Constant.NATIVE_AD_STYLE,
                    android.R.color.transparent,
                    android.R.color.transparent
            );

            vItem.setNativeAdBackgroundResource(R.drawable.bg_native_ad);

            vItem.setNativeAdMargin(
                    context.getResources().getDimensionPixelOffset(R.dimen.spacing_medium),
                    context.getResources().getDimensionPixelOffset(R.dimen.no_spacing),
                    context.getResources().getDimensionPixelOffset(R.dimen.spacing_medium),
                    context.getResources().getDimensionPixelOffset(R.dimen.spacing_medium)
            );

        }

    }

    public void setListData(List<Chapter> items) {
        this.items = items;
        items.add(0, new Chapter());
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Chapter obj = items.get(position);
        if (obj != null) {
            if (obj.page_title == null || obj.page_title.equals("")) {
                return VIEW_AD;
            }
            return VIEW_ITEM;
        } else {
            return VIEW_ITEM;
        }
    }

}