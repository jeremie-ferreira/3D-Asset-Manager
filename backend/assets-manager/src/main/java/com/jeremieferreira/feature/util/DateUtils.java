package com.jeremieferreira.feature.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class DateUtils {
	public static final SimpleDateFormat DATE_CLASSIC_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
	public static final long SECOND = 1000l;
	public static final long MINUTE = 60000l;
	public static final long HOUR = 3600000l;

	public static Long toLong(LocalDateTime localDateTime) {
		return Optional.ofNullable(localDateTime).map(Timestamp::valueOf).map(Timestamp::getTime).orElse(null);
	}

	public static LocalDateTime toLocalDateTime(Long timestamp) {
		return Optional.ofNullable(timestamp).map(Timestamp::new).map(Timestamp::toLocalDateTime).orElse(null);
	}

	public static LocalDateTime getCurrentMonth() {
		return LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
	}
	
	public static LocalDateTime truncateToMonth(LocalDateTime date) {
		return date.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
	}
	
	/**
	 * Get the intersection of two intervals
	 * 
	 * @param firstIntervalStart
	 * @param firstIntervalEnd
	 * @param secondIntervalStart
	 * @param secondIntervalEnd
	 * @return the duration of the intersection
	 */
	public static Duration getIntersection(LocalDateTime firstIntervalStart, LocalDateTime firstIntervalEnd,
			LocalDateTime secondIntervalStart, LocalDateTime secondIntervalEnd) {
		LocalDateTime latestStart = firstIntervalStart.isAfter(secondIntervalStart) ? firstIntervalStart
				: secondIntervalStart;
		LocalDateTime earliestEnd = firstIntervalEnd.isBefore(secondIntervalEnd) ? firstIntervalEnd : secondIntervalEnd;

		Duration duration = Duration.between(latestStart, earliestEnd);

		if (duration.isNegative()) {
			return Duration.ZERO;
		}
		return duration;
	}
	
	public static Duration getIntersection(Long firstIntervalStart, Long firstIntervalEnd,
			Long secondIntervalStart, Long secondIntervalEnd) {
		LocalDateTime fs = toLocalDateTime(firstIntervalStart);
		LocalDateTime fe = toLocalDateTime(firstIntervalEnd);
		LocalDateTime ss = toLocalDateTime(secondIntervalStart);
		LocalDateTime se = toLocalDateTime(secondIntervalEnd);
		return getIntersection(fs, fe, ss, se);
	}

	public static Long now() {
		return DateUtils.toLong(LocalDateTime.now());
	}
}
