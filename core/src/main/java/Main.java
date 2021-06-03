public class Main {

    public static void main(String[] args) {

        var utils = new Utils();

        System.out.println(utils.message());
    }


    private static Utils giveMeAnObject(){
        return new Utils();
    }
}
