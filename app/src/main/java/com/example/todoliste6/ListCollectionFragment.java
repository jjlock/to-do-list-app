package com.example.todoliste6;

import androidx.fragment.app.ListFragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ListCollectionFragment extends ListFragment {

    static interface Listener {
        void listClicked(long id);
    };

    private Listener listener;

    private SQLiteDatabase db;
    private Cursor cursor;


    public ListCollectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (Listener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SQLiteOpenHelper todoListDatabaseHelper = new TodoListDatabaseHelper(getActivity());
        try {
            db = todoListDatabaseHelper.getWritableDatabase();
            cursor = db.query("LIST",
                    new String[]{"_id", "LIST_NAME"},
                    null, null, null, null, null);
            SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"LIST_NAME"},
                    new int[]{android.R.id.text1},
                    0);
            setListAdapter(listAdapter);
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id) {
        if (listener != null) {
            listener.listClicked(id);
        }
    }

    public void searchLists(String listName) {
        Cursor newCursor = db.query("LIST",
                new String[]{"_id", "LIST_NAME"},
                "LIST_NAME LIKE ?",
                new String[]{"%" + listName + "%"},
                null, null, null);
        CursorAdapter adapter = (CursorAdapter) getListAdapter();
        adapter.changeCursor(newCursor);
    }

    public void addTodoList(String name) {
        ContentValues listValues = new ContentValues();
        listValues.put("LIST_NAME", name);
        try {
            db.insert("LIST", null, listValues);
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        refreshList();
    }

    public void sortLists(String option) {
        Cursor newCursor;
        switch(option.toLowerCase()) {
            case "alphabetical":
                newCursor = db.query("LIST",
                        new String[]{"_id", "LIST_NAME"},
                        null, null, null, null,
                        "LIST_NAME COLLATE NOCASE");
                break;
            default:
                newCursor = db.query("LIST",
                        new String[]{"_id", "LIST_NAME"},
                        null, null, null, null, null);
                break;
        }
        CursorAdapter adapter = (CursorAdapter) getListAdapter();
        adapter.changeCursor(newCursor);
    }

    public void refreshList() {
        Cursor newCursor = db.query("LIST",
                new String[]{"_id", "LIST_NAME"},
                null, null, null, null, null);
        CursorAdapter adapter = (CursorAdapter) getListAdapter();
        adapter.changeCursor(newCursor);
    }
}