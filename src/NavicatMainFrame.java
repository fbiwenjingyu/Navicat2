import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class NavicatMainFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static int WIDTH = 1200;
	private final static int HEIGHT = 900;
	private final static int LABEL_SIZE = 60;
	private JPanel topPanel = new JPanel();
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JPanel buttonPanel = new JPanel();
	private JScrollPane treePane = new JScrollPane();
	private JTable table = new JTable(); 
	private JScrollPane tablePane = new JScrollPane();
	private JButton firstPage = new JButton("首页");
	private JButton previousPage = new JButton("上一页");
	private JButton nextPage = new JButton("下一页");
	private JButton lastPage = new JButton("末页");
	private Page page;
	List<Map<String,Object>> contents = new ArrayList<>();
	private Database db = new Database();
	private ConnDialog connDialog;
	
    private JPopupMenu popMenu; //右键菜单
	private JMenuItem addItem;   //各个菜单项
	private JMenuItem delItem;
	private JMenuItem editItem;
	
	private String currentRightClickedConnName;
	
	public static void main(String[] args) throws Exception {
		new NavicatMainFrame();

		
		
	}
	
	public NavicatMainFrame() throws Exception {
		showGUI();
	}
	
	private void showGUI() throws Exception {
		addMenus();
		initPopUpMenu();
		initTopPanel();
		initLeftPanel();
		initRightPanel();
		initButtonPanel();
		 
		getContentPane().add(topPanel, BorderLayout.NORTH);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setProperties();
		splitPane.setDividerLocation(0.2); 
		
	}

	private void initPopUpMenu() {
		popMenu = new JPopupMenu();
	 	addItem = new JMenuItem("新建连接");
	 	addItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NavicatMainFrame.this.connDialog = new ConnDialog(NavicatMainFrame.this,OperateType.CREATE,currentRightClickedConnName);
				
			}
		});
	 	delItem = new JMenuItem("删除连接");
	 	delItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == delItem) {
					int ret = JOptionPane.showConfirmDialog(NavicatMainFrame.this, "你确定要删除这个连接吗？","确认删除",JOptionPane.YES_NO_OPTION);
					if(ret == 0) {
						System.out.println(currentRightClickedConnName);
						JsonFileUtils utils = new JsonFileUtils();
						utils.deleteConnbyName(currentRightClickedConnName);
						try {
							NavicatMainFrame.this.dispose();
							new NavicatMainFrame();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}
			}
		});
	 	editItem = new JMenuItem("编辑连接");
	 	editItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NavicatMainFrame.this.connDialog = new ConnDialog(NavicatMainFrame.this,OperateType.MODIFY,currentRightClickedConnName);
				
			}
		});
	 	popMenu.add(addItem);
	 	popMenu.add(delItem);
	 	popMenu.add(editItem);
		
	}

	private void initButtonPanel() {
		JTextArea button = new JTextArea();
		button.setEditable(false);
		buttonPanel.add(button);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); 
		
		buttonPanel.add(firstPage);  
		buttonPanel.add(previousPage);
		buttonPanel.add(nextPage);
		buttonPanel.add(lastPage);
		firstPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				page.firstPage();
				//currentClickedButton = firstPage;
				showTable(page.getCurrentPageData());
			}
		});
		
		previousPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				page.previousPage();
				//currentClickedButton = previousPage;
				showTable(page.getCurrentPageData());
			}
		});
		
		nextPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				page.nextPage();
				//currentClickedButton = nextPage;
				showTable(page.getCurrentPageData());
			}
		});
		
		lastPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				page.lastPage();
				//currentClickedButton = lastPage;
				showTable(page.getCurrentPageData());
			}
		});
	}

	private void initRightPanel() {
		TableModel dataModel = new DefaultTableModel(new String[][] {},new String[] {});
		table.setModel(dataModel);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//		table.validate();
//		//JPanel rightPanel = new JPanel();
		tablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		tablePane.getViewport().add(table);
//        tablePane.getViewport().add(table); 
//        //rightPanel.add(tablePane);
		splitPane.add(tablePane);
		
	}

	private void initLeftPanel() throws Exception {  
		List<MyConnection> conns = new JsonFileUtils().readListFromFile();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("我的连接",  true);  
		JTree jTree = new JTree(root);  
	    jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    jTree.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				JTree tree = (JTree) e.getSource();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				currentRightClickedConnName = node.toString();
				if(node!=null && node.getLevel() == 1) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						popMenu.show(tree, e.getX(), e.getY());
						
					}
				}
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    treePane.getViewport().add(jTree);
	    splitPane.add(treePane);
	    if(conns != null) {
			for(MyConnection conn : conns) {
				Vector<String> dbs = new Database().getMysqlDatabaseNamesByConnName(conn.getConnName());
				
				DefaultMutableTreeNode connection = new DefaultMutableTreeNode(conn.getConnName(),  true);  
				root.add(connection);
				
				for(String db : dbs) {
					DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode(db,true);
					connection.add(dbNode);
					Vector<String> tables = new Database().getMysqlTableNamesByDatabaseName(db);
					for(String table : tables) {
						DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);
						dbNode.add(tableNode);
					}
				}
				
		 
			}
		}
	    jTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				JTree tree = (JTree) e.getSource();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if(node!=null && node.getLevel() == 3) {
					String databasename = node.getParent().toString();
					String tablename = node.toString();
					try {
						MyConnection conn = new JsonFileUtils().findConnByName(node.getParent().getParent().toString());
						contents = db.getTableContents(conn,databasename,tablename);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					page = new Page(contents);
									
					List currentPageData = page.getCurrentPageData();
					showTable(currentPageData);
				}
			}
			
			private void print(String[] colum) {
				for(String c : colum) {
					System.out.print(c + " " );
				}
			}
		});
	    jTree.expandPath(new TreePath(root.getPath()));
	    //jTree.expandRow(0);
//		Vector<String> dbs = new Database().getMysqlDatabaseNames();
//		
//		DefaultMutableTreeNode database = new DefaultMutableTreeNode("Databases",  true);  
//		for(String db : dbs) {
//			DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode(db);
//			database.add(dbNode);
//			Vector<String> tables = new Database().getMysqlTableNamesByDatabaseName(db);
//			for(String table : tables) {
//				DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);
//				dbNode.add(tableNode);
//			}
//		}
//		
//        JTree jTree = new JTree(database);  
//        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
//        //panel.add(new JScrollPane(jTree));  
//        treePane.getViewport().add(jTree);
//        splitPane.add(treePane);
//        //splitPane.add(panel);
//        jTree.addTreeSelectionListener(new TreeSelectionListener() {
//			@Override
//			public void valueChanged(TreeSelectionEvent e) {
//				JTree tree = (JTree) e.getSource();
//				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//				if(node!=null && node.getLevel() == 2) {
//					String databasename = node.getParent().toString();
//					String tablename = node.toString();
//					try {
//						contents = db.getTableContents(databasename,tablename);
//					} catch (Exception e1) {
//						e1.printStackTrace();
//					}
//					page = new Page(contents);
//									
//					List currentPageData = page.getCurrentPageData();
//					showTable(currentPageData);
//				}
//			}
//
//			
//
//			private void print(String[] colum) {
//				for(String c : colum) {
//					System.out.print(c + " " );
//				}
//			}
//
//			
//		});
	}
	
	public void showTable(List currentPageData) {
				String colum[] = db.getColumn(currentPageData);
				String data[][] = db.getData(currentPageData);
				db.print(data);
				TableModel dataModel = new DefaultTableModel(data,colum);
				//table = new JTable(); 
				table.setModel(dataModel);
				table.validate();
	}

	private void initTopPanel() {
		
		JLabel conn = new JLabel("连接");
		JLabel user = new JLabel("用户");
		JLabel table = new JLabel("表");
		JLabel view = new JLabel("视图");
		JLabel func = new JLabel("函数");
		JLabel event = new JLabel("事件");
		JLabel query = new JLabel("查询");
		JLabel report = new JLabel("报表");
		JLabel backup = new JLabel("备份");
		JLabel plan = new JLabel("计划");
		JLabel model = new JLabel("模型");
		//conn.setSize(LABEL_SIZE, LABEL_SIZE);
		conn.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//user.setSize(LABEL_SIZE, LABEL_SIZE);
		user.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//table.setSize(LABEL_SIZE, LABEL_SIZE);
		table.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//view.setSize(LABEL_SIZE, LABEL_SIZE);
		view.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//func.setSize(LABEL_SIZE, LABEL_SIZE);
		func.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//event.setSize(LABEL_SIZE, LABEL_SIZE);
		event.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//query.setSize(LABEL_SIZE, LABEL_SIZE);
		query.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//report.setSize(LABEL_SIZE, LABEL_SIZE);
		report.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//backup.setSize(LABEL_SIZE, LABEL_SIZE);
		backup.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//plan.setSize(LABEL_SIZE, LABEL_SIZE);
		plan.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		//model.setSize(LABEL_SIZE, LABEL_SIZE);
		model.setPreferredSize(new Dimension(LABEL_SIZE, LABEL_SIZE));
		topPanel.add(conn);
		topPanel.add(user);
		topPanel.add(table);
		topPanel.add(view);
		topPanel.add(func);
		topPanel.add(event);
		topPanel.add(query);
		topPanel.add(report);
		topPanel.add(backup);
		topPanel.add(plan);
		topPanel.add(model);
	}

	private void addMenus() {
		MenuBar menuBar = new MenuBar();
		Menu file = new Menu("文件");
		Menu view = new Menu("查看");
		Menu favorite = new Menu("收藏夹");
		Menu tools = new Menu("工具");
		Menu windows = new Menu("窗口");
		Menu help = new Menu("帮助");
		
		MenuItem newConn = new MenuItem("新建连接");
		MenuItem openConn = new MenuItem("打开连接");
		MenuItem closeConn = new MenuItem("关闭连接");
		MenuItem exportConn = new MenuItem("导出连接");
		MenuItem importConn = new MenuItem("导入连接");
		MenuItem close = new MenuItem("关闭");
		MenuItem exit = new MenuItem("退出 Navicat");
		newConn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NavicatMainFrame.this.connDialog = new ConnDialog(NavicatMainFrame.this,OperateType.CREATE,currentRightClickedConnName);
				
				
			}
		});
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NavicatMainFrame.this.dispose();
			}
		});
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NavicatMainFrame.this.dispose();
			}
		});
		file.add(newConn);
		file.add(openConn);
		file.add(closeConn);
		file.add(exportConn);
		file.add(importConn);
		file.add(close);
		file.add(exit);
		
		menuBar.add(file);
		menuBar.add(view);
		menuBar.add(favorite);
		menuBar.add(tools);
		menuBar.add(windows);
		menuBar.add(help);
		this.setMenuBar(menuBar);
	}

	private void setProperties() {
		setTitle("Navicat Premium");
		setSize(WIDTH, HEIGHT);
		setLocation();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void setLocation() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double x = (screenSize.getWidth() - WIDTH) / 2;
		double y = (screenSize.getHeight() - HEIGHT) / 2;
		setLocation((int)x, (int)y);
	}

}
