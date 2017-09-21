package zooplus.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hsqldb.Server;
import org.hsqldb.jdbc.*;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;

import zooplus.log.Ilogging;

public abstract class BaseDAO implements Ilogging {
	static Server server = new Server();
	static Connection conn = null;

	public static boolean startServer() {

		// HsqlProperties p = new HsqlProperties();
		// p.setProperty("server.database.2", "mem:adatabase");
		// p.setProperty("server.dbname.2", "quickdb");

		// set up the rest of properties

		// alternative to the above is

		/*
		 * try { server.setProperties(p); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (AclFormatException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		server.setSilent(false);
		server.setTrace(true);

		// server.setLogWriter(null); // can use custom writer
		// server.setErrWriter(null); // can use custom writer
		server.start();

		return true;
	}

	public static boolean createDatabase() {
		return true;
	}

	public static Connection getConnection() {

		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (Exception e) {
			System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
			log.error("ERROR: failed to load HSQLDB JDBC driver."
					+ e.getMessage());
			e.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:hsqldb:mem:zooplusDB",
					"SA", "");
		} catch (SQLException e) {
			log.error("Error occurred while making a in-memory connection."
					+ e.getMessage());
			e.printStackTrace();
		}
		return conn;
	}

	public static boolean createTables() {

		PreparedStatement ps;
		
		// Creating Users table
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE Users (");
		sb.append("ID IDENTITY  PRIMARY KEY");
		sb.append(",email VARCHAR(250)  NOT NULL");
		sb.append(",password  VARCHAR(250) NOT NULL");
		sb.append(",version SMALLINT NOT NULL");
		sb.append(", lastLogin TIMESTAMP NOT NULL");
		sb.append(");");
		
		try {
			ps = getConnection().prepareStatement(sb.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		// Creating QueryHistory Table
		sb.setLength(0); //this will clear the sb
		sb.append("CREATE TABLE QueryHistory (");
		sb.append("ID IDENTITY  PRIMARY KEY");
		sb.append(",user_id INTEGER FOREIGN KEY REFERENCES users(ID)");
		sb.append(",from_currency  VARCHAR(3) NOT NULL");
		sb.append(",from_amount DECIMAL (10,2) NOT NULL");
		sb.append(",to_currency  VARCHAR(3) NOT NULL");
		sb.append(",to_amount DECIMAL (10,2) NOT NULL");
		sb.append(",version SMALLINT NOT NULL");
		sb.append(", query_date TIMESTAMP NOT NULL");
		sb.append(");");

		
		try {
			ps = getConnection().prepareStatement(sb.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		return true;

	}

	public static boolean stopServer() {

		server.shutdownCatalogs(1);
		return true;
	}
}
