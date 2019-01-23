package com.tahirietrit.socialapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import com.tahirietrit.socialapp.R;
import com.tahirietrit.socialapp.ui.PaintView;

public class NumberPickerDialog {

    public int brushSize;

    public void showDialog(Activity activity, int minValue, int maxValue, int currentSize, final PaintView pw){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.number_picker_dialog);
        String[] numbers = new String[maxValue];
        for(int i=0; i< numbers.length; i++)
            numbers[i] = Integer.toString(i + 1);
        final NumberPicker np = dialog.findViewById(R.id.numberPicker);
        np.setMinValue(minValue);
        np.setMaxValue(maxValue);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(numbers);
        np.setValue(currentSize);
        Button btn = dialog.findViewById(R.id.numberPickerBttn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brushSize = np.getValue();
                pw.setBRUSH_SIZE(brushSize);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
