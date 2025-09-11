package io.nncdevel.domaeclipseconfig.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for Doma Eclipse configuration logic that can be tested
 * independently of Eclipse APIs.
 */
public final class DomaConfigUtils {
    
    private static final String BEFORE = 
        "<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" "
        + "path=\"src/main/resources\">";
    private static final String AFTER = 
        "<classpathentry including=\"**/*.script|**/*.sql\" kind=\"src\" "
        + "output=\"target/classes\" path=\"src/main/resources\">";
    private static final String POM_DOMA_KEY = "<groupId>org.seasar.doma</groupId>";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DomaConfigUtils() {
        // Utility class - no instances
    }

    /**
     * Gets the path separator key for factory path entries based on OS.
     * @return the appropriate path separator string for the current OS
     */
    public static String getKeyInFactoryPathEntry() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("win")) {
            return "repository\\org\\seasar\\doma";
        } else {
            return "repository/org/seasar/doma";
        }
    }

    /**
     * Checks if a pom.xml file contains Doma dependency.
     * @param pomContent the content of the pom.xml file
     * @return true if contains Doma dependency
     */
    public static boolean containsDomaDependency(List<String> pomContent) {
        return pomContent.stream().anyMatch(line -> line.contains(POM_DOMA_KEY));
    }

    /**
     * Checks if a .factorypath file contains Doma reference.
     * @param factoryPathContent the content of the .factorypath file
     * @return true if contains Doma reference
     */
    public static boolean containsDomaFactoryPath(List<String> factoryPathContent) {
        String containsKey = getKeyInFactoryPathEntry();
        return factoryPathContent.stream().anyMatch(line -> line.contains(containsKey));
    }

    /**
     * Transforms classpath content from BEFORE to AFTER pattern.
     * @param lines the original classpath lines
     * @return the transformed lines
     */
    public static List<String> transformClasspathContent(List<String> lines) {
        return lines.stream()
            .map(line -> line.replace(BEFORE, AFTER))
            .collect(Collectors.toList());
    }

    /**
     * Updates a classpath file by replacing the BEFORE pattern with AFTER pattern.
     * @param classpathFile the path to the .classpath file
     * @throws IOException if file operations fail
     */
    public static void updateClasspathFile(Path classpathFile) throws IOException {
        List<String> lines = Files.readAllLines(classpathFile, StandardCharsets.UTF_8);
        List<String> modifiedLines = transformClasspathContent(lines);
        Files.write(classpathFile, modifiedLines, StandardCharsets.UTF_8);
    }
}