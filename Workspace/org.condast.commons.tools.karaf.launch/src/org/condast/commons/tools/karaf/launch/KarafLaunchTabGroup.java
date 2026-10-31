
package org.condast.commons.tools.karaf.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class KarafLaunchTabGroup
        extends AbstractLaunchConfigurationTabGroup {

    @Override
    public void createTabs(
            ILaunchConfigurationDialog dialog,
            String mode) {

        setTabs(new ILaunchConfigurationTab[] {
                new KarafMainTab(),
                new KarafBrowserTab(),
                new CommonTab()
        });
    }
}
