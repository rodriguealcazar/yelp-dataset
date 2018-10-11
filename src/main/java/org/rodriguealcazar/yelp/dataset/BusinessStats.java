package org.rodriguealcazar.yelp.dataset;

import org.apache.commons.io.IOUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.rodriguealcazar.yelp.dataset.exception.DatasetChallengeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BusinessStats {

    Logger logger = LoggerFactory.getLogger(BusinessStats.class);

    private static final String BUSINESSES = "businesses";

    private static final String ROOT_SQL_FOLDER = "/queries/";
    private static final String WEEK_OPENING_HOURS_SQL = ROOT_SQL_FOLDER + "extract-week-opening-hours.sql";
    private static final String EXPLODE_OPENING_HOURS_SQL = ROOT_SQL_FOLDER + "explode-opening-hours.sql";
    private static final String SPLIT_OPENING_CLOSING_SQL = ROOT_SQL_FOLDER + "split-opening-closing.sql";
    private static final String CALCULATE_PERCENTILES_SQL = ROOT_SQL_FOLDER + "calculate-percentiles-grouped.sql";

    private SparkSession spark;
    private String datasetPath;

    private Dataset<Row> businesses;

    public BusinessStats(SparkSession spark, String datasetPath) {
        this.spark = spark;
        this.datasetPath = datasetPath;
    }

    public BusinessStats loadDataset() {
        logger.info(String.format("Loading business dataset from %s.", datasetPath));
        businesses = spark.read()
                .format("json")
                .schema(Business.SCHEMA)
                .load(datasetPath);
        businesses.createOrReplaceTempView(BUSINESSES);
        return this;
    }

    public Dataset<Row> openingHoursPercentiles() {
        logger.info("Extracting weekday opening hours.");
        Dataset<Row> operatingHours = spark.sql(readQuery(WEEK_OPENING_HOURS_SQL));
        operatingHours.createOrReplaceTempView("opening_hours");

        logger.info("Exploding opening hours for all businesses.");
        Dataset<Row> explodedOpeningHours = spark.sql(readQuery(EXPLODE_OPENING_HOURS_SQL));
        explodedOpeningHours.createOrReplaceTempView("exploded_opening_hours");

        logger.info("Splitting opening and closing hours.");
        Dataset<Row> splitOpeningHours = spark.sql(readQuery(SPLIT_OPENING_CLOSING_SQL));
        splitOpeningHours.createOrReplaceTempView("split_opening_hours");

        logger.info("Calculating percentiles grouped by state, city, neighbohood.");
        return spark.sql(readQuery(CALCULATE_PERCENTILES_SQL));
    }

    private String readQuery(String weekOpeningHoursSql) {
        try {
            return IOUtils.toString(getClass().getResourceAsStream(weekOpeningHoursSql));
        } catch (IOException e) {
            throw new DatasetChallengeException(String.format("Error loading SQL query %s", weekOpeningHoursSql), e);
        }
    }


}
