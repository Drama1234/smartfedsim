package workflowschedule;

import java.util.List;

import org.jgap.Gene;
import org.jgap.IChromosome;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;

public abstract class MSPolicy {
	
	public enum ConstraintScope
	{
		Global,
		Local
	}
	
	public static final char ASCENDENT_TYPE = 'A';
	public static final char DESCENDENT_TYPE = 'D';
	public static final char EQUAL_TYPE = 'E';
	
	protected final static int RUNTIME_ERROR = 1000;
	protected final int MAXSATISFACTION_DISTANCE = -1;
	protected static final boolean DEBUG = true;
	
	private double weight;
	private char type;
	private ConstraintScope scope;
	protected String constraintName = "Generic";
	
	public MSPolicy(double weight,char type,ConstraintScope group) {
		this.weight = weight;
		this.type = type;
		this.scope = group;
	}
	
	public MSPolicy(double weight, char type){
		this(weight, type, ConstraintScope.Local);
	}
	public double evaluatePolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet){
		double res;
		
		switch (this.scope){
		case Global: 
			res = evaluateGlobalPolicy(gene_index, chromos, app, prov, internet);
			break;
		case Local:
			res = evaluateLocalPolicy(chromos.getGene(gene_index), app.getNodes().get(gene_index), prov, internet);
			break;
		default:
			res = evaluateLocalPolicy(chromos.getGene(gene_index), app.getNodes().get(gene_index), prov, internet);
		}
		return res;
	}
	//为了抽象类继承少一个，如果没有还可以放入本地
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet){
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		return evaluateLocalPolicy(chromos.getGene(gene_index), node, prov, null);
	}
	
	protected abstract double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet);
	
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
			return (u_constraint - value)/maxValue;
		case DESCENDENT_TYPE:
			return (value - u_constraint)/maxValue;
		case EQUAL_TYPE:
			throw new Exception("Wrong method for this type of constraint");
		default:
			return 0;
		}
	}
	
	public double evaluateDistance(String value, String u_constraint) throws Exception{
		value.trim();
		u_constraint.trim();
		//final double epsilon = 0.99;
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
	
	protected double calculateDistance_ErrHandling(long cost, long budget, Double max){
		String name = this.constraintName;
		double distance;
		try {
			distance = evaluateDistance(cost, budget, max);
		} catch (Exception e) {
			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG){
//			if (this instanceof NetworkBWConstraint){
//				System.out.println("\t Eval on " + name + " " + "每秒成本：" + NetworkBWConstraint.printMBperSec(cost) + "-" + "每秒预算" + NetworkBWConstraint.printMBperSec(budget)
//						+ "/" + "最大值：" + NetworkBWConstraint.printMBperSec(max) + "=" + distance);
//			}
//			else 
				System.out.println("\t Eval on " + name + " " + cost + "-" + budget + "/" + max + "=" + distance);
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
