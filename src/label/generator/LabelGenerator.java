//package label.generator;
//
//import java.io.ObjectInputStream.GetField;
//import java.util.Scanner;
//
//public class LabelGenerator {
//
//	static Scanner input = new Scanner(System.in);
//
//	static ArrayManager am = new ArrayManager();
//	static ArrayManager CoreSheet; 
//	static ArrayManager TestRun;
//
//	public static void PopulateArray(){
//
//		CoreSheet = new ArrayManager(1);
//
//		CoreSheet.add(new CoreSheet("2016 AN GEOTECH", "AURORA", "ANG165IVP005N", "CROSSBORDERS", "20.5", "283.561", "20.4", "283.561", "304.061", "CROSSBORDERS", "402", "sept.20,2016", "9:15 hrs", "SEPT.20.2016", "13:45 hrs", "49", "97", "This is a test CoreSheet"));	
//	}
//
//	public static void main(String[] args) {
//		PopulateArray();
//		Menu();
//	}//end main
//
//
//	public static void Menu(){
//		int choice = 0;
//		int loop = 0;
//
//		while (loop == 0) {
//			System.out.println("(1) Show all CoreSheets");
//			System.out.println("(2) Add A CoreSheet");
//			System.out.println("(3) Delete A CoreSheet");
//
//			choice = input.nextInt();
//
//
//			switch(choice){
//			case 1:
//				DisplayAllCoreSheets();
//				break;
//
//			case 2:
//				AddACoreSheet();
//				break;
//
//			case 3:
//				DeleteCoreSheet();
//				break;
//
//			}//ends switch
//		}//end while
//	}//end the options menu method
//
//
//	public static void DisplayAllCoreSheets(){
//		System.out.println("All Tickets:\n");
//
//		for (int i = 0; i < CoreSheet.size(); i++) {
//			System.out.println(((CoreSheet)CoreSheet.Getelementbypos(i)).GetDrillingCompany());
//		}
//	}
//
//	public static void AddACoreSheet(){
//		
//			System.out.println("Enter Drill Program");
//			String DrillProgram = input.next();
//			System.out.println("Enter Lease");
//			String Lease = input.next();
//			System.out.println("Enter WellID");
//			String WellID = input.next();
//                        System.out.println("Enter Company");
//			String Company = input.next();
//                        System.out.println("Enter ConductorCasingDepth");
//			String ConductorCasingDepth = input.next();
//                        System.out.println("Enter ConductorCasingElevation");
//			String ConductorCasingElevation = input.next();
//                        System.out.println("Enter CorePointDepth");
//			String CorePointDepth = input.next();
//                        System.out.println("Enter CorePointElevation");
//			String CorePointElevation = input.next();
//                        System.out.println("Enter GroundElevtion");
//			String GroundElevtion = input.next();
//                        System.out.println("Enter DrillingCompany");
//			String DrillingCompany = input.next();
//                        System.out.println("Enter RigUnitNumber");
//			String RigUnitNumber = input.next();
//                        System.out.println("Enter WellSpudDate");
//			String WellSpudDate = input.next();
//                        System.out.println("Enter WellSpudTime");
//			String WellSpudTime = input.next();
//                        System.out.println("Enter WellCompelationDate");
//			String WellCompelationDate = input.next();
//                        System.out.println("Enter WellCompelationTime");
//			String WellCompelationTime = input.next();
//                        System.out.println("Enter WellTD");
//			String WellTD = input.next();
//                        System.out.println("Enter TotalPercentRecovery");
//			String TotalPercentRecovery = input.next();
//                        System.out.println("Enter Remarks");
//			String Remarks = input.next();
//
//			CoreSheet.add(new CoreSheet(DrillProgram, Lease, WellID, Company, ConductorCasingDepth, ConductorCasingElevation, CorePointDepth, CorePointElevation, GroundElevtion, DrillingCompany, RigUnitNumber, WellCompelationDate, WellCompelationTime, WellSpudDate, WellSpudTime, WellTD, TotalPercentRecovery, Remarks));		
//	}//end method
//
//	public static void DeleteCoreSheet(){
//		System.out.println("Select the ticket you would like to delete\n");
//
//		for (int i = 0; i < CoreSheet.size(); i++) {
//			System.out.println(i+":   "+((CoreSheet)CoreSheet.Getelementbypos(i)).GetDrillProgram()+"  "+((CoreSheet)CoreSheet.Getelementbypos(i)).GetCompany());
//		}
//
//		int CoreSheettoDelete = input.nextInt();
//
//		CoreSheet.remove(CoreSheettoDelete);
//	}//end method
//
////	
//}
