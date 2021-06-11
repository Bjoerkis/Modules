module DB {
    requires jakarta.persistence;
    requires java.sql;
    opens domain to com.google.gson;
    exports db;


}