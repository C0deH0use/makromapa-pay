DELETE FROM product
    WHERE reasons = 'USE';

INSERT INTO product(reasons, points, name, description, created, last_updated)
VALUES ('PURCHASE', 0, 'sub_premium', 'Purchase product for MakroMapa PREMIUM content for one month', now(), now()),
       ('PURCHASE', 0, 'ads_removal', 'Purchase product to remove ads for lifetime', now(), now()),
       ('USE', 400, 'DISABLE_ADS', 'Disable Ads in MakroMapa for one week', now(), now());


