package com.philips.dao;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupDB {
    public static boolean backupDataWithOutDatabase(String dumpExePath, String host, String port, String user,
            String password, String database, String backupPath) {

        boolean status = false;
        try {
            Process p = null;

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            String filepath = "backup-" + database + "-" + dateFormat.format(date) + ".sql";

            String batchCommand = "";
            if (password != "") {
                // only backup the data not included create database
                batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " --password="
                        + password + " " + database + " -r " + backupPath + "" + filepath + "";

                System.out.println(batchCommand);

            } else {
                batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " " + database
                        + " -r \"" + backupPath + "" + filepath + "\"";
                System.out.println(batchCommand);
            }

            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(batchCommand);
            int processComplete = p.waitFor();

            if (processComplete == 0) {
                status = true;
                System.out
                        .println("Backup created successfully for without DB " + database + " in " + host + ":" + port);
            } else {
                status = false;
                System.out
                        .println("Could not create the backup for without DB " + database + " in " + host + ":" + port);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
}