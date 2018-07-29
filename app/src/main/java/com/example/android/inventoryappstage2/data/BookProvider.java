package com.example.android.inventoryappstage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.inventoryappstage2.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    //URI matcher code for the content URI for the books table
    private static final int BOOKS = 100;

    //URI matcher code for the content URI for a single book in the books table
    private static final int BOOK_ID = 101;

    //URI matcher object to match a content URI to a corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer. This runs the first time anything is called from this class
    static {
        //Matches the content URI for the whole books table with the code 100
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        //Matches the content URI for a single book with the code 101
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    //Database helper object
    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //Cursor to hold the result of the query
        Cursor cursor;

        //Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            //case for the whole books table URI
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            //case for a single row in the books table URI
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //Set notification URI on the cursor, so it is known what content URI the cursor was created for
        //If the data at this URI changes, the cursor needs to be updated
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //Insert a book into the database with given content values
    private Uri insertBook(Uri uri, ContentValues values) {
        //Check that the title is not null
        String title = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book requires title");
        }

        //Check that the author is not null
        String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);
        if (author == null) {
            throw new IllegalArgumentException("Book requires author");
        }

        //Check that the price is not null
        String price = values.getAsString(BookEntry.COLUMN_BOOK_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Book requires price");
        }

        //Check that the quantity is not null amd a positive number
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Book requires a valid quantity");
        }
        //Check that the supplier name is not null
        String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Book requires a supplier name");
        }

        //Check that the supplier phone number is not null
        String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_PHONENUMBER);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Book requires a supplier phone number");
        }

        //Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert the new book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        //If the id is -1 , the insertion failed. Return null.
        if (id == -1) {
            return null;
        }

        //Notify all listeners that the data has changed for the content URI
        getContext().getContentResolver().notifyChange(uri, null);

        //Return the new URI with the id of the newly inserted book
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    //Update books in the database with given content values. Apply changes to the rows that are specified. Returns the number of rows successfully updated.
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Check that the title is not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_TITLE)) {
            String title = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Book requires title");
            }
        }

        //Check that the author is not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_AUTHOR)) {
            String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);
            if (author == null) {
                throw new IllegalArgumentException("Book requires author");
            }
        }

        //Check that the price is not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            String price = values.getAsString(BookEntry.COLUMN_BOOK_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Book requires price");
            }
        }

        //Check that the quantity is not null amd a positive number
        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Book requires a valid quantity");
            }
        }

        //Check that the supplier name is not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book requires a supplier name");
            }
        }
//Check that the supplier phone number is not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_PHONENUMBER)) {
            String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_PHONENUMBER);
            if (supplierPhoneNumber == null) {
                throw new IllegalArgumentException("Book requires a supplier phone number");
            }
        }

        //If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        //Otherwise get a writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Update the database and return the number of rows affected
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        //If one or more rows are updated, notify the listeners that the data at the URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                //Delete all rows
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //If one or more rows were deleted, then notify all the listeners that the data at the URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Return the number of rows deleted
        return rowsDeleted;
    }


    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + "with match " + match);
        }
    }
}
