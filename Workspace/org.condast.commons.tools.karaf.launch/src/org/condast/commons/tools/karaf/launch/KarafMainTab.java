package org.condast.commons.tools.karaf.launch;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

public class KarafMainTab extends AbstractLaunchConfigurationTab {

    private Text homeText;

    @Override
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        setControl(composite);

        Label label = new Label(composite, SWT.NONE);
        label.setText("Karaf installation:");

        homeText = new Text(composite, SWT.BORDER);
        homeText.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button browseButton = new Button(composite, SWT.PUSH);
        browseButton.setText("Browse...");

        browseButton.addListener(SWT.Selection, event -> {
            DirectoryDialog dialog =
                    new DirectoryDialog(composite.getShell());

            dialog.setText("Select Apache Karaf Installation");

            String selected = dialog.open();
            if (selected != null) {
                homeText.setText(selected);
            }
        });

        ModifyListener listener = event -> updateLaunchConfigurationDialog();
        homeText.addModifyListener(listener);

        Label hint = new Label(composite, SWT.WRAP);
        hint.setText(
                "Select the Karaf installation directory. "
                + "Eclipse variables such as "
                + "${env_var:KARAF_HOME} are supported.");

        GridData hintData =
                new GridData(SWT.FILL, SWT.CENTER, true, false);
        hintData.horizontalSpan = 3;
        hint.setLayoutData(hintData);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(
                IKarafLaunchDefinition.ATTR_KARAF_HOME,
                "${env_var:KARAF_HOME}");
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            homeText.setText(configuration.getAttribute(
            		IKarafLaunchDefinition.ATTR_KARAF_HOME, ""));
        } catch (CoreException e) {
            homeText.setText("");
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(
        		IKarafLaunchDefinition.ATTR_KARAF_HOME,
                homeText.getText().trim());
    }

    @Override
    public boolean isValid(ILaunchConfiguration configuration) {
        setErrorMessage(null);

        String path = homeText.getText().trim();

        if (path.isEmpty()) {
            setErrorMessage("Select a Karaf installation directory.");
            return false;
        }

        try {
            path = VariablesPlugin.getDefault()
                    .getStringVariableManager()
                    .performStringSubstitution(path);
        } catch (CoreException e) {
            setErrorMessage("Cannot resolve Karaf installation variable.");
            return false;
        }

        File home = new File(path);
        File script = new File(home, "bin/karaf.bat");

        if (!home.isDirectory() || !script.isFile()) {
            setErrorMessage(
                    "The directory must contain bin/karaf.bat.");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "Karaf";
    }

    @Override
    public Control getControl() {
        return super.getControl();
    }
}
