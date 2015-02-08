package me.amielkollek.todolist2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AddToDoItem extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_item);

        final Intent intent = new Intent(this, MainActivity.class);

        Button addButton = (Button) findViewById(R.id.todo_button);

        // adding the listener
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText input = (EditText) findViewById(R.id.todo_item);
                        String todoItem = input.getText().toString();
                        if(! todoItem.matches("")) { // if not empty, add to to do list
                            String sql = String.format("INSERT INTO ToDoItems VALUES ('%s');",
                                    todoItem);
                            SQLiteDatabase toDoDB = openOrCreateDatabase("ToDoListDB.db", MODE_PRIVATE, null);
                            toDoDB.execSQL(sql);
                        }
                        startActivity(intent);
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
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
}
