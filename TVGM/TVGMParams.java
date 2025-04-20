package model.TVGM;

public class TVGMParams {
    private double API0;
    private double APImax;
    private double K;
    private double ML;//记忆长度
    //TVGM计算相关参数(二项式结构）
    private double g1;
    private double g2;
    private double g3;
    private double KKG;
    //瞬时单位线相关参数
    private double k;
    private double n;

    public double getAPI0() {
        return API0;
    }

    public void setAPI0(double API0) {
        this.API0 = API0;
    }

    public double getAPImax() {
        return APImax;
    }

    public void setAPImax(double APImax) {
        this.APImax = APImax;
    }

    public double getK() {
        return K;
    }

    public void setK(double K) {
        K = K;
    }
    public double getk() {
        return k;
    }

    public void setk(double k) {
        k = k;
    }


    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    public double getML() {
        return ML;
    }

    public void setML(double ML) {
        this.ML = ML;
    }

    public double getG1() {
        return g1;
    }

    public void setG1(double g1) {
        this.g1 = g1;
    }

    public double getG2() {
        return g2;
    }

    public void setG2(double g2) {
        this.g2 = g2;
    }

    public double getG3() {
        return g3;
    }

    public void setG3(double g3) {
        this.g3 = g3;
    }

    public double getKKG() {
        return KKG;
    }

    public void setKKG(double KKG) {
        this.KKG = KKG;
    }

    public TVGMParams(double ML, double g1, double g2, double g3, double KKG, double k, double n) {
        this.ML = ML;
        this.g1 = g1;
        this.g2 = g2;
        this.g3 = g3;
        this.KKG = KKG;
        this.k = k;
        this.n = n;
    }
}
