package it.jaschke.alexandria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Serloman on 07/08/2015.
 * Example from https://github.com/dm77/barcodescanner
 */
public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    public final static String ARG_EAN = "ARG_EAN";

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setResult(RESULT_CANCELED);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Intent ok = new Intent();
        ok.putExtra(ARG_EAN, result.getText());

        this.setResult(RESULT_OK, ok);

        finish();
    }
}
