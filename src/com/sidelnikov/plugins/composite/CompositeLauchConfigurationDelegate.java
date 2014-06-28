package com.sidelnikov.plugins.composite;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class CompositeLauchConfigurationDelegate implements ILaunchConfigurationDelegate {

	public final static String CONFIGURATION_TYPE_IDENTIFIER = "com.sidelnikov.plugins.composite.launcher";
	public static final String ATTRIBUTE_CONFIGURATIONS_LIST = "Configurations";
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		List<?> attribute = configuration.getAttribute(
				CompositeLauchConfigurationDelegate.ATTRIBUTE_CONFIGURATIONS_LIST,
				new LinkedList<String>());
		List<String> mementos = Utils.castList(String.class, attribute);
		List<ILaunchConfiguration> executedConfigurations = new LinkedList<ILaunchConfiguration>();
		for (String memento: mementos) {
			try {
				ILaunchConfiguration newConfiguration = manager.getLaunchConfiguration(memento);
				if (newConfiguration != null && newConfiguration.supportsMode(mode)) {
					executedConfigurations.add(newConfiguration);	
				}
			} catch (CoreException e) {
			}
		};
		for (ILaunchConfiguration executedConfiguration: executedConfigurations) {
			executedConfiguration.launch(mode, monitor);
		}
	}

}
