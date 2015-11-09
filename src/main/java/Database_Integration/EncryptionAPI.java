package Database_Integration;

import sun.security.validator.ValidatorException;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** This is used to encrypt and decrypt any sensitive variables with a Password, database info, etc
 * It uses a Singleton design pattern to use as a static object between all classes
 */

public class EncryptionAPI {
    private static EncryptionAPI firstInstance = null;

    private static Key symmKey;
    private static Cipher cipher;

    private EncryptionAPI() {}

    protected static EncryptionAPI getInstance() throws NoSuchAlgorithmException, NoSuchPaddingException {

        if(firstInstance == null) {
            firstInstance = new EncryptionAPI();       // if there is no firstInstance yet, a new EncryptionAPI is created as the one and only object as firstInstance

            KeyGenerator keyGen = KeyGenerator.getInstance("Blowfish");     // when the firstInstance is created, so is the Blowfish encryption key
            keyGen.init(128);
            symmKey = keyGen.generateKey();           // it is 128 bits and the key is made an instance variable for the singleton

            cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");     // a Cipher for the Blowfish encryption is also created with an Electronic Code Book (ECB) encryption mode and PKCS5 padding for extra bits in the last encryption block
        }

        return firstInstance;
    }

    protected byte[] encrypt(String text) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] plainText = text.getBytes();

        cipher.init(Cipher.ENCRYPT_MODE, symmKey);
        byte[] cipherText = cipher.doFinal(plainText);

        return cipherText;
    }

    protected String decrypt(byte[] cipherText) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, symmKey);
        byte[] plainText = cipher.doFinal(cipherText);

        return new String(plainText);
    }

    protected static String filterString(String str) {
        String s = Normalizer.normalize(str, Form.NFKC);          // first normalizes the String input by normalizing it to the NFKC form that first uses Compatibility Decomposition and then uses Canonical Composition

        // Replaces all noncharacter code points with Unicode U+FFFD
        s = s.replaceAll("[\\p{Cn}]", "\uFFFD");         // this replacement of unknown characters to Unicode ensures no malicious script can bypass the validation filter by being in unknown characters

        // Validate Input
        Pattern pattern = Pattern.compile("<script>");        // this looks for a <script> pattern in the input to ensure no script is run
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()) {
            BusinessLayer bl = new BusinessLayer();
            bl.alertBox("Malicious <script> Input Detected");
            throw new IllegalArgumentException();
        }

        try {

            return s;
        } finally {
            str = null;
            s = null;              // str and s are made to null after so that they can be garbage collected if they are passwords
        }
    }

    protected static String filterPasswords(String str) throws ValidatorException {
        BusinessLayer bl = new BusinessLayer();

        if(str.length() <= 7 || str.length() >= 16) {
            bl.alertBox("The password needs to be at least 8 characters and can be no more than 15 characters long.");
            throw new ValidatorException("Password too long or short.");
        }

        int count = 0;
        for(int i = 0; i < str.length(); i++) {
            if(str.substring(i, i+1).matches("[^A-Za-z0-9 ]")) {
                count++;
                if(count == 2) {
                    bl.alertBox("There can only be one character in the password.");
                    throw new ValidatorException("Can only have one character in a password.");
                }
            }
        }
        try {
            return str;
        } finally {
            str = null;       // str is made null so it can be garbage collected when System.gc() is called
        }
    }

    protected static String filterSearch(String str) throws ValidatorException {
        BusinessLayer bl = new BusinessLayer();

        if(str.length() > 30) {
            bl.alertBox("Search input cannot be more than 30 characters.");
            throw new ValidatorException("Cannot be more than 30 characters.");
        }

        if(str.matches("[^A-Za-z0-9 ]")) {
            bl.alertBox("Cannot enter any special characters.");
            throw new ValidatorException("Cannot enter any special characters.");
        }
        int count = 0;
        for(int i = 0; i < str.length(); i++) {
            if(Character.isWhitespace(str.charAt(i))) {
                count++;
                if(count == 2) {
                    bl.alertBox("Cannot enter more than one whitespace between a first and last name.");
                    throw new ValidatorException("Only one whitespace allowed.");
                }
            }
        }

        return str;
    }

    protected static String filterInput(String str) {


        return str;
    }

    public static void main(String[] args) throws ValidatorException {
        String maliciousInput = "<scr" + "\uFDEF" + "ipt>";     // the middle part of the malicious String tries to bypass the filter by using unknown characters to Unicode to complete a "<script>"
        String s = filterString(maliciousInput);
        System.out.println(s);
        System.out.println(filterPasswords("Jackpot1%"));
        System.out.println(filterSearch("Michael Bitner"));
        //System.out.println(filterString("<script>"));       // this will throw an exception
    }
}
