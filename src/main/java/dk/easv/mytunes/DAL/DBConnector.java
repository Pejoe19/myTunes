package dk.easv.mytunes.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.mytunes.BLL.MusicException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;


public class DBConnector {

    // Instance variables
    private static final String PROP_FILE = "config/config.settings";
    private SQLServerDataSource dataSource;

    public DBConnector() throws MusicException{
        Properties databaseProperties = new Properties();
        try {
            databaseProperties.load(new FileInputStream(new File(PROP_FILE)));

            dataSource = new SQLServerDataSource();
            dataSource.setServerName(databaseProperties.getProperty("Server"));
            dataSource.setDatabaseName(databaseProperties.getProperty("Database"));
            dataSource.setUser(databaseProperties.getProperty("User"));
            dataSource.setPassword(databaseProperties.getProperty("Password"));
            dataSource.setPortNumber(1433);
            dataSource.setTrustServerCertificate(true);
        } catch (IOException e) {
            throw new MusicException("Connection to database failed");
        }
    }

    public Connection getConnection() throws SQLServerException {
        return dataSource.getConnection();
    }
    public static Connection getStaticConnection() throws Exception {
        DBConnector dbConnector = new DBConnector();
        return dbConnector.getConnection();
    }

    public static void main(String[] args) throws Exception {
        DBConnector databaseConnector = new DBConnector();
        try (Connection connection = databaseConnector.getConnection()) {
            System.out.println("Is it open? " + !connection.isClosed());
        }
    }
}