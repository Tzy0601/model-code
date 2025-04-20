package model.tank;

import evaluate_indicator.ErroIndex_D.ErroIndex_D;
import evaluate_indicator.ErroIndex_H.ErrorIndex_H;
import flow.Gamma;
import model.XAJ_3.Xaj3Output;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class TANK {
    /**
     * 水箱模型采取四层水箱结构
     * 顶层水箱出流模拟地面径流
     * 第二层水箱出流模拟壤中流
     * 第三层、四层水箱的出流分别模拟浅层和深层地下径流
     * 将每层水箱的出流过程线性叠加，即为流域总的径流过程
     * 为考虑河槽的调蓄作用，可以再并联一个水箱，将上面计算的总的径流过程再进行一次线性水库的调蓄
     */
    /**
     * 模型输入参数
     */
    public TankInput tankInput;

    public TANK(TankInput tankInput) {
        this.tankInput = tankInput;
    }

    /************输入资料************/
    private double[] P_TS;//降雨
    private double[] E_TS;//蒸发资料
    private double[] Qobs_T;//观测流量资料
    private int N;//数据长度
    private double area;//流域面积：km2
    private double dt;//计算时段
    private String[] Date;

    private String StartTime;
    private String EndTime;
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
    public double h4 = 0;//第四层水箱出流孔高度，出流孔高度为0，不参与参数率定
    public double r4;//第四层水箱出流孔系数
    /**
     * 瞬时单位线汇流参数
     */
    public double n;
    public double k;

    public void init() {
        /******************输入资料*******************/
        this.P_TS = tankInput.getP();
        this.E_TS = tankInput.getP();
        this.Qobs_T = tankInput.getQobs();
        this.area = tankInput.getArea();
        this.dt = tankInput.getDt();
        this.N = tankInput.getN();
        this.Date = tankInput.getDate();
        this.StartTime = tankInput.getTimeStart();
        this.EndTime = tankInput.getTimeEnd();
        /**********************模型参数*****************/
        this.Ks = tankInput.getTankParams().getKs();
        this.x1 = tankInput.getTankParams().getX1();
        this.s1 = tankInput.getTankParams().getS1();
        this.h1_1 = tankInput.getTankParams().getH1_1();
        this.h1_2 = tankInput.getTankParams().getH1_2();
        this.r1_1 = tankInput.getTankParams().getR1_1();
        this.r1_2 = tankInput.getTankParams().getR1_2();
        this.d1 = tankInput.getTankParams().getD1();
        this.x2 = tankInput.getTankParams().getX2();
        this.s2 = tankInput.getTankParams().getS2();
        this.h2 = tankInput.getTankParams().getH2();
        this.r2 = tankInput.getTankParams().getR2();
        this.d2 = tankInput.getTankParams().getD2();
        this.x3 = tankInput.getTankParams().getX3();
        this.s3 = tankInput.getTankParams().getS3();
        this.h3 = tankInput.getTankParams().getX3();
        this.d3 = tankInput.getTankParams().getD3();
        this.x4 = tankInput.getTankParams().getX4();
        this.r4 = tankInput.getTankParams().getR4();
        this.n = tankInput.getTankParams().getN();
        this.k = tankInput.getTankParams().getK();
    }


    /**
     * 单出流孔水箱产流计算
     *
     * @param x 初始贮水量
     * @param s 土壤饱和蓄水深
     * @param h 出流孔高度
     * @param r 出流系数
     * @param d 渗流系数
     * @return 出流量q，渗流量f
     */
    public TankOUT SingleTank(double[] F_in, double x, double s, double h, double r, double d) {
        double[] q = new double[this.N];//水箱出流量
        double[] f = new double[this.N];//水箱下渗量
        TankOUT tankOUT = new TankOUT();
        double z_start;
        double z_end = 0;
        //计算初始蓄水量
        for (int i = 0; i < N; i++) {
            if (i == 0) {
                z_start = x + F_in[0];
            } else {
                z_start = z_end + F_in[i];
            }
            if (z_start <= h) {
                q[i] = 0;
            } else {
                q[i] = (z_start - h) * r;
            }
            if (z_start <= s) {
                f[i] = z_start * d;
            } else {
                f[i] = s * d;
            }
            z_end = z_start - f[i] - q[i];
        }
        tankOUT.setQ(q);
        tankOUT.setF(f);
        return tankOUT;
    }

    /**
     * 双出流孔水箱产流计算
     *
     * @param x  初始蓄水深
     * @param s  土壤饱和蓄水深
     * @param h1 水箱出流孔1高度
     * @param h2 水箱出流孔2高度
     * @param r1 水箱出流孔1系数
     * @param r2 水箱出流孔2高度
     * @param d  水流孔渗流系数
     * @return 出流量q，泄流量f
     */
    public TankOUT DoubleTank(double x, double s, double h1, double h2, double r1, double r2, double d) {
        double[] q1 = new double[this.N];//水箱出流量1
        double[] q2 = new double[this.N];//水箱出流量2
        double[] q = new double[this.N];//水箱出流量
        double[] f = new double[this.N];//水箱下渗量
        TankOUT tankOUT = new TankOUT();
        //计算初始蓄水量
        double z_start;
        double z_end = 0;
        for (int i = 0; i < N; i++) {
            if (i == 0) {
                z_start = P_TS[0] + x - Ks * E_TS[0];
            } else {
                z_start = z_end + P_TS[i] - Ks * E_TS[i];
            }
            if (z_start <= h1) {
                q1[i] = 0;
            } else {
                q1[i] = (z_start - h1) * r1;
            }
            if (z_start <= h2) {
                q2[i] = 0;
            } else {
                q2[i] = (z_start - h2) * r2;
            }
            if (z_start <= s) {
                f[i] = z_start * d;
            } else {
                f[i] = s * d;
            }
            q[i] = q1[i] + q2[i];
            z_end = z_start - f[i] - q[i];
        }
        tankOUT.setQ(q);
        tankOUT.setF(f);
        return tankOUT;
    }

    /**
     * 无底孔水箱产流计算：底层水箱
     *
     * @param x 水箱初始蓄水量
     * @param h 水箱出流孔高度
     * @param r 水箱出流系数
     * @return 产流量
     */

    public TankOUT DeepTank(double[] F_in, double x, double h, double r) {
        double[] q = new double[this.N];//水箱出流量
        TankOUT tankOUT = new TankOUT();
        double z_start;
        double z_end = 0;
        //计算初始蓄水量
        for (int i = 0; i < N; i++) {
            if (i == 0) {
                z_start = F_in[0] + x;
            } else {
                z_start = z_end + F_in[i];
            }
            if (z_start <= h) {
                q[i] = 0;
            } else {
                q[i] = (z_start - h) * r;
            }
            z_end = z_start - q[i];
        }
        tankOUT.setQ(q);
        return tankOUT;
    }

    public double[] calQ() {
        //搭建四层串联水箱模型
        TankOUT t1 = DoubleTank(x1, s1, h1_1, h1_2, r1_1, r1_2, d1);
        double[] F1 = t1.getF();
        double[] Q1 = t1.getQ();
        TankOUT t2 = SingleTank(F1, x2, s2, h2, r2, d2);
        double[] F2 = t2.getF();
        double[] Q2 = t2.getQ();
        TankOUT t3 = SingleTank(F2, x3, s3, h3, r3, d3);
        double[] F3 = t3.getF();
        double[] Q3 = t3.getQ();
        TankOUT t4 = DeepTank(F3, x4, h4, r4);
        double[] Q4 = t4.getQ();
        double[] Rs = new double[this.N];
        for (int i = 0; i < N; i++) {
            Rs[i] = Q1[i] + Q2[i] + Q3[i] + Q4[i];
        }
        Gamma gamma = new Gamma(area, dt, n, k);
        double[] uh = gamma.uh();
        double[] Qsim = gamma.SurfaceFlowCalculation(Rs, uh);
//        for (int i = 0; i < Qsim.length; i++) {
//            Qsim[i] += Qobs_T[0];
//        }
        return Qsim;
    }

    public TankOutput output() throws ParseException {
        double[] Qsim = calQ();//径流计算
        TankOutput tankOutput = new TankOutput();
        tankOutput.setQsim(Qsim);
        tankOutput.setQobs(Qobs_T);
        HashMap<String, Double> hashMap = new HashMap<>();
        hashMap.put("Ks", Ks);
        hashMap.put("x1", x1);
        hashMap.put("s1", s1);
        hashMap.put("h1_1", h1_1);
        hashMap.put("h1_2", h1_2);
        hashMap.put("r1_1", r1_1);
        hashMap.put("r1_2", r1_2);
        hashMap.put("d1", d1);
        hashMap.put("x2", x2);
        hashMap.put("s2", s2);
        hashMap.put("h2", h2);
        hashMap.put("r2", r2);
        hashMap.put("d2", d2);
        hashMap.put("x3", x3);
        hashMap.put("s3", s3);
        hashMap.put("h3", h3);
        hashMap.put("r3", r3);
        hashMap.put("d3", d3);
        hashMap.put("x4", x4);
        hashMap.put("r4", r4);
        hashMap.put("k", k);
        hashMap.put("n", n);
        tankOutput.setParams(hashMap);
        if (dt == 1) {
            ErrorIndex_H error = new ErrorIndex_H(Qsim, StartTime, Qobs_T);
            Map<String, Double> info = error.getErrors();
            tankOutput.setFlowError(info.get("FlowError"));
            tankOutput.setQmaxError(info.get("QmaxError"));
            tankOutput.setQmaxTimeError(info.get("QmaxTimeError"));
            tankOutput.setNse(info.get("NSE"));
        } else if (dt == 24) {
            Map<String, Double> info = ErroIndex_D.getErrors(Qobs_T, Qsim);
            tankOutput.setRMSE(info.get("RMSE"));
            tankOutput.setMSE(info.get("MSE"));
            tankOutput.setMAE(info.get("MAE"));
            tankOutput.setR2(info.get("R2"));
            tankOutput.setNse(info.get("NSE"));
        }
        tankOutput.setDt(dt);
        return tankOutput;
    }


}

class TankOUT {
    /**
     * 出流量
     */
    public double[] q;
    /**
     * 下渗量
     */
    public double[] f;

    public double[] getQ() {
        return q;
    }

    public void setQ(double[] q) {
        this.q = q;
    }

    public double[] getF() {
        return f;
    }

    public void setF(double[] f) {
        this.f = f;
    }

}
