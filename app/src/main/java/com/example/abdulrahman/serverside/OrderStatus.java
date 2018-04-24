package com.example.abdulrahman.serverside;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.abdulrahman.serverside.Common.Common;
import com.example.abdulrahman.serverside.InterFace.ItemClickListener;
import com.example.abdulrahman.serverside.Remote.APiService;
import com.example.abdulrahman.serverside.ViewHolder.OrderViewHolder;
import com.example.abdulrahman.serverside.model.MyResponse;
import com.example.abdulrahman.serverside.model.Notification;
import com.example.abdulrahman.serverside.model.Request;
import com.example.abdulrahman.serverside.model.Sender;
import com.example.abdulrahman.serverside.model.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    FirebaseRecyclerAdapter<Request,OrderViewHolder>adapter;
    MaterialSpinner spinner;
    APiService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        mService=Common.getFCMClient();
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");
        ///////
        recyclerView=(RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadOrders();
    }

    private void loadOrders() {
        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests

        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {
                    viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                    viewHolder.txtOrderStatus.setText(Common.convertCodeStatus(model.getStatus()));
                    viewHolder.txtOrderAddress.setText(model.getAddress());
                    viewHolder.txtOrderPhone.setText(model.getPhone());
                ////////
                viewHolder.btnEtid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUpdateDialog(adapter.getRef(position).getKey(),adapter.getItem(position));

                    }
                });
                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent orderDetail = new Intent(OrderStatus.this,OrderDetail.class);
                        Common.currentRequest=model;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });

                viewHolder.btnDirecation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(OrderStatus.this,Track.class);
                        Common.currentRequest=model;
                        startActivity(intent);
                    }
                });




            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }



    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();

    }

    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");
        LayoutInflater inflater=this.getLayoutInflater();
        final View view =inflater.inflate(R.layout.update_order_layout,null);

        spinner=(MaterialSpinner)view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On My Way","Shipped");
        alertDialog.setView(view);
        final  String localKey=key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                requests.child(localKey).setValue(item);
                adapter.notifyDataSetChanged();
                sendOrderStatusToUser(localKey,item);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void sendOrderStatusToUser(final String key,Request item) {
        DatabaseReference tokens=database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token =postSnapShot.getValue(Token.class);
                            Notification notification =new Notification("ABDULRAHMAN","Your order"+key+"was updated");
                            Sender contet=new Sender(token.getToken(),notification);
                            mService.sendNotification(contet)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            ///////////bug
                                            if (response.code()==20) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(OrderStatus.this, "Order was updated", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(OrderStatus.this, "Order was updated but failed to send notification", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }
}
