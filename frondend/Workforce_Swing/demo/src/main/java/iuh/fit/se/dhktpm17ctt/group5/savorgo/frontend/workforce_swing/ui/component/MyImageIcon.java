package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component;

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
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

	public void setBufferedImage(String url) throws IOException {
		try {
			bufferedImage = ImageIO.read(new File(url));
		} catch (IOException e) {
			throw new IOException("Can't read image file");
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
	public MyImageIcon(String url, int width, int height, int round) throws IOException {
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

	public MyImageIcon(String url) throws IOException {
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

	private static Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "dtgzcw63e", "api_key",
			"965871288783373", "api_secret", "cT1_xumJYIooLl_pPU751VeL7go"));

	public static MyImageIcon getMyImageIconFromCloudinaryImageTag(String publicID, int width, int height, int radius)
	        throws URISyntaxException, IOException {
	    String urlString = null;
	    int retryCount = 3;  // Số lần thử lại
	    boolean success = false;
	    MyImageIcon icon = null;

	    while (retryCount > 0 && !success) {
	        try {
	            // Tạo URL từ Cloudinary
	            urlString = cloudinary.url()
	                    .transformation(new Transformation().height(height).width(width).crop("scale").chain().radius(radius))
	                    .generate(publicID);

	            // Kiểm tra URL
	            if (urlString == null || urlString.isEmpty()) {
	            	return icon = new MyImageIcon("src/main/resources/images/png/no_image_found.png", width, height, radius);
	            }

	            // Mở URL để kiểm tra
	            URL url = new URL(urlString);
	            url.openStream().close();

	            // Tạo MyImageIcon từ URL
	            icon = new MyImageIcon(url);

	            // Đánh dấu là tải thành công
	            success = true;

	        } catch (IOException e) {
	            // In ra lỗi và giảm số lần thử lại
	            System.err.println("public id: " + publicID + " | " + urlString + " | Error generating image URL or loading image: " + e.getMessage());
	            retryCount--;

	            // Nếu hết lần thử, dùng ảnh mặc định
	            if (retryCount == 0) {
	                try {
	                    icon = new MyImageIcon("src/main/resources/images/png/no_image_found.png", width, height, radius);
	                } catch (IOException e1) {
	                    e1.printStackTrace();
	                }
	            }
	        }
	    }

	    // Trả về icon sau khi tải xong hoặc ảnh mặc định nếu không thành công
	    return icon;
	}


	public static String updateImageToCloud(String tenThuMuc, File file) throws Exception {
	    String randomPublicID = generateRandomString();
	    cloudinary.api().createFolder(tenThuMuc, ObjectUtils.emptyMap());

	    // Tải lên hình ảnh lên Cloudinary
	    System.out.println("file: " + file);
	    Map<String, Object> uploadResult = cloudinary.uploader().upload(file, ObjectUtils.asMap("public_id","SavorGO"+"/"+tenThuMuc+"/"+randomPublicID,"asset_folder", "SavorGO"+"/"+tenThuMuc,
	    		  "resource_type", "image"));
	    // Lấy URL của hình ảnh đã tải lên
	    String imageUrl = (String) uploadResult.get("url");
	    return randomPublicID;
	}

	public static String generateRandomString() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = ThreadLocalRandom.current();
		return random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
	}
}
