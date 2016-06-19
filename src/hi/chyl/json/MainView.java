package hi.chyl.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ListDataEvent;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.netbeans.swing.tabcontrol.DefaultTabDataModel;
import org.netbeans.swing.tabcontrol.TabData;
import org.netbeans.swing.tabcontrol.TabDataModel;
import org.netbeans.swing.tabcontrol.TabbedContainer;
import org.netbeans.swing.tabcontrol.event.ComplexListDataEvent;
import org.netbeans.swing.tabcontrol.event.ComplexListDataListener;

public class MainView extends FrameView {
    private JDialog     aboutBox;
    private TabDataModel tabDataModel;
    private TabbedContainer  tabbedContainer;
    private Map jsonEleTreeMap = new HashMap();
    private boolean isTxtFindDlgOpen = false;
    private boolean isTreeFinDlgdOpen = false;
    private List<TreePath> treePathLst = new ArrayList<TreePath>();
    private char dot = 30;
    private int curPos = 0;
    private ResourceMap resourceMap;
    
    public MainView(SingleFrameApplication app) {
        super(app);
        resourceMap = Application.getInstance(MainApp.class).getContext().getResourceMap(MainView.class);
        initUI();
    }


    private void initUI(){
        
        Image ico = new ImageIcon(getClass().getResource("resources/json.png")).getImage();
        getFrame().setIconImage(ico);
        setToolBar(createToolBar());
        setMenuBar(createMenuBar());
        initTabbedContainer();
        setComponent(tabbedContainer);
    }

    private JToolBar createToolBar(){
        JToolBar toolbar = new JToolBar();
        final JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(180,25));
        JButton btnAppTitle = new JButton("标题修改");
        JButton btnFormat = new JButton("格式化(F)");
        JButton btnClean = new JButton("清空(D)");
        JButton btnParse = new JButton("粘帖(V)");
        JButton btnNewLine = new JButton("清除(\\n)");
        JButton btnXG = new JButton("清除(\\)");
        JButton btnTxtFind = new JButton("文本查找");
        JButton btnNodeFind = new JButton("节点查找");
        JButton btnNewTab = new JButton("新标签(N)");
        JButton btnSelTabName = new JButton("标签名修改");
        JButton btnCloseTab = new JButton("关闭标签");
        
        btnFormat.setBackground(Color.GREEN);

        btnAppTitle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(textField.getText());
                getFrame().setTitle(textField.getText());
            }
        });
        btnFormat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                formatJson();
            }
        });

        btnClean.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                  JTextArea ta = getTextArea();
                  if(ta!=null){
                      ta.setText("");
                  }

            }
        });
        
        btnParse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 JTextArea ta = getTextArea();
                 if(ta!=null){
                      ta.paste();
                      formatJson();
                 }
            }
        });

        btnNewLine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextArea ta = getTextArea();
                 if(ta!=null){
                      ta.setText(ta.getText().replaceAll("\n", ""));
                 }
            }
        });

        btnXG.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 JTextArea ta = getTextArea();
                 if(ta!=null){
                      ta.setText(ta.getText().replaceAll("\\\\", ""));
                 }
            }
        });

        btnTxtFind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(isTxtFindDlgOpen) return;
                showFindDialog(1, "文本查找对话框");
            }
        });

        btnNodeFind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(isTreeFinDlgdOpen) return;
                showFindDialog(2, "树节点查找对话框");
            }
        });

        btnSelTabName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selIndex = getTabIndex();
                if(selIndex>=0){
                    tabDataModel.setText(selIndex, textField.getText());
                    System.out.println("Modify HashCode : " + getTree(selIndex).hashCode() + " . TabTitle : " + textField.getText() + " !");
                }
            }
        });

        btnNewTab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTab("NewTab",true);
            }
        });

        btnCloseTab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selIndex = getTabIndex();
                if(selIndex >= 0){
                    tabDataModel.removeTab(selIndex);
                }
            }
        });
        toolbar.add(btnNewTab);
        toolbar.add(btnCloseTab);
        toolbar.add(btnFormat);
        toolbar.add(btnClean);
        toolbar.add(btnParse);
        toolbar.add(btnNewLine);
        toolbar.add(btnXG);
        toolbar.add(btnNodeFind);
        toolbar.add(btnTxtFind);
        toolbar.addSeparator(new Dimension(30, 20));
        toolbar.add(textField);
        toolbar.add(btnAppTitle);
        toolbar.add(btnSelTabName);
        return toolbar;
    }

    private int getTabIndex(){
        return tabbedContainer.getSelectionModel().getSelectedIndex();
    }

    private JMenuItem createMenuItem(String name,int keyCode){
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK));
        menuItem.setText(resourceMap.getString(name+".text"));
        return menuItem;
    }

    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenu editMenu = new JMenu();
        JMenu toolMenu = new JMenu();
        JMenu helpMenu = new JMenu();

        menuBar.setName("menuBar");

       
        fileMenu.setText(resourceMap.getString("fileMenu.text"));
        fileMenu.setName("fileMenu");

        JMenuItem menuItemOpenFile = createMenuItem("menuItemOpenFile", KeyEvent.VK_O);
        menuItemOpenFile.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileAction(getTextArea());
            }
        });
        fileMenu.add(menuItemOpenFile);
        
        JMenuItem menuItemSaveFile = createMenuItem("menuItemSaveFile", KeyEvent.VK_S);
        menuItemSaveFile.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileAction(getTextArea());
            }
        });
        fileMenu.add(menuItemSaveFile);



        JMenuItem exitMenuItem = new JMenuItem();
        ActionMap actionMap = Application.getInstance(MainApp.class).getContext().getActionMap(MainView.class, this);
        exitMenuItem.setAction(actionMap.get("quit"));
        exitMenuItem.setName("exitMenuItem");
        exitMenuItem.setText(resourceMap.getString("exitMenu.text"));
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setText(resourceMap.getString("editMenu.text"));

        JMenuItem menuItemClean = createMenuItem("menuItemClean", KeyEvent.VK_D);
        menuItemClean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getTextArea().setText("");
            }
        });
        editMenu.add(menuItemClean);

        JMenuItem menuItemFormat = createMenuItem("menuItemFormat", KeyEvent.VK_F);
        menuItemFormat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                formatJson();
            }
        });
        editMenu.add(menuItemFormat);

        menuBar.add(editMenu);

        toolMenu.setText(resourceMap.getString("toolMenu.text"));

        JMenuItem menuItemLayout = createMenuItem("menuItemLayout", KeyEvent.VK_L);
        menuItemLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLayout();
            }
        });
        toolMenu.add(menuItemLayout);

        

        JMenuItem menuItemNew = createMenuItem("menuItemNew", KeyEvent.VK_N);
        menuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTab("NewTab", true);
            }
        });
        toolMenu.add(menuItemNew);

        JMenuItem menuItemCode = createMenuItem("menuItemCode", KeyEvent.VK_T);
        menuItemCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codeChangeAction();
            }
        });
        toolMenu.add(menuItemCode);

        menuBar.add(toolMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text"));
        helpMenu.setName("helpMenu");

        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText(resourceMap.getString("aboutMenu.text"));
        aboutMenuItem.setName("aboutMenuItem");
        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutBox();
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        return menuBar;
    }


    private void initTabbedContainer() {
        TabData tabData = newTabData("Welcome!","This is a Tab!",null);
	tabDataModel = new DefaultTabDataModel(new TabData[] { tabData });
	tabbedContainer = new TabbedContainer(tabDataModel,TabbedContainer.TYPE_EDITOR);
        tabbedContainer.getSelectionModel().setSelectedIndex(0);
        tabbedContainer.setShowCloseButton(true);
        tabDataModel.addComplexListDataListener(new ComplexListDataListener() {
            public void indicesAdded(ComplexListDataEvent clde) {}
            public void indicesRemoved(ComplexListDataEvent clde) {}
            public void indicesChanged(ComplexListDataEvent clde) {}
            public void intervalAdded(ListDataEvent e) {}
            public void intervalRemoved(ListDataEvent e) {
                ComplexListDataEvent ce = (ComplexListDataEvent)e;
                TabData[] tbArr = ce.getAffectedItems();
                if(tbArr!=null && tbArr.length>0){
                     tbArr[0].getText();
                     JTree tree = getTree(tbArr[0]);
                     if(tree!=null){
                         jsonEleTreeMap.remove(tree.hashCode());
                         System.out.println("Remove HashCode: "+ tree.hashCode() + ". Close Tab: " + tbArr[0].getText() + " !");
                     }
                }
            }
            public void contentsChanged(ListDataEvent e) {}
        });

        tabbedContainer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //System.out.println("@@@:TabbedContainerActionCommand = "+e.getActionCommand());
                if("select".equalsIgnoreCase(e.getActionCommand())){
                    treePathLst.clear();
                }
            }
        });

    }

    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = MainApp.getApplication().getMainFrame();
            aboutBox = new MainAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        MainApp.getApplication().show(aboutBox);
    }

    private TabData newTabData(String tabName,String tabTip,Icon icon){
        final JSplitPane splitPane = new JSplitPane();
        splitPane.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                 splitPane.setDividerLocation(0.45);
            }
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}
            public void componentHidden(ComponentEvent e) {}
        });

        RSyntaxTextArea textArea = newTextArea();
       
//        textArea.set
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setFoldIndicatorEnabled(true);
        splitPane.setLeftComponent(sp);
        //splitPane.setLeftComponent(new JScrollPane(textArea));

        final JSplitPane rightSplitPane = new JSplitPane();
        rightSplitPane.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                int w = rightSplitPane.getWidth();
                if(w>500){
                    rightSplitPane.setDividerLocation((w-220)/(w*1.0f));
                }else{
                    rightSplitPane.setDividerLocation(0.8);
                }
            }
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}
            public void componentHidden(ComponentEvent e) {}
        });
        
        JTree tree = newTree();

        rightSplitPane.setLeftComponent(new JScrollPane(tree));
        JTable table = newTable();
        rightSplitPane.setRightComponent(new JScrollPane(table));

        splitPane.setRightComponent(rightSplitPane);
        
        TabData tabData = new TabData(splitPane, icon, tabName, tabTip);
        
        return tabData;
    }

    private RSyntaxTextArea newTextArea(){
//        JTextArea textArea = new JTextArea();
//        textArea.setAutoscrolls(true);
////      textArea.getDocument().addUndoableEditListener(undoMg);
//        textArea.addMouseListener(new TextAreaMouseListener());
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setAutoscrolls(true);

        SyntaxScheme scheme = textArea.getSyntaxScheme();
//        scheme.getStyle(Token.COMMENT_KEYWORD).foreground = Color.red;
//      scheme.getStyle(Token.DATA_TYPE).foreground = Color.blue;
        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.BLUE;
        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = new Color(164, 0, 0);
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = new Color(164, 0, 0);
        scheme.getStyle(Token.LITERAL_BOOLEAN).foreground = Color.RED;
        scheme.getStyle(Token.OPERATOR).foreground = Color.BLACK;
        textArea.revalidate();
        textArea.addMouseListener(new TextAreaMouseListener());
       
        return textArea;
    }

    private JTree newTree(){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("o-JSON");
        DefaultTreeModel model = new DefaultTreeModel(root);
        JTree tree = new JTree(model);
        tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeSelection(getTree(),getTable());
            }
        });
        setNodeIcon(tree);
        tree.addMouseListener(new TreeMouseListener(tree));
        System.out.println("New HashCode : " + tree.hashCode());
        return tree;
    }

    private JTable newTable(){
        String col[] ={"key","value"};
        DefaultTableModel tm = new DefaultTableModel();
        tm.setColumnCount(2);
        tm.setColumnIdentifiers(col);
        JTable table = new JTable(tm);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoscrolls(true);
        table.setMinimumSize(new Dimension(160, 100));
        return table;
    }

    private void treeSelection(JTree tree,JTable table){
        DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if(selNode==null){
            //System.out.println("jTree1ValueChanged:selNode is null");
            return;
        }
        String col[] ={"key","value"};
        DefaultTableModel tm = (DefaultTableModel)table.getModel();
        tm.setColumnCount(2);
        tm.setColumnIdentifiers(col);
        if(selNode.isLeaf()){
            tm.setRowCount(1);
            String arr[]  = Kit.pstr(selNode.toString());
            tm.setValueAt(arr[1], 0, 0);
            tm.setValueAt(arr[2], 0, 1);
        } else {
            int childCount = selNode.getChildCount();
            tm.setRowCount(childCount);
            for(int i = 0; i < childCount; i++){
                String arr[]  = Kit.pstr(selNode.getChildAt(i).toString());
                tm.setValueAt(arr[1], i, 0);
                tm.setValueAt(arr[2], i, 1);
            }
        }
        table.setModel(tm);
        TableColumn column0 = table.getColumnModel().getColumn(0);
        column0.setPreferredWidth(getPreferredWidthForColumn(table,column0));
        TableColumn column1 = table.getColumnModel().getColumn(1);
        column1.setPreferredWidth(getPreferredWidthForColumn(table,column1));
        table.updateUI();
    }

    private int addTab(String tabName,boolean isSel){
        TabData  tabData = newTabData(tabName,tabName,null);
        int newIndex = tabbedContainer.getTabCount();
        tabDataModel.addTab(newIndex, tabData);
        if(isSel){
            tabbedContainer.getSelectionModel().setSelectedIndex(newIndex);
        }
        return newIndex;
    }

    private JTextArea getTextArea(){
        int selIndex = getTabIndex();
        if(selIndex >= 0){
            TabData selTabData = tabDataModel.getTab(selIndex);
            JSplitPane selSplitPane = (JSplitPane)selTabData.getComponent();
            JScrollPane sp = (JScrollPane)selSplitPane.getLeftComponent();
            JViewport vp = (JViewport)sp.getComponent(0);
            JTextArea ta = (JTextArea)vp.getComponent(0);
            return ta;
        }
        return null;
    }

     private JTree getTree(TabData tabData){
         if(tabData==null){
             return null;
         }
         JSplitPane selSplitPane = (JSplitPane)tabData.getComponent();
         JSplitPane rightSplitPane = (JSplitPane)selSplitPane.getRightComponent();
         JScrollPane sp = (JScrollPane)rightSplitPane.getLeftComponent();
         JViewport vp = (JViewport)sp.getComponent(0);
         JTree t = (JTree)vp.getComponent(0);
         return t;
     }

    private JTree getTree(int tabIndex){
        if(tabIndex >= 0){
            TabData selTabData = tabDataModel.getTab(tabIndex);
            return getTree(selTabData);
        }
        return null;
    }

    private JTree getTree(){
        return getTree(getTabIndex());
    }

    private JTable getTable(int tabIndex){
        if(tabIndex >= 0){
            TabData selTabData = tabDataModel.getTab(tabIndex);
            JSplitPane selSplitPane = (JSplitPane)selTabData.getComponent();
            JSplitPane rightSplitPane = (JSplitPane)selSplitPane.getRightComponent();
            JScrollPane sp = (JScrollPane)rightSplitPane.getRightComponent();
            JViewport vp = (JViewport)sp.getComponent(0);
            JTable t = (JTable)vp.getComponent(0);
            return t;
        }
        return null;
    }

    private JTable getTable(){
        return getTable(getTabIndex());
    }

    private void formatJson(){
        //格式化字符串
        JsonElement jsonEle = null;
        JTextArea ta = getTextArea();
        String text = ta.getText();
        try {
            JsonParser parser = new JsonParser();
            
            Object obj = JSON.parse(text);
            text = JSON.toJSONString(obj,SerializerFeature.WriteMapNullValue);
            jsonEle =  parser.parse(text);
            if(jsonEle!=null && !jsonEle.isJsonNull()){
                GsonBuilder gb = new GsonBuilder();
                gb.setPrettyPrinting();
                gb.serializeNulls();
                Gson gson = gb.create();
                String jsonStr = gson.toJson(jsonEle);
                if(jsonStr!=null){
                    jsonStr = StringEscapeUtils.unescapeJava(jsonStr);
                    ta.setText(jsonStr);
                }
            }else{
                showMessageDialog("非法JSON字符串！","是否缺少开始“{”或结束“}”？");
            }
        }catch (Exception ex) {
            showMessageDialog("非法JSON字符串！",ex.getMessage());
            return;
        }
        System.gc();

        //创建树节点
        JTree tree = getTree();
        System.out.println("Put HashCode : " + tree.hashCode() + " . TabTitle : " + getTabTitle() +  " !");
        jsonEleTreeMap.put(tree.hashCode(), jsonEle);
        DefaultMutableTreeNode root = Kit.objNode("JSON");
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        try {
            createJsonTree(jsonEle, root);
            model.setRoot(root);
            setNodeIcon(tree);
        } catch (Exception ex) {
            root.removeAllChildren();
            model.setRoot(root);
            showMessageDialog("创建json树失败！",ex.getMessage());
        }
        System.gc();
    }

    private String getTabTitle(){
        return tabDataModel.getTab(getTabIndex()).getText();
    }

    /**
     * 构造json树结构.
     * @param obj JsonElement
     * @param pNode DefaultMutableTreeNode
     */
    private void createJsonTree(JsonElement obj, DefaultMutableTreeNode pNode){
            if(obj.isJsonNull()){
                pNode.add(Kit.nullNode("NULL"));
            }else if(obj.isJsonArray()){
                createJsonArray(obj.getAsJsonArray(),pNode,"[0]");
            }else if(obj.isJsonObject()){
                JsonObject child = obj.getAsJsonObject();
               // DefaultMutableTreeNode node = Kit.objNode(key);
                createJsonObject(child,pNode);
               // pNode.add(node);
            }else if(obj.isJsonPrimitive()){
                JsonPrimitive pri = obj.getAsJsonPrimitive();
                formatJsonPrimitive("PRI",pri,pNode);
            }
    }
    

    /**
     * 处理json数组.
     * @param arr
     * @param pNode
     * @param key
     */
    private void createJsonArray(JsonArray arr,DefaultMutableTreeNode pNode,String key){
        int index = 0;
        DefaultMutableTreeNode child = Kit.arrNode(key);
        for (Iterator it = arr.iterator(); it.hasNext();) {
            JsonElement el = (JsonElement)it.next();
            if(el.isJsonObject()){
                JsonObject  obj = el.getAsJsonObject();
                DefaultMutableTreeNode node = Kit.objNode(index);
                createJsonObject(obj, node);
                child.add(node);
            }else if(el.isJsonArray()){
                JsonArray lst = el.getAsJsonArray();
                createJsonArray(lst,child,Kit.fkey(index));
            }else if(el.isJsonNull()){
                child.add(Kit.nullNode(index));
            }else if(el.isJsonPrimitive()){
                formatJsonPrimitive(Kit.fkey(index),el.getAsJsonPrimitive(),child);
            }
            ++index;
        }
        pNode.add(child);
    }

    /**
     * 处理jsoon对象.
     * @param obj
     * @param pNode
     */
    private void createJsonObject(JsonObject obj, DefaultMutableTreeNode pNode){
        for(Map.Entry<String, JsonElement> el : obj.entrySet()){
            String key = el.getKey();
            JsonElement val = el.getValue();
            if(val.isJsonNull()){
                pNode.add(Kit.nullNode(key));
            }else if(val.isJsonArray()){
                createJsonArray(val.getAsJsonArray(),pNode,key);
            }else if(val.isJsonObject()){
                JsonObject child = val.getAsJsonObject();
                DefaultMutableTreeNode node = Kit.objNode(key);
                createJsonObject(child,node);
                pNode.add(node);
            }else if(val.isJsonPrimitive()){
                JsonPrimitive pri = val.getAsJsonPrimitive();
                formatJsonPrimitive(key,pri,pNode);
            }
        }

    }

    private void formatJsonPrimitive(String key, JsonPrimitive pri, DefaultMutableTreeNode pNode) {
        if(pri.isJsonNull()){
            pNode.add(Kit.nullNode(key));
        }else if (pri.isNumber()) {
            pNode.add(Kit.numNode(key ,pri.getAsString()));
        }else if (pri.isBoolean()) {
            pNode.add(Kit.boolNode(key,pri.getAsBoolean()));
        }else if (pri.isString()) {
            pNode.add(Kit.strNode(key, pri.getAsString()));
        }else if(pri.isJsonArray()){
            createJsonArray(pri.getAsJsonArray(),pNode,key);
        }else if(pri.isJsonObject()){
                JsonObject child = pri.getAsJsonObject();
                DefaultMutableTreeNode node = Kit.objNode(key);
                createJsonObject(child,node);
                pNode.add(node);
        }else if(pri.isJsonPrimitive()){
            formatJsonPrimitive(key,pri.getAsJsonPrimitive(),pNode);
        }
    }

    private void setNodeIcon(JTree tree){
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,int row, boolean hasFocus) {
                  super.getTreeCellRendererComponent(tree, value, sel, expanded,leaf, row, hasFocus);
                  DefaultMutableTreeNode   node   =   (DefaultMutableTreeNode)value;
                  String tmp = node.toString();
                  if(tmp.startsWith(Kit.sArr)){
                      this.setIcon(new ImageIcon(getClass().getResource("resources/a.gif")));
                      this.setText(tmp.substring(2));
                  }else if(tmp.startsWith(Kit.sStr)){
                      this.setIcon(new ImageIcon(getClass().getResource("resources/v.gif")));
                      this.setText(tmp.substring(2));
                  }else if(tmp.startsWith(Kit.sObj)){
                      this.setIcon(new ImageIcon(getClass().getResource("resources/o.gif")));
                      this.setText(tmp.substring(2));
                  }else if(tmp.startsWith(Kit.sNum)){
                      this.setIcon(new ImageIcon(getClass().getResource("resources/n.gif")));
                      this.setText(tmp.substring(2));
                  }else if(tmp.startsWith(Kit.sNull)){
                      this.setIcon(new ImageIcon(getClass().getResource("resources/k.gif")));
                      this.setText(tmp.substring(2));
                  }else if(tmp.startsWith(Kit.sBool)){
                      this.setIcon(new ImageIcon(getClass().getResource("resources/v.gif")));
                      this.setText(tmp.substring(2));
                  }else{
                      this.setIcon(new ImageIcon(getClass().getResource("resources/v.gif")));
                      this.setText(tmp.substring(2));
                  }
                  return this;
            }
         });
    }

    private void showMessageDialog(String title,String msg){
        if(msg==null) msg = "";
        String ex = "com.google.gson.stream.MalformedJsonException:";
        int index = msg.indexOf(ex);
        if(index>=0){
            msg = msg.substring(index+ex.length());
        }
        JDialog dlg = new JDialog(getFrame());
        dlg.setTitle(title);
        dlg.setMinimumSize(new Dimension(350, 160));
        BorderLayout layout = new BorderLayout();
        dlg.getContentPane().setLayout(layout);
        dlg.getContentPane().add(new JLabel("异常信息："),  BorderLayout.NORTH);
        JTextArea ta = new JTextArea();
        ta.setLineWrap(true);
        ta.setText(msg);
        ta.setWrapStyleWord(true);
        dlg.getContentPane().add(new JScrollPane(ta), BorderLayout.CENTER);
        MainApp.getApplication().show(dlg);
    }

    //[start]自动调列宽
    private int getPreferredWidthForColumn(JTable table,TableColumn col) {
        int hw = columnHeaderWidth(table,col);  // hw = header width
        int cw = widestCellInColumn(table,col);  // cw = column width
        return hw > cw ? hw : cw;

    }
    private int columnHeaderWidth(JTable table,TableColumn col) {
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
        return comp.getPreferredSize().width;
    }
    private int widestCellInColumn(JTable table,TableColumn col) {
        int c = col.getModelIndex();
        int width = 0, maxw = 0;
        for (int r = 0; r < table.getRowCount(); r++) {
            TableCellRenderer renderer = table.getCellRenderer(r, c);
            Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, c), false, false, r, c);
            width = comp.getPreferredSize().width;
            maxw = width > maxw ? width : maxw;
        }
        if(maxw<90)maxw = 90;
        return maxw + 10;
    }
    //[end]自动调列宽

    private void modifyDialgTitle(JDialog dlg,boolean flag,int n){
        String[] tmp = dlg.getTitle().split("-");
        if(n==-1){
            dlg.setTitle(tmp[0] + "-" + "  ==");
            return;
        }
        if (flag) {
            dlg.setTitle(tmp[0] + "-" + "  找到了^_^");
        } else {
            dlg.setTitle(tmp[0] + "-" + "  没找到╮(╯_╰)╭");
        }
    }

    private TreePath expandTreeNode(JTree tree,TreeNode[] arr, Boolean expand) {
        TreePath[] tp = new TreePath[arr.length];
        tp[0] = new TreePath(arr[0]);
        int pos = 0;
        for (int i = 1; i < arr.length; i++) {
            tp[i] = tp[i - 1].pathByAddingChild(arr[i]);
        }
        for (int i = 0; i < arr.length; i++) {
            if (expand) {
                tree.expandPath(tp[i]);
            } else {
                tree.collapsePath(tp[i]);
            }
            pos = i;
        }
        return tp[pos];
    }

    private void findTreeChildValue(String findText,List<TreePath> treePathLst) {
        JTree tree = getTree();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        Enumeration e = root.depthFirstEnumeration();
        treePathLst.clear();
        curPos = 0;
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.isLeaf()) {
                String str = node.toString();
                if (str.substring(2).indexOf(findText) >= 0) {
                    tree.expandPath(new TreePath(node.getPath()));
                    TreePath tp = expandTreeNode(tree,node.getPath(), true);
                    treePathLst.add(tp);
                }
            }
        }
        if(!treePathLst.isEmpty()){
            tree.setSelectionPath(treePathLst.get(0));
            tree.scrollPathToVisible(treePathLst.get(0));
        }
//        return treePathLst;
    }

    /**
     * 打开查找对话框
     * @param type 查找类型（1：文本查找，2树节点查找）
     * @param title 打开的窗口标题名称
     */
    private void showFindDialog(final int type,String title){
        final  JDialog openDlg = new JDialog(getFrame());
        openDlg.setTitle(title);
        openDlg.setModal(false);
        openDlg.setSize(500,70);
        openDlg.setResizable(false);
        java.awt.Container pane =  openDlg.getContentPane();
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
        pane.setLayout(layout);
        JButton btnFind = new JButton("查找");
        JButton btnNext = new JButton("下一个");
        JButton btnPrev = new JButton("上一个");
        final JTextField textFieldFind = new JTextField(50);
        pane.add(textFieldFind);
        pane.add(btnFind);
        pane.add(btnPrev);
        pane.add(btnNext);
        //从头开始查找
        btnFind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean flag = false;
                modifyDialgTitle(openDlg,flag,-1);
                if(type==1){
                    flag =  startSegmentFindOrReplaceOperation(getTextArea(),textFieldFind.getText(), true, true,true);
                }else{
                    findTreeChildValue(textFieldFind.getText(),treePathLst);
                    if(!treePathLst.isEmpty()) flag = true;
                }
                modifyDialgTitle(openDlg,flag,1);
            }
        });
        //向下查找
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean flag = false;
                modifyDialgTitle(openDlg,flag,-1);
                JTree tree = getTree();
                if(type==1){
                    flag =  startSegmentFindOrReplaceOperation(getTextArea(),textFieldFind.getText(), true, true,false);
                }else{
                    curPos++;
                    if(curPos<treePathLst.size()){
                        tree.setSelectionPath(treePathLst.get(curPos));
                        tree.scrollPathToVisible(treePathLst.get(curPos));
                        flag = true;
                    }else{
                        curPos = treePathLst.size() - 1;
                    }
                }
                modifyDialgTitle(openDlg,flag,1);
            }
        });
        //向上查找
        btnPrev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean flag = false;
                JTree tree = getTree();
                modifyDialgTitle(openDlg,flag,-1);
                 if(type==1){
                    flag = startSegmentFindOrReplaceOperation(getTextArea(),textFieldFind.getText(), true, false,false);
                }else{
                    curPos--;
                    if(curPos>=0){
                        tree.setSelectionPath(treePathLst.get(curPos));
                        tree.scrollPathToVisible(treePathLst.get(curPos));
                        flag = true;
                    }else{
                        curPos = 0;
                    }
                }
                modifyDialgTitle(openDlg,flag,1);
            }
        });

        openDlg.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {
                treePathLst.clear();
                if(type == 1){
                    isTxtFindDlgOpen = false;
                }else{
                    isTreeFinDlgdOpen = false;
                }
                System.gc();
            }
            public void windowClosed(WindowEvent e) { }
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        });

        MainApp.getApplication().show(openDlg);

        if(type == 1){
            isTxtFindDlgOpen = true;
        }else{
            isTreeFinDlgdOpen = true;
        }
    }

    /**
     * 文本内容查找定位
     * @param key 要查找的字符串
     * @param ignoreCase 是否区分大小写
     * @param down  查找方向（向上false，向下true）
     * @param isFirst 是否从开头开始查找
     * @return
     */
    public boolean startSegmentFindOrReplaceOperation(JTextArea textArea, String key, boolean ignoreCase, boolean down,boolean isFirst) {
        int length = key.length();
        Document doc = textArea.getDocument();
        int offset = textArea.getCaretPosition();
        int charsLeft = doc.getLength() - offset;
        if(charsLeft <=0 ){
            offset = 0;
            charsLeft = doc.getLength() - offset;
        }
        if (!down) {
            offset -= length;
            offset--;
            charsLeft = offset;
        }
        if(isFirst){
            offset = 0;
            charsLeft = doc.getLength() - offset;
        }
        Segment text = new Segment();
        text.setPartialReturn(true);
        try {
            while (charsLeft > 0) {
                doc.getText(offset, length, text);
                if ((ignoreCase == true && text.toString().equalsIgnoreCase(key))
                        || (ignoreCase == false && text.toString().equals(key))) {
                    textArea.requestFocus();////焦点,才能能看到效果
                    textArea.setSelectionStart(offset);
                    textArea.setSelectionEnd(offset + length);
                    return true;
                }
                charsLeft--;
                if (down) {
                    offset++;
                } else {
                    offset--;
                }

            }
        } catch (Exception e) {

        }
        return false;
    }

    private void changeLayout() {
        int selIndex = getTabIndex();
        if(selIndex < 0){
            return;
        }
        TabData selTabData = tabDataModel.getTab(selIndex);
        JSplitPane splitPane = (JSplitPane)selTabData.getComponent();
        if (splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
            splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setDividerLocation(0.45);
        } else {
            splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane.setDividerLocation(0.45);
        }
    }

    private class TreeMouseListener implements MouseListener {
        private JTree tree;
        public TreeMouseListener(JTree tree){
            this.tree = tree;
        }

        public void mouseClicked(MouseEvent e) {}

        public void mousePressed(MouseEvent e) {}

        public void mouseReleased(MouseEvent e) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path == null)  return;
            tree.setSelectionPath(path);
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (e.isPopupTrigger()) {
                JPopupMenu popMenu = new JPopupMenu();
                JMenuItem copyValue = new JMenuItem("复制 键值");
                JMenuItem copyKey = new JMenuItem("复制 键名");
                JMenuItem copyPath = new JMenuItem("复制 路径");
                JMenuItem copyKeyValue = new JMenuItem("复制 键名键值");
                JMenuItem copyNode = new JMenuItem("复制 节点内容");
                JMenuItem copyPathAllVal = new JMenuItem("复制 同路径键值");
                JMenuItem copySingleNodeString = new JMenuItem("复制 MAP式内容");
                JMenuItem copyNodeFormat = new JMenuItem("复制 节点内容带格式");
                
                popMenu.add(copyKey);
                popMenu.add(copyValue);
                popMenu.add(copyPath);
                popMenu.add(copyNode);
                popMenu.add(copyKeyValue);
                popMenu.add(copySingleNodeString);
                popMenu.add(copyPathAllVal);
                popMenu.add(copyNodeFormat);
                copyKey.addActionListener(new TreeNodeMenuItemActionListener(tree,1, selNode));
                copyValue.addActionListener(new TreeNodeMenuItemActionListener(tree,2, selNode));
                copyKeyValue.addActionListener(new TreeNodeMenuItemActionListener(tree,3, selNode));
                copyPath.addActionListener(new TreeNodeMenuItemActionListener(tree,4, path));
                copyPathAllVal.addActionListener(new TreeNodeMenuItemActionListener(tree,5,selNode));
                copyNode.addActionListener(new TreeNodeMenuItemActionListener(tree,6,path));
                copyNodeFormat.addActionListener(new TreeNodeMenuItemActionListener(tree,7,path));
                copySingleNodeString.addActionListener(new TreeNodeMenuItemActionListener(tree,8,selNode));
                popMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}
    }

    private class TreeNodeMenuItemActionListener implements ActionListener{
        private int optType;
        private Object obj;
        private JTree tree;
        /**
         * optType 1:key;2:value;3:key value
         * @param optType
         * @param str
         */
        public TreeNodeMenuItemActionListener(JTree tree,int optType,Object obj){
            this.optType = optType;
            this.obj = obj;
            this.tree = tree;
        }
        /**
         * 复制节点路径.
         * @param treePath
         * @return 
         */
        public String copyTreeNodePath(TreePath treePath){
            String str = "";
            String s = "";
            int len =  treePath.getPathCount() -1;
            for(int i = 0; i <= len; i++){
                s = treePath.getPathComponent(i).toString();
                if(i>0) str += String.valueOf(dot);
                if(i == len) {
                    str += Kit.pstr(s)[1];
                }else{
                    str += s.substring(2);
                }
            }
            str = StringUtils.replace(str, String.valueOf(dot)+"[", "[");
            str = StringUtils.substring(str, 5);
            return str;
        }
        /**
         * 复制相似路径节点键值对.
         * @param treeNode
         * @return 
         */
        public String copySimilarPathKeyValue(TreeNode treeNode){
            String str = "";
            String key = Kit.pstr(treeNode.toString())[1];
            TreeNode node = treeNode.getParent();
            if(node==null) return "";
            node = node.getParent();
            if(node == null) return "";
            int count = node.getChildCount();
            int size = 0;
            for(int i = 0; i < count; i++){
                TreeNode child = node.getChildAt(i);
                if(child==null) continue;
                size = child.getChildCount();
                for(int i2 = 0; i2 < size; i2++){
                    TreeNode tmp = child.getChildAt(i2);
                    if(tmp==null)continue;
                    String arr[] = Kit.pstr(tmp.toString());
                    if(key!=null && key.equals(arr[1])){
                        str += arr[2] + "\n";
                    }
                }
            }
            return str;
        }
        /**
         * 复制节点内容.
         * @param path 节点路径
         * @param isFormat 是否带格式
         * @return 
         */
        private String copyNodeContent(String path,boolean isFormat){
            String str = "";
            String arr[] = StringUtils.split(path, String.valueOf(dot));
            System.out.println("Get HashCode : " + tree.hashCode() + " . TabTitle : " + getTabTitle());
            JsonElement obj = (JsonElement)jsonEleTreeMap.get(tree.hashCode());
            if(arr.length>1){
                for(int i =1; i< arr.length; i++){
                    int index = Kit.getIndex(arr[i]);
                    String key = Kit.getKey(arr[i]);
                    if(obj.isJsonPrimitive())break;
                    if(index==-1){
                        obj = obj.getAsJsonObject().get(key);
                    }else{
                        obj = obj.getAsJsonObject().getAsJsonArray(key).get(index);
                    }
                }
            }
            if(obj!=null && !obj.isJsonNull()){
                    GsonBuilder gb = new GsonBuilder();
                    if(isFormat) gb.setPrettyPrinting();
                    gb.serializeNulls();
                    Gson gson = gb.create();
                    str = gson.toJson(obj);
            }
            return str;
        }
        public void actionPerformed(ActionEvent e) {
            if(obj==null) return;
            StringSelection stringSelection = null;
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if(optType == 4){
                 String path = copyTreeNodePath((TreePath)obj);
                 path = StringUtils.replace(path, String.valueOf(dot), ".");
                 stringSelection = new StringSelection(path);
                 clipboard.setContents(stringSelection, null);
                 return;
            }
            else if(optType == 5){
                 stringSelection = new StringSelection(copySimilarPathKeyValue((TreeNode)obj));
                 clipboard.setContents(stringSelection, null);
                 return;
            }
            else if(optType == 6 || optType == 7){
                String path = copyTreeNodePath((TreePath)obj);
                boolean isForamt = false;
                if(optType == 7) isForamt = true;
                String str = copyNodeContent(path,isForamt);
                stringSelection = new StringSelection(str);
                clipboard.setContents(stringSelection, null);
                 return;
            }
            else{
                String str = obj.toString();
                String[] arr = Kit.pstr(str);
                 if("<null>".equals(arr[2])){
                        arr[2] = "null";
                 }
                if (optType == 1 || optType == 2){
                    stringSelection = new StringSelection(arr[optType]);
                } else if (optType == 3) {
                    stringSelection = new StringSelection(str.substring(2));
                }else if(optType == 8){
                    String temp = "\"" + arr[1] + "\",\"" + arr[2] + "\"";
                    stringSelection = new StringSelection(temp);
                }
                clipboard.setContents(stringSelection, null);
            }
        }
    }//end TreeNodeCopyActionListener

    private class TextAreaMouseListener implements MouseListener{

        public void mouseClicked(MouseEvent e) {}

        public void mousePressed(MouseEvent e) {}

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                JPopupMenu popMenu = new JPopupMenu();
                JMenuItem mtCopy = new JMenuItem(resourceMap.getString("mtCopy.text"));
                JMenuItem mtPaste = new JMenuItem(resourceMap.getString("mtPaste.text"));
                JMenuItem mtSelAll = new JMenuItem(resourceMap.getString("mtSelAll.text"));
                JMenuItem mtClean = new JMenuItem(resourceMap.getString("mtClean.text"));
                
                popMenu.add(mtCopy);
                popMenu.add(mtPaste);
                popMenu.add(mtSelAll);
                popMenu.add(mtClean);
                JTextArea ta = getTextArea();
                if(ta.getSelectedText() == null || ta.getSelectedText().length()==0){
                    mtCopy.setEnabled(false);
                }

                mtCopy.addActionListener(new TextAreaMenuItemActionListener(1));
                mtPaste.addActionListener(new TextAreaMenuItemActionListener(2));
                mtSelAll.addActionListener(new TextAreaMenuItemActionListener(3));
                mtClean.addActionListener(new TextAreaMenuItemActionListener(4));
                popMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

    }

    private class TextAreaMenuItemActionListener implements ActionListener{
        private int optType;
        private String str;
        /**
         * optType 1:复制;2:粘帖;3:全选;4:清空
         * @param optType
         */
        public TextAreaMenuItemActionListener(int optType){
            this.optType = optType;
        }
        public void actionPerformed(ActionEvent e) {
            if(optType==1){
                getTextArea().copy();
            }else if(optType==2){
                getTextArea().paste();
                formatJson();
            }else if(optType==3){
                getTextArea().selectAll();
            }else if(optType==4){
                getTextArea().setText("");
            }
        }
    }//end TreeNodeCopyActionListener


    private void openFileAction(JTextArea textArea) {
        String title = resourceMap.getString("openDlg.text");
        java.awt.FileDialog openDlg = new java.awt.FileDialog(getFrame(), title, java.awt.FileDialog.LOAD);
        openDlg.setVisible(true);
        File file = new File(openDlg.getDirectory(),openDlg.getFile()); //fc.getSelectedFile();
        if(file==null||file.getPath().length()==0) return;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
            reader = new BufferedReader(isr);
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            reader.close();
        } catch (IOException e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        textArea.setText(sb.toString());
        formatJson();
    }

    private void codeChangeAction(){
        javax.swing.JDialog dlg = new javax.swing.JDialog(getFrame());
        dlg.setTitle(resourceMap.getString("menuItemCode.text"));
        dlg.setSize(500, 350);
        dlg.setMinimumSize(new Dimension(500, 350));
        JSplitPane spiltPane2 = new JSplitPane();
        spiltPane2.setDividerLocation(150);
        spiltPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);

        final JTextArea textAreaSrc = new JTextArea();
        final JTextArea textAreaDest = new JTextArea();
        textAreaSrc.setLineWrap(true);
        textAreaDest.setLineWrap(true);

        spiltPane2.setTopComponent(new JScrollPane(textAreaSrc));
        spiltPane2.setBottomComponent(new JScrollPane(textAreaDest));

        JButton btnOK = new JButton("转换");
        btnOK.setSize(50, 25);
        java.awt.Container pane = dlg.getContentPane();
        BorderLayout layout = new BorderLayout();
        //layout.addLayoutComponent(spiltPane, BorderLayout.CENTER);
       // layout.addLayoutComponent(btnOK, BorderLayout.SOUTH);
        pane.setLayout(layout);
        pane.add(spiltPane2,  BorderLayout.CENTER);
        pane.add(btnOK,  BorderLayout.SOUTH);

        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String str = textAreaSrc.getText();
                str = StringEscapeUtils.unescapeJava(str);
                textAreaDest.setText(str);
            }
        });
        MainApp.getApplication().show(dlg);

    }

    private void saveFileAction(JTextArea textArea) {
 //       JFileChooser open = new JFileChooser();
        String title = resourceMap.getString("closeDlg.text");
        java.awt.FileDialog closeDlg = new java.awt.FileDialog(getFrame(), title, java.awt.FileDialog.SAVE);
        closeDlg.setVisible(true);
        File file = new File(closeDlg.getDirectory(),closeDlg.getFile());
        if(file==null||file.getPath().length()==0) return;
        BufferedWriter write= null;
        StringBuilder sb = new StringBuilder();
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file),"GBK");
            write = new BufferedWriter(osw);
            String text = StringUtils.replace(textArea.getText(), "\n", "\r\n");
            write.write(text, 0, text.length());
            write.close();
        } catch (IOException e) {

        } finally {
            if (write != null) {
                try {
                    write.close();
                } catch (IOException e1) {
                }
            }
        }
    }
}
