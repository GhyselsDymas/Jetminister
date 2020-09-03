package pack.jetminister.ui.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*A class used to match a regex pattern with an email address*/
public class EmailValidator {

    public static final Pattern VALID_EMAIL_ADDRESS =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public EmailValidator() {
    }

    public static boolean validate(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS.matcher(email);
        return matcher.find();
    }
}
