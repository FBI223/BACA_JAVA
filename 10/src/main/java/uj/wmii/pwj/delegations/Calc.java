//Marcin Sztukowski

package uj.wmii.pwj.delegations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Calc {

    BigDecimal calculate(String name, String start, String end, BigDecimal dailyRate) throws IllegalArgumentException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
        ZonedDateTime startTime = ZonedDateTime.parse(start, formatter);
        ZonedDateTime endTime = ZonedDateTime.parse(end, formatter);

        Duration duration = Duration.between(startTime.toInstant(), endTime.toInstant());

        long totalMinutes = duration.toMinutes();
        long fullDays = totalMinutes / (24 * 60);
        long remainingMinutes = totalMinutes % (24 * 60);

        BigDecimal paycheck = BigDecimal.ZERO;
        paycheck = paycheck.add(dailyRate.multiply(BigDecimal.valueOf(fullDays)));


        if (remainingMinutes > 0) {
            BigDecimal partialRate;
            if (remainingMinutes <= 8 * 60) {
                partialRate = dailyRate.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
            } else if (remainingMinutes <= 12 * 60) {
                partialRate = dailyRate.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            } else {
                partialRate = dailyRate;
            }
            paycheck = paycheck.add(partialRate);
        }

        return paycheck.setScale(2, RoundingMode.HALF_UP);
    }
}
