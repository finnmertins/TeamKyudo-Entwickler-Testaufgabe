package de.dionik.testaufgabe.Utils;

import de.dionik.testaufgabe.Testaufgabe;

import java.sql.*;

public class MySQL {

    private static String host = Testaufgabe.getCfg().getString("MySQL.host");
    private static String user = Testaufgabe.getCfg().getString("MySQL.user");
    private static String password = Testaufgabe.getCfg().getString("MySQL.password");
    private static String database = Testaufgabe.getCfg().getString("MySQL.database");
    private static int port = Testaufgabe.getCfg().getInt("MySQL.port");

    private static String con_link = "jdbc:mysql://" + host + ":" + port +"/" + database + "?autoReconnect=true&useUnicode=true&characterEncoding=utf8";

    private static Connection con;

    public static void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection(con_link, user, password);
                System.out.println(Testaufgabe.getConsolePrefix() + "Verbindung zur MySQL-Datenbank hergestellt.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                con.close();
                System.out.println(Testaufgabe.getConsolePrefix() + "Verbindung zur MySQL-Datenbank getrennt.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void prepareStatement(String sql) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.executeUpdate();

            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Object getResult(String sql) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                Object result = rs.getObject(1);

                rs.close();
                ps.close();

                return result;
            } else {

                rs.close();
                ps.close();

                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static ResultSet getResultSet(String sql) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createTable() {
        if(!isConnected()) {
            return;
        }
        prepareStatement("CREATE TABLE IF NOT EXISTS Players (UUID VARCHAR(36), Name VARCHAR(16), Cash INTEGER, Accounts INTEGER)");
        prepareStatement("CREATE TABLE IF NOT EXISTS Accounts (UUID VARCHAR(36), ID VARCHAR(6), Cash INTEGER)");
        prepareStatement("CREATE TABLE IF NOT EXISTS Transaction (ACCOUNTID VARCHAR(6), TARGETACCOUNTID VARCHAR(6), ID INTEGER, Cash INTEGER)");
    }

    public static boolean isConnected() {
        return (con == null ? false : true);
    }

    public static Connection getConnection() {
        return con;
    }
}
