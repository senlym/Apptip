package Avange.apptip;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static java.lang.Character.getType;
import static Avange.apptip.CustomProgress.customProgress;
import static Avange.apptip.MainActivity3.sha256;
import static Avange.apptip.Variables.context;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amrdeveloper.codeview.Code;
import com.amrdeveloper.codeview.CodeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import Avange.apptip.plugin.CommentManager;
import Avange.apptip.plugin.SourcePositionListener;
import Avange.apptip.plugin.UndoRedoManager;
import Avange.apptip.syntax.LanguageManager;
import Avange.apptip.syntax.LanguageName;
import Avange.apptip.syntax.ThemeName;

public class MainActivity extends AppCompatActivity implements UsersAdapter.SelectedUser {

    FirebaseAuth mAuth = null;
    private DatabaseReference mDatabase;
    public ScrollView scrollmain;
    Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver, broadcastReceiver1,broadcastReceiver2;
    RecyclerView recyclerView;

    List<UserModel> userModelList = new ArrayList<>();

    ArrayList<String> nameslist = new ArrayList<String>();
    UsersAdapter usersAdapter;
    EditText etvFind = null;
    DividerItemDecoration divider;

    @SuppressLint({"MissingInflatedId", "SuspiciousIndentation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Seteando la activity
        setContentView(R.layout.activity_main);
        context=this;
        nameslist.clear();
        recyclerView = findViewById(R.id.recyclerview);
        toolbar =  findViewById(R.id.toolbar);
        etvFind = findViewById(R.id.editTextText5);
        CustomProgress customProgress = CustomProgress.getInstance();
        ImageButton salir = findViewById(R.id.imageButton8);
        scrollmain = findViewById(R.id.scrollmain);
        //botón salir
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Variables.logging = false;
                finish();
            }
        });
        //boton buscar
        etvFind.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usersAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //seteo del actionbar
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.divider));
        recyclerView.addItemDecoration(divider);
        //region Declarando boton flotante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       try {
                                           if(!Variables.logging){
                                           startActivity(new Intent(MainActivity.this, MainActivity3.class));
                                           finish();
                                           }
                                           else {
                                               startActivity(new Intent(MainActivity.this, MainActivity1.class));
                                               finish();
                                           }
                                       }catch (Exception e){
                                           String dd = e.getStackTrace().toString();
                                       }
                                   }
                               });
        fab.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_MOVE:
                                view.setX(event.getRawX() - 120);
                                view.setY(event.getRawY() - 425);
                                break;
                            case MotionEvent.ACTION_UP:
                                view.setOnTouchListener(null);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                return true;
            }
        });
        //endregion
        //region Configurando el alertdialog para el host local del firebase
        if(!Variables.sethost) {
            Variables.sethost=true;
            androidx.appcompat.app.AlertDialog.Builder alertName = new androidx.appcompat.app.AlertDialog.Builder(this);
            final EditText editTextName1 = new EditText(this);
            editTextName1.setHint("Teclee el ip del host de firebase local");
            editTextName1.setText(Variables.host);
            alertName.setTitle("Host FireBase Local");
            alertName.setView(editTextName1);
            LinearLayout layoutName = new LinearLayout(this);
            layoutName.setOrientation(LinearLayout.VERTICAL);
            layoutName.addView(editTextName1); // mostrar el edit en el dialog
            alertName.setView(layoutName);
            alertName.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Variables.host = editTextName1.getText().toString(); // variable para almacenar la entrada
                    //authenticando con el usuario default de apk.
                    mAuth = FirebaseAuth.getInstance();
                    if(!Variables.host.equals("0.0.0.0")) mAuth.useEmulator(Variables.host, 9099);
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if(currentUser==null)
                        if(!Variables.host.equals("0.0.0.0")) mAuth.useEmulator(Variables.host, 9099);
                    customProgress.showProgress(MainActivity.this,"Conectando...",false);
                    // Autenticar el usuario apkuser en FireBase
                    mAuth.signInWithEmailAndPassword("apkuser@d.com", "123456789")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Usuario autenticado con exito
                                    Log.e("Autenticado","Exito") ;
                                    CargarDatosDB();
                                } else{
                                    // No autenticado el usuario
                                    String exp = task.getException().getMessage();
                                    Log.e("Autenticado",task.getException().getMessage()) ;
                                    Toast.makeText(context,"Error en la conexión",Toast.LENGTH_SHORT).show();
                                    customProgress.hideProgress();
                                }
                                //endregion
                            });
                }
            });
            alertName.setNegativeButton("Internet Server", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Variables.host = "0.0.0.0"; // variable para almacenar la entrada
                    //authenticando con el usuario default de apk.
                    mAuth = FirebaseAuth.getInstance();
                    if(!Variables.host.equals("0.0.0.0")) mAuth.useEmulator(Variables.host, 9099);
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if(currentUser==null)
                        if(!Variables.host.equals("0.0.0.0")) mAuth.useEmulator(Variables.host, 9099);
                    customProgress.showProgress(MainActivity.this,"Conectando...",false);
                    // Autenticar el usuario apkuser en FireBase
                    mAuth.signInWithEmailAndPassword("apkuser@d.com", "123456789")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Usuario autenticado con exito
                                    Log.e("Autenticado","Exito") ;
                                    CargarDatosDB();
                                } else{
                                    // No autenticado el usuario
                                    String exp = task.getException().getMessage();
                                    Log.e("Autenticado",task.getException().getMessage()) ;
                                    Toast.makeText(context,"Error en la conexión",Toast.LENGTH_SHORT).show();
                                    customProgress.hideProgress();
                                }
                                //endregion
                            });
                }
            });
            alertName.show(); // mostrar the dialog;
        }
        //endregion
    }
    //probando json desde assets
    public static String getAssetJsonData(Context context) {
        String json = null;

        try {
            InputStream is = context.getAssets().open("user.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Log.e("data", json);
        return json;
    }
    //Cargando los datos a la Mainactivity
    private void CargarDatosDB(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if(!Variables.host.equals("0.0.0.0")) database.useEmulator(Variables.host, 9015);
        mDatabase = database.getReference();
           customProgress.hideProgress();
           customProgress.showProgress(this, "Cargando Datos...", false);
           try {
                mDatabase.child("Tips").orderByChild("likes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    UserModel userModel=null;
                    if (!task.isSuccessful()) {
                        if(task.getException().getMessage().equals("Permission denied")){

                        };
                        Log.e("firebase", "Error getting data", task.getException());
                        customProgress.hideProgress();
                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        // Obteniedo los datos de los tips desde firebase
                        for (DataSnapshot childDataSnapshot : task.getResult().getChildren()) {
                            userModel = childDataSnapshot.getValue(UserModel.class);
                            userModelList.add(userModel);
                        }
                        if(userModelList != null){
                        usersAdapter = new UsersAdapter(userModelList,MainActivity.this);
                        recyclerView.setAdapter(usersAdapter);
                        }
                        customProgress.hideProgress();
                    }
                }
            });

        } catch (Exception e) {
            userModelList = null;
            new Thread(() -> mailException.sendMail(e.getStackTrace().toString())).start();
            customProgress.hideProgress();
        }

    }
    //region Pasar parámetros desde otras clases o Actividades
    @Override
    protected void onResume() {
        super.onResume();
        try{
        IntentFilter intentFilter = new IntentFilter(
                "FINISHED");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // show notification
                if(intent.getAction().equals("FINISHED")){
                CargarDatosDB();
                customProgress.hideProgress();
                Variables.context.sendBroadcast(new Intent("Noaction"));
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }catch (Exception e){
            Log.d("onResume",e.getStackTrace().toString());
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        try{
        unregisterReceiver(broadcastReceiver);
    }catch (Exception e){
            Log.d("onPause",e.getStackTrace().toString());
        }
    }
    //endregion
    public boolean checkStoragePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11 (R) or above
            return Environment.isExternalStorageManager();
        }
        else return false;
    }
    @Override
    public void selectedUser(UserModel userModel) {
        userModel.getUserName();
        //startActivity(new Intent(MainActivity.this,Selected_User.class).putExtra("data",userModel));
    }

    @Override
    public void selectedText(String text) {
        startActivity(new Intent(MainActivity.this,detalle.class).putExtra("text",text));
    }
}