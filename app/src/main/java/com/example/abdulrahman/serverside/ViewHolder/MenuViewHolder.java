package com.example.abdulrahman.serverside.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdulrahman.serverside.Common.Common;
import com.example.abdulrahman.serverside.InterFace.ItemClickListener;
import com.example.abdulrahman.serverside.R;



public class MenuViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListener clickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);
        txtMenuName=(TextView)itemView.findViewById(R.id.menu_name);
        imageView=(ImageView)itemView.findViewById(R.id.menu_image);
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
