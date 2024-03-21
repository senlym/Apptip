package Avange.apptip;

public class TipModel {
    public String userName;
    public String iduser;
    public String codigo;
    public String resena;
    public String idtip;

    public String title;
    public String  likes;
    public String lastaccess;
    public String created;

        public TipModel(String userName,String iduser, String codigo, String resena, String idtip, String title, String  likes, String lastaccess, String created) {
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
        public TipModel() {
            // Constructor por defecto para llamar a DataSnapshot.getValue(TipModel.class)
        }
        public String getUserName() {
            return userName;
        }
        public void setUserName(String userName) {
            this.userName = userName;
        }
        public String getIduser() {
            return iduser;
        }
        public void setIduser(String iduser) {
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
        public void setLikes(String likes) {this.likes = likes;}
        public String  getLikes() {return likes;}
        public void setLastaccess(String lastaccess) {this.lastaccess = lastaccess;}
        public String getLastaccess() {return lastaccess;}
        public void setCreated(String created) {this.created = created;}
        public String getCreated() {return created;}
}
