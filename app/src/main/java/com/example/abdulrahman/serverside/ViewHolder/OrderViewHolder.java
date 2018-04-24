package com.example.abdulrahman.serverside.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.abdulrahman.serverside.InterFace.ItemClickListener;
import com.example.abdulrahman.serverside.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;

    public Button btnEtid,btnRemove,btnDetail,btnDirecation;

    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        /////////
        btnEtid=(Button)itemView.findViewById(R.id.btnEdit);
        btnDetail=(Button)itemView.findViewById(R.id.btnDetail);
        btnDirecation=(Button)itemView.findViewById(R.id.btnDirection);
        btnRemove=(Button)itemView.findViewById(R.id.btnRemove);


    }







}
