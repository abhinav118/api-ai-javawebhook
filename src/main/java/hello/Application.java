//package hello;
//
//import java.io.IOException;
//import java.security.InvalidKeyException;
//import java.security.Key;
//import java.security.NoSuchAlgorithmException;
//import java.security.SignatureException;
//import java.util.Base64;
//import java.util.Map;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.impl.crypto.MacProvider;
//
//
//public class Application {
//    public static void main (String [] args) {
//    	Key key = MacProvider.generateKey();
//
//    	String s = Jwts.builder().setSubject("1234567890").signWith(SignatureAlgorithm.HS256, key).compact();
//    	
//    	System.out.println(s);
//    }
//}