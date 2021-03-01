package com.example.lab4;

import javax.ejb.Singleton;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Singleton
public class UserBean {

    private static final Map<String, String> loggedInSecretKeys = new HashMap<>();

    private List<User> getRegistered() {
        return Database.userEM.createQuery("select c from users4_table c").getResultList();
    }

    public String register(String username, String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String encodedPass = Base64.getEncoder().encodeToString(hash);

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encodedPass);

        String secKey = new BigInteger(130, new SecureRandom()).toString(32);

        loggedInSecretKeys.put(secKey, username);

        Database.userEM.getTransaction().begin();
        Database.userEM.persist(user);
        Database.userEM.flush();
        Database.userEM.getTransaction().commit();
        return secKey;
    }

    public String isValidUser(String secKey) {
        return loggedInSecretKeys.getOrDefault(secKey, null);
    }

    public String login(String username, String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String encodedPass = Base64.getEncoder().encodeToString(hash);

        List<User> users = getRegistered();
        for (User user : users) {
            if (username.equals(user.getUsername()) &&
                encodedPass.equals(user.getPasswordHash())) {
                String secKey = new BigInteger(130, new SecureRandom()).toString(32);
                loggedInSecretKeys.put(secKey, username);
                return secKey;
            }
        }
        throw new Exception("Invalid username or pass");
    }

    public Boolean isRegistered(String username) {
        List<User> users = getRegistered();
        for (User user : users) {
            if (username.equals(user.getUsername()))
                return true;
        }
        return false;
    }

    public Boolean logout(String secretKey) {
        return loggedInSecretKeys.remove(secretKey) != null;
    }
}
