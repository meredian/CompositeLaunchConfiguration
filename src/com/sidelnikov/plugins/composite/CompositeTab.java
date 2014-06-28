package com.sidelnikov.plugins.composite;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.sidelnikov.plugins.composite.ILaunchConfigurationTreeNode;
import com.sidelnikov.plugins.composite.LaunchConfigurationCollection;

public class CompositeTab extends AbstractLaunchConfigurationTab {

	public static final String COMP_TAB_NAME = "Launch Configurations";
	public static final String COMP_TAB_CONFIGURATIONS_LIST_NAME = "Select launching configurations:";
	
	private LaunchConfigurationCollection launchCollection;
	private CheckboxTreeViewer configurationViewer;

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));
		comp.setFont(parent.getFont());
		setControl(comp);
		
		Group group = new Group(comp, SWT.NONE);
		group.setFont(parent.getFont());
		group.setLayout(new GridLayout(1, true));
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setText(COMP_TAB_CONFIGURATIONS_LIST_NAME);
		
		configurationViewer = new CheckboxTreeViewer(group, SWT.BORDER);
		configurationViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		configurationViewer.getTree().setFont(parent.getFont());
		configurationViewer.setContentProvider(new LaunchConfigurationTreeContentProvider());
		configurationViewer.setLabelProvider(new LaunchConfigurationTreeLabelProvider());
		configurationViewer.setCheckStateProvider(new LaunchConfigurationTreeCheckProvider());
		configurationViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				((ILaunchConfigurationTreeNode) event.getElement()).setCheckState(event.getChecked());
				((CheckboxTreeViewer) event.getCheckable()).refresh(true);
				CompositeTab.this.updateLaunchConfigurationDialog();
			}
		});
		configurationViewer.setAutoExpandLevel(2);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		if (launchCollection == null) {
			launchCollection = new LaunchConfigurationCollection(DebugPlugin.getDefault().getLaunchManager());
		}

		try {
			List<?> attribute = configuration.getAttribute(
					CompositeLauchConfigurationDelegate.ATTRIBUTE_CONFIGURATIONS_LIST,
					new LinkedList<String>());
			List<String> identifiers = Utils.castList(String.class, attribute);
			launchCollection.setSelectedLaunchConfigrations(identifiers);
		} catch (CoreException e) {				
		} finally {
			if (configurationViewer != null) {
				configurationViewer.setInput(launchCollection);
			}
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (launchCollection != null) {
			configuration.setAttribute(CompositeLauchConfigurationDelegate.ATTRIBUTE_CONFIGURATIONS_LIST,
					launchCollection.getSelectedLaunchConfigrationIdentifiers());
		}
	}

	@Override
	public String getName() {
		return COMP_TAB_NAME;
	}

	class LaunchConfigurationTreeLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getImage(Object element) {
			return ((ILaunchConfigurationTreeNode) element).getImage();
		}

		@Override
		public String getText(Object element) {
			return ((ILaunchConfigurationTreeNode) element).getText();
		}
		
	}

	class LaunchConfigurationTreeContentProvider implements ITreeContentProvider {
		public LaunchConfigurationCollection collection;
		public Object[] getChildren(Object parent) {
			return ((ILaunchConfigurationTreeNode) parent).getChildren();
		}

		public Object getParent(Object element) {
			return ((ILaunchConfigurationTreeNode) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return ((ILaunchConfigurationTreeNode) element).hasChildren();
		}

		public Object[] getElements(Object inputElement) {
			return ((LaunchConfigurationCollection) inputElement).getChildren();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	class LaunchConfigurationTreeCheckProvider implements ICheckStateProvider {
		@Override
		public boolean isChecked(Object element) {
			return ((ILaunchConfigurationTreeNode) element).isChecked();
		}

		@Override
		public boolean isGrayed(Object element) {
			return ((ILaunchConfigurationTreeNode) element).isGrayed();
		}		
	}
}
