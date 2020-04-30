package com.sand.swt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ManageJar {
	private static Logger log = Logger.getLogger("ManageJar");
	private StartCmd cmd = new StartCmd();
	List<String[]> lst=new ArrayList<String[]>();
	List<String> jars = new ArrayList<String>();
	List<String> paths = new ArrayList<String>();
	private HashMap<Integer, TableEditor> editorHm = new HashMap<Integer, TableEditor>();// 存放tableEditor
	private HashMap<Integer, Button> btnHm = new HashMap<Integer, Button>();// 存放按钮
	private HashMap<Integer, TableItem> itemHm = new HashMap<Integer, TableItem>();// 存放tableitem
	private HashMap<Integer, TableEditor> editorJar= new HashMap<Integer, TableEditor>();// 存放tableEditor
	private HashMap<Integer, Button> btnJar = new HashMap<Integer, Button>();// 存放按钮
	private HashMap<Integer, TableItem> itemJar = new HashMap<Integer, TableItem>();// 存放tableitem

	public static void main(String[] args) {
		ManageJar manageJar = new ManageJar();
		manageJar.init();
	}

	// 初始化工具界面
	private void init() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("jar自动运维工具");// 璁剧疆鏍囬
		// 璁剧疆鑿滃崟
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText("文件-File");

		MenuItem refresh = new MenuItem(menu, SWT.CASCADE);
		refresh.setText("刷新-refresh");
		// 点击文件按钮弹框
		getJarsByDir(menuItem);
		
		// 设置主shell的表格
		shell.setLayout(new FillLayout());
		Table tab = new Table(shell, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK);
		tab.setHeaderVisible(true);
		tab.setLinesVisible(true);
		creatMainTab(tab);
		// 设置刷新按钮操作
		setRefresh(refresh,tab);
		// 1、调用cmd命令获取系统中运行的Java程序
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	// 对主面板进行刷新操作
	private void setRefresh(MenuItem refresh, Table tab) {
		refresh.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				log.info("点击按钮是---" + refresh.getText());
				Shell parShell = refresh.getParent().getShell();
				// 清楚所有的按钮
				Set<Entry<Integer, TableEditor>> entrySet = editorHm.entrySet();
				for (Entry<Integer, TableEditor> e : entrySet) {
					e.getValue().dispose();
				}
				editorHm.clear();
				Set<Entry<Integer, Button>> btnSet = btnHm.entrySet();
				for (Entry<Integer, Button> e : btnSet) {
					e.getValue().dispose();
				}
				btnHm.clear();
				Set<Entry<Integer, TableItem>> itemSet = itemHm.entrySet();
				for (Entry<Integer, TableItem> e : itemSet) {
					e.getValue().dispose();
				}
				itemSet.clear();
				lst.clear();
				// 清除表格数据
				tab.clearAll();
				tab.notifyListeners(SWT.KeyDown, null);
				creatMainTab(tab);
				parShell.layout();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
			}
		});
	}

	// 对文件-File文件处理
	public void getJarsByDir(MenuItem menuItem) {
		menuItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				log.log(Level.INFO, "点击按钮是---" + menuItem.getText());
				// 鍦ㄧ埗绐楀彛鍒涘缓寮规
				Shell parShell = menuItem.getParent().getShell();
				// 设置文件对话框的标题
				DirectoryDialog folderdlg = new DirectoryDialog(parShell);
				// 设置文件对话框的标题
				folderdlg.setText("文件选择");
				// 设置初始路径
				folderdlg.setFilterPath("SystemDrive");
				// 设置对话框提示文本信息
				folderdlg.setMessage("请选择相应的文件夹");
				// 打开文件对话框，返回选中文件夹目录
				String selecteddir = folderdlg.open();
				log.info("您选中的文件夹目录为：" + selecteddir);
				File jarDir = new File(selecteddir);
				if (jarDir.exists()) {
					String[] fileArray = jarDir.list();
					// 过滤文件获取所有的jar包
					for (String f : fileArray) {
						if (f.endsWith(".jar")) {
							log.info("读取jar包为" + f);
							// 拼接jar包系统路径
							jars.add(f);
							log.info("读取jar包路径为" + jarDir + "\\" + f);
							paths.add(jarDir + "\\" + f);
						}
					}
					log.info("获取jar包数量为： 个" + jars.size());
					//清空表格
					Set<Entry<Integer, TableEditor>> entrySet = editorJar.entrySet();
					for(Entry<Integer, TableEditor> e:entrySet) {
						e.getValue().dispose();
					}
					Set<Entry<Integer, Button>> btnSet = btnJar.entrySet();
					for(Entry<Integer, Button> e:btnSet) {
						e.getValue().dispose();
					}
					Set<Entry<Integer, TableItem>> itemSet = itemJar.entrySet();
					for(Entry<Integer, TableItem> e:itemSet) {
						e.getValue().dispose();
					}
					editorJar.clear();
					btnJar.clear();
					itemJar.clear();
					// 将获取到的jar重新渲染到表格中
					Shell selectFile = new Shell(parShell, SWT.RESIZE | SWT.MAX);
					selectFile.setText("jar包列表");
					selectFile.setLayout(new FillLayout());
					// 创建表
					Table tab2 = new Table(selectFile, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK);
					tab2.setHeaderVisible(true);
					tab2.setLinesVisible(true);
					tab2.clearAll();
					creatJarsTab(tab2, jars, paths);
					jars.clear();
					paths.clear();
					// 添加表格的操作事件
					selectFile.open();
					selectFile.layout();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
			}
		});
	}

	// 创建主表的表格
	public void creatMainTab(Table tab) {
		// 設置表格頭
		String[] heards = { "ID", "jar包名稱", "PID", "状态", "操作" };
		for (int i = 0; i < heards.length; i++) {
			TableColumn column = new TableColumn(tab, SWT.LEFT);
			column.setText(heards[i]);
			if (i == 0 || i == 2 || i == 3) {
				column.setWidth(80);
			} else if (i == 4) {
				column.setWidth(150);
			} else {
				column.setWidth(350);
			}
			column.setResizable(true);
		}
		// TODO获取系统的Java执行的线程
		 lst = cmd.runCmd("jps -l");
		if (lst != null && lst.size() > 0) {
			for (int i = 0; i < lst.size(); i++) {
				TableItem item = new TableItem(tab, SWT.NONE);
				item.setText(0, String.valueOf(i));
				item.setText(1, lst.get(i)[1]);
				item.setText(2, lst.get(i)[0]);
				item.setText(3, "启动");
				item.setData(i);
				// 添加按钮
				addItemStop(item, i, tab);
			}
		}
	}

	// 对主表格添加线程停止事件
	public void addItemStop(TableItem item, int i, Table table) {
		Button btn = new Button(table, 0);
		btn.setText("停止");
		btn.setData(i);
		btnHm.put(i, btn);
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.minimumWidth = 10;
		editor.minimumHeight = 20;
		editor.setEditor(btn, item, 4);// 将按钮放在第四列
		editorHm.put(i, editor);
		itemHm.put(i, item);
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				int bIndex = Integer.valueOf(arg0.widget.getData() + "");
				TableItem[] items = table.getItems();
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					log.info("选中的jar包为： " + item.getText(1));
					int index = Integer.valueOf(item.getData() + "");
					if (index == bIndex) {
						editorHm.get(i).dispose();
						String pid = item.getText(2);
						log.info("找到了该线程为：  " + pid);
						// 使用CMD命令停止线程
						StartCmd startCmd = new StartCmd();
						startCmd.runCmd("taskkill /f /pid " + pid);
						item.setText(3, "停止");
						item.setText(2, "");
						// table.remove(i);
						break;
					}
				}
				// 一下三种都可以达到刷新的效果
				table.notifyListeners(SWT.KeyDown, null);
			}
		});
	}

	// 创建获取jar的表格
	public void creatJarsTab(Table tab2, List<String> jars, List<String> paths) {
		// 設置表格頭
		String[] heards = { "ID", "jar包名稱", "jar包絕對路徑", "状态", "操作" };
		for (int i = 0; i < heards.length; i++) {
			TableColumn column = new TableColumn(tab2, SWT.LEFT);
			column.setText(heards[i]);
			if (i == 0 || i == 3) {
				column.setWidth(50);
			} else if (i == 4) {
				column.setWidth(150);
			} else {
				column.setWidth(350);
			}
			column.setResizable(true);
		}
		for (int j = 0; j < jars.size(); j++) {
			TableItem item = new TableItem(tab2, SWT.NONE);
			item.setText(0, String.valueOf(j));
			item.setText(1, jars.get(j));
			item.setText(2, paths.get(j));
			item.setText(3, "停止");
			item.setData(j);
			// 添加按钮
			addItemEvent(item, j, tab2);
		}
	}

	// 在jar表格详情中添加启动事件
	public void addItemEvent(TableItem item, int i, Table table) {
		Button btn = new Button(table, 0);
		btn.setText("启动");
		btn.setData(i);
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.minimumWidth = 10;
		editor.minimumHeight = 20;
		editor.setEditor(btn, item, 4);// 将按钮放在第四列
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				int bIndex = Integer.valueOf(arg0.widget.getData() + "");
				TableItem[] items = table.getItems();
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					String text = item.getText(2);
					log.info("选中的jar包为： " + item.getText(1));
					int index = Integer.valueOf(item.getData() + "");
					if (index == bIndex) {
						log.info("启动jar包的路径为：" + text);
						// 创建线程启动jar文件
						ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
						fixedThreadPool.submit(new Callable<Object>() {
							@Override
							public Object call() throws Exception {//
								StartCmd startCmd = new StartCmd();
								startCmd.runJar(" javaw -jar " + text);
								return null;
							}
						});
						item.setText(3, "启动");
						break;
					}
				}
				// 一下三种都可以达到刷新的效果
				table.notifyListeners(SWT.KeyDown, null);
			}
		});
	}

}
