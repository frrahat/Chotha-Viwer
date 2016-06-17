package chothaViewer;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Rahat
 * @since Apr June 17, 2016
 */
public class ChothaImage {
	
	private Image image;
	private String fileName;
	private int fileIndex;
	
	public ChothaImage() {
	}
	
	
	
	public ChothaImage(Image image, String fileName, int fileIndex) {
		this.image = image;
		this.fileName = fileName;
		this.fileIndex = fileIndex;
	}



	public static ChothaImage getFromFile(File file, int fileIndex){
		try {
			return new ChothaImage(ImageIO.read(file),file.getName(), fileIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}



	public Image getImage() {
		return image;
	}



	public String getFileName() {
		return fileName;
	}

	

	public int getFileIndex() {
		return fileIndex;
	}

	@Override
	public String toString() {
		return "ChothaImage [image=" + "..." + ", fileName=" + fileName + "]";
	}
	
}
