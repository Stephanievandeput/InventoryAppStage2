package com.example.android.inventoryappstage2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappstage2.data.BookContract;
import com.example.android.inventoryappstage2.data.BookContract.BookEntry;

import java.net.URI;

//BookCursorAdapter is an adapter for a list view that uses a Cursor of book data as its data source
public class BookCursorAdapter extends CursorAdapter {

    //Constructs a new BookCursorAdapter
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    //Makes a new black list item view. No data is set to the views yet
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //This method binds the book data to the given list item layout
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //Find the individual views that we want to modify in the list item layout
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        //Find the colums of book attributes we want to display
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

        //Read the book attributes from the cursor for the current book
        String bookTitle = cursor.getString(titleColumnIndex);
        String bookAuthor = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);

        //Update the TextViews with the attributes for the current book
        titleTextView.setText(bookTitle);
        priceTextView.setText(bookAuthor);
        quantityTextView.setText(bookQuantity);

        final int quantity = Integer.valueOf(bookQuantity);
        final int currentBookId = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));

//Set up the sale button to decrement when the user clicks on it
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Let's the user only decrement when the quantity of books is > 0
                if (quantity > 0) {
                    int decrementedQuantity = quantity - 1;

                    //Get the URI with the ID for the row
                    Uri quantityUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, currentBookId);

                    //Update the database with the new quantity value
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, decrementedQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);
                } else {
                    //Show an error when the quantity reaches 0
                    Toast.makeText(context, R.string.out_of_stock_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
