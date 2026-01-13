USE asese;

-- USERS
START TRANSACTION;
REPLACE users (id, firstname, surname, rol, email, email_token, passw) VALUES
(1,'Admin','Root','ADMIN','admin1@asese.com','TK001', PASSWORD('1234')),
(2,'Admin','Sec','ADMIN','admin2@asese.com','TK002', PASSWORD('1234')),
(3,'Juan','Perez','USER','user1@asese.com','TK003', PASSWORD('1234')),
(4,'Maria','Gomez','USER','user2@asese.com','TK004', PASSWORD('1234')),
(5,'Luis','Martinez','USER','user3@asese.com','TK005', PASSWORD('1234')),
(6,'Ana','Lopez','USER','user4@asese.com','TK006', PASSWORD('1234')),
(7,'Pedro','Ramirez','USER','user5@asese.com','TK007', PASSWORD('1234')),
(8,'Sofia','Diaz','USER','user6@asese.com','TK008', PASSWORD('1234')),
(9,'Carlos','Ruiz','USER','user7@asese.com','TK009', PASSWORD('1234')),
(10,'Laura','Fernandez','USER','user8@asese.com','TK010', PASSWORD('1234')),
(11,'Diego','Torres','USER','user9@asese.com','TK011', PASSWORD('1234')),
(12,'Elena','Castro','USER','user10@asese.com','TK012', PASSWORD('1234')),
(13,'SUGAM','POUDEL','USER','schoolsugamdam21@gmail.com','kdgc errk iyno wqub', PASSWORD('1234')),
(14,'MICHAEL','JARAMILLO','ADMIN','micjaramillop@gmail.com','igrw haah thyo cmaa', PASSWORD('1234')),
(15,'HUGO','ANGUIANO','USER','hugoanguiano0327@gmail.com','oltz eqsb ooyq pgmy', PASSWORD('1234'));

-- PROJECTS
REPLACE projects (id, full_name) VALUES
(1,'Proyecto 01'),
(2,'Proyecto 02'),
(3,'Proyecto 03'),
(4,'Proyecto 04'),
(5,'Proyecto 05'),
(6,'Proyecto 06'),
(7,'Proyecto 07'),
(8,'Proyecto 08'),
(9,'Proyecto 09'),
(10,'Proyecto 10'),
(11,'Proyecto 11'),
(12,'Proyecto 12'),
(13,'Proyecto 13'),
(14,'Proyecto 14'),
(15,'Proyecto 15');

-- CONTRACTORS
REPLACE contractors (id, full_name, addres) VALUES
(1,'Contratista 01','Direccion 01'),
(2,'Contratista 02','Direccion 02'),
(3,'Contratista 03','Direccion 03'),
(4,'Contratista 04','Direccion 04'),
(5,'Contratista 05','Direccion 05'),
(6,'Contratista 06','Direccion 06'),
(7,'Contratista 07','Direccion 07'),
(8,'Contratista 08','Direccion 08'),
(9,'Contratista 09','Direccion 09'),
(10,'Contratista 10','Direccion 10'),
(11,'Contratista 11','Direccion 11'),
(12,'Contratista 12','Direccion 12'),
(13,'Contratista 13','Direccion 13'),
(14,'Contratista 14','Direccion 14'),
(15,'Contratista 15','Direccion 15');

-- STAGES
REPLACE stages (
    id, id_project, full_name, descripcion, initial_date, final_date,
    permissionCount, initial_delivery_count, final_delivery_count
) VALUES
(1,1,'Etapa 01','Descripcion 01','2025-01-01','2025-01-31',1,1,1),
(2,2,'Etapa 02','Descripcion 02','2025-02-01','2025-02-28',1,1,0),
(3,3,'Etapa 03','Descripcion 03','2025-03-01','2025-03-31',2,1,1),
(4,4,'Etapa 04','Descripcion 04','2025-04-01','2025-04-30',1,0,0),
(5,5,'Etapa 05','Descripcion 05','2025-05-01','2025-05-31',3,2,1),
(6,6,'Etapa 06','Descripcion 06','2025-06-01','2025-06-30',1,1,1),
(7,7,'Etapa 07','Descripcion 07','2025-07-01','2025-07-31',2,1,0),
(8,8,'Etapa 08','Descripcion 08','2025-08-01','2025-08-31',1,1,1),
(9,9,'Etapa 09','Descripcion 09','2025-09-01','2025-09-30',2,2,1),
(10,10,'Etapa 10','Descripcion 10','2025-10-01','2025-10-31',1,1,0),
(11,11,'Etapa 11','Descripcion 11','2025-11-01','2025-11-30',1,0,0),
(12,12,'Etapa 12','Descripcion 12','2025-12-01','2025-12-31',3,2,1),
(13,13,'Etapa 13','Descripcion 13','2026-01-01','2026-01-31',1,1,1),
(14,14,'Etapa 14','Descripcion 14','2026-02-01','2026-02-28',2,1,0),
(15,15,'Etapa 15','Descripcion 15','2026-03-01','2026-03-31',1,1,1);

-- DELIVERIES
REPLACE deliveries (id, material, descripcion, id_stage, tipo_entrega) VALUES
(1,'Material 01','Desc 01',1,'INITIAL'),
(2,'Material 02','Desc 02',2,'INITIAL'),
(3,'Material 03','Desc 03',3,'FINAL'),
(4,'Material 04','Desc 04',4,'INITIAL'),
(5,'Material 05','Desc 05',5,'FINAL'),
(6,'Material 06','Desc 06',6,'INITIAL'),
(7,'Material 07','Desc 07',7,'FINAL'),
(8,'Material 08','Desc 08',8,'INITIAL'),
(9,'Material 09','Desc 09',9,'FINAL'),
(10,'Material 10','Desc 10',10,'INITIAL'),
(11,'Material 11','Desc 11',11,'FINAL'),
(12,'Material 12','Desc 12',12,'INITIAL'),
(13,'Material 13','Desc 13',13,'FINAL'),
(14,'Material 14','Desc 14',14,'INITIAL'),
(15,'Material 15','Desc 15',15,'FINAL');

-- CREW

REPLACE crew (id, full_name, job_type, id_contractor, id_project) VALUES
(1,'Empleado 01','Ingeniero',1,1),
(2,'Empleado 02','Arquitecto',2,2),
(3,'Empleado 03','Tecnico',3,3),
(4,'Empleado 04','Tecnico',4,4),
(5,'Empleado 05','Ingeniero',5,5),
(6,'Empleado 06','Arquitecto',6,6),
(7,'Empleado 07','Tecnico',7,7),
(8,'Empleado 08','Ingeniero',8,8),
(9,'Empleado 09','Arquitecto',9,9),
(10,'Empleado 10','Tecnico',10,10),
(11,'Empleado 11','Ingeniero',11,11),
(12,'Empleado 12','Arquitecto',12,12),
(13,'Empleado 13','Tecnico',13,13),
(14,'Empleado 14','Ingeniero',14,14),
(15,'Empleado 15','Arquitecto',15,15);

-- PROJECT_ACCESS
REPLACE project_access (id_project, id_user) VALUES
(1,1),
(2,2),
(3,3),
(4,4),
(5,5),
(6,6),
(7,7),
(8,8),
(9,9),
(10,10),
(11,11),
(12,12),
(13,13),
(14,14),
(15,15);

-- PERMISSIONS
REPLACE permissions (id, id_stage, full_name, descripcion) VALUES
(1,1,'Permiso 01','Desc'),
(2,2,'Permiso 02','Desc'),
(3,3,'Permiso 03','Desc'),
(4,4,'Permiso 04','Desc'),
(5,5,'Permiso 05','Desc'),
(6,6,'Permiso 06','Desc'),
(7,7,'Permiso 07','Desc'),
(8,8,'Permiso 08','Desc'),
(9,9,'Permiso 09','Desc'),
(10,10,'Permiso 10','Desc'),
(11,11,'Permiso 11','Desc'),
(12,12,'Permiso 12','Desc'),
(13,13,'Permiso 13','Desc'),
(14,14,'Permiso 14','Desc'),
(15,15,'Permiso 15','Desc');

-- LOGS
REPLACE logs (id, date_log, description, id_user) VALUES
(1,'2025-01-01','Log 01',1),
(2,'2025-01-02','Log 02',2),
(3,'2025-01-03','Log 03',3),
(4,'2025-01-04','Log 04',4),
(5,'2025-01-05','Log 05',5),
(6,'2025-01-06','Log 06',6),
(7,'2025-01-07','Log 07',7),
(8,'2025-01-08','Log 08',8),
(9,'2025-01-09','Log 09',9),
(10,'2025-01-10','Log 10',10),
(11,'2025-01-11','Log 11',11),
(12,'2025-01-12','Log 12',12),
(13,'2025-01-13','Log 13',13),
(14,'2025-01-14','Log 14',14),
(15,'2025-01-15','Log 15',15);

-- COURSES
REPLACE courses (id_project, id_stage) VALUES
(1,1),
(2,2),
(3,3),
(4,4),
(5,5),
(6,6),
(7,7),
(8,8),
(9,9),
(10,10),
(11,11),
(12,12),
(13,13),
(14,14),
(15,15);

-- WHITELIST
REPLACE whitelist (email) VALUES
('julianc2b@gmail.com'),
('emoyano@fundacionloyola.es'),
('mamaldonado@fundacionloyola.es');

COMMIT;