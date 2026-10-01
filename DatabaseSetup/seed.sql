-- Smart Pantry Manager: seed data (20 recipes)
-- Ingredient names are deliberately natural (plural/singular mixed);
-- the app's IngredientNormalizer handles the differences.

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Tomato Omelette',
        E'Beat the eggs with the salt.\nChop the tomatoes and onion.\nMelt the butter in a pan and soften the onion and tomatoes for 3 minutes.\nPour in the eggs and cook until set, folding in half.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('eggs', 3, 'pcs'),
        ('tomatoes', 2, 'pcs'),
        ('onion', 1, 'pcs'),
        ('butter', 1, 'tbsp'),
        ('salt', 0.5, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Scrambled Eggs on Toast',
        E'Toast the bread.\nBeat the eggs with the salt.\nMelt the butter in a pan over low heat and stir the eggs gently until just set.\nServe on the toast.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('eggs', 3, 'pcs'),
        ('bread', 2, 'pcs'),
        ('butter', 1, 'tbsp'),
        ('salt', 0.25, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Garlic Butter Pasta',
        E'Boil the pasta in salted water until tender, then drain.\nMince the garlic and fry gently in the butter for 1 minute.\nToss the pasta in the garlic butter and season with salt.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('pasta', 200, 'g'),
        ('garlic', 3, 'pcs'),
        ('butter', 2, 'tbsp'),
        ('salt', 1, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Fresh Tomato Pasta',
        E'Boil the pasta in salted water until tender, then drain.\nChop the tomatoes and mince the garlic.\nHeat the olive oil, fry the garlic, then add the tomatoes and simmer for 10 minutes.\nStir the sauce through the pasta.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('pasta', 200, 'g'),
        ('tomatoes', 4, 'pcs'),
        ('garlic', 2, 'pcs'),
        ('olive oil', 2, 'tbsp'),
        ('salt', 1, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Egg Fried Rice',
        E'Cook the rice and let it cool.\nChop the onion and fry it in the oil for 3 minutes.\nPush the onion aside, scramble the eggs in the pan.\nAdd the rice and soy sauce and stir-fry for 4 minutes.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('rice', 1, 'cup'),
        ('eggs', 2, 'pcs'),
        ('onion', 1, 'pcs'),
        ('soy sauce', 2, 'tbsp'),
        ('vegetable oil', 1, 'tbsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Cheese Toastie',
        E'Butter the outside of both slices of bread.\nPlace the cheese between the slices.\nFry in a pan for 3 minutes per side until golden and melted.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('bread', 2, 'pcs'),
        ('cheese', 60, 'g'),
        ('butter', 1, 'tbsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Fluffy Pancakes',
        E'Whisk the flour, sugar, milk and egg into a smooth batter.\nMelt a little butter in a pan.\nPour in small ladles of batter and cook until bubbles form.\nFlip and cook for another minute.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('flour', 150, 'g'),
        ('milk', 250, 'ml'),
        ('eggs', 1, 'pcs'),
        ('sugar', 1, 'tbsp'),
        ('butter', 1, 'tbsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Banana Oat Porridge',
        E'Simmer the oats and milk for 5 minutes, stirring often.\nSlice the banana.\nTop the porridge with banana and drizzle with honey.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('oats', 50, 'g'),
        ('milk', 250, 'ml'),
        ('bananas', 1, 'pcs'),
        ('honey', 1, 'tbsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Simple Potato Soup',
        E'Peel and cube the potatoes, chop the onion.\nSoften the onion in the butter for 3 minutes.\nAdd potatoes and stock, simmer for 20 minutes.\nBlend until smooth and season with salt.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('potatoes', 4, 'pcs'),
        ('onion', 1, 'pcs'),
        ('vegetable stock', 500, 'ml'),
        ('butter', 1, 'tbsp'),
        ('salt', 1, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Baked Potatoes with Cheese',
        E'Heat the oven to 200C.\nPrick the potatoes, rub with butter and salt.\nBake for 60 minutes until soft.\nSplit open and top with grated cheese.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('potatoes', 2, 'pcs'),
        ('cheese', 50, 'g'),
        ('butter', 1, 'tbsp'),
        ('salt', 0.5, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Chicken Rice Bowl',
        E'Cook the rice.\nSlice the chicken, chop the onion and garlic.\nFry the chicken in the olive oil until golden, add onion and garlic for 3 minutes.\nServe over the rice.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('chicken breast', 300, 'g'),
        ('rice', 1, 'cup'),
        ('onion', 1, 'pcs'),
        ('garlic', 2, 'pcs'),
        ('olive oil', 1, 'tbsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Vegetable Stir-Fry',
        E'Slice the carrots, onion and bell pepper into strips.\nHeat the oil in a wok.\nStir-fry the vegetables for 5 minutes.\nAdd the soy sauce and toss for 1 more minute.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('carrots', 2, 'pcs'),
        ('onion', 1, 'pcs'),
        ('bell peppers', 1, 'pcs'),
        ('soy sauce', 2, 'tbsp'),
        ('vegetable oil', 1, 'tbsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Hearty Lentil Soup',
        E'Dice the carrot and onion, mince the garlic.\nSoften them in a pot for 5 minutes.\nAdd lentils and stock and simmer for 25 minutes until tender.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('lentils', 1, 'cup'),
        ('carrots', 1, 'pcs'),
        ('onion', 1, 'pcs'),
        ('garlic', 2, 'pcs'),
        ('vegetable stock', 750, 'ml')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Tuna Tomato Pasta',
        E'Boil the pasta in salted water, then drain.\nChop the tomatoes and warm them in the olive oil for 5 minutes.\nStir in the tuna and the pasta and heat through.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('pasta', 200, 'g'),
        ('tuna', 150, 'g'),
        ('tomatoes', 2, 'pcs'),
        ('olive oil', 1, 'tbsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('French Toast',
        E'Whisk the eggs, milk, sugar and cinnamon in a shallow dish.\nSoak each slice of bread for 30 seconds per side.\nFry in the butter until golden on both sides.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('bread', 4, 'pcs'),
        ('eggs', 2, 'pcs'),
        ('milk', 100, 'ml'),
        ('sugar', 1, 'tbsp'),
        ('butter', 1, 'tbsp'),
        ('cinnamon', 0.5, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Easy Guacamole',
        E'Halve the avocados and scoop into a bowl, then mash.\nFinely chop the tomato and onion and stir in.\nSqueeze in the lemon juice and season with salt.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('avocados', 2, 'pcs'),
        ('tomatoes', 1, 'pcs'),
        ('onion', 0.5, 'pcs'),
        ('lemons', 1, 'pcs'),
        ('salt', 0.5, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Creamy Mashed Potatoes',
        E'Peel and chop the potatoes, boil for 15 minutes, then drain.\nAdd butter, warm milk and salt.\nMash until smooth.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('potatoes', 4, 'pcs'),
        ('butter', 2, 'tbsp'),
        ('milk', 100, 'ml'),
        ('salt', 0.5, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Cinnamon Rice Pudding',
        E'Combine the rice, milk and sugar in a pot.\nSimmer on low heat for 30 minutes, stirring often.\nStir in the cinnamon and serve warm.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('rice', 0.5, 'cup'),
        ('milk', 500, 'ml'),
        ('sugar', 2, 'tbsp'),
        ('cinnamon', 0.5, 'tsp')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Cheesy Veggie Omelette',
        E'Beat the eggs.\nDice the bell pepper and onion and fry until soft.\nPour in the eggs, sprinkle with cheese and fold when set.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('eggs', 3, 'pcs'),
        ('bell peppers', 1, 'pcs'),
        ('onion', 1, 'pcs'),
        ('cheese', 30, 'g')
) AS v(name, qty, unit);

WITH r AS (
    INSERT INTO recipes (name, steps) VALUES ('Roasted Tomato Soup',
        E'Chop the tomatoes, onion and garlic.\nRoast with the olive oil at 200C for 25 minutes.\nTransfer to a pot with the stock, simmer for 10 minutes and blend.')
    RETURNING id
)
INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.qty, v.unit
FROM r, (VALUES
        ('tomatoes', 6, 'pcs'),
        ('onion', 1, 'pcs'),
        ('garlic', 2, 'pcs'),
        ('vegetable stock', 500, 'ml'),
        ('olive oil', 1, 'tbsp')
) AS v(name, qty, unit);