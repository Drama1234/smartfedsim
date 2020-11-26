package workflowschedule;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jgap.Gene;
import org.jgap.IChromosome;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;

public class Solution {
	//vmId->dcId
	private HashMap<Integer, Integer> associationMap;
	List<String> messages;
	private double fit;
	IChromosome chromosome = null;
	private double cost;
	private double makespan;
	private boolean valid;
	
	public IChromosome getChromosome() {
		return chromosome;
	}

	public void setChromosome(IChromosome chromosome) {
		this.chromosome = chromosome;
	}
	
	public Solution(){
		associationMap = new HashMap<Integer, Integer>();
	}
	
	private HashMap<Integer, Integer> createMapping(List<MSApplicationNode>nodes){
		HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
		for (int i=0; i < chromosome.size(); i++){
			mapping.put(nodes.get(i).getID(), (Integer) chromosome.getGene(i).getAllele());
		}
		return mapping;
	}
	
	public Solution(IChromosome chromosome, List<MSApplicationNode> nodes){
		setSolution(chromosome, nodes);
	}
	
	public void setSolution(IChromosome chromosome, List<MSApplicationNode> nodes){
		this.setChromosome(chromosome);
		this.associationMap = createMapping(nodes);
		this.fit = chromosome.getFitnessValue();
		this.valid = checkCompleteSatisfaction(chromosome);
	}
	
	private boolean checkCompleteSatisfaction(IChromosome chromosome){
		//对染色体中的每个基因的适应度是否有值进行判断，全部有值为true.
		boolean res = true;
		Gene[] genes = chromosome.getGenes();
		int i = 0;
		while (i < genes.length){
			CIntegerGene g = (CIntegerGene) genes[i];
			if (g.getFitness() == 0){
				res = false;
				break;
			}
			i++;
		}
		return res;
	}
	
	/**
	 * It returns the association between the applianceId and 
	 * the providerId for a single Application
	 * @return
	 */
	public HashMap<Integer, Integer> getAllocationMap(){
		return associationMap;
	}
	
	@Deprecated
	public void setFit(double fit){
		this.fit = fit;
	}
	
	public double getFit(){
		return this.fit;
	}
	
	public void setMessages(List<String> messages){
		this.messages = messages;
	}
	public List<String> getMessages(){
		return this.messages;
	}
	
	private String fitnessToString(){
		Gene[] genes = chromosome.getGenes();
		String s = "Total Fitness ";
		s += fit + " ( |";
		for (int i = 0; i < genes.length; i++) {
			s += ((CIntegerGene) genes[i]).getFitness() + "|";
		}
		s+=" )";
		return s;
	}
	
	public String toString(){
		String s = "[MS SOLUTION] cloudlet ";
		Set<Integer> keys = associationMap.keySet();
		s += "<";
		for (Integer key : keys) {
			s += key + ",";
		}
		s += ">\n dc |";
		for (Integer key : keys) {
			s += associationMap.get(key) + "|";
		}
		s+= " \n" + fitnessToString() + "\n";
		s+= "cost: " + this.getCostAmount() + "\n";
		s+= "makespan: " + this.getMakespan() + "\n";
		return s;
	}
	
	public String getInternalRepresentation(){
		String repr = new String();
		Set<Integer> keys = associationMap.keySet();
		repr += "(key, value)\n";
		for (Integer key : keys) {
			repr += " (" + key + ", " + associationMap.get(key) + ") \n";
		}
		return repr;
	}
	
	/**
	 * Set the cost to be paid for the allocation represented by
	 * this solution
	 * @param cost
	 */
	public void setCostAmount(double cost){
		this.cost = cost; 
	}
	
	public double getCostAmount(){
		return cost; 
	}
	
	
	public double getMakespan() {
		return makespan;
	}

	public void setMakespan(double makespan) {
		this.makespan = makespan;
	}

	public boolean getCompleteSatisfaction() {
		return valid;
	}
}
