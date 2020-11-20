package firebaseauthdemo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ryanhsueh on 2018/8/22
 */
public class Utils {

    private static final String EMAIL_REGEX =
            "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static boolean validateUsername(String username) {
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public static boolean validatePassword(String password) {
        return password.length() > 6 ;
    }

}
