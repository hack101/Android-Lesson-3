# Hack101 - Android: Lesson 3 #

## Saving Data ##
----------------------------------

In this tutorial we will be picking up where we left off last time with our to-do list and add saved data. If you recall, our app so far only appears to be a to-do list, but the items in the list are always the same, and users cannot add to them.

To save data, we will create a database on the phone. Before we can add this database to our app, we need to learn how to use a database.

### SQL

SQL (Structured Query Language) is a language used to handle data stored in a database. In our database, everything is stored in **tables**. Here is an example table:

| DogBreed         |  AverageHeight  |  DogId        |
|------------------|:---------------:|--------------:|
| Chihuahua        | 18              | 00000000000   |
| Border Collie    | 50              | 00000000001   | 
| Chow Chow        | 52              | 00000000002   |
| French Bulldog   | 30              | 00000000003   | 
| Great Dane       | 73              | 00000000004   | 

This table stored a list of dog breeds, their IDs and their average heights (in cm). 

I will be using [this online SQL interpreter](http://kripken.github.io/sql.js/GUI/) during the tutorial.

If we wanted to create this table in SQL, we would use the `CREATE TABLE` SQL statement.

```sql
CREATE TABLED Dogs ( DogBreed      varchar(50),
                    AverageHeight integer,
                    DogId         integer);
```

Here, we've said that we want to make a table that stores a string of max length 50, `DogBreed` and two integers `AverageHeight` and `DogId`. If we want to add items to this table, we use the `INSERT` statement. 

```sql
INSERT INTO Dogs VALUES ('Chihuahua',18,00000000000);
INSERT INTO Dogs VALUES ('Border Collie',50,00000000001);
INSERT INTO Dogs VALUES ('Chow Chow',52,00000000002);
INSERT INTO Dogs VALUES ('French Bulldog',30,00000000003);
INSERT INTO Dogs VALUES ('Great Dane ',73,00000000004);
```

Now we use the `SELECT` statement to grab data from our database. If I want to get the names of all the dog breeds in the table `Dogs`, I would type

```sql
SELECT DogBreed FROM Dogs;
```
This returns

| DogBreed         | 
|:-----------------|
| Chihuahua        | 
| Border Collie    |
| Chow Chow        | 
| French Bulldog   |
| Great Dane       |

I can also select multiple columns at once, 

```sql 
SELECT DogBreed,AverageHeight FROM Dogs;
```
This returns

| DogBreed         |  AverageHeight  | 
|------------------|:---------------:|
| Chihuahua        | 18              |
| Border Collie    | 50              | 
| Chow Chow        | 52              | 
| French Bulldog   | 30              |
| Great Dane       | 73              |

And finally, I can make selections based on the entries. For example

```sql
SELECT DogBreed,AverageHeight FROM Dogs WHERE AverageHeight>30;
```
returns

| DogBreed         |  AverageHeight  | 
|------------------|:---------------:|
| Border Collie    | 50              | 
| Chow Chow        | 52              | 
| Great Dane       | 73              |

and

```sql
SELECT DogBreed FROM Dogs WHERE DogId=00000000003;
```

returns

| DogBreed         |
|:-----------------|
| French Bulldog   | 

That's all the SQL we will need. There are plenty of resources to learn SQL on line if you'd like to learn more.

Now back to our app!

### Adding a database

First we need to create a database for our app. This only needs to happen the first time a user opens the app, after that we simply open the preexisting database and edit it. To do this, we use `openOrCreateDatabase`. This will open a preexisting database, or make a new one if it doesn't exist. We make sure we are choosing a unique name.

```java
    SQLiteDatabase toDoDB = openOrCreateDatabase("me.amielkollek.ToDoListDB.db", MODE_PRIVATE, null);
```

*Note:* Typically one would create a whole new class to handle the database, but given our simple application this is easier. 

The first argument is the filename we want to give our database. The first time the app is run, it will make a database of this name, and all the subsequent times it will just search for and open the database of this name. The next argument is how you are opening/creating the database (in our case it is private, meaning only our app can access it). The next argument would be a reference to an error handler, but we wont be using one so we just leave it null.

Once we have the database set up, we can send it SQL commands. 

```java
    toDoDB.execSQL("CREATE TABLE IF NOT EXISTS ToDoItems( Items varchar(100));");
```

There is some new SQL here, the `IF NOT EXISTS` simply tells to database that if the table already exists then don't bother creating it. Let's also put a character limit on our input. 100, since that's the size of our database field. In the EditText tag of `res/layout/activity_add_todo_item.xml`

```xml
android:maxLength="100"
```


Now we want to grab all the to-do items in the database and add them to our `ArrayAdapter` so they show up in our app.

Let's make a method, `getToDoItems` to grab all the todo items from the database.
We use an object called a cursor to access the items in our database. You can think of a cursor as a pointer than goes up and down the rows of a table. We specify which items we want to access with an SQL query.

```java
        SQLiteDatabase toDoDB = openOrCreateDatabase("ToDoListDB.db", MODE_PRIVATE, null);
        Cursor items = toDoDB.rawQuery("SELECT Items FROM ToDoItems",null);
```

We can now access the items through the query. We move our cursor to the beginning of the table, and pick off each item, storing it in a list. In the end our method looks like this:

```java
    public ArrayList<String> getToDoItems(){
        ArrayList<String> todoListItems = new ArrayList<String>();

        SQLiteDatabase toDoDB = openOrCreateDatabase("ToDoListDB.db", MODE_PRIVATE, null);
        Cursor items = toDoDB.rawQuery("SELECT Items FROM ToDoItems",null);
        items.moveToFirst();
        while(!items.isAfterLast()){
            todoListItems.add(items.getString(0));
            items.moveToNext();
        }
        return todoListItems;
    }
```

We now can grab all the todo items in our `onCreate` method. As we did last tutorial, we set up an `ArrayAdapter` and give it a list of strings, then set it as the `ListView`'s adapter. Since we will want to use this `ArrayAdapter` again in our `onResume` method we put it outside the method.

```java
    private ArrayAdapter<String> todoListAdapter;
```

And in our `onCreate` method

```java
        todoListAdapter = new ArrayAdapter<String>(
                this,
                R.layout.todo_item,
                getToDoItems());

        todoList = (ListView) findViewById(R.id.todo_list);
        todoList.setAdapter(todoListAdapter);
```

We'll also override the `onResume` method so that we can have the list refresh whenever the main activity is viewed.

```java
    @Override
    protected void onResume() {
        super.onResume();
        todoListAdapter.clear();
        todoListAdapter.addAll(getToDoItems());

    }
```

Great, now we're all set to add items to our to-do list!

### Adding Items

We move over to our AddToDoListItem activity, and add a couple lines to the listener for the Add button. First we grab the contents of the edit text field. Next we add it to our database with and SQL statement, and finally we return to our main activity. 

```java
    new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText input = (EditText) findViewById(R.id.todo_item);
                        String todoItem = input.getText().toString();
                        if(! todoItem.matches("")) { // check to make sure it's not empty
                            String sql = String.format("INSERT INTO ToDoItems VALUES ('%s');",
                                    todoItem);
                            SQLiteDatabase toDoDB = openOrCreateDatabase("ToDoListDB.db", MODE_PRIVATE, null);
                            toDoDB.execSQL(sql);
                        }
                        startActivity(intent);
                    }
                }
```

And now we can add to-do items to our app! Since everything is being stored in a database, you can close and open the app with your to-do items staying in place.

### Deleting items

The goal is that if an item is clicked, a confirm window pops up and asks the user if they want to delete an item. If they say yes, the item is deleted.

To delete an item, we need to have a way of responding to when an item is clicked. 
We do this with another listener, called an `OnItemClickListener`.

Inside our `onCreate`, we add

```java

        todoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                    // do stuff here
            }
        });

```

Inside the onItemClick method, we need to first grab the item we are going to delete. Notice that one of the parameters for `onItemClick` is the view that has the text in it, so we can grab the text from it's child view.

First we add an id to the TextView in `res/layout/todo_item.xml` so that we can refer to it. 

```xml
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/item"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:paddingBottom="5dp"
    android:textSize="18dp"
    android:maxLength="100"/>
```

Then we grab it in our code


```java
    final String item = ((TextView) v.findViewById(R.id.item)).getText().toString();
```

We declare it as final because we will need it in an inner class in a second.

Next we need a confirm window. We use an `AlertDialog.Builder` to build a dialog, then show it. We first declare a new object, and then by calling a few methods, we set the attributes about the confirm window we want, including what to do if someone says yes (in the form of a listener).

```java
                new AlertDialog.Builder(context)
                        .setMessage(
                                String.format("Would you like to delete the todo item: %s",
                                        item))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do stuff when the user clicks yes
                            }
                            })
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            // do stuff when the user clicks no
                        }
                        })
                        .show();
```

The first thing we do is set the confirm message, then we set the response we want when the user clicks yes or no, then we display the confirm window.

If the user clicks "No", then we just do nothing, but if the user clicks "Yes", we want to delete the item from the database. 

We use an SQL delete statement.

```java
    SQLiteDatabase toDoDB = openOrCreateDatabase("ToDoListDB.db", MODE_PRIVATE, null);
    String sql = String.format("DELETE FROM ToDoItems WHERE Item='%s'",item);
    toDoDB.execSQL(sql);
```


Then, we refresh our list

```java
    todoListAdapter.clear();
    todoListAdapter.addAll(getToDoItems());
```

We can leave our negative button method blank, so nothing happens. And that's it! Users can now create and delete items from their todo list!



