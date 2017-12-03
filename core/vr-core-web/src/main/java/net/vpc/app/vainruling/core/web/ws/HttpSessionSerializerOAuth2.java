package net.vpc.app.vainruling.core.web.ws;

import com.google.gson.Gson;
import net.vpc.common.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HttpSessionSerializerOAuth2 implements HttpSessionSerializer {
//    private String key = "Bar12345Bar12345"; // 128 bit key
    private byte[] key = "ABABABABABABABAB".getBytes(); // 128 bit key
    private boolean compress=true;
    public static void main(String[] args) {
        HttpSessionSerializerOAuth2 a=new HttpSessionSerializerOAuth2();
        for (boolean b:new boolean[]{false,true}) {
            a.compress=b;
            a.init(null);

            HashMap<String, Object> h = new HashMap<>();
            h.put("one", "un");
            for (int i = 0; i < 100000; i++) {
                h.put(String.valueOf(i), String.valueOf(i + 1));
            }
            HttpSessionIdOAuth2 e = new HttpSessionIdOAuth2("rrr", h);

            String encrypted = a.encrypt(e);
            System.out.println("Encoded bytes are " + encrypted.length());
            HttpSessionIdOAuth2 e2 = a.decrypt(encrypted);
        }
    }

    public String getType(){
        return "OAuth2";
    }

    @Override
    public void init(Map<String,String> config) {

        KeyGenerator gen = null;
        try {
            gen = KeyGenerator.getInstance("AES");
            gen.init(128); /* 128-bit AES */
            SecretKey secret = gen.generateKey();
            byte[] binary = secret.getEncoded();
            key = binary;
//            key = String.format("%032X", new BigInteger(+1, binary));
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public HttpSessionId read(HttpServletRequest request) {
        // OAuth2, ex : Authorization: Bearer mF_9.B5f-4.1JqM
        String s = request.getHeader("Authorization");
        if (s == null) {
            s = request.getHeader("X-Authorization");
        }
        if (s != null) {
            s = s.trim();
            if (s.toLowerCase().startsWith("bearer ")) {
                String r = s.substring("bearer".length()).trim();
                return decrypt(r);
            }
        }
        return null;
    }

    // jsonZip->->AES->B64
    private String encrypt(HttpSessionIdOAuth2 token) {
        byte[] bytes = new Gson().toJson(token).getBytes();
//        System.out.println(key.getBytes().length);
        // Create key and cipher
        Key aesKey = new SecretKeySpec(key, "AES");
        // encrypt the text
        try {
            if(compress) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(out);
                gzip.write(bytes);
                gzip.close();
                bytes = out.toByteArray();
            }
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            bytes = cipher.doFinal(bytes);

            return (new String(Base64.getEncoder().encode(bytes)));
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // B64->AES->Zip->json
    private HttpSessionIdOAuth2 decrypt(String token) {
        //decypher token
        String decrypted = null;
        byte[] bytes = Base64.getDecoder().decode(token);
        try {

            Key aesKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            bytes=cipher.doFinal(bytes);

            if(compress) {
                GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes));
                bytes = IOUtils.toByteArray(gzip);
            }

        } catch (GeneralSecurityException | IOException e ) {
            throw new IllegalArgumentException(e);
        }
        return new Gson().fromJson(new String(bytes), HttpSessionIdOAuth2.class);
    }

    @Override
    public void write(HttpServletResponse response, HttpSessionId id) {
        response.setHeader("X-Authorization", encrypt((HttpSessionIdOAuth2) id));
    }

    @Override
    public void destroy() {

    }
}
