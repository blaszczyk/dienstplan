package muettinghoven.putzplan.service;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rosecommon.controller.ModelController;
import bn.blaszczyk.roseservice.server.Endpoint;

public class PutzplanEndpoint implements Endpoint
{
	
	private final ModelController controller;
	
	public PutzplanEndpoint(final ModelController controller)
	{
		this.controller = controller;
	}

	@Override
	public int get(final String path, final HttpServletRequest request, final HttpServletResponse response) throws RoseException
	{
		try
		{
			final String[] split = path.split("\\/");
			
			return HttpServletResponse.SC_OK;
		}
		catch (final Exception e) 
		{
			throw RoseException.wrap(e, "Error GET@/putzplan");
		}
	}

	@Override
	public int post(final String path, final HttpServletRequest request, final HttpServletResponse response) throws RoseException
	{
		try
		{
			final String[] split = path.split("\\/");
			
			return HttpServletResponse.SC_OK;
		}
		catch (final Exception e) 
		{
			throw RoseException.wrap(e, "Error POST@/putzplan");
		}
	}

	@Override
	public int put(final String path, final HttpServletRequest request, final HttpServletResponse response) throws RoseException
	{
		return HttpServletResponse.SC_NO_CONTENT;
	}

	@Override
	public int delete(final String path, final HttpServletRequest request, final HttpServletResponse response) throws RoseException
	{
		return HttpServletResponse.SC_NO_CONTENT;
	}

	@Override
	public Map<String, String> status()
	{
		return Collections.singletonMap("endpoint /putzplan", "active");
	}

}
