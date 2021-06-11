module HttpModule {

    opens clientmodule to com.fasterxml.jackson.databind;
    requires core;
    requires java.net.http;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;

}