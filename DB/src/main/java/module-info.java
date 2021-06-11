module DB {
    opens dbpackage to com.google.gson, eclipselink;
    requires jakarta.persistence;
    requires eclipselink;
    requires java.sql;
    exports dbpackage;
}