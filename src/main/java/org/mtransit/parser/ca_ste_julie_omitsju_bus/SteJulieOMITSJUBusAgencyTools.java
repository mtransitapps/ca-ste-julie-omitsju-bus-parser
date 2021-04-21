package org.mtransit.parser.ca_ste_julie_omitsju_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.RegexUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mtransit.parser.Constants.SPACE_;
import static org.mtransit.parser.StringUtils.EMPTY;

// https://exo.quebec/en/about/open-data
// https://exo.quebec/xdata/omitsju/google_transit.zip
public class SteJulieOMITSJUBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new SteJulieOMITSJUBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "exo Ste-Julie";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	private static final long RID_STARTS_WITH_T = 20_000L;

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		if (CharUtils.isDigitsOnly(gRoute.getRouteShortName())) {
			return Long.parseLong(gRoute.getRouteShortName());
		}
		//noinspection deprecation
		final Matcher matcher = DIGITS.matcher(gRoute.getRouteId());
		if (matcher.find()) {
			final long id = Long.parseLong(matcher.group());
			if (gRoute.getRouteShortName().toLowerCase(Locale.ENGLISH).startsWith("t")) {
				return id + RID_STARTS_WITH_T;
			}
		}
		throw new MTLog.Fatal("Unexpected route ID for %s!", gRoute);
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		routeLongName = CleanUtils.cleanBounds(Locale.FRENCH, routeLongName);
		routeLongName = CleanUtils.cleanStreetTypesFRCA(routeLongName);
		return CleanUtils.cleanLabelFR(routeLongName);
	}

	private static final String AGENCY_COLOR = "1F1F1F"; // DARK GRAY (from GTFS)

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Nullable
	@Override
	public String getRouteColor(@NotNull GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			if ("1".equals(gRoute.getRouteShortName())) return "7E4A94";
			if ("3".equals(gRoute.getRouteShortName())) return "41A62B";
			if ("4".equals(gRoute.getRouteShortName())) return "EAC800";
			if ("5".equals(gRoute.getRouteShortName())) return "E2007A";
			if ("6".equals(gRoute.getRouteShortName())) return "B49C4C";
			if ("7".equals(gRoute.getRouteShortName())) return "60BDDC";
			if ("8".equals(gRoute.getRouteShortName())) return "F39400";
			if ("T1".equals(gRoute.getRouteShortName())) return "E53834";
			if ("T2".equals(gRoute.getRouteShortName())) return "007C79";
			if ("T3".equals(gRoute.getRouteShortName())) return "79421E";
			if ("325".equals(gRoute.getRouteShortName())) return "57585A";
			if ("330".equals(gRoute.getRouteShortName())) return "57585A";
			if ("340".equals(gRoute.getRouteShortName())) return "57585A";
			if ("350".equals(gRoute.getRouteShortName())) return "57585A";
			if ("600".equals(gRoute.getRouteShortName())) return "9C9E9F";
			throw new MTLog.Fatal("Unexpected route color for %s!", gRoute);
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern DIRECTION = Pattern.compile("(direction )", Pattern.CASE_INSENSITIVE);

	private static final Pattern SERVICE_LOCAL = Pattern.compile("(service local)", Pattern.CASE_INSENSITIVE);

	private static final Pattern MONT_SAINT_ = Pattern.compile("(mont-saint-|mont-st-)", Pattern.CASE_INSENSITIVE);
	private static final String MONT_SAINT_REPLACEMENT = "St-";

	private static final Pattern EXPRESS_ = CleanUtils.cleanWordsFR("express");

	private static final Pattern _DASH_ = Pattern.compile("( - )");
	private static final String _DASH_REPLACEMENT = "<>"; // form<>to

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = _DASH_.matcher(tripHeadsign).replaceAll(_DASH_REPLACEMENT); // from - to => form<>to
		tripHeadsign = EXPRESS_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = DEVANT_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = MONT_SAINT_.matcher(tripHeadsign).replaceAll(MONT_SAINT_REPLACEMENT);
		tripHeadsign = DIRECTION.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = SERVICE_LOCAL.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.cleanBounds(Locale.FRENCH, tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern START_WITH_FACE_A = Pattern.compile("^(face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final Pattern START_WITH_FACE_AU = Pattern.compile("^(face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE = Pattern.compile("^(face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern SPACE_FACE_A = Pattern.compile("( face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final Pattern SPACE_WITH_FACE_AU = Pattern.compile("( face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE = Pattern.compile("( face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern[] START_WITH_FACES = new Pattern[]{START_WITH_FACE_A, START_WITH_FACE_AU, START_WITH_FACE};

	private static final Pattern[] SPACE_FACES = new Pattern[]{SPACE_FACE_A, SPACE_WITH_FACE_AU, SPACE_WITH_FACE};

	private static final Pattern DEVANT_ = CleanUtils.cleanWordsFR("devant");

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = _DASH_.matcher(gStopName).replaceAll(SPACE_);
		gStopName = DEVANT_.matcher(gStopName).replaceAll(EMPTY);
		gStopName = RegexUtils.replaceAllNN(gStopName, START_WITH_FACES, CleanUtils.SPACE);
		gStopName = RegexUtils.replaceAllNN(gStopName, SPACE_FACES, CleanUtils.SPACE);
		gStopName = CleanUtils.cleanBounds(Locale.FRENCH, gStopName);
		gStopName = CleanUtils.cleanStreetTypesFRCA(gStopName);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	private static final String ZERO = "0";

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		if (ZERO.equals(gStop.getStopCode())) {
			return EMPTY;
		}
		return super.getStopCode(gStop);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public int getStopId(@NotNull GStop gStop) {
		final String stopCode = getStopCode(gStop);
		if (stopCode.length() > 0 && CharUtils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		//noinspection deprecation
		final String stopId1 = gStop.getStopId();
		final Matcher matcher = DIGITS.matcher(stopId1);
		if (matcher.find()) {
			final int digits = Integer.parseInt(matcher.group());
			int stopId;
			if (stopId1.startsWith("SJU")) {
				stopId = 0;
			} else if (stopId1.startsWith("LON")) {
				stopId = 1_000_000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (start with)! %s", gStop);
			}
			if (stopId1.endsWith("A")) {
				stopId += 100_000;
			} else if (stopId1.endsWith("B")) {
				stopId += 200_000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (end with)! %s!", gStop);
			}
			return stopId + digits;
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop);
	}
}
