package org.rodriguealcazar.yelp;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.rodriguealcazar.yelp.dataset.BusinessStats;
import org.rodriguealcazar.yelp.dataset.udf.ToMinutes;
import org.rodriguealcazar.yelp.dataset.udf.ToTime;

public class DatasetChallenge {

    private final String businessDatasetPath;

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("Yelp Dataset Challenge")
                .getOrCreate();

        try {
            new DatasetChallenge("file:///tmp/yelp_academic_dataset_business.json.gz").run(spark);
        } finally {
            spark.stop();
        }

    }

    public DatasetChallenge(String businessDatasetPath) {
        this.businessDatasetPath = businessDatasetPath;
    }

    public void run(SparkSession spark) {
        registerUdfs(spark);

        Dataset<Row> openingHoursPercentiles = new BusinessStats(spark, businessDatasetPath)
                .loadDataset()
                .openingHoursPercentiles();

        // We are going to split this dataframe in two to produce the opening and closing time results so we persist to
        // avoid having to redo the computation.
        openingHoursPercentiles.persist();

        // To only produce a single file here we first coalesce the Spark partition back into a single one.
        // We would not do this with a real-life size dataset as a single executor would probably not be able to handle
        // the whole lot and we would lose write parallelism making the job slower.
        openingOnlyPercentiles(openingHoursPercentiles)
                .coalesce(1)
                .write()
                .mode(SaveMode.Overwrite)
                .csv("file:///tmp/yelp-spark/business_opening_hours_percentiles");

        closingOnlyPercentiles(openingHoursPercentiles)
                .coalesce(1)
                .write()
                .mode(SaveMode.Overwrite)
                .csv("file:///tmp/yelp-spark/business_closing_hours_percentiles");
    }

    private Dataset<Row> openingOnlyPercentiles(Dataset<Row> openingHoursPercentiles) {
        return openingHoursPercentiles.select("state", "city", "neighborhood", "opening_median", "opening_95th");
    }

    private Dataset<Row> closingOnlyPercentiles(Dataset<Row> openingHoursPercentiles) {
        return openingHoursPercentiles.select("state", "city", "neighborhood", "closing_median", "closing_95th");
    }

    private void registerUdfs(SparkSession spark) {
        spark.udf().register("to_time", new ToTime(), DataTypes.StringType);
        spark.udf().register("to_minutes", new ToMinutes(), DataTypes.DoubleType);
    }

}
