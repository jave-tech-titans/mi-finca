DROP TABLE accounts, files, payments, pending_creations, properties, ratings, schedules, sessions CASCADE;


DROP PROCEDURE insert_properties;



CREATE OR REPLACE PROCEDURE clean_tables()
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    DELETE FROM ratings;
    DELETE FROM pending_creations;
    DELETE FROM payments;
    DELETE FROM files;
    DELETE FROM schedules;
    DELETE FROM properties;
    DELETE FROM sessions;
    DELETE FROM accounts;
END;
$$;