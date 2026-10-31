
package org.condast.commons.tools.karaf.launch;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.statushandlers.StatusManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

public class KarafLaunchDelegate extends LaunchConfigurationDelegate {

    @Override
    public void launch(
            ILaunchConfiguration configuration,
            String mode,
            ILaunch launch,
            IProgressMonitor monitor) throws CoreException {

        String karafHome = configuration.getAttribute(
                IKarafLaunchDefinition.ATTR_KARAF_HOME, "");

        karafHome = VariablesPlugin.getDefault()
                .getStringVariableManager()
                .performStringSubstitution(karafHome);

        File home = new File(karafHome);
        File script = new File(home, "bin/karaf.bat");

        if (!home.isDirectory() || !script.isFile()) {
            throw failure(
            		IKarafLaunchDefinition.S_ERR_INVALID_HOME, null);
        }

        if (monitor.isCanceled()) {
            return;
        }

        try {
        	List<String> command = new ArrayList<>();

        	command.add(script.toString());
        	command.add("server");

        	if (ILaunchManager.DEBUG_MODE.equals(mode))
        	    command.add("debug");
        	
        	ProcessBuilder builder = new ProcessBuilder(command);
        	
            builder.directory(home);
            builder.redirectErrorStream(true);

            Process process = builder.start();

            DebugPlugin.newProcess(
                    launch,
                    process,
                    "Apache Karaf - " + home.getName());

            if (configuration.getAttribute(
                    IKarafLaunchDefinition.ATTR_OPEN_BROWSER, true)) {

                String startUrl = configuration.getAttribute(
                        IKarafLaunchDefinition.ATTR_START_URL,
                        IKarafLaunchDefinition.S_DEFAULT_START_URL);

                String entry = configuration.getAttribute(
                        IKarafLaunchDefinition.ATTR_START_ENTRY,
                        IKarafLaunchDefinition.S_DEFAULT_START_ENTRY);

                boolean external = configuration.getAttribute(
                        IKarafLaunchDefinition.ATTR_EXTERNAL_BROWSER, true);

                openBrowser(startUrl, entry, external);
            }
        } catch (IOException e) {
        	throw failure("Unable to start Apache Karaf.", e);
        }
    }


    private static void openBrowser(String startUrl, String entry, boolean external) {
    	Display.getDefault().asyncExec(() -> {
    		try {
                StringBuilder builder = new StringBuilder();
                builder.append( startUrl );
                builder.append( entry );
            	URL url = URI.create(builder.toString()).toURL();

    			IWorkbenchBrowserSupport support =
    					PlatformUI.getWorkbench().getBrowserSupport();

    			if (external) {
    				support.getExternalBrowser().openURL(url);
    			} else {
    				support.createBrowser(
    						IWorkbenchBrowserSupport.AS_EDITOR
    						| IWorkbenchBrowserSupport.LOCATION_BAR
    						| IWorkbenchBrowserSupport.NAVIGATION_BAR,
    						IKarafLaunchDefinition.PLUGIN_ID
    						+ ".browser",
    						"Karaf Application",
    						"Karaf Application Browser"
    						).openURL(url);
    			}

    		} catch (Exception e) {
    			StatusManager.getManager().handle(
    					new Status(
    							org.eclipse.core.runtime.IStatus.ERROR,
    							IKarafLaunchDefinition.PLUGIN_ID,
    							"Unable to open the Karaf application URL.",
    							e),
    					StatusManager.LOG | StatusManager.SHOW);
    		}
    	});
    }


    private static CoreException failure(
            String message, Throwable cause) {

        return new CoreException(
                new Status(
                        org.eclipse.core.runtime.IStatus.ERROR,
                        IKarafLaunchDefinition.PLUGIN_ID,
                        message,
                        cause));
    }
}
