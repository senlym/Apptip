package Avange.apptip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class UserData {

    private List<UserModel> Tip;

    public List<UserModel> getTip() {
        return Tip;
    }

    public void setTip(List<UserModel> Tip) {
        this.Tip = Tip;
    }

}
