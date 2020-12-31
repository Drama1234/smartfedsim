package workflowtest;

import it.cnr.isti.smartfed.test.TestResult;
import workflowmapping.GeneticAllocator;
import workflowmapping.GeneticAllocator1;
import workflowmapping.RandomAllocator;

public class Runner {
	private static StringBuilder sb = new StringBuilder();
	
	public static void main(String[] args) {
		sb.append("\t\tmakespan").append("\t\t").append("budget\t\t").append("realduration\t").append("\n");
//		String[] files = new String[]{"RemoteSense_13","RemoteSense_23","RemoteSense_53","RemoteSense_83","RemoteSense_103","RemoteSense_143"};
//		String[] files = new String[] {"RemoteSense_103"};
		String[] files = new String[] {"RemoteSense_143"};
		for (String file : files) {
			runworkflow(file);			
		}
		System.out.println(sb);
	}
	
	private static void runworkflow(String filename) {
		WorkflowDataset dataset = new WorkflowDataset(20, filename);
		
		TestResult.reset();
		GeneticAllocator allocator = new GeneticAllocator();
		System.out.println("基于时间和成本的遗传算法");
//		allocator.setPolicyType();//全局网络		
		for(int i= 0;i<10;i++) {
			allocator.setRandomSeed(i*7);
			Experiment experiment = new Experiment(allocator, dataset, i*7);
			experiment.run();
		}
		
		double makespan = TestResult.getCompletion().getMean();
		double cost = TestResult.getCost().getMean();
		double realduration = TestResult.getRealDuration().getMean();
		
		sb.append(filename).append("\t");
		sb.append(makespan).append("\t").append(cost).append("\t").append(realduration).append("\t\n");
		
		TestResult.reset();
		GeneticAllocator1 allocator1 = new GeneticAllocator1();
		System.out.println("基于多个目标约束的遗传算法");
//		allocator.setPolicyType();//全局网络		
		for(int i= 0;i<10;i++) {
			allocator1.setRandomSeed(i*7);
			Experiment experiment1 = new Experiment(allocator, dataset, i*7);
			experiment1.run();
		}
		
		double dd_makespan = TestResult.getCompletion().getMean();
		double dd_cost = TestResult.getCost().getMean();
		double dd_realduration = TestResult.getRealDuration().getMean();
		
		sb.append(filename).append("\t");
		sb.append(dd_makespan).append("\t").append(dd_cost).append("\t").append(dd_realduration).append("\t\n");
		
//		TestResult.reset();
//		RandomAllocator allocator2 = new RandomAllocator();
//		System.out.println("随机算法");
////		allocator.setPolicyType();//全局网络		
////		for(int i= 0;i<10;i++) {
//			allocator1.setRandomSeed(77);
//			Experiment experiment2 = new Experiment(allocator2, dataset, 77);
//			experiment2.run();
////		}
//		
//		double rr_makespan = TestResult.getCompletion().getMean();
//		double rr_cost = TestResult.getCost().getMean();
//		double rr_realduration = TestResult.getRealDuration().getMean();
//		
//		sb.append(filename).append("\t");
//		sb.append(rr_makespan).append("\t").append(rr_cost).append("\t").append(rr_realduration).append("\t\n");
	}
}

