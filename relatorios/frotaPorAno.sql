select marc_mode, min(ano_fabr), max(ano_fabr), sum(quan_veic) quan
 from frota.frota
group by marc_mode
order by sum(quan_veic) desc;

select uf, muni, count(1)
 from frota.frota
where ano_fabr between 2000 and 2023
   and muni in ('Alambari', 
'Alumínio',
'Araçariguama',
'Araçoiaba da Serra',
'Boituva',
'Cabreúva',
'Campinas',
'Capela do Alto',
'Capivari',
'Cerquilho',
'Cesário Lange',
'Conchas',
'Elias Fausto',
'Ibiúna',
'Indaiatuba',
'Iperó',
'Itapetininga',
'Itu',
'Itupeva',
'Jumirim',
'Jundiaí',
'Laranjal Paulista',
'Mairinque',
'Mombuca',
'Monte Mor',
'Pereiras',
'Piedade',
'Pilar do Sul',
'Piracicaba',
'Porto Feliz',
'Rafard',
'Rio das Pedras',
'Salto',
'Salto de Pirapora',
'São Miguel Arcanjo',
'São Paulo',
'São Roque',
'Sorocaba',
'Tapiraí',
'Tatuí',
'Tietê',
'Votorantim')
group by uf, muni;


-- Com modelos
select marc_mode, min(ano_fabr), max(ano_fabr), sum(quan_veic) quan, count(1)
  from frota.frota
where ano_fabr between 2000 and 2023
   and muni in (
'Boituva',
'Cabreúva',
'Cerquilho',
'Elias Fausto',
'Indaiatuba',
'Iperó',
'Itu',
'Jumirim',
'Laranjal Paulista',
'Mairinque',
'Piedade',
'Porto Feliz',
'Salto',
'Sorocaba',
'Tatuí',
'Tietê')
group by uf, muni, marc_mode
limit 5000000;
