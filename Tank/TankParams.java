package model.tank;

public class TankParams {
    /**
     * 采用一层蒸发模式
     * 蒸发参数
     */
    public double Ks;//流域蒸散发折算系数
    /**
     * 顶层水箱参数
     */
    public double x1;//顶层水箱初始贮水量
    public double s1;//顶层水箱饱和蓄水深
    public double h1_1;//顶层水箱出流孔1高度
    public double h1_2;//顶层水箱出流孔2高度
    public double r1_1;//顶层水箱出流孔1出流系数
    public double r1_2;//顶层水箱出流孔2出流系数
    public double d1;//顶层水箱下渗系数
    /**
     * 第二层水箱参数
     */
    public double x2;//第二层水箱初始贮水量
    public double s2;//第二层水箱饱和水深
    public double h2;//第二层水箱出流孔高度
    public double r2;//第二层水箱出流孔系数
    public double d2;//第二层水箱下渗系数
    /**
     * 第三层水箱参数
     */
    public double x3;//第三层水箱初始贮水量
    public double s3;//第三层水箱饱和水深
    public double h3;//第三层水箱出流孔高度
    public double r3;//第三层水箱出流孔系数
    public double d3;//第三层水箱下渗系数
    /**
     * 第4四层水箱参数
     */
    public double x4;//第四层水箱初始贮水量
    public double r4;//第四层水箱出流孔系数
    /**
     * 瞬时单位线汇流参数
     */
    public double n;
    public double k;

    public TankParams() {
    }

    public TankParams(double ks, double x1, double s1, double h1_1, double h1_2, double r1_1, double r1_2, double d1, double x2, double s2, double h2, double r2, double d2, double x3, double s3, double h3, double r3, double d3, double x4, double r4, double n, double k) {
        Ks = ks;
        this.x1 = x1;
        this.s1 = s1;
        this.h1_1 = h1_1;
        this.h1_2 = h1_2;
        this.r1_1 = r1_1;
        this.r1_2 = r1_2;
        this.d1 = d1;
        this.x2 = x2;
        this.s2 = s2;
        this.h2 = h2;
        this.r2 = r2;
        this.d2 = d2;
        this.x3 = x3;
        this.s3 = s3;
        this.h3 = h3;
        this.r3 = r3;
        this.d3 = d3;
        this.x4 = x4;
        this.r4 = r4;
        this.n = n;
        this.k = k;
    }

    public double getKs() {
        return Ks;
    }

    public void setKs(double ks) {
        Ks = ks;
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getS1() {
        return s1;
    }

    public void setS1(double s1) {
        this.s1 = s1;
    }

    public double getH1_1() {
        return h1_1;
    }

    public void setH1_1(double h1_1) {
        this.h1_1 = h1_1;
    }

    public double getH1_2() {
        return h1_2;
    }

    public void setH1_2(double h1_2) {
        this.h1_2 = h1_2;
    }

    public double getR1_1() {
        return r1_1;
    }

    public void setR1_1(double r1_1) {
        this.r1_1 = r1_1;
    }

    public double getR1_2() {
        return r1_2;
    }

    public void setR1_2(double r1_2) {
        this.r1_2 = r1_2;
    }

    public double getD1() {
        return d1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getS2() {
        return s2;
    }

    public void setS2(double s2) {
        this.s2 = s2;
    }

    public double getH2() {
        return h2;
    }

    public void setH2(double h2) {
        this.h2 = h2;
    }

    public double getR2() {
        return r2;
    }

    public void setR2(double r2) {
        this.r2 = r2;
    }

    public double getD2() {
        return d2;
    }

    public void setD2(double d2) {
        this.d2 = d2;
    }

    public double getX3() {
        return x3;
    }

    public void setX3(double x3) {
        this.x3 = x3;
    }

    public double getS3() {
        return s3;
    }

    public void setS3(double s3) {
        this.s3 = s3;
    }

    public double getH3() {
        return h3;
    }

    public void setH3(double h3) {
        this.h3 = h3;
    }

    public double getR3() {
        return r3;
    }

    public void setR3(double r3) {
        this.r3 = r3;
    }

    public double getD3() {
        return d3;
    }

    public void setD3(double d3) {
        this.d3 = d3;
    }

    public double getX4() {
        return x4;
    }

    public void setX4(double x4) {
        this.x4 = x4;
    }

    public double getR4() {
        return r4;
    }

    public void setR4(double r4) {
        this.r4 = r4;
    }

    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }
}
