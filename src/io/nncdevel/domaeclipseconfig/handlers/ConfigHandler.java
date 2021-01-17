package io.nncdevel.domaeclipseconfig.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import io.nncdevel.domaeclipseconfig.Activator;

public class ConfigHandler extends AbstractHandler {

	ILog log = Activator.getDefault().getLog();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		var projects = Arrays.stream(root.getProjects()).filter(it -> isMavenProject(it) && isDomaProject(it))
				.collect(Collectors.toList());
		if(projects.isEmpty()) {
			MessageDialog.openInformation(window.getShell(), "doma-eclipse-config", "Domaプロジェクトが見つかりませんでした。");
			return null;
		}
		

		for (IProject p : projects) {			
			try {
				var roota = Paths.get(p.getLocationURI());
				var classfilepath = Files.walk(roota).filter(it -> it.toFile().isFile())
						.filter(it -> it.getFileName().endsWith(".classpath")).findFirst();

				if (classfilepath.isPresent()) {
					var text = Files.readString(classfilepath.get(), StandardCharsets.UTF_8);
					var replaced = text.replace(
							"<classpathentry excluding=\"**\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">",
							"<classpathentry including=\"**/*.script|**/*.sql\" kind=\"src\" output=\"target/classes\" path=\"src/main/resources\">");
					Files.writeString(classfilepath.get(), replaced, StandardCharsets.UTF_8,
							StandardOpenOption.TRUNCATE_EXISTING);
				}
			} catch (Exception e) {
				// TODO: handle exception
				// do nothing
			}
		}
		var message = String.join(System.lineSeparator(), projects.stream().map(it -> it.getName()).collect(Collectors.toList()));
		MessageDialog.openInformation(window.getShell(), "doma-eclipse-config", "Domaプロジェクトのビルドパスを変更しました。" + System.lineSeparator() + message);
		return null;
	}

	private boolean isMavenProject(IProject projectDir) {
		var files = Paths.get(projectDir.getLocationURI()).toFile().listFiles();
		return Arrays.stream(files).filter(it -> it.getName().equals("pom.xml")).findFirst().isPresent();
	}

	private boolean isDomaProject(IProject projectDir) {
		try {
			var files = Paths.get(projectDir.getLocationURI()).toFile().listFiles();
			var factorypath = Arrays.stream(files).filter(it -> it.getName().equals(".factorypath")).findFirst();
			if (factorypath.isPresent()) {
				return Files.readString(factorypath.get().toPath()).contains("repository/org/seasar/doma");
			}
			return false;
		} catch (IOException e) {
			return false;
		}
	}
}
