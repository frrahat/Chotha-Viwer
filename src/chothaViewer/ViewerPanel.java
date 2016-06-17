package chothaViewer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class ViewerPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image background;
	private Image scaledImage;
	private float zoom = 1f;
	
	private int scrollX;
	private int scrollY;
	private final int maxYStep=50;
	private final int maxXStep=20;
	
	private Point dragPoint;
	
	private InputKeyListener inputKeyListener;

	public ViewerPanel() {
		
		InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), "plus");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "minus");
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "prev");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "next");

		am.put("plus", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setZoom(getZoom() + 0.1f);
				repaint();
			}
		});
		am.put("minus", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setZoom(getZoom() - 0.1f);
				repaint();
			}
		});
		
		am.put("left", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(scrollX<0)
				{
					scrollX+=maxXStep;
					repaint();
				}
			}
		});
		
		am.put("right", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(scrollX+getImgRect().width>getBounds().getWidth())
				{
					scrollX-=maxXStep;
					repaint();
				}
			}
		});
		
		am.put("up", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(scrollY<0)
				{
					scrollY+=maxYStep;
					repaint();
				}
			}
		});
		
		am.put("down", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(scrollY+getImgRect().height>getBounds().getHeight())
				{
					scrollY-=maxYStep;
					repaint();
				}
			}
		});
		
		am.put("prev", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				inputKeyListener.actionPerformed("prev");
			}
		});
		
		am.put("next", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				inputKeyListener.actionPerformed("next");
			}
		});

		setFocusable(true);
		requestFocusInWindow();
		
		addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				//upwheel k -1
				//downwheel k +1
				int k=e.getWheelRotation();
				if(k<0 && scrollY<0)
				{
					scrollY+=maxYStep;
					repaint();	
				}
				else if(k>0 && scrollY+getImgRect().height>getBounds().getHeight())
				{
					scrollY-=maxYStep;
					repaint();
				}
			}
		});
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				dragPoint=e.getPoint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1)
				{
					setZoom(getZoom() + 0.1f);
					repaint();
				}
				else
				{
					setZoom(getZoom() - 0.1f);
					repaint();
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				scrollX+=e.getX()-dragPoint.x;
				scrollY+=e.getY()-dragPoint.y;
				repaint();
				
				dragPoint=e.getPoint();
			}
		});

	}

	@Override
	public void addNotify() {

		super.addNotify();

	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float value) {
		
		if(background==null)
			return;
		
		zoom = value;

		if (zoom < 0) {
			zoom = 0f;
		}
		//System.out.println(background.getWidth(this)+" "+background.getHeight(this));
		int width = (int) Math.floor(background.getWidth(this) * zoom);
		int height = (int) Math.floor(background.getHeight(this) * zoom);
		
		if(width==0 || height==0)
			return;
		
		scaledImage = background.getScaledInstance(width, height,
				Image.SCALE_SMOOTH);

		invalidate();
	}

/*	@Override
	public Dimension getPreferredSize() {

		return scaledSize;

	}*/

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (scaledImage != null) {
			try
			{
				//g.setFont(g.getFont().deriveFont(40f));
				//g.setColor(Color.RED);
				//g.drawString("Loading...", scrollX, scrollY);
				g.drawImage(scaledImage, scrollX, scrollY, this);
			}catch(OutOfMemoryError oe)
			{
				oe.printStackTrace();
				setZoom((float) 1);
				repaint();
			}

		}

	}

	public void setImage(Image image) {
		
		background=image;
		scaledImage=image;
		setZoom(zoom);
		setInitImagePosition();
		repaint();
	}
	
	private void setInitImagePosition() {
		Rectangle rect=getImgRect();
		//System.out.println(rect.width+" "+rect.height);
		scrollX=(int) ((getBounds().getWidth()-rect.width)/2);
		scrollY=(int) ((getBounds().getHeight()-rect.height)/2);
		if(scrollX<0)
		{
			scrollX=0;
		}
		if(scrollY<0)
		{
			scrollY=0;
		}
	}
	
	public void setInputKeyListener(InputKeyListener listener)
	{
		this.inputKeyListener=listener;
	}
	
	private Rectangle getImgRect()
	{
		//System.out.println(scaledImage.getWidth(this));
		try
		{
			return new Rectangle(scaledImage.getWidth(this),scaledImage.getHeight(this));
		}catch(OutOfMemoryError oe)
		{
			return new Rectangle(background.getWidth(this),background.getHeight(this));
		}
	}
	
}
