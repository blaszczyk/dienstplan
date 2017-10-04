package muettinghoven.dienstplan.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rosecommon.controller.ModelController;
import bn.blaszczyk.roseservice.server.Endpoint;
import muettinghoven.dienstplan.model.*;
import muettinghoven.dienstplan.util.DienstplanGenerator;

public class DienstplanEndpoint implements Endpoint
{
	private final ModelController controller;
	
	public DienstplanEndpoint(final ModelController controller)
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
			
			final int planid = Integer.parseInt(split[0]);
			final Dienstplan plan = controller.getEntityById(Dienstplan.class, planid);
			final Zeiteinheit zeittyp = Zeiteinheit.valueOf(split[1]);
			if(plan == null || zeittyp == null)
				return HttpServletResponse.SC_NOT_FOUND;
			
			DienstplanGenerator.generiereBis(new Date(), plan, controller);
			final int jetztId = DienstplanGenerator.jetztId(plan, zeittyp);
			response.getWriter().write(jetztId);
			return HttpServletResponse.SC_OK;
		}
		catch (final Exception e) 
		{
			throw RoseException.wrap(e, "Error GET@/dienstplan");
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
			throw RoseException.wrap(e, "Error POST@/dienstplan");
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

}
