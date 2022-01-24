package com.example.todoliste6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {

    public static final String LIST_ID = "id";

    private int listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listId = (int) getIntent().getExtras().get(LIST_ID);
        FragmentManager fm = getSupportFragmentManager();
        TodoListTopFragment top = (TodoListTopFragment) fm.findFragmentById(R.id.list_top);
        TodoListFragment items = (TodoListFragment) fm.findFragmentById(R.id.todo_list_items);
        top.setListId(listId);
        items.setListId(listId);
        top.setOnSearchListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    top.dismissKeyboard(v);
                    String item = v.getText().toString();
                    items.searchList(item);
                    return true;
                }

                return false;
            }
        });
        top.setOnAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                top.dismissKeyboard(v);
                TodoListFragment itemsFrag = (TodoListFragment) fm.findFragmentById(R.id.todo_list_items);
                EditText itemTextView = (EditText) top.getItemText();
                String text = itemTextView.getText().toString();
                itemsFrag.addListItem(text, TodoListFragment.STATUS_TODO);
                itemTextView.getText().clear();
            }
        });
        top.setOnSortingSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner sortingView = (Spinner) top.getSortingView();
                String sortingOption = sortingView.getSelectedItem().toString();
                items.sortList(sortingOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}