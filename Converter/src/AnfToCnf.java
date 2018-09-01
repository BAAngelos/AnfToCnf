import java.util.ArrayList;
import java.util.Iterator;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;


public class AnfToCnf {

	public static int substitutvar = 1;
	public static int cutvar = 1;
	public static int tmpCutvar;
	boolean[] lfsr = new boolean[80];
	boolean[] nfsr = new boolean[80];
	ArrayPrimitive nfsrPrim = new ArrayPrimitive(80, "2");
	ArrayPrimitive lfsrPrim = new ArrayPrimitive(80 ,"1");
	ArrayList<String> nfsrFeedbackFormular = new ArrayList<String>();
	ArrayList<String> lfsrFeedbackFormular = new ArrayList<String>();
	ArrayList<String> outputFormular = new ArrayList<String>();
	ISolver solver = SolverFactory.newDefault();
	
	String key = "00000001001000110100010101100111100010011010101111001101111011110001001000110100";
	String iv = "0000000100100011010001010110011110001001101010111100110111101111";
	
	AnfToCnf(){
		
		if (key.length() != 80) {
			System.out.println("der key ist nicht 80 bits lang");
		}
		for (int i = 0; i < key.length(); i++) {
			nfsr[i] = key.charAt(i) == '1' ? true : false;
		}

		for (int i = 0; i < iv.length(); i++) {
			lfsr[i] = iv.charAt(i) == '1' ? true : false;
		}

		for (int i = iv.length(); i < lfsr.length; i++) {
			lfsr[i] = true;
		}
		nfsrFeedbackFormular.add("n");								
		nfsrFeedbackFormular.add("l0");
		nfsrFeedbackFormular.add("n63");
		nfsrFeedbackFormular.add("n60");
		nfsrFeedbackFormular.add("n52");
		nfsrFeedbackFormular.add("n45");
		nfsrFeedbackFormular.add("n37");
		nfsrFeedbackFormular.add("n33");
		nfsrFeedbackFormular.add("n28");
		nfsrFeedbackFormular.add("n21");
		nfsrFeedbackFormular.add("n15");
		nfsrFeedbackFormular.add("n9");
		nfsrFeedbackFormular.add("n0");
		nfsrFeedbackFormular.add("n63&n60");
		nfsrFeedbackFormular.add("n37&n33");
		nfsrFeedbackFormular.add("n15&n9");
		nfsrFeedbackFormular.add("n60&n52&n45");
		nfsrFeedbackFormular.add("n33&n28&n21");
		nfsrFeedbackFormular.add("n63&n45&n28&n9");
		nfsrFeedbackFormular.add("n60&n52&n37&n33");
		nfsrFeedbackFormular.add("n63&n60&n21&n15");
		nfsrFeedbackFormular.add("n63&n60&n52&n45&n37");
		nfsrFeedbackFormular.add("n33&n28&n21&n15&n9");
		nfsrFeedbackFormular.add("n52&n45&n37&n33&n28&n21");
		//lfsr[62] ^ lfsr[51] ^ lfsr[38] ^ lfsr[23] ^ lfsr[13] ^ lfsr[0];
		lfsrFeedbackFormular.add("l");
		lfsrFeedbackFormular.add("l62");
		lfsrFeedbackFormular.add("l51");
		lfsrFeedbackFormular.add("l38");
		lfsrFeedbackFormular.add("l23");
		lfsrFeedbackFormular.add("l13");
		lfsrFeedbackFormular.add("l0");
		//(lfsr[3] & lfsr[64]) ^ (lfsr[46] & lfsr[64]) ^ (nfsr[63] & lfsr[64]) ^ (lfsr[46] & lfsr[25] & lfsr[3])^ (lfsr[46] & lfsr[64] & lfsr[3])
		//^ (lfsr[46] & nfsr[63] & lfsr[3]) ^ (lfsr[46] & nfsr[63] & lfsr[25]) ^ (lfsr[46] & nfsr[63] & lfsr[64])
		//^ lfsr[25] ^ nfsr[63] ^ nfsr[1] ^ nfsr[2] ^ nfsr[4] ^ nfsr[10] ^ nfsr[31] ^ nfsr[43] ^ nfsr[56];
		outputFormular.add("o");
		outputFormular.add("l3&l64");
		outputFormular.add("l46&l64");
		outputFormular.add("n63&l64");
		outputFormular.add("l46&l25&l3");
		outputFormular.add("l46&l64&l3");
		outputFormular.add("l46&n63&l3");
		outputFormular.add("l46&n63&l25");
		outputFormular.add("l46&n63&l64");
		outputFormular.add("l25");
		outputFormular.add("n63");
		outputFormular.add("n1");
		outputFormular.add("n2");
		outputFormular.add("n4");
		outputFormular.add("n10");
		outputFormular.add("n31");
		outputFormular.add("n43");
		outputFormular.add("n56");
	}
	
	
	public int getNewSubVar(){
		String tmp = "9"+substitutvar;
		substitutvar++;
		return Integer.parseInt(tmp);
	}
	
	public int getNewCutVar(){
		String tmp = "8"+cutvar;
		cutvar++;
		return Integer.parseInt(tmp);
	}
	
	
	//Converting ArrayList<Integer> to int[]
	public static int[] convertIntegers(ArrayList<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
	
	public boolean feedbackNFSR() {
		boolean g;
		g = lfsr[0] ^ nfsr[63] ^ nfsr[60] ^ nfsr[52] ^ nfsr[45] ^ nfsr[37] ^ nfsr[33] ^ nfsr[28] ^ nfsr[21] ^ nfsr[15] ^ nfsr[9]
				^ nfsr[0] ^ (nfsr[63] & nfsr[60]) ^ (nfsr[37] & nfsr[33]) ^ (nfsr[15] & nfsr[9])
				^ (nfsr[60] & nfsr[52] & nfsr[45]) ^ (nfsr[33] & nfsr[28] & nfsr[21])
				^ (nfsr[63] & nfsr[45] & nfsr[28] & nfsr[9]) ^ (nfsr[60] & nfsr[52] & nfsr[37] & nfsr[33])
				^ (nfsr[63] & nfsr[60] & nfsr[21] & nfsr[15]) ^ (nfsr[63] & nfsr[60] & nfsr[52] & nfsr[45] & nfsr[37])
				^ (nfsr[33] & nfsr[28] & nfsr[21] & nfsr[15] & nfsr[9])
				^ (nfsr[52] & nfsr[45] & nfsr[37] & nfsr[33] & nfsr[28] & nfsr[21]);
		return g;
	}
	
	public boolean feedbackLFSR(){
		return lfsr[62] ^ lfsr[51] ^ lfsr[38] ^ lfsr[23] ^ lfsr[13] ^ lfsr[0];
	}

	// generates the streamkey z's
	public boolean output() {
		boolean z;

		z = (lfsr[3] & lfsr[64]) ^ (lfsr[46] & lfsr[64]) ^ (nfsr[63] & lfsr[64]) ^ (lfsr[46] & lfsr[25] & lfsr[3])^ (lfsr[46] & lfsr[64] & lfsr[3])
				^ (lfsr[46] & nfsr[63] & lfsr[3]) ^ (lfsr[46] & nfsr[63] & lfsr[25]) ^ (lfsr[46] & nfsr[63] & lfsr[64])
				^ lfsr[25] ^ nfsr[63] ^ nfsr[1] ^ nfsr[2] ^ nfsr[4] ^ nfsr[10] ^ nfsr[31] ^ nfsr[43] ^ nfsr[56];

		
		return z;
	}
	
	public void clockOneTime() {
		boolean feedbackLFSR = feedbackLFSR();
		boolean feedbackNFSR = feedbackNFSR();


		for (int i = 0; i < lfsr.length - 1; i++) {
			lfsr[i] = lfsr[i + 1];
		}
		for (int i = 0; i < nfsr.length - 1; i++) {
			nfsr[i] = nfsr[i + 1];
		}

		nfsr[79] = feedbackNFSR;
		lfsr[79] = feedbackLFSR;

		lfsrPrim.clock();
		nfsrPrim.clock();

	}

//	
	//FUNCTION TO SUBSTITUE THE MONOMIALS
	//Steps
	//1.   Check for AND'S in the Formular
	//2.   Split the Variables
	//3.   Make equivalent clauses with substitute
	//4.   add the to the Solver
	
	//Example:     1&3 --> [1] [3] --> 	clause1 = (-subvar, 1)
	//									clause2 = (-subvar, 3)
	//									clause3 = (subvar, -1, -3)
	
	public ArrayList<Integer> substituteFeedbackFormel(ArrayList<String> formular){
	
		String formularType = formular.get(0);
		formular.remove(0);
		
		
		
		ArrayList<Integer> substitutedFormular = new ArrayList<Integer>();
		for (int i = 0; i < formular.size(); i++) {
			if(formular.get(i).contains("&")){
				
				String[] split = formular.get(i).split("&"); 
				int subvar = getNewSubVar();
				int[] clause = null;
				ArrayList<Integer> lastClause = new ArrayList<Integer>();
				
				for (int j = 0; j < split.length+1; j++) {
					if(j != split.length){
						if(split[j].substring(0,1).equals("l")){
							
							clause = new int[]{-subvar, lfsrPrim.get(Integer.parseInt(split[j].substring(1)))};
							lastClause.add(-(lfsrPrim.get(Integer.parseInt(split[j].substring(1)))));
							
						}else if(split[j].substring(0,1).equals("n")){
							
							clause = new int[]{-subvar, nfsrPrim.get(Integer.parseInt(split[j].substring(1)))};
							lastClause.add(-(nfsrPrim.get(Integer.parseInt(split[j].substring(1)))));
						}
						
						
						try {
							solver.addClause(new VecInt(clause));
						} catch (ContradictionException e) {
							e.printStackTrace();
						}
					}
					else{
						lastClause.add(subvar);
						try {
							solver.addClause(new VecInt(convertIntegers(lastClause)));
						} catch (ContradictionException e) {
							e.printStackTrace();
						}
					}
				}
				substitutedFormular.add(subvar);
			}
			else{
				if(formular.get(i).substring(0,1).equals("l")){
					substitutedFormular.add(lfsrPrim.get(Integer.parseInt(formular.get(i).substring(1))));
				}
				else if(formular.get(i).substring(0,1).equals("n")){
					substitutedFormular.add(nfsrPrim.get(Integer.parseInt(formular.get(i).substring(1))));
				}
				
			}
		}
		if(formularType.equals("n")){
			substitutedFormular.add(nfsrPrim.get(79)+1);
		}
		else if(formularType.equals("l")){
			substitutedFormular.add(lfsrPrim.get(79)+1);
		}
		formular.add(0, formularType);
		return substitutedFormular;
	}
	

	public void convertFalseFourSubForm(ArrayList<Integer> subFormel){
		
		int[] clause1 = new int[4];
		int[] clause2 = new int[4];
		int[] clause3 = new int[4];
		int[] clause4 = new int[4];
		int[] clause5 = new int[4];
		int[] clause6 = new int[4];
		int[] clause7 = new int[4];
		int[] clause8 = new int[4];
		
		clause1[0] = -subFormel.get(0); clause1[1] = subFormel.get(1); clause1[2] = subFormel.get(2); clause1[3] = subFormel.get(3);
		clause2[0] = subFormel.get(0); clause2[1] = -subFormel.get(1); clause2[2] = subFormel.get(2); clause2[3] = subFormel.get(3);
		clause3[0] = subFormel.get(0); clause3[1] = subFormel.get(1); clause3[2] = -subFormel.get(2); clause3[3] = subFormel.get(3);
		clause4[0] = subFormel.get(0); clause4[1] = subFormel.get(1); clause4[2] = subFormel.get(2); clause4[3] = -subFormel.get(3);
		clause5[0] = subFormel.get(0); clause5[1] = -subFormel.get(1); clause5[2] = -subFormel.get(2); clause5[3] = -subFormel.get(3);
		clause6[0] = -subFormel.get(0); clause6[1] = subFormel.get(1); clause6[2] = -subFormel.get(2); clause6[3] = -subFormel.get(3);
		clause7[0] = -subFormel.get(0); clause7[1] = -subFormel.get(1); clause7[2] = subFormel.get(2); clause7[3] = -subFormel.get(3);
		clause8[0] = -subFormel.get(0); clause8[1] = -subFormel.get(1); clause8[2] = -subFormel.get(2); clause8[3] = subFormel.get(3);
		
		try {
			solver.addClause(new VecInt(clause1));
			solver.addClause(new VecInt(clause2));
			solver.addClause(new VecInt(clause3));
			solver.addClause(new VecInt(clause4));
			solver.addClause(new VecInt(clause5));
			solver.addClause(new VecInt(clause6));
			solver.addClause(new VecInt(clause7));
			solver.addClause(new VecInt(clause8));
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
	}
	
public void convertTrueFourSubForm(ArrayList<Integer> subFormel){
		
		int[] clause1 = new int[4];
		int[] clause2 = new int[4];
		int[] clause3 = new int[4];
		int[] clause4 = new int[4];
		int[] clause5 = new int[4];
		int[] clause6 = new int[4];
		int[] clause7 = new int[4];
		int[] clause8 = new int[4];
		
		clause1[0] = -subFormel.get(0); clause1[1] = -subFormel.get(1); clause1[2] = subFormel.get(2); clause1[3] = subFormel.get(3);
		clause2[0] = -subFormel.get(0); clause2[1] = subFormel.get(1); clause2[2] = -subFormel.get(2); clause2[3] = subFormel.get(3);
		clause3[0] = -subFormel.get(0); clause3[1] = subFormel.get(1); clause3[2] = subFormel.get(2); clause3[3] = -subFormel.get(3);
		clause4[0] = subFormel.get(0); clause4[1] = -subFormel.get(1); clause4[2] = -subFormel.get(2); clause4[3] = subFormel.get(3);
		clause5[0] = subFormel.get(0); clause5[1] = -subFormel.get(1); clause5[2] = subFormel.get(2); clause5[3] = -subFormel.get(3);
		clause6[0] = subFormel.get(0); clause6[1] = subFormel.get(1); clause6[2] = -subFormel.get(2); clause6[3] = -subFormel.get(3);
		clause7[0] = -subFormel.get(0); clause7[1] = -subFormel.get(1); clause7[2] = -subFormel.get(2); clause7[3] = -subFormel.get(3);
		clause8[0] = subFormel.get(0); clause8[1] = subFormel.get(1); clause8[2] = subFormel.get(2); clause8[3] = subFormel.get(3);
		
		try {
			solver.addClause(new VecInt(clause1));
			solver.addClause(new VecInt(clause2));
			solver.addClause(new VecInt(clause3));
			solver.addClause(new VecInt(clause4));
			solver.addClause(new VecInt(clause5));
			solver.addClause(new VecInt(clause6));
			solver.addClause(new VecInt(clause7));
			solver.addClause(new VecInt(clause8));
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
	}
	
	public void convertFalseThreeSubForm(ArrayList<Integer> subFormel){
		
		int[] clause1 = new int[3];
		int[] clause2 = new int[3];
		int[] clause3 = new int[3];
		int[] clause4 = new int[3];
		
		
		clause1[0] = -subFormel.get(0); clause1[1] = subFormel.get(1); clause1[2] = subFormel.get(2); 
		clause2[0] = subFormel.get(0); clause2[1] = -subFormel.get(1); clause2[2] = subFormel.get(2); 
		clause3[0] = subFormel.get(0); clause3[1] = subFormel.get(1); clause3[2] = -subFormel.get(2);
		clause4[0] = -subFormel.get(0); clause4[1] = -subFormel.get(1); clause4[2] = subFormel.get(2); 
		
		
		try {
			solver.addClause(new VecInt(clause1));
			solver.addClause(new VecInt(clause2));
			solver.addClause(new VecInt(clause3));
			solver.addClause(new VecInt(clause4));
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
	}

	public void cutFeedbackFormel(ArrayList<Integer> formel){
		
		ArrayList<Integer> clause;
		
		//First Cut 
		if(formel.size() > 4){
			
			tmpCutvar = getNewCutVar();
			clause = new ArrayList<Integer>();
			clause.add(tmpCutvar);
			
			for (int i = 0; i < 3; i++) {
				clause.add(formel.get(0));
				formel.remove(0);
			}
			
			convertFalseFourSubForm(clause);
			
		}else{
			System.out.println("nohc nicht programmier");
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		//Cutting till the lits is empty
		while(formel.size() != 0){
			
			//bigger than 4
			if(formel.size() > 3){
				clause = new ArrayList<Integer>();
				clause.add(tmpCutvar);
				tmpCutvar = getNewCutVar();
				clause.add(tmpCutvar);
				
				for (int i = 0; i < 2; i++) {
					clause.add(formel.get(0));
					formel.remove(0);
				}
				
				convertFalseFourSubForm(clause);
				
			}
			else if(formel.size() == 3){
				
				clause = new ArrayList<Integer>();
				clause.add(tmpCutvar);
				
				for (int i = 0; i < 3; i++) {
					clause.add(formel.get(0));
					formel.remove(0);
				}
				convertFalseFourSubForm(clause);
			}
			else if(formel.size() == 2){
				clause = new ArrayList<Integer>();
				clause.add(tmpCutvar);

				for (int i = 0; i < 2; i++) {
					clause.add(formel.get(0));
					formel.remove(0);
				}
				convertFalseThreeSubForm(clause);
			}
		}
	}
	
public void cutOutputFormel(ArrayList<Integer> formel){
		
		ArrayList<Integer> clause;
		
		//First Cut 
		if(formel.size() > 4){
			
			tmpCutvar = getNewCutVar();
			clause = new ArrayList<Integer>();
			clause.add(tmpCutvar);
			
			for (int i = 0; i < 3; i++) {
				clause.add(formel.get(0));
				formel.remove(0);
			}
			if(output()){
				convertTrueFourSubForm(clause);
			}else{
				convertFalseFourSubForm(clause);
			}
			
			
			
		}else{
			System.out.println("nohc nicht programmier");
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		//Cutting till the lits is empty
		while(formel.size() != 0){
			
			//bigger than 4
			if(formel.size() > 3){
				clause = new ArrayList<Integer>();
				clause.add(tmpCutvar);
				tmpCutvar = getNewCutVar();
				clause.add(tmpCutvar);
				
				for (int i = 0; i < 2; i++) {
					clause.add(formel.get(0));
					formel.remove(0);
				}
				
				convertFalseFourSubForm(clause);
				
			}
			else if(formel.size() == 3){
				
				clause = new ArrayList<Integer>();
				clause.add(tmpCutvar);
				
				for (int i = 0; i < 3; i++) {
					clause.add(formel.get(0));
					formel.remove(0);
				}
				convertFalseFourSubForm(clause);
			}
			else if(formel.size() == 2){
				clause = new ArrayList<Integer>();
				clause.add(tmpCutvar);

				for (int i = 0; i < 2; i++) {
					clause.add(formel.get(0));
					formel.remove(0);
				}
				convertFalseThreeSubForm(clause);
			}
		}
	}

public void  solutionFinder(){
	int[] model = null;
	String[] help;
	ArrayList<String> lfsr = new ArrayList<String>();
	ArrayList<String> nfsr = new ArrayList<String>();
	
	IProblem problem = solver;
	try {
		if(problem.isSatisfiable()) {
			 model = problem.findModel();
		}else {
			System.out.println("nicht satisfiable");
		}
			
		
	} catch (TimeoutException e) {
		e.printStackTrace();
	} 
	
	help = new String[model.length];
	for (int i = 0; i < model.length; i++) {
		help[i] = ""+ model[i];
	}
	for (int i = 0; i < help.length; i++) {
		if(help[i].charAt(0)=='-'){
		switch(help[i].charAt(1)){
			case '1':
			
				lfsr.add(help[i].substring(2));
			break;
			
			case '2':
				nfsr.add(help[i].substring(2));
			break;
			}
		}
	}
	System.out.println("Alle negativen Variablen im LFSR: ");
	for (int i = 0; i < lfsr.size(); i++) {
		
		System.out.print(lfsr.get(i)+" ");
	}
	System.out.println();
//	int[] iv = new int[80];
//	int[] key=new int[Integer.parseInt(nfsr.get(lfsr.size()-1))];
	int[] iv = new int[63];
	int[] key=new int[80];

	int counter = 0;
	int tmp;
	
	for (int i = 0; i < iv.length; i++) {
		tmp = Integer.parseInt(lfsr.get(counter));
		if(tmp == i){
			iv[i] = 0;
			counter++;
		}
		else{
			iv[i] = 1;
		}
	}
	counter = 0;
	for (int i = 1; i < key.length+1; i++) {
		tmp = Integer.parseInt(nfsr.get(counter));
		if(tmp == i){
			key[i-1] = 0;
			counter++;
		}
		else{
			key[i-1] = 1;
		}
	}
	System.out.println("IV: ");
	for (int i = 0; i < iv.length; i++) {
		System.out.print(iv[i]);
	}
	System.out.println();
	System.out.println("Key: ");
	for (int i = 0; i < key.length; i++) {
		System.out.print(key[i]);
	}
}
	
	public void convert(int times){
		for (int i = 0; i < times; i++) {
			
		
		ArrayList<Integer> tmp = substituteFeedbackFormel(nfsrFeedbackFormular);
		ArrayList<Integer> tmp2 = substituteFeedbackFormel(lfsrFeedbackFormular);
		ArrayList<Integer> tmp3 = substituteFeedbackFormel(outputFormular);
		
		
		
//		for (int i = 0; i < tmp.size(); i++) {
//			System.out.print(tmp.get(i)+" ");
//		}
//		System.out.println("  nfsr \n-----------------");
//		for (int i = 0; i < tmp2.size(); i++) {
//			System.out.print(tmp2.get(i)+" ");
//		}
//		System.out.println("  lfsr\n---------------");
//		for (int i = 0; i < tmp3.size(); i++) {
//			System.out.print(tmp3.get(i)+" ");
//		}
//		
//		System.out.println(" output \n-----------------------");
		cutFeedbackFormel(tmp);
		cutFeedbackFormel(tmp2);
		cutOutputFormel(tmp3);
		clockOneTime();
		}
	}
	
	public static void main(String[] args){
		AnfToCnf atc = new AnfToCnf();
		atc.convert(85);
		
		
		//adding keys to the SatSolver ; keys are in the nfsr
		for (int i = 0; i < 52; i++) {
			if(atc.key.charAt(i) == '1'){
				try {
					atc.solver.addClause(new VecInt(new int[]{Integer.parseInt("2"+(i+1))}));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
			else{
				try {
					atc.solver.addClause(new VecInt(new int[]{-Integer.parseInt("2"+(i+1))}));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		for (int i = 0; i < 14 ; i++) {
			if(atc.iv.charAt(i) == '1'){
				try {
					atc.solver.addClause(new VecInt(new int[]{Integer.parseInt("1"+(i+1))}));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
			else{
				try {
					atc.solver.addClause(new VecInt(new int[]{-Integer.parseInt("1"+(i+1))}));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
			
		}
	
			//00000001001000110100010101100111100010011010101111001101111011110001001000110100	
		atc.solutionFinder();
	}

}
