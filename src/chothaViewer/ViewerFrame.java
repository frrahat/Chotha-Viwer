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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
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

	private JButton button;
	private JButton btnNext;

	private static JLabel lblFileName;
	private JButton btnHelp;
	
	private static int currentImageFileIndex;
	private static int currentImgInBufferIndex;
	private static final int CHOTHA_IMAGE_BUFFER_SIZE=10;//>1
	private static ChothaImage[] chothaImages;
	private static ArrayList<File> chothaImageFiles;
	

	private final static String[] supportedFileExts={"jpg","png","bmp","gif","jpeg"};
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
					if(args.length>0){
						File file=new File(args[0]);
						initChothaBuffer(file);
						updateChothaImage();
					}
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
		setIconImage(Toolkit.getDefaultToolkit().getImage(ViewerFrame.class.getResource("/chothaViewer/chothaViewer.png")));
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
			       initChothaBuffer(file);
			       updateChothaImage();
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
		
		chothaImages=new ChothaImage[CHOTHA_IMAGE_BUFFER_SIZE];
		chothaImageFiles=new ArrayList<File>();
 
	}
	

	private static void updateChothaImage(){
		ChothaImage chothaImage=chothaImages[currentImgInBufferIndex];
		if(chothaImage==null){
			lblFileName.setText("null chotha image");
		}else{
			lblFileName.setText(chothaImage.getFileName());
			viewerPanel.setImage(chothaImage.getImage());
		}
	}
	
	private static void initChothaBuffer(File currentFile){
		File listFiles[]=currentFile.getParentFile().listFiles();
		
		chothaImageFiles.clear();
		
		for(int i=0;i<listFiles.length;i++){
			File f=listFiles[i];
			if(isSupported(f.getName())){
				chothaImageFiles.add(f);
			}
		}
		
		Comparator<File> fileComparator=new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		};
		
		chothaImageFiles.sort(fileComparator);
		
		currentImageFileIndex=chothaImageFiles.indexOf(currentFile);
		
		
		int middleIndex=getBufferMiddleIndex();
		
		int size=chothaImageFiles.size();
		for(int i=0;i<CHOTHA_IMAGE_BUFFER_SIZE;i++){
			int fileIndex=currentImageFileIndex-middleIndex+i;
			if(fileIndex<0 || fileIndex>=size)
				continue;
			chothaImages[i]=ChothaImage.getFromFile(
					chothaImageFiles.get(fileIndex), fileIndex);
		}
		
		currentImgInBufferIndex=middleIndex;
		//printBuffer();
	}
	
	private void refreshChothaBuffer(){
		int middleIndex=getBufferMiddleIndex();
		
		int size=chothaImageFiles.size();
		for(int i=0;i<CHOTHA_IMAGE_BUFFER_SIZE;i++){
			int fileIndex=currentImageFileIndex-middleIndex+i;
			if(fileIndex<0 || fileIndex>=size)
				continue;
			chothaImages[i]=ChothaImage.getFromFile(
					chothaImageFiles.get(fileIndex), fileIndex);
		}
	}
	
	private static int getBufferMiddleIndex(){
		return (CHOTHA_IMAGE_BUFFER_SIZE-1)/2;
	}

	private static void printBuffer(){
		System.out.println("Printing Buffer:");
		for(int i=0;i<CHOTHA_IMAGE_BUFFER_SIZE;i++){
			String indicator=Integer.toString(i);
			if(currentImgInBufferIndex==i){
				indicator+="*";
			}
			System.out.println(indicator+"-->"+chothaImages[i]);
		}
	}
	private void setPrevFile() {
		if(currentImageFileIndex<0)
			return;
			
		currentImageFileIndex--;
		currentImgInBufferIndex+=CHOTHA_IMAGE_BUFFER_SIZE-1;
		currentImgInBufferIndex%=CHOTHA_IMAGE_BUFFER_SIZE;
		
		updateChothaImage();
		
		Thread t=new Thread(new Runnable() {
			
			@Override
			public void run() {
				int midIndex=getBufferMiddleIndex();
				int fileIndexToLoad=currentImageFileIndex-midIndex-1;
				if(fileIndexToLoad<0)
					return;
				
				chothaImages[(CHOTHA_IMAGE_BUFFER_SIZE+
						currentImgInBufferIndex-midIndex)%CHOTHA_IMAGE_BUFFER_SIZE]
						=ChothaImage.getFromFile(chothaImageFiles.get(fileIndexToLoad),
								fileIndexToLoad);
			}
		});
		
		t.run();
		
		//printBuffer();
	}

	private void setNextFile() {
		if(currentImageFileIndex>=chothaImageFiles.size())
			return;
		
		currentImageFileIndex++;
		currentImgInBufferIndex++;
		currentImgInBufferIndex%=CHOTHA_IMAGE_BUFFER_SIZE;
		
		updateChothaImage();
		
		Thread t=new Thread(new Runnable() {
			
			@Override
			public void run() {
				int midIndex=getBufferMiddleIndex();
				int fileIndexToLoad=currentImageFileIndex+(CHOTHA_IMAGE_BUFFER_SIZE-midIndex+1);
				if(fileIndexToLoad>=chothaImageFiles.size())
					return;
				
				chothaImages[(CHOTHA_IMAGE_BUFFER_SIZE+
						currentImgInBufferIndex-midIndex-1)%CHOTHA_IMAGE_BUFFER_SIZE]
						=ChothaImage.getFromFile(chothaImageFiles.get(fileIndexToLoad),
								fileIndexToLoad);
			}
		});
		
		t.run();
		
		//printBuffer();
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
 
	
	private static boolean isSupported(String name)
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
