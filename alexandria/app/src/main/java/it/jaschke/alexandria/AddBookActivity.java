package it.jaschke.alexandria;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Serloman on 05/08/2015.
 */
public class AddBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_book_activity);

        init();
    }

    private void init(){
        AddBook fragment = AddBook.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
