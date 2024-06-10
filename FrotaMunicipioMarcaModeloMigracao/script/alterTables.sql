alter table frota add index ix_uf (uf);
alter table frota modify column muni char(32);
alter table frota add index ix_muni (muni);