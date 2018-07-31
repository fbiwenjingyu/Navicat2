import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConnDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	private JFrame parent;
	private OperateType opType;
	private static final int DEFAULT_WIDTH = 300;
	private static final int DEFAULT_HEIGHT = 310;
	private String connName;
	private String ipAddress;
	private int port;
	private String username;
	private String password;
	private String currentRightClickedConnName;
	JTextField connNameField;
	JTextField ipAddressField;
	JTextField portField ;
	JTextField usernameField;
	JTextField passwordField;
	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ConnDialog(JFrame owner,OperateType opType,String currentRightClickedConnName) {
		super(owner,"MySQL-新建连接",true);
		parent = owner;
		this.opType = opType;
		this.currentRightClickedConnName = currentRightClickedConnName;
		JPanel inputPanel = new JPanel();
		inputPanel.setMinimumSize(new Dimension(180, 220));
		inputPanel.setBorder(BorderFactory.createEtchedBorder());
		
		if(opType == OperateType.CREATE) {
			connNameField = new JTextField("",20);
			ipAddressField = new JTextField("localhost",20);
			portField = new JTextField("3306",20);
			usernameField= new JTextField("root",20);
			passwordField = new JPasswordField("",20);
		}else if(opType == OperateType.MODIFY) {
			this.setTitle("MySQL-编辑连接");
			JsonFileUtils utils = new JsonFileUtils();
			MyConnection connection = utils.findConnByName(currentRightClickedConnName);
			connNameField = new JTextField(connection.getConnName(),20);
			ipAddressField = new JTextField(connection.getIpAddress(),20);
			portField = new JTextField(String.valueOf(connection.getPort()),20);
			usernameField= new JTextField(connection.getUsername(),20);
			passwordField = new JPasswordField(connection.getPassword(),20);
		}
		
		
		JCheckBox savePassBox = new JCheckBox("保存密码");
		savePassBox.setSelected(true);
		
		inputPanel.add(new JLabel("连接名  :"));
		inputPanel.add(connNameField);
		inputPanel.add(new JLabel("ip地址   :"));
		inputPanel.add(ipAddressField);
		inputPanel.add(new JLabel("端口      :"));
		inputPanel.add(portField);
		inputPanel.add(new JLabel("用户名  :"));  
		inputPanel.add(usernameField);
		inputPanel.add(new JLabel("密码      :"));
		inputPanel.add(passwordField);
		inputPanel.add(new JLabel("         "));
		inputPanel.add(savePassBox);
		
		JPanel buttonPanel = new JPanel();
		JButton testButton = new JButton("连接测试");
		JButton okButton = new JButton("确定");
		JButton cancelButton = new JButton("取消");
		testButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(StringUtils.isEmpty(ipAddressField.getText())) {
					JOptionPane.showMessageDialog(ConnDialog.this, "请输入ip地址");
					return;
				}
				if(StringUtils.isEmpty(portField.getText())){
					JOptionPane.showMessageDialog(ConnDialog.this, "请输入端口号");
					return;
				}
				
				if(StringUtils.isEmpty(usernameField.getText())){
					JOptionPane.showMessageDialog(ConnDialog.this, "请输入用户名");
					return;
				}
				if(StringUtils.isEmpty(passwordField.getText())){
					JOptionPane.showMessageDialog(ConnDialog.this, "请输入密码");
					return;
				}
				ipAddress = ipAddressField.getText();
				port = Integer.parseInt(portField.getText());
				username = usernameField.getText();
				password = passwordField.getText();
				Database db = new Database();
				db.setMyipAddress(ipAddress);
				db.setMyPort(String.valueOf(port));
				db.setMyUSERNAME(username);
				db.setMyPASSWORD(password);
				if(!db.testMyConnection()) {
					JOptionPane.showMessageDialog(ConnDialog.this, "连接失败！" + db.getErrorMsg(),"",JOptionPane.ERROR_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(ConnDialog.this, "连接成功！");
				}
				
			}
		});
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(StringUtils.isEmpty(ipAddressField.getText())) {
					JOptionPane.showMessageDialog(ConnDialog.this, "请输入ip地址");
					return;
				}
				if(StringUtils.isEmpty(portField.getText())){
					JOptionPane.showMessageDialog(ConnDialog.this, "请输入端口号");
					return;
				}
				
				if(StringUtils.isEmpty(usernameField.getText())){
					JOptionPane.showMessageDialog(ConnDialog.this, "请输入用户名");
					return;
				}
				if(StringUtils.isEmpty(passwordField.getText())){
					JOptionPane.showMessageDialog(ConnDialog.this, "请输入密码");
					return;
				}
				
				ipAddress = ipAddressField.getText();
				port = Integer.parseInt(portField.getText());
				username = usernameField.getText();
				password = passwordField.getText();
				if(StringUtils.isEmpty(connNameField.getText())) {
					connName = ipAddress + "_" + port;
				}else {
					connName = connNameField.getText();
				}
				if(ConnDialog.this.opType == OperateType.CREATE) {
					MyConnection conn = new MyConnection();
					conn.setConnName(connName);
					conn.setIpAddress(ipAddress);
					conn.setPort(port);
					conn.setUsername(username);
					conn.setPassword(password);
					JsonFileUtils utils = new JsonFileUtils();
					Result result = utils.appendConnToFile(conn);
					if(result == Result.failure) {
						JOptionPane.showMessageDialog(ConnDialog.this, "连接名: " + conn.getConnName() + "已存在","",JOptionPane.ERROR_MESSAGE);
					}else if(result == Result.success) {
						JOptionPane.showMessageDialog(ConnDialog.this, "新建连接成功","",JOptionPane.INFORMATION_MESSAGE);
						ConnDialog.this.dispose();
						parent.dispose();
						try {
								new NavicatMainFrame();
						} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
						}
						
					}
				}else if(ConnDialog.this.opType == OperateType.MODIFY) {
					JsonFileUtils utils = new JsonFileUtils();
					MyConnection conn = new MyConnection();
					conn.setConnName(connName);
					conn.setIpAddress(ipAddress);
					conn.setPort(port);
					conn.setUsername(username);
					conn.setPassword(password);
					Result result = utils.modifyConnByName(currentRightClickedConnName, conn);
					if(result == Result.nameExist) {
						JOptionPane.showMessageDialog(ConnDialog.this, "连接名: " + conn.getConnName() + "已存在","",JOptionPane.ERROR_MESSAGE);
					}else if(result == Result.failure) {
						JOptionPane.showMessageDialog(ConnDialog.this, "修改连接失败","",JOptionPane.ERROR_MESSAGE);
					}else if(result == Result.success) {
						JOptionPane.showMessageDialog(ConnDialog.this, "修改连接成功","",JOptionPane.INFORMATION_MESSAGE);
						ConnDialog.this.dispose();
						parent.dispose();
						try {
							new NavicatMainFrame();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}
			
				
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConnDialog.this.dispose();
				
			}
		});
		
		buttonPanel.add(testButton);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		getContentPane().add(inputPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
