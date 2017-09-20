package zooplus.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.hsqldb.jdbc.*;


public abstract class BaseDAO {
	
	public static boolean createDatabase(){
		
		
		return true;
		
	}
	
	public static boolean createConnection(){
		try {
			Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return true;
	}

}
