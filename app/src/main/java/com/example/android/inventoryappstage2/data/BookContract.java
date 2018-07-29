package com.example.android.inventoryappstage2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

//API contract for the Inventory App
public final class BookContract {

    //Empty constructor to prevent someone from accidentally instantiating the class
    private BookContract() {
    }

    //Name for the content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryappstage2";

    //Base for all URI's for contacting the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible path for the uri: content://com.example.android.inventoryappstage2/books
    public static final String PATH_BOOKS = "books";

    /*
     * Inner class that defines constant values for the database table of the Inventory App
     * Each entry in the table represents a book
     */
    public static final class BookEntry implements BaseColumns {

        //The content URI to access the book data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        //THE MIME type of the CONTENT_URI for a list of books
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //The MIME type of the CONTENT_URI for a single book
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //Name of the database table for books
        public final static String TABLE_NAME = "books";

        //Unique ID number for the book (only for use in the database table
        //Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Title of the book (Product Name)
        //Type: TEXT
        public final static String COLUMN_BOOK_TITLE = "title";

        //Author of the book
        //Type: TEXT
        public final static String COLUMN_BOOK_AUTHOR = "author";

        //Price of the book
        //Type: TEXT
        public final static String COLUMN_BOOK_PRICE = "price";

        //Quantity of books
        //Type: INTEGER
        public final static String COLUMN_BOOK_QUANTITY = "quantity";

        //Name of the supplier of the book
        //Type: TEXT
        public final static String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";

        //Phonenumber of the supplier of the book
        //Type: TEXT
        public final static String COLUMN_BOOK_SUPPLIER_PHONENUMBER = "supplier_phonenumber";

    }
}

