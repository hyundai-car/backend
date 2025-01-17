package com.myme.mycarforme.global.util.helper;

import com.myme.mycarforme.domains.car.domain.Car;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CalculateHelper {
    private static final int VERTEX_COUNT = 5;
    private static final double ANGLE = Math.toRadians(360.0 / VERTEX_COUNT);

    public static double calculatePentagonArea(double [] values) {
        validateInput(values);

        double area = 0.0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            int nexIndex = (i + 1) % VERTEX_COUNT;
            area += calculateTriangleArea(values[i], values[nexIndex]);
        }
        return area;
    }

    private static double calculateTriangleArea(double r1, double r2) {
        return 0.5 * r1 * r2 * Math.sin(ANGLE);
    }

    public static String calculateAverageDate(List<Car> cars) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        long averageDays = cars.stream()
                .map(Car::getInitialRegistration)
                .map(dateStr -> LocalDate.parse(dateStr, formatter))
                .mapToLong(date -> ChronoUnit.DAYS.between(CarScoreNormalizer.BASE_DATE, date))
                .sum() / cars.size();

        return CarScoreNormalizer.BASE_DATE.plusDays(averageDays).toString();
    }

    private static void validateInput(double[] values) {
        if (values == null || values.length != VERTEX_COUNT) {
            throw new IllegalArgumentException("정확히 5개의 값이 필요합니다.");
        }

        for (double value : values) {
            if (value < 0 || value > 100) {
                throw new IllegalArgumentException("모든 값은 0과 100 사이여야 합니다.");
            }
        }
    }

    public static class CarScoreNormalizer {
        private static final LocalDate BASE_DATE = LocalDate.of(2019, 1, 1);
        private static final LocalDate MAX_DATE = LocalDate.of(2024, 12, 31);
        private static final int TOTAL_MONTHS = calculateMonthsDifference(MAX_DATE);

        // accidentCount 정규화
        public static double normalizeAccidentCount(double count) {
            validateRange(count, 0, 4);
            return 100 - (count * 25);
        }

        // initialRegistration 정규화
        public static double normalizeInitialRegistration(String dateStr) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate date = LocalDate.parse(dateStr, formatter);

            validateDateRange(date, BASE_DATE, MAX_DATE);
            int months = calculateMonthsDifference(date);
            return (double) months / TOTAL_MONTHS * 100;
        }

        // mileage 정규화
        public static double normalizeMileage(double mileage) {
            validateRange(mileage, 600, 100000);

            return 100 * (100000 - mileage) / (100000 - 600);
        }

        // fuelEfficiency 정규화
        public static double normalizeFuelEfficiency(double fuelEfficiency) {
            validateRange(fuelEfficiency, 4.0, 20.0);
            return (fuelEfficiency - 4.0) / (20.0 - 4.0) * 100;
        }

        private static int calculateMonthsDifference(LocalDate endDate) {
            return (int) ChronoUnit.MONTHS.between(CarScoreNormalizer.BASE_DATE, endDate);
        }

        private static void validateRange(double value, double min, double max) {
            if (value < min || value > max) {
                throw new IllegalArgumentException(
                        String.format("값은 %s와 %s 사이여야 합니다. 현재값: %s", min, max, value));
            }
        }

        private static void validateDateRange(LocalDate date, LocalDate min, LocalDate max) {
            if (date.isBefore(min) || date.isAfter(max)) {
                throw new IllegalArgumentException(
                        String.format("날짜는 %s와 %s 사이여야 합니다. 현재값: %s", min, max, date));
            }
        }
    }
}
