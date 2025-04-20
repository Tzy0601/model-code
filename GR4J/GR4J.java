package model.GR4J;

import evaluate_indicator.ErroIndex_D.ErroIndex_D;
import evaluate_indicator.ErroIndex_H.ErrorIndex_H;

import java.util.HashMap;
import java.util.Map;

public class GR4J {
    /**
     * GR4J模型是一个日尺度的概念性降雨径流模型。
     * GR4J 模型结构简单，仅仅包含 4 个模型参数：
     * x1表示流域产流水库容量，单位为 mm；
     * x2表示地下水交换系数，单位为 mm；
     * x3表示汇流水库容量，单位为 mm；
     * x4表示单位线汇流时间，单位为天。
     * 四个模型参数均为实数，其中x1, x3和x4均为正值，x2值正负均可，也可为零。
     */
    //输入资料
    private double[] P;
    private double[] E;
    private double[] Qobs;
    private String startTime;
    private String endTime;
    private int N;
    private double dt;
    private double area;
    //模型参数
    private double x1;
    private double x2;
    private double x3;
    private double x4;
    private double Ks;//产流水库蓄水量S系数
    private double Kr;//汇流水库蓄水量R系数
    private double Kc;//蒸发折算系数
    //模型中间变量
    private double[] Pn;//流域有效降水量
    private double[] En;//流域剩余蒸发能力
    private double[] Ps;//补给进产流水库部分的产流
    private double[] Es;//产流水库中额外消耗用于蒸发的水量
    private double S;//产流水库蓄水量S
    private double[] Perc;//产流水库出流量
    private double[] Pr;//时段总产流量
    private double SH1;//S曲线1
    private int uhlength1;//单位线长度
    private int uhlength2;//单位线长度
    private double[] UH1;//时段单位线1
    private double SH2;//S曲线2
    private double[] UH2;//时段单位线2
    private double F;//地下水交换函数
    private double R;//汇流水库水量
    private double[] Qr;//汇流水库出流量0.9
    private double[] Qd;//0.1
    private GR4JInput gr4JInput;

    public GR4J(GR4JInput gr4JInput) {
        this.gr4JInput = gr4JInput;
    }

    public void init() {
        //资料获取
        this.P = gr4JInput.getP();
        this.E = gr4JInput.getE();
        this.Qobs = gr4JInput.getQobs();
        this.startTime = gr4JInput.getTimeStart();
        this.endTime = gr4JInput.getTimeEnd();
        this.N = gr4JInput.getN();
        this.dt = gr4JInput.getDt();
        this.area = gr4JInput.getArea();
        //模型参数获取
        this.x1 = gr4JInput.getGr4JParams().getX1();
        this.x2 = gr4JInput.getGr4JParams().getX2();
        this.x3 = gr4JInput.getGr4JParams().getX3();
        this.x4 = gr4JInput.getGr4JParams().getX4();
        this.Ks = gr4JInput.getGr4JParams().getKs();
        this.Kr = gr4JInput.getGr4JParams().getKr();
        this.Kc = gr4JInput.getGr4JParams().getKc();
        this.uhlength1 = (int) x4 + 1;
        this.uhlength2 = (int) (2 * x4) + 1;
        //初始化内存
        this.Pn = new double[this.N];
        this.En = new double[this.N];
        this.Ps = new double[this.N];
        this.Es = new double[this.N];
        this.S = Ks * x1;
        this.Perc = new double[this.N];
        this.Pr = new double[this.N];
        this.UH1 = new double[uhlength1];
        this.UH2 = new double[uhlength2];
        this.Qr = new double[this.N];
        this.Qd = new double[this.N];
        this.R = Kr * x3;

    }

    /**
     * 产流计算
     *
     * @return 计算流域时段总产流量Pr
     */

    public double[] calRunoff() {
        for (int i = 0; i < N; i++) {
            if (P[i] > E[i]) {
                Pn[i] = P[i] - Kc * E[i];
                En[i] = 0;
                double temp1 = Pn[i] / x1;
                double temp2 = x1 * (1 - Math.pow((S / x1), 2)) * Math.tanh(temp1);
                double temp3 = 1 + (S / x1) * Math.tanh(temp1);
                Ps[i] = temp2 / temp3;
            } else {
                En[i] = Kc * E[i] - P[i];
                Pn[i] = 0;
                double temp4 = En[i] / x1;
                double temp5 = S * (2 - S / x1) * Math.tanh(temp4);
                double temp6 = 1 + (1 - S / x1) * Math.tanh(temp4);
                Es[i] = temp5 / temp6;
            }
            //更新产流水库蓄水量S
            S = S + Ps[i] - Es[i];
            //S值不能超过产流水库最大容量x1
            if (S >= x1) {
                S = x1;
            }
            double temp7 = 1 + Math.pow(4 * S / (9 * x1), 4);
            //计算产流水库出流量Perc
            Perc[i] = S * (1 - Math.pow(temp7, (-1 / 4)));
            S = S - Perc[i];
            //计算流域时段总产流量Pr
            Pr[i] = Perc[i] - Ps[i] + Pn[i];
        }
        return Pr;
    }

    /**
     * 汇流计算：采用时段单位线（Unit Hydrograph， UH）进行汇流计算，考虑到不同的洪水组成成分具有不同的汇流速度，将总产流量 Pr分为两部分。
     * 其中，90%总产流量 Pr 视为快速径流部分，采用单位线 UH1 进行计算；
     * 剩余 10%总产流量 Pr 视为慢速径流部分，汇流速度较慢，起到基流成分的作用，采用单位线 UH2进行计算
     *
     * @return Qsim
     */
    public double SH1(int t) {
        if (t <= 0) {
            SH1 = 0;
        } else if (t < x4) {
            SH1 = Math.pow((t / x4), 2.5);
        } else if (t >= x4) {
            SH1 = 1;
        }
        return SH1;
    }

    public double SH2(int t) {
        if (t <= 0) {
            SH2 = 0;
        } else if (t < x4) {
            SH2 = 0.5 * Math.pow((t / x4), 2.5);
        } else if (t < 2 * x4) {
            SH2 = 1 - 0.5 * Math.pow((2 - (t / x4)), 2.5);
        } else {
            SH2 = 1;
        }
        return SH2;
    }

    public double[] getUH1() {
        for (int j = 1; j < uhlength1; j++) {
            UH1[j] = SH1(j) - SH1(j - 1);
        }
        return UH1;
    }

    public double[] getUH2() {
        for (int j = 1; j < uhlength2; j++) {
            UH2[j] = SH2(j) - SH2(j - 1);
        }
        return UH2;
    }

    public double[] SurfaceFlowCalculation(double[] Rs, double[] uh) {
        int n = Rs.length;
        int m = uh.length;
        double[] Qs = new double[n];
        for (int i = 0; i < n; i++) {
            int k = Math.min(i + 1, m);
            for (int j = 0; j < k; j++) {
                //System.out.println(uh[j]);
                Qs[i] += (uh[j] * Rs[i - j]);
            }
            Qs[i] = Math.max(Qs[i], 0.0);
        }
        return Qs;
    }

    public double[] calQ() {
        double[] Qsim = new double[this.N];
        double[] qsim = new double[this.N];
        double[] Pr = calRunoff();
        double[] R9 = new double[Pr.length];
        double[] R1 = new double[Pr.length];
        for (int i = 0; i < Pr.length; i++) {
            R9[i] = 0.9 * Pr[i];
            R1[i] = 0.1 * Pr[i];
        }
        double[] UH1 = getUH1();
        double[] UH2 = getUH2();
        double[] Q9 = SurfaceFlowCalculation(R9, UH1);
        double[] Q1 = SurfaceFlowCalculation(R1, UH2);
        for (int i = 0; i < Pr.length; i++) {
            F = x2 * Math.pow((R / x3), 3.5);
            R = Math.max(0, Q9[i] + F + R);
            double temp1 = 1 + Math.pow((R / x3), 4);
            Qr[i] = R * (1 - Math.pow(temp1, ((double) -1 / 4)));
            R = R - Qr[i];
            Qd[i] = Math.max(0, Q1[i] + F);
            Qsim[i] = Qr[i] + Qd[i];//(mm/day)
            qsim[i] = Qsim[i] * area / (3.6 * dt);//(m3/s)
        }

        return qsim;
    }

    public GR4JOutput output() {
        double[] qsim = calQ();
        GR4JOutput gr4JOutput = new GR4JOutput();
        gr4JOutput.setQsim(qsim);
        gr4JOutput.setQobs(Qobs);
        HashMap<String, Double> hashMap = new HashMap<>();
        hashMap.put("x1", x1);
        hashMap.put("x2", x2);
        hashMap.put("x3", x3);
        hashMap.put("x4", x4);
        hashMap.put("Ks", Ks);
        hashMap.put("Kr", Kr);
        hashMap.put("Kc",Kc);
        gr4JOutput.setParams(hashMap);
        Map<String, Double> info = ErroIndex_D.getErrors(Qobs, qsim);
        gr4JOutput.setRMSE(info.get("RMSE"));
        gr4JOutput.setMSE(info.get("MSE"));
        gr4JOutput.setMAE(info.get("MAE"));
        gr4JOutput.setR2(info.get("R2"));
        gr4JOutput.setNse(info.get("NSE"));
        gr4JOutput.setDt(dt);
        return gr4JOutput;
    }
}



