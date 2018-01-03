package uk.me.asbridge.calendarcountdown;

/**
 * Created by AsbridgeD on 03-Jan-18.
 */

public class Calendar {
    private String name;
    private long Id; /* 0 > checkbox disable, 1 > checkbox enable */

    Calendar(String name, long Id){
        this.name = name;
        this.Id = Id;
    }
    public String getName(){
        return this.name;
    }
    public long getId(){
        return this.Id;
    }

}
