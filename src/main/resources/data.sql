INSERT INTO cars (
    car_name, car_type, year, initial_registration, mileage, drive_type,
    displacement, selling_price, exterior_color, interior_color, seating,
    fuel_type, transmission_type, is_on_sale, location, mm_score,
    fuel_efficiency, main_image, new_car_price, saving_account, car_number,
    accident_severity, repair_probability, predicted_price, city_efficiency,
    highway_efficiency, payment_delivery_status, contracted_at, payed_at,
    delivery_started_at, delivery_ended_at, created_at, updated_at
)
VALUES
    (
        '아반떼 N', '세단', 2023, '2023-01', 5000, '전륜구동',
        1598, 25000000, '퍼포먼스 블루', '블랙', 5,
        '가솔린', '자동8단', 1, '서울 강남구', 9.5,
        12.5, 'avante_n.jpg', 28000000, 3000000, '12가3456',
        0.1, 0.05, 24500000, 11.5, 13.5, 0,
        NULL, NULL, NULL, NULL,
        NOW(), NOW()
    ),
    (
        '그랜저 IG', '세단', 2022, '2022-06', 15000, '전륜구동',
        3342, 35000000, '미드나잇 블랙', '브라운', 5,
        '가솔린', '자동8단', 1, '서울 서초구', 9.2,
        9.5, 'grandeur_ig.jpg', 40000000, 5000000, '34나5678',
        0.2, 0.08, 34500000, 8.5, 11.2, 0,
        NULL, NULL, NULL, NULL,
        NOW(), NOW()
    ),
    (
        '투싼 NX4', 'SUV', 2023, '2023-03', 8000, '4륜구동',
        1998, 28000000, '크리미 화이트', '그레이', 5,
        '디젤', '자동8단', 1, '경기 성남시', 9.0,
        12.0, 'tucson_nx4.jpg', 32000000, 4000000, '56다7890',
        0.0, 0.03, 27500000, 11.0, 13.8, 0,
        NULL, NULL, NULL, NULL,
        NOW(), NOW()
    ),
    (
        '카니발 KA4', '승합', 2022, '2022-09', 20000, '전륜구동',
        3342, 45000000, '아스트라 블루', '베이지', 7,
        '디젤', '자동8단', 1, '인천 연수구', 8.8,
        10.5, 'carnival_ka4.jpg', 50000000, 5000000, '78라1234',
        0.15, 0.07, 44000000, 9.5, 12.5, 0,
        NULL, NULL, NULL, NULL,
        NOW(), NOW()
    ),
    (
        'K5 3세대', '세단', 2023, '2023-02', 10000, '전륜구동',
        1998, 27000000, '인터스텔라 그레이', '블랙', 5,
        'LPG', '자동8단', 1, '대전 유성구', 9.3,
        11.0, 'k5_3rd.jpg', 30000000, 3000000, '90마5678',
        0.05, 0.04, 26500000, 10.5, 12.8, 0,
        NULL, NULL, NULL, NULL,
        NOW(), NOW()
    );