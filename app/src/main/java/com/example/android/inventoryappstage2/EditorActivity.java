package com.example.android.inventoryappstage2;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.android.inventoryappstage2.data.BookContract.BookEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier for the book data loader
    private static final int EXISTING_BOOK_LOADER = 0;

    //Content URI for the existing book
    private Uri mCurrentBookUri;

    // EditText fielt to enter the title of the book
    private EditText mTitleEditText;

    //EditText field to enter the author of the book
    private EditText mAuthorEditText;

    //EditText field to enter the price of the book
    private EditText mPriceEditText;

    //EditText field to enter the quantity of this type of book
    private EditText mQuantityEditText;

    //EditText field to enter the name of the supplier of the book
    private EditText mSupplierNameEditText;

    //EditText field to enter the phonenumber of the supplier of the book
    private EditText mSupplierPhonenumberEditText;

    //Boolean that keeps track of whether the book has been edited (true) or not (false)
    private boolean mBookHasChanged = false;

    //Variable for the quantity increment and decrement buttons
    int mQuantity = 0;

    //OnTouchListener that listens for any user touches, checking for editing
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Examine the intent that was used to launch this activity in order to figure out if a new book is added or an existing one edited
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        //If the intent does not containt a book content URI, then a new one should be created
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
            //Otherwise this is an existing book
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

//Find the relevant views that will be needed for the user to use the editor
        mTitleEditText = findViewById(R.id.edit_book_title);
        mAuthorEditText = findViewById(R.id.edit_book_author);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_book_supplier_name);
        mSupplierPhonenumberEditText = findViewById(R.id.edit_book_supplier_phonenumber);

        mQuantityEditText.setText(String.valueOf(mQuantity));

        //Set up OnTouchListeners on all the input fields
        mTitleEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhonenumberEditText.setOnTouchListener(mTouchListener);

        //Set up the increment button for the quantity
        Button incrementButton = (Button) findViewById(R.id.increment_button);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mQuantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    mQuantity++;
                    mQuantityEditText.setText(String.valueOf(mQuantity));
                } catch (NumberFormatException e) {

                }
            }
        });

        //Set up the decrement button for the quantity
        Button decrementButton = (Button) findViewById(R.id.decrement_button);
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mQuantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    if (mQuantity <= 0) {
                        Toast.makeText(v.getContext(), v.getContext().getString(R.string.decrement_message), Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        mQuantity--;
                        mQuantityEditText.setText(String.valueOf(mQuantity));
                    }
                } catch (NumberFormatException e) {

                }
            }

        });


        //Set up an OnClickListener for the call button, to call the supplier
        Button callButton = (Button) findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mSupplierPhonenumberEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        });
    }

    //Get user input from the editor and save book into database
    private void saveBook() {
        //Read from input fields and use trim to eliminate leading or trailing white space
        String titleString = mTitleEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhonenumberEditText.getText().toString().trim();

        //Check if this is supposed to be a new book and check if all fields are blank
        if (mCurrentBookUri == null && TextUtils.isEmpty(titleString) || TextUtils.isEmpty(authorString) ||
                TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierNameString) ||
                TextUtils.isEmpty(supplierPhoneNumberString)) {
            Toast.makeText(this, R.string.empty_fields_message, Toast.LENGTH_SHORT).show();
            return;
        }

        //Create a ContentValues object where column names are the keys and book attributes are the values
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, titleString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONENUMBER, supplierPhoneNumberString);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

        //Determine if this is a new or existing book by checking whether the URI mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {
            //This is a new book, so a new one should be added
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            //Show a toast message whether the insertion was successful or failed
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            //Otherwise the book already exists and should be updated with the content URI and values
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options and add menu items to the app bar
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //If this is a new book, hide the delete item
        if (mCurrentBookUri == null) {
            MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
            MenuItem menuItemSave = menu.findItem(R.id.action_save);
            menuItemSave.setVisible(true);
            menuItemDelete.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked on a menu option in the app bar menu
        switch (item.getItemId()) {
            case R.id.action_save:
                //save book to database
                saveBook();
                finish();
                return true;
            case R.id.action_delete:
                //delete book from the database
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                //If there are unsaved changes, set up a dialog to warn the user
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //This method is called when the back button is pressed
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        //If there are unsaved changes, set up a dialog to warn the user
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Since the editor shows all book attributes, the projection contains all columns from the books table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONENUMBER};

        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Leave early if the cursor is null or there is less than one row
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }


        //Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            //Find the columns of the book attributes
            int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONENUMBER);

            //Extract the value from the cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            //Update views on the screen with the values from the database
            mTitleEditText.setText(title);
            mAuthorEditText.setText(author);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhonenumberEditText.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhonenumberEditText.setText("");
    }

    //Show a dialog that warns the user there are unsaved changes that will be lost if they leave now
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Prompt the user to confirm they want to delete this book
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Delete a book from the database
    private void deleteBook() {
        //Only perform the delete if this is an existing book
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        //Close the activity
        finish();
    }

}
