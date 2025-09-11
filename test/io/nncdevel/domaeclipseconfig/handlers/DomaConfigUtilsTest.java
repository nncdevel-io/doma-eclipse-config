package io.nncdevel.domaeclipseconfig.handlers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Simple unit tests for DomaConfigUtils class functionality.
 * Tests the core logic without Eclipse dependencies.
 */
public class DomaConfigUtilsTest {

    private String originalOsName;
    
    @Before
    public void setUp() {
        originalOsName = System.getProperty("os.name");
    }
    
    @Test
    public void testGetKeyInFactoryPathEntry_OnWindows() {
        // Arrange
        System.setProperty("os.name", "Windows 10");
        
        try {
            // Act
            String result = DomaConfigUtils.getKeyInFactoryPathEntry();
            
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
        System.setProperty("os.name", "Linux");
        
        try {
            // Act
            String result = DomaConfigUtils.getKeyInFactoryPathEntry();
            
            // Assert
            assertEquals("Linux path should use forward slashes", 
                "repository/org/seasar/doma", result);
        } finally {
            // Restore original OS name
            System.setProperty("os.name", originalOsName);
        }
    }
    
    @Test
    public void testContainsDomaDependency_WithDomaDependency_ReturnsTrue() {
        // Arrange
        List<String> pomContent = new ArrayList<>();
        pomContent.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pomContent.add("<project>");
        pomContent.add("  <dependencies>");
        pomContent.add("    <dependency>");
        pomContent.add("      <groupId>org.seasar.doma</groupId>");
        pomContent.add("      <artifactId>doma-core</artifactId>");
        pomContent.add("    </dependency>");
        pomContent.add("  </dependencies>");
        pomContent.add("</project>");
        
        // Act
        boolean result = DomaConfigUtils.containsDomaDependency(pomContent);
        
        // Assert
        assertTrue("Should detect Doma dependency", result);
    }
    
    @Test
    public void testContainsDomaDependency_WithoutDomaDependency_ReturnsFalse() {
        // Arrange
        List<String> pomContent = new ArrayList<>();
        pomContent.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pomContent.add("<project>");
        pomContent.add("  <dependencies>");
        pomContent.add("    <dependency>");
        pomContent.add("      <groupId>junit</groupId>");
        pomContent.add("      <artifactId>junit</artifactId>");
        pomContent.add("    </dependency>");
        pomContent.add("  </dependencies>");
        pomContent.add("</project>");
        
        // Act
        boolean result = DomaConfigUtils.containsDomaDependency(pomContent);
        
        // Assert
        assertFalse("Should not detect Doma dependency", result);
    }
    
    @Test
    public void testContainsDomaFactoryPath_WithDomaFactory_ReturnsTrue() {
        // Arrange (using Linux path for test)
        System.setProperty("os.name", "Linux");
        List<String> factoryPathContent = new ArrayList<>();
        factoryPathContent.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        factoryPathContent.add("<factorypath>");
        factoryPathContent.add("  <factorypathentry kind=\"VARJAR\" id=\"M2_REPO/repository/org/seasar/doma/doma-processor/2.44.1/doma-processor-2.44.1.jar\"/>");
        factoryPathContent.add("</factorypath>");
        
        try {
            // Act
            boolean result = DomaConfigUtils.containsDomaFactoryPath(factoryPathContent);
            
            // Assert
            assertTrue("Should detect Doma factory path", result);
        } finally {
            System.setProperty("os.name", originalOsName);
        }
    }
    
    @Test
    public void testTransformClasspathContent() {
        // Arrange
        List<String> originalContent = new ArrayList<>();
        originalContent.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        originalContent.add("<classpath>");
        originalContent.add("\t<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">");
        originalContent.add("\t\t<attributes>");
        originalContent.add("\t\t\t<attribute name=\"maven.pomderived\" value=\"true\"/>");
        originalContent.add("\t\t</attributes>");
        originalContent.add("\t</classpathentry>");
        originalContent.add("</classpath>");
        
        // Act
        List<String> result = DomaConfigUtils.transformClasspathContent(originalContent);
        
        // Assert
        boolean hasIncluding = result.stream().anyMatch(line -> line.contains("including=\"**/*.script|**/*.sql\""));
        boolean hasExcluding = result.stream().anyMatch(line -> line.contains("excluding=\"**\""));
        boolean preservesAttributes = result.stream().anyMatch(line -> line.contains("maven.pomderived"));
        
        assertTrue("Processed content should have including attribute", hasIncluding);
        assertFalse("Processed content should not have excluding attribute", hasExcluding);
        assertTrue("Processed content should preserve other attributes", preservesAttributes);
    }
    
    @Test
    public void testUpdateClasspathFile() throws IOException {
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
            // Act
            DomaConfigUtils.updateClasspathFile(tempFile);
            
            // Assert
            List<String> processed = Files.readAllLines(tempFile, StandardCharsets.UTF_8);
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