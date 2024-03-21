package Avange.apptip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
//Esta clase se usa para los tiempos de carga de la aplicación y es configurable.
public class CustomProgress {

    public static CustomProgress customProgress = null;
    private Dialog mDialog;
    private ProgressBar mProgressBar;

    public static CustomProgress getInstance() {
        if (customProgress == null) {
            customProgress = new CustomProgress();
        }
        return customProgress;
    }
    public void showProgress(Context context, String message, boolean cancelable) {
        mDialog = new Dialog(context);
        // Dialógo sin título
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.prograss_bar_dialog);
        mProgressBar = (ProgressBar) mDialog.findViewById(R.id.progress_bar);
        TextView progressText = (TextView) mDialog.findViewById(R.id.progress_text);
        progressText.setText("" + message);
        progressText.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        // seteando el progressbar
        mProgressBar.setIndeterminate(true);
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }
    //ocultar el progressbar
    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}