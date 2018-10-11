package org.rodriguealcazar.yelp.dataset.udf;

import org.apache.spark.sql.api.java.UDF1;

public class ToMinutes implements UDF1<String, Double> {

    @Override
    public Double call(String time) {
        String[] hoursMinutes = time.split(":");
        return Double.valueOf(hoursMinutes[0]) * 60 + Double.valueOf(hoursMinutes[1]);
    }
}
