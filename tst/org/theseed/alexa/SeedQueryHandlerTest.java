package org.theseed.alexa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;


/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class SeedQueryHandlerTest {

    private static SeedQueryHandler handler;

    @BeforeClass
    public static void createInput() throws IOException {
        handler = new SeedQueryHandler();
    }

    private Context createContext() {
        TestContext ctx = new TestContext();
        ctx.setFunctionName("SeedQuery");

        return ctx;
    }

    @Test
    public void testSeedQueryHandler() {
        Context ctx = createContext();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("testAssets\\genometest.json"));
            String inString = new String(encoded);
            String outString = handler.handleRequest(inString, ctx);
            System.out.println(outString);
        } catch (IOException e) {
            System.err.println("IO exception: " + e.getMessage());
        }
    }

    @Test
    public void testSeedCountQueryHandler() {
        Context ctx = createContext();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("testAssets\\proteintest.json"));
            String inString = new String(encoded);
            String outString = handler.handleRequest(inString, ctx);
            System.out.println(outString);
        } catch (IOException e) {
            System.err.println("IO exception: " + e.getMessage());
        }
    }


    @Test
    public void testSeedPegQueryHandler() {
        Context ctx = createContext();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("testAssets\\pegtest.json"));
            String inString = new String(encoded);
            String outString = handler.handleRequest(inString, ctx);
            System.out.println(outString);
        } catch (IOException e) {
            System.err.println("IO exception: " + e.getMessage());
        }
    }


}
