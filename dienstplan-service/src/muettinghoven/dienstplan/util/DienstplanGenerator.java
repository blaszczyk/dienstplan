package muettinghoven.dienstplan.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rosecommon.controller.ModelController;
import muettinghoven.dienstplan.model.*;

public class DienstplanGenerator
{
	private static final Comparator<Zeitraum> BY_ANFANGSDATUM = (z1,z2) -> z1.getAnfangsdatum().compareTo(z2.getAnfangsdatum());
	
	private static final Comparator<DienstAusfuehrung> AUSFUEHRUNG_BY_ANFANGSDATUM = (d1,d2) -> BY_ANFANGSDATUM.compare(d1.getZeitraum(), d2.getZeitraum());
	
	private final ModelController controller;
	
	private final Dienstplan plan;
	
	private final Set<Zeiteinheit> zeiteinheiten;
	
	public DienstplanGenerator(final ModelController controller, final Dienstplan plan)
	{
		this.controller = controller;
		this.plan = plan;
		zeiteinheiten = plan.getDiensts()
				.stream()
				.map(Dienst::getZeiteinheit)
				.collect(Collectors.toSet());
	}

	public void generiereBis(final Date endDatum) throws RoseException
	{
		for(final Zeiteinheit einheit : zeiteinheiten)
		{
			final Optional<Zeitraum> optZeitraum = plan.getZeitraums()
					.stream()
					.filter(z -> z.getZeiteinheit().equals(einheit))
					.max(BY_ANFANGSDATUM);
			Date datum ;
			if(optZeitraum.isPresent())
				datum = optZeitraum.get().getAnfangsdatum();
			else
			{
				datum = new Date();
				addZeitraum(datum, einheit);
			}
			while(datum.before(endDatum))
			{
				datum = nextDatum(datum, einheit);
				addZeitraum(datum, einheit);
			}
		}
		
		for(final Dienst dienst : plan.getDiensts())
		{
			final Optional<DienstAusfuehrung> optAusfuehrung = dienst.getDienstAusfuehrungs()
					.stream()
					.max(AUSFUEHRUNG_BY_ANFANGSDATUM);
			final DienstAusfuehrung ausfuehrung;
			if(optAusfuehrung.isPresent())
				ausfuehrung = optAusfuehrung.get();
			else
				ausfuehrung = createDienstAusfuehrung(dienst, firstZeitraum(dienst.getZeiteinheit()));
			Zeitraum zeitraum = next(ausfuehrung.getZeitraum());
			while(zeitraum != null)
			{
				final DienstAusfuehrung nextAusfuehrung = createDienstAusfuehrung(dienst, zeitraum);
				zeitraum = next(nextAusfuehrung.getZeitraum());
			}
		}
	}

	private DienstAusfuehrung createDienstAusfuehrung(final Dienst dienst, final Zeitraum zeitraum) throws RoseException
	{
		final DienstAusfuehrung ausfuehrung = controller.createNew(DienstAusfuehrung.class);
		final Bewohner bewohner = nextBewohner(dienst);
		ausfuehrung.setEntity(DienstAusfuehrung.DIENST, dienst);
		ausfuehrung.setEntity(DienstAusfuehrung.ZEITRAUM, zeitraum);
		ausfuehrung.setEntity(DienstAusfuehrung.BEWOHNER, bewohner);
		LogManager.getLogger(DienstplanGenerator.class).info("neue dienstausführung " + ausfuehrung);
		controller.update(ausfuehrung,dienst,bewohner,zeitraum);
		return ausfuehrung;
	}

	private Bewohner nextBewohner(final Dienst dienst)
	{
		final List<Bewohner> bewohner = new ArrayList<>(plan.getBewohners());
		if(bewohner.isEmpty())
			return null;
		DienstAusfuehrung ausfuehrung = dienst.getDienstAusfuehrungs()
				.stream()
				.max(AUSFUEHRUNG_BY_ANFANGSDATUM)
				.orElse(null);
		while(bewohner.size() > 1 && ausfuehrung != null)
		{
			bewohner.remove(ausfuehrung.getBewohner());
			ausfuehrung = previous(ausfuehrung);
		}
		final int index = (int)(Math.random() * bewohner.size());
		return bewohner.get(index);
	}

	private Zeitraum firstZeitraum(final Zeiteinheit zeiteinheit)
	{
		return plan.getZeitraums()
			.stream()
			.filter(z -> z.getZeiteinheit().equals(zeiteinheit))
			.min(BY_ANFANGSDATUM)
			.orElse(null);
	}

	private void addZeitraum(final Date datum, final Zeiteinheit einheit) throws RoseException
	{
		final Zeitraum zeitraum = controller.createNew(Zeitraum.class);
		zeitraum.setAnfangsdatum(datum);
		zeitraum.setZeiteinheit(einheit);
		zeitraum.setEntity(Zeitraum.DIENSTPLAN, plan);
		LogManager.getLogger(DienstplanGenerator.class).info("neuer zeitraum " + zeitraum + " in " + plan);
		controller.update(zeitraum, plan);
	}
	
	private static Date nextDatum(final Date datum, final Zeiteinheit einheit)
	{
		final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
		calendar.setTime(datum);
		switch(einheit)
		{
		case TAG:
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			break;
		case WOCHE:
			calendar.add(Calendar.DAY_OF_YEAR, 7);
			break;
		case MONAT:
			calendar.add(Calendar.MONTH, 1);
			break;
		}
		return calendar.getTime();
	}
	
	private static Zeitraum next(final Zeitraum thisOne)
	{
		return thisOne.getDienstplan()
			.getZeitraums()
			.stream()
			.filter(z -> thisOne.getZeiteinheit().equals(z.getZeiteinheit()))
			.filter(z -> z.getAnfangsdatum().after(thisOne.getAnfangsdatum()))
			.min(BY_ANFANGSDATUM)
			.orElse(null);
	}
	
	private static DienstAusfuehrung previous(final DienstAusfuehrung thisOne)
	{
		return thisOne.getDienst()
			.getDienstAusfuehrungs()
			.stream()
			.filter(a -> a.getZeitraum().getAnfangsdatum().before(thisOne.getZeitraum().getAnfangsdatum()))
			.max(AUSFUEHRUNG_BY_ANFANGSDATUM)
			.orElse(null);
	}

//	public int jetztId(final Zeiteinheit einheit)
//	{
//		final Date now = new Date();
//		final Optional<Zeitraum> optZeitraum = plan.getZeitraums()
//				.stream()
//				.filter(z -> z.getZeiteinheit().equals(einheit))
//				.filter(z -> z.getAnfangsdatum().before(now))
//				.max(BY_ANFANGSDATUM);
//		if(!optZeitraum.isPresent())
//			return -1;
//		return optZeitraum.get().getId();
//	}

}
