package test;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;

public class CloudinaryComboBox extends JFrame {
    private JComboBox<ImageIcon> comboBox;
    private DefaultComboBoxModel<ImageIcon> model;
    private ExecutorService executor;

    public CloudinaryComboBox() {
        setTitle("Load Hình Ảnh từ Cloudinary");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        model = new DefaultComboBoxModel<>();
        comboBox = new JComboBox<>(model);
        comboBox.setRenderer(new ImageComboBoxRenderer());

        add(comboBox);

        executor = Executors.newFixedThreadPool(5); // Tạo pool 5 thread

        loadImages(); // Gọi hàm load ảnh từ Cloudinary
    }

    private void loadImages() {
        List<String> imageUrls = List.of(
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg",
            "https://res.cloudinary.com/demo/image/upload/sample.jpg"
        );

        for (String url : imageUrls) {
            executor.submit(() -> {
                try {
                    ImageIcon icon = new ImageIcon(ImageIO.read(new URL(url))
                        .getScaledInstance(50, 50, Image.SCALE_SMOOTH)); // Resize ảnh

                    SwingUtilities.invokeLater(() -> model.addElement(icon)); // Cập nhật UI
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static class ImageComboBoxRenderer extends JLabel implements ListCellRenderer<ImageIcon> {
        @Override
        public Component getListCellRendererComponent(JList<? extends ImageIcon> list, ImageIcon value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setIcon(value);
            setHorizontalAlignment(CENTER);
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CloudinaryComboBox().setVisible(true));
    }
}
