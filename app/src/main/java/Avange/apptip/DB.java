package Avange.apptip;

import static androidx.core.content.ContextCompat.startActivity;

import static Avange.apptip.CustomProgress.customProgress;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
//Esta clase es la encargada de manejar la BD
public class DB {
    //Guardando el Tip en la base de datos
    public void storeTipModel (Context context, TipModel storage, DatabaseReference mDatabase) {
        try {

               mDatabase.child("Tips").push().setValue(storage)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Database", "Se insertó el registro en la base de datos");
                            Toast.makeText(context,"Creado el Tip con exito",Toast.LENGTH_SHORT).show();

                        } else {
                        Toast.makeText(context,"Fallo al crear el Tip",Toast.LENGTH_SHORT).show();
                        Log.e("Database", "Ocurrió un error al insertar el registro en la base de datos");
                            String tag = task.getException().getStackTrace().toString();
                            mailException.sendMail(tag);
                        }
                    });

        }catch (Exception e){
           Log.e("Database",e.getStackTrace().toString());
        }
    }
    //Guardando los likes en la BD
    public void storeLikes (Context context,String idtip,String iduser, DatabaseReference mDatabase, long d) {
        try {
            mDatabase.child("Likes").child(idtip).child("like").setValue(d);
            mDatabase.child("Likes").child(idtip).child(iduser).setValue("");
            Toast.makeText(context,"Like!!!",Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Log.e("Database",e.getStackTrace().toString());
        }
    }
    // Quitar los likes
    public void removeLikes (String iduser, DatabaseReference mDatabase) {
        try {
            mDatabase.child(iduser).removeValue();
        }catch (Exception e){
            Log.e("Database",e.getStackTrace().toString());
        }
    }
}
