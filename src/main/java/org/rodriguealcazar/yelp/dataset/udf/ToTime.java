package org.rodriguealcazar.yelp.dataset.udf;

import org.apache.spark.sql.api.java.UDF1;

public class ToTime implements UDF1<Double, String> {

    @Override
    public String call(Double minutes) {
        return String.format("%02d:%02d", minutes.longValue() / 60, minutes.longValue() % 60);
    }
}
