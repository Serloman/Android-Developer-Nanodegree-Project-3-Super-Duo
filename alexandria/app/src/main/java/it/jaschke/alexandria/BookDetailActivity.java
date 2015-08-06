package it.jaschke.alexandria;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Serloman on 06/08/2015.
 */
public class BookDetailActivity extends AppCompatActivity {

    public final static String ARG_EAN = "ARG_EAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_detail_activity);

        initFragment();
    }

    private void initFragment(){
        BookDetail fragment = BookDetail.newInstance(getEan());

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private String getEan(){
        return this.getIntent().getExtras().getString(ARG_EAN);
    }
}
