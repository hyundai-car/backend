package com.myme.mycarforme.domains.car.service;

import static com.myme.mycarforme.domains.car.api.request.RecommendRequest.UsageType.BUSINESS;
import static com.myme.mycarforme.domains.car.api.request.RecommendRequest.UsageType.COMMUTE;
import static com.myme.mycarforme.domains.car.api.request.RecommendRequest.UsageType.FAMILY;
import static com.myme.mycarforme.domains.car.api.request.RecommendRequest.UsageType.LEISURE;
import static com.myme.mycarforme.domains.car.api.request.RecommendRequest.UsageType.TRAVEL;

import com.myme.mycarforme.domains.car.api.request.RecommendRequest;
import com.myme.mycarforme.domains.car.api.request.RecommendRequest.BudgetRange;
import com.myme.mycarforme.domains.car.api.request.RecommendRequest.DrivingExperience;
import com.myme.mycarforme.domains.car.api.request.RecommendRequest.MaintenanceBudget;
import com.myme.mycarforme.domains.car.api.request.RecommendRequest.PreferredType;
import com.myme.mycarforme.domains.car.api.request.RecommendRequest.UsageType;
import com.myme.mycarforme.domains.car.api.response.RecommendHistoryResponse;
import com.myme.mycarforme.domains.car.api.response.RecommendResponse;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Recommend;
import com.myme.mycarforme.domains.car.dto.RecommendDto.CarScore;
import com.myme.mycarforme.domains.car.dto.RecommendDto.CategoryWeights;
import com.myme.mycarforme.domains.car.dto.RecommendHistoryDto;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.car.repository.LikeRepository;
import com.myme.mycarforme.domains.car.repository.RecommendRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {
    private final CarRepository carRepository;
    private final RecommendRepository recommendRepository;
    private final LikeRepository likeRepository;


    @Transactional
    public RecommendResponse recommendCars(String userId, RecommendRequest request) {
        List<Car> filteredCars = filterCarsByBudget(request.budget());
        CategoryWeights weights = calculateWeights(request);
        List<CarScore> scoredCars = calculateCarScores(filteredCars, weights, request);
        Map<String, List<CarScore>> categoryRankings = rankByCategories(scoredCars);
        List<Recommend> recommendations = createRecommendations(userId, categoryRankings);
        return createResponse(recommendations);
    }

    private List<Car> filterCarsByBudget(BudgetRange budget) {
        Long maxPrice = switch (budget) {
            case UNDER_2000 -> 2000L;
            case UNDER_4000 -> 4000L;
            case UNDER_6000 -> 6000L;
            case OVER_6000, NO_LIMIT -> Long.MAX_VALUE;
        };
        return carRepository.findBySellingPriceLessThanEqualAndIsOnSale(maxPrice, 1);
    }

    private CategoryWeights calculateWeights(RecommendRequest request) {
        Map<UsageType, CategoryWeights> usageWeights = Map.of(
                COMMUTE, CategoryWeights.of(0.8, 0.7, 0.7, 0.6, 0.5, 0.7),
                TRAVEL, CategoryWeights.of(0.9, 0.8, 0.8, 0.7, 0.6, 0.8),
                LEISURE, CategoryWeights.of(0.7, 0.9, 0.8, 0.8, 0.6, 0.9),
                FAMILY, CategoryWeights.of(0.7, 0.8, 0.7, 0.7, 0.8, 0.8),
                BUSINESS, CategoryWeights.of(0.8, 0.8, 0.8, 0.7, 0.7, 0.7)
        );

        return usageWeights.get(request.usage());
    }

    private List<CarScore> calculateCarScores(List<Car> cars, CategoryWeights weights, RecommendRequest request) {
        return cars.stream()
                .map(car -> calculateIndividualCarScore(car, weights, request))
                .collect(Collectors.toList());
    }

    private CarScore calculateIndividualCarScore(Car car, CategoryWeights weights, RecommendRequest request) {
        double efficiencyScore = calculateEfficiencyScore(car, request) * weights.efficiencyWeight();
        double typeScore = calculateTypeScore(car, request) * weights.typeWeight();
        double fuelScore = calculateFuelScore(car, request) * weights.fuelWeight();
        double displacementScore = calculateDisplacementScore(car, request) * weights.displacementWeight();
        double repairScore = calculateRepairScore(car, request) * weights.repairWeight();
        double totalScore = efficiencyScore + typeScore + fuelScore + displacementScore + repairScore;
        double preferredTypeScore = calculatePreferredTypeScore(car, request.preferredType()) * weights.typeWeight();
        // preferredTypeScore를 typeScore와 결합
        double finalTypeScore = (typeScore + preferredTypeScore) / 2.0;

        return CarScore.of(
                car,
                efficiencyScore,
                typeScore,
                fuelScore,
                displacementScore,
                repairScore,
                preferredTypeScore,
                finalTypeScore,
                totalScore
        );
    }

    private double calculateEfficiencyScore(Car car, RecommendRequest request) {
        return switch (request.usage()) {
            case COMMUTE -> car.getCityEfficiency() * 0.8 + car.getHighwayEfficiency() * 0.6;
            case TRAVEL -> car.getCityEfficiency() * 0.5 + car.getHighwayEfficiency() * 0.9;
            case LEISURE -> car.getCityEfficiency() * 0.5 + car.getHighwayEfficiency() * 0.7;
            case FAMILY -> car.getCityEfficiency() * 0.7 + car.getHighwayEfficiency() * 0.6;
            case BUSINESS -> car.getCityEfficiency() * 0.8 + car.getHighwayEfficiency() * 0.7;
        };
    }

    private double calculateTypeScore(Car car, RecommendRequest request) {
        Map<String, Double> typeScores = switch (request.usage()) {
            case COMMUTE -> Map.of(
                    "승용차", 0.8,
                    "SUV", 0.4,
                    "럭셔리", 0.3,
                    "Van", 0.2
            );
            case TRAVEL -> Map.of(
                    "SUV", 0.8,
                    "Van", 0.8,
                    "승용차", 0.4,
                    "럭셔리", 0.3
            );
            case LEISURE -> Map.of(
                    "SUV", 0.9,
                    "Van", 0.6,
                    "승용차", 0.4,
                    "럭셔리", 0.3
            );
            case FAMILY -> Map.of(
                    "Van", 0.8,
                    "SUV", 0.7,
                    "승용차", 0.5,
                    "럭셔리", 0.4
            );
            case BUSINESS -> Map.of(
                    "승용차", 0.8,
                    "럭셔리", 0.7,
                    "SUV", 0.5,
                    "Van", 0.4
            );
        };

        return typeScores.getOrDefault(car.getCarType(), 0.0);
    }


    private double calculateFuelScore(Car car, RecommendRequest request) {
        Map<String, Double> fuelScores = switch (request.usage()) {
            case COMMUTE -> Map.of(
                    "전기", 0.8,
                    "가솔린", 0.7,
                    "하이브리드", 0.5,
                    "디젤", 0.3
            );
            case TRAVEL -> Map.of(
                    "디젤", 0.8,
                    "가솔린", 0.8,
                    "하이브리드", 0.5,
                    "전기", 0.3
            );
            case LEISURE -> Map.of(
                    "가솔린", 0.8,
                    "디젤", 0.7,
                    "하이브리드", 0.5,
                    "전기", 0.4
            );
            case FAMILY -> Map.of(
                    "가솔린", 0.8,
                    "하이브리드", 0.7,
                    "전기", 0.6,
                    "디젤", 0.4
            );
            case BUSINESS -> Map.of(
                    "전기", 0.8,
                    "가솔린", 0.7,
                    "하이브리드", 0.6,
                    "디젤", 0.3
            );
        };

        return fuelScores.getOrDefault(car.getFuelType(), 0.0);
    }

    private double calculateDisplacementScore(Car car, RecommendRequest request) {
        Map<DrivingExperience, Map<Range, Double>> experienceScores = Map.of(
                DrivingExperience.LICENSE_ONLY, Map.of(
                        new Range(0L, 1600L), 0.9,
                        new Range(1600L, 2000L), 0.3,
                        new Range(2000L, 2500L), 0.1,
                        new Range(2500L, Long.MAX_VALUE), 0.0
                ),
                DrivingExperience.UNDER_1YEAR, Map.of(
                        new Range(0L, 1600L), 0.8,
                        new Range(1600L, 2000L), 0.7,
                        new Range(2000L, 2500L), 0.2,
                        new Range(2500L, Long.MAX_VALUE), 0.1
                ),
                DrivingExperience.UNDER_3YEARS, Map.of(
                        new Range(0L, 1600L), 0.5,
                        new Range(1600L, 2000L), 0.7,
                        new Range(2000L, 2500L), 0.8,
                        new Range(2500L, Long.MAX_VALUE), 0.4
                ),
                DrivingExperience.OVER_3YEARS, Map.of(
                        new Range(0L, 1600L), 0.6,
                        new Range(1600L, 2000L), 0.7,
                        new Range(2000L, 2500L), 0.8,
                        new Range(2500L, Long.MAX_VALUE), 0.8
                ),
                DrivingExperience.EXPERT, Map.of(
                        new Range(0L, 1600L), 0.6,
                        new Range(1600L, 2000L), 0.7,
                        new Range(2000L, 2500L), 0.9,
                        new Range(2500L, Long.MAX_VALUE), 0.9
                )
        );

        Map<Range, Double> rangeScores = experienceScores.get(request.experience());
        return rangeScores.entrySet().stream()
                .filter(entry -> entry.getKey().contains(Double.valueOf(car.getDisplacement())))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(0.0);
    }

    private double calculateRepairScore(Car car, RecommendRequest request) {
        Map<MaintenanceBudget, Map<Range, Double>> budgetScores = Map.of(
                MaintenanceBudget.UNDER_200000, Map.of(
                        new Range(0.0, 30.0), 0.9,
                        new Range(30.0, 50.0), 0.5,
                        new Range(50.0, 100.0), 0.1
                ),
                MaintenanceBudget.UNDER_300000, Map.of(
                        new Range(0.0, 50.0), 0.8,
                        new Range(50.0, 70.0), 0.5,
                        new Range(70.0, 100.0), 0.2
                ),
                MaintenanceBudget.UNDER_400000, Map.of(
                        new Range(0.0, 70.0), 0.7,
                        new Range(70.0, 80.0), 0.5,
                        new Range(80.0, 100.0), 0.3
                ),
                MaintenanceBudget.UNDER_500000, Map.of(
                        new Range(0.0, 80.0), 0.7,
                        new Range(80.0, 90.0), 0.6,
                        new Range(90.0, 100.0), 0.4
                ),
                MaintenanceBudget.OVER_500000, Map.of(
                        new Range(0.0, 100.0), 0.7
                )
        );

        Map<Range, Double> rangeScores = budgetScores.get(request.maintenance());
        return rangeScores.entrySet().stream()
                .filter(entry -> entry.getKey().contains(car.getRepairProbability()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(0.0);
    }
    private double calculatePreferredTypeScore(Car car, PreferredType preferredType) {
        if (preferredType == PreferredType.NO_PREFERENCE) {
            return 0.7; // 선호도 없음의 경우 중간 정도의 점수 부여
        }

        // 차종 가중치
        Map<String, Double> typeScores = switch (preferredType) {
            case COMPACT -> Map.of(
                    "승용차", 0.9,
                    "SUV", 0.2,
                    "Van", 0.1,
                    "럭셔리", 0.1
            );
            case SEDAN -> Map.of(
                    "승용차", 0.9,
                    "럭셔리", 0.7,
                    "SUV", 0.3,
                    "Van", 0.2
            );
            case SUV -> Map.of(
                    "SUV", 0.9,
                    "Van", 0.5,
                    "승용차", 0.3,
                    "럭셔리", 0.4
            );
            case VAN -> Map.of(
                    "Van", 0.9,
                    "SUV", 0.6,
                    "승용차", 0.2,
                    "럭셔리", 0.2
            );
            default -> Map.of(
                    "승용차", 0.7,
                    "SUV", 0.7,
                    "Van", 0.7,
                    "럭셔리", 0.7
            );
        };

        // 배기량 가중치
        Map<Range, Double> displacementScores = switch (preferredType) {
            case COMPACT -> Map.of(
                    new Range(0L, 1600L), 0.9,
                    new Range(1600L, 2000L), 0.4,
                    new Range(2000L, 2500L), 0.2,
                    new Range(2500L, Long.MAX_VALUE), 0.1
            );
            case SEDAN -> Map.of(
                    new Range(0L, 1600L), 0.5,
                    new Range(1600L, 2000L), 0.8,
                    new Range(2000L, 2500L), 0.7,
                    new Range(2500L, Long.MAX_VALUE), 0.6
            );
            case SUV -> Map.of(
                    new Range(0L, 1600L), 0.3,
                    new Range(1600L, 2000L), 0.7,
                    new Range(2000L, 2500L), 0.9,
                    new Range(2500L, Long.MAX_VALUE), 0.8
            );
            case VAN -> Map.of(
                    new Range(0L, 1600L), 0.2,
                    new Range(1600L, 2000L), 0.5,
                    new Range(2000L, 2500L), 0.8,
                    new Range(2500L, Long.MAX_VALUE), 0.9
            );
            default -> Map.of(
                    new Range(0L, Long.MAX_VALUE), 0.7
            );
        };

        // 차종 점수와 배기량 점수의 평균을 계산
        double typeScore = typeScores.getOrDefault(car.getCarType(), 0.0);
        double displacementScore = displacementScores.entrySet().stream()
                .filter(entry -> entry.getKey().contains(Double.valueOf(car.getDisplacement())))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(0.0);

        return (typeScore + displacementScore) / 2.0;
    }

    private Map<String, List<CarScore>> rankByCategories(List<CarScore> scoredCars) {
        Map<String, List<CarScore>> rankings = new HashMap<>();

        rankings.put("efficiency", new ArrayList<>(scoredCars));
        rankings.put("type", new ArrayList<>(scoredCars));
        rankings.put("fuel", new ArrayList<>(scoredCars));
        rankings.put("displacement", new ArrayList<>(scoredCars));
        rankings.put("repair", new ArrayList<>(scoredCars));

        Collections.sort(rankings.get("efficiency"),
                (a, b) -> Double.compare(b.efficiencyScore(), a.efficiencyScore()));
        Collections.sort(rankings.get("type"),
                (a, b) -> Double.compare(b.typeScore(), a.typeScore()));
        Collections.sort(rankings.get("fuel"),
                (a, b) -> Double.compare(b.fuelScore(), a.fuelScore()));
        Collections.sort(rankings.get("displacement"),
                (a, b) -> Double.compare(b.displacementScore(), a.displacementScore()));
        Collections.sort(rankings.get("repair"),
                (a, b) -> Double.compare(b.repairScore(), a.repairScore()));

        return rankings;
    }

    private String findTopCategory(Map<String, List<CarScore>> categoryRankings) {
        return categoryRankings.entrySet().stream()
                .max(Comparator.comparingDouble(entry ->
                        entry.getValue().stream()
                                .mapToDouble(score -> switch(entry.getKey()) {
                                    case "efficiency" -> score.efficiencyScore();
                                    case "type" -> score.typeScore();
                                    case "fuel" -> score.fuelScore();
                                    case "displacement" -> score.displacementScore();
                                    case "repair" -> score.repairScore();
                                    default -> 0.0;
                                })
                                .average()
                                .orElse(0.0)
                ))
                .map(Map.Entry::getKey)
                .orElse("efficiency"); // 기본값 설정
    }

    private String findSecondCategory(Map<String, List<CarScore>> categoryRankings) {
        String topCategory = findTopCategory(categoryRankings);
        return categoryRankings.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(topCategory))
                .max(Comparator.comparingDouble(entry ->
                        entry.getValue().stream()
                                .mapToDouble(score -> switch(entry.getKey()) {
                                    case "efficiency" -> score.efficiencyScore();
                                    case "type" -> score.typeScore();
                                    case "fuel" -> score.fuelScore();
                                    case "displacement" -> score.displacementScore();
                                    case "repair" -> score.repairScore();
                                    default -> 0.0;
                                })
                                .average()
                                .orElse(0.0)
                ))
                .map(Map.Entry::getKey)
                .orElse("type"); // 기본값 설정
    }

    private List<Recommend> createRecommendations(String userId, Map<String, List<CarScore>> categoryRankings) {
        List<Recommend> recommendations = new ArrayList<>();

        // 최상위 카테고리에서 3개 선택
        String topCategory = findTopCategory(categoryRankings);
        List<CarScore> topScores = categoryRankings.get(topCategory);
        int topCount = Math.min(3, topScores.size());

        for (int i = 0; i < topCount; i++) {
            CarScore score = topScores.get(i);
            recommendations.add(createRecommend(score.car(), userId, (long) i, topCategory));
        }

        // 두 번째 카테고리에서 2개 선택
        String secondCategory = findSecondCategory(categoryRankings);
        List<CarScore> secondScores = categoryRankings.get(secondCategory);

        // 이미 선택된 차량은 제외
        secondScores = secondScores.stream()
                .filter(score -> !recommendations.stream()
                        .map(recommend -> recommend.getCar().getId())
                        .toList()
                        .contains(score.car().getId()))
                .toList();

        int secondCount = Math.min(2, secondScores.size());

        for (int i = topCount; i < topCount + secondCount; i++) {
            CarScore score = secondScores.get(i);
            recommendations.add(createRecommend(score.car(), userId, (long) i, secondCategory));
        }

        // 만약 5개가 안되면 남은 카테고리에서 추가 선택
        if (recommendations.size() < 5) {
            int remaining = 5 - recommendations.size();
            List<CarScore> remainingScores = categoryRankings.values().stream()
                    .flatMap(List::stream)
                    .filter(score -> !recommendations.stream()
                            .map(recommend -> recommend.getCar().getId())
                            .toList()
                            .contains(score.car().getId()))
                    .sorted((a, b) -> Double.compare(b.totalScore(), a.totalScore()))
                    .limit(remaining)
                    .toList();

            for (CarScore score : remainingScores) {
                recommendations.add(createRecommend(score.car(), userId, 2L, findBestCategory(score)));
            }
        }

        return recommendRepository.saveAll(recommendations);
    }

    private String findBestCategory(CarScore score) {
        Map<String, Double> scores = Map.of(
                "efficiency", score.efficiencyScore(),
                "type", score.finalTypeScore(),
                "fuel", score.fuelScore(),
                "displacement", score.displacementScore(),
                "repair", score.repairScore()
        );

        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("efficiency");
    }

    private Recommend createRecommend(Car car, String userId, Long priority, String category) {
        return Recommend.builder()
                .car(car)
                .userId(userId)
                .recommendedAt(LocalDateTime.now())
                .recommendPriority(priority)
                .recommendCondition(generateCondition(category))
                .recommendReason(generateReason(category))
                .build();
    }

    private RecommendResponse createResponse(List<Recommend> recommendations) {
        List<RecommendResponse.RecommendCarInfo> contents = recommendations.stream()
                .map(this::convertToRecommendCarInfo)
                .collect(Collectors.toList());

        return RecommendResponse.of(contents);  // of 메소드 사용
    }

    private RecommendResponse.RecommendCarInfo convertToRecommendCarInfo(Recommend recommend) {
        return RecommendResponse.RecommendCarInfo.of(
                recommend.getId(),
                recommend.getRecommendedAt().toString(),
                recommend.getRecommendPriority(),
                recommend.getRecommendCondition(),
                recommend.getRecommendReason(),
                recommend.getCreatedAt().toString(),
                recommend.getUpdatedAt().toString(),
                convertToCarInfo(recommend.getCar())
        );
    }

    private RecommendResponse.CarInfo convertToCarInfo(Car car) {
        return RecommendResponse.CarInfo.of(
                car.getId(),
                car.getCarName(),
                car.getInitialRegistration(),
                car.getMileage(),
                car.getSellingPrice(),
                (long) car.getLikeList().size(),  // size()가 int를 반환하므로 long으로 캐스팅
                car.getFuelType(),
                car.getMainImage()
        );
    }

    private String generateCondition(String category) {
        return switch (category) {
            case "efficiency" -> "연비 덕후를 위한 최고의 선택";
            case "type" -> "당신의 스타일에 딱 맞는 차종";
            case "fuel" -> "내 연료 취향 저격한 차량";
            case "displacement" -> "운전의 재미를 완성하는 배기량의 차";
            case "repair" -> "유지비 걱정 없는 똑똑한 선택";
            default -> "종합적으로 우수한 차량";
        };
    }

    private String generateReason(String category) {
        return switch (category) {
            case "efficiency" -> "도심 및 고속도로 연비가 우수하여 경제적인 운행이 가능합니다.";
            case "type" -> "고객님께서 선호하시는 차종으로, 용도에 적합한 차량입니다.";
            case "fuel" -> "선호하시는 연료 타입으로, 운행 스타일에 잘 맞는 차량입니다.";
            case "displacement" -> "운전 경험을 고려할 때 가장 적합한 배기량을 가진 차량입니다.";
            case "repair" -> "예상 관리 비용이 고객님의 예산에 적합한 차량입니다.";
            default -> "전반적으로 고객님의 조건에 잘 부합하는 차량입니다.";
        };
    }

    public RecommendHistoryResponse getRecommendHistory(String userId) {
        List<Recommend> recommendHistory = recommendRepository.findTop10RecommendHistory(userId);

        List<RecommendHistoryDto> contents = recommendHistory.stream()
                .map(recommend -> {
                    Long carId = recommend.getCar().getId();
                    boolean isLike = likeRepository.existsByCarIdAndUserIdAndIsLikeTrue(carId, userId);
                    long likeCount = likeRepository.countByCarIdAndIsLikeTrue(carId);
                    return RecommendHistoryDto.of(recommend, isLike, likeCount);
                })
                .collect(Collectors.toList());

        return RecommendHistoryResponse.of(contents);
    }

    @Getter
    @AllArgsConstructor
    private static class Range {
        private final double min;
        private final double max;

        public boolean contains(Double value) {
            return value >= min && value < max;
        }
    }





}

