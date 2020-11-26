package workflowtest;


import it.cnr.isti.smartfed.test.TestResult;
import workflowmapping.GeneticAllocator;

public class Runner {	
	public static void main(String[] args) {
		String[] files = new String[]{"RemoteSense_13"};
		for (String file : files) {
			runworkflow(file);			
		}
	}
	
	private static void runworkflow(String filename) {
		WorkflowDataset dataset = new WorkflowDataset(20, filename);
		TestResult.reset();
		GeneticAllocator allocator = new GeneticAllocator();
//		allocator.setPolicyType();//全局网络		
		
		allocator.setRandomSeed(77);
		Experiment experiment = new Experiment(allocator, dataset, 77);		
		experiment.run();
	}
}
	
//	private static String f(double value)
//	{
//		return String.format(Locale.ENGLISH, "%.2f", value);
//	}
