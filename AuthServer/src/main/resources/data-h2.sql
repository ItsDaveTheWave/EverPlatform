INSERT INTO oauth_client_details (client_id, client_secret, web_server_redirect_uri, scope, access_token_validity, refresh_token_validity, resource_ids, authorized_grant_types, additional_information) VALUES 
	('web', '{bcrypt}$2a$10$uc9J2qrtkKzdqhCfuL2y1etM4AoHWTTEfk80Q5p7/O850K/bu9I.y', 'http://localhost:8080/code', 'READ,WRITE', '3600', '10000', 'course,assignment,homework,user,course-aggregator', 'authorization_code,password,refresh_token,implicit', '{}');

/* web-webpass */

 INSERT INTO role (NAME) VALUES
	('ROLE_admin'),
	('ROLE_student'),
	('ROLE_teacher');
     
insert into user (id, username,password, email, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked) VALUES 
	('1', 'admin','{bcrypt}$2a$10$FFreamGuf1GAGz8ihggKi.kExZ5o0ALTJMhERvlwCHWBdnMI3qlP.', 'admin@gmail.com', '1', '1', '1', '1'),
	('2', 'julioprofe','{bcrypt}$2a$10$sVDucc56L.l7l5CKq2VZ9O7OCxxABefKKdSzu0SvK5pLJk2LhweU2', 'julioprofe@gmail.com', '1', '1', '1', '1'),
	('3', 'pepe','{bcrypt}$2a$10$IIjzOES.20Uhm2fqXYqtiuE38T8UNi9jjtCNlzSalWbwdqbK6XZSm', 'pepe@gmail.com', '1', '1', '1', '1');

/* admin-admin */
/* julioprofe-profepass */
/* pepe-pepepass */

INSERT INTO ROLE_USER (ROLE_ID, USER_ID) VALUES
    (1, 1), /* admin-admin */
    (3, 2), /* teacher-julioprofe */
    (2, 3); /* student-pepe */ 