package com.example.abdulrahman.serverside.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.abdulrahman.serverside.Remote.APiService;
import com.example.abdulrahman.serverside.Remote.FCMRetrofitClient;
import com.example.abdulrahman.serverside.Remote.IGeoCoorginates;
import com.example.abdulrahman.serverside.Remote.RetrofitClient;
import com.example.abdulrahman.serverside.model.Request;
import com.example.abdulrahman.serverside.model.User;

/**
 * Created by Abdulrahman on 11/25/2017.
 */

public class Common {
    public  static User currentuser;
    public  static Request currentRequest;
    public static final String UPDATE="Update";
    public static final String DELETE="Delete";
    public static final  int PICK_IMAGE_REQUEST=71;
    public static final String baseUrl="https://maps.googleapis.com";
    private static final String facmUrl="https://fcm.googleapis.com/";


    public  static String convertCodeStatus(String  code)
    {
        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On My Way";
        else
            return "Shipped";
    }
    public static APiService getFCMClient()
    {
        return FCMRetrofitClient.getClient(facmUrl).create(APiService.class);
    }
    public static IGeoCoorginates getGeoCodeService()
    {
        return RetrofitClient.getClient(baseUrl).create(IGeoCoorginates.class);
    }
    public  static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight)
    {
        Bitmap scaledBitmap=Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleX=newWidth/(float)bitmap.getWidth();
        float scaleY=newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix scaleMatrix=new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas=new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }
}
