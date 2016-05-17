package org.theseed.alexa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;

/**
 * This class intercepts intents and decides what to do with them.
 */

public class SeedQuerySpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(SeedQuerySpeechlet.class);

    private static final String SEED_URL = "http://bioseed.mcs.anl.gov/~parrello/SEEDtk/svr.cgi";
    
    /** Default constructor. */
    public SeedQuerySpeechlet() {
        initializeComponents();
    }

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

        // TODO initialize session
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        return getBasicSpeechletResponse("Welcome to SEED.");
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        Intent intent = request.getIntent();
        log.info("onIntent requestId={}, sessionId={}, intent={}", request.getRequestId(), session.getSessionId(),
                intent.getName());

        if ("CountIntent".equals(intent.getName())) {
            String parameter = intent.getSlot("ObjectType").getValue();
            return getSeedData("CountIntent", parameter);
        } else if ("GenomeIntent".equals(intent.getName())) {
            String parameter = intent.getSlot("TaxonId").getValue();
            String version = intent.getSlot("Version").getValue();
            parameter += "." + version;
            return getSeedData("GenomeIntent", parameter);
        } else if ("AMAZON.HelpIntent".equals(intent.getName())) {
            String helpText = "To get genome data, use the genome ID, for example 'ask SEED about 83333.1'. To get counts, use the table name, for example 'ask SEED how many genomes'.";
            return getBasicSpeechletResponse(helpText);

        } else if ("AMAZON.CancelIntent".equals(intent.getName())) {
            return getExitIntentResponse(intent, session);

        } else if ("AMAZON.StopIntent".equals(intent.getName())) {
            return getExitIntentResponse(intent, session);

        } else {
            throw new IllegalArgumentException("Unrecognized intent: " + intent.getName());
        }

    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        // TODO any cleanup logic goes here
    }

    /**
     * Initializes the instance components.
     */
    private void initializeComponents() {
        // TODO initialization common to all constructors
    }

    /**
     * Returns a tell Speechlet response for a speech with a card.
     *
     * @param speechText
     *            Text for speech output
     * @return a tell Speechlet response for a speech with a card.
     */
    private SpeechletResponse getTellSpeechletResponse(String speechText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Feed Tracker");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    /**
     * Returns a tell Speechlet response for a speech with no card.
     *
     * @param speechText
     *            Text for speech output
     * @return a tell Speechlet response for a speech and reprompt text
     */
    private SpeechletResponse getBasicSpeechletResponse(String speechText) {

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech);
    }


    /** Get data from the SEED */
    private SpeechletResponse getSeedData(String action, String parameter) {
        String retVal;
        try {
            // Format the parameters.
            StringBuilder postData = new StringBuilder();
            postData.append(URLEncoder.encode("action", "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(action, "UTF-8"));
            postData.append(";");
            postData.append(URLEncoder.encode("parameter", "UTF-8"));
            postData.append("=");
            postData.append(URLEncoder.encode(parameter, "UTF-8"));
            // Connect to the URL.
            URL seedUrl = new URL(SEED_URL + "?" + postData.toString());
            HttpURLConnection connection = (HttpURLConnection) seedUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                retVal = "Fatal internet error " + Integer.toString(responseCode);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                retVal = in.readLine();
                if (retVal == null) {
                    retVal = "No further information.";
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            retVal = "A fatal error of type " + e.getClass() + " occurred.";
        }
        
        return getTellSpeechletResponse(retVal);
    }
    
    private SpeechletResponse getExitIntentResponse(Intent intent, Session session) {
        return getBasicSpeechletResponse("Goodbye");
    }

}
