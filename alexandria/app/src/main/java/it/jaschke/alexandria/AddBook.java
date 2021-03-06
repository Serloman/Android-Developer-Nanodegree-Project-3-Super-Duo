package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static AddBook newInstance(){
        AddBook fragment = new AddBook();
        return fragment;
    }

    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText ean;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT="eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";
    private final static int REQUEST_EAN_CODE = 1987;

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";
    private String eanData;
    private Snackbar lastSnack;

    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean!=null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);

        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateMaxLength(13);

                if(lastSnack!=null) {
                    lastSnack.dismiss();
                    lastSnack = null;
                }

                String eanData = s.toString();
                //catch isbn10 numbers
                if(eanData.length()==10 && !eanData.startsWith("978")){
                    eanData="978"+eanData;
                }
                if(eanData.length()<13){
//                    clearFields();
                    return;
                }

                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, eanData);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBook();
            }
        });

        rootView.findViewById(R.id.clearEan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
            }
        });
/** /
        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                ean.setText("");
            }
        });
/**/
        if(savedInstanceState!=null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        initToolbar();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.addBookToolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateMaxLength(int maxLength){
        ean.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    private void scanBook(){
        Intent scan = new Intent(getActivity(), ScanActivity.class);
        startActivityForResult(scan, REQUEST_EAN_CODE);
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length()==0){
            return null;
        }
        String eanStr= ean.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }

        eanData = eanStr;

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        updateMaxLength(ean.length());

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        TextView subtitle = ((TextView) rootView.findViewById(R.id.fullBookSubTitle));
        subtitle.setText(bookSubTitle);
        if(bookSubTitle.compareTo("")!=0)
            subtitle.setVisibility(View.VISIBLE);
        else
        subtitle.setVisibility(View.GONE);

        TextView authorsTextView = ((TextView) rootView.findViewById(R.id.authors));
        authorsTextView.setLines(1);
        authorsTextView.setText("");
        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if(authors!=null){
            String[] authorsArr = authors.split(",");
            authorsTextView.setLines(authorsArr.length);
            authorsTextView.setText(authors.replace(",","\n"));
        }

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            ImageView cover = (ImageView) rootView.findViewById(R.id.fullBookCover);
            Picasso.with(getActivity()).load(imgUrl).into(cover);
            cover.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

//        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
//        rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);

        showBookSnack(bookTitle, eanData);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.fullBookCover).setVisibility(View.INVISIBLE);
//        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
//        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==Activity.RESULT_OK){
            switch (requestCode){
                case REQUEST_EAN_CODE:
                    onCodeReceived(data);
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onCodeReceived(Intent data){
        ean.setText(data.getExtras().getString(ScanActivity.ARG_EAN));
    }

    private void showBookSnack(final String bookTitle, final String eanData){
        if(getActivity()==null)
            return;

        String message = getString(R.string.book_added);

        Snackbar bar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.cancel_button), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelBook(eanData);
                    }
                })
                .setActionTextColor(Color.RED);
        bar.getView().setBackgroundResource(R.color.title_color);
        bar.show();

        lastSnack = bar;
    }

    private void cancelBook(String eanData){
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, eanData);
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        getActivity().getSupportFragmentManager().popBackStack();

        showDiscardedToast();
    }

    private void showDiscardedToast(){
        ean.setText("");
        clearFields();

        Snackbar bar = Snackbar.make(rootView, getString(R.string.book_discarded), Snackbar.LENGTH_SHORT);

        bar.getView().setBackgroundResource(R.color.title_color);
        bar.show();
    }
}
