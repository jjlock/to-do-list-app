package com.example.todoliste6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ListCollectionFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        TodoListTopFragment top = (TodoListTopFragment) fm.findFragmentById(R.id.all_lists_top);
        ListCollectionFragment lists = (ListCollectionFragment) fm.findFragmentById(R.id.todo_lists_container);
        top.setOnSearchListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    top.dismissKeyboard(v);
                    String listName = v.getText().toString();
                    lists.searchLists(listName);
                    return true;
                }
                return false;
            }
        });
        top.setOnAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                top.dismissKeyboard(v);
                EditText nameView = (EditText) top.getItemText();
                String listName = nameView.getText().toString();
                lists.addTodoList(listName);
                nameView.getText().clear();
            }
        });
        top.setOnSortingSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner sortingView = (Spinner) top.getSortingView();
                String sortingOption = sortingView.getSelectedItem().toString();
                lists.sortLists(sortingOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void listClicked(long id) {
        View todoListView = findViewById(R.id.todo_list_fragment_container);
        if (todoListView != null) {
            FragmentManager fm = getSupportFragmentManager();
            TodoListTopFragment top = new TodoListTopFragment();
            TodoListFragment todoList = new TodoListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            top.setListId(id);
            top.setOnAddClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText itemTextView = (EditText) top.getItemText();
                    String text = itemTextView.getText().toString();
                    todoList.addListItem(text, TodoListFragment.STATUS_TODO);
                    itemTextView.getText().clear();
                }
            });
            todoList.setListId(id);
            ft.replace(R.id.top_fragment_container, top);
            ft.replace(R.id.list_fragment_container, todoList);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra(ListActivity.LIST_ID, (int) id);
            startActivity(intent);
        }
    }
}