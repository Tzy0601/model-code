package model.XAJ_3;

import evaluate_indicator.ErroIndex_H.ErrorIndex_H;
import evaluate_indicator.ErroIndex_D.ErroIndex_D;
import flow.Gamma;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


public class XAJ3 {
    /**
     * 输入资料
     */
    private double[] P_TS;//降雨
    private double[] E_TS;//蒸发资料
    private double[] Qobs_TS;//观测流量资料
    private int N;//数据长度
    private double area;//流域面积：km2
    private double dt;//计算时段
    private String[] Date;

    private String StartTime;
    private String EndTime;

    /**
     * 蒸散发计算:三层模型
     */
//    private double UM_T;//上层张力水容量，mm
//    private double LM_T;//下土层张力水容量，mm
    private double K_T;//蒸散发折算系数
    private double C_T;//深层蒸散发扩散系数
    private double WUM_T;//上层蓄水容量
    private double WLM_T;//下层蓄水容量


    /**
     * 产流计算：蓄满产流
     */
    private double WM_T;//流域平均蓄水容量，
    private double WDM_T;//深层蓄水容量,WDM=WM-WUM-WLM,不参与算法率定

    private double B_T;//蓄水容量曲线次方
    private double IMP_T;//不透水面积占全流域面积之比
    private double FE;//？？？？？？
    private double A;//土壤初始土湿
    /**
     * 水源划分：三水源，自由水蓄水库
     */
    private double SM_T;//自由水蓄水容量（mm）
    private double EX_T;//自由水蓄水容量曲线指数
    private double KG_T;//地下水出流系数
    private double KSS_T;//壤中流出流系数
    /**
     * 汇流计算
     */
    private double KKG;//地下径流消退系数
    private double KKSS;//壤中流消退系数
    private double k;//瞬时单位线系数
    private double n;//瞬时单位线系数
    //private Xaj3Params xaj3Input.getXaj3Params();//新安江模型参数输入
    private Xaj3Input xaj3Input;//新安江模型数据输入

    public XAJ3(Xaj3Input xaj3Input) {
        this.xaj3Input = xaj3Input;
    }

    public XAJ3() {
    }
    /**
     * 模型数据输入
     *
     * @param area    流域面积，km2
     * @param dt      计算时段，h
     * @param P_TS    降雨时间序列
     * @param E_TS    蒸发资料时间序列
     * @param Qobs_TS 径流时间序列
     */
//    public void input(double area, double dt, double[] P_TS, double[] E_TS, double[] Qobs_TS) {
//        this.area = area;
//        this.dt = dt;
//        this.P_TS = P_TS;
//        this.E_TS = E_TS;
//        this.Qobs_TS = Qobs_TS;
//        this.N=P_TS.length;
//    }

    /**
     * 模型参数输入
     * @param WUM 上层土壤蓄水容量
     * @param WLM 下层土壤蓄水容量
     * @param WM 土壤总蓄水容量
     * @param K 蒸发皿折算系数
     * @param C 深层蒸散发系数
     * @param B 蓄水容量曲线指数
     * @param IMP 不透水面积与全流域面积之比
     * @param SM 流域平均自由水蓄水容量
     * @param EX 自由水蓄水容量曲线指数
     * @param FE 比例系数？？
     * @param KSS 自由水蓄水库对壤中流的出流系数
     * @param KG 自由水蓄水库对地下径流出流系数
     * @param KKSS 地下水库消退系数
     * @param KKG 壤中流水库消退系数
     * @param n 单位线系数
     * @param k 单位线系数
     */
//    public void paramInput(double WUM,double WLM,double WM,double K,double C,double B,double IMP,double SM,
//                           double EX,double FE,double KSS,double KG,double KKSS,double KKG,double n,double k){
//        this.WUM_T=WUM;
//        this.WLM_T=WLM;
//        this.WM_T=WM;
//        this.K_T=K;
//        this.C_T=C;
//        this.B_T=B;
//        this.IMP_T=IMP;
//        this.SM_T=SM;
//        this.EX_T=EX;
//        this.FE=FE;
//        this.KSS_T=KSS;
//        this.KG_T=KG;
//        this.KKSS=KKSS;
//        this.KKG=KKG;
//        this.n=n;
//        this.k=k;
//
//    }


    /**
     * 模型初始化数据
     */

    public void init() throws IllegalAccessException {
        xaj3Input.getXaj3Params().paramCheck();
        //输入数据
        this.P_TS = xaj3Input.getP();
        this.E_TS = xaj3Input.getE();
        this.Qobs_TS = xaj3Input.getQobs();
        this.area = xaj3Input.getArea();
        this.N = xaj3Input.getN();
        this.dt = xaj3Input.getDt();
        this.Date = xaj3Input.getDate();
        this.StartTime = xaj3Input.getTimeStart();
        this.EndTime = xaj3Input.getTimeEnd();
        //模型参数
        this.WUM_T = xaj3Input.getXaj3Params().getWUM();
        this.WLM_T = xaj3Input.getXaj3Params().getWLM();
        this.WM_T = xaj3Input.getXaj3Params().getWM();
        this.B_T = xaj3Input.getXaj3Params().getB();
        this.IMP_T = xaj3Input.getXaj3Params().getIMP();
        this.FE = xaj3Input.getXaj3Params().getFE();
        this.C_T = xaj3Input.getXaj3Params().getC();
        this.K_T = xaj3Input.getXaj3Params().getKC();
        this.EX_T = xaj3Input.getXaj3Params().getEX();
        this.SM_T = xaj3Input.getXaj3Params().getSM();
        this.KG_T = xaj3Input.getXaj3Params().getKG();
        this.KSS_T = xaj3Input.getXaj3Params().getKSS();
        this.KKG = xaj3Input.getXaj3Params().getKKG();
        this.KKSS = xaj3Input.getXaj3Params().getKKSS();
        this.n = xaj3Input.getXaj3Params().getN();
        this.k = xaj3Input.getXaj3Params().getK();
    }

    /**
     * 执行产流计算
     *
     * @return 模型计算实例
     */
    public runoffGenerationResult calRunoff() {
        runoffGenerationResult result = new runoffGenerationResult(N);
        WDM_T = WM_T - WUM_T - WLM_T;
        double wu0 = FE * WUM_T;
        double wl0 = FE * WLM_T;
        double wd0 = FE * WDM_T;
        LayeredSoilParam w0 = new LayeredSoilParam(wu0, wl0, wd0);  // 时段初土壤分层含水量
        double w0Sum = w0.getSum();                                 // 时段初土壤总含水量
        for (int i = 0; i < this.N; ++i) {
            double p = P_TS[i];                                        // 时段内降雨量
            double ep = E_TS[i] * K_T;                              // 计算时段内实际蒸发量
            double e = calE(p, ep, w0.U, w0.L);                     // 计算时段内土壤蒸发量
            double pe = p - e;                                      // 计算时段内净雨量
            double r = calR(pe, w0Sum);                             // 计算时段内产流量
            w0 = calLayeredW(w0, pe, r);          // 计算时段末土壤分层含水量
            w0Sum = w0.getSum();                      // 计算时段末土壤总含水量
            result.Set(i, pe, r, w0Sum);
        }
        return result;
    }

    /**
     * 计算蒸发量：三层蒸发模型
     *
     * @param p   时段内降雨量
     * @param ep  时段内实际蒸发量
     * @param wu0 时段初上层含水量
     * @param wl0 时段初上层含水量
     * @return 时段内土壤蒸发量
     */
    public double calE(double p, double ep, double wu0, double wl0) {
        double eu, el, ed;
        if (p + wu0 >= ep) {
            eu = ep;
            el = 0;
            ed = 0;
        } else {
            eu = p + wu0;
            if (wl0 >= C_T * WLM_T) {
                el = (ep - eu) * wl0 / WLM_T;
                ed = 0;
            } else {
                if (wl0 >= C_T * (ep - eu)) {
                    el = C_T * (eu - ep);
                    ed = 0;
                } else {
                    el = wl0;
                    ed = C_T * (ep - eu) - el;
                }

            }
        }
        double e = eu + el + ed;
        return e;
    }

    /**
     * 计算时段内产流量：蓄满产流
     *
     * @param pe 时段内净雨量，降雨-蒸发
     * @param w0 时段初土壤总含水量
     * @return 时段内产流量
     */
    public double calR(double pe, double w0) {
        double WMM = WM_T * (1 + B_T) / (1 - IMP_T);
        double r;
        A = WMM * (1 - Math.pow((1 - w0 / WM_T), (1 / (1 + B_T))));
        if (pe <= 0) {
            r = 0;
        } else {
            if (pe + A < WMM) {
                r = pe - (WM_T - w0) + WM_T * Math.pow((1 - (A + pe) / WMM), (1 + B_T));
            } else {
                r = pe - (WM_T - w0);
            }
        }
        return r;
    }

    /**
     * 此处存疑，没有找到准缺写法
     * 计算时段末土壤含水量
     *
     * @param w0 时段初土壤含水量
     * @param pe 时段净雨量
     * @param r  时段产流量
     * @return 时段末土壤含水量
     */
    public LayeredSoilParam calLayeredW(LayeredSoilParam w0, double pe, double r) {
        double wu0 = w0.U;
        double wl0 = w0.L;
        double wd0 = w0.D;
        double wu, wl, wd;
        double dw = pe - r;
        if (dw > 0) {
            if (wu0 + dw < WUM_T) {
                wu = wu0 + dw;
                wl = wl0;
                wd = wd0;
            } else {
                wu = WUM_T;
                wl = wl0 + dw - (WUM_T - wu0);
                if (wl < WLM_T)
                    wd = wd0;
                else {
                    wl = WLM_T;
                    wd = wd0 + dw - (WUM_T - wu0) - (WLM_T - wl0);
                }
            }
        } else {
            if (wu0 + dw > 0) {
                wu = wu0 + dw;
                wl = wl0;
                wd = wd0;
            } else {
                wu = 0;
                wl = wu0 + dw + wl0;
                if (wl > 0)
                    wd = wd0;
                else {
                    wl = 0;
                    wd = wu0 + wl0 + dw + wd0;
                }
            }
        }
        return new LayeredSoilParam(wu, wl, wd);
    }

    /**
     * 水源划分：三水源划分
     *
     * @return 完成水源划分计算的模型实例
     */
    public sourcePartitionResult calSourcePartition() {
        //checkNotNull(runoffGenerationResult, "尚未完成产流计算");

        //int n = runoffGenerationResult.Length;
        sourcePartitionResult sourceresult = new sourcePartitionResult(N);
        runoffGenerationResult runoffresult = calRunoff();
        double s0 = FE * SM_T;//初始时刻流域平均自由含水量
        double KSSD = (1 - Math.pow(1 - (KG_T + KSS_T), dt / 24)) / (1 + KG_T / KSS_T);     // 转化后的壤中流出流系数
        double KGD = KSSD * KG_T / KSS_T;     // 转化后的地下水出流系数
        double Smax = (1 + EX_T) * SM_T;      // 流域点自由蓄水量最大值
        for (int i = 0; i < this.N; ++i) {
            double pe = runoffresult.PE[i];
            double w = runoffresult.W[i];
            double r = runoffresult.R[i];
            double rimp = 0;
            double au = Smax * (1 - Math.pow(1 - s0 / SM_T, 1 / (1 + EX_T)));
            double FR;              // 产流面积
            double coef;            //中间变量
            if (pe > 0) {
                rimp = pe * IMP_T;
                FR = r / pe - IMP_T;
                if (pe + au < Smax) {
                    coef = SM_T - SM_T * Math.pow(1 - (pe + au) / Smax, 1 + EX_T);
                } else {
                    coef = SM_T;
                }
            } else {
                FR = 1 - IMP_T - Math.pow(1 - w / WM_T, B_T / (1 + B_T)) * (1 - IMP_T);
                coef = s0;
            }
            //double rimp = pe * IMP_T;//不透水面积产流
            double rs = Math.max((pe + s0 - coef) * FR, 0.0);
            double rss = coef * KSSD * FR;
            double rg = coef * KGD * FR;
            s0 = coef * (1 - KSSD - KGD);//本时段末、下时段初的自由水蓄水量
            sourceresult.Set(i, rimp, rs, rss, rg, s0);
        }
        return sourceresult;
    }

    /**
     * 地面径流汇流计算：瞬时单位线
     *
     * @return 地表径流序列
     */
    public double[] UnitHydrograph() {
        sourcePartitionResult sourceresult = calSourcePartition();
        double[] R = new double[N];
        double[] RS = new double[N];
        double[] RIM = new double[N];
        for (int i = 0; i < this.N; i++) {
            RS[i] = sourceresult.RS[i];//地表径流产流序列
            RIM[i] = sourceresult.RIMP[i];//不透水面积产流序列
            R[i] = RS[i] + RIM[i];
        }
        Gamma gamma = new Gamma(area, dt, n, k);
        double[] uh = gamma.uh();
        return gamma.SurfaceFlowCalculation(R, uh);
    }

    /**
     * 地下径流、壤中流汇流计算：线性水库
     *
     * @return 返回地下径流、壤中流序列
     */
    public double[] LinearReservoir() {
        sourcePartitionResult sourceresult = calSourcePartition();
        double U = area / (3.6 * dt);
        double[] QSS = new double[N];
        double[] QG = new double[N];
        double[] Q = new double[N];
        double[] RSS = new double[N];
        double[] RG = new double[N];
        for (int i = 0; i < this.N; i++) {
            RSS[i] = sourceresult.RSS[i];//地表径流产流序列
            RG[i] = sourceresult.RG[i];//不透水面积产流序列
            if (i == 0) {
                QSS[i] = (1 - KKSS) * RSS[i] * U;
                QG[i] = (1 - KKG) * RG[i] * U;

            } else {
                QSS[i] = KKSS * QSS[i - 1] + (1 - KKSS) * RSS[i] * U;
                QG[i] = KKG * QG[i - 1] + (1 - KKG) * RG[i] * U;
            }
            Q[i] = QSS[i] + QG[i];
        }
        return Q;
    }

    /**
     * 计算总径流
     *
     * @return 径流总量
     */
    public double[] calQ() {
        double[] QU = UnitHydrograph();//地表径流序列
        double[] QG = LinearReservoir();//地下径流序列
        double[] Qsim = new double[N];
        //第一个流量值作为基流
        for (int i = 0; i < this.N; i++) {
            Qsim[i] = QU[i] + QG[i] ;
        }
        return Qsim;
    }


    /**
     * 新安江模型输出函数
     */
    public Xaj3Output output() throws ParseException {
        calRunoff();//执行产流计算
        calSourcePartition();//水源划分
        double[] Qsim = calQ();//径流计算
        Xaj3Output xaj3Output = new Xaj3Output();
        xaj3Output.setQsim(Qsim);
        xaj3Output.setQobs(Qobs_TS);
        HashMap<String, Double> hashMap = new HashMap<>();
        hashMap.put("WUM", WUM_T);
        hashMap.put("WLM", WLM_T);
        hashMap.put("WM", WM_T);
        hashMap.put("B", B_T);
        hashMap.put("IMP", IMP_T);
        hashMap.put("FE", FE);
        hashMap.put("KC", K_T);
        hashMap.put("C", C_T);
        hashMap.put("SM", SM_T);
        hashMap.put("EX", EX_T);
        hashMap.put("KG", KG_T);
        hashMap.put("KSS", KSS_T);
        hashMap.put("KKG", KKG);
        hashMap.put("KKSS", KKSS);
        hashMap.put("k", k);
        hashMap.put("n", n);
        xaj3Output.setParams(hashMap);
        if (dt == 1) {
            ErrorIndex_H error = new ErrorIndex_H(Qsim, StartTime, Qobs_TS);
            Map<String, Double> info = error.getErrors();
            xaj3Output.setFlowError(info.get("FlowError"));
            xaj3Output.setQmaxError(info.get("QmaxError"));
            xaj3Output.setQmaxTimeError(info.get("QmaxTimeError"));
            xaj3Output.setNse(info.get("NSE"));
        } else if (dt == 24) {
            Map<String, Double> info = ErroIndex_D.getErrors(Qobs_TS, Qsim);
            xaj3Output.setRMSE(info.get("RMSE"));
            xaj3Output.setMSE(info.get("MSE"));
            xaj3Output.setMAE(info.get("MAE"));
            xaj3Output.setR2(info.get("R2"));
            xaj3Output.setNse(info.get("NSE"));
        }
        xaj3Output.setDt(dt);
        return xaj3Output;
    }
}

/**
 * 土壤分层参数
 */
class LayeredSoilParam {
    /**
     * @param u 上层参数值
     * @param l 下层参数值
     * @param d 深层参数值
     */
    public LayeredSoilParam(double u, double l, double d) {
        Set(u, l, d);
    }

    /**
     * @param u 上层参数值
     * @param l 下层参数值
     * @param d 深层参数值
     */
    public void Set(double u, double l, double d) {
        U = u;
        L = l;
        D = d;
    }

    /**
     * @return 分层参数值总和
     */
    public double getSum() {
        return U + L + D;
    }

    /**
     * 上层参数值
     */
    public double U;

    /**
     * 下层参数值
     */
    public double L;

    /**
     * 深层参数值
     */
    public double D;
}















