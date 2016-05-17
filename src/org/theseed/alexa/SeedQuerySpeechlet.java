package org.theseed.alexa;

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

/**
 * This class intercepts intents and decides what to do with them.
 */

public class SeedQuerySpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(SeedQuerySpeechlet.class);

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

        // TODO compute the launch response.
        return null;
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        Intent intent = request.getIntent();
        log.info("onIntent requestId={}, sessionId={}, intent={}", request.getRequestId(), session.getSessionId(),
                intent.getName());

        if ("CountIntent".equals(intent.getName())) {
            // TODO count the object
            return null;
        } else if ("GenomeIntent".equals(intent.getName())) {
            // TODO get the genome
        } else if ("AMAZON.HelpIntent".equals(intent.getName())) {
            // TODO give the user a hint
            return null;

        } else if ("AMAZON.CancelIntent".equals(intent.getName())) {
            return getExitIntentResponse(intent, session);

        } else if ("AMAZON.StopIntent".equals(intent.getName())) {
            return getExitIntentResponse(intent, session);

        } else {
            throw new IllegalArgumentException("Unrecognized intent: " + intent.getName());
        }
        return null;
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

    private SpeechletResponse getExitIntentResponse(Intent intent, Session session) {
        // TODO say goodbye
        return null;
    }

}
