package com.mytechwall.android.inventory.data;

import android.provider.BaseColumns;

/**
 * Created by arshdeep chimni on 25-06-2017.
 */

public final class InventoryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }
    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single item.
     */

    public static final  class InventoryEntry implements BaseColumns{

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
