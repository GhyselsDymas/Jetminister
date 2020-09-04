package pack.jetminister.ui.util;

import androidx.appcompat.widget.DialogTitle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {


    private static final Pattern PASSWORD_PATTERN_REGEX = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})");

    public static boolean validatePassword(String password) {
        Matcher matcher = PASSWORD_PATTERN_REGEX.matcher(password);
        return matcher.matches();

    }
}

