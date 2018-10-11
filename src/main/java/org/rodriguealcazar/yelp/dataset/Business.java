package org.rodriguealcazar.yelp.dataset;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class Business {

    // With Spark, it is important to specify the schema of the data, especially when dealing with JSON (when no schema
    // is given the framework reads the data twice in order to infer the schema). Here we only specify the columns that
    // we will need to answer the questions.
    // While with JSON Spark will still have to read the full objects, it can discard all the fields that are not part
    // of the schema thus saving a lot of memory. This would be even more beneficial with a columnar storage format,
    // e.g. parquet, because the reader could read from disk only the columns it needs thus saving a lot of I/O.
    public static final StructType SCHEMA = new StructType()
            .add("state", DataTypes.StringType, true)
            .add("city", DataTypes.StringType, true)
            .add("neighborhood", DataTypes.StringType, true)
            .add("hours", DataTypes.createMapType(DataTypes.StringType, DataTypes.StringType), true);
}
