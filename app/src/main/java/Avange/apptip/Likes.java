package Avange.apptip;

public class Likes {
    public String iduser;
    public String idtip;
    public String lastaccess;
    public Likes(String iduser, String idtip, String lastaccess) {
        this.iduser = iduser;
        this.idtip = idtip;
        this.lastaccess = lastaccess;
    }
    public Likes() {
        // Constructor por defecto para llamar a DataSnapshot.getValue(Likes.class)
    }
    public String getIduser() {
        return iduser;
    }
    public void setIduser(String iduser) {
        this.iduser = iduser;
    }
    public void setIdtip(String idtip) {this.idtip = idtip;}
    public String getIdtip() {return idtip;}
    public void setLastaccess(String lastaccess) {this.lastaccess = lastaccess;}
    public String getLastaccess() {return lastaccess;}
}
