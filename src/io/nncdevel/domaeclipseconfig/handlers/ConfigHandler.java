package io.nncdevel.domaeclipseconfig.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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

	private static final String BEFORE = "<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">";
	private static final String AFTER = "<classpathentry including=\"**/*.script|**/*.sql\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">";
	ILog log = Activator.getDefault().getLog();

	private static final String POM_DOMA_KEY = "<groupId>org.seasar.doma</groupId>";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		List<IProject> projects = Arrays.stream(root.getProjects())
				.filter(it -> isMavenProject(it) && isDomaProject(it)).collect(Collectors.toList());
		if (projects.isEmpty()) {
			MessageDialog.openInformation(window.getShell(), "doma-eclipse-config", "Domaプロジェクトが見つかりませんでした。");
			return null;
		}

		for (IProject p : projects) {
			try {
				Path roota = Paths.get(p.getLocationURI());
				Optional<Path> classfilepath = Files.walk(roota).filter(it -> it.toFile().isFile())
						.filter(it -> it.getFileName().endsWith(".classpath")).findFirst();

				if (classfilepath.isPresent()) {
					List<String> replaced = new ArrayList<>();
					List<String> lines = Files.readAllLines(classfilepath.get(), StandardCharsets.UTF_8);
					for (String line : lines) {
						replaced.add(line.replace(BEFORE, AFTER));
					}
					replaced.forEach(System.out::println);
					Files.write(classfilepath.get(), replaced, StandardCharsets.UTF_8,
							StandardOpenOption.TRUNCATE_EXISTING);
				}
				p.refreshLocal(IResource.PROJECT, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String message = String.join(System.lineSeparator(),
				projects.stream().map(it -> it.getName()).collect(Collectors.toList()));
		MessageDialog.openInformation(window.getShell(), "doma-eclipse-config",
				"Doma2プロジェクトのビルドパスを変更しました。" + System.lineSeparator() + message);
		return null;
	}

	private boolean isMavenProject(IProject projectDir) {
		File[] files = Paths.get(projectDir.getLocationURI()).toFile().listFiles();
		return Arrays.stream(files).filter(it -> it.getName().equals("pom.xml")).findFirst().isPresent();
	}

	private boolean isDomaProject(IProject project) {
		return isDomaProjectByPom(project) || isDomaProjectByFactoryPath(project);
	}

	private boolean isDomaProjectByPom(IProject project) {
		try {
			File[] files = Paths.get(project.getLocationURI()).toFile().listFiles();
			Optional<File> pomfile = Arrays.stream(files).filter(it -> it.getName().equals("pom.xml")).findFirst();
			if (pomfile.isPresent()) {
				Optional<String> line = Files.readAllLines(pomfile.get().toPath(), StandardCharsets.UTF_8).stream()
						.filter(it -> it.contains(POM_DOMA_KEY)).findFirst();
				return line.isPresent();
			}
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	private boolean isDomaProjectByFactoryPath(IProject project) {
		String containsKey = getKeyInFactoryPathEntry();
		try {
			File[] files = Paths.get(project.getLocationURI()).toFile().listFiles();
			Optional<File> factorypath = Arrays.stream(files).filter(it -> it.getName().equals(".factorypath"))
					.findFirst();
			if (factorypath.isPresent()) {
				Optional<String> line = Files.readAllLines(factorypath.get().toPath(), StandardCharsets.UTF_8).stream()
						.filter(it -> it.contains(containsKey)).findFirst();
				return line.isPresent();
			}
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	private String getKeyInFactoryPathEntry() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.startsWith("win")) {
			return "repository\\org\\seasar\\doma";
		} else {
			return "repository/org/seasar/doma";
		}
	}

}
