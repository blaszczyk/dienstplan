package muettinghoven.dienstplan.util;

import bn.blaszczyk.rosecommon.tools.Preference;
import static bn.blaszczyk.rosecommon.tools.Preference.Type.*;

public enum DienstplanPreference implements Preference
{
	UPDATE_TIME_INTERVAL(INT,"update_time_interval",86400000), // milliseconds
	UPDATE_LOOK_AHEAD_TIME(INT,"update_look_ahead_time",90); //days

	private final Type type;
	private final String key;
	private final Object defaultValue;
	private final boolean needsCaching;

	private DienstplanPreference(final Type type, final String key, final Object defaultValue, final boolean needsCaching)
	{
		if(defaultValue != null && !type.getType().isInstance(defaultValue))
			throw new IllegalArgumentException("preference " + key + "of type " + type + " has false default value class: " + defaultValue.getClass());
		this.type = type;
		this.key = key;
		this.defaultValue = defaultValue;
		this.needsCaching = needsCaching;
	}

	private DienstplanPreference(final Type type, final String key, final Object defaultValue)
	{
		this(type, key, defaultValue, false);
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public Object getDefaultValue()
	{
		return defaultValue;
	}
	
	@Override
	public boolean needsCaching()
	{
		return needsCaching;
	}

}
