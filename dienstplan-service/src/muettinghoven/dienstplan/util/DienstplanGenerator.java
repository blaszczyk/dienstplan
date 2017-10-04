package muettinghoven.dienstplan.util;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rosecommon.controller.ModelController;
import muettinghoven.dienstplan.model.*;

public class DienstplanGenerator
{
	private static final Comparator<Zeitraum> BY_ANFANGSDATUM = (z1,z2) -> z1.getAnfangsdatum().compareTo(z2.getAnfangsdatum());
	
	private static final Calendar CALENDAR = GregorianCalendar.getInstance(Locale.GERMANY);
	
	public static void generiereBis(final Date endDatum, final Dienstplan plan, final ModelController controller) throws RoseException
	{
		final Set<Zeiteinheit> zeiteinheiten = plan.getDiensts()
				.stream()
				.map(Dienst::getZeiteinheit)
				.collect(Collectors.toSet());
		for(final Zeiteinheit einheit : zeiteinheiten)
		{
			final Optional<Zeitraum> optZeitraum = plan.getZeitraums()
					.stream()
					.filter(z -> z.getZeiteinheit().equals(einheit))
					.max(BY_ANFANGSDATUM);
			if(optZeitraum.isPresent())
			{
				Date datum = optZeitraum.get().getAnfangsdatum();
				while(datum.before(endDatum))
				{
					datum = nextDatum(datum, einheit);
					addZeitraum(datum, plan, controller, einheit);
				}
			}
			else
				addZeitraum(endDatum, plan, controller, einheit);
		}
	}

	private static void addZeitraum(final Date datum, final Dienstplan plan, final ModelController controller,	final Zeiteinheit einheit) throws RoseException
	{
		final Zeitraum jetzt = controller.createNew(Zeitraum.class);
		jetzt.setAnfangsdatum(datum);
		jetzt.setZeiteinheit(einheit);
		jetzt.setEntity(Zeitraum.DIENSTPLAN, plan);
		controller.update(jetzt, plan);
	}
	
	private static Date nextDatum(final Date datum, final Zeiteinheit einheit)
	{
		CALENDAR.setTime(datum);
		switch(einheit)
		{
		case TAG:
			CALENDAR.add(Calendar.DAY_OF_YEAR, 1);
			break;
		case WOCHE:
			CALENDAR.add(Calendar.DAY_OF_YEAR, 7);
			break;
		case MONAT:
			CALENDAR.add(Calendar.MONTH, 1);
			break;
		}
		return CALENDAR.getTime();
	}

	public static int jetztId(final Dienstplan plan, final Zeiteinheit einheit) throws RoseException
	{
		final Date now = new Date();
		final Optional<Zeitraum> optZeitraum = plan.getZeitraums()
				.stream()
				.filter(z -> z.getZeiteinheit().equals(einheit))
				.filter(z -> z.getAnfangsdatum().before(now))
				.max(BY_ANFANGSDATUM);
		if(!optZeitraum.isPresent())
			throw new RoseException("kein zeitraum gefunden");
		return optZeitraum.get().getId();
	}

}
