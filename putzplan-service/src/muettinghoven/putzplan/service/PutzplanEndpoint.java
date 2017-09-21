package muettinghoven.putzplan.service;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rosecommon.controller.ModelController;
import bn.blaszczyk.roseservice.server.Endpoint;
import muettinghoven.putzplan.model.Putzplan;
import muettinghoven.putzplan.model.Zeiteinheit;
import muettinghoven.putzplan.util.PutzplanGenerator;

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
			if(split.length != 2)
				return HttpServletResponse.SC_BAD_REQUEST;
			
			final int putzplanid = Integer.parseInt(split[0]);
			final Putzplan putzplan = controller.getEntityById(Putzplan.class, putzplanid);
			final Zeiteinheit zeittyp = Zeiteinheit.valueOf(split[1]);
			if(putzplan == null || zeittyp == null)
				return HttpServletResponse.SC_NOT_FOUND;
			
			PutzplanGenerator.generiereBis(new Date(), putzplan, controller);
			final int jetztId = PutzplanGenerator.jetztId(putzplan, zeittyp);
			response.getWriter().write(jetztId);
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
