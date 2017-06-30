package com.mytechwall.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by arshdeep chimni on 25-06-2017.
 */

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private InventoryDbHelper dbHelper;
    private static final int ITEMS = 100;
    private static final int ITEMS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORIYTY, InventoryContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORIYTY, InventoryContract.PATH_ITEMS + "/#", ITEMS_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection
            , @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // For the ITEMS code, query the items table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the items table.
                cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ITEMS_ID:
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        //sets up the uri that needs to be watched for changes,needed for updating the ui without restarting the app
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case ITEMS_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                rowsDeleted = db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEMS_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, s, strings);

            case ITEMS_ID:
                s = InventoryContract.InventoryEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, s, strings);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }


    }

    private int updateItem(Uri uri, ContentValues contentValues, String s, String[] strings) {
        if (contentValues.size() == 0) {
            return 0;
        }
        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            if (quantity < 0 && quantity != null) {
                throw new IllegalArgumentException("Items don't have negative quantity");
            }
        }
        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME)) {
            String supplieName = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            if (supplieName == null) {
                throw new IllegalArgumentException("Item requires a supplier name");
            }
        }
        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_NUMBER)) {
            String supplierNumber = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_NUMBER);
            if (supplierNumber == null) {
                throw new IllegalArgumentException("Supplier number is needed");
            }
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = db.update(InventoryContract.InventoryEntry.TABLE_NAME, contentValues, s, strings);

        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;

    }

    private Uri insertItem(Uri uri, ContentValues contentValues) {

        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            if (quantity < 0 && quantity != null) {
                throw new IllegalArgumentException("Items don't have negative quantity");
            }
        }
        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME)) {
            String supplieName = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            if (supplieName == null) {
                throw new IllegalArgumentException("Item requires a supplier name");
            }
        }
        if (contentValues.containsKey(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_NUMBER)) {
            String supplierNumber = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_NUMBER);
            if (supplierNumber == null) {
                throw new IllegalArgumentException("Supplier number is needed");
            }
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //signal that a change has occured in the table and change the ui display accordingly
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

}
