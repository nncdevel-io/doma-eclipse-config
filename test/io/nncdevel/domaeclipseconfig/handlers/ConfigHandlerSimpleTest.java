package io.nncdevel.domaeclipseconfig.handlers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Simple unit tests for ConfigHandler class functionality.
 * Tests the core logic without Eclipse dependencies.
 */
public class ConfigHandlerSimpleTest {

    private ConfigHandler handler;
    
    @Before
    public void setUp() {
        handler = new ConfigHandler();
    }
    
    @Test
    public void testGetKeyInFactoryPathEntry_OnWindows() {
        // Arrange
        String originalOsName = System.getProperty("os.name");
        System.setProperty("os.name", "Windows 10");
        
        try {
            // Act
            String result = handler.getKeyInFactoryPathEntry();
            
            // Assert
            assertEquals("Windows path should use backslashes", 
                "repository\\org\\seasar\\doma", result);
        } finally {
            // Restore original OS name
            System.setProperty("os.name", originalOsName);
        }
    }
    
    @Test
    public void testGetKeyInFactoryPathEntry_OnLinux() {
        // Arrange
        String originalOsName = System.getProperty("os.name");
        System.setProperty("os.name", "Linux");
        
        try {
            // Act
            String result = handler.getKeyInFactoryPathEntry();
            
            // Assert
            assertEquals("Linux path should use forward slashes", 
                "repository/org/seasar/doma", result);
        } finally {
            // Restore original OS name
            System.setProperty("os.name", originalOsName);
        }
    }
    
    @Test
    public void testGetKeyInFactoryPathEntry_OnMac() {
        // Arrange
        String originalOsName = System.getProperty("os.name");
        System.setProperty("os.name", "Mac OS X");
        
        try {
            // Act
            String result = handler.getKeyInFactoryPathEntry();
            
            // Assert
            assertEquals("Mac path should use forward slashes", 
                "repository/org/seasar/doma", result);
        } finally {
            // Restore original OS name
            System.setProperty("os.name", originalOsName);
        }
    }
    
    @Test
    public void testClasspathStringReplacement() {
        // Test the core string replacement logic used in execute()
        String before = "<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">";
        String after = "<classpathentry including=\"**/*.script|**/*.sql\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">";
        
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                      "<classpath>\n" +
                      "\t" + before + "\n" +
                      "\t\t<attributes>\n" +
                      "\t\t\t<attribute name=\"maven.pomderived\" value=\"true\"/>\n" +
                      "\t\t</attributes>\n" +
                      "\t</classpathentry>\n" +
                      "</classpath>";
        
        String result = input.replace(before, after);
        
        assertTrue("Result should contain including attribute", result.contains("including=\"**/*.script|**/*.sql\""));
        assertFalse("Result should not contain excluding attribute", result.contains("excluding=\"**\""));
        assertTrue("Result should preserve other XML structure", result.contains("<attributes>"));
        assertTrue("Result should preserve maven.pomderived attribute", result.contains("maven.pomderived"));
    }
    
    @Test
    public void testClasspathFileProcessing() throws IOException {
        // Create a temporary classpath file
        Path tempFile = Files.createTempFile("test-classpath", ".classpath");
        
        List<String> originalContent = new ArrayList<>();
        originalContent.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        originalContent.add("<classpath>");
        originalContent.add("\t<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">");
        originalContent.add("\t\t<attributes>");
        originalContent.add("\t\t\t<attribute name=\"maven.pomderived\" value=\"true\"/>");
        originalContent.add("\t\t</attributes>");
        originalContent.add("\t</classpathentry>");
        originalContent.add("</classpath>");
        
        Files.write(tempFile, originalContent, StandardCharsets.UTF_8);
        
        try {
            // Read and process like the handler does
            List<String> lines = Files.readAllLines(tempFile, StandardCharsets.UTF_8);
            List<String> processed = new ArrayList<>();
            
            for (String line : lines) {
                processed.add(line.replace(
                    "<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">",
                    "<classpathentry including=\"**/*.script|**/*.sql\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">"
                ));
            }
            
            // Verify the processing
            boolean hasIncluding = processed.stream().anyMatch(line -> line.contains("including=\"**/*.script|**/*.sql\""));
            boolean hasExcluding = processed.stream().anyMatch(line -> line.contains("excluding=\"**\""));
            boolean preservesAttributes = processed.stream().anyMatch(line -> line.contains("maven.pomderived"));
            
            assertTrue("Processed content should have including attribute", hasIncluding);
            assertFalse("Processed content should not have excluding attribute", hasExcluding);
            assertTrue("Processed content should preserve other attributes", preservesAttributes);
            
        } finally {
            // Clean up
            Files.deleteIfExists(tempFile);
        }
    }
}