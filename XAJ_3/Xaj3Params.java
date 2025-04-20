package model.XAJ_3;

import java.lang.reflect.Field;

public class Xaj3Params {
    /**
     * 蒸散发计算:三层模型
     */
    private double KC;//蒸散发折算系数
    private double C;//深层蒸散发扩散系数
    private double WUM;//上层蓄水容量
    private double WLM;//下层蓄水容量
    /**
     * 产流计算：蓄满产流
     */
    private double WM;//流域平均蓄水容量，
    private double WDM;//深层蓄水容量,WDM=WM-WUM-WLM,不参与算法率定

    private double B;//蓄水容量曲线次方
    private double IMP;//不透水面积占全流域面积之比
    private double FE;//？？？？？？

    /**
     * 水源划分：三水源，自由水蓄水库
     */
    private double SM;//自由水蓄水容量（mm）
    private double EX;//自由水蓄水容量曲线指数
    private double KG;//地下水出流系数
    private double KSS;//壤中流出流系数
    /**
     * 汇流计算
     */
    private double KKG;//地下径流消退系数
    private double KKSS;//壤中流消退系数


    private double k;//瞬时单位线系数
    private double n;//瞬时单位线系数

    public Xaj3Params() {
    }

    public Xaj3Params(double WUM, double WLM, double WM, double KC, double C, double B, double IMP, double SM,
                      double EX, double FE, double KSS, double KG, double KKSS, double KKG, double n, double k) {
        this.KC = KC;
        this.C = C;
        this.WUM = WUM;
        this.WLM = WLM;
        this.WM = WM;
        //this.WDM = WDM;
        this.B = B;
        this.IMP = IMP;
        this.FE = FE;
        this.SM = SM;
        this.EX = EX;
        this.KG = KG;
        this.KSS = KSS;
        this.KKG = KKG;
        this.KKSS = KKSS;
        this.k = k;
        this.n = n;
    }

    public double getKC() {
        return KC;
    }

    public void setKC(double KC) {
        this.KC = KC;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }

    public double getWUM() {
        return WUM;
    }

    public void setWUM(double WUM) {
        this.WUM = WUM;
    }

    public double getWLM() {
        return WLM;
    }

    public void setWLM(double WLM) {
        this.WLM = WLM;
    }

    public double getWM() {
        return WM;
    }

    public void setWM(double WM) {
        this.WM = WM;
    }

    public double getWDM() {
        return WDM;
    }

    public void setWDM(double WDM) {
        this.WDM = WDM;
    }

    public double getB() {
        return B;
    }

    public void setB(double b) {
        B = b;
    }

    public double getIMP() {
        return IMP;
    }

    public void setIMP(double IMP) {
        this.IMP = IMP;
    }

    public double getFE() {
        return FE;
    }

    public void setFE(double FE) {
        this.FE = FE;
    }

    public double getSM() {
        return SM;
    }

    public void setSM(double SM) {
        this.SM = SM;
    }

    public double getEX() {
        return EX;
    }

    public void setEX(double EX) {
        this.EX = EX;
    }

    public double getKG() {
        return KG;
    }

    public void setKG(double KG) {
        this.KG = KG;
    }

    public double getKSS() {
        return KSS;
    }

    public void setKSS(double KSS) {
        this.KSS = KSS;
    }

    public double getKKG() {
        return KKG;
    }

    public void setKKG(double KKG) {
        this.KKG = KKG;
    }

    public double getKKSS() {
        return KKSS;
    }

    public void setKKSS(double KKSS) {
        this.KKSS = KKSS;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    /**
     * @Description 参数输入检查
     * @Notices 当返回值为null，参数无误；否则返回值中包含错误信息。
     * @Return String
     * @Company Hydroy(Leshui)
     * @Author Chen Shu
     * @Date 2023/3/1 21:34
     */
    public String paramCheck() throws IllegalAccessException {
        String msg = null;
        Field[] declaredFields = this.getClass().getDeclaredFields();
        int length = declaredFields.length;
        for (int i = 0; i < length; i++) {
            declaredFields[i].setAccessible(true);
            double aDouble =(double) declaredFields[i].get(this);
            if (aDouble <= 0) {
                msg += "模型参数"+declaredFields[i].getName()+"应大于0，请检查！";
                break;
            }
        }
        if (KSS + KG > 1) {
            msg += "模型参数输入错误，KSS、KG之和应小于1";
        }
        return msg;
    }
}
