package me.amielkollek.todolist2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.DialogPreference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ArrayAdapter<String> todoListAdapter;
    private ListView todoList;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase toDoDB = openOrCreateDatabase("ToDoListDB.db", MODE_PRIVATE, null);
        toDoDB.execSQL("CREATE TABLE IF NOT EXISTS ToDoItems( Item varchar(100));");

        todoListAdapter = new ArrayAdapter<String>(
                this,
                R.layout.todo_item,
                getToDoItems());

        todoList = (ListView) findViewById(R.id.todo_list);
        todoList.setAdapter(todoListAdapter);

        Button addButton = (Button) findViewById(R.id.main_button);

        final Intent intent = new Intent(this, AddToDoItem.class);

        addButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(intent);
                    }
                }
        );

        todoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                final String item = ((TextView) v.findViewById(R.id.item)).getText().toString();

                new AlertDialog.Builder(context)
                        .setMessage(
                                String.format("Would you like to delete the todo item: %s",
                                        item))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase toDoDB = openOrCreateDatabase("ToDoListDB.db", MODE_PRIVATE, null);
                                String sql = String.format("DELETE FROM ToDoItems WHERE Item='%s'", item);
                                toDoDB.execSQL(sql);
                                todoListAdapter.clear();
                                todoListAdapter.addAll(getToDoItems());
                            }
                            })
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .show();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        todoListAdapter.clear();
        todoListAdapter.addAll(getToDoItems());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String> getToDoItems(){
        ArrayList<String> todoListItems = new ArrayList<String>();

        SQLiteDatabase toDoDB = openOrCreateDatabase("ToDoListDB.db", MODE_PRIVATE, null);
        Cursor items = toDoDB.rawQuery("SELECT Item FROM ToDoItems",null);
        items.moveToFirst();
        while(!items.isAfterLast()){
            todoListItems.add(items.getString(0));
            items.moveToNext();
        }
        return todoListItems;
    }
}
