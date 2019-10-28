package com.distsys.denatram.frota;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 25/10/2019 17:30:33
 *
 * @author murilo
 */
public class Migrar {

    public static void main(String args[]) throws SQLException, ClassNotFoundException {
        LocalDateTime antes = LocalDateTime.now();

        Class.forName("com.mysql.jdbc.Driver");
        Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/frota?useSSL=false", "root", "root");

        String truncate = System.getProperty("truncate");
        if (truncate != null) {
            Statement tstmt = myConn.createStatement();
            boolean execute = tstmt.execute("truncate frota");
            tstmt.close();
        }

        PreparedStatement myStmt = myConn.prepareStatement("insert into frota.frota (frot_it,uf,muni,marc_mode,ano_fabr,quan_veic) values (?,?,?,?,?,?)");

        try ( //C:\Frota\MuMaMod.mdb :: Frota
                //Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/__tmp/test/zzz.accdb");
                Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Frota/MuMaMod.mdb")) {
            Statement s = conn.createStatement();

//            s.executeUpdate("DELETE FROM Frota WHERE [Campo1]!='SAO PAULO'");
//        //ResultSet rs = s.executeQuery("SELECT [Campo1], [Campo2] FROM Frota WHERE [Campo1]='ACRE'");
//        ResultSet rs = s.executeQuery("SELECT [Campo1], [Campo2] FROM Frota WHERE [Campo1]='SAO PAULO'");
            String cidade = (args.length > 0 ? args[0] : "ITU");
            String query = "SELECT * FROM Frota WHERE [Campo1]='SAO PAULO' AND [Campo2]='" + cidade + "'";
            ResultSet rs = s.executeQuery(query);
            int i = 0;
            while (rs.next()) {
                i++;
                long identificacao = rs.getLong(1);
                int j = 1;
                if (rs.wasNull()) {
                    myStmt.setNull(i, j);
                } else {
                    myStmt.setLong(j++, identificacao);
                }

                String uf = rs.getString(2);
                myStmt.setString(j++, uf);

                String muni = rs.getString(3);
                myStmt.setString(j++, muni);

                String marcaModelo = rs.getString(4);
                myStmt.setString(j++, marcaModelo);

                String aux = rs.getString(5);
                if (rs.wasNull() || aux.startsWith("Sem")) {
                    myStmt.setNull(j++, java.sql.Types.INTEGER);
                } else {
                    int ano = 0;
                    try {
                        ano = Integer.parseInt(aux);
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Erro de formatacao", ex);
                    }
                    myStmt.setInt(j++, ano);
                }

                double quantidade = rs.getDouble(6);
                if (rs.wasNull()) {
                    myStmt.setNull(j++, java.sql.Types.BIGINT);
                } else {
                    myStmt.setBigDecimal(j++, new BigDecimal(quantidade));
                }

                myStmt.addBatch();

                if ((i % 100) == 0) {
                    int registros = sum(myStmt.executeBatch());
                    System.out.println(registros + " registros inseridos");
                }
            }
            rs.close();
            s.close();

            if ((i % 100) >= 0) {
                int registros = sum(myStmt.executeBatch());
                System.out.println(registros + " registros inseridos");
            }
        }
        myStmt.close();
        myConn.close();

        LocalDateTime depois = LocalDateTime.now();
        Duration duration = Duration.between(antes, depois);
        System.out.println(duration.getSeconds() + " segundos");
    }

    private static int sum(int[] vetor) {
        int soma = 0;
        for (int posicao = 0; posicao < vetor.length; posicao++) {
            soma += vetor[posicao];
        }
        return soma;
    }

}
