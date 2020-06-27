package toolsClases;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import javax.xml.bind.DatatypeConverter; //DEPRECATED

/**
 *
 * @author Shkirmantsev
 */
public class MyMD5Hash {

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    
    public static String getMD5HsFromString(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();

        md.update(str.getBytes("utf-8"));
        byte[] digest = md.digest();

//        String myHash = new String(digest, "UTF-8");
        String myHash =bytesToHex(digest) ;

        return myHash;
    }

       
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
