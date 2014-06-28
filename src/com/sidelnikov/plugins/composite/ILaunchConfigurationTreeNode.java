package com.sidelnikov.plugins.composite;

import org.eclipse.swt.graphics.Image;

interface ILaunchConfigurationTreeNode {
	public ILaunchConfigurationTreeNode[] getChildren();
	public ILaunchConfigurationTreeNode getParent();
	public boolean hasChildren();
	public boolean hasSelectedChildren();	

	public Image getImage();
	public String getText();

	public boolean isChecked();
	public boolean isGrayed();

	public void setCheckState(boolean state);
}
