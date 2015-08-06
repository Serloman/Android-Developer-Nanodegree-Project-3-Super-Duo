package it.jaschke.alexandria;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;

/**
 * Created by Serloman on 05/08/2015.
 */
public class NewMainActivity extends AppCompatActivity implements Callback{

    private static final int REQUEST_ADD_BOOK = 27;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.new_main_activity);

        init();
    }

    private void init(){
        initToolbar();
        initListOfBooks();
        initAddBooks();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        toolbar.setTitle(R.string.books);

        this.setSupportActionBar(toolbar);
    }

    private void initListOfBooks(){
        ListOfBooks fragment = ListOfBooks.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void initAddBooks(){
        FloatingActionButton addBooks = (FloatingActionButton) findViewById(R.id.addBookFAB);
        addBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddBook();
            }
        });
    }

    private void openAddBook(){
//        Toast.makeText(this, "Open Add", Toast.LENGTH_SHORT).show();

        Intent addBook = new Intent(this, AddBookActivity.class);
        startActivityForResult(addBook, REQUEST_ADD_BOOK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case RESULT_OK:
                resultOk(resultCode, data);
                break;
        }
    }

    private void resultOk(int requestCode, Intent data){
        switch (requestCode){
            case REQUEST_ADD_BOOK:
                updateNewBooks();
                break;
        }
    }

    private void updateNewBooks(){

    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onItemSelected(String ean) {
        Intent openBookDetailsIntent = new Intent(this, BookDetailActivity.class);
        openBookDetailsIntent.putExtra(BookDetailActivity.ARG_EAN, ean);
        startActivity(openBookDetailsIntent);
    }
}
