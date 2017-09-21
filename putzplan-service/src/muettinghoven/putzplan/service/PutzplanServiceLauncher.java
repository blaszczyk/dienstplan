package muettinghoven.putzplan.service;

import org.apache.logging.log4j.LogManager;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rosecommon.tools.CommonPreference;
import bn.blaszczyk.rosecommon.tools.LoggerConfigurator;
import bn.blaszczyk.rosecommon.tools.Preferences;
import bn.blaszczyk.rosecommon.tools.TypeManager;
import bn.blaszczyk.roseservice.Launcher;

public class PutzplanServiceLauncher extends Launcher {
	
	private static final String ROSE_FILE = "muettinghoven/putzplan/resources/putzplan.rose";
	
	public static void main(String[] args)
	{
		try
		{
			Preferences.setMainClass(PutzplanServiceLauncher.class);
			TypeManager.parseRoseFile(PutzplanServiceLauncher.class.getClassLoader().getResourceAsStream(ROSE_FILE));
			LoggerConfigurator.configureLogger(CommonPreference.BASE_DIRECTORY, CommonPreference.LOG_LEVEL);
			new PutzplanServiceLauncher().launch();
		}
		catch (RoseException e)
		{
			LogManager.getLogger(PutzplanServiceLauncher.class).error("Error launching putzplan service", e);
		}
	}
	
	
	@Override
	protected void registerEndpoints()
	{
		super.registerEndpoints();
		final PutzplanEndpoint endpoint = new PutzplanEndpoint(getController());
		getServer().getHandler().registerEndpoint("putzplan", endpoint);
	}

}
