package net.vpc.app.vainruling.core.web.rest.api;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/*
*
* TODO implements com.auth0.jwt.interfaces.RSAKeyProvider and use jwks-rsa-java library for kid
* TODO implements a key distribution scheme or map kids to servers and store keys on db for load balancing
* */
public class RSAKeyProvider {
    private static RSAKeyProvider ourInstance;

    static {
        try {
            ourInstance = new RSAKeyProvider();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "SUN");

    private KeyPair keyPair;

    private RSAKeyProvider() throws NoSuchProviderException, NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        keyGen.initialize(2048, random);
        keyPair = keyGen.generateKeyPair();
    }

    public static RSAKeyProvider getInstance() {
        return ourInstance;
    }

    public RSAPublicKey getPublicKey(){
        return (RSAPublicKey)keyPair.getPublic();
    }

    public RSAPrivateKey getPrivateKey(){
        return (RSAPrivateKey)keyPair.getPrivate();
    }
}
