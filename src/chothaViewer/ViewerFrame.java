package chothaViewer;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;



import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Toolkit;

public class ViewerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JFileChooser fileChooser;
	private static ViewerPanel viewerPanel;
	
	private static File[] listFiles;
	private JButton button;
	private JButton btnNext;
	protected static int fileIndex=-1;
	private static JLabel lblFileName;
	private JButton btnHelp;

	private final String[] supportedFileExts={"jpg","png","bmp","gif","jpeg"};
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewerFrame frame = new ViewerFrame();
					frame.pack();
					frame.setVisible(true);
					if(args.length>0)
						setFile(new File(args[0]));
						//setFile(new File("C:/Users/Rahat/Desktop/routine.png"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ViewerFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(ViewerFrame.class.getResource("/imageViewer/chothaViewer.png")));
		setTitle("Chotha Viewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		System.out.println("Starting...");
		setDesign("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0,Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		
		JButton btnOpen = new JButton("Open");
		GridBagConstraints gbc_btnOpen = new GridBagConstraints();
		gbc_btnOpen.anchor = GridBagConstraints.EAST;
		gbc_btnOpen.insets = new Insets(0, 0, 5, 5);
		gbc_btnOpen.gridx = 0;
		gbc_btnOpen.gridy = 0;
		contentPane.add(btnOpen, gbc_btnOpen);
		
		button = new JButton("<<Previous");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPrevFile();
			}
		});
		
		btnHelp = new JButton("Help");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HelpDialog().setVisible(true);
			}
		});
		GridBagConstraints gbc_btnHelp = new GridBagConstraints();
		gbc_btnHelp.anchor = GridBagConstraints.WEST;
		gbc_btnHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnHelp.gridx = 1;
		gbc_btnHelp.gridy = 0;
		contentPane.add(btnHelp, gbc_btnHelp);
		
		lblFileName = new JLabel("No file Opened");
		lblFileName.setForeground(Color.ORANGE);
		lblFileName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_lblFileName = new GridBagConstraints();
		gbc_lblFileName.insets = new Insets(0, 0, 5, 5);
		gbc_lblFileName.gridx = 2;
		gbc_lblFileName.gridy = 0;
		contentPane.add(lblFileName, gbc_lblFileName);
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.anchor = GridBagConstraints.EAST;
		gbc_button.insets = new Insets(0, 0, 5, 5);
		gbc_button.gridx = 3;
		gbc_button.gridy = 0;
		contentPane.add(button, gbc_button);
		
		btnNext = new JButton("Next>>");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setNextFile();
			}
		});
		GridBagConstraints gbc_btnNext = new GridBagConstraints();
		gbc_btnNext.anchor = GridBagConstraints.WEST;
		gbc_btnNext.insets = new Insets(0, 0, 5, 0);
		gbc_btnNext.gridx = 4;
		gbc_btnNext.gridy = 0;
		contentPane.add(btnNext, gbc_btnNext);
		
		viewerPanel = new ViewerPanel();
		viewerPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_viewerPanel = new GridBagConstraints();
		gbc_viewerPanel.gridwidth = 5;
		gbc_viewerPanel.fill = GridBagConstraints.BOTH;
		gbc_viewerPanel.gridx = 0;
		gbc_viewerPanel.gridy = 1;
		
		contentPane.add(viewerPanel,gbc_viewerPanel);
		
		viewerPanel.setInputKeyListener(new InputKeyListener() {
			
			@Override
			public void actionPerformed(String action) {
				if(action.equals("prev"))
				{
					setPrevFile();
				}
				else if(action.equals("next"))
				{
					setNextFile();
				}
			}
		});
		
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal=fileChooser.showOpenDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
			       File file=fileChooser.getSelectedFile();
			       setFile(file);
				}
				
			}
		});
		
		
		//addFont("/imageViewer/Files/Tahoma.ttf");
		
		fileChooser=new JFileChooser(System.getProperty("user.home"));
		setFileChooserFont(fileChooser.getComponents(),(new Font("Tahoma",Font.PLAIN,20)));
		fileChooser.setPreferredSize(new Dimension(800,600));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Image Files", supportedFileExts);
		fileChooser.setFileFilter(filter);
 
	}
	

	private static void setFile(File file) {
		lblFileName.setText(file.getName());
		viewerPanel.setFile(file);
		listFiles = file.getParentFile().listFiles();
		fileIndex = getFileIndex(file);
	}

	private void setPrevFile() {
		if(listFiles!=null)
		{
			setPrevFileIndex();
			//System.out.println(fileIndex);
			//if(fileIndex<0)
				//return;
			
			File file=listFiles[fileIndex];
			lblFileName.setText(file.getName());
			viewerPanel.setFile(file);
		}
	}

	private void setNextFile() {
		
		if(listFiles!=null)
		{
			setNextFileIndex();
			//System.out.println(fileIndex);
			//if(fileIndex>=listFiles.length)
				//return;
			File file=listFiles[fileIndex];
			lblFileName.setText(file.getName());
			viewerPanel.setFile(file);
		}
	}

	private static int getFileIndex(File selectedFile) {
		for(int i=0;i<listFiles.length;i++)
		{
			if(listFiles[i].equals(selectedFile))
				return i;
		}
		return -1;
	}

	private static void setDesign(String newLookAndFeel)
	{
		try
		{
			UIManager.setLookAndFeel(newLookAndFeel);
		}
		catch(Exception e)
		{
			System.out.println("Unable to load look and feel");
		}
	}

	public void setFileChooserFont(Component[] comp, Font font) {
		for (int x = 0; x < comp.length; x++) {
			if (comp[x] instanceof Container)
				setFileChooserFont(((Container) comp[x]).getComponents(), font);
			try {
				comp[x].setFont(font);
			} catch (Exception e) {
			}// do nothing
		}
	}
    
	private void setPrevFileIndex()
	{
		int p=fileIndex;
		fileIndex--;//current file index decreased
		while(fileIndex>=0)
		{
			String name=listFiles[fileIndex].getName();
			if(isSupported(name))
				return;
			fileIndex--;
		}
		fileIndex=p;//current file Index or 0
	}
	
	private void setNextFileIndex()
	{
		int p=fileIndex;
		fileIndex++;//current file Index increased
		while(fileIndex<listFiles.length)
		{
			String name=listFiles[fileIndex].getName();
			if(isSupported(name))
				return;
			fileIndex++;
		}
		fileIndex=p;//current fileIndex
	}
	
	private boolean isSupported(String name)
	{
		name=name.toLowerCase(Locale.ENGLISH);
		for(int i=0;i<supportedFileExts.length;i++)
		{
			if(name.endsWith("."+supportedFileExts[i]))
				return true;
		}
		return false;
	}
}
