package queue.gov.rameses.com.queuecodegenerator.bean;

/**
 * Created by Dino Quimson on 2/16/15.
 */
public class Color {
    private String primary, secondary, tertiary;

    public Color(String primary, String secondary, String tertiary){
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }

    public String getPrimary(){
        return this.primary;
    }

    public String getSecondary(){
        return this.secondary;
    }

    public String getTertiary(){
        return this.tertiary;
    }
}
