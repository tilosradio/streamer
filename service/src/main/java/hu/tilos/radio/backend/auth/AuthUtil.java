package hu.tilos.radio.backend.auth;

import hu.tilos.radio.backend.data.UserInfo;
import hu.tilos.radio.backend.data.types.Contribution;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class AuthUtil {

    public static String toSHA1(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return byteArrayToHexString(md.digest(data.getBytes()));
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static void calculatePermissions(UserInfo userInfo) {
        if (userInfo.getAuthor() != null) {
            userInfo.getPermissions().add("/author/" + userInfo.getAuthor().getId());
            userInfo.getPermissions().add("/author/" + userInfo.getAuthor().getAlias());
            if (userInfo.getAuthor().getContributions() != null) {
                for (Contribution contribution : userInfo.getAuthor().getContributions()) {
                    userInfo.getPermissions().add("/show/" + contribution.getShow().getId());
                    userInfo.getPermissions().add("/show/" + contribution.getShow().getAlias());

                }
            }
        }

    }

    public String generateSalt() {
        Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        return toSHA1(new String(salt));
    }

    public String encode(String password, String salt) {
        return toSHA1(password + salt);
    }

}
