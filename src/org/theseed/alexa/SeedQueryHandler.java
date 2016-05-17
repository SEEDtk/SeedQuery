package org.theseed.alexa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * This class is the handler for an AWS Lambda function powering an Alexa Skills
 * Kit experience. To set it up, simply set the handler field in the AWS Lambda
 * console to "org.theseed.alexa.org.theseed.alexaHandler". Upload the resulting zip file to
 * power your function.
 */
public final class SeedQueryHandler extends SpeechletRequestStreamHandler implements RequestHandler<String, String> {
    private static final Set<String> supportedApplicationIds;

    static {
        /*
         * our special application ID
         */
        supportedApplicationIds = new HashSet<String>();
        supportedApplicationIds.add("amzn1.echo-sdk-ams.app.d32becb3-600b-49d2-96ed-8cc2e7734ca8");
    }

    public SeedQueryHandler() {
        super(new SeedQuerySpeechlet(), supportedApplicationIds);
    }

    @Override
    /** Request handler: json in, json out */
    public String handleRequest(String input, Context context) {
        String retVal;
        InputStream inStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outStream = new ByteArrayOutputStream();
        try {
            this.handleRequest(inStream, outStream, context);
            retVal = new String(outStream.toString());
        } catch (IOException e) {
            retVal = null;
        }
        return retVal;
    }

}

