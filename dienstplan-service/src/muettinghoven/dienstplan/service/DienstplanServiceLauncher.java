package muettinghoven.dienstplan.service;

import org.apache.logging.log4j.LogManager;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rosecommon.tools.CommonPreference;
import bn.blaszczyk.rosecommon.tools.LoggerConfigurator;
import bn.blaszczyk.rosecommon.tools.Preferences;
import bn.blaszczyk.rosecommon.tools.TypeManager;
import bn.blaszczyk.roseservice.Launcher;

public class DienstplanServiceLauncher extends Launcher {
	
	private static final String ROSE_FILE = "muettinghoven/dienstplan/resources/dienstplan-common.rose";
	
	public static void main(String[] args)
	{
		try
		{
			Preferences.setMainClass(DienstplanServiceLauncher.class);
			TypeManager.parseRoseFile(ROSE_FILE);
			LoggerConfigurator.configureLogger(CommonPreference.BASE_DIRECTORY, CommonPreference.LOG_LEVEL);
			new DienstplanServiceLauncher().launch();
		}
		catch (RoseException e)
		{
			LogManager.getLogger(DienstplanServiceLauncher.class).error("Error launching dienstplan service", e);
		}
	}
	
	
	@Override
	protected void registerEndpoints()
	{
		super.registerEndpoints();
		final DienstplanEndpoint endpoint = new DienstplanEndpoint(getController());
		getServer().getHandler().registerEndpoint("dienstplan", endpoint);
	}

}
