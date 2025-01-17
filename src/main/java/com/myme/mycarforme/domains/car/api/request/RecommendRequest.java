package com.myme.mycarforme.domains.car.api.request;

public record RecommendRequest(BudgetRange budget,
                               UsageType usage,
                               MaintenanceBudget maintenance,
                               DrivingExperience experience,
                               PreferredType preferredType) {
    public static RecommendRequest of(
            BudgetRange budget,
            UsageType usage,
            MaintenanceBudget maintenance,
            DrivingExperience experience,
            PreferredType preferredType) {
        return new RecommendRequest(budget, usage, maintenance, experience, preferredType);
    }

    public enum BudgetRange {
        NO_LIMIT(0),
        UNDER_2000(1),
        UNDER_4000(2),
        UNDER_6000(3),
        OVER_6000(4);

        private final int value;

        BudgetRange(int value) {
            this.value = value;
        }

        public static BudgetRange fromValue(int value) {
            for (BudgetRange type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid budget value: " + value);
        }
    }

    public enum UsageType {
        COMMUTE(0),
        TRAVEL(1),
        LEISURE(2),
        FAMILY(3),
        BUSINESS(4);

        private final int value;

        UsageType(int value) {
            this.value = value;
        }

        public static UsageType fromValue(int value) {
            for (UsageType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid usage value: " + value);
        }
    }

    public enum MaintenanceBudget {
        UNDER_200000(0),
        UNDER_300000(1),
        UNDER_400000(2),
        UNDER_500000(3),
        OVER_500000(4);

        private final int value;

        MaintenanceBudget(int value) {
            this.value = value;
        }

        public static MaintenanceBudget fromValue(int value) {
            for (MaintenanceBudget type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid maintenance budget value: " + value);
        }
    }

    public enum DrivingExperience {
        LICENSE_ONLY(0),
        UNDER_1YEAR(1),
        UNDER_3YEARS(2),
        OVER_3YEARS(3),
        EXPERT(4);

        private final int value;

        DrivingExperience(int value) {
            this.value = value;
        }

        public static DrivingExperience fromValue(int value) {
            for (DrivingExperience type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid driving experience value: " + value);
        }
    }

    public enum PreferredType {
        NO_PREFERENCE(0),
        COMPACT(1),
        SEDAN(2),
        SUV(3),
        VAN(4);

        private final int value;

        PreferredType(int value) {
            this.value = value;
        }

        public static PreferredType fromValue(int value) {
            for (PreferredType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid preferred type value: " + value);
        }
    }


}
