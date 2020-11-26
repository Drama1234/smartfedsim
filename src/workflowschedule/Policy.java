package workflowschedule;

import org.jgap.Gene;
import org.jgap.IChromosome;

import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;

public abstract class Policy {
	public static final char ASCENDENT_TYPE = 'A';
	public static final char DESCENDENT_TYPE = 'D';
	public static final char EQUAL_TYPE = 'E';
	
	protected final static int RUNTIME_ERROR = 1000;
	protected final int MAXSATISFACTION_DISTANCE = -1;
	protected static final boolean DEBUG = true;
	
	private double weight;
	private char type;
	protected String constraintName = "Genetic algorithm";
	
	public Policy(double weight, char type) {
		this.weight = weight;
		this.type = type;
	}
	
//	public double evaluatePolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet) {
//		double res = evaluateGlobalPolicy(gene_index, chromos, app, prov, internet);
//		return res;
//	}
	
	protected abstract double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet);

	public char getType(){
		return type;
	}
	public double getWeight(){
		return weight;
	}
	public void setWeight(double w){
		weight = w;
	}
	
	/*
	 * value is what I have, u_constraint is what I want, maxValue is best value
	 */
	public double evaluateDistance(double value, double u_constraint, double maxValue) throws Exception{
		if (maxValue == 0){
			throw new Exception("Max Value not set in method " + this.getClass().getName());
		}
		switch (type){
		case ASCENDENT_TYPE:
			return Double.valueOf(String.format("%.2f", (u_constraint - value)/maxValue));
		case DESCENDENT_TYPE:
			return Double.valueOf(String.format("%.2f", (value - u_constraint)/maxValue));
		case EQUAL_TYPE:
			throw new Exception("Wrong method for this type of constraint");
		default:
			return 0;
		}
	}
	
	public double evaluateDistance(String value, String u_constraint) throws Exception{
		value.trim();
		u_constraint.trim();
		switch (type){
		case ASCENDENT_TYPE:
			throw new Exception("Wrong method for this type of constraint");
		case DESCENDENT_TYPE:
			throw new Exception("Wrong method for this type of constraint");
		case EQUAL_TYPE:
			double ret = (value.compareTo(u_constraint) == 0 ) ? -1 : 0; // 0 if different one each other
			return ret;
		default:
			return 0;
		}
	}
	
	protected double calculateDistance_ErrHandling(long prov, long node, Double max){
		String name = this.constraintName;
		double distance;
		try {
			distance = evaluateDistance(prov, node, max);
		} catch (Exception e) {
			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG)
			switch (type) {
			case ASCENDENT_TYPE:
				System.out.println("\t Eval on " + name + " " + node + "-" + prov + "/" + max + "=" + distance);
				break;
			case DESCENDENT_TYPE:
				System.out.println("\t Eval on " + name + " " + prov + "-" + node + "/" + max + "=" + distance);
				break;
			default:
				return 0;
			}
		return distance;
	}
	
	protected double calculateDistance_ErrHandling(Double cost, Double budget, Double maxCost){
		String name = this.constraintName;
		double distance;
		try {
			distance = evaluateDistance(cost, budget, maxCost);
		} catch (Exception e) {
			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("\t Eval on " + name + " " + cost + "-" + budget + "/" + maxCost + "=" + distance);
		return distance;
	}
	
	public static int getGeneIndexFromNodeId(int vmId, Gene[] genes, IMSApplication app){
		int target_index = 0;
		boolean trovato = false;
		for (int i=0; i<genes.length && !trovato; i++){
			if (app.getNodes().get(i).getID() == vmId)
				target_index = i;
		}
		return target_index;
	}

	public String getName() {
		return this.constraintName;
	}	
}
