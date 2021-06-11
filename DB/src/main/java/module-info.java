module DB {
    opens dbpackage to com.google.gson;
    requires jakarta.persistence;
    requires java.sql;
    exports dbpackage;
}