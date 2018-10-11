package org.rodriguealcazar.yelp;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatasetChallengeIT {

    private SparkSession spark;

    @Before
    public void setup() {
        SparkConf conf = new SparkConf().setMaster("local[2]");
        spark = SparkSession.builder()
                .appName("Yelp Dataset Challenge - Test")
                .config(conf)
                .getOrCreate();
    }

    @After
    public void tearDown() {
        spark.stop();
    }

    @Test
    public void shouldRunJob() {
        // In a real life Integration Test, we could:
        //   * setup some test dataset
        //   * copy it to a temp folder
        //   * pass the path to the DatasetChallenge constructor
        //   * run the job
        //   * read the output and compare the result with the expected result
        new DatasetChallenge("file:///tmp/yelp_academic_dataset_business.json").run(spark);
    }
}