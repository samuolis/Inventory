package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.StoreContract.StoreEntry;

/**
 * Created by Lukas on 2017-07-19.
 */

public class StoreCursorAdapter extends CursorAdapter {

    public StoreCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        TextView countTextView = (TextView) view.findViewById(R.id.count);

        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_ITEM_PRICE);
        int countColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_ITEM_COUNT);

        // Read the pet attributes from the Cursor for the current item
        String ItemName = cursor.getString(nameColumnIndex);
        int ItemPrice = cursor.getInt(priceColumnIndex);
        int ItemCount = cursor.getInt(countColumnIndex);

        nameTextView.setText(ItemName);
        summaryTextView.setText(Integer.toString(ItemPrice));
        countTextView.setText(Integer.toString(ItemCount));
    }

}
