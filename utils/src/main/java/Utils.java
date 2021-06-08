public class Utils {

    public static String parseUrl(String input) {
        //alternative 1
//        int firstSpace = input.indexOf(' ') + 1;
//        int secondSpace = input.indexOf(' ', firstSpace);
//
//        return input.substring(firstSpace, secondSpace);
        //alternative 2, too memory-consuming.

        String[] result = input.split(" ");
        return result[1];


    }

    public static HTTPType parseHttpRequestType(String input) {
        // return input.substring(0, input.indexOf(' '));
        if (input.startsWith("G"))
            return HTTPType.GET;
        else if (input.startsWith("H"))
            return HTTPType.HEAD;
        else if (input.startsWith("PO"))
            return HTTPType.POST;

        throw new RuntimeException("Invalid type");

    }

    public Request parseHttpHeader(String input) {

        var request = new Request();
        request.type = parseHttpRequestType(input);
        request.url = parseUrl(input);
        return request;
    }

    public static boolean handleRequest(Request request) {
        return switch (request.type) {
            case GET -> true;
            case HEAD -> false;
            case POST -> true;
        };
    }
//    public Map<String, String> parseHttpRequestHeader(String input) {
//        var map = new HashMap<String, String>();
//        map.put("Type", parseHttpRequestType(input));
//        map.put("Url", parseUrl(input));
//        return map;
//
//    }

    public String message() {
        return "Hello from Utils";
    }

}
