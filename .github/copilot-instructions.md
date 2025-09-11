# Doma Eclipse Config Plugin

Doma Eclipse Config is an Eclipse plugin that automatically configures build paths for Doma2 projects in Eclipse workspaces. The plugin modifies `.classpath` files to include SQL and script resources properly for Maven projects using the Doma2 ORM framework.

**ALWAYS reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.**

## Working Effectively

### Prerequisites and Environment Setup
- **CRITICAL**: This project REQUIRES Eclipse IDE with PDE (Plug-in Development Environment)
- **Java Version**: Java 8+ (JavaSE-1.8 minimum, as specified in MANIFEST.MF)
- **Eclipse Download**: Install Eclipse IDE for Eclipse Committers from https://www.eclipse.org/downloads/packages/
  - **Alternative**: Eclipse IDE for RCP and RAP Developers (also includes PDE)
- **IMPORTANT**: Standard Java compilation with `javac` WILL NOT WORK due to Eclipse-specific dependencies
- **Environment Note**: This plugin cannot be built in headless CI/CD environments without Eclipse runtime

### Repository Quick Facts
```bash
# Repository contains:
find . -name "*.java" | wc -l  # 2 Java source files
find . -name "*.xml" | wc -l   # 1 configuration file (plugin.xml)

# Key files sizes:
wc -l src/io/nncdevel/domaeclipseconfig/handlers/ConfigHandler.java  # ~140 lines
wc -l src/io/nncdevel/domaeclipseconfig/Activator.java               # ~45 lines
```

### Project Structure
```
doma-eclipse-config/
├── src/io/nncdevel/domaeclipseconfig/
│   ├── Activator.java                    # Plugin lifecycle management
│   └── handlers/ConfigHandler.java       # Main plugin logic
├── META-INF/MANIFEST.MF                  # Bundle manifest and dependencies
├── plugin.xml                           # Plugin configuration and UI extension points
├── build.properties                     # Build configuration
├── icons/                               # Plugin toolbar icons
├── .project                            # Eclipse project configuration
├── .classpath                          # Eclipse classpath configuration
└── .settings/                          # Eclipse IDE settings
```

### Building the Plugin
**NEVER CANCEL: Plugin build process may take 5-10 minutes including Eclipse startup time. Set timeouts to 600+ seconds.**

#### Method 1: Eclipse PDE Export (RECOMMENDED)
1. **Install and Launch Eclipse IDE** (TIMEOUT: 300+ seconds):
   ```bash
   # Download Eclipse IDE for Eclipse Committers
   # Extract and launch eclipse executable
   # NEVER CANCEL: Eclipse startup takes 60-120 seconds
   ```

2. **Import Project** (TIMEOUT: 60+ seconds):
   ```
   1. File > Import...
   2. General > Existing Projects into Workspace
   3. Browse to repository root directory
   4. Select "doma-eclipse-config" project
   5. Click Finish
   6. Wait for workspace refresh to complete
   ```

3. **Verify Project Setup** (IMMEDIATE):
   ```
   - Check project has no compilation errors
   - Verify JRE version is 1.8 or higher
   - Confirm PDE nature is enabled (project icon shows plugin symbol)
   ```

4. **Export Plugin JAR** (TIMEOUT: 180+ seconds):
   ```
   1. Right-click project > Export...
   2. Plug-in Development > Deployable plug-ins and fragments
   3. Select the doma-eclipse-config plugin
   4. Specify destination directory (e.g., ./build-output)
   5. Leave "Export for multiple platforms" unchecked
   6. Click Finish
   7. NEVER CANCEL: Export process takes 60-180 seconds
   8. Result: io.nncdevel.doma-eclipse-config_0.0.3.jar
   ```

#### Alternative Method: Update Site Export
For deployment to multiple Eclipse instances:
```
1. Right-click project > Export...
2. Plug-in Development > Deployable features
3. Create feature project first if needed
4. Export as update site archive
5. TIMEOUT: 300+ seconds for complete export
```

### Installation and Testing
1. **Install Plugin**:
   ```bash
   # Copy JAR to Eclipse dropins folder
   cp io.nncdevel.doma-eclipse-config_*.jar $ECLIPSE_HOME/dropins/
   # Restart Eclipse (takes 60+ seconds)
   ```
   
   **Alternative Installation**:
   ```
   1. Help > Install New Software...
   2. Add Local... > Select exported update site
   3. Follow installation wizard
   4. Restart when prompted
   ```

2. **CRITICAL Manual Validation Steps** (REQUIRED AFTER ANY CODE CHANGES):
   
   **Step 1: Visual Verification**
   ```
   - Launch Eclipse IDE
   - Look for Doma icon in main toolbar (small square icon)
   - If missing, check Error Log view for plugin load failures
   ```
   
   **Step 2: Create Test Maven Project**
   ```
   1. File > New > Maven Project
   2. Use maven-archetype-quickstart
   3. Add to pom.xml:
      <dependency>
        <groupId>org.seasar.doma</groupId>
        <artifactId>doma-core</artifactId>
        <version>2.51.0</version>
      </dependency>
   4. Refresh project
   ```
   
   **Step 3: Test Plugin Functionality**
   ```
   1. Click Doma toolbar icon
   2. Verify success dialog appears showing project name
   3. Check .classpath file contains:
      <classpathentry including="**/*.script|**/*.sql" ...>
   4. If no projects found, verify pom.xml contains Doma dependency
   ```
   
   **Step 4: Test Alternative Detection Method**
   ```
   1. Create .factorypath file in project root with content:
      <?xml version="1.0" encoding="UTF-8"?>
      <factorypath>
        <factorypathentry kind="VARJAR" id="M2_REPO/org/seasar/doma/doma-processor/2.51.0/doma-processor-2.51.0.jar"/>
      </factorypath>
   2. Click Doma icon again
   3. Verify project is still detected
   ```

### Alternative Build Methods
- **Maven/Gradle builds**: NOT SUPPORTED - Eclipse PDE required
- **Command-line builds**: NOT SUPPORTED - requires Eclipse runtime
- **CI/CD automation**: Limited - requires headless Eclipse setup

### Key Plugin Functionality
The plugin performs these operations:
1. **Project Detection**: Scans workspace for Maven projects containing Doma2 dependencies
2. **Classpath Modification**: Changes `.classpath` entries from:
   ```xml
   <classpathentry excluding="**" kind="src" output="target/classes" path="src/main/resources">
   ```
   to:
   ```xml
   <classpathentry including="**/*.script|**/*.sql" kind="src" output="target/classes" path="src/main/resources">
   ```
3. **User Notification**: Shows dialog with list of modified projects

### Validation Requirements
**ALWAYS** validate plugin changes by:
1. **Build Testing**: Ensure plugin exports successfully without errors
2. **Installation Testing**: Install in clean Eclipse and verify toolbar icon appears  
3. **Functional Testing**: Test with real Doma2 Maven project:
   - Create test project with pom.xml containing `<groupId>org.seasar.doma</groupId>`
   - Add `.factorypath` file with doma references (alternative detection method)
   - Click Doma toolbar button
   - Verify success dialog appears
   - Check `.classpath` file modifications are correct
4. **Cross-platform Testing**: Plugin handles Windows vs Unix path separators in `.factorypath` detection

### Common Development Tasks

#### Modifying Plugin Logic
- **Primary file**: `src/io/nncdevel/domaeclipseconfig/handlers/ConfigHandler.java`
- **Key constants**: BEFORE/AFTER classpath strings, POM_DOMA_KEY
- **Detection methods**: `isDomaProjectByPom()`, `isDomaProjectByFactoryPath()`
- **ALWAYS test both detection methods after changes**

#### Updating Plugin Metadata
- **Version**: Update in `META-INF/MANIFEST.MF` (Bundle-Version)
- **Dependencies**: Modify Require-Bundle in MANIFEST.MF
- **UI Elements**: Update `plugin.xml` for toolbar/menu changes

#### Debugging
- Eclipse plugin debugging requires "Run As > Eclipse Application" 
- **NEVER CANCEL**: Debug Eclipse startup takes 60+ seconds
- Use Console view for System.out.println() output from plugin
- Check Error Log view for exceptions

## Common Commands and Expected Outputs

### Repository Structure
```bash
ls -la  # Repository root contents:
# .classpath      - Eclipse classpath configuration
# .git/           - Git repository data  
# .gitignore      - Git ignore patterns
# .project        - Eclipse project configuration
# .settings/      - Eclipse workspace settings
# LICENSE         - MIT license file
# META-INF/       - OSGi bundle manifest
# README.md       - Japanese documentation
# build.properties - PDE build configuration
# icons/          - Plugin toolbar icons (doma.png, doma@2x.png)
# plugin.xml      - Plugin extension point definitions
# src/            - Java source code
```

### Source Code Structure  
```bash
find src -type f
# src/io/nncdevel/domaeclipseconfig/Activator.java
# src/io/nncdevel/domaeclipseconfig/handlers/ConfigHandler.java

wc -l src/io/nncdevel/domaeclipseconfig/handlers/ConfigHandler.java
# 139 /home/runner/work/doma-eclipse-config/doma-eclipse-config/src/io/nncdevel/domaeclipseconfig/handlers/ConfigHandler.java

wc -l src/io/nncdevel/domaeclipseconfig/Activator.java  
# 44 /home/runner/work/doma-eclipse-config/doma-eclipse-config/src/io/nncdevel/domaeclipseconfig/Activator.java
```

### Key Configuration Files
```bash
cat META-INF/MANIFEST.MF | grep -E "(Bundle-Name|Bundle-Version|Bundle-RequiredExecutionEnvironment)"
# Bundle-Name: doma-eclipse-config
# Bundle-Version: 0.0.3
# Bundle-RequiredExecutionEnvironment: JavaSE-1.8

cat .project | grep -A2 "projectDescription"
# <projectDescription>
# 	<name>doma-eclipse-config</name>
# 	<comment></comment>

cat build.properties
# source.. = src/
# output.. = bin/
# bin.includes = plugin.xml,\
#                META-INF/,\
#                .,\
#                icons/
```

## Important Code Locations and Architecture

### Core Plugin Files
- **ConfigHandler.java** - Main plugin logic, modify for functional changes
  - `execute()` method: Entry point when toolbar button clicked
  - `isMavenProject()`: Detects Maven projects by pom.xml presence
  - `isDomaProject()`: Combines POM and FactoryPath detection methods
  - Constants: `BEFORE`/`AFTER` strings for classpath transformation
- **Activator.java** - Plugin lifecycle management, rarely needs modification  
- **plugin.xml** - UI configuration, toolbar icon, command definitions
- **MANIFEST.MF** - OSGi bundle dependencies and metadata

### Detection Logic
The plugin detects Doma2 projects using two methods:
1. **POM Detection**: Searches pom.xml for `<groupId>org.seasar.doma</groupId>`
2. **FactoryPath Detection**: Searches `.factorypath` for Maven repository paths containing `/org/seasar/doma`

### Build Time Expectations
- **Eclipse Import**: 30-60 seconds
- **Plugin Export**: 60-120 seconds  
- **Eclipse Restart**: 30-60 seconds
- **Total Build Cycle**: 3-5 minutes minimum

**CRITICAL**: NEVER CANCEL builds or exports - Eclipse PDE operations must complete fully.

## Troubleshooting Common Issues

### Build Failures
**Problem**: "Cannot find symbol" compilation errors
**Solution**: This is expected outside Eclipse PDE - use Eclipse IDE for building

**Problem**: Plugin export fails with "Missing dependencies" 
**Solution**: 
```
1. Check MANIFEST.MF Require-Bundle entries
2. Verify target platform includes required Eclipse plugins
3. Clean and rebuild workspace: Project > Clean...
```

**Problem**: Eclipse startup hangs during import
**Solution**: 
```
1. Wait minimum 300 seconds before considering timeout
2. Check Eclipse error log: Window > Show View > Error Log
3. Restart with fresh workspace if needed
```

### Runtime Failures  
**Problem**: Doma icon doesn't appear in toolbar after installation
**Solution**:
```
1. Check Eclipse Error Log for plugin loading errors
2. Verify JAR is in dropins/ folder
3. Restart Eclipse with -clean argument
4. Check plugin is enabled: Help > About > Installation Details
```

**Problem**: Plugin runs but shows "No Doma projects found"
**Solution**:
```
1. Verify test project has pom.xml with <groupId>org.seasar.doma</groupId>
2. Or create .factorypath with doma processor reference
3. Refresh project: F5
4. Check project is Maven project (has pom.xml in root)
```

### Testing Validation Failures
**Problem**: .classpath file not modified correctly  
**Solution**:
```
1. Check original .classpath contains exact BEFORE string:
   <classpathentry excluding="**" kind="src" output="target/classes" path="src/main/resources">
2. Verify Maven project structure has src/main/resources folder
3. Manual test: print replaced lines in ConfigHandler.java (line 64)
```

## Limitations and Constraints
- **Eclipse IDE Required**: Cannot build without Eclipse PDE environment
- **Manual Testing Only**: No automated test suite available
- **GUI-Dependent**: Plugin functionality requires Eclipse workspace with projects
- **Platform-Specific**: Different path handling for Windows vs Unix systems
- **Network Limitations**: Eclipse downloads may be blocked in some environments

Fixes #9.