insert into fin_pais (id, nombre, codigo) values (1, 'Argentina','AR');
insert into fin_mercado (id, nombre, codigo, pais_id) values (1, 'BCBA','BCBA',1);
insert into fin_instrumento (id, nombre, ticker, precio, mercado_id) values (1, 'YPF', 'YPFD', 610.5, 1);
insert into fin_broker (id, codigo, nombre) values (1, 'IOL','Invertir Online');
insert into fin_cuenta (id, saldo, numero) values (1, 3852.16, 'ABC00001');
insert into fin_cartera (id, broker_id, cuenta_id) values (1, 1, 1);
insert into fin_activo (id, cartera_id, instrumento_id, ppc, tenencia) values (1, 1, 1, 610.50, 10);

insert into fin_cargo (id, iva, tasa, tipo, broker_id) values (1, 0.21, 0.000799, 'D', 1);
insert into fin_cargo (id, iva, tasa, tipo, broker_id) values (2, 0.21, 0.005, 'C', 1);

insert into fin_movimiento_activo
(id, activo_id, cartera_id, instrumento_id, cantidad, fecha, precio, tipo, cargos) values
(1, 1, 1, 1, 10, '2021-01-04', 610.50, 'C', 42.843);

insert into fin_cargo_movimiento (id, monto, cargo_id, movimiento_id) values (1, 5.908, 1, 1);
insert into fin_cargo_movimiento (id, monto, cargo_id, movimiento_id) values (2, 36.935, 2, 1);
insert into fin_movimiento_cuenta (id, cuenta_id, monto, tipo, fecha) values (1, 1, 10000, 'D', '2021-01-04');