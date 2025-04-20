package model.TVGM;

import evaluate_indicator.ErroIndex_D.ErroIndex_D;
import evaluate_indicator.ErroIndex_H.ErrorIndex_H;
import flow.Gamma;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class TVGM {
    /**
     * 二水源TVGM模型
     */
    //输入资料
    private double[] P_TS;
    private double[] Qobs;
    private double area;
    private double dt;
    private int N;//数据长度
    private String StartTime;
    private String EndTime;
    //API计算相关参数（需要率定）
//    private double API0;
//    private double APImax;
//    private double K;
    private double ML;//记忆长度
    //TVGM计算相关参数(二项式结构）
    private double g1;
    private double g2;
    private double g3;
    private double KKG;
    //瞬时单位线相关参数
    private double k;
    private double n;

    private TVGMInput tvgmInput;

    public TVGM(TVGMInput tvgmInput) {
        this.tvgmInput = tvgmInput;
    }

    public void init() {
        this.P_TS = tvgmInput.getP();
        this.Qobs = tvgmInput.getQobs();
        this.area = tvgmInput.getArea();
        this.dt = tvgmInput.getDt();
        this.N = tvgmInput.getN();
        this.StartTime = tvgmInput.getTimeStart();
        this.EndTime = tvgmInput.getTimeEnd();
        //
        this.ML = tvgmInput.getTvgmParams().getML();
        this.g1 = tvgmInput.getTvgmParams().getG1();
        this.g2 = tvgmInput.getTvgmParams().getG2();
        this.g3 = tvgmInput.getTvgmParams().getG3();
        this.KKG = tvgmInput.getTvgmParams().getKKG();
        this.n = tvgmInput.getTvgmParams().getN();
        this.k = tvgmInput.getTvgmParams().getk();
    }

    //API计算方法1
//    public double[] calAPI1() {
//        double[] API = new double[P_TS.length];
//        API[0] = API0 + P_TS[0];
//        for (int i = 1; i < P_TS.length; i++) {
//            API[i] = K * (API[i - 1] + P_TS[i - 1]);
//            API[i] = Math.min(API[i], APImax);
//        }
//        return API;
//    }
    //API计算方法2
    public double[] calAPI2() {
        int ml = (int) (ML);
        int ke = 2 * ml;
        int Mk = ml;
        int n = P_TS.length;
        double[] API = new double[n];
        double[] w = new double[ml];
        for (int i = 0; i < Mk; ++i) {
            w[i] = (1.0 - Math.exp(-1.0 / (double) ke)) * Math.exp((double) (-i) * 1.0 / (double) ke);
        }
        for (int i = 0; i < n; ++i) {
            int k = Math.min(i, Mk);
            for (int j = 0; j < k; ++j) {
                API[i] += w[j] * P_TS[i - j];
            }
        }
        return API;
    }

    //TVGM地面产流计算
    public double[] calRs() {
        double[] API = calAPI2();
        double[] Gt = new double[API.length];
        double[] Rs = new double[API.length];
        for (int i = 0; i < API.length; i++) {
            Gt[i] = g1 + g2 * API[i];
            Gt[i] = (Gt[i] < 0) ? 0.0 : ((Gt[i] >= 1.0) ? 1.0 : Gt[i]);
            Rs[i] = Gt[i] * P_TS[i];
        }
        return Rs;
    }

    //TVGM地下产流计算
    public double[] calRg() {
        double[] API = calAPI2();
        double[] Rg = new double[API.length];
        for (int i = 0; i < API.length; i++) {
            Rg[i] = g3 * API[i];
        }
        return Rg;
    }

    //TVGM地上汇流计算（纳希瞬时单位线）
    public double[] calQs() {
        double[] Rs = calRs();
        Gamma gamma = new Gamma(area, dt, n, k);
        double[] uh = gamma.uh();
        double[] Qs = gamma.SurfaceFlowCalculation(Rs, uh);
        return Qs;
    }

    //TVGM地下汇流计算（线性水库）
    public double[] calQg() {
        double U = area / (3.6 * dt);
        double[] Rg = calRg();
        double[] Qg = new double[N];
        for (int i = 0; i < N; i++) {
            if (i == 0) {
                Qg[i] = (1 - KKG) * Rg[i] * U;
            } else {
                Qg[i] = KKG * Qg[i - 1] + (1 - KKG) * Rg[i] * U;
            }
        }
        return Qg;

    }


    //汇流计算
    public double[] calQ() {
        double[] Qs = calQs();
        double[] Qg = calQg();
        double[] Qsim = new double[Qs.length];
        //每场洪水第一个流量值作为基流
        for (int i = 0; i < Qs.length; i++) {
            Qsim[i] = Qg[i] + Qs[i] ;
        }
        return Qsim;
    }

    public TVGMOutput output() throws ParseException {
        double[] Qsim = calQ();
        TVGMOutput tvgmOutput = new TVGMOutput();
        tvgmOutput.setQsim(Qsim);
        tvgmOutput.setQobs(Qobs);
        HashMap<String, Double> hashMap = new HashMap<>();
        hashMap.put("ML", ML);
        hashMap.put("g1", g1);
        hashMap.put("g2", g2);
        hashMap.put("g3", g3);
        hashMap.put("KKG", KKG);
        hashMap.put("n", n);
        hashMap.put("k", k);
        tvgmOutput.setParams(hashMap);
        if(dt==1){
            ErrorIndex_H error = new ErrorIndex_H(Qsim, StartTime, Qobs);
            Map<String, Double> info = error.getErrors();
            tvgmOutput.setFlowError(info.get("FlowError"));
            tvgmOutput.setQmaxError(info.get("QmaxError"));
            tvgmOutput.setQmaxTimeError(info.get("QmaxTimeError"));
            tvgmOutput.setNse(info.get("NSE"));
        }else if (dt==24){
            Map<String, Double> info = ErroIndex_D.getErrors(Qobs,Qsim);
            tvgmOutput.setRMSE(info.get("RMSE"));
            tvgmOutput.setMSE(info.get("MSE"));
            tvgmOutput.setMAE(info.get("MAE"));
            tvgmOutput.setR2(info.get("R2"));
            tvgmOutput.setNse(info.get("NSE"));

        }
        tvgmOutput.setDt(dt);
        return tvgmOutput;
    }

}
