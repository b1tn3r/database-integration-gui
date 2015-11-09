package Database_Integration;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
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

        return s;
    }

    public static void main(String[] args) {
        String maliciousInput = "<scr" + "\uFDEF" + "ipt>";     // the middle part of the malicious String tries to bypass the filter by using unknown characters to Unicode to complete a "<script>"
        String s = filterString(maliciousInput);
        System.out.println(s);

        System.out.println(filterString("<script>"));       // this will throw an exception
    }
}
