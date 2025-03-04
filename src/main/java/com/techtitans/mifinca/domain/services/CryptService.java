package com.techtitans.mifinca.domain.services;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class CryptService {

    private static String key;
    public static void setKey(String key){
        CryptService.key = key;
    }

    public String encryptAES(String text){
        try{
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedData = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        }catch(Exception ex){
            System.out.println(ex.toString());
            //bad practice, but, is not probably that the crypt of the key fail
            return "";
        }
    }

    public String decrypt(String text){
        try{
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decodedData = Base64.getDecoder().decode(text);
            byte[] decryptedData = cipher.doFinal(decodedData);        
            return new String(decryptedData);
        }catch(Exception ex){
            return "";
        }
    }
}
