package Avange.apptip;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Utils {
    public static String fecha(String patern){
        String currentDate= new SimpleDateFormat(patern, Locale.getDefault()).format(new Date());
        return currentDate;
    }
    public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            content = new String(Files.readAllBytes(Paths.get(filename)));
        }
        else {

        }
        return new JSONObject(content);
    }
    public static void jsonObjet(String file) throws IOException, JSONException {
        String filename = Variables.context.getFilesDir()+"/"+file;
        JSONObject jsonObject = parseJSONFile(filename);
    }
    public static String dayMonthyear(String end, String startpub) {
        // Define format
        Date start = new Date(startpub);
        Date ending = new Date(end);
        String daymonthyear="";
        int diffInDays = (int)( (ending.getTime() - start.getTime())
                / (1000 * 60 * 60 * 24) );

                if (diffInDays<32){
                  daymonthyear = String.valueOf(diffInDays)+"d·";
                }
                if (diffInDays>31 && diffInDays<366){
                    daymonthyear = String.valueOf(Math.round(diffInDays/31))+"M·";
                }
                if (diffInDays>365){
                    daymonthyear = String.valueOf(Math.round(diffInDays/365))+"Y·";
                }
        return daymonthyear;
        }
    public static String tiempo(){
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        return currentTime;
    }
    public static byte[] readContentIntoByteArray(File file)
    {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try
        {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bFile;
    }
    public static String name(Context context, String filename){
        String name=null;
        try{
        File file = new File(context.getFilesDir(),filename);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null){
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
            String responce = stringBuilder.toString();
            JSONObject jsonObject  = new JSONObject(responce);
            name = jsonObject.get("mail").toString();
        }catch (Exception e){
            String error= e.getMessage();
            new Thread(() -> mailException.sendMail(e.getMessage())).start();
        }
        return name;
    }
    public static JSONObject DevuelveJson(Context context, String filename){
        String name=null;
        JSONObject jsonObject=null;
        try{
            File file = new File(context.getFilesDir(),filename);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            String responce = stringBuilder.toString();
            jsonObject  = new JSONObject(responce);
        }catch (Exception e){
            String error= e.getMessage();
            new Thread(() -> mailException.sendMail(e.getMessage())).start();
        }
        return jsonObject;
    }
}
