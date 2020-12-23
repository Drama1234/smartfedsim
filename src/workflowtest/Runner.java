package workflowtest;

import it.cnr.isti.smartfed.test.TestResult;
import workflowmapping.GeneticAllocator;

public class Runner {
	private static StringBuilder sb = new StringBuilder();
	
	public static void main(String[] args) {
		sb.append("\t\texecution time").append("\t\t").append("budget\t").append("\n");
//		String[] files = new String[]{"RemoteSense_13","RemoteSense_23","RemoteSense_53","RemoteSense_83","RemoteSense_103","RemoteSense_143"};
//		String[] files = new String[] {"RemoteSense_103"};
		String[] files = new String[] {"RemoteSense_13"};
		for (String file : files) {
			runworkflow(file);			
		}
		System.out.println(sb);
	}
	
	private static void runworkflow(String filename) {
		WorkflowDataset dataset = new WorkflowDataset(20, filename);
		TestResult.reset();
		GeneticAllocator allocator = new GeneticAllocator();
//		allocator.setPolicyType();//全局网络		
//		for(int i= 0;i<10;i++) {
			allocator.setRandomSeed(77);
			Experiment experiment = new Experiment(allocator, dataset, 77);		
			experiment.run();
//		}
		
		double makespan = TestResult.getCompletion().getMean();
		double cost = TestResult.getCost().getMean();
		
		sb.append(filename).append("\t");
		sb.append(makespan).append("\t").append(cost).append("\t");
	}
}

