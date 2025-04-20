package model.GR4J;

public class GR4JParams {
    /**
     * 产流水库蓄水容量x1
     * 取值范围100~1200
     */
    private double x1;
    /**
     * 地下水交换系数x2
     *取值范围-5~3
     */
    private double x2;
    /**
     * 汇流水库蓄水容量x3
     * 取值范围20~300
     */
    private double x3;
    /**
     * 时段单位线汇流时间
     * 取值范围1.1~2.9
     */
    private double x4;

    /**
     * 产流水库蓄水量S初始值系数
     * 取值范围0~1
     */
    private double Ks;
    /**
     * 汇流水库蓄水量R初始值系数
     * 取值范围0~1
     */
    private double Kr;
    private double Kc;

    public double getKc() {
        return Kc;
    }

    public void setKc(double kc) {
        Kc = kc;
    }

    public GR4JParams(double x1, double x2, double x3, double x4, double ks, double kr, double kc) {
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
        this.x4 = x4;
        Ks = ks;
        Kr = kr;
        Kc = kc;
    }

    public GR4JParams() {
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getX3() {
        return x3;
    }

    public void setX3(double x3) {
        this.x3 = x3;
    }

    public double getX4() {
        return x4;
    }

    public void setX4(double x4) {
        this.x4 = x4;
    }

    public double getKs() {
        return Ks;
    }

    public void setKs(double ks) {
        Ks = ks;
    }

    public double getKr() {
        return Kr;
    }

    public void setKr(double kr) {
        Kr = kr;
    }
}

