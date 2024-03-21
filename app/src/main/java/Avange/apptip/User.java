package Avange.apptip;
import java.util.Date;
import java.util.List;

public class  User {
    public String username;
    public String email;
    public String lastaccess;
    public String userid;
    public boolean activate;
    public User() {
        // Constructor por defecto para llamar a DataSnapshot.getValue(User.class)
    }
    public User (String id,String username, String email, String lastaccess, boolean activate) {
        this.userid = id;
        this.username = username;
        this.email = email;
        this.lastaccess= lastaccess;
        this.activate = activate;
    }
    public String getEmail() {
        return email;
    }
    public boolean isActivate() {
        return activate;
    }
    public String getLastaccess() {
        return lastaccess;
    }

    public String getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }
}
