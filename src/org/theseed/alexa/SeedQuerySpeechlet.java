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
            log.info("count intent, object = {}", parameter);
            return getSeedData("CountIntent", parameter);
        } else if ("TextIntent".equals(intent.getName())) {
            String term = intent.getSlot("Term").getValue();
            log.info("term intent, type = {}", term);
            return getExplanation(term);
        } else if ("GenomeIntent".equals(intent.getName())) {
            String parameter = intent.getSlot("TaxonId").getValue();
            String version = intent.getSlot("Version").getValue();
            parameter += "." + version;
            return getSeedData("GenomeIntent", parameter);
        } else if ("AMAZON.HelpIntent".equals(intent.getName())) {
            String helpText = "To get genome data, use the genome ID, for example 'ask the SEED about 83333.1'. To get counts, use the table name, for example 'ask the SEED how many genomes'.";
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
        // any cleanup logic goes here
    }

    /**
     * Initializes the instance components.
     */
    private void initializeComponents() {
        // initialization common to all constructors
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
        card.setTitle("SEED Information");
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

    private SpeechletResponse getExplanation(String term) {
        String retVal = "";
        if (term == null) {
            retVal = "I am sorry, but I didn't understand your request.";
        } else {
            switch (term) {
            case "protein" :
            case "proteins" :
                retVal = "A protein is a large molecule made up mostly of carbon, hydrogen, oxygen, and nitrogen. " +
                         "Proteins are formed by putting smaller molecules called amino acids together in a long " +
                         "chain which folds itself into a complicated nest of curls and spirals. The amino acids " +
                         "in these curls and spirals trigger chemical reactions in a cell. Every single thing " +
                         "that happens inside a living cell is determined by the proteins formed from the cell's " +
                         "DNA.";
                break;
            case "amino acid" :
            case "acid" :
            case "amino acids" :
            case "acids" :
                retVal = "An amino acid is a small molecule that forms part of a protein. Every known protein is " +
                        "a long sequence of amino acids. There are 22 known amino acids, of which 20 are used by " +
                        "most life forms. Phenylalanine, a common dietary supplement, is an amino acid. " +
                        "Your body knows how to make 11 of the amino acids, but you have to get the other 9 from " +
                        "the food you eat and the liquids you drink.";
                break;
            case "bacteria" :
            case "bacterium" :
                retVal = "Bacteria are simple, single-celled creatures that live everywhere and in everything. " +
                         "Unlike the cells in large animals and plants, that contain many different parts, a " +
                         "bacterium is simply a bag of chemicals inside a thick membrane called the cell wall. " +
                         "Bacteria are the cause of many terrible diseases, such as tuberculosis, plague, and " +
                         "anthrax. But most bacteria are harmless. The bacteria in your stomach help you digest " +
                         "plant and animal tissue. The bacteria in a baby's milk protect the baby from disorders " +
                         "such as allergies and autism. In your body, there are 10 bacterial cells " +
                         "for every human cell. The human cells, however, are much larger, anywhere from 100 " +
                         "to 1000 times the volume of a typical bacterium.";
                break;
            case "archaea" :
                retVal = "Archaea are simple, single-celled creatures known for their ability to live in extreme " +
                         "environments like volcano springs, glaciers, and acid pools. Some of them even breathe " +
                         "methane instead of carbon dioxide or oxygen. The chemistry inside an archael cell is " +
                         "therefore very different from the chemistry inside a bacterium.  Not much is known about " +
                         "archaea because most of them cannot be cultured in a laboratory for study.";
                break;
            case "eukaryote" :
            case "eukaryotes" :
            case "eukaryota" :
            case "eukarya" :
                retVal = "Eukaryotes are complex organisms in which the cell is divided into tiny biological machines " +
                         "separated by membranes. These machines include the nucleus, the cytoskeleton, mitochondria, " +
                         "and chloroplasts. All multi-cellular life forms are eukaryotes, but some are single-celled, " +
                         "such as the plasmodium, which is the parasite that causes malaria. A eukaryote has multiple " +
                         "strings of DNA instead of a single circular chain like bacteria and archaea. This allows " +
                         "eukaryotes to evolve much faster, which is how they became so big and complicated. Life has " +
                         "existed on Earth for approximately 4 billion years, but eukaryotes have been around for only " +
                         "1800 million years. Yet in that time, they have come to dominate the surface of the planet.";
                break;
            case "DNA" :
            case "deoxyribonucleic acid" :
            case "nucleic acid" :
                retVal = "DNA, or deoxyribonucleic acid, is a gigantic molecule consisting of two intertwined helixes. " +
                         "Each helix is a chain of small molecules called nucleotides or bases. The nucleotides match up " +
                         "with each other, so if you lose one helix, you can rebuild it from the other one. DNA is used " +
                         "to create proteins from amino acids, and these proteins control all the chemical reactions in " +
                         "living cells.";
                break;
            case "RNA" :
            case "ribonucleic acid" :
                retVal = "RNA, or ribonucleic acid, is a molecule formed from DNA. There are several types of RNA. Messenger RNA " +
                         "contains the codons from DNA, and describes a protein. Transfer RNA collects the amino acids from the " +
                         "cell and connects them to the messenger RNA to create the protein. Ribosomal RNA turns into the ribosome " +
                         "chemicals than create the messenger RNA from the DNA. Each species has its own slightly different version of " +
                         "RNA, and RNA is used in modern bio-informatics to chart the course of evolution.";
                break;
            case "bio informatics" :
            case "bio infomatics" :
            case "bioinformatics" :
            case "bioinfomatics" :
                retVal = "Bio-informatics is the use of computers to study DNA sequences so that we can learn how life works, how to " +
                        "cure and prevent diseases and genetic disorders, how to help people live longer, and how the various parts of " +
                        "earth's ecology fit together. ";
                break;
            case "genome" :
            case "genomes" :
                retVal = "A genome is the DNA of a single organism stored in a computer. When we sequence DNA to create a genome, " +
                        "we don't always get everything, so the genome comes back in multiple pieces called contigs. " +
                        "The size of the genome is measured in millions of base pairs, or megabases.";
                break;
            case "contig" :
            case "contigs" :
                retVal = "A contig is a contiguous sequence of DNA for a genome. If we are missing a lot of the DNA for a genome, " +
                        "then there will be a lot of short contigs. So the fewer contigs a genome has, the more confident we are " +
                        "that we know everything we need to know about the genome.";
                break;
            case "feature" :
            case "features" :
                retVal = "A feature is a region of DNA in a genome that performs a function. Most features create proteins. We call " +
                        "those genes. Some features create RNA, some switch other features on and off. Some represent groups of " +
                        "other features that work together.";
                break;
            case "function" :
            case "functions" :
                retVal = "A function is a description of what a protein does. Most proteins only do one thing, but some do " +
                        "two or three. Each thing the protein does is called a role. Each role represents a chemical " +
                        "reaction that happens in the cell when the protein is present.";
                break;
            case "role" :
            case "roles" :
                retVal = "A role is a description of a group of chemical reactions caused by a protein's presence in the cell. " +
                        "If we know all the roles in the genome for a simple life form like a bacteria or archaea, we can create " +
                        "giant computer models of all the chemical reactions. These models enable us to simulate the life of the " +
                        "creature so we can guess what kills it, what helps it live, and what might make it decide to get " +
                        "angry and attack.";
                break;
            case "subsystem" :
            case "subsystems" :
            case "sub systems" :
            case "sub system" :
            case "subsistence" :
                retVal = "A subsystem is a group of roles that work together to perform a major process, such as building a " +
                        "cell wall, replicating the cell, converting sugar into energy, or protecting the cell from toxins." +
                        "In bio-informatics, susystems help us to figure out the functions of proteins.";
                break;
            default:
                retVal = "I am sorry. I don't understand the term " + term + ".";
            }
        }

        return getTellSpeechletResponse(retVal);
    }

    private SpeechletResponse getExitIntentResponse(Intent intent, Session session) {
        return getBasicSpeechletResponse("Goodbye");
    }

}
