package com.tahirietrit.socialapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tahirietrit.socialapp.BR;
import com.tahirietrit.socialapp.SavePhotoService;
import com.tahirietrit.socialapp.model.FingerPath;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PaintView extends View {

    public int BRUSH_SIZE = 10;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX,mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private ArrayList<FingerPath> editedPaths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    private Bitmap bitmap;
    public PaintView(Context ctx)
    {
        super(ctx);
    }

    private boolean cleared;
    public PaintView(Context ctx, AttributeSet attrs)
    {
        super(ctx,attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[] {1,1,1},0.4f,6,3.5f);
        mBlur = new BlurMaskFilter(5,BlurMaskFilter.Blur.NORMAL);
        setDrawingCacheEnabled(true);
    }

    public  void Init(DisplayMetrics metrics)
    {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void Normal()
    {
        emboss = false;
        blur = false;
    }

    public void Emboss()
    {
        emboss = true;
        blur = false;
    }

    public void Blur()
    {
        emboss = false;
        blur = true;
    }

    public void Clear()
    {
        System.out.println("Clearing");
        backgroundColor = DEFAULT_BG_COLOR;
        System.out.println("Current paths size : " + paths.size());
        System.out.println("Current editedPaths size : " + editedPaths.size());
        editedPaths = new ArrayList<>(paths);
        System.out.println("editedPaths size : " + editedPaths.size());
        paths.clear();
        System.out.println("After clearing paths size : " + paths.size());
        draw(mCanvas);
        Normal();
        invalidate();
        cleared = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.save();
        mCanvas.drawColor(backgroundColor);
        for (FingerPath fp : paths)
        {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);

            if(fp.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if (fp.blur)
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(fp.path,mPaint);
        }
        canvas.drawBitmap(mBitmap,0,0,mBitmapPaint);
        canvas.restore();
        mCanvas.restore();
    }

    private void touchStart(float x, float y)
    {
        mPath = new Path();
        System.out.println(strokeWidth + " current brush size ");
        FingerPath fp = new FingerPath(currentColor,emboss,blur,strokeWidth,mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x,y);
        mX = x;
        mY = y;
        editedPaths.clear();
    }

    private void touchMove(float x,float y)
    {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        editedPaths.clear();
    }

    private void touchUp()
    {
        mPath.lineTo(mX,mY);
        editedPaths.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touchStart(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    public void SetCurrentColor(int color)
    {
        currentColor = color;
    }

    @SuppressLint("WrongThread")
    public Bitmap saveDrawing()
    {
        if (bitmap != null) {
            System.out.println("Bitmap not null!");
            bitmap.recycle();
            setDrawingCacheEnabled(false);
            setDrawingCacheEnabled( true );
        }

        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        bitmap = getDrawingCache();
        // don't forget to clear it (see above) or you just get duplicate
        // almost always you will want to reduce res from the very high screen res
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        byte[] yourByteArray;
        yourByteArray = baos.toByteArray();

        return bitmap;
    }

    @SuppressLint("WrongThread")
    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(getContext(), new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

    }

    public void Undo()
    {
        if(!(paths.size() > 0))
        {
            if(editedPaths.size() >= 1 && cleared)
            {
                paths = new ArrayList<>(editedPaths);
                draw(mCanvas);
                Normal();
                invalidate();
                editedPaths.clear();
                cleared = false;
                return;
            }
            Toast.makeText(getContext().getApplicationContext(),"No Steps To Undo",Toast.LENGTH_SHORT).show();
            return;
        }
        FingerPath undoedPath = paths.get(paths.size() - 1);
        paths.remove(paths.size() - 1);
        draw(mCanvas);
        Normal();
        invalidate();
        Toast.makeText(getContext().getApplicationContext(),"Undo Successfully 1 step behind",Toast.LENGTH_SHORT).show();
        editedPaths.add(undoedPath);
        System.out.println("Edited Paths : " + editedPaths.size());
    }

    public void Redo()
    {

        if (editedPaths.size() >= 1)
        {
            System.out.println("Paths size before redo " + paths.size());
            paths.add(editedPaths.get(editedPaths.size() - 1));
            editedPaths.remove(editedPaths.size() - 1);
            System.out.println("Paths size after redo " + paths.size() + " edited paths size : " + editedPaths.size());
            draw(mCanvas);
            Normal();
            invalidate();
            Toast.makeText(getContext().getApplicationContext(),"Redo Successfully 1 step behind",Toast.LENGTH_SHORT).show();
        }
        else  Toast.makeText(getContext().getApplicationContext(),"No Steps To Redo",Toast.LENGTH_SHORT).show();
    }

    public void setBRUSH_SIZE(int brush_size)
    {
        BRUSH_SIZE = brush_size;
        strokeWidth = BRUSH_SIZE;
    }
}
