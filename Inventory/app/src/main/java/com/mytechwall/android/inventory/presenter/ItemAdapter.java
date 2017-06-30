package com.mytechwall.android.inventory.presenter;

import android.content.Context;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mytechwall.android.inventory.R;
import com.mytechwall.android.inventory.data.InventoryContract;

/**
 * Created by arshdeep chimni on 27-06-2017.
 */

public class ItemAdapter extends CursorAdapter {
    public ItemAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView itemImage=(ImageView)view.findViewById(R.id.itemImage);
        TextView itemtName = (TextView) view.findViewById(R.id.itemtName);
        TextView itemQuantity = (TextView) view.findViewById(R.id.itemQuantity);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        int itemImageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE);

        byte[] imageByte=cursor.getBlob(itemImageColumnIndex);
        int quantity=cursor.getInt(quantityColumnIndex);
        String currentName = cursor.getString(nameColumnIndex);

        if (imageByte!=null){
             Bitmap bitmap=ImageUtils.getImageBitmap(imageByte);
            itemImage.setImageBitmap(bitmap);
        }
        itemtName.setText(currentName);
        itemQuantity.setText(String.valueOf(quantity));


    }
}
