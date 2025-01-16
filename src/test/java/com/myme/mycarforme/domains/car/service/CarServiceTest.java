package com.myme.mycarforme.domains.car.service;

import com.myme.mycarforme.domains.car.api.request.CarSearchRequest;
import com.myme.mycarforme.domains.car.api.response.MmScoreResponse;
import com.myme.mycarforme.domains.car.domain.*;
import com.myme.mycarforme.domains.car.dto.*;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.exception.ImageNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    // 차 목록 및 검색
    @Test
    void searchCars_withNoCondition_returnAllCars() { // 검색 조건에 아무것도 없을 때
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Car> cars = createSampleCars();
        Page<Car> carPage = new PageImpl<>(cars, pageable, cars.size());

        given(carRepository.findAllBySearchCondition(
                null, null, null, null, null, null, null, null, null, pageable
        )).willReturn(carPage);

        // when
        CarSearchRequest request = new CarSearchRequest(
                null, null, null, null, null, null, null, null, null
        );
        Page<CarDto> result = carService.searchCars(request, "userId", pageable);

        // then
        assertThat(result).hasSize(5);
        assertThat(result).allSatisfy(car -> {
            assertThat(car.carName()).isNotNull();
            assertThat(car.carNumber()).isNotNull();
            assertThat(car.mainImage()).isNotNull();
//            assertThat(car.createdAt()).isNotNull();
//            assertThat(car.updatedAt()).isNotNull();
        });
    }

    @Test
    void searchCars_withModelKeyWord_returnMatchedCars() { // 모델명 키워드로
        // given
        String keyword = "아반떼";
        List<Car> cars = List.of(
                createCar("아반떼 N", "세단", "가솔린"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> carPage = new PageImpl<>(cars, pageable, cars.size());

        given(carRepository.findAllBySearchCondition(
                keyword,    // keyword
                null,       // carType
                null,       // fuelType
                null,       // minSellingPrice
                null,       // maxSellingPrice
                null,       // minMileage
                null,       // maxMileage
                null,       // minYear
                null,       // maxYear
                pageable    // pageable 추가
        )).willReturn(carPage);

        // when
        CarSearchRequest request = new CarSearchRequest(
                keyword, null, null, null, null, null, null, null, null);
        Page<CarDto> result = carService.searchCars(request, "userId", pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).carName()).contains("아반떼");
        //assertThat(result.getContent().get(0).carId()).isNotNull();
        assertThat(result.getContent().get(0).carNumber()).isNotNull();
        assertThat(result.getContent().get(0).mainImage()).isNotNull();
    }

    @Test
    void searchCars_withFuelTypeandCarType_returnMatchedCars() { // 연료 타입 & 차종
        // given
        String fuelType = "가솔린";
        String carType = "세단";
        List<Car> cars = List.of(
                createCar("아반떼", carType, fuelType),
                createCar("소나타", carType, fuelType)
        );
        given(carRepository.findAllBySearchCondition(
                null, List.of(carType), List.of(fuelType), null, null, null, null, null, null
        )).willReturn(cars);

        // when
        CarSearchRequest request = CarSearchRequest.ofSingle(
                null, carType, fuelType, null, null, null, null, null, null
        );
        List<CarDto> result = carService.searchCars(request);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(car ->
                car.carName().contains("아반떼") || car.carName().contains("소나타")
        );

    }

    @Test
    void searchCars_withPriceRange_returnFilteredCars() { // 가격 범위
        // given
        Long minPrice = 2000L;
        Long maxPrice = 3000L;
        List<Car> cars = List.of(createCarWithPrice("아반떼", 2500L));
        given(carRepository.findAllBySearchCondition(
                null, null, null, minPrice, maxPrice, null, null, null, null
        )).willReturn(cars);

        // when
        CarSearchRequest request = new CarSearchRequest(
                null, null, null, minPrice, maxPrice, null, null, null, null
        );
        List<CarDto> result = carService.searchCars(request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).sellingPrice()).isBetween(2000L, 3000L);
    }

    @Test
    void searchCars_withAllConditionsAndMultipleTypes_returnFilteredCars() { // 모든 검색 조건 + 다중 선택까지
        // given
        String keyword = "아반떼";
        List<String> carTypes = List.of("세단", "SUV");
        List<String> fuelTypes = List.of("가솔린", "하이브리드");
        Long minPrice = 2000L;
        Long maxPrice = 3000L;
        Long minMileage = 5000L;
        Long maxMileage = 20000L;
        Long minYear = 2020L;
        Long maxYear = 2023L;

        List<Car> cars = List.of(
                Car.builder()
                        .carName("아반떼 CN7")
                        .carNumber("12가3456")
                        .carType("세단")
                        .fuelType("가솔린")
                        .sellingPrice(2500L)
                        .mileage(10000L)
                        .year(2022L)
                        .mainImage("image1.jpg")
                        .build(),
                Car.builder()

                        .carName("아반떼 하이브리드")
                        .carNumber("34나5678")
                        .carType("세단")
                        .fuelType("하이브리드")
                        .sellingPrice(2800L)
                        .mileage(8000L)
                        .year(2021L)
                        .mainImage("image2.jpg")
                        .build()
        );

        given(carRepository.findAllBySearchCondition(
                keyword, carTypes, fuelTypes,
                minPrice, maxPrice,
                minMileage, maxMileage,
                minYear, maxYear
        )).willReturn(cars);

        // when
        CarSearchRequest request = new CarSearchRequest(
                keyword, carTypes, fuelTypes,
                minPrice, maxPrice,
                minMileage, maxMileage,
                minYear, maxYear
        );
        List<CarDto> result = carService.searchCars(request);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(car -> {
            assertThat(car.carName()).contains(keyword);
            assertThat(car.sellingPrice()).isBetween(minPrice, maxPrice);
            assertThat(car.mileage()).isBetween(minMileage, maxMileage);
            assertThat(car.year()).isBetween(minYear, maxYear);
        });

        // 구체적인 결과 검증
        assertThat(result).anySatisfy(car -> {
            assertThat(car.carName()).isEqualTo("아반떼 CN7");
            assertThat(car.carNumber()).isEqualTo("12가3456");
            assertThat(car.mainImage()).isEqualTo("image1.jpg");
        });
    }

    // 테스트용 데이터 생성 헬퍼 메서드
    private List<Car> createSampleCars() {
        return List.of(
                Car.builder()

                        .carName("아반떼 CN7")
                        .carNumber("12가3456")
                        .carType("세단")
                        .fuelType("가솔린")
                        .mainImage("image1.jpg")
                        .year(2022L)
                        .mileage(10000L)
                        .sellingPrice(2500L)

                        .build(),

                Car.builder()

                        .carName("투싼 NX4")
                        .carNumber("34나5678")
                        .carType("SUV")
                        .fuelType("디젤")
                        .mainImage("image2.jpg")
                        .year(2021L)
                        .mileage(25000L)
                        .sellingPrice(3200L)
                        .build(),

                Car.builder()
                        .carName("그랜저 IG")
                        .carNumber("56다7890")
                        .carType("세단")
                        .fuelType("가솔린")
                        .mainImage("image3.jpg")
                        .year(2020L)
                        .mileage(45000L)
                        .sellingPrice(2800L)
                        .build(),

                Car.builder()
                        .carName("K5 DL3")
                        .carNumber("78라1234")
                        .carType("세단")
                        .fuelType("LPG")
                        .mainImage("image4.jpg")
                        .year(2023L)
                        .mileage(5000L)
                        .sellingPrice(3000L)
                        .build(),

                Car.builder()
                        .carName("카니발 KA4")
                        .carNumber("90마5678")
                        .carType("승합")
                        .fuelType("디젤")
                        .mainImage("image5.jpg")
                        .year(2022L)
                        .mileage(30000L)
                        .sellingPrice(4200L)
                        .build()
        );
    }

    private Car createCar(String name, String type, String fuel) {
        return Car.builder()

                .carName(name)
                .carNumber("12가3456")
                .carType(type)
                .fuelType(fuel)
                .mainImage("image1.jpg")
                .year(2022L)
                .mileage(10000L)
                .sellingPrice(2500L)
                .build();
    }

    private Car createCarWithPrice(String name, Long price) {
        return Car.builder()
                .carName(name)
                .carNumber("테스트번호")  // 필수 필드 추가
                .carType("세단")         // 필수 필드 추가
                .fuelType("가솔린")      // 필수 필드 추가
                .mainImage("test.jpg")   // 필수 필드 추가
                .year(2022L)            // 필수 필드 추가
                .mileage(10000L)        // 필수 필드 추가
                .sellingPrice(price)
                .build();
    }

    // 차량 상세 정보
    @Test
    void getCarDetail_whenCarExists_returnCarDetail() {
        // given
        Long carId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // 차량 생성
        Car car = createTestCar();

        // BaseTimeEntity 필드 설정
        ReflectionTestUtils.setField(car, "createdAt", now);
        ReflectionTestUtils.setField(car, "updatedAt", now);

        // accidentHistories 초기화
        ReflectionTestUtils.setField(car, "accidentHistoryList", new ArrayList<>());

        // 옵션리스트 생성 및 설정
        OptionList optionList = createTestOptionList(car);
        ReflectionTestUtils.setField(car, "optionList", optionList);
        // OptionList의 BaseTimeEntity 필드도 설정
        ReflectionTestUtils.setField(optionList, "createdAt", now);
        ReflectionTestUtils.setField(optionList, "updatedAt", now);
        ReflectionTestUtils.setField(car, "optionList", optionList);

        // 사고이력 추가
        AccidentHistory accidentHistory = createTestAccidentHistory(car);
        // AccidentHistory의 BaseTimeEntity 필드도 설정
        ReflectionTestUtils.setField(accidentHistory, "createdAt", now);
        ReflectionTestUtils.setField(accidentHistory, "updatedAt", now);
        car.getAccidentHistoryList().add(accidentHistory);

        given(carRepository.findByIdWithDetails(carId)).willReturn(Optional.of(car));

        // when
        CarDetailDto result = carService.getCarDetail(carId);

        // then
        assertThat(result).satisfies(detail -> {
            // Car 기본 정보 검증
            assertThat(detail.carName()).isEqualTo("아반떼 CN7");
            assertThat(detail.year()).isEqualTo(2022L);
            assertThat(detail.mileage()).isEqualTo(10000L);
            assertThat(detail.sellingPrice()).isEqualTo(2500L);
            assertThat(detail.exteriorColor()).isEqualTo("검정");
            assertThat(detail.interiorColor()).isEqualTo("크림색");
            assertThat(detail.displacement()).isEqualTo(1598L);
            assertThat(detail.fuelType()).isEqualTo("가솔린");
            assertThat(detail.transmissionType()).isEqualTo("자동8단");
            assertThat(detail.location()).isEqualTo("서울 강남구");
            assertThat(detail.fuelEfficiency()).isEqualTo(12.5);
            assertThat(detail.mainImage()).isEqualTo("main.jpg");
            assertThat(detail.newCarPrice()).isEqualTo(2800L);
            assertThat(detail.carNumber()).isEqualTo("12가3456");

            // OptionList 검증
            assertThat(detail.optionLists()).satisfies(options -> {
                assertThat(options.hasNavigation()).isTrue();
                assertThat(options.hasHiPass()).isTrue();
                assertThat(options.hasHeatedSteeringWheel()).isTrue();
                assertThat(options.hasPowerTrunk()).isFalse();
                assertThat(options.hasHeadUpDisplay()).isFalse();
            });

            // AccidentHistories 검증
            assertThat(detail.accidentHistoryList())
                    .hasSize(1)
                    .first()
                    .satisfies(accident -> {
                        //assertThat(accident.accidentDate()).isEqualTo(LocalDateTime.of(2023, 5, 15, 0, 0));
                        assertThat(accident.accidentDate()).isEqualTo("2023-05-15T00:00");
                        assertThat(accident.carPartsPrice()).isEqualTo(500L);
                        assertThat(accident.carLaborPrice()).isEqualTo(200L);
                        assertThat(accident.carPaintPrice()).isEqualTo(300L);
                    });
        });
    }

    private Car createTestCar() {
        return Car.builder()
                .carName("아반떼 CN7")
                .carNumber("12가3456")
                .carType("세단")
                .year(2022L)
                .initialRegistration("202201")
                .mileage(10000L)
                .driveType("FF")
                .displacement(1598L)
                .sellingPrice(2500L)
                .exteriorColor("검정")
                .interiorColor("크림색")
                .seating(5L)
                .fuelType("가솔린")
                .transmissionType("자동8단")
                .location("서울 강남구")
                .fuelEfficiency(12.5)
                .mainImage("main.jpg")
                .newCarPrice(2800L)
                .carNumber("12가3456")
                .build();
    }

    private OptionList createTestOptionList(Car car) {
        return OptionList.builder()
                .car(car)
                .hasNavigation(true)
                .hasHiPass(true)
                .hasHeatedSteeringWheel(true)
                .hasHeatedSeats(true)
                .hasVentilatedFrontSeats(true)
                .hasPowerFrontSeats(true)
                .isLeatherSeats(true)
                .hasPowerTrunk(false)
                .hasSunroof(true)
                .hasHeadUpDisplay(false)
                .hasSurroundViewMonitor(true)
                .hasRearViewMonitor(true)
                .hasBlindSpotWarning(true)
                .hasLaneDepartureWarning(true)
                .hasSmartCruiseControl(true)
                .hasFrontParkingSensors(true)
                .build();
    }

    private AccidentHistory createTestAccidentHistory(Car car) {
        return AccidentHistory.builder()
                .car(car)
                .accidentDate(LocalDateTime.of(2023, 5, 15, 0, 0))
                .carPartsPrice(500L)
                .carLaborPrice(200L)
                .carPaintPrice(300L)
                .build();
    }





    @Test
    void getCarDetail_whenCarNotExists_throwEntityNotFoundException() { // 차량이 존재하지 않을 때 적절한 예외를 발생????
        // given
        Long carId = 999L;

        given(carRepository.findByIdWithDetails(carId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CarNotFoundException.class, () -> {
            carService.getCarDetail(carId);
        });



    }

    // exterior 360 Image
    @Test
    void get360Images_whenImagesExist_returnAllImages() {
        // given
        Long carId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Car car = createTestCar();
        List<Exterior360Image> images = createTest360Images(car);

        // BaseTimeEntity 필드 설정
        images.forEach(image -> {
            ReflectionTestUtils.setField(image, "createdAt", now);
            ReflectionTestUtils.setField(image, "updatedAt", now);
        });

        given(carRepository.findAllByCarIdOrderByRotationDegree(carId))
                .willReturn(images);

        // when
        List<Exterior360ImageDto> result = carService.get360Images(carId);

        // then
        assertThat(result).hasSize(36);
        assertThat(result).allSatisfy(image -> {
            assertThat(image.imageUrl()).isNotNull();
            assertThat(image.rotationDegree()).isBetween(0, 350);
            assertThat(image.createdAt()).isNotNull();
            assertThat(image.updatedAt()).isNotNull();
        });

        // 구체적인 검증
        assertThat(result).anySatisfy(image -> {
            assertThat(image.rotationDegree()).isEqualTo(0);
            assertThat(image.imageUrl()).isEqualTo("exterior_0.jpg");
        });

        assertThat(result).anySatisfy(image -> {
            assertThat(image.rotationDegree()).isEqualTo(350);
            assertThat(image.imageUrl()).isEqualTo("exterior_350.jpg");
        });
    }

    @Test
    void get360Images_whenNoImagesExist_throwImageNotFoundException() {
        // given
        Long carId = 999L;
        given(carRepository.findAllByCarIdOrderByRotationDegree(carId))
                .willReturn(new ArrayList<>());

        // when & then
        assertThrows(ImageNotFoundException.class, () -> {
            carService.get360Images(carId);
        });
    }

    // 테스트 헬퍼 메서드
    private List<Exterior360Image> createTest360Images(Car car) {
        List<Exterior360Image> images = new ArrayList<>();
        for (int degree = 0; degree <= 350; degree += 10) {
            images.add(Exterior360Image.builder()
                    .car(car)
                    .imageUrl("exterior_" + degree + ".jpg")
                    .rotationDegree(degree)
                    .build());
        }
        return images;
    }

    // 차량 detail test
    @Test
    void getDetailImages_whenImagesExist_returnAllImages() {
        // given
        Long carId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Car car = createTestCar();
        List<DetailImage> images = createTestDetailImages(car);

        // BaseTimeEntity 필드 설정
        images.forEach(image -> {
            ReflectionTestUtils.setField(image, "createdAt", now);
            ReflectionTestUtils.setField(image, "updatedAt", now);
        });

        given(carRepository.findAllDetailImagesByCarId(carId))
                .willReturn(images);

        // when
        List<DetailImageDto> result = carService.getDetailImages(carId);

        // then
        assertThat(result).hasSize(5); // 예시로 5장의 이미지를 가정
        assertThat(result).allSatisfy(image -> {
            assertThat(image.detailImageId()).isNotNull();
            assertThat(image.imageUrl()).isNotNull();
            assertThat(image.createdAt()).isNotNull();
            assertThat(image.updatedAt()).isNotNull();
        });

        // 구체적인 검증
        assertThat(result).anySatisfy(image -> {
            assertThat(image.imageUrl()).isEqualTo("detail_1.jpg");
        });
    }

    @Test
    void getDetailImages_whenNoImagesExist_throwImageNotFoundException() {
        // given
        Long carId = 999L;
        given(carRepository.findAllDetailImagesByCarId(carId))
                .willReturn(new ArrayList<>());

        // when & then
        assertThrows(ImageNotFoundException.class, () -> {
            carService.getDetailImages(carId);
        });
    }

    // 테스트 헬퍼 메서드
    private List<DetailImage> createTestDetailImages(Car car) {
        List<DetailImage> images = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            DetailImage image = DetailImage.builder()
                    .car(car)
                    .imageUrl("detail_" + i + ".jpg")
                    .build();

            // id 값 설정
            ReflectionTestUtils.setField(image, "id", (long) i);

            images.add(image);
        }
        return images;
    }

    // 가성비 점수순
    @Test
    void getCarList_orderByMmScoreDesc_returnTopFiveCars() { // mmScore 상위 5개 차량
        // given
        LocalDateTime now = LocalDateTime.now();


        List<Car> mockCars = List.of(
                Car.builder()
                        .carName("제네시스 G80")
                        .year(2023L)
                        .mileage(1000L)
                        .sellingPrice(50000000L)
                        .mainImage("genesis_g80.jpg")
                        .mmScore(98.5)
                        .contractedAt(now)
                        .build(),
                Car.builder()
                        .carName("그랜저 하이브리드")
                        .year(2022L)
                        .mileage(5000L)
                        .sellingPrice(45000000L)
                        .mainImage("grandeur.jpg")
                        .mmScore(95.0)
                        .contractedAt(now)
                        .build(),
                Car.builder()
                        .carName("벤츠 E클래스")
                        .year(2023L)
                        .mileage(3000L)
                        .sellingPrice(70000000L)
                        .mainImage("benz_e.jpg")
                        .mmScore(92.5)
                        .contractedAt(now)
                        .build(),
                Car.builder()
                        .carName("BMW 5시리즈")
                        .year(2022L)
                        .mileage(8000L)
                        .sellingPrice(65000000L)
                        .mainImage("bmw_5.jpg")
                        .mmScore(90.0)
                        .contractedAt(now)
                        .build(),
                Car.builder()
                        .carName("아우디 A6")
                        .year(2023L)
                        .mileage(2000L)
                        .sellingPrice(68000000L)
                        .mainImage("audi_a6.jpg")
                        .mmScore(88.5)
                        .contractedAt(now)
                        .build()
        );

        given(carRepository.findTop5ByOrderByMmScoreDesc()).willReturn(mockCars);

        // when
        MmScoreResponse response = carService.getTop5CarsByMmScore();

        // then
        assertThat(response).isNotNull();
        assertThat(response.contents()).hasSize(5);

        // 첫 번째 차량 검증
        MmScoreDto firstCar = response.contents().get(0);
        assertThat(firstCar.carName()).isEqualTo("제네시스 G80");
        assertThat(firstCar.year()).isEqualTo(2023L);
        assertThat(firstCar.mileage()).isEqualTo(1000L);
        assertThat(firstCar.sellingPrice()).isEqualTo(50000000L);
        assertThat(firstCar.mainImage()).isEqualTo("genesis_g80.jpg");
        assertThat(firstCar.mmScore()).isEqualTo(98.5);

        // mmScore 내림차순 정렬 검증
        List<Double> mmScores = response.contents().stream()
                .map(MmScoreDto::mmScore)
                .toList();
        assertThat(mmScores)
                .isSortedAccordingTo((a, b) -> Double.compare(b, a)); // 내림차순 검증

        // 각 차량의 필수 필드 존재 여부 검증
        response.contents().forEach(car -> {
            //assertThat(car.carId()).isNotNull();
            assertThat(car.carName()).isNotNull();
            assertThat(car.year()).isNotNull();
            assertThat(car.mileage()).isNotNull();
            assertThat(car.sellingPrice()).isNotNull();
            assertThat(car.mainImage()).isNotNull();
            assertThat(car.mmScore()).isNotNull();
           // assertThat(car.createdAt()).isNotNull();
           // assertThat(car.updatedAt()).isNotNull();
        });

        verify(carRepository).findTop5ByOrderByMmScoreDesc();
    }
    @Test
    void getCarList_withLessThanFiveData_returnAllAvailableCars() { // 데이터 5개 미만 (3개로 해보자)
        // given
        LocalDateTime now = LocalDateTime.now();
        List<Car> mockCars = List.of(
                Car.builder()
                        .carName("제네시스 G80")
                        .year(2023L)
                        .mileage(1000L)
                        .sellingPrice(50000000L)
                        .mainImage("genesis_g80.jpg")
                        .mmScore(98.5)
                        .contractedAt(now)
                        .build(),
                Car.builder()
                        .carName("그랜저 하이브리드")
                        .year(2022L)
                        .mileage(5000L)
                        .sellingPrice(45000000L)
                        .mainImage("grandeur.jpg")
                        .mmScore(95.0)
                        .contractedAt(now)
                        .build(),
                Car.builder()
                        .carName("벤츠 E클래스")
                        .year(2023L)
                        .mileage(3000L)
                        .sellingPrice(70000000L)
                        .mainImage("benz_e.jpg")
                        .mmScore(92.5)
                        .contractedAt(now)
                        .build()
        );

        given(carRepository.findTop5ByOrderByMmScoreDesc()).willReturn(mockCars);

        // when
        MmScoreResponse response = carService.getTop5CarsByMmScore();

        // then
        assertThat(response).isNotNull();
        assertThat(response.contents()).hasSize(3); // 3개만 있는지 확인

        // 첫 번째 차량 검증
        MmScoreDto firstCar = response.contents().get(0);
        assertThat(firstCar.carName()).isEqualTo("제네시스 G80");
        assertThat(firstCar.year()).isEqualTo(2023L);
        assertThat(firstCar.mileage()).isEqualTo(1000L);
        assertThat(firstCar.sellingPrice()).isEqualTo(50000000L);
        assertThat(firstCar.mainImage()).isEqualTo("genesis_g80.jpg");
        assertThat(firstCar.mmScore()).isEqualTo(98.5);

        // mmScore 내림차순 정렬 검증
        List<Double> mmScores = response.contents().stream()
                .map(MmScoreDto::mmScore)
                .toList();
        assertThat(mmScores)
                .isSortedAccordingTo((a, b) -> Double.compare(b, a)); // 내림차순 검증

        // 각 차량의 필수 필드 존재 여부 검증
        response.contents().forEach(car -> {
            //assertThat(car.carId()).isNotNull();
            assertThat(car.carName()).isNotNull();
            assertThat(car.year()).isNotNull();
            assertThat(car.mileage()).isNotNull();
            assertThat(car.sellingPrice()).isNotNull();
            assertThat(car.mainImage()).isNotNull();
            assertThat(car.mmScore()).isNotNull();
           // assertThat(car.createdAt()).isNotNull();
           // assertThat(car.updatedAt()).isNotNull();
        });

        verify(carRepository).findTop5ByOrderByMmScoreDesc();
    }

    @Test
    void getCarList_withNoData_returnEmptyList() { // 데이터가 없으면?? 빈 리스트 반환
        // given
        given(carRepository.findTop5ByOrderByMmScoreDesc()).willReturn(List.of());

        // when
        MmScoreResponse response = carService.getTop5CarsByMmScore();

        // then
        assertThat(response).isNotNull();
        assertThat(response.contents()).isEmpty();
        verify(carRepository).findTop5ByOrderByMmScoreDesc();
    }













}


