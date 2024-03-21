package Avange.apptip;

import java.io.Serializable;

public class UserModel implements Serializable {

        private String userName;
        private String iduser;
        private String codigo;
        private String resena;
        private String idtip;
        private String title;
        private String likes;
        private String lastaccess;
        private String created;

        public UserModel(String userName,String iduser, String codigo, String resena, String idtip, String title, String likes, String lastaccess, String created) {
            this.userName = userName;
            this.iduser = iduser;
            this.codigo = codigo;
            this.resena = resena;
            this.idtip = idtip;
            this.title = title;
            this.likes = likes;
            this.lastaccess = lastaccess;
            this.created = created;
        }
    public UserModel() {
        // Constructor por defecto para llamar a DataSnapshot.getValue(User.class)
    }
    public String getUserName() {
            return userName;
        }
        public void setUserName(String userName) {
            this.userName = userName;
        }
        public String getiduser() {
        return iduser;
    }
        public void setiduser(String iduser) {
        this.iduser = iduser;
    }
        public String getCodigo() {
        return codigo;
    }
        public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
        public String getResena() {
        return resena;
    }
        public void setResena(String resena) {this.resena = resena;}
        public void setIdtip(String idtip) {this.idtip = idtip;}
        public String getIdtip() {return idtip;}

        public void setTitle(String title) {this.title = title;}
        public String getTitle() {return title;}
        public void setLikes(String  likes) {this.likes = likes;}
        public String getLikes() {return likes;}
        public void setLastaccess(String lastaccess) {this.lastaccess = lastaccess;}
        public String getLastaccess() {return lastaccess;}
        public void setCreated(String created) {this.created = created;}
        public String getCreated() {return created;}
}


