/**
 * This is just a class I use to test random stuff
 */
public class t implements Cloneable{
    public static void main(String[] args) throws CloneNotSupportedException {
        t t = new t(10);
        t t2 = (t) t.clone();
        System.out.println("before " + t.i + " .. " + t2.i);
        t.i += 10;
        System.out.println("after " + t.i + " .. " + t2.i);
    }

    int i;
    t(int i) {
        this.i = i;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
