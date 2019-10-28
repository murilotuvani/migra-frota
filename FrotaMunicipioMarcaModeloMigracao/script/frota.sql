/**
 * Author:  murilo
 * Created: 28/10/2019
 */
create database frota;

use frota;

create table frota (
frot_it bigint unsigned,
uf char(30),
muni varchar(255),
marc_mode varchar(1024),
ano_fabr int,
quan_veic decimal(10,2)
) engine=MyISAM;
