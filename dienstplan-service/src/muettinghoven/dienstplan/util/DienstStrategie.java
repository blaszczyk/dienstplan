package muettinghoven.dienstplan.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import muettinghoven.dienstplan.model.Bewohner;

public abstract class DienstStrategie
{
	public static DienstStrategie parse(final String strategie)
	{
		if(strategie == null || strategie.isEmpty())
			return null;
		if(strategie.equals("all"))
			return all();
		if(strategie.equals("none"))
			return none();
		if(strategie.startsWith("only"))
			return only(parseInts(strategie.substring(4)));
		if(strategie.startsWith("not"))
			return not(parseInts(strategie.substring(3)));
		return null;
	}
	
	public abstract List<Bewohner> selection(final Collection<Bewohner> bewohner);
	
	private static Collection<Integer> parseInts(final String idsString)
	{
		return Arrays.stream(idsString.split("\\D+"))
				.filter(s -> !s.isEmpty())
				.map(Integer::parseInt)
				.collect(Collectors.toSet());
	}

	private static DienstStrategie none() {
		return new DienstStrategie() {
			public @Override
			List<Bewohner> selection(final Collection<Bewohner> bewohner) {
				return Collections.emptyList();
			}
		};
	}
	
	private static DienstStrategie all() {
		return new DienstStrategie() {			
			@Override
			public List<Bewohner> selection(final Collection<Bewohner> bewohner)
			{
				return bewohner.stream().collect(Collectors.toList());
			}
		};
	}
	
	private static DienstStrategie only(final Collection<Integer> ids) {
		return new DienstStrategie() {
			@Override
			public List<Bewohner> selection(Collection<Bewohner> bewohner) {
				return bewohner.stream().filter(b -> ids.contains(b.getId())).collect(Collectors.toList());
			}
		};
	}
	
	private static DienstStrategie not(final Collection<Integer> ids) {
		return new DienstStrategie() {
			@Override
			public List<Bewohner> selection(Collection<Bewohner> bewohner) {
				return bewohner.stream().filter(b -> !ids.contains(b.getId())).collect(Collectors.toList());
			}
		};
	}

}
