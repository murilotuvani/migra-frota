/**
 * Author:  murilo
 * Created: 28/10/2019
 */
drop database frota;
create database frota;

use frota;

create table frota (
frot_it BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
ano_mes int(6),
uf char(30),
muni varchar(100),
marc_mode varchar(1024),
ano_fabr int,
quan_veic decimal(10,2),
PRIMARY KEY (`frot_it`),
INDEX idx_frot_ano_mes (ano_mes),
INDEX idx_frot_uf (uf),
INDEX idx_frot_muni (muni)
) engine=MyISAM;
