package com.distsys.denatram.frota;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * 27/12/2019 20:57:28
 *
 * @author murilo
 */
public class MigrarTxt {

    private static final String DATABASE = "frota";
    private static final Pattern PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    private final BufferedReader br;

    public MigrarTxt(BufferedReader br) {
        this.br = br;
    }

    public static void main(String args[]) throws SQLException, ClassNotFoundException {
        if (args.length > 0) {
            File file = new File(args[0]);
            if (file.exists() && file.canRead()) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    MigrarTxt mt = new MigrarTxt(br);
                    mt.execute();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MigrarTxt.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MigrarTxt.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("Arquivo nao encontrado : " + file.getAbsolutePath());
            }
        } else {
            System.out.println("Arquivo nao especificado");
        }

    }

    private static int sum(int[] vetor) {
        int soma = 0;
        for (int posicao = 0; posicao < vetor.length; posicao++) {
            soma += vetor[posicao];
        }
        return soma;
    }

    private void execute() throws SQLException {
        boolean first = true;

        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE + "?useSSL=false", "root", "root")) {

            try (Statement tstmt = myConn.createStatement()) {
                boolean execute = tstmt.execute("truncate frota");
                System.out.println("Frota truncated : " + execute);
            }
            int i = 0;
            String insert = "insert into " + DATABASE
                    + ".frota (uf,muni,marc_mode,ano_fabr,quan_veic) values (?,?,?,?,?)";
            try (PreparedStatement myStmt = myConn.prepareStatement(insert)) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (first) {
                        first = false;
                    } else {
                        prepareStatement(myStmt, line);
                        i++;
                    }

                    executeBatch(i, myStmt, line);
                }
                executeBatch(i, myStmt, line);
            } catch (IOException ex) {
                Logger.getLogger(MigrarTxt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int prepareStatement(PreparedStatement myStmt, String line) throws SQLException {
        //UF;Município;Marca Modelo;Ano Fabricação Veículo CRV;Qtd. Veículos
        String[] params = line.split(";");
        if (params.length == 5) {
            int j = 1;
            int i = 0;
            String uf = params[i++];
            if (uf != null) {
                uf = uf.replace("\"", "");
            }
            myStmt.setString(j++, uf);

            String muni = params[i++];
            if (muni != null) {
                muni = muni.replace("\"", "");
            }
            myStmt.setString(j++, muni);

            String marcaModelo = params[i++];
            if (marcaModelo!= null) {
                marcaModelo = marcaModelo.replace("\"", "");
            }
            myStmt.setString(j++, marcaModelo);

            try {
                String aux = params[i++];
                if (isNumber(aux)) {
                    int ano = Integer.parseInt(aux);
                    myStmt.setInt(j++, ano);
                } else {
                    myStmt.setNull(j++, java.sql.Types.INTEGER);
                }                
            } catch (NumberFormatException ex) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Erro de formatacao", ex);
            }
            
            String quantidade = params[i++].trim();
            if (isNumber(quantidade)) {
                BigDecimal bd = new BigDecimal(quantidade);
                myStmt.setBigDecimal(j++, bd);
            } else {
                System.out.println("Linha " + i + " nao eh um numero : " + quantidade);
                myStmt.setNull(j++, java.sql.Types.BIGINT);
            }

            myStmt.addBatch();
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isNumber(String strNum) {
        if (strNum == null) {
            return false;
        }
        return PATTERN.matcher(strNum).matches();
    }

    private void executeBatch(int i, PreparedStatement myStmt, String line) throws SQLException {
        if ((i % 10000) == 0) {
            int registros = sum(myStmt.executeBatch());
            System.out.println(registros + " registros inseridos\nUltima linha : " + line);
        }
    }
}
