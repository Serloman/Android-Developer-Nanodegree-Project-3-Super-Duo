package it.jaschke.alexandria;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;

/**
 * Created by Serloman on 05/08/2015.
 * Updated UI to implements Material Design
 */
public class NewMainActivity extends AppCompatActivity implements Callback{

    private static final int REQUEST_ADD_BOOK = 27;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.new_main_activity);

        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            initListOfBooks(query);
        }
    }

    private void init(){
        initToolbar();
        initListOfBooks("");
        initAddBooks();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        toolbar.setTitle(R.string.books);

        this.setSupportActionBar(toolbar);
    }

    private void initListOfBooks(String query){
        ListOfBooks fragment = ListOfBooks.newInstance(query);

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
        if(isTabletLandMode())
            showDetails(ean);
        else
            openDetails(ean);
    }

    private void showDetails(String ean){
        BookDetail fragment = BookDetail.newInstance(ean);

        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityDetailsContainer, fragment).commit();
    }

    private void openDetails(String ean){
        Intent openBookDetailsIntent = new Intent(this, BookDetailActivity.class);
        openBookDetailsIntent.putExtra(BookDetailActivity.ARG_EAN, ean);
        startActivity(openBookDetailsIntent);
    }

    // Example from http://stackoverflow.com/questions/27378981/how-to-use-searchview-in-toolbar-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
/**/
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) NewMainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(NewMainActivity.this.getComponentName()));
        }
/**/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_about:
                openAboutActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isTabletLandMode(){
        return findViewById(R.id.mainActivityDetailsContainer) != null;
    }

    private void openAboutActivity(){
        Intent about = new Intent(this, AboutActivity.class);
        startActivity(about);
    }
}
