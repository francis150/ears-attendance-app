package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class ScanActivity extends AppCompatActivity {

    TextView branchName, branchDescription, helpText;

    CodeScannerView scannerView;

    Branch branch;

    private CodeScanner qrScanner;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        branchName = (TextView) findViewById(R.id.scan_branch_name);
        branchDescription = (TextView) findViewById(R.id.scan_branch_description);
        helpText = (TextView) findViewById(R.id.scan_help_text);
        scannerView = (CodeScannerView) findViewById(R.id.scan_scanner_view);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        branch = (Branch) getIntent().getSerializableExtra("BRANCH");

        branchName.setText(branch.getName());
        branchDescription.setText(branch.getDescription());


        qrScanner = new CodeScanner(ScanActivity.this, scannerView);
        qrScanner.setAutoFocusEnabled(true);

        qrScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else { 
                            //deprecated in API 26
                            vibrator.vibrate(100);
                        }

                        Intent intent = new Intent(ScanActivity.this, LoadActivity.class);
                        intent.putExtra("EMPLOYEE_PATH", result.getText());
                        intent.putExtra("BRANCH", branch);
                        startActivity(intent);
                    }
                });
            }
        });

        final Runnable onScanViewClicked = new Runnable() {
            @Override
            public void run() {
                qrScanner.releaseResources();
                qrScanner.stopPreview();
                helpText.setText("Tap anywhere to scan.");
            }
        };

        final Handler handler = new Handler();
        handler.postDelayed(onScanViewClicked,10000);

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.removeCallbacks(onScanViewClicked);
                handler.postDelayed(onScanViewClicked, 10000);

                if (qrScanner.isPreviewActive()) {
                    qrScanner.releaseResources();
                    qrScanner.stopPreview();
                    helpText.setText("Tap anywhere to scan.");
                } else {
                    qrScanner.startPreview();
                    helpText.setText("Tap anywhere to stop.");
                }

            }
        });
    }


    @Override
    protected void onPause() {
        qrScanner.releaseResources();
        helpText.setText("Tap anywhere to scan.");
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(ScanActivity.this, "Press back again to do nothing.", Toast.LENGTH_LONG).show();
    }
}