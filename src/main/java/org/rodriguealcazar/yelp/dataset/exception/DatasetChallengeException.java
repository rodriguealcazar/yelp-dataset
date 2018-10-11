package org.rodriguealcazar.yelp.dataset.exception;

import java.io.IOException;

public class DatasetChallengeException extends RuntimeException {

    public DatasetChallengeException(String message, IOException cause) {
        super(message, cause);
    }
}
