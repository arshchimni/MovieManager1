package com.mytechwall.android.inventory.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by arshdeep chimni on 29-06-2017.
 */

public class ImageUtils {

    public static byte[] getImageBytes(Bitmap imageBitmap){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,70,outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getImageBitmap(byte[] imageByte){
       return BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
    }
}
