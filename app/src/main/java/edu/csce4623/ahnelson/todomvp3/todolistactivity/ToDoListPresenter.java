package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItemRepository;
import edu.csce4623.ahnelson.todomvp3.data.ToDoListDataSource;

/**
 * ToDoListPresenter -- Implements the Presenter interface from ToDoListContract Presenter
 */
public class ToDoListPresenter implements ToDoListContract.Presenter {

    //Data repository instance
    //Currently has a memory leak -- need to refactor context passing
    private static ToDoItemRepository mToDoItemRepository;
    //View instance
    private final ToDoListContract.View mToDoItemView;



    // Integer request codes for creating or updating through the result method
    private static final int CREATE_TODO_REQUEST = 0;
    private static final int UPDATE_TODO_REQUEST = 1;
    private static final int DELETE_TODO = 2;


    /**
     * ToDoListPresenter constructor
     * @param toDoItemRepository - Data repository instance
     * @param toDoItemView - ToDoListContract.View instance
     */
    public ToDoListPresenter(@NonNull ToDoItemRepository toDoItemRepository, @NonNull ToDoListContract.View toDoItemView){
        mToDoItemRepository = toDoItemRepository;
        mToDoItemView = toDoItemView;
        //Make sure to pass the presenter into the view!
        mToDoItemView.setPresenter(this);
    }

    @Override
    public void start(){
        //Load all toDoItems
        loadToDoItems();
    }


    @Override
    public void addNewToDoItem() {
        //Create stub ToDoItem with temporary data
        Log.d("ToDoListPresenter", "add new to do item");
        ToDoItem item = new ToDoItem();
        item.setTitle("Title");
        item.setContent("Content");
        item.setCompleted(false);
        item.setDueDate(System.currentTimeMillis());
        item.setId(-1);
        //Show AddEditToDoItemActivity with a create request and temporary item
        mToDoItemView.showAddEditToDoItem(item,CREATE_TODO_REQUEST);
    }

    @Override
    public void showExistingToDoItem(ToDoItem item) {
        //Show AddEditToDoItemActivity with a edit request, passing through an item
       mToDoItemView.showAddEditToDoItem(item,UPDATE_TODO_REQUEST);
    }

    @Override
    public void result(int requestCode, int resultCode, ToDoItem item) {
        Log.d("TO DO LIST PRESENTER","made it to result");
        Log.d("REQUEST CODE IS ",Integer.toString(requestCode));
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CREATE_TODO_REQUEST){
                createToDoItem(item);
            }else if(requestCode == UPDATE_TODO_REQUEST){
                updateToDoItem(item);
            }else if(requestCode == DELETE_TODO){
                delete(item);
            }else{
                Log.e("ToDoPresenter", "No such request!");
            }
        }
    }

    /**
     * Create ToDoItem in repository from ToDoItem and reload data
     * @param item - item to be placed in the data repository
     */
    private void createToDoItem(final ToDoItem item){
        Log.d("ToDoListPresenter","Create Item");
        // request to the Repository to create the new To Do item & add it to DB
        mToDoItemRepository.createToDoItem(item, new ToDoListDataSource.CreateToDoItemCallback() {
            @Override
            public void onCreateToDoItem(long id, ToDoItem item1) {
                Log.d("ID IN PRESENTER",String.valueOf(id));
                mToDoItemView.setAlarm(id, item1);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("PRESENTER","Error creating item");
            }
        });
    }

    /**
     * Update ToDoItem in repository from ToDoItem and reload data
     * @param item -- ToDoItem to be updated in the ToDoItemRepository
     */
    @Override
    public void updateToDoItem(ToDoItem item){
        // request to the Repository to update the given To Do item in the DB
        mToDoItemRepository.saveToDoItem(item);
        if (item.getCompleted()) {
            mToDoItemView.deleteAlarm(item.getId(), item);
        } else {
            mToDoItemView.setAlarm(item.getId(), item);
        }

    }

    /**
     * Update ToDoItem in repository from ToDoItem and reload data
     * @param item -- ToDoItem to be updated in the ToDoItemRepository
     */
    @Override
    public void delete(ToDoItem item){
        // request to the Repository to delete item
        mToDoItemRepository.deleteToDoItem(item);
        mToDoItemView.deleteAlarm(item.getId(), item);
        // refresh to do item lists
        loadToDoItems();
    }

    /**
     * loadToDoItems -- loads all items from ToDoItemRepository
     * Two callbacks -- success/failure
     */
    @Override
    public void loadToDoItems(){
        Log.d("ToDoListPresenter","Loading ToDoItems");
        mToDoItemRepository.getToDoItems(new ToDoListDataSource.LoadToDoItemsCallback() {
            @Override
            public void onToDoItemsLoaded(List<ToDoItem> toDoItems) {
                Log.d("PRESENTER","Loaded");
                mToDoItemView.showToDoItems(toDoItems);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("PRESENTER","Not Loaded");
            }
        });
    }

}
