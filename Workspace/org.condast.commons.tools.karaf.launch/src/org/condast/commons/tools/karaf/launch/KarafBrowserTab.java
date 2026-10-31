package org.condast.commons.tools.karaf.launch;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class KarafBrowserTab extends AbstractLaunchConfigurationTab {

    private Button openBrowserButton;
    private Button externalBrowserButton;
    private Button internalBrowserButton;
    private Text urlText;
    private Text startupUrlText;

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        setControl(composite);

        openBrowserButton = new Button(composite, SWT.CHECK);
        openBrowserButton.setText(
                "Open application in browser after launching Karaf");
        openBrowserButton.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Label urlLabel = new Label(composite, SWT.NONE);
        urlLabel.setText("Karaf URL:");

        urlText = new Text(composite, SWT.BORDER);
        urlText.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label startupUrlLabel = new Label(composite, SWT.NONE);
        startupUrlLabel.setText("Application entry:");

        startupUrlText = new Text(composite, SWT.BORDER);
        startupUrlText.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false));

        externalBrowserButton = new Button(composite, SWT.RADIO);
        externalBrowserButton.setText(
                "External browser (Eclipse preferences)");
        externalBrowserButton.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        internalBrowserButton = new Button(composite, SWT.RADIO);
        internalBrowserButton.setText("Internal Eclipse browser");
        internalBrowserButton.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        openBrowserButton.addListener(SWT.Selection, event -> {
            updateEnabledState();
            updateLaunchConfigurationDialog();
        });

        urlText.addModifyListener(
                event -> updateLaunchConfigurationDialog());

        startupUrlText.addModifyListener(
                event -> updateLaunchConfigurationDialog());


        externalBrowserButton.addListener(
                SWT.Selection,
                event -> updateLaunchConfigurationDialog());

        internalBrowserButton.addListener(
                SWT.Selection,
                event -> updateLaunchConfigurationDialog());
    }

    private void updateEnabledState() {
        boolean enabled = openBrowserButton.getSelection();
        urlText.setEnabled(enabled);
        startupUrlText.setEnabled(enabled);
        externalBrowserButton.setEnabled(enabled);
        internalBrowserButton.setEnabled(enabled);
    }

    @Override
    public void setDefaults(
            ILaunchConfigurationWorkingCopy configuration) {

        configuration.setAttribute(
                IKarafLaunchDefinition.ATTR_OPEN_BROWSER, true);

        configuration.setAttribute(
                IKarafLaunchDefinition.ATTR_START_URL,
                IKarafLaunchDefinition.S_DEFAULT_START_URL);

        configuration.setAttribute(
                IKarafLaunchDefinition.ATTR_EXTERNAL_BROWSER, true);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            openBrowserButton.setSelection(
                    configuration.getAttribute(
                            IKarafLaunchDefinition.ATTR_OPEN_BROWSER,
                            true));

            urlText.setText(
                    configuration.getAttribute(
                            IKarafLaunchDefinition.ATTR_START_URL,
                            IKarafLaunchDefinition.S_DEFAULT_START_URL));

            startupUrlText.setText(
                    configuration.getAttribute(
                            IKarafLaunchDefinition.ATTR_START_ENTRY,
                            IKarafLaunchDefinition.S_DEFAULT_START_ENTRY));

            boolean external = configuration.getAttribute(
                    IKarafLaunchDefinition.ATTR_EXTERNAL_BROWSER, true);

            externalBrowserButton.setSelection(external);
            internalBrowserButton.setSelection(!external);

        } catch (CoreException e) {
            openBrowserButton.setSelection(true);
            urlText.setText(
                    IKarafLaunchDefinition.S_DEFAULT_START_URL);
            startupUrlText.setText(
                    IKarafLaunchDefinition.S_DEFAULT_START_ENTRY);
            externalBrowserButton.setSelection(true);
            internalBrowserButton.setSelection(false);
        }

        updateEnabledState();
    }

    @Override
    public void performApply(
            ILaunchConfigurationWorkingCopy configuration) {

        configuration.setAttribute(
                IKarafLaunchDefinition.ATTR_OPEN_BROWSER,
                openBrowserButton.getSelection());

        configuration.setAttribute(
                IKarafLaunchDefinition.ATTR_START_URL,
                urlText.getText().trim());

        configuration.setAttribute(
                IKarafLaunchDefinition.ATTR_START_ENTRY,
                startupUrlText.getText().trim());

        configuration.setAttribute(
                IKarafLaunchDefinition.ATTR_EXTERNAL_BROWSER,
                externalBrowserButton.getSelection());
    }

    @Override
    public boolean isValid(ILaunchConfiguration configuration) {
        setErrorMessage(null);

        if (!openBrowserButton.getSelection()) {
            return true;
        }

        try {
            StringBuilder builder = new StringBuilder();
            builder.append( urlText.getText().trim());
            builder.append(startupUrlText.getText().trim());
        	URI uri = URI.create(builder.toString());
            String scheme = uri.getScheme();

            if (!uri.isAbsolute()
                    || uri.getHost() == null
                    || (!"http".equalsIgnoreCase(scheme)
                        && !"https".equalsIgnoreCase(scheme))) {

                setErrorMessage(
                        "Enter a valid HTTP or HTTPS start URL.");
                return false;
            }

        } catch (IllegalArgumentException e) {
            setErrorMessage("Invalid application URL.");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "Browser";
    }
}
