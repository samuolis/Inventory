package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventory.data.StoreContract.StoreEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    final Context mContext = this;
    private static final int STORE_LOADER = 0;
    private ListView itemListView;
    StoreCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the Item data
        itemListView = (ListView) findViewById(R.id.list);


        // Setup the item click listener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, id);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of Item data in the Cursor.
        // There is no Item data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new StoreCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);

        // Kick off the loader
        getLoaderManager().initLoader(STORE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all items ?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the Item.
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
     * Perform the deletion of the Item in the database.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing Item.
        // Call the ContentResolver to delete the Item at the given content URI.
        // Pass in null for the selection and selection args because the mCurrentItemUri
        // content URI already identifies the Item that we want.
        int rowsDeleted = getContentResolver().delete(StoreEntry.CONTENT_URI, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, ("Something went wrong deleting item"),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, ("Delleted items succesfully"),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                StoreEntry._ID,
                StoreEntry.COLUMN_ITEM_NAME,
                StoreEntry.COLUMN_ITEM_PRICE,
                StoreEntry.COLUMN_ITEM_COUNT};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                StoreEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}