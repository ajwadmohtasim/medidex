module dex.medidex {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.desktop;

    opens dex.medidex to javafx.fxml;
    opens Medicine to javafx.base;
    exports dex.medidex;
}