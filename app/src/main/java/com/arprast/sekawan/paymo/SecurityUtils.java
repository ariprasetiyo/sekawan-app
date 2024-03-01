package com.arprast.sekawan.paymo;

import static com.arprast.sekawan.util.ConstKt.EMPTY_STRING;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils {

    static final String characterEncoding = "UTF-8";
    static final String cipherTransformation = "AES/CBC/PKCS5PADDING";
    static final String aesEncryptionAlgorithem = "AES";
    static final String charsetName = "UTF8";
    static final Logger logger = Logger.getLogger(SecurityUtils.class.getName());
    private static final String HMAC_SHA256 = "HmacSHA256";

    public static String hmacSHA256(final String salt, final String bodyMessage) {
        try {
            final Mac hmac256SHAInstance = Mac.getInstance(HMAC_SHA256);
            final SecretKeySpec secret_key = new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            hmac256SHAInstance.init(secret_key);
            return Hex.encodeHexString(hmac256SHAInstance.doFinal(bodyMessage.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            logger.log(Level.ALL, "Error generate hmacSHA256=" + ex.getMessage(), ex);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptedAES128(String encryptionKey, String plainText) {
        String encryptedText = EMPTY_STRING;

        try {
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = encryptionKey.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(1, secretKey, ivparameterspec);
            byte[] cipherText = cipher.doFinal(plainText.getBytes(characterEncoding));
            Base64.Encoder encoder = Base64.getUrlEncoder();
            encryptedText = encoder.encodeToString(cipherText);
        } catch (Exception err) {
            logger.log(Level.ALL, "Encrypt Exception : {}" + err.getMessage(), err);
        }

        return encryptedText;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decryptAES128(final String encryptionKey, String encryptedText) {
        String decryptedText = EMPTY_STRING;
        try {
            final Cipher cipher = Cipher.getInstance(cipherTransformation);
            final byte[] key = encryptionKey.getBytes(characterEncoding);
            final SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            final IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
            final Base64.Decoder decoder = Base64.getUrlDecoder();
            final byte[] cipherText = decoder.decode(encryptedText.getBytes(charsetName));
            decryptedText = new String(cipher.doFinal(cipherText), characterEncoding);

        } catch (Exception err) {
            logger.log(Level.ALL, "decrypt Exception : {}" + err.getMessage(), err);
        }
        return decryptedText;
    }

    public static String encryptionKeyGenerator(String encryptionKey, Long requestTime) {
        final String encryptionKeyFinal = encryptionKey + requestTime;
        int encryptionKeyLength = encryptionKeyFinal.length();
        if (encryptionKeyLength > 16) {
            return encryptionKeyFinal.substring(encryptionKeyLength - 16, encryptionKeyLength) ;
        }
        return encryptionKeyFinal;
    }
}
