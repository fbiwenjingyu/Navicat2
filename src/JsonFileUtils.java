import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.fastjson.JSON;


public class JsonFileUtils {
	
	public static final String fileName = "connections.json";
	public static void main(String[] args) throws IOException {
		JsonFileUtils utils = new JsonFileUtils();
		MyConnection conn = new MyConnection();
		conn.setConnName("localhost2");
		conn.setIpAddress("localhost");
		conn.setPort(3306);
		conn.setUsername("root");
		conn.setPassword("123456");
		List<MyConnection> list = new ArrayList<>();
		list.add(conn);
		Result result = utils.appendConnToFile(conn);
		if(result == Result.failure) {
			System.out.println("连接名: " + conn.getConnName() + "已存在");
		}else if(result == Result.success) {
			System.out.println("新建连接成功");
		}
	}
	
	public JsonFileUtils() {
		File file = new File(fileName);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static boolean writeFile(String filePath, String jsonStr) {
        FileWriter fw;
        try {
            fw = new FileWriter(filePath);
            PrintWriter out = new PrintWriter(fw);
            out.write(jsonStr);
            out.println();
            fw.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
	
	public String readFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String laststr = "";
        try {
            // System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                laststr = laststr + tempString;
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return laststr;
    }
	
	public List<MyConnection> jsonStrToList(String jsonString) {
		if(StringUtils.isEmpty(jsonString)) return new ArrayList<MyConnection>();
		List<MyConnection> list = JSON.parseArray(jsonString,MyConnection.class);
		return list;
	}
	
	public static String listToJsonStr(List<MyConnection> list) {
		 String jsonString = JSON.toJSONString(list);
		 return jsonString;
	}
	
	public List<MyConnection> readListFromFile(){
		String jsonStr = readFile(fileName);
		return jsonStrToList(jsonStr);
	}
	
	public Result appendConnToFile(MyConnection conn) {
		Result result;
		if(isConnNameExist(conn.getConnName())) {
			result = Result.failure;
		}else {
			List<MyConnection> list = readListFromFile();
			list.add(conn);
			String jsonStr = listToJsonStr(list);
			writeFile(fileName,jsonStr);
			result = Result.success;
		}
		return result;
	}
	
	public boolean isConnNameExist(String connName) {
		boolean isExist = false;
		List<MyConnection> list = readListFromFile();
		if(list!=null && list.size() > 0) {
			for(MyConnection conn : list) {
				if(connName.equalsIgnoreCase(conn.getConnName())) {
					isExist = true;
					break;
				}
			}
		}
		
		return isExist;
	}
	
	public void deleteConnbyName(String connName) {
		List<MyConnection> list = readListFromFile();
		CopyOnWriteArrayList<MyConnection> copyList = new CopyOnWriteArrayList<MyConnection>(list);
		Iterator<MyConnection> iterator = copyList.iterator();
		while(iterator.hasNext()) {
			MyConnection connection = iterator.next();
			String name = connection.getConnName();
			if(name.equals(connName)) {
				copyList.remove(connection);
			}
		}
		String jsonStr = listToJsonStr(copyList);
		writeFile(fileName,jsonStr);
	}
	
	public MyConnection findConnByName(String connName) {
		MyConnection ret = null;
		List<MyConnection> list = readListFromFile();
		if(list!=null && list.size() > 0) {
			for(MyConnection conn : list) {
				if(connName.equalsIgnoreCase(conn.getConnName())) {
					ret = conn;
					break;
				}
			}
		}
		return 	ret;
	}

}
