package autoschool.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/**
 * Загрузка изображений приложения.
 *
 * <p>Сначала картинка ищется рядом с классами (папка {@code src/autoschool},
 * которую Eclipse и NetBeans копируют в папку сборки), затем — в папке
 * {@code assets} рядом с проектом. Так приложение запускается и из IDE,
 * и из командной строки.</p>
 */
public final class Assets {

    private Assets() {
        // утилитарный класс
    }

    /**
     * @param fileName имя файла, например {@code "card_b.png"}
     * @return изображение или {@code null}, если файл не найден (приложение продолжит работу)
     */
    public static BufferedImage load(String fileName) {
        BufferedImage image = loadFromClassPath(fileName);
        if (image == null) {
            image = loadFromFileSystem(fileName);
        }
        if (image == null) {
            System.err.println("Не найдено изображение: " + fileName);
        }
        return image;
    }

    private static BufferedImage loadFromClassPath(String fileName) {
        String resource = "/autoschool/" + fileName;
        try (InputStream in = Assets.class.getResourceAsStream(resource)) {
            if (in != null) {
                return ImageIO.read(in);
            }
        } catch (Exception e) {
            System.err.println("Ошибка чтения ресурса " + resource + ": " + e.getMessage());
        }
        return null;
    }

    private static BufferedImage loadFromFileSystem(String fileName) {
        try {
            File file = new File("assets", fileName);
            if (file.isFile()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
            System.err.println("Ошибка чтения файла assets/" + fileName + ": " + e.getMessage());
        }
        return null;
    }
}
