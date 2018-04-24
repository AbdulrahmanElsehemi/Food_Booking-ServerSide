package com.example.abdulrahman.serverside.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdulrahman.serverside.Common.Common;
import com.example.abdulrahman.serverside.InterFace.ItemClickListener;
import com.example.abdulrahman.serverside.R;

/**
 * Created by Abdulrahman on 11/27/2017.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView food_name;
    public ImageView food_image;
    private ItemClickListener clickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);
        food_name=(TextView)itemView.findViewById(R.id.food_name);
        food_image=(ImageView)itemView.findViewById(R.id.food_image);
       // itemView.setOnClickListener((View.OnClickListener) this);
        itemView.setOnClickListener( this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the Action ");
        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}