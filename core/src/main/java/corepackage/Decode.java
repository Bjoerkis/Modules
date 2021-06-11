package corepackage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

class Decoder {

    public static String decode(String url) {

        try {

            String previousUrl = "";
            String decodedUrl = url;

            while(!previousUrl.equals(decodedUrl)) {

                previousUrl = decodedUrl;
                decodedUrl = URLDecoder.decode(decodedUrl, "UTF-8");
            }
            return decodedUrl;

        }
        catch(UnsupportedEncodingException e) {

            return "result: " + e.getMessage();
        }

    }
}


