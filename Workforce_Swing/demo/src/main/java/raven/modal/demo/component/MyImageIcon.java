package raven.modal.demo.component;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

public class MyImageIcon extends ImageIcon {

	private BufferedImage bufferedImage;
	private int height, width;

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}

	public void setBufferedImage(String url) {
		try {
			bufferedImage = ImageIO.read(new File(url));
		} catch (IOException e) {
			System.out.println("Không đọc file ảnh được");
			e.printStackTrace();
		}
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public MyImageIcon() {
		// TODO Auto-generated constructor stub
	}

	// Khoi tao day du
	public MyImageIcon(String url,int width, int height, int round) {
		setBufferedImage(url);
		setBufferedImage(resize(bufferedImage, width, height));
		makeRoundedCorner(bufferedImage, round);
		setImage(bufferedImage);
	}

	public MyImageIcon(URL location) {
		super(location);
	}

	private BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		bufferedImage.getGraphics().drawImage(image, 0, 0, null);
		return bufferedImage;
	}

	public MyImageIcon(String url) {
		setBufferedImage(url);
		setImage(bufferedImage);
	}

	public void resizeIconImage(int x, int y) {
		bufferedImage = this.resize(bufferedImage, x, y);
	}

	public void reconnerIconImage(int radius) {
		bufferedImage = this.makeRoundedCorner(bufferedImage, radius);
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = output.createGraphics();

		// This is what we want, but it only does hard-clipping, i.e. aliasing
		// g2.setClip(new RoundRectangle2D ...)

		// so instead fake soft-clipping by first drawing the desired clip shape
		// in fully opaque white with antialiasing enabled...
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

		// ... then compositing the image on top,
		// using the white shape from above as alpha source
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);

		g2.dispose();

		return output;
	}

	private static Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "dje0pvmau", "api_key",
			"773151356157165", "api_secret", "u3Rcr_2sPMoyFM6gtHQ_0vqnMKg"));

	public static MyImageIcon getMyImageIconFromCloudnaryImageTag(String publicID, int width, int height, int radius)
			throws URISyntaxException, IOException {
		// TODO Auto-generated method stub
		String urlString = cloudinary.url()
				.transformation(new Transformation().height(height).width(width).crop("scale").chain().radius(radius))
				.generate(publicID).trim();
		URL url = new URL(urlString);
		url.openStream().close();
		
		return new MyImageIcon(url);
	}

	public static String updateImageToCloud(String tenThuMuc, File file) {
		String randomPublicID = generateRandomString();
		 try {
			cloudinary.uploader().upload(file, ObjectUtils.asMap("public_id", tenThuMuc+randomPublicID));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return randomPublicID;
	}
	public static String generateRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = ThreadLocalRandom.current();
        return random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();
    }
}
