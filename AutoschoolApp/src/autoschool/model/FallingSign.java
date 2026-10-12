package autoschool.model;

/**
 * Падающая карточка категории (A / B / C / D).
 *
 * <p>Карточка летит сверху вниз; её нужно «поймать» учебным автомобилем,
 * если категория совпадает с требуемой. Это модель предметной области
 * «автошкола»: карточки — категории водительского удостоверения.</p>
 */
public class FallingSign {

    private final char category;
    private final int size;
    private final double speed;
    private double x;
    private double y;

    public FallingSign(char category, double x, double y, double speed, int size) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.size = size;
    }

    /** Сместить карточку вниз за указанное время (в секундах). */
    public void fall(double deltaSeconds) {
        if (deltaSeconds > 0) {
            y = y + speed * deltaSeconds;
        }
    }

    /** @return true, если автомобиль успел подставить крышу под карточку. */
    public boolean isCaughtBy(double carLeft, double carRight, double catchLine) {
        return ExamRules.isCaught(centerX(), bottom(), carLeft, carRight, catchLine);
    }

    /** @return true, если карточка ушла ниже поля и считается пропущенной. */
    public boolean isOutOfField(int fieldHeight) {
        return y > fieldHeight;
    }

    public char getCategory() {
        return category;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public double getSpeed() {
        return speed;
    }

    public double centerX() {
        return x + size / 2.0;
    }

    public double bottom() {
        return y + size;
    }

    @Override
    public String toString() {
        return "FallingSign{" + category + ", x=" + Math.round(x) + ", y=" + Math.round(y)
                + ", speed=" + speed + '}';
    }
}
