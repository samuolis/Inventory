package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventory.data.StoreContract;
import com.example.android.inventory.data.StoreContract.StoreEntry;

import java.io.ByteArrayOutputStream;

public class EditActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the item data loader
     */
    private static final int EXISTING_ITEM_LOADER = 0;
    private static final String STATE_IMAGE_URI = "STATE_IMAGE_URI";
    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final int IMAGE_REQUEST_CODE = 0;
    final Context mContext = this;

    /**
     * Content URI for the existing item (null if it's a new item)
     */
    private Uri mCurrentItemUri;

    /**
     * EditText field to enter the item's name
     */
    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mCountEditText;

    private EditText mSupNameEditText;

    private EditText mSupEmailEditText;
    private boolean imageHelper;

    /**
     * Boolean flag that keeps track of whether the item has been edited (true) or not (false)
     */
    private boolean mItemHasChanged = false;

    private Button mButtonAddImage;
    private ImageView mImageProduct;
    private Uri mImageUri;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mitemHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        imageHelper = false;
        mButtonAddImage = (Button) findViewById(R.id.upload_image_button);
        mImageProduct = (ImageView) findViewById(R.id.item_image);
        mButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonImageClick();

            }
        });

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // If the intent DOES NOT contain a item content URI, then we know that we are
        // creating a new item.
        if (mCurrentItemUri == null) {
            // This is a new item, so change the app bar to say "Add a item"
            setTitle(getString(R.string.editor_activity_title_new));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a item that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing item, so change app bar to say "Edit item"
            setTitle(getString(R.string.editor_activity_title_edit));

            // Initialize a loader to read the item data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mCountEditText = (EditText) findViewById(R.id.edit_item_count);
        mSupNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupEmailEditText = (EditText) findViewById(R.id.edit_supplier_email);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mCountEditText.setOnTouchListener(mTouchListener);
        mSupNameEditText.setOnTouchListener(mTouchListener);
        mSupEmailEditText.setOnTouchListener(mTouchListener);


    }

    private void buttonImageClick() {
        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST_CODE);
    }


    private void saveItem() {
        if (!imageHelper) {
            Toast.makeText(mContext, "Please insert Image", Toast.LENGTH_LONG).show();
            return;
        }
        //Read input fields
        Drawable imageImage = mImageProduct.getDrawable();
        //Convert to bitmap
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageImage);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        //Convert to byte to store
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] imageByte = bos.toByteArray();


        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String countString = mCountEditText.getText().toString().trim();
        String supNameString = mSupNameEditText.getText().toString().trim();
        String supEmailString = mSupEmailEditText.getText().toString().trim();

        if(nameString.isEmpty()){
            Toast.makeText(mContext, "Please insert Item name", Toast.LENGTH_LONG).show();
            return;
        }
        if(priceString.isEmpty()){
            Toast.makeText(mContext, "Please insert Item price", Toast.LENGTH_LONG).show();
            return;
        }
        if(countString.isEmpty()){
            Toast.makeText(mContext, "Please insert Item count", Toast.LENGTH_LONG).show();
            return;
        }

        if(supNameString.isEmpty()||supEmailString.isEmpty()){
            Toast.makeText(mContext, "Please insert Supplier info", Toast.LENGTH_LONG).show();
            return;
        }

        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(countString) && TextUtils.isEmpty(supNameString) && TextUtils.isEmpty(supEmailString) && TextUtils.isEmpty(imageByte.toString())) {
            // Since no fields were modified, we can return early without creating a new item.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and item attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_ITEM_NAME, nameString);
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(StoreEntry.COLUMN_ITEM_PRICE, price);
        int count = 0;
        if (!TextUtils.isEmpty(countString)) {
            count = Integer.parseInt(countString);
        }
        values.put(StoreEntry.COLUMN_ITEM_COUNT, count);
        values.put(StoreEntry.COLUMN_SUP_NAME, supNameString);
        values.put(StoreEntry.COLUMN_SUP_EMAIL, supEmailString);

        values.put(StoreEntry.COLUMN_ITEM_IMAGE, imageByte);

        // Determine if this is a new or existing item by checking if mCurrentitemUri is null or not
        if (mCurrentItemUri == null) {
            // This is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.
            Uri newUri = getContentResolver().insert(StoreEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, ("Error with saving item"),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, ("Item saved"),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentitemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentitemUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, ("Error updating item "),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, ("Item updated"),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save item to database
                saveItem();
                // Exit activity

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
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

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this item ?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the item in the database.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentitemUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, ("error delleting item"),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, ("Deleted succesfully"),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the item table
        String[] projection = {
                StoreEntry._ID,
                StoreEntry.COLUMN_ITEM_NAME,
                StoreEntry.COLUMN_ITEM_PRICE,
                StoreEntry.COLUMN_ITEM_COUNT,
                StoreEntry.COLUMN_SUP_NAME,
                StoreEntry.COLUMN_SUP_EMAIL,
                StoreEntry.COLUMN_ITEM_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_ITEM_PRICE);
            int countColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_ITEM_COUNT);
            int supNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUP_NAME);
            int supEmailColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUP_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_ITEM_IMAGE);


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int count = cursor.getInt(countColumnIndex);
            String supName = cursor.getString(supNameColumnIndex);
            String supEmail = cursor.getString(supEmailColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mCountEditText.setText(Integer.toString(count));
            mSupNameEditText.setText(supName);
            mSupEmailEditText.setText(supEmail);
            Bitmap itemBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            mImageProduct.setImageBitmap(itemBitmap);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mCountEditText.setText("");
        mSupNameEditText.setText("");
        mSupEmailEditText.setText("");

    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && (resultCode == RESULT_OK)) {
            try {
                mImageUri = data.getData();
                int takeFlags = data.getFlags();
                takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
                    imageHelper = true;
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                mImageProduct.setImageBitmap(Utils.getBitmapFromUri(mImageUri, mContext, mImageProduct));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}