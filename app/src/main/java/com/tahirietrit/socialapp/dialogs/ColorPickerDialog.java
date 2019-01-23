package com.tahirietrit.socialapp.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.tahirietrit.socialapp.ui.PaintView;

public class ColorPickerDialog {

    public static void ShowDialog(final PaintView paintView, Context ctx)
    {
        ColorPickerDialogBuilder
                .with(ctx)
                .setTitle("Choose color")
                .initialColor(paintView.DEFAULT_COLOR)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        paintView.SetCurrentColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }
}