

import java.util.Date;

public class CoreSheet {
    
    private int ID;
    private String DrillProgram;
    private String Lease;
    private String WellID;
    private String Company;
    private Double ConductorCasingDepth; 
    private Double ConductorCasingElevation; 
    private Double CorePointDepth; 
    private Double CorePointElevation;
    private Double GroundElevation; 
    private String DrillingCompany; 
    private String RigUnitNumber; 
    private String WellSpudDate;
    private String WellSpudTime; 
    private String WellCompletionDate; 
    private String WellCompletionTime; 
    private String WellTD; 
    private Double TotalPercentRecovery;
    private String Remarks;
    
    public CoreSheet()
    {
        
    }
    
    public CoreSheet(int ID, String DrillProgram, String Lease, String WellID, String Company, Double ConductorCasingDepth, Double ConductorCasingElevation, Double CorePointDepth, 
            Double CorePointElevation, Double GroundElevation, String DrillingCompany, String RigUnitNumber, String WellSpudDate, String WellSpudTime, String WellCompletionDate,
            String WellCompletionTime, String WellTD, Double TotalPercentRecovery, String Remarks)
    {
        SetID(ID);
        SetDrillProgram(DrillProgram);
        SetLease(Lease);
        SetWellID(WellID);
        SetCompany(Company);
        SetConductorCasingDepth(ConductorCasingDepth);
        SetConductorCasingElevation(ConductorCasingElevation);
        SetCorePointDepth(CorePointDepth);
        SetCorePointElevation(CorePointElevation);
        SetGroundElevation(GroundElevation);
        SetDrillingCompany(DrillingCompany);
        SetRigUnitNumber(RigUnitNumber);
        SetWellSpudDate(WellSpudDate);
        SetWellSpudTime(WellSpudTime);
        SetWellCompletionDate(WellCompletionDate);
        SetWellCompletionTime(WellCompletionTime);
        SetWellTD(WellTD);
        SetTotalPercentRecovery(TotalPercentRecovery);
        SetRemarks(Remarks);
    }
    
    public int getID() {
        return ID;
    }

    public void SetID(int ID) {
        this.ID = ID;
    }

    public String GetDrillProgram() {
        return DrillProgram;
    }

    public void SetDrillProgram(String DrillProgram) {
        this.DrillProgram = DrillProgram;
    }

    public String GetLease() {
        return Lease;
    }

    public void SetLease(String Lease) {
        this.Lease = Lease;
    }

    public String GetWellID() {
        return WellID;
    }

    public void SetWellID(String WellID) {
        this.WellID = WellID;
    }

    public String GetCompany() {
        return Company;
    }

    public void SetCompany(String Company) {
        this.Company = Company;
    }

    public Double GetConductorCasingDepth() {
        return ConductorCasingDepth;
    }

    public void SetConductorCasingDepth(Double ConductorCasingDepth) {
        this.ConductorCasingDepth = ConductorCasingDepth;
    }

    public Double GetConductorCasingElevation() {
        return ConductorCasingElevation;
    }

    public void SetConductorCasingElevation(Double ConductorCasingElevation) {
        this.ConductorCasingElevation = ConductorCasingElevation;
    }

    public Double GetCorePointDepth() {
        return CorePointDepth;
    }

    public void SetCorePointDepth(Double CorePointDepth) {
        this.CorePointDepth = CorePointDepth;
    }

    public Double GetCorePointElevation() {
        return CorePointElevation;
    }

    public void SetCorePointElevation(Double CorePointElevation) {
        this.CorePointElevation = CorePointElevation;
    }

    public Double GetGroundElevation() {
        return GroundElevation;
    }

    public void SetGroundElevation(Double GroundElevation) {
        this.GroundElevation = GroundElevation;
    }

    public String GetDrillingCompany() {
        return DrillingCompany;
    }

    public void SetDrillingCompany(String DrillingCompany) {
        this.DrillingCompany = DrillingCompany;
    }

    public String GetRigUnitNumber() {
        return RigUnitNumber;
    }

    public void SetRigUnitNumber(String RigUnitNumber) {
        this.RigUnitNumber = RigUnitNumber;
    }

    public String GetWellSpudDate() {
        return WellSpudDate;
    }

    public void SetWellSpudDate(String WellSpudDate) {
        this.WellSpudDate = WellSpudDate;
    }

    public String GetWellSpudTime() {
        return WellSpudTime;
    }

    public void SetWellSpudTime(String WellSpudTime) {
        this.WellSpudTime = WellSpudTime;
    }

    public String GetWellCompletionDate() {
        return WellCompletionDate;
    }

    public void SetWellCompletionDate(String WellCompletionDate) {
        this.WellCompletionDate = WellCompletionDate;
    }

    public String GetWellCompletionTime() {
        return WellCompletionTime;
    }

    public void SetWellCompletionTime(String WellCompletionTime) {
        this.WellCompletionTime = WellCompletionTime;
    }

    public String GetWellTD() {
        return WellTD;
    }

    public void SetWellTD(String WellTD) {
        this.WellTD = WellTD;
    }

    public Double GetTotalPercentRecovery() {
        return TotalPercentRecovery;
    }

    public void SetTotalPercentRecovery(Double TotalPercentRecovery) {
        this.TotalPercentRecovery = TotalPercentRecovery;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void SetRemarks(String Remarks) {
        this.Remarks = Remarks;
    }

}
