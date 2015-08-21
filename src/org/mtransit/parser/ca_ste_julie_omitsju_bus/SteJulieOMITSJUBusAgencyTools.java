package org.mtransit.parser.ca_ste_julie_omitsju_bus;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.mt.data.MTrip;

// https://www.amt.qc.ca/en/about/open-data
// http://www.amt.qc.ca/xdata/omitsju/google_transit.zip
public class SteJulieOMITSJUBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-ste-julie-omitsju-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new SteJulieOMITSJUBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("\nGenerating OMITSJU bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("\nGenerating OMITSJU bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongName();
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		routeLongName = SECTEUR.matcher(routeLongName).replaceAll(SECTEUR_REPLACEMENT);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR = "649039";

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_7C51A1 = "7C51A1";
	private static final String COLOR_AC9349 = "AC9349";
	private static final String COLOR_38BABE = "38BABE";
	private static final String COLOR_3AB54A = "3AB54A";
	private static final String COLOR_57585A = "57585A";
	private static final String COLOR_F7931D = "F7931D";
	private static final String COLOR_E6C303 = "E6C303";
	private static final String COLOR_00A4D5 = "00A4D5";
	private static final String COLOR_ED028C = "ED028C";
	private static final String COLOR_EE423C = "EE423C";
	private static final String COLOR_008784 = "008784";
	private static final String COLOR_764526 = "764526";

	private static final String RSN_100 = "100";
	private static final String RSN_200 = "200";
	private static final String RSN_220 = "220";
	private static final String RSN_250 = "250";
	private static final String RSN_325 = "325";
	private static final String RSN_330 = "330";
	private static final String RSN_340 = "340";
	private static final String RSN_350 = "350";
	private static final String RSN_450 = "450";
	private static final String RSN_500 = "500";
	private static final String RSN_600 = "600";
	private static final String RSN_800 = "800";
	private static final String RSN_T110 = "T110";
	private static final String RSN_T120 = "T120";
	private static final String RSN_T510 = "T510";

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (RSN_100.equals(gRoute.getRouteShortName())) return COLOR_7C51A1;
		if (RSN_200.equals(gRoute.getRouteShortName())) return COLOR_AC9349;
		if (RSN_220.equals(gRoute.getRouteShortName())) return COLOR_38BABE;
		if (RSN_250.equals(gRoute.getRouteShortName())) return COLOR_3AB54A;
		if (RSN_325.equals(gRoute.getRouteShortName())) return COLOR_57585A;
		if (RSN_330.equals(gRoute.getRouteShortName())) return COLOR_57585A;
		if (RSN_340.equals(gRoute.getRouteShortName())) return COLOR_57585A;
		if (RSN_350.equals(gRoute.getRouteShortName())) return COLOR_57585A;
		if (RSN_450.equals(gRoute.getRouteShortName())) return COLOR_F7931D;
		if (RSN_500.equals(gRoute.getRouteShortName())) return COLOR_E6C303;
		if (RSN_600.equals(gRoute.getRouteShortName())) return COLOR_00A4D5;
		if (RSN_800.equals(gRoute.getRouteShortName())) return COLOR_ED028C;
		if (RSN_T110.equals(gRoute.getRouteShortName())) return COLOR_EE423C;
		if (RSN_T120.equals(gRoute.getRouteShortName())) return COLOR_008784;
		if (RSN_T510.equals(gRoute.getRouteShortName())) return COLOR_764526;
		System.out.printf("\nUnexpected route color %s", gRoute);
		System.exit(-1);
		return null;
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
	}

	private static final Pattern DIRECTION = Pattern.compile("(direction )", Pattern.CASE_INSENSITIVE);
	private static final String DIRECTION_REPLACEMENT = "";

	private static final Pattern SECTEUR = Pattern.compile("(secteur[s]? )", Pattern.CASE_INSENSITIVE);
	private static final String SECTEUR_REPLACEMENT = "";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = DIRECTION.matcher(tripHeadsign).replaceAll(DIRECTION_REPLACEMENT);
		tripHeadsign = SECTEUR.matcher(tripHeadsign).replaceAll(SECTEUR_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern START_WITH_FACE_A = Pattern.compile("^(face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE_AU = Pattern.compile("^(face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE = Pattern.compile("^(face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern SPACE_FACE_A = Pattern.compile("( face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE_AU = Pattern.compile("( face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE = Pattern.compile("( face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern[] START_WITH_FACES = new Pattern[] { START_WITH_FACE_A, START_WITH_FACE_AU, START_WITH_FACE };

	private static final Pattern[] SPACE_FACES = new Pattern[] { SPACE_FACE_A, SPACE_WITH_FACE_AU, SPACE_WITH_FACE };

	private static final Pattern AVENUE = Pattern.compile("( avenue)", Pattern.CASE_INSENSITIVE);
	private static final String AVENUE_REPLACEMENT = " av.";

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = AVENUE.matcher(gStopName).replaceAll(AVENUE_REPLACEMENT);
		gStopName = Utils.replaceAll(gStopName, START_WITH_FACES, CleanUtils.SPACE);
		gStopName = Utils.replaceAll(gStopName, SPACE_FACES, CleanUtils.SPACE);
		gStopName = CleanUtils.cleanStreetTypesFRCA(gStopName);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	private static final String ZERO = "0";

	@Override
	public String getStopCode(GStop gStop) {
		if (ZERO.equals(gStop.getStopCode())) {
			return null;
		}
		return super.getStopCode(gStop);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public int getStopId(GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode != null && stopCode.length() > 0) {
			return Integer.valueOf(stopCode); // using stop code as stop ID
		}
		// generating integer stop ID
		Matcher matcher = DIGITS.matcher(gStop.getStopId());
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		int stopId;
		System.out.println("Stop doesn't have an ID (start with)! " + gStop);
		System.exit(-1);
		stopId = -1;
		System.out.println("Stop doesn't have an ID (end with)! " + gStop);
		System.exit(-1);
		return stopId + digits;
	}
}
