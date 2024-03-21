package Avange.apptip;

import static Avange.apptip.CustomProgress.customProgress;
import static Avange.apptip.Variables.mail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity3 extends AppCompatActivity {
    Context context= this;
    int width = 0;
    Button button = null;
    EditText username, user, pass;
    ProgressDialog progressBar;
    TextView textView;
    private FirebaseAuth mAuth;
    boolean estado = true, initial = false, continued = false;
    LinearLayout userlaout;
    FirebaseUser currentUser;
    EditText match;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        button = findViewById(R.id.button1);
        textView = findViewById(R.id.createaccount);
        username = findViewById(R.id.nick);
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        userlaout = findViewById(R.id.userlayaout);
        ImageView imageView = findViewById(R.id.imageView4);
        match = findViewById(R.id.match);
        //region Autenticando por Usuario y Contraseña en FireBase
        try {
            mAuth = FirebaseAuth.getInstance();
            if(!Variables.host.equals("0.0.0.0")) mAuth.useEmulator(Variables.host, 9099);
            currentUser = mAuth.getCurrentUser();
            if (currentUser != null && initial) {
                // Usuario autenticado
                Variables.username = currentUser.getDisplayName();
                mail = currentUser.getEmail();
                customProgress.hideProgress();
                Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show();
                Variables.logging = true;
                startActivity(new Intent(context, MainActivity.class));
                finish();
            } else {
                // Usuario no autenticado
                if(initial) {
                    mail = user.getText().toString();
                    customProgress.hideProgress();
                    Variables.logging = false;
                    MessageBox("Usuario no Registrado", "El usuario no se encuentra en nuestra base de datos");
                }else {
                    initial=true;
                }
            }
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            if(!Variables.host.equals("0.0.0.0")) database.useEmulator(Variables.host, 9015);
            mDatabase = database.getReference();
        } catch (Exception e){
            MessageBox("Error", "Ha ocurrido un error al conectarse a la Base de Datos");
        }
        match.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(!pass.getText().toString().equals(match.getText().toString())){
                       MessageBox("Error en Contraseña","Por favor repita la contraseña porque no coincide...");
                    }
                }
            }
        });
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    if(estado) {
                        width = username.getWidth();
                    }
                    view.getLayoutParams().width =(int) (width *0.95);
                    view.requestLayout();
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.denied);
                    estado =false;
                } else {
                    imageView.setImageResource(R.drawable.checkbox);
                }

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView.getText().equals("No Tienes Cuenta? Registrar")){
                    textView.setText(R.string.signup_login_hint);
                    button.setText(R.string.button);
                    username.setVisibility(View.VISIBLE);
                    match.setVisibility(View.VISIBLE);
                } else{
                    textView.setText(R.string.login_signup_hint);
                    button.setText(R.string.registered);
                    username.setVisibility(View.GONE);
                    match.setVisibility(View.GONE);
                }
            }
        });
        //endregion
        //region Acción de Autenticación
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    if(button.getText().toString().equals("Entrar"))
                        username.setText(user.getText().toString().substring(0,user.getText().toString().indexOf('@')));
                    if(TextUtils.isEmpty(username.getText().toString())
                            || username.getText().toString().trim().length() == 0
                            || TextUtils.isEmpty(user.getText().toString())
                            || user.getText().toString().trim().length() == 0
                            || TextUtils.isEmpty(pass.getText().toString())
                            || pass.getText().toString().trim().length() == 0
                    ){
                        MessageBox("Error en Datos","Faltan por declarar datos, todos son obligatorios");
                    }
                    else {
                        Variables.logging=false;
                        CustomProgress customProgress = CustomProgress.getInstance();
                        customProgress.showProgress(MainActivity3.this, "Conectando Espere...", false);
                        //region Crear el usuario con Email y contraseña en FireBase
                        if(button.getText().equals("Registrar")){
                            assert mail != null;
                            mAuth.createUserWithEmailAndPassword(user.getText().toString(), sha256(pass.getText().toString()))
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            // Usuario autenticado
                                            Variables.username = currentUser.getDisplayName();
                                            mail = currentUser.getEmail();
                                            Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show();
                                            Variables.logging = true;
                                            user.sendEmailVerification();
                                            //Escribir en DB la clase user para dar el alta
                                            User newuser = new User();
                                            newuser.userid = currentUser.getUid();
                                            newuser.username = username.getText().toString();
                                            newuser.email = currentUser.getEmail();
                                            newuser.activate = currentUser.isEmailVerified();
                                            newuser.lastaccess = Utils.fecha("yyyy-MM-dd hh:mm:ss");
                                            Variables.user = newuser;
                                            storeUserData(newuser);
                                            customProgress.hideProgress();
                                            Toast.makeText(MainActivity3.this,"Revise su email y verifique su cuenta!!!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(context, MainActivity.class));
                                            finish();
                                        } else {
                                            // Fallo al autenticase el usuario
                                            customProgress.hideProgress();
                                            String ddd = task.getException().getMessage();
                                            MessageBox("Ocurrió un error",ddd);
                                        }
                                    });
                        } else{
                            // Autenticar el usuario registrado con Email y contraseña en FireBase
                            mAuth.signInWithEmailAndPassword(user.getText().toString(), sha256(pass.getText().toString()))
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Usuario autenticado con exito
                                            Variables.userfire = mAuth.getCurrentUser();
                                            customProgress.hideProgress();
                                            Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show();
                                            Variables.logging = true;
                                            Variables.username = username.getText().toString();
                                            readUserData();
                                            startActivity(new Intent(context, MainActivity.class));
                                            finish();
                                        } else {
                                            // Login failed
                                            customProgress.hideProgress();
                                            Variables.logging = false;
                                            String typeExcep = task.getException().getMessage();
                                            typeExcep = "No coincide el usuario o contraseña";
                                            MessageBox("Error al autenticarse",typeExcep);
                                        }
                                    });
                            }
                        //endregion
                        }
                }catch (Exception e) {
                    MessageBox("Error al Entrar a la plataforma", "Revise usuario y contraseña por favor");
                }
                //endregion
            }
        });
        //endregion
    }
    //Función Codificar para la contraseña
    public static String sha256(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    //Función para personalizar los mensajes
    public void MessageBox (String title, String message) {
        // Creando un AlertDialog Builder
        MaterialAlertDialogBuilder Alert = new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3_Animation);
        Alert.setTitle(title);
        Alert.setIcon(R.drawable.clipart1451144);
        Alert.setMessage(message);
        Alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        Alert.show(); // display the Dialog
    }
    //Almacenar usuario creado en el hijo Users
    private void storeUserData(User user) {
        AtomicBoolean st = new AtomicBoolean(false);
        mDatabase.child("Users").push().setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "Se insertó el registro en la base de datos");
                        st.set(true);
                        continued = true;
                    } else {
                        Log.e("TAG", "Ocurrió un error al insertar el registro en la base de datos");
                        String fkg = task.getException().getStackTrace().toString();
                        st.set(false);
                        continued = true;
                    }
                });
    }
    //Leyendo datos del usuario
    private void readUserData(){
        Variables.user = new User();
        mDatabase.child("Users").orderByChild("email").equalTo(Variables.userfire.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    // Obteniedo los datos de usuario desde firebase
                    for (DataSnapshot childDataSnapshot : task.getResult().getChildren()) {
                        Variables.user = childDataSnapshot.getValue(User.class);
                    }
                    try{
                        Intent intent1 = new Intent("FINISHED");
                        Variables.context.sendBroadcast(intent1);
                    }catch (Exception e){
                        Log.d("onResume",e.getStackTrace().toString());
                    }
                }
            }
        });
    }
}