CREATE OR REPLACE PROCEDURE insert_properties()
LANGUAGE plpgsql
AS $$
DECLARE
    user_id UUID;
BEGIN
    SELECT id INTO user_id FROM accounts WHERE role='LANDLORD' ORDER BY id LIMIT 1;

    IF user_id IS NOT NULL THEN
        INSERT INTO properties (
            id, name, department, enter_type, 
            description, number_rooms, number_bathrooms, 
            is_pet_friendly, has_pool, has_asador, 
            night_price, account_id, status, created_at, updated_at
        ) VALUES 
            (gen_random_uuid(), 'Finca El Paraíso', 'Meta', 'Alquiler completo', 'Hermosa finca rodeada de naturaleza y tranquilidad.', 5, 3, true, true, true, 350000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Hacienda San Rafael', 'Casanare', 'Alquiler por habitación', 'Una hacienda ganadera con historia y confort.', 8, 5, true, false, true, 500000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Estancia La Esperanza', 'Antioquia', 'Alquiler completo', 'Amplia estancia con vista a las montañas.', 6, 4, true, true, false, 420000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Villa Campestre', 'Cundinamarca', 'Alquiler completo', 'Un lugar ideal para escapadas familiares.', 4, 2, true, false, true, 300000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Finca Los Pinos', 'Santander', 'Alquiler por habitación', 'Finca tradicional con amplios jardines.', 7, 5, false, true, false, 380000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Refugio del Río', 'Tolima', 'Alquiler completo', 'Casa de campo junto al río, perfecta para relajarse.', 5, 3, true, false, true, 280000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Hacienda El Dorado', 'Boyacá', 'Alquiler completo', 'Tradicional hacienda con caballerizas.', 10, 6, true, true, true, 600000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Casa de Campo La Pradera', 'Valle del Cauca', 'Alquiler por habitación', 'Pequeña finca con huerta orgánica.', 3, 2, true, false, false, 250000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Finca Los Girasoles', 'Huila', 'Alquiler completo', 'Colorida finca con vista a los cafetales.', 6, 4, true, true, true, 400000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Hacienda Santa Isabel', 'Nariño', 'Alquiler por habitación', 'Hacienda colonial con arquitectura rústica.', 9, 6, false, true, true, 520000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Estancia El Nogal', 'Quindío', 'Alquiler completo', 'Acogedora finca en la zona cafetera.', 5, 3, true, false, true, 350000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Finca La Cascada', 'Putumayo', 'Alquiler completo', 'Ubicada cerca de una cascada natural.', 4, 2, false, true, false, 270000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Casa Rural El Bosque', 'Cauca', 'Alquiler por habitación', 'Finca ecológica con senderos naturales.', 7, 5, true, true, false, 390000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Villa Serrana', 'Cesar', 'Alquiler completo', 'Casa de campo con una vista impresionante.', 8, 5, true, false, true, 480000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Refugio del Lago', 'Amazonas', 'Alquiler completo', 'Finca rodeada de naturaleza y lagunas.', 6, 4, true, true, true, 500000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Estancia Monte Verde', 'Magdalena', 'Alquiler completo', 'Gran finca con cultivos de frutas.', 9, 6, false, true, true, 550000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Finca Tierra Fértil', 'Arauca', 'Alquiler completo', 'Finca ganadera con zona de camping.', 5, 3, true, false, true, 320000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Hacienda Las Palmeras', 'Guaviare', 'Alquiler por habitación', 'Elegante hacienda con ambiente campestre.', 7, 5, false, true, false, 450000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Villa del Sol', 'La Guajira', 'Alquiler completo', 'Casa rural con piscina y solárium.', 6, 4, true, true, true, 420000, user_id, 0, NOW(), NOW()),
            (gen_random_uuid(), 'Finca El Mirador', 'Chocó', 'Alquiler completo', 'Finca con vistas al mar y la selva.', 8, 5, true, false, true, 510000, user_id, 0, NOW(), NOW());

    ELSE
        RAISE NOTICE 'No user found in the users table.';
    END IF;
END;
$$;

CALL insert_properties();
