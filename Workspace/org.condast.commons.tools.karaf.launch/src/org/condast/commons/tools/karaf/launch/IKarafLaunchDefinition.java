package org.condast.commons.tools.karaf.launch;

public interface IKarafLaunchDefinition {

	public static final String PLUGIN_ID =
            "org.condast.tools.karaf.launch";

    public static final String ATTR_KARAF_HOME =
            PLUGIN_ID + ".karafHome";

    public static final String S_INFO_LAUNCH =
            "Launching Apache Karaf";

    public static final String S_ERR_INVALID_HOME =
            "Invalid Apache Karaf installation directory";
    

    static final String ATTR_OPEN_BROWSER = PLUGIN_ID + ".openBrowser";
    static final String ATTR_START_URL = PLUGIN_ID + ".startUrl";
    static final String ATTR_START_ENTRY = PLUGIN_ID + ".entry";
    static final String ATTR_EXTERNAL_BROWSER = PLUGIN_ID + ".externalBrowser";

    static final String S_DEFAULT_START_URL = "http://localhost:8181/";

    static final String S_DEFAULT_START_ENTRY = "home";

}
