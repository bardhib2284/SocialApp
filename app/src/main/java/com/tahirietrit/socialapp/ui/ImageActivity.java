package com.tahirietrit.socialapp.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;

import com.tahirietrit.socialapp.R;
import com.tahirietrit.socialapp.SavePhotoService;
import com.tahirietrit.socialapp.dialogs.ColorPickerDialog;

public class ImageActivity extends AppCompatActivity {

    public PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerpaint);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.Init(metrics);
        Toolbar toolbar = findViewById(R.id.fingerPaintToolbar);
        toolbar.setTitle("Edit Picture");
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.baseline_edit_black_18dp));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.fingerpaintmain, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.savePicture:
                requestStoragePermission();
                return true;
            case R.id.changeColor:
                ColorPickerDialog.ShowDialog(paintView,this);
            case R.id.normal:
                paintView.Normal();
                return true;
            case R.id.undoPath:
                paintView.Undo();
                return true;
            case R.id.redoPath:
                paintView.Redo();
                return true;
            case R.id.emboss:
                paintView.Emboss();
                return true;
            case R.id.blur:
                paintView.Blur();
                return true;
            case R.id.clear:
                paintView.Clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ImageActivity.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                SavePhotoService.insertImage(getContentResolver(),paintView.saveDrawing(),"Photo Title","Photo Desc");
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
