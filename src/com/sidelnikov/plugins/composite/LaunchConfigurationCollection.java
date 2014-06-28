package com.sidelnikov.plugins.composite;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

import com.sidelnikov.plugins.composite.CompositeLauchConfigurationDelegate;

class LaunchConfigurationCollection {
	protected ILaunchManager manager;
	private LaunchConfigurationTreeCategory[] categories;

	public LaunchConfigurationCollection(ILaunchManager manager) {
		this.manager = manager;

		List<LaunchConfigurationTreeCategory> categories= new LinkedList<LaunchConfigurationTreeCategory>();
		ILaunchConfigurationType[] types = this.manager.getLaunchConfigurationTypes();
		
		for (int i = types.length - 1; i >= 0; --i) {
			if  (types[i].isPublic() && !types[i].getIdentifier().equals(CompositeLauchConfigurationDelegate.CONFIGURATION_TYPE_IDENTIFIER)) {
				categories.add(new LaunchConfigurationTreeCategory(this, types[i]));
			}
		}
		this.categories = categories.toArray(new LaunchConfigurationTreeCategory[categories.size()]);
	}


	public ILaunchConfigurationTreeNode[] getChildren() {
		List<LaunchConfigurationTreeCategory> nonEmptyCategories = new LinkedList<LaunchConfigurationTreeCategory>();
		for (LaunchConfigurationTreeCategory category: categories) {
			if(category.hasChildren()) {
				nonEmptyCategories.add(category);
			}
		}
		
		return nonEmptyCategories.toArray(new LaunchConfigurationTreeCategory[nonEmptyCategories.size()]);
	}

	public List<String> getSelectedLaunchConfigrationIdentifiers() {
		LinkedList<String> identifiers = new LinkedList<String>();
		for (LaunchConfigurationTreeCategory category: categories) {
			identifiers.addAll(category.getSelectedLaunchConfigrationIdentifiers());
		};
		return identifiers;
	}

	public void setSelectedLaunchConfigrations(List<String> identifiers) {
		for (LaunchConfigurationTreeCategory category: categories) {
			category.setSelectedLaunchConfigrations(identifiers);
		}
	}
}

