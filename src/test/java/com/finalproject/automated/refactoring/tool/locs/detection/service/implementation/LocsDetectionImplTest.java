package com.finalproject.automated.refactoring.tool.locs.detection.service.implementation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 24 October 2018
 */

public class LocsDetectionImplTest {

    private static final Long LLOC_COUNT = 15L;

    private LocsDetectionImpl locsDetection;

    private String body;

    @Before
    public void setUp() {
        locsDetection = new LocsDetectionImpl();
        body = createBody();
    }

    @Test
    public void llocDetection_success() {
        Long count = locsDetection.llocDetection(body);
        assertEquals(LLOC_COUNT, count);
    }

    @Test
    public void llocDetection_success_emptyBody() {
        Long count = locsDetection.llocDetection("");
        assertEquals(0, count.intValue());
    }

    @Test(expected = NullPointerException.class)
    public void llocDetection_success_nullBody() {
        locsDetection.llocDetection(null);
    }

    private String createBody() {
        return "package path;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "\n" +
                "public class Filename implements Serializable {\n" +
                "\n" +
                "    private String name;\n" +
                "    private String extension;\n" +
                "\n" +
                "    @SuppressWarnings()\n" +
                "    public Filename() throws Exception, IOException {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @SuppressWarnings()\n" +
                "    public Filename(@NonNull String name, @NonNull String extension) throws Exception, IOException {\n" +
                "        this.name = name;\n" +
                "        this.extension = extension;\n" +
                "    }\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "\n" +
                "    public void setName(@NonNull String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    public String getExtension() {\n" +
                "        return extension;\n" +
                "    }\n" +
                "\n" +
                "    public void setExtension(@NonNull String extension) {\n" +
                "        this.extension = extension;\n" +
                "    }\n" +
                "}";
    }
}