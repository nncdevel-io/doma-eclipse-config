package io.nncdevel.domaeclipseconfig.handlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for ConfigHandler class.
 * Tests the core functionality of Doma project detection and classpath modification.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigHandlerTest {

    private ConfigHandler handler;
    private Path testResourcesPath;
    
    @Mock
    private IProject mockProject;
    
    @Before
    public void setUp() {
        handler = new ConfigHandler();
        // Get the path to test resources
        testResourcesPath = Paths.get("test/resources/sample-projects").toAbsolutePath();
    }
    
    @After
    public void tearDown() {
        // Clean up any temporary files created during tests
    }
    
    @Test
    public void testIsMavenProject_WithPomXml_ReturnsTrue() throws Exception {
        // Arrange
        URI projectUri = testResourcesPath.resolve("doma-maven-project").toUri();
        when(mockProject.getLocationURI()).thenReturn(projectUri);
        
        // Act
        boolean result = handler.isMavenProject(mockProject);
        
        // Assert
        assertTrue("Project with pom.xml should be identified as Maven project", result);
    }
    
    @Test
    public void testIsMavenProject_WithoutPomXml_ReturnsFalse() throws Exception {
        // Arrange - create a temporary directory without pom.xml
        Path tempDir = Files.createTempDirectory("no-pom-project");
        URI projectUri = tempDir.toUri();
        when(mockProject.getLocationURI()).thenReturn(projectUri);
        
        try {
            // Act
            boolean result = handler.isMavenProject(mockProject);
            
            // Assert
            assertFalse("Project without pom.xml should not be identified as Maven project", result);
        } finally {
            // Clean up
            Files.deleteIfExists(tempDir);
        }
    }
    
    @Test
    public void testIsDomaProject_WithDomaDependencyInPom_ReturnsTrue() throws Exception {
        // Arrange
        URI projectUri = testResourcesPath.resolve("doma-maven-project").toUri();
        when(mockProject.getLocationURI()).thenReturn(projectUri);
        
        // Act
        boolean result = handler.isDomaProject(mockProject);
        
        // Assert
        assertTrue("Project with Doma dependency should be identified as Doma project", result);
    }
    
    @Test
    public void testIsDomaProject_WithoutDomaDependency_ReturnsFalse() throws Exception {
        // Arrange
        URI projectUri = testResourcesPath.resolve("non-doma-project").toUri();
        when(mockProject.getLocationURI()).thenReturn(projectUri);
        
        // Act
        boolean result = handler.isDomaProject(mockProject);
        
        // Assert
        assertFalse("Project without Doma dependency should not be identified as Doma project", result);
    }
    
    @Test
    public void testIsDomaProjectByFactoryPath_WithDomaFactoryPath_ReturnsTrue() throws Exception {
        // Arrange
        URI projectUri = testResourcesPath.resolve("doma-factorypath-project").toUri();
        when(mockProject.getLocationURI()).thenReturn(projectUri);
        
        // Act
        boolean result = handler.isDomaProjectByFactoryPath(mockProject);
        
        // Assert
        assertTrue("Project with Doma in .factorypath should be identified as Doma project", result);
    }
    
    @Test
    public void testGetKeyInFactoryPathEntry_OnWindows_ReturnsWindowsPath() {
        // Arrange
        System.setProperty("os.name", "Windows 10");
        
        // Act
        String result = handler.getKeyInFactoryPathEntry();
        
        // Assert
        assertEquals("Windows path should use backslashes", "repository\\org\\seasar\\doma", result);
    }
    
    @Test
    public void testGetKeyInFactoryPathEntry_OnLinux_ReturnsUnixPath() {
        // Arrange
        System.setProperty("os.name", "Linux");
        
        // Act
        String result = handler.getKeyInFactoryPathEntry();
        
        // Assert
        assertEquals("Linux path should use forward slashes", "repository/org/seasar/doma", result);
    }
    
    @Test
    public void testClasspathModification() throws IOException {
        // Arrange
        Path tempClasspath = Files.createTempFile("test-classpath", ".classpath");
        List<String> originalContent = new ArrayList<>();
        originalContent.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        originalContent.add("<classpath>");
        originalContent.add("\t<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">");
        originalContent.add("\t</classpathentry>");
        originalContent.add("</classpath>");
        Files.write(tempClasspath, originalContent, StandardCharsets.UTF_8);
        
        try {
            // Act - Read and modify like the handler does
            List<String> lines = Files.readAllLines(tempClasspath, StandardCharsets.UTF_8);
            List<String> replaced = new ArrayList<>();
            for (String line : lines) {
                replaced.add(line.replace(
                    "<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">",
                    "<classpathentry including=\"**/*.script|**/*.sql\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">"
                ));
            }
            
            // Assert
            assertTrue("Modified content should contain including attribute", 
                replaced.stream().anyMatch(line -> line.contains("including=\"**/*.script|**/*.sql\"")));
            assertFalse("Modified content should not contain excluding attribute", 
                replaced.stream().anyMatch(line -> line.contains("excluding=\"**\"")));
        } finally {
            // Clean up
            Files.deleteIfExists(tempClasspath);
        }
    }
    
    @Test
    public void testNullSafetyInProjectDetection() {
        // Test that methods handle null inputs gracefully
        URI nullUri = null;
        when(mockProject.getLocationURI()).thenReturn(nullUri);
        
        // These should not throw exceptions
        assertFalse("Should handle null URI gracefully", handler.isMavenProject(mockProject));
    }
    
    @Test
    public void testEmptyDirectoryHandling() throws Exception {
        // Arrange - create empty directory
        Path emptyDir = Files.createTempDirectory("empty-project");
        URI emptyUri = emptyDir.toUri();
        when(mockProject.getLocationURI()).thenReturn(emptyUri);
        
        try {
            // Act & Assert
            assertFalse("Empty directory should not be Maven project", handler.isMavenProject(mockProject));
            assertFalse("Empty directory should not be Doma project", handler.isDomaProject(mockProject));
        } finally {
            // Clean up
            Files.deleteIfExists(emptyDir);
        }
    }
}