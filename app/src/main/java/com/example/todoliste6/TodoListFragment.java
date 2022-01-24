package com.example.todoliste6;

import androidx.fragment.app.ListFragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class TodoListFragment extends ListFragment {

    public static final String STATUS_TODO = "todo";
    public static final String STATUS_DOING = "doing";
    public static final String STATUS_DONE = "done";

    private long listId;
    private String sortingOption = "newest";
    private SQLiteDatabase db;
    private Cursor cursor;

    public TodoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            listId = savedInstanceState.getLong("listId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        SQLiteOpenHelper todoListDatabaseHelper = new TodoListDatabaseHelper(getActivity());
        try {
            db = todoListDatabaseHelper.getWritableDatabase();
            String[] selectionArgs = {String.valueOf((int) listId)};
            cursor = db.query("ITEM",
                    new String[]{"item_id", "_id", "ITEM_TEXT", "ITEM_STATUS"},
                    "_id = ?",
                    selectionArgs,
                    null, null, null);
            TodoListAdapter listAdapter = new TodoListAdapter(getActivity(),
                    R.layout.todo_list_item,
                    cursor,
                    new String[]{"ITEM_TEXT", "ITEM_STATUS"},
                    new int[]{R.id.item_text, R.id.item_status},
                    0);
            setListAdapter(listAdapter);
        } catch(SQLiteException e) {
            System.out.println(e.toString());
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("listId", listId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id) {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View bottomSheet = getLayoutInflater().inflate(R.layout.edit_list_item_dialog, null);
        TextView itemTextView = itemView.findViewById(R.id.item_text);
        TextView itemStatusView = itemView.findViewById(R.id.item_status);
        String itemText = itemTextView.getText().toString();
        String itemStatus = itemStatusView.getText().toString();

        // set text view in dialog
        TextView textView = bottomSheet.findViewById(R.id.text);
        textView.setText(itemText);

        // set which radio button should be checked upon opening the dialog
        RadioGroup statusGroup = bottomSheet.findViewById(R.id.statuses);
        switch (itemStatus) {
            case STATUS_TODO:
                statusGroup.check(R.id.todo);
                break;
            case STATUS_DOING:
                statusGroup.check(R.id.doing);
                break;
            case STATUS_DONE:
                statusGroup.check(R.id.done);
                break;
            default:
                break;
        }
        // set OnClickListener for radio group
        statusGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ContentValues itemValues = new ContentValues();
                if (checkedId == R.id.todo) {
                    itemValues.put("ITEM_STATUS", STATUS_TODO);
                } else if (checkedId == R.id.doing) {
                    itemValues.put("ITEM_STATUS", STATUS_DOING);
                } else if (checkedId == R.id.done) {
                    itemValues.put("ITEM_STATUS", STATUS_DONE);
                }

                String[] selectionArgs = {String.valueOf((int) listId), itemText, itemStatus};
                try {
                    db.update("ITEM",
                            itemValues,
                            "_id = ? and ITEM_TEXT = ? and ITEM_STATUS = ?",
                            selectionArgs);
                } catch(SQLiteException e) {
                    Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
                    toast.show();
                }

                refreshList();
                dialog.dismiss();
            }
        });

        // set OnClickListener for delete button in dialog
        Button deleteButton = bottomSheet.findViewById(R.id.delete_item);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] selectionArgs = {String.valueOf((int) listId), itemText, itemStatus};
                try {
                    db.delete("ITEM",
                            "_id = ? and ITEM_TEXT = ? and ITEM_STATUS = ?",
                            selectionArgs);
                } catch(SQLiteException e) {
                    Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
                    toast.show();
                }

                refreshList();
                dialog.dismiss();
            }
        });

        // show the dialog
        dialog.setContentView(bottomSheet);
        dialog.show();
    }

    public void setListId(long id) { listId = id; }

    public void searchList(String item) {
        Cursor newCursor = db.query("ITEM",
                new String[]{"_id", "ITEM_TEXT", "ITEM_STATUS"},
                "ITEM_TEXT LIKE ?",
                new String[]{"%" + item + "%"},
                null, null, null);
        CursorAdapter adapter = (CursorAdapter) getListAdapter();
        adapter.changeCursor(newCursor);
    }

    public void addListItem(String item, String status) {
        ContentValues itemValues = new ContentValues();
        itemValues.put("_id", (int) listId);
        itemValues.put("ITEM_TEXT", item);
        itemValues.put("ITEM_STATUS", status);
        try {
            db.insert("ITEM", null, itemValues);
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        refreshList();
    }

    public void sortList(String option) {
        sortingOption = option.toLowerCase();
        refreshList();
    }

    public void refreshList() {
        String[] selectionArgs = {String.valueOf((int) listId)};
        Cursor newCursor;
        switch(sortingOption) {
            case "alphabetical":
                newCursor = db.query("ITEM",
                        new String[]{"item_id", "_id", "ITEM_TEXT", "ITEM_STATUS"},
                        "_id = ?",
                        selectionArgs,
                        null, null, "ITEM_TEXT COLLATE NOCASE");
                break;
            default:
                newCursor = db.query("ITEM",
                        new String[]{"item_id", "_id", "ITEM_TEXT", "ITEM_STATUS"},
                        "_id = ?",
                        selectionArgs,
                        null, null, null);
                break;
        }
        CursorAdapter adapter = (CursorAdapter) getListAdapter();
        adapter.changeCursor(newCursor);
    }
}