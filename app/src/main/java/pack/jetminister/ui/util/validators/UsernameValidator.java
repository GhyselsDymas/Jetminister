package pack.jetminister.ui.util.validators;

import android.widget.Toast;

import pack.jetminister.R;
import pack.jetminister.ui.LoginOrRegister;

public class UsernameValidator {
    public static boolean validateUsername(String username) {
        if (username.isEmpty()){
            return false;
        } else if (username.length()>16){
            return false;
        } else return !username.contains(" ");
    }
}
