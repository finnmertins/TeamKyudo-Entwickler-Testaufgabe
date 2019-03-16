package de.dionik.testaufgabe.Manager;

import de.dionik.testaufgabe.Utils.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SaveManager {

    public SaveManager() {}

    public void log(String pAccountID, String pTargetAccountID, int pCash) {
        ResultSet rs = MySQL.getResultSet("SELECT ID FROM Transaction");
        int useID = 0;

        try {
            while (rs.next()) {
                if (useID < rs.getInt("ID")) {
                    useID = rs.getInt("ID");
                }
            }
            useID++;
            MySQL.prepareStatement("INSERT INTO Transaction(ACCOUNTID, TARGETACCOUNTID, ID, Cash) VALUES ('"
                    + pAccountID + "', '" + pTargetAccountID + "', " + useID + ", " + pCash + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Wenn pAccountID = "-": Von System erhalten
       Wenn pAccountID = "p": Vom eigenen Bargeld erhalten
       Sonst nur KontoNummern!
    */
}