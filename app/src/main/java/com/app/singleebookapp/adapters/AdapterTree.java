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
import com.app.singleebookapp.databases.prefs.SharedPref;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class AdapterTree extends RecyclerView.Adapter<AdapterTree.ViewHolder> {

    private List<PdfDocument.Bookmark> items;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private int clickedItemPosition = -1;
    SharedPref sharedPref;

    public interface OnItemClickListener {
        void onItemClick(View view, PdfDocument.Bookmark obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterTree(Context context, List<PdfDocument.Bookmark> items) {
        this.items = items;
        this.context = context;
        this.sharedPref = new SharedPref(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView menuName;
        public ImageView menuIcon;
        public LinearLayout lytItem;
        public RelativeLayout lytParent;

        public ViewHolder(View v) {
            super(v);
            menuName = v.findViewById(R.id.menu_name);
            menuIcon = v.findViewById(R.id.menu_icon);
            lytItem = v.findViewById(R.id.lyt_item);
            lytParent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"RecyclerView", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final PdfDocument.Bookmark obj = items.get(position);

        holder.menuName.setText(obj.getTitle());
        holder.menuIcon.setImageResource(R.drawable.ic_selected_page);

        holder.lytParent.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, obj, position);
                //Log.d("Drawer", "position " + sharedPref.getLastItemPosition());
                clickedItemPosition = position;
                notifyDataSetChanged();
                if (position == sharedPref.getLastItemPosition()) {
                    Log.d("Drawer", "item already selected");
                } else {
                    sharedPref.setLastItemPosition(position);
                    Log.d("Drawer", "page : " + obj.getPageIdx());
                    //((MainActivity) context).loadWebPage(obj.name, obj.type, obj.url);
                }
            }
        });

        if (clickedItemPosition == position) {
            holder.lytItem.setBackgroundResource(R.drawable.bg_item_selected);
            holder.menuName.setTextColor(ContextCompat.getColor(context, R.color.color_primary));
            holder.menuIcon.setColorFilter(context.getResources().getColor(R.color.color_primary), PorterDuff.Mode.SRC_IN);
        } else {
            holder.lytItem.setBackgroundResource(R.drawable.bg_item_unselected);
            holder.menuName.setTextColor(ContextCompat.getColor(context, R.color.color_text_default));
            holder.menuIcon.setColorFilter(context.getResources().getColor(R.color.color_text_default), PorterDuff.Mode.SRC_IN);
        }

    }

    public void setListData(List<PdfDocument.Bookmark> items) {
        this.items = items;
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

}