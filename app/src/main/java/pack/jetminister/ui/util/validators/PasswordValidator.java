package pack.jetminister.ui.util.validators;

import androidx.appcompat.widget.DialogTitle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

   private static final Pattern VALID_PASSWORD_PATTERN_REGEX = Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=\\S+$)" +           //no white spaces
            ".{6,}" +               //at least 8 characters
            "$");

    public static boolean validate(String password) {
        Matcher matcher = VALID_PASSWORD_PATTERN_REGEX.matcher(password);
        return matcher.matches();
    }
}

