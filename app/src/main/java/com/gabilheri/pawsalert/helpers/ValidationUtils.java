package com.gabilheri.pawsalert.helpers;

import com.gabilheri.pawsalert.PawsApp;
import com.gabilheri.pawsalert.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/8/16.
 */
public final class ValidationUtils {

    private ValidationUtils() {}

    /**
     * Validates where a Email address is valid or not
     * @param email
     *      Email address to be validated
     * @return
     *      null if is a valid email
     */
    public static String isValidEmailAddress(String email) {
        Pattern p = java.util.regex.Pattern.compile(Const.EMAIL_REGEX);
        Matcher m = p.matcher(email);
        return m.matches() ? null : PawsApp.instance().getString(R.string.invalid_email);
    }

    /**
     * Validates where this password is valid or not.
     *
     * The default behavior is:
     *      Min Chars: 6
     *      Min Numbers: 1
     *
     * @param password
     *      The password to be validated
     * @return
     *      null if is a valid password
     */
    public static String isValidPassword(String password) {
        if (password.length() >= Const.MIN_PASSWORD_SIZE) {
            if(!hasNumbers(password, Const.MIN_PASSWORD_NUMBERS)) {
                return PawsApp.instance().getString(R.string.password_number);
            }
        } else {
            return PawsApp.instance().getString(R.string.password_min_chars);
        }
        return null;
    }

    /**
     * Validates if a String has a required amoun of numbers
     *
     * @param str
     *      The string to be validated
     * @param requiredNumbers
     *      The number of numbers that the string must have
     * @return
     *      True if has the requiredNumber of numbers
     */
    public static boolean hasNumbers(String str, int requiredNumbers) {
        int numbers = 0;
        for(int i = 0; i < str.length(); i++) {
            if(Character.isDigit(str.charAt(i))) {
                numbers++;
            }
        }
        return numbers >= requiredNumbers;
    }

    /**
     * Simple validation of a Phone Number
     * Just checks the size of all the numbers
     *
     * @param phoneNumber
     *      The phone number to be validades
     * @return
     *      null if the Phone number is valid
     */
    public static String isValidPhoneNumber(String phoneNumber) {
        StringBuilder numericBuilder = new StringBuilder();
        for(int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            if (Character.isDigit(c)) {
                numericBuilder.append(c);
            }
        }
        if (numericBuilder.length() < 9) {
            return PawsApp.instance().getString(R.string.phone_number_error);
        }
        return null;
    }

    public static String validateName(String name) {
        String[] names = name.split(" ");
        if (names.length == 2) {
            if (names[0].length() < 2) {
                return "First name must be at least 2 characters.";
            }
            if (names[1].length() < 2) {
                return "Last name must be at least 2 characters.";
            }
            return  null;
        } else {
            return "Please specify a first name and a last name";
        }
    }

    /**
     * Checks if both passwords match or not
     * @param password
     *      The password
     * @param confirmPassword
     *      Confirmation of the password
     * @return
     *      null if both passwords match
     */
    public static String passwordsMatch(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return null;
        }
        return PawsApp.instance().getString(R.string.passwords_match);
    }
}