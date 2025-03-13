CREATE OR REPLACE PROCEDURE insert_properties()
LANGUAGE plpgsql
AS $$
DECLARE
    landlord_1 UUID;
    landlord_2 UUID;
    landlord_3 UUID;
    user_1 UUID;
    user_2 UUID;
    property_1 UUID;
    property_2 UUID;
    property_3 UUID;
    property_4 UUID;
    property_5 UUID;
    property_6 UUID;
    property_7 UUID;
    property_8 UUID;
    sch_1 UUID;
    sch_2 UUID;
    sch_3 UUID;
    sch_4 UUID;
    sch_5 UUID;
    sch_6 UUID;
    sch_7 UUID;
    sch_8 UUID;
    sch_9 UUID;
    sch_10 UUID;
BEGIN
    --initializing accounts ids
    landlord_1 := gen_random_uuid();
    landlord_2 := gen_random_uuid();
    landlord_3 := gen_random_uuid();
    user_1 := gen_random_uuid();
    user_2 := gen_random_uuid();

    --initializing properties ids
    property_1 := gen_random_uuid();
    property_2 := gen_random_uuid();
    property_3 := gen_random_uuid();
    property_4 := gen_random_uuid();
    property_5 := gen_random_uuid();
    property_6 := gen_random_uuid();
    property_7 := gen_random_uuid();
    property_8 := gen_random_uuid();

    --initializing schedules ids
    sch_1 := gen_random_uuid();
    sch_2 := gen_random_uuid();
    sch_3 := gen_random_uuid();
    sch_4 := gen_random_uuid();
    sch_5 := gen_random_uuid();
    sch_6 := gen_random_uuid();
    sch_7 := gen_random_uuid();
    sch_8 := gen_random_uuid();
    sch_9 := gen_random_uuid();
    sch_10 := gen_random_uuid();

    --INSERT LANDLORDS AND USERS
    INSERT INTO accounts(
        id, email, names, last_names, number,
        is_active, role, status, created_at, updated_at, hash
    ) VALUES
        (landlord_1, 'landlord1@gmail.com', 'land 1', 'lord', '1234567891', true, 'LANDLORD', 0, NOW(), NOW(), 'WlcB5u4PYsRZxFsskEdg4A=='),
        (landlord_2, 'landlord2@gmail.com', 'land 2', 'lord', '1234567891', true, 'LANDLORD', 0, NOW(), NOW(), 'WlcB5u4PYsRZxFsskEdg4A=='),
        (landlord_3, 'landlord3@gmail.com', 'land 3', 'lord', '1234567891', true, 'LANDLORD', 0, NOW(), NOW(), 'WlcB5u4PYsRZxFsskEdg4A=='),
        (user_1, 'user1@gmail.com', 'user 1', 'xd', '1234567891', true, 'USER', 0, NOW(), NOW(), 'WlcB5u4PYsRZxFsskEdg4A=='),
        (user_2, 'user2@gmail.com', 'user 2', 'xd', '1234567891', true, 'USER', 0, NOW(), NOW(), 'WlcB5u4PYsRZxFsskEdg4A==');


    --INSERT PROPERTIES
    INSERT INTO properties (
        id, name, department, enter_type, 
        description, number_rooms, number_bathrooms, 
        is_pet_friendly, has_pool, has_asador, 
        night_price, account_id, status, created_at, updated_at
    ) VALUES 
        (property_1, 'Finca El Paraíso', 'Meta', 'Alquiler completo', 'Hermosa finca rodeada de naturaleza y tranquilidad.', 5, 3, true, true, true, 350000, landlord_1, 0, NOW(), NOW()),
        (property_2, 'Hacienda San Rafael', 'Casanare', 'Alquiler por habitación', 'Una hacienda ganadera con historia y confort.', 8, 5, true, false, true, 500000, landlord_1, 0, NOW(), NOW()),
        (property_3, 'Estancia La Esperanza', 'Antioquia', 'Alquiler completo', 'Amplia estancia con vista a las montañas.', 6, 4, true, true, false, 420000, landlord_1, 0, NOW(), NOW()),
        (property_4, 'Villa Campestre', 'Cundinamarca', 'Alquiler completo', 'Un lugar ideal para escapadas familiares.', 4, 2, true, false, true, 300000, landlord_2, 0, NOW(), NOW()),
        (property_5, 'Finca Los Pinos', 'Santander', 'Alquiler por habitación', 'Finca tradicional con amplios jardines.', 7, 5, false, true, false, 380000, landlord_2, 0, NOW(), NOW()),
        (property_6, 'Refugio del Río', 'Tolima', 'Alquiler completo', 'Casa de campo junto al río, perfecta para relajarse.', 5, 3, true, false, true, 280000, landlord_3, 0, NOW(), NOW()),
        (property_7, 'Hacienda El Dorado', 'Boyacá', 'Alquiler completo', 'Tradicional hacienda con caballerizas.', 10, 6, true, true, true, 600000, landlord_3, 0, NOW(), NOW()),
        (property_8, 'Casa de Campo La Pradera', 'Valle del Cauca', 'Alquiler por habitación', 'Pequeña finca con huerta orgánica.', 3, 2, true, false, false, 250000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Finca Los Girasoles', 'Huila', 'Alquiler completo', 'Colorida finca con vista a los cafetales.', 6, 4, true, true, true, 400000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Hacienda Santa Isabel', 'Nariño', 'Alquiler por habitación', 'Hacienda colonial con arquitectura rústica.', 9, 6, false, true, true, 520000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Estancia El Nogal', 'Quindío', 'Alquiler completo', 'Acogedora finca en la zona cafetera.', 5, 3, true, false, true, 350000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Finca La Cascada', 'Putumayo', 'Alquiler completo', 'Ubicada cerca de una cascada natural.', 4, 2, false, true, false, 270000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Casa Rural El Bosque', 'Cauca', 'Alquiler por habitación', 'Finca ecológica con senderos naturales.', 7, 5, true, true, false, 390000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Villa Serrana', 'Cesar', 'Alquiler completo', 'Casa de campo con una vista impresionante.', 8, 5, true, false, true, 480000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Refugio del Lago', 'Amazonas', 'Alquiler completo', 'Finca rodeada de naturaleza y lagunas.', 6, 4, true, true, true, 500000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Estancia Monte Verde', 'Magdalena', 'Alquiler completo', 'Gran finca con cultivos de frutas.', 9, 6, false, true, true, 550000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Finca Tierra Fértil', 'Arauca', 'Alquiler completo', 'Finca ganadera con zona de camping.', 5, 3, true, false, true, 320000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Hacienda Las Palmeras', 'Guaviare', 'Alquiler por habitación', 'Elegante hacienda con ambiente campestre.', 7, 5, false, true, false, 450000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Villa del Sol', 'La Guajira', 'Alquiler completo', 'Casa rural con piscina y solárium.', 6, 4, true, true, true, 420000, landlord_3, 0, NOW(), NOW()),
        (gen_random_uuid(), 'Finca El Mirador', 'Chocó', 'Alquiler completo', 'Finca con vistas al mar y la selva.', 8, 5, true, false, true, 510000, landlord_3, 0, NOW(), NOW());


    --INSERT SCHEDULINGS
    INSERT INTO schedules(
        id, number_persons, price, sc_status,
        start_date, end_date, property_id, user_id, 
        created_at, updated_at, status
    )VALUES
        (sch_1, 7, 360000, 'PAID', '2025-03-10', '2025-03-29', property_1, user_1, NOW(), NOW(), 0),        --this should be in course
        (sch_2, 4, 178000, 'PAID', '2025-02-10', '2025-02-28', property_2, user_1, NOW(), NOW(), 0),        --this should be completed
        (sch_3, 9, 417552, 'RATED', '2024-10-10', '2024-10-15', property_5, user_2, NOW(), NOW(), 0),
        (sch_4, 9, 105599, 'RATED', '2024-09-10', '2024-09-15', property_5, user_1, NOW(), NOW(), 0),
        (sch_5, 14, 150000, 'RATED', '2024-08-10', '2024-08-15', property_5, user_1, NOW(), NOW(), 0),
        (sch_6, 20, 150000, 'RATED', '2024-08-10', '2024-08-15', property_4, user_2, NOW(), NOW(), 0),
        (sch_7, 17, 150000, 'RATED', '2024-07-10', '2024-07-15', property_4, user_1, NOW(), NOW(), 0),
        (sch_8, 17, 150000, 'RATED', '2024-06-10', '2024-06-15', property_3, user_1, NOW(), NOW(), 0),
        (sch_9, 11, 12000, 'RATED', '2024-06-16', '2024-06-21', property_3, user_2, NOW(), NOW(), 0),
        (sch_10, 11, 470000, 'RATED', '2024-05-16', '2024-05-21', property_8, user_2, NOW(), NOW(), 0),
        (gen_random_uuid(), 10, 740000, 'DENIED', '2024-04-16', '2024-04-21', property_8, user_1, NOW(), NOW(), 0),
        (gen_random_uuid(), 7, 471000, 'REQUESTED', '2025-04-16', '2025-04-21', property_1, user_1, NOW(), NOW(), 0),
        (gen_random_uuid(), 7, 471000, 'REQUESTED', '2025-05-16', '2025-05-21', property_1, user_2, NOW(), NOW(), 0),
        (gen_random_uuid(), 7, 471000, 'REQUESTED', '2025-06-16', '2025-07-21', property_2, user_2, NOW(), NOW(), 0),
        (gen_random_uuid(), 7, 748000, 'APPROVED', '2025-07-16', '2025-08-21', property_2, user_2, NOW(), NOW(), 0);

    --INSERT PAYMENTS
    INSERT INTO payments(
        id, account_number, bank, value ,schedule_id ,created_at
    )VALUES
        (gen_random_uuid(), 1165454, 'Bancolombia', 14220,sch_1, NOW()),
        (gen_random_uuid(), 2500303, 'Davivienda', 471100,sch_2, NOW());

    --INSERT RATINGS
    INSERT INTO ratings(
        id, comment, rating, type, schedule_id, created_at, status
    )VALUES
        (gen_random_uuid(), 'Todo en general bien', 4, 'LANDLORD', sch_2, NOW(),0),       --only 1 rating, for the landlord, for this sch
        (gen_random_uuid(), 'mal cliente', 1, 'USER', sch_3, NOW(), 0),
        (gen_random_uuid(), 'la finca no estaba tan linda', 2, 'LANDLORD', sch_3, NOW(), 0),
        (gen_random_uuid(), 'Muy ordneado', 5, 'USER', sch_4, NOW(), 0),
        (gen_random_uuid(), 'Casa muy linda', 3, 'LANDLORD', sch_4, NOW(), 0),
        (gen_random_uuid(), 'Normal', 4, 'USER', sch_5, NOW(), 0),
        (gen_random_uuid(), 'La mejor finca de mi vida', 5, 'LANDLORD', sch_5, NOW(), 0),
        (gen_random_uuid(), 'bien bien', 3, 'USER', sch_6, NOW(), 0),
        (gen_random_uuid(), 'Muy amplia la casa', 5, 'LANDLORD', sch_6, NOW(), 0),
        (gen_random_uuid(), 'Espectacular cliente', 5, 'USER', sch_7, NOW(), 0),
        (gen_random_uuid(), 'Un poco sucia, piscina descuidada', 4, 'LANDLORD', sch_7, NOW(), 0),
        (gen_random_uuid(), 'pesmio cliente', 2, 'USER', sch_8, NOW(), 0),
        (gen_random_uuid(), 'MEHHHHHHHH', 2, 'LANDLORD', sch_8, NOW(), 0),
        (gen_random_uuid(), 'pbuen ', 3, 'USER', sch_9, NOW(), 0),
        (gen_random_uuid(), 'muy normalita la finca', 3, 'LANDLORD', sch_9, NOW(), 0),
        (gen_random_uuid(), 'si pues normal realmente ', 4, 'USER', sch_10, NOW(), 0),
        (gen_random_uuid(), 'En general bien', 4, 'LANDLORD', sch_10, NOW(), 0);

END;
$$;

CALL insert_properties();
