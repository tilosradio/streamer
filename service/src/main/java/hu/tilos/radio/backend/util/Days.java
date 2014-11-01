package hu.tilos.radio.backend.util;

/**
 * Hungarian week days.
 * <p/>
 * Just to avoid dependency on OS dependent locale packages.
 */
public enum Days {

    SUNDAY("Vasárnap"), MONDAY("Hétfő"), TUESDAY("Kedd"), WEDNESDAY("Szerda"), THURSDAY("Csütörtök"), FRIDAY("Péntek"), SATURDAY("SZOMBAT");


    private String hungarian;

    private Days(String day) {
        this.hungarian = day;
    }

    public String getHungarian() {
        return hungarian;
    }
}
