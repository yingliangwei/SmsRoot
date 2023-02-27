module com.example.smsroot2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.alibaba.fastjson2;
    requires io.netty.all;

    opens com.example.smsroot2 to javafx.fxml;
    opens com.example.smsroot2.entity to javafx.base;
    exports com.example.smsroot2;
}