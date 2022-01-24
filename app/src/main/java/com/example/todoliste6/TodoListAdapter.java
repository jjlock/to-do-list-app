package com.example.todoliste6;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TodoListAdapter extends SimpleCursorAdapter {

    public TodoListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView statusView = view.findViewById(R.id.item_status);
        String status = statusView.getText().toString();
        switch (status) {
            case TodoListFragment.STATUS_TODO:
                statusView.setBackgroundResource(R.drawable.status_todo);
                break;
            case TodoListFragment.STATUS_DOING:
                statusView.setBackgroundResource(R.drawable.status_doing);
                break;
            case TodoListFragment.STATUS_DONE:
                statusView.setBackgroundResource(R.drawable.status_done);
                break;
            default:
                break;
        }
    }
}
