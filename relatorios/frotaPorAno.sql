select marc_mode, min(ano_fabr), max(ano_fabr), sum(quan_veic) quan
 from frota.frota
group by marc_mode
order by sum(quan_veic) desc;