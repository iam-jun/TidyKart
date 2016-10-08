package fpoly.pro205.fit.items;

/**
 * Created by mthu1 on 9/24/2016.
 */

public class itemScheduled {
    int slot;
    boolean sun;
    boolean mon;
    boolean tue;
    boolean wed;
    boolean thu;
    boolean fri;
    boolean sat;

    public itemScheduled() {
    }

    public itemScheduled(int slot, boolean sun, boolean mon, boolean tue, boolean thu, boolean wed, boolean fri, boolean sat) {
        this.slot = slot;
        this.sun = sun;
        this.mon = mon;
        this.tue = tue;
        this.thu = thu;
        this.wed = wed;
        this.fri = fri;
        this.sat = sat;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean isSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }

    public boolean isMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean isTue() {
        return tue;
    }

    public void setTue(boolean tue) {
        this.tue = tue;
    }

    public boolean isWed() {
        return wed;
    }

    public void setWed(boolean wed) {
        this.wed = wed;
    }

    public boolean isThu() {
        return thu;
    }

    public void setThu(boolean thu) {
        this.thu = thu;
    }

    public boolean isFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }
}
