package com.ga4w20.bookazon.beans;

import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.slf4j.LoggerFactory;

/**
 * Bean used in the manager.xhtml to store data for dates (some data is shown between specified dates)
 * @author Grant
 */
@Named("dateRange")
@SessionScoped
public class DateRangeBackingBean implements Serializable{
    private Date startRange;
    private Date endRange;
    
    public Date getStartRange() {
        return startRange;
    }

    public void setStartRange(Date startRange) {
        this.startRange = startRange;
    }

    public Date getEndRange() {
        return endRange;
    }

    public void setEndRange(Date endRange) {
        this.endRange = endRange;
    }

}
