-- Insertion des options
INSERT INTO options (nom, description) VALUES 
('Data Science', 'Option spécialisée en science des données et intelligence artificielle'),
('SAE', 'Systèmes et Applications Embarqués'),
('Génie Logiciel', 'Développement et architecture logicielle'),
('Réseaux et Sécurité', 'Administration réseaux et cybersécurité');

-- Insertion des classes pour l'option Data Science (id=1)
INSERT INTO classes (nom, description, option_id) VALUES 
('4DS1', 'Quatrième année Data Science - Groupe 1', 1),
('4DS2', 'Quatrième année Data Science - Groupe 2', 1),
('5DS1', 'Cinquième année Data Science - Groupe 1', 1),
('5DS2', 'Cinquième année Data Science - Groupe 2', 1);

-- Insertion des classes pour l'option SAE (id=2)
INSERT INTO classes (nom, description, option_id) VALUES 
('4SAE1', 'Quatrième année SAE - Groupe 1', 2),
('4SAE2', 'Quatrième année SAE - Groupe 2', 2),
('5SAE1', 'Cinquième année SAE - Groupe 1', 2),
('5SAE2', 'Cinquième année SAE - Groupe 2', 2);

-- Insertion des classes pour l'option Génie Logiciel (id=3)
INSERT INTO classes (nom, description, option_id) VALUES 
('4GL1', 'Quatrième année Génie Logiciel - Groupe 1', 3),
('4GL2', 'Quatrième année Génie Logiciel - Groupe 2', 3),
('5GL1', 'Cinquième année Génie Logiciel - Groupe 1', 3),
('5GL2', 'Cinquième année Génie Logiciel - Groupe 2', 3);

-- Insertion des classes pour l'option Réseaux et Sécurité (id=4)
INSERT INTO classes (nom, description, option_id) VALUES 
('4RS1', 'Quatrième année Réseaux et Sécurité - Groupe 1', 4),
('4RS2', 'Quatrième année Réseaux et Sécurité - Groupe 2', 4),
('5RS1', 'Cinquième année Réseaux et Sécurité - Groupe 1', 4),
('5RS2', 'Cinquième année Réseaux et Sécurité - Groupe 2', 4);
