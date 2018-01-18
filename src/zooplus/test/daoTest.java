package zooplus.test;

import static org.junit.Assert.fail;
import org.junit.Assert;
import org.junit.Test;
import zooplus.dao.*;
import zooplus.log.Ilogging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hsqldb.Server;



public class daoTest implements Ilogging {

	@Test
	public void testStartServer() {
		Assert.assertTrue(BaseDAO.startServer());
	}

	@Test
	public void testGetConnection() {
		Assert.assertNotNull(BaseDAO.getConnection());
	}

	@Test
	public void testGetVersion() {
		Server s = new Server();
		log.info(s.getClass().getPackage().getSpecificationVersion());
		log.info(s.getClass().getPackage().getImplementationVersion());

		System.out.println("test");

		Assert.assertTrue(true);

	}

	@Test
	public void testCreateTables() {
		BaseDAO.startServer();
		Connection conn = BaseDAO.getConnection();
		if (conn == null)
			Assert.fail("conn == null");

		Assert.assertTrue(BaseDAO.createTables());

	}

	@Test
	public void testInsertSampleRecords() {
		BaseDAO.startServer();
		Connection conn = BaseDAO.getConnection();

		if (conn == null)
			return;

		// insert records in Users table
		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO USERS (EMAIL,");
		sb.append("PASSWORD,");
		sb.append("VERSION,");
		sb.append("LASTLOGIN)");
		sb.append("VALUES ('abc@abc.com',");
		sb.append("'password',");
		sb.append("1,");
		sb.append("'2012-09-21 20:08:08')");

		PreparedStatement ps;
		int updateCount = 0;

		try {
			ps = BaseDAO.getConnection().prepareStatement(sb.toString());
			updateCount = ps.executeUpdate();
		} catch (SQLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		Assert.assertEquals(1, updateCount);

		sb.setLength(0);
		// insert records in QueryHistory table

		sb.append("INSERT INTO QUERYHISTORY (USER_ID,");
		sb.append("FROM_CURRENCY,");
		sb.append("FROM_AMOUNT,");
		sb.append("TO_CURRENCY,");
		sb.append("TO_AMOUNT,");
		sb.append("VERSION,");
		sb.append("QUERY_DATE)");
		sb.append("VALUES (0,");
		sb.append("'USD',");
		sb.append("12.12,");
		sb.append("'INR',");
		sb.append("452.25,");
		sb.append("1,");
		sb.append("'2012-09-21 20:08:08')");

		try {
			ps = BaseDAO.getConnection().prepareStatement(sb.toString());
			updateCount = ps.executeUpdate();
		} catch (SQLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

	}

	@Test
	public void testTemp() throws ClassNotFoundException, SQLException {

		// 'Server' is a class of HSQLDB representing
		// the database server
		Server hsqlServer = null;
		try {
			hsqlServer = new Server();

			// HSQLDB prints out a lot of informations when
			// starting and closing, which we don't need now.
			// Normally you should point the setLogWriter
			// to some Writer object that could store the logs.
			hsqlServer.setLogWriter(null);
			hsqlServer.setSilent(true);

			// The actual database will be named 'xdb' and its
			// settings and data will be stored in files
			// testdb.properties and testdb.script
			hsqlServer.setDatabaseName(0, "xdb");
			hsqlServer.setDatabasePath(0, "file:testdb");

			// Start the database!
			hsqlServer.start();

			Connection connection = null;
			// We have here two 'try' blocks and two 'finally'
			// blocks because we have two things to close
			// after all - HSQLDB server and connection
			try {
				// Getting a connection to the newly started database
				Class.forName("org.hsqldb.jdbcDriver");
				// Default user of the HSQLDB is 'sa'
				// with an empty password
				connection = DriverManager.getConnection(
						"jdbc:hsqldb:hsql://localhost/xdb", "sa", "");

				// Here we run a few SQL statements to see if
				// everything is working.
				// We first drop an existing 'testtable' (supposing
				// it was there from the previous run), create it
				// once again, insert some data and then read it
				// with SELECT query.
				connection.prepareStatement("drop table testtable;").execute();
				connection.prepareStatement(
						"create table testtable ( id INTEGER, "
								+ "name VARCHAR);").execute();
				connection.prepareStatement(
						"insert into testtable(id, name) "
								+ "values (1, 'testvalue');").execute();
				ResultSet rs = connection.prepareStatement(
						"select * from testtable;").executeQuery();

				// Checking if the data is correct
				rs.next();
				System.out.println("Id: " + rs.getInt(1) + " Name: "
						+ rs.getString(2));
			} finally {
				// Closing the connection
				if (connection != null) {
					connection.close();
				}

			}
		} finally {
			// Closing the server
			if (hsqlServer != null) {
				hsqlServer.stop();
			}
		}
	}

}
