INSERT INTO BAND (name, region, pricePerHour, rate) VALUES('Eufory', 3, 200000.00, 7.4);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Eufory'), 3);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Eufory'), 2);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Eufory'), 4);

INSERT INTO BAND (name, region, pricePerHour, rate) VALUES('Kabat', 4, 600000.00, 6.4);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Kabat'), 3);

INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Ondrej Hnedy','+421 922 222 222', 'Razusova 8');
INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Petr Zeleny','+420 922 354 284', 'Hodzova 4');
INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Filip Maly','+421 922 448 336', 'Bohradova 12');