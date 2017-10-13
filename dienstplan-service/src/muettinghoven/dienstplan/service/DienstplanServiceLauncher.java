package muettinghoven.dienstplan.service;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rosecommon.tools.CommonPreference;
import bn.blaszczyk.rosecommon.tools.LoggerConfigurator;
import bn.blaszczyk.rosecommon.tools.Preferences;
import bn.blaszczyk.rosecommon.tools.TypeManager;
import bn.blaszczyk.roseservice.Launcher;
import muettinghoven.dienstplan.model.Dienstplan;
import muettinghoven.dienstplan.util.DienstplanGenerator;
import muettinghoven.dienstplan.util.DienstplanPreference;

public class DienstplanServiceLauncher extends Launcher {
	
	private static final String ROSE_FILE = "muettinghoven/dienstplan/resources/dienstplan-common.rose";
	
	private static final long MILLIS_PER_DAY = 24L * 60L * 60L * 1000L;
	
	private UpdateThread updateThread;
	
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
	public void launch() throws RoseException {
		super.launch();
		updateThread = new UpdateThread();
		updateThread.start();
	}
	
	@Override
	public void stop() {
		if(updateThread != null)
			updateThread.interrupt();
		updateThread = null;
		super.stop();
	}

	@Override
	protected void registerEndpoints()
	{
		super.registerEndpoints();
//		final DienstplanEndpoint endpoint = new DienstplanEndpoint(getController());
//		getServer().getHandler().registerEndpoint("dienstplan", endpoint);
	}
	
	private void updateDienstplaene() throws RoseException
	{
		final List<Dienstplan> plaene = getController().getEntities(Dienstplan.class);
		final long look_ahead_time = Preferences.getIntegerValue(DienstplanPreference.UPDATE_LOOK_AHEAD_TIME);
		final Date look_ahead_date = new Date(System.currentTimeMillis() + look_ahead_time * MILLIS_PER_DAY);
		for(final Dienstplan plan : plaene)
		{
			final DienstplanGenerator generator = new DienstplanGenerator(getController(), plan);
			generator.generiereBis(look_ahead_date);
		}
	}
	
	private final class UpdateThread extends Thread
	{
		public UpdateThread()
		{
			super("update-dienstplaene-thread");
		}
		
		@Override
		public void run()
		{
			while(true)
			{
				try
				{
					updateDienstplaene();
				}
				catch (Exception e)
				{
					LogManager.getLogger(DienstplanServiceLauncher.class).error("error updating dienstplaene",e);
				}
				final int updateTime = Preferences.getIntegerValue(DienstplanPreference.UPDATE_TIME_INTERVAL);
				try
				{
					Thread.sleep(updateTime);
				}
				catch (Exception e)
				{
					LogManager.getLogger(DienstplanServiceLauncher.class).warn("update thread interrupted. stopping update thread.");
					return;
				}
			}
		}
	}

}
