import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Database {
	private final String DRIVER = "com.mysql.jdbc.Driver";
	private final String URL = "jdbc:mysql://localhost:3306/";
	private final String USERNAME = "root";
	private final String PASSWORD = "123456";
	private String myDriver = DRIVER;
	private String myURL = "jdbc:mysql://";
	private String myUSERNAME = USERNAME;
	private String myPASSWORD = PASSWORD;
	private String myPort = "3306";
	private String myipAddress = "localhost";
	private String errorMsg;
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getMyDriver() {
		return myDriver;
	}

	public void setMyDriver(String myDriver) {
		this.myDriver = myDriver;
	}

	public String getMyURL() {
		return myURL;
	}

	public void setMyURL(String myURL) {
		this.myURL = myURL;
	}

	public String getMyUSERNAME() {
		return myUSERNAME;
	}

	public void setMyUSERNAME(String myUSERNAME) {
		this.myUSERNAME = myUSERNAME;
	}

	public String getMyPASSWORD() {
		return myPASSWORD;
	}

	public void setMyPASSWORD(String myPASSWORD) {
		this.myPASSWORD = myPASSWORD;
	}

	public String getMyPort() {
		return myPort;
	}

	public void setMyPort(String myPort) {
		this.myPort = myPort;
	}
	

	public String getMyipAddress() {
		return myipAddress;
	}

	public void setMyipAddress(String myipAddress) {
		this.myipAddress = myipAddress;
	}

	public boolean testMyConnection() {
		myURL = myURL + getMyipAddress() + ":" + getMyPort() + "/";
		try {
			Class.forName(DRIVER);
			Connection con = DriverManager.getConnection(myURL,getMyUSERNAME(),getMyPASSWORD());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			this.setErrorMsg(e.getMessage());
			return false;
		}
		return true;
	}

	public Vector<String> getMysqlDatabaseNames() throws Exception {
		Vector<String> names = new Vector<>();
		Class.forName(DRIVER);
		Connection con = (Connection) DriverManager.getConnection(URL,USERNAME,PASSWORD);
		DatabaseMetaData metaData = (DatabaseMetaData) con.getMetaData();
		ResultSet rs = metaData.getCatalogs();
		while(rs.next()) {
			String databasename = rs.getString(1);
			names.add(databasename);
		}
		return names;
	}
	
	public Vector<String> getMysqlDatabaseNamesByConnName(String connName) throws Exception {
		MyConnection conn = new JsonFileUtils().findConnByName(connName);
		this.setMyipAddress(conn.getIpAddress());
		this.setMyPort(String.valueOf(conn.getPort()));
		this.setMyUSERNAME(conn.getUsername());
		this.setMyPASSWORD(conn.getPassword());
		String myConnURL = myURL + getMyipAddress() + ":" + getMyPort() + "/";
		Vector<String> names = new Vector<>();
		Class.forName(DRIVER);
		try {
			Connection con = (Connection) DriverManager.getConnection(myConnURL,getMyUSERNAME(),getMyPASSWORD());
			DatabaseMetaData metaData = (DatabaseMetaData) con.getMetaData();
			ResultSet rs = metaData.getCatalogs();
			while(rs.next()) {
				String databasename = rs.getString(1);
				names.add(databasename);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return names;
	}

	public Vector<String> getMysqlTableNamesByDatabaseName(String databasename) throws Exception {
		Vector<String> names = new Vector<>();
		Class.forName(DRIVER);
		Connection con = (Connection) DriverManager.getConnection(URL+ databasename,USERNAME,PASSWORD);
		
		DatabaseMetaData metaData = con.getMetaData();
		ResultSet rs = metaData.getTables(databasename, "", null, null);
		while(rs.next()) {
			names.add(rs.getString(3));  
		}
		return names;
	}
	
	public List<Map<String,Object>> getTableContents(String databasename,String tablename) throws Exception{
		Vector<Map<String,Object>> contents = new Vector<>();
		Class.forName(DRIVER);
		Connection con = (Connection) DriverManager.getConnection(URL+ databasename,USERNAME,PASSWORD);
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("select * from " + tablename);
		int columnCount = rs.getMetaData().getColumnCount();
		while(rs.next()) {
			Map<String,Object> content = new LinkedHashMap<>();	
			for(int i=1;i<=columnCount;i++) {
				content.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
			}
			contents.add(content);
		}
		return contents;
	}
	
	public List<Map<String,Object>> getTableContents(MyConnection conn,String databasename,String tablename) throws Exception{
		this.setMyipAddress(conn.getIpAddress());
		this.setMyPort(String.valueOf(conn.getPort()));
		this.setMyUSERNAME(conn.getUsername());
		this.setMyPASSWORD(conn.getPassword());
		String myConnURL = myURL + getMyipAddress() + ":" + getMyPort() + "/" + databasename;
		Vector<Map<String,Object>> contents = new Vector<>();
		Class.forName(DRIVER);
		Connection con = (Connection) DriverManager.getConnection(myConnURL,getMyUSERNAME(),getMyPASSWORD());
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("select * from " + tablename);
		int columnCount = rs.getMetaData().getColumnCount();
		while(rs.next()) {
			Map<String,Object> content = new LinkedHashMap<>();	
			for(int i=1;i<=columnCount;i++) {
				content.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
			}
			contents.add(content);
		}
		return contents;
	}
	
	public static void main(String[] args) throws Exception {
		Database db = new Database();
		List<Map<String,Object>> contents = db.getTableContents("world","city");
		System.out.println(contents.size());
		String[][] data = db.getData(contents);
		db.print(data);
	}
	
	
	public void print(String[][] data) {
		for(int i=0;i<data.length;i++) {
			for(int j=0;j<data[i].length;j++) {
				System.out.print(data[i][j] + " ");
			}
			System.out.println();
		}
		
	}

	public String[][] getData(List<Map<String, Object>> contents) {
		if(contents != null && contents.size() > 0) {
			String data[][] = new String[contents.size()][contents.get(0).size()];
			for(int i=0;i<contents.size();i++) {
				LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) contents.get(i);
				Collection<Object> values = map.values();
				Iterator<Object> iterator = values.iterator();
				int j = 0;
				while(iterator.hasNext()) {
					Object value = iterator.next();
					if(value != null) {
						data[i][j] = value.toString();
					}else {
						data[i][j] = "";
					}
					
					j++;
				}
			}
			return data;
		}
		return new String[0][0];
	}

	public String[] getColumn(List<Map<String, Object>> contents) {
		List<String> list = new ArrayList<>();
		if(contents != null && contents.size() > 0) {
			Map<String, Object> map = contents.get(0);
			Set<String> keySet = map.keySet();
			for(String key : keySet) {
				list.add(key);
			}
		}
		return list.toArray(new String[] {});
	}

	public void print(List<Map<String, Object>> data) {
		for(Map<String, Object> map : data) {
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
			}
			System.out.println();
		}
		
	}
}
