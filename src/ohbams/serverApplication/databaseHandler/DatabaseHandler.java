package ohbams.serverApplication.databaseHandler;

import java.sql.*;


public class DatabaseHandler {

    protected Connection connection;
    protected Statement statement;
    protected PreparedStatement preparedStatement;

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/luton_hotel";
    private static final String USERNAME = "luton@admin";
    private static final String PASSWORD = "luton@admin";

    public DatabaseHandler() throws SQLException, ClassNotFoundException {
        this(DATABASE_URL, USERNAME, PASSWORD);
    }

    public DatabaseHandler(String url, String username, String password) throws SQLException, ClassNotFoundException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, username, password);
        statement = connection.createStatement();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public static String getDatabaseUrl() {
        return DATABASE_URL;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public void close() throws SQLException{
        connection.close();
    }
}
