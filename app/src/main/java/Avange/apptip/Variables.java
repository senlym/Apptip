package Avange.apptip;
import android.content.Context;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
/**
 * Created by senly on 21/02/2024.
 */
public class Variables {
    public static FirebaseUser userfire = null;
    public static boolean logging = false;
    public static boolean sethost = false;
    public static String  hashDirectory= "c920494594148cbf41f45042e6033db5d33bb717c86a844e77042042b3954d8c.sd/";
    public static String username="";
    public static String host="";
    public static String currentTitle="";
    public static String iduser="";
    public static String mail="";
    public static long ExpierdTime=0;
    public static Context context = null;
    public static ArrayList ramdomizeArray(){
        ArrayList<Integer> tmp = new ArrayList<Integer>();
        tmp.add(R.drawable.knife_10387747);
        tmp.add(R.drawable.knife_10387748);
        tmp.add(R.drawable.knife_10387749);
        tmp.add(R.drawable.knife_10387750);
        tmp.add(R.drawable.knife_10387751);
        return tmp;
    };
    public static User user = null;
    public static TextView languageNameText = null;
    }
