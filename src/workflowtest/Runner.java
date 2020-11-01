package workflowtest;


import it.cnr.isti.smartfed.test.TestResult;
import workflowmapping.GeneticAllocator;

public class Runner {
	private static StringBuilder sb = new StringBuilder();
	
	public static void main(String[] args) {
//		sb.append("makespan\t").append("cost\t");
		String[] files = new String[]{"RemoteSense_13"};
		for (String file : files) {
			runworkflow(file);
			
//			System.out.println(sb);
		}
	}
	
	private static void runworkflow(String filename) {
		WorkflowDataset dataset = new WorkflowDataset(20, filename);
		TestResult.reset();
		GeneticAllocator allocator = new GeneticAllocator();
		allocator.setPolicyType();//全局网络		
		
		//for (int i=0; i<10; i++)
		//{ 
			allocator.setRandomSeed(77);
			dataset.setSeed(77);
			Experiment experiment = new Experiment(allocator, dataset);		
			experiment.run();
		//}
		
//		double makespan = TestResult.getCompletion().getMean();
//		double cost = TestResult.getCost().getMean();
//		
//		sb.append(filename).append("\t");
//		sb.append(f(makespan)).append("\t").append(f(cost)).append("\t");
	}
	
//	private static String f(double value)
//	{
//		return String.format(Locale.ENGLISH, "%.2f", value);
//	}
}
