/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.firmavalidator.bean;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    public static byte[] decode(String value) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String encode(byte[] bytes) {
        Base64.Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(bytes), StandardCharsets.UTF_8);
    }
}
