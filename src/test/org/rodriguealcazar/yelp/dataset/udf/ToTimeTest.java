package org.rodriguealcazar.yelp.dataset.udf;


import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ToTimeTest {

    @Test
    public void shouldHaveZeroMinutesForMultiples() {
        String time = new ToTime().call(540.);
        assertThat(time, is("09:00"));
    }

    @Test
    public void shouldHaveMinutesForNonMultiples() {
        String time = new ToTime().call(565.);
        assertThat(time, is("09:25"));
    }


}