package hu.radio.tilos.model.tools;

import org.flywaydb.core.Flyway;

public class Upgrader {

    public static void main(String[] args) {
        new Upgrader().upgrade();
    }

    private void upgrade() {
        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:mysql://localhost:3306/tilos2", "root", "");
        flyway.repair();
        flyway.migrate();
        //flyway.init();
    }
}
