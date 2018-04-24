package com.example.abdulrahman.serverside;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.abdulrahman.serverside.Common.Common;
import com.example.abdulrahman.serverside.InterFace.ItemClickListener;
import com.example.abdulrahman.serverside.ViewHolder.FoodViewHolder;
import com.example.abdulrahman.serverside.model.Category;
import com.example.abdulrahman.serverside.model.Food;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {
    RelativeLayout rootLayout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;
    //fireBase
     FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference foodlist;

    String categoryId="";
    FirebaseRecyclerAdapter<Food,FoodViewHolder>adapter;

    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    FButton btnSelect,btnUpload;
    Food newFood;

    Uri saveUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        //firebase
        database=FirebaseDatabase.getInstance();
        foodlist=database.getReference("Foods");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        /////////
        recyclerView=(RecyclerView)findViewById(R.id.recyclerFood);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rootLayout=(RelativeLayout)findViewById(R.id.rootLayout);
       fab=(FloatingActionButton)findViewById(R.id.fabb);
       fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               showAddFoodDialog();

           }
       });

        if (getIntent()!=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty())
            loadListFood(categoryId);



    }

    private void showAddFoodDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill full information");
        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout,null);
        edtName= (MaterialEditText) add_menu_layout.findViewById(R.id.edtNName);
        edtDescription= (MaterialEditText) add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice= (MaterialEditText) add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount=(MaterialEditText)add_menu_layout.findViewById(R.id.edtDiscount);
        btnSelect = (FButton) add_menu_layout.findViewById(R.id.btnSelectF);
        btnUpload= (FButton) add_menu_layout.findViewById(R.id.btnUploadF);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(); //let user select image from Gallery and save uri of this image
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        //setButton
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //just create new category
                if (newFood !=null)
                {
                    foodlist.push().setValue(newFood);
                   Snackbar.make(rootLayout,"New category"+newFood.getName()+"was added",Snackbar.LENGTH_SHORT).show();


                }

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



    private void loadListFood(String categoryId) {
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodlist.orderByChild("menuId").equalTo(categoryId)

        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.food_image);
                viewHolder.setClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void chooseImage() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (saveUri!=null)
        {
            final ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();
            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this,"Uploaded !!",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for newCategory if image upload and we can get download link
                                    newFood=new Food();
                                    newFood.setName(edtName.getText().toString());
                                    newFood.setDescription(edtDescription.getText().toString());
                                    newFood.setPrice(edtPrice.getText().toString());
                                    newFood.setDiscount(edtDiscount.getText().toString());
                                    newFood.setMenuId(categoryId);
                                    newFood.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Uploaded"+progress+"%");
                        }
                    });


        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Common.PICK_IMAGE_REQUEST&&resultCode==RESULT_OK &&data!=null&&data.getData()!=null)
        {
            saveUri=data.getData();
            btnSelect.setText("Image Selected");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateFoodDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Edit Food");
        alertDialog.setMessage("Please fill full information");
        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout,null);
        edtName= (MaterialEditText) add_menu_layout.findViewById(R.id.edtNName);
        edtDescription= (MaterialEditText) add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice= (MaterialEditText) add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount=(MaterialEditText)add_menu_layout.findViewById(R.id.edtDiscount);
        ///////////////
        edtName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());



        btnSelect = (FButton) add_menu_layout.findViewById(R.id.btnSelectF);
        btnUpload= (FButton) add_menu_layout.findViewById(R.id.btnUploadF);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(); //let user select image from Gallery and save uri of this image
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               changeImage(item);
            }
        });
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        //setButton
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //just create new category

                    //update Information
                    item.setName(edtName.getText().toString());
                    item.setPrice(edtPrice.getText().toString());
                    item.setDiscount(edtDiscount.getText().toString());
                    item.setDescription(edtDescription.getText().toString());

                    foodlist.child(key).setValue(item);
                    Snackbar.make(rootLayout,"category"+item.getName()+"was added",Snackbar.LENGTH_SHORT).show();




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

    private void changeImage(final Food item) {
        if (saveUri!=null)
        {
            final ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();
            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this,"Uploaded !!",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for newCategory if image upload and we can get download link
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Uploaded"+progress+"%");
                        }
                    });


        }
    }


    private void deleteCategory(String key) {
        foodlist.child(key).removeValue();
        Toast.makeText(this,"Item Deleted",Toast.LENGTH_SHORT).show();

    }
}
