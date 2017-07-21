package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Lukas on 2017-07-19.
 */

public class StoreContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private StoreContract() {
    }


    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_STORE = "store";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class StoreEntry implements BaseColumns {


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE;


        public final static String TABLE_NAME = "store";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_ITEM_NAME = "name";

        public final static String COLUMN_ITEM_PRICE = "price";
        public final static String COLUMN_ITEM_IMAGE = "product_image";
        public final static String COLUMN_ITEM_COUNT = "count";
        public final static String COLUMN_SUP_NAME = "supname";
        public final static String COLUMN_SUP_EMAIL = "supemail";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STORE);

    }
}