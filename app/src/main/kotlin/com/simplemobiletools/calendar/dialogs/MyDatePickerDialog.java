package com.simplemobiletools.calendar.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.simplemobiletools.calendar.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MyDatePickerDialog extends DatePickerDialog {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MyDatePickerDialog(@NonNull Context context, @Nullable DatePickerDialog.OnDateSetListener listener,
                              int year, int month, int dayOfMonth) {
        this(context, 0, listener, null, year, month, dayOfMonth);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private MyDatePickerDialog(@NonNull Context context, @StyleRes int themeResId,
                               @Nullable DatePickerDialog.OnDateSetListener listener, @Nullable Calendar calendar, int year,
                               int monthOfYear, int dayOfMonth) {

        super(context, resolveDialogTheme(context, themeResId));

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.date_picker, null);
        setView(view);

        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);

        if (calendar != null) {
            year = calendar.get(Calendar.YEAR);
            monthOfYear = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }

        DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        datePicker.init(year, monthOfYear, dayOfMonth, this);

        try {
            // Install our DatePicker into the dialog.
            findField(DatePickerDialog.class, "mDatePicker").set(this, datePicker);

            // Replace the following from DatePickerDialog with our logic:
            //     setButtonPanelLayoutHint(LAYOUT_HINT_SIDE);
            // This code just sets what is already defaulted. Uncomment, if needed.
//            final Class alertDialogClass = pickerDialogClass.getSuperclass();
//            final int layoutHint = findField(alertDialogClass, "LAYOUT_HINT_NONE").getInt(null);
//            Method method = findMethod(alertDialogClass, "setButtonPanelLayoutHint", int.class);
//            method.invoke(this, layoutHint);

            // Replace the following line with our logic:
            //     mDatePicker.setValidationCallback(mValidationCallback);
            Field field = findField(getClass().getSuperclass(), "mValidationCallback");
            Method method = findMethod(datePicker.getClass(), "setValidationCallback", field.getType());
            method.invoke(getDatePicker(), field.get(this));
        } catch (Exception e) { // Just ignore errors
            e.printStackTrace();
        }
        setOnDateSetListener(listener);
    }

    private Field findField(Class objectClass, String fieldName) throws NoSuchFieldException {
        Field field = objectClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    private Method findMethod(Class<?> objectClass, String methodName, Class<?> parmTypes)
            throws NoSuchMethodException {
        Method method = objectClass.getDeclaredMethod(methodName, parmTypes);
        method.setAccessible(true);
        return method;
    }

    private static
    @StyleRes
    int resolveDialogTheme(@NonNull Context context, @StyleRes int themeResId) {
        if (themeResId == 0) {
            final TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.datePickerDialogTheme, outValue, true);
            return outValue.resourceId;
        } else {
            return themeResId;
        }
    }
}