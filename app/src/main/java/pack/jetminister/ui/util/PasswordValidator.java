package pack.jetminister.ui.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    private static final Pattern VALID_PASSWORD = Pattern.compile("^(?=.*[0-9])(?=.*[A-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public static boolean validatePassword(String password) {
        Matcher matcher = VALID_PASSWORD.matcher(password);
        return matcher.find();
    }
}
