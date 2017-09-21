package muettinghoven.putzplan.util;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Optional;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rosecommon.controller.ModelController;
import muettinghoven.putzplan.model.Putzplan;
import muettinghoven.putzplan.model.Zeiteinheit;
import muettinghoven.putzplan.model.Zeitraum;

public class PutzplanGenerator
{
	private static final Comparator<Zeitraum> BY_ANFANGSDATUM = (z1,z2) -> z1.getAnfangsdatum().compareTo(z2.getAnfangsdatum());
	
	private static final Calendar CALENDAR = GregorianCalendar.getInstance(Locale.GERMANY);
	
	public static void generiereBis(final Date endDatum, final Putzplan putzplan, final ModelController controller) throws RoseException
	{
		for(final Zeiteinheit einheit : Zeiteinheit.values())
		{
			final Optional<Zeitraum> optZeitraum = putzplan.getZeitraums()
					.stream()
					.filter(z -> z.getZeiteinheit().equals(einheit))
					.max(BY_ANFANGSDATUM);
			if(optZeitraum.isPresent())
			{
				Date datum = optZeitraum.get().getAnfangsdatum();
				while(datum.before(endDatum))
				{
					datum = nextDatum(datum, einheit);
					addZeitraum(datum, putzplan, controller, einheit);
				}
			}
			else
				addZeitraum(endDatum, putzplan, controller, einheit);
		}
	}

	private static void addZeitraum(final Date datum, final Putzplan putzplan, final ModelController controller,	final Zeiteinheit einheit) throws RoseException
	{
		final Zeitraum jetzt = controller.createNew(Zeitraum.class);
		jetzt.setAnfangsdatum(datum);
		jetzt.setZeiteinheit(einheit);
		jetzt.setEntity(Zeitraum.PUTZPLAN, putzplan);
		controller.update(jetzt, putzplan);
	}
	
	private static Date nextDatum(final Date datum, final Zeiteinheit einheit)
	{
		CALENDAR.setTime(datum);
		switch(einheit)
		{
		case WOCHE:
			CALENDAR.add(Calendar.DAY_OF_YEAR, 7);
			break;
		case MONAT:
			CALENDAR.add(Calendar.MONTH, 1);
			break;
		}
		return CALENDAR.getTime();
	}

	public static int jetztId(final Putzplan putzplan, final Zeiteinheit einheit) throws RoseException
	{
		final Date now = new Date();
		final Optional<Zeitraum> optZeitraum = putzplan.getZeitraums()
				.stream()
				.filter(z -> z.getZeiteinheit().equals(einheit))
				.filter(z -> z.getAnfangsdatum().before(now))
				.max(BY_ANFANGSDATUM);
		if(!optZeitraum.isPresent())
			throw new RoseException("kein zeitraum gefunden");
		return optZeitraum.get().getId();
	}

}
