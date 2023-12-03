

public class TestRun {
    
    private int ID;
    private String Time;
    private int Run;
    private int TubeNumber1;
    private int TubeNumber2;
    private double CoreFromDepth;
    private double CoreToDepth;
    private double MetersCored;
    private String KD;
    private double MetersRecovered;
    private String Description;
    private int ParentCoreSheet;
    
    public TestRun(){}
    
    public TestRun(int ID, int Run, String Time, int TubeNumber1, int TubeNumber2, double CoreFromDepth, 
            double CoreToDepth, double MetersCored, String KD, double MetersRecovered, String Description, int ParentCoreSheet){
        SetID(ID);
        SetTime(Time);
        SetRun(Run);
        SetTubeNumber1(TubeNumber1);
        SetTubeNumber2(TubeNumber2);
        SetCoreFromDepth(CoreFromDepth);
        SetCoreToDepth(CoreToDepth);
        SetMetersCored(MetersCored);
        SetKD(KD);
        SetMetersRecovered(MetersRecovered);
        SetDescription(Description);
        SetParentCoreSheet(ParentCoreSheet);
    }

     public int GetID() {
        return ID;
    }

    public void SetID(int ID) {
        this.ID = ID;
    }

    public String GetTime() {
        return Time;
    }

    public void SetTime(String Time) {
        this.Time = Time;
    }

    public int GetRun() {
        return Run;
    }

    public void SetRun(int Run) {
        this.Run = Run;
    }

    public int GetTubeNumber1() {
        return TubeNumber1;
    }

    public void SetTubeNumber1(int TubeNumber1) {
        this.TubeNumber1 = TubeNumber1;
    }

    public int GetTubeNumber2() {
        return TubeNumber2;
    }

    public void SetTubeNumber2(int TubeNumber2) {
        this.TubeNumber2 = TubeNumber2;
    }

    public double GetCoreFromDepth() {
        return CoreFromDepth;
    }

    public void SetCoreFromDepth(double CoreFromDepth) {
        this.CoreFromDepth = CoreFromDepth;
    }

    public double GetCoreToDepth() {
        return CoreToDepth;
    }

    public void SetCoreToDepth(double CoreToDepth) {
        this.CoreToDepth = CoreToDepth;
    }

    public double GetMetersCored() {
        return MetersCored;
    }

    public void SetMetersCored(double MetersCored) {
        this.MetersCored = MetersCored;
    }

    public String GetKD() {
        return KD;
    }

    public void SetKD(String KD) {
        this.KD = KD;
    }

    public double getMetersRecovered() {
        return MetersRecovered;
    }

    public void SetMetersRecovered(double MetersRecovered) {
        this.MetersRecovered = MetersRecovered;
    }
    
    public String GetDescription() {
        return Description;
    }

    public void SetDescription(String Description) {
        this.Description = Description;
    }
    
    public int getParentCoreSheet() {
        return ParentCoreSheet;
    }

    public void SetParentCoreSheet(int ParentCoreSheet) {
        this.ParentCoreSheet = ParentCoreSheet;
    }
    
}
