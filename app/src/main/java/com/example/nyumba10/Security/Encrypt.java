package com.example.nyumba10.Security;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {


    private static final String AES ="AES" ;

    public   String encrypt(String password) throws Exception {
        SecretKeySpec key=generateKey(password);
        Cipher c=Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);


        byte [] encvalue= new byte[0];
        try {
            encvalue = c.doFinal(password.getBytes());
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(encvalue,Base64.DEFAULT);
    }

    public  String decrypt(String encypted_value,String password) throws  Exception
    {
        SecretKeySpec key=generateKey(password);
        Cipher c=Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);

        byte [] decode_value=Base64.decode(encypted_value,Base64.DEFAULT);
        byte [] dec_val=c.doFinal(decode_value);

        return new String(dec_val);


    }


    public SecretKeySpec generateKey(String password) throws Exception {

            final MessageDigest digest=MessageDigest.getInstance("SHA-256");

        byte[] bytes=password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key=digest.digest();
        SecretKeySpec secretkeyspec=new SecretKeySpec(key,"AES");

        return  secretkeyspec;

    }




}
