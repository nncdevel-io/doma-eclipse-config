package io.nncdevel.domaeclipseconfig.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import io.nncdevel.domaeclipseconfig.Activator;

public class ConfigHandler extends AbstractHandler {

    private static final String BEFORE = 
        "<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" "
        + "path=\"src/main/resources\">";
    private static final String AFTER = 
        "<classpathentry including=\"**/*.script|**/*.sql\" kind=\"src\" "
        + "output=\"target/classes\" path=\"src/main/resources\">";
    private final ILog log = Activator.getDefault().getLog();

    private static final String POM_DOMA_KEY = "<groupId>org.seasar.doma</groupId>";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        List<IProject> projects = Arrays.stream(root.getProjects())
                .filter(it -> isMavenProject(it) && isDomaProject(it))
                .collect(Collectors.toList());
        if (projects.isEmpty()) {
            MessageDialog.openInformation(window.getShell(), "doma-eclipse-config", 
                "Domaプロジェクトが見つかりませんでした。");
            return null;
        }

        for (IProject p : projects) {
            try {
                updateClasspathForProject(p);
                p.refreshLocal(IResource.PROJECT, null);
            } catch (Exception e) {
                log.error("Failed to update classpath for project: " + p.getName(), e);
            }
        }
        String message = String.join(System.lineSeparator(),
                projects.stream().map(it -> it.getName()).collect(Collectors.toList()));
        MessageDialog.openInformation(window.getShell(), "doma-eclipse-config",
                "Doma2プロジェクトのビルドパスを変更しました。" + System.lineSeparator() + message);
        return null;
    }

    /**
     * Checks if a project is a Maven project by looking for pom.xml file.
     * @param projectDir the project directory
     * @return true if the project contains a pom.xml file
     */
    boolean isMavenProject(IProject projectDir) {
        File[] files = getProjectFiles(projectDir);
        if (files == null) {
            return false;
        }
        return Arrays.stream(files)
            .filter(file -> "pom.xml".equals(file.getName()))
            .findFirst()
            .isPresent();
    }

    /**
     * Checks if a project is a Doma project by either pom.xml or .factorypath.
     * @param project the project to check
     * @return true if the project is a Doma project
     */
    boolean isDomaProject(IProject project) {
        return isDomaProjectByPom(project) || isDomaProjectByFactoryPath(project);
    }

    /**
     * Extracts file listing logic that was duplicated across methods.
     * @param project the project
     * @return array of files in the project root, or null if not accessible
     */
    private File[] getProjectFiles(IProject project) {
        return Paths.get(project.getLocationURI()).toFile().listFiles();
    }

    /**
     * Checks if a project is a Doma project by looking for Doma dependency in pom.xml.
     * @param project the project to check
     * @return true if pom.xml contains Doma dependency
     */
    boolean isDomaProjectByPom(IProject project) {
        try {
            File[] files = getProjectFiles(project);
            if (files == null) {
                return false;
            }
            Optional<File> pomFile = Arrays.stream(files)
                .filter(file -> "pom.xml".equals(file.getName()))
                .findFirst();
            
            if (pomFile.isPresent()) {
                List<String> pomContent = Files.readAllLines(pomFile.get().toPath(), StandardCharsets.UTF_8);
                return DomaConfigUtils.containsDomaDependency(pomContent);
            }
            return false;
        } catch (IOException e) {
            log.error("Error reading pom.xml for project: " + project.getName(), e);
            return false;
        }
    }

    /**
     * Checks if a project is a Doma project by looking for Doma in .factorypath file.
     * @param project the project to check
     * @return true if .factorypath contains Doma reference
     */
    boolean isDomaProjectByFactoryPath(IProject project) {
        String containsKey = getKeyInFactoryPathEntry();
        try {
            File[] files = getProjectFiles(project);
            if (files == null) {
                return false;
            }
            Optional<File> factoryPath = Arrays.stream(files)
                .filter(file -> ".factorypath".equals(file.getName()))
                .findFirst();
            
            if (factoryPath.isPresent()) {
                List<String> factoryPathContent = Files.readAllLines(
                    factoryPath.get().toPath(), StandardCharsets.UTF_8);
                return DomaConfigUtils.containsDomaFactoryPath(factoryPathContent);
            }
            return false;
        } catch (IOException e) {
            log.error("Error reading .factorypath for project: " + project.getName(), e);
            return false;
        }
    }

    /**
     * Gets the path separator key for factory path entries based on OS.
     * @return the appropriate path separator string for the current OS
     */
    String getKeyInFactoryPathEntry() {
        return DomaConfigUtils.getKeyInFactoryPathEntry();
    }

    /**
     * Updates the classpath file for a specific project.
     * @param project the project to update
     * @throws IOException if file operations fail
     */
    private void updateClasspathForProject(IProject project) throws IOException {
        Path rootPath = Paths.get(project.getLocationURI());
        Optional<Path> classpathFile = Files.walk(rootPath)
            .filter(path -> path.toFile().isFile())
            .filter(path -> path.getFileName().toString().equals(".classpath"))
            .findFirst();

        if (classpathFile.isPresent()) {
            DomaConfigUtils.updateClasspathFile(classpathFile.get());
        }
    }

}
