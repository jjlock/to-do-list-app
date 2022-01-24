package com.example.todoliste6;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TodoListTopFragment extends Fragment {

    private long listId = -1;

    private TextView.OnEditorActionListener onSearchListener;
    private View.OnClickListener onAddClickListener;
    private AdapterView.OnItemSelectedListener onSortingSelectedListener;

    public TodoListTopFragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_list_top, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView heading = getView().findViewById(R.id.fragment_heading);
        if (listId == -1) {
            heading.setText(getString(R.string.my_todo_lists));
        } else {
//            heading.setText(TodoList.lists.get((int) listId).getName());
            SQLiteOpenHelper todoListDatabaseHelper = new TodoListDatabaseHelper(getActivity());
            try {
                SQLiteDatabase db = todoListDatabaseHelper.getReadableDatabase();
                Cursor cursor = db.query ("LIST",
                        new String[] {"LIST_NAME"},
                        "_id = ?",
                        new String[] {Integer.toString((int) listId)},
                        null, null,null);
                if(cursor.moveToFirst()) {
                    heading.setText(cursor.getString(0));
                }
                cursor.close();
                db.close();
            } catch(SQLiteException e) {
                Toast toast = Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        EditText searchView = getView().findViewById(R.id.query);
        searchView.setOnEditorActionListener(onSearchListener);

        Button addButton = getView().findViewById(R.id.add_item);
        addButton.setOnClickListener(onAddClickListener);

        Spinner sortSpinner = getView().findViewById(R.id.sorting_option);
        sortSpinner.setOnItemSelectedListener(onSortingSelectedListener);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("listId", listId);
    }

    public void setListId(long id) { listId = id; }

    public void setOnSearchListener(TextView.OnEditorActionListener l) { onSearchListener = l; }

    public void setOnAddClickListener(View.OnClickListener l) { onAddClickListener = l; }

    public void setOnSortingSelectedListener(AdapterView.OnItemSelectedListener l) { onSortingSelectedListener = l; }

    public View getSortingView() { return getView().findViewById(R.id.sorting_option); }

    public View getItemText() {
        return getView().findViewById(R.id.new_item_text);
    }

    public void dismissKeyboard(View v) {
        v.clearFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}