package backend.academy.samples.commonscodec;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.language.Soundex;
import org.gaul.modernizer_maven_annotations.SuppressModernizer;

/**
 * Apache Commons Codec provides utilities for encoding/decoding data
 * <p>
 * <a href="https://commons.apache.org/proper/commons-codec/">Library website</a>
 */
@Log4j2
@SuppressModernizer
public class CommonsCodecExample {
    public static void main(String[] args) {
        String someString = "Just common string, nothing special";
        log.info("Original string: '{}'", someString);

        // Base64 is commonly used to encode binary data into text format
        // though java.util.Base64 is preferred
        byte[] base64Encoded = Base64.encodeBase64(someString.getBytes());
        log.info("Base64 encoded string: '{}'", new String(base64Encoded));
        byte[] base64Decoded = Base64.decodeBase64(base64Encoded);
        log.info("Base64 decoded string: '{}'", new String(base64Decoded));

        // hexadecimal string representation
        char[] hexEncoded = Hex.encodeHex(someString.getBytes());
        log.info("Hex encoded string: '{}'", new String(hexEncoded));

        // hash functions used to map data of arbitrary size to fixed-size values
        log.info("Hex encoded MD5 hash: '{}'", DigestUtils.md5Hex(someString));
        log.info("Hex encoded SHA-256 hash: '{}'", DigestUtils.sha256Hex(someString));

        // Soundex encodes words into a representation that approximates how they sound
        var soundex = new Soundex();
        String input1 = "Smith";
        String input2 = "Smyth";
        String input3 = "Smile";
        log.info("Code for '{}' is: '{}'", input1, soundex.soundex(input1));
        log.info("Code for '{}' is: '{}'", input2, soundex.soundex(input2));
        log.info("Code for '{}' is: '{}'", input3, soundex.soundex(input3));
    }
}
