CREATE USER 'chatbot_ro'@'%' IDENTIFIED BY 'chatbot_ro_pass';
GRANT SELECT ON agua_db.Tb_Tipos_Servicios TO 'chatbot_ro'@'%';
GRANT SELECT ON agua_db.Tb_Tarifas TO 'chatbot_ro'@'%';
