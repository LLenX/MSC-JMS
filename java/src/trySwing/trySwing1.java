package trySwing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;

import Epred.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;

public class trySwing1 extends JFrame {

	static boolean input_builded = false;
	static boolean output_builded = false;
	static boolean Epred_builded = false;
	static String ipadd,opadd;
	static EPredMain Epred_sample;
	static boolean pred=false,check=false,ana=false;
	private JPanel contentPane;

	private String[] get_year() {
		Calendar aCalendar = Calendar.getInstance();
		int year =aCalendar.get(Calendar.YEAR);
		String[] recent_Year = new String[7];
		for(int i = (year-3);i<=(year+3);i++){
			recent_Year[i-(year-3)]=Integer.toString(i);
		}
		return recent_Year;
	}
	private String get_Dictionary() {
		JFileChooser fc=new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//只能选择目录
		String path=null;
		File f=null;
		int flag = 0;
		try{     
			flag=fc.showOpenDialog(null);     
			}    
		    catch(HeadlessException head){     
		         System.out.println("Open File Dialog ERROR!");    
		    }        
		if(flag==JFileChooser.APPROVE_OPTION){
		//获得该文件
//			System.out.println(fc.getSelectedFile().toString());
		return(fc.getSelectedFile().toString());
		}
		else return "error";
	
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					trySwing1 frame = new trySwing1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public trySwing1() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 844, 562);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel01 = new JPanel();
		contentPane.add(panel01);
		panel01.setLayout(new BorderLayout(0, 0));
		
		final JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 25));
		textArea.setText("请选择数据的输入输出地址");
		panel01.add(textArea);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 5, 0, 0));
		
		final JCheckBox checkBox = new JCheckBox("预测");
		checkBox.setFont(new Font("宋体", Font.PLAIN, 25));
		checkBox.setEnabled(false);
		panel_1.add(checkBox);
		
		final JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setFont(new Font("宋体", Font.PLAIN, 20));
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"请选择","全社会用电量","分镇街用电量"}));
		comboBox_1.setBorder(BorderFactory.createTitledBorder("分类"));
		comboBox_1.setEnabled(false);
		panel_1.add(comboBox_1);
		
		final JComboBox comboBox = new JComboBox(get_year());
		comboBox.setFont(new Font("宋体", Font.PLAIN, 25));
//		comboBox.setModel(new DefaultComboBoxModel(new String[] {"年度"}));
		comboBox.setBorder(BorderFactory.createTitledBorder("年度"));
		comboBox.insertItemAt("请选择", 0);
		comboBox.setSelectedItem("请选择");
		comboBox.setEnabled(false);
		panel_1.add(comboBox);
		
		final JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setBorder(BorderFactory.createTitledBorder("时间跨度"));
		comboBox_2.setEnabled(false);
		panel_1.add(comboBox_2);
		
		//“预测”功能列的最后一项，在确认时间跨度后才赋值
		final JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setFont(new Font("宋体", Font.PLAIN, 20));
		comboBox_3.setBorder(BorderFactory.createTitledBorder("  "));
		comboBox_3.setEnabled(false);
		panel_1.add(comboBox_3);
		
		JPanel panel02 = new JPanel();
		contentPane.add(panel02);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2);
		panel_2.setLayout(new GridLayout(0, 5, 0, 0));
		
		final JCheckBox checkBox_1 = new JCheckBox("精度检验");
		
		checkBox_1.setFont(new Font("宋体", Font.PLAIN, 25));
		checkBox_1.setEnabled(false);
		panel_2.add(checkBox_1);
		
		//选择检验的内容
		final JComboBox comboBox_4 = new JComboBox();
		comboBox_4.setFont(new Font("宋体", Font.PLAIN, 20));
		comboBox_4.setModel(new DefaultComboBoxModel(new String[] {"请选择","全社会用电量","分镇街用电量","副模型"}));
		comboBox_4.setBorder(BorderFactory.createTitledBorder("分类"));
		comboBox_4.setEnabled(false);
		panel_2.add(comboBox_4);
		
		final JComboBox comboBox_5 = new JComboBox(get_year());
		comboBox_5.setFont(new Font("宋体", Font.PLAIN, 25));
//		comboBox_5.setModel(new DefaultComboBoxModel(new String[] {"年度"}));
		comboBox_5.setBorder(BorderFactory.createTitledBorder("年度"));
		comboBox_5.insertItemAt("请选择", 0);
		comboBox_5.setSelectedItem("请选择");
		comboBox_5.setEnabled(false);
		panel_2.add(comboBox_5);
		
		final JComboBox comboBox_6 = new JComboBox();
		comboBox_6.setFont(new Font("宋体", Font.PLAIN, 20));
		comboBox_6.setBorder(BorderFactory.createTitledBorder("时间跨度"));
		comboBox_6.setEnabled(false);
		panel_2.add(comboBox_6);
		
		final JComboBox comboBox_7 = new JComboBox();
		comboBox_7.setFont(new Font("宋体", Font.PLAIN, 20));
		comboBox_7.setBorder(BorderFactory.createTitledBorder("  "));
		comboBox_7.setEnabled(false);
		panel_2.add(comboBox_7);
		
		JPanel panel03 = new JPanel();
		contentPane.add(panel03);
		
		JPanel panel_3 = new JPanel();
		contentPane.add(panel_3);
		panel_3.setLayout(new GridLayout(0, 5, 0, 0));
		
		final JCheckBox checkBox_2 = new JCheckBox("关联性分析");
		
		checkBox_2.setFont(new Font("宋体", Font.PLAIN, 25));
		checkBox_2.setEnabled(false);
		panel_3.add(checkBox_2);
		
		JPanel panel04 = new JPanel();
		contentPane.add(panel04);
		
		JPanel panel_4 = new JPanel();
		contentPane.add(panel_4);
		panel_4.setLayout(new GridLayout(0, 3, 0, 0));
		
		final JButton button = new JButton("文件输入地址");
		button.setFont(new Font("宋体", Font.PLAIN, 25));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					ipadd = get_Dictionary().replace("\\", "/");
//					System.out.println(ipadd);
					input_builded = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(input_builded&&output_builded){
					checkBox.setEnabled(true);
					checkBox_1.setEnabled(true);
					checkBox_2.setEnabled(true);
//					if(Epred_sample==null){
//						Epred_sample = new EPredMain(opadd+"/Data/", opadd+"/Result/", opadd+"/Rfile/", opadd+"/Report/", ipadd+'/');
//						textArea.setText("地址初始化成功，请勾选所需要的功能，按“下一步”结束");
//					}
					Epred_sample = new EPredMain(opadd+"/Result/",opadd+"/Report/",
							ipadd+'/',opadd+"/Data4r/",ipadd+"/Rfile/",ipadd+"/Model/");
					textArea.setText("地址初始化成功，请勾选所需要的功能，按“下一步”结束");

				}
			}
		});
		panel_4.add(button);
		
		final JButton button_1 = new JButton("文件保存地址");
		button_1.setFont(new Font("宋体", Font.PLAIN, 25));
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg1) {
				try {
					opadd = get_Dictionary().replace("\\", "/");
//					System.out.println(opadd);
					output_builded = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(input_builded&&output_builded){
					checkBox.setEnabled(true);
					checkBox_1.setEnabled(true);
					checkBox_2.setEnabled(true);
//					if(Epred_sample==null){
//						Epred_sample = new EPredMain(opadd+"/Data/", opadd+"/Result/", opadd+"/Rfile/", opadd+"/Report/", ipadd+'/');
//						textArea.setText("地址初始化成功，请勾选所需要的功能，按“下一步”结束");
//					}
					Epred_sample = new EPredMain(opadd+"/Result/", opadd+"/Report/", 
							ipadd+'/',opadd+"/Data4r/",ipadd+"/Rfile/",ipadd+"/Model/");
					textArea.setText("地址初始化成功，请勾选所需要的功能，按“下一步”结束");
				}
			}
		});
		panel_4.add(button_1);
		
		final JButton btnNewButton = new JButton("运行");
		
		btnNewButton.setFont(new Font("宋体", Font.PLAIN, 25));
		btnNewButton.setEnabled(false);
		panel_4.add(btnNewButton);
		
		JPanel panel05 = new JPanel();
		contentPane.add(panel05);
		
		//若选择预测功能，则后续选框可点击
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox.isSelected()){
					comboBox_1.setEnabled(true);
				}
				else {
					comboBox.setEnabled(false);
					comboBox_1.setEnabled(false);
					comboBox_2.setEnabled(false);
					comboBox_3.setEnabled(false);
					pred=false;
				}
				if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
				}

			}
		});
		
		//若选择了预测的类别，则可以选择年份
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(comboBox_1.getSelectedItem().equals("全社会用电量")){
					comboBox.setEnabled(true);
					comboBox_2.setFont(new Font("宋体", Font.PLAIN, 20));
					comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"请选择","年度","半年度","季度"}));
					comboBox_3.setEnabled(false);
					pred=false;
				}
				else if(comboBox_1.getSelectedItem().equals("分镇街用电量")){
					comboBox.setEnabled(true);
					comboBox_2.setFont(new Font("宋体", Font.PLAIN, 20));
					comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"请选择","年度","半年度"}));
					comboBox_3.setEnabled(false);
					pred=false;
				}
				else if(comboBox_1.getSelectedItem().equals("请选择")){
					comboBox.setEnabled(false);
					comboBox_2.setEnabled(false);
					comboBox_3.setEnabled(false);
					pred=false;
				}
				if(!pred&&!check&&!ana){
					btnNewButton.setEnabled(false);
				}
			}
		});
		
		//选择了预测的年份，可以选择时间跨度
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!comboBox.getSelectedItem().equals("请选择")){
					comboBox_2.setEnabled(true);
				}
				else {
					comboBox_2.setEnabled(false);
					comboBox_3.setEnabled(false);
					pred=false;
				}
				if(!pred&&!check&&!ana){
					btnNewButton.setEnabled(false);
				}
			}
		});
		
		//选择了预测的时间跨度，可以选择具体的半年/季度
		comboBox_2.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {				
				if(comboBox_2.getSelectedItem().equals("半年度")){
					comboBox_3.setFont(new Font("宋体", Font.PLAIN, 20));
					comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"请选择","上半年","下半年"}));
					comboBox_3.setEnabled(true);
					pred=false;
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
				if(comboBox_2.getSelectedItem().equals("季度")){
					comboBox_3.setFont(new Font("宋体", Font.PLAIN, 20));
					comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"请选择","第一季度","第二季度","第三季度","第四季度"}));
					comboBox_3.setEnabled(true);
					pred=false;
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
				if(comboBox_2.getSelectedItem().equals("年度")){
					comboBox_3.setFont(new Font("宋体", Font.PLAIN, 20));
					comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"全年"}));
					comboBox_3.setEnabled(false);
					btnNewButton.setEnabled(true);
					pred=true;
				}
				if(comboBox_2.getSelectedItem().equals("请选择")){
					comboBox_3.setEnabled(false);
					pred=false;
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
			}
		});
		
		//选择预测的年度/半年度/季度，可以开始运行程序了
		comboBox_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!comboBox_3.getSelectedItem().equals("请选择")){
					btnNewButton.setEnabled(true);
					pred=true;
				}
				else {
					pred=false;
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
			}
		});
		
		//勾选检验模块
		checkBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_1.isSelected()){
					comboBox_4.setEnabled(true);
				}
				else {
					comboBox_4.setEnabled(false);
					comboBox_5.setEnabled(false);
					comboBox_6.setEnabled(false);
					comboBox_7.setEnabled(false);
					check=false;
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
			}
		});
		
		//若选择了检验的类别，则可以选择年份
		comboBox_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!comboBox_4.getSelectedItem().equals("请选择")){
					comboBox_5.setEnabled(true);
					if(comboBox_4.getSelectedItem().equals("全社会用电量")){
						comboBox_6.setFont(new Font("宋体", Font.PLAIN, 20));
						comboBox_6.setModel(new DefaultComboBoxModel(new String[] {"请选择","年度","半年度","季度"}));
						comboBox_7.setEnabled(false);
						check=false;
					}
					if(comboBox_4.getSelectedItem().equals("分镇街用电量")){
						comboBox_6.setFont(new Font("宋体", Font.PLAIN, 20));
						comboBox_6.setModel(new DefaultComboBoxModel(new String[] {"请选择","年度","半年度"}));
						comboBox_7.setEnabled(false);
						check=false;
					}
					if(comboBox_4.getSelectedItem().equals("副模型")){
						comboBox_6.setFont(new Font("宋体", Font.PLAIN, 20));
						comboBox_6.setModel(new DefaultComboBoxModel(new String[] {"全年"}));
						comboBox_6.setEnabled(false);
						comboBox_7.setEnabled(false);
//						comboBox_6.setEnabled(true); //不需要再细分
						btnNewButton.setEnabled(true);
					}
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
				else {
					comboBox_5.setEnabled(false);
					comboBox_6.setEnabled(false);
					comboBox_7.setEnabled(false);
					check=false;
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
			}
		});
		
		//选择了检验年份，则可以选择时间区域（全年/半年度/季度）
		comboBox_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(comboBox_5.getSelectedItem().equals("请选择")){
					comboBox_6.setEnabled(false);
					comboBox_7.setEnabled(false);
					check=false;
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
				if(comboBox_4.getSelectedItem().equals("副模型")){
					btnNewButton.setEnabled(true);
					check=true;
				}
				else{
					comboBox_6.setEnabled(true);
				}
			}
		});
		
		//选择检验时间区域，可以选择具体的上下半年/季度
		comboBox_6.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {				
				if(comboBox_6.getSelectedItem().equals("半年度")){
					comboBox_7.setFont(new Font("宋体", Font.PLAIN, 20));
					comboBox_7.setModel(new DefaultComboBoxModel(new String[] {"请选择","上半年","下半年"}));
					comboBox_7.setEnabled(true);
					check=false;
				}
				if(comboBox_6.getSelectedItem().equals("季度")){
					comboBox_7.setFont(new Font("宋体", Font.PLAIN, 20));
					comboBox_7.setModel(new DefaultComboBoxModel(new String[] {"请选择","第一季度","第二季度","第三季度","第四季度"}));
					comboBox_7.setEnabled(true);
					check=false;
				}
				if(comboBox_6.getSelectedItem().equals("年度")){
					comboBox_7.setFont(new Font("宋体", Font.PLAIN, 20));
					comboBox_7.setModel(new DefaultComboBoxModel(new String[] {"全年"}));
					comboBox_7.setEnabled(false);
					check=true;
					btnNewButton.setEnabled(true);
				}
				if(comboBox_6.getSelectedItem().equals("请选择")){
					comboBox_7.setEnabled(false);
					check=false;
				}
				if(!pred&&!check&&!ana){
					btnNewButton.setEnabled(false);
				}
			}
		});
		
		//选择检验的具体时间，可以运行程序
		comboBox_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!comboBox_7.getSelectedItem().equals("请选择")){
					btnNewButton.setEnabled(true);
					check=true;
				}
				else {
					check=false;
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
			}
		});
		
		//勾选宏观经济关联性分析，可以按下一步进行分析
		checkBox_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_2.isSelected()){
					btnNewButton.setEnabled(true);
					ana=true;
				}
				else {
					ana=false;
					if(!pred&&!check&&!ana){
						btnNewButton.setEnabled(false);
					}
				}
			}
		});
		
		/*
		 * 点击“下一步”，根据勾选的内容生成各类报告
		 * 报告生成的时候，将所有按键设置成disable，抬头信息改为正在计算。
		 * 完成后将其他按钮以及复选框enable，复选框重置为未勾选，“下一步”按钮设置为disable
		 */
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean pred_succeed = true;
				boolean check_succeed = true;
				boolean ana_succeed = true;
				//1.先记录下有哪些模块需要计算（检查时间相对较短，把握好不会发生线程错误问题），然后将按键disable
				
				btnNewButton.setEnabled(false);
				button.setEnabled(false);
				button_1.setEnabled(false);
				checkBox.setEnabled(false);
				checkBox_1.setEnabled(false);
				checkBox_2.setEnabled(false);
				comboBox.setEnabled(false);
				comboBox_1.setEnabled(false);
				comboBox_2.setEnabled(false);
				comboBox_3.setEnabled(false);
				comboBox_4.setEnabled(false);
				comboBox_5.setEnabled(false);
				comboBox_6.setEnabled(false);
				comboBox_7.setEnabled(false);
				
				
				//2.修改抬头信息
				textArea.setText("正在计算，请稍候");
//				textArea.setVisible(true);
				//3.按信息选择功能，并逐一进行计算。
				//3.1 预测模块
				if(pred){
					if(comboBox_3.getSelectedItem().equals("全年")){
						if(comboBox_1.getSelectedItem().equals("全社会用电量")){
							pred_succeed = Epred_sample.a_Y_pred(Integer.parseInt((String) comboBox.getSelectedItem()));
						}
						else{
							pred_succeed = Epred_sample.t_Y_pred(Integer.parseInt((String) comboBox.getSelectedItem()));
						}
					}
					
					if(comboBox_3.getSelectedItem().equals("上半年")){
						if(comboBox_1.getSelectedItem().equals("全社会用电量")){
							pred_succeed = Epred_sample.a_HY_pred(Integer.parseInt((String) comboBox.getSelectedItem()), 0);
						}
						else {
							pred_succeed = Epred_sample.t_HY_pred(Integer.parseInt((String) comboBox.getSelectedItem()), 0);
						}
					
					}
					if(comboBox_3.getSelectedItem().equals("下半年")){
						if(comboBox_1.getSelectedItem().equals("全社会用电量")){
							pred_succeed = Epred_sample.a_HY_pred(Integer.parseInt((String) comboBox.getSelectedItem()), 1);
						}
						else {
							pred_succeed = Epred_sample.t_HY_pred(Integer.parseInt((String) comboBox.getSelectedItem()), 1);
						}
					}
					
					if(comboBox_3.getSelectedItem().equals("第一季度"))
						pred_succeed = Epred_sample.a_S_pred(Integer.parseInt((String) comboBox.getSelectedItem()), 1);

					if(comboBox_3.getSelectedItem().equals("第二季度"))
						pred_succeed = Epred_sample.a_S_pred(Integer.parseInt((String) comboBox.getSelectedItem()), 2);
					
					if(comboBox_3.getSelectedItem().equals("第三季度"))
						pred_succeed = Epred_sample.a_S_pred(Integer.parseInt((String) comboBox.getSelectedItem()), 3);
					
					if(comboBox_3.getSelectedItem().equals("第四季度"))
						pred_succeed = Epred_sample.a_S_pred(Integer.parseInt((String) comboBox.getSelectedItem()), 4);
					
				}
				
				//3.2检验模块
				if(check){
					if(!comboBox_4.getSelectedItem().equals("副模型")){
						if(comboBox_7.getSelectedItem().equals("全年")){
							if(comboBox_4.getSelectedItem().equals("全社会用电量")){
								check_succeed = Epred_sample.a_Y_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()));
							}
							else{
								check_succeed = Epred_sample.t_Y_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()));
							}
						}
							
						if(comboBox_7.getSelectedItem().equals("上半年")){
							if(comboBox_4.getSelectedItem().equals("全社会用电量")){
								check_succeed = Epred_sample.a_HY_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()), 0);
							}
							else {
								check_succeed = Epred_sample.t_HY_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()), 0);
							}
						}
						
						if(comboBox_7.getSelectedItem().equals("下半年")){
							if(comboBox_4.getSelectedItem().equals("全社会用电量")){
								check_succeed = Epred_sample.a_HY_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()), 1);
							}
							else {
								check_succeed = Epred_sample.t_HY_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()), 1);
							}
						}
						
						if(comboBox_7.getSelectedItem().equals("第一季度"))
							check_succeed = Epred_sample.a_S_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()), 1);
							
						if(comboBox_7.getSelectedItem().equals("第二季度"))
							check_succeed = Epred_sample.a_S_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()), 2);
	
						if(comboBox_7.getSelectedItem().equals("第三季度"))
							check_succeed = Epred_sample.a_S_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()), 3);
							
						if(comboBox_7.getSelectedItem().equals("第四季度"))
							check_succeed = Epred_sample.a_S_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()), 4);
						
						
					}
					else {
						check_succeed = Epred_sample.var_Check(Integer.parseInt((String) comboBox_5.getSelectedItem()));
					}
				}
				
				//3.3 分析模块
				if(ana){
					ana_succeed = Epred_sample.var_pred(Calendar.getInstance().get(Calendar.YEAR));
				}
				
				//4.？得到的分析文档直接打开,该功能可后期添加，暂时不用。
				
				//5.修改抬头信息
				if(pred_succeed&&check_succeed&&ana_succeed){
					textArea.setText("计算完成");
				}
				else {
					textArea.setText("计算出错，请查阅控制台输出以检查错误来源");
				}
				
				//6.打开分析文档所在文件夹。
				
				//7.重置按键，清空历史数据
				Epred_sample.reset_data();

				button.setEnabled(true);
				button_1.setEnabled(true);
				checkBox.setEnabled(true);
				checkBox_1.setEnabled(true);
				checkBox_2.setEnabled(true);
				checkBox.setSelected(false);
				checkBox_1.setSelected(false);
				checkBox_2.setSelected(false);
				pred=false;
				check=false;
				ana=false;
			}
		});
		
	}
	
	
}
