package data;

import lombok.val;
import lombok.var;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {
    static String url = System.getProperty("db.url");
    static String user = System.getProperty("db.user");
    static String password = System.getProperty("db.password");

    public static void clearTables() throws SQLException {

        val deleteOrderEntity = "DELETE FROM order_entity;";
        val deletePaymentEntity = "DELETE FROM payment_entity;";
        val deleteCreditRequestEntity = "DELETE FROM credit_request_entity;";
        val countSQL = "SELECT COUNT(*) FROM order_entity;";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection(
                        url, user, password
                );
        ) {
            runner.update(conn, deleteOrderEntity);
            runner.update(conn, deletePaymentEntity);
            runner.update(conn, deleteCreditRequestEntity);
             var count = runner.query(conn, countSQL, new ScalarHandler<>());
            System.out.println(count);
        }
    }

    public static String findPaymentStatus() throws SQLException {
        val statusSQL = "SELECT status FROM payment_entity;";
        return getData(statusSQL);
    }

    public static String findCreditStatus() throws SQLException {
        val statusSQL = "SELECT status FROM credit_request_entity;";
        return getData(statusSQL);
    }

    public static String countRecords() throws SQLException {
        val countSQL = "SELECT COUNT(*) FROM order_entity;";
        val runner = new QueryRunner();
        Long count = null;

        try (
                val conn = DriverManager.getConnection(
                        url, user, password
                );
        ) {
            count = runner.query(conn, countSQL, new ScalarHandler<>());
            System.out.println(count);
        }
        return Long.toString(count);
    }

    private static String getData(String query) throws SQLException {
        val runner = new QueryRunner();
        String data = "";

        try (
                val conn = DriverManager.getConnection(
                        url, user, password
                );
        ) {
            data = runner.query(conn, query, new ScalarHandler<>());
            System.out.println(data);
        }
        return data;
    }
}
