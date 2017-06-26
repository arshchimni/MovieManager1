package com.mytechwall.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by arshdeep chimni on 25-06-2017.
 */

public final class InventoryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }

    public static final String CONTENT_AUTHORIYTY="com.mytechwall.android.inventory";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORIYTY);
    public static final String PATH_ITEMS="items";
    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single item.
     */

    public static final  class InventoryEntry implements BaseColumns{

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORIYTY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORIYTY + "/" + PATH_ITEMS;


        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_ITEMS);
        /** Name of database table for items */
        public final static String TABLE_NAME = "items";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_NAME="name";

        public static final String COLUMN_QUANTITY="quantity";

        public static final String COLUMN_SUPPLIER_NAME="supplier";

        public static final String COLUMN_SUPPLIER_CONTACT_NUMBER="supplier_contact_number";

        public static final String COLUMN_SUPPLIER_CONTACT_EMAIL="supplier_contact_email";

        public static final String COLUMN_ITEM_IMAGE="item_image";

    }
}
