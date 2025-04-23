package my.edu.utar.assignment2;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class YesNoDialogBox {
    public static void show(Context context, String title, String message,
                            String confirmText, String cancelText,
                            View.OnClickListener onConfirm,
                            View.OnClickListener onCancel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.yes_no_dialog_box, null);

        TextView titleText = view.findViewById(R.id.dialogTitle);
        TextView messageText = view.findViewById(R.id.dialogMessage);
        Button confirmButton = view.findViewById(R.id.dialogConfirm);
        Button cancelButton = view.findViewById(R.id.dialogCancel);

        titleText.setText(title);
        messageText.setText(message);
        confirmButton.setText(confirmText);
        cancelButton.setText(cancelText);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        confirmButton.setOnClickListener(v -> {
            onConfirm.onClick(v);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            onCancel.onClick(v);
            dialog.dismiss();
        });
    }
}
