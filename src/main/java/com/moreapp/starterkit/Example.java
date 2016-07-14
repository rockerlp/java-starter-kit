package com.moreapp.starterkit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Example {

    public static void main(String[] args) throws IOException {
        //Load the properties
        Properties properties = new Properties();
        properties.load(Example.class.getResourceAsStream("/com/moreapp/starterkit/api.properties"));
        String endpoint = properties.getProperty("endpoint");

        //Prepare the client
        OAuthRestTemplate oAuthRestTemplate = prepareClient(properties);

        //Make the API Call (get all customers)
        ResponseEntity<Map[]> response = oAuthRestTemplate.getForEntity(endpoint + "/customers", Map[].class);
        System.out.println("The response status is: " + response.getStatusCode());
        if (response.getStatusCode().value() != 200) {
            System.out.printf("Something went wrong please check the credentials and the API endpoint");
            return;
        }

        //Print the result
        System.out.println("The customers are:");
        Map[] customers = response.getBody();
        for (Map customer : customers) {
            System.out.println(" - " + customer.get("name"));
        }

        // For more api calls and the response types ee http://developer.moreapp.com/#/apidoc.
    }

    private static OAuthRestTemplate prepareClient(Properties properties) {
        //Setup the credentials
        BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
        resource.setConsumerKey(properties.getProperty("consumerKey"));
        String hash = hash(properties.getProperty("salt"), properties.getProperty("password").toCharArray());
        resource.setSharedSecret(new SharedConsumerSecretImpl(hash));
        resource.setAuthorizationHeaderRealm("more");


        OAuthRestTemplate oAuthRestTemplate = new OAuthRestTemplate(resource);

        //Add the Jackson message converter to parse the JSON response into Java objects.
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(converter);
        oAuthRestTemplate.setMessageConverters(messageConverters);

        return oAuthRestTemplate;
    }

    //Method to salt the password and hash the password using a SHA-1
    public static String hash(String hashSalt, char[] plainText) {
        char[] prefix = hashSalt.substring(0, (int) Math.ceil(hashSalt.length() / 2.0)).toCharArray();
        char[] suffix = hashSalt.substring((int) Math.ceil(hashSalt.length() / 2.0)).toCharArray();

        char[] salted = new char[prefix.length + plainText.length + suffix.length];
        byte[] saltedBytes = new byte[salted.length];
        System.arraycopy(prefix, 0, salted, 0, prefix.length);
        System.arraycopy(plainText, 0, salted, prefix.length, plainText.length);
        System.arraycopy(suffix, 0, salted, prefix.length + plainText.length, suffix.length);

        for (int i = 0; i < salted.length; i++) {
            saltedBytes[i] = (byte) salted[i];
        }
        final char[] hexChars = "0123456789abcdef".toCharArray();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        byte[] buf = md.digest(saltedBytes);

        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = hexChars[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = hexChars[buf[i] & 0x0F];
        }

        Arrays.fill(salted, (char) 0);
        Arrays.fill(plainText, (char) 0);
        Arrays.fill(saltedBytes, (byte) 0);
        return new String(chars);
    }

}
