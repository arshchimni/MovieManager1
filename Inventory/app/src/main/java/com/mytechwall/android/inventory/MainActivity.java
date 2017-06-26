package com.mytechwall.android.inventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mytechwall.android.inventory.data.InventoryContract;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            insertItem();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertItem() {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_NAME, "CHERRY");
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, 3);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_EMAIL, "CHERRY@gmail.com");
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_NUMBER, "8146530645");
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, "Chimni");

        Uri newRow = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

    }

    private void displayDatabaseInfo() {

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_NUMBER,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_EMAIL,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME

        };

        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            cursor = getContentResolver().query(InventoryContract.InventoryEntry.CONTENT_URI, projection, null
                    , null, null, null);
        }
        cursor.moveToFirst();
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME);
        String currentName = cursor.getString(nameColumnIndex);
        TextView name = (TextView) findViewById(R.id.hello);
        name.setText(currentName);
        name.append("The items table contains " + cursor.getCount() + " items.\n\n");

    }
}
