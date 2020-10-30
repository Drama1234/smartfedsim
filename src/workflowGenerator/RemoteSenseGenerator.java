package workflowGenerator;

import java.util.Arrays;

import com.weiyu.experiment.simulation.app.Application;
import com.weiyu.experiment.simulation.app.Montage;

public class RemoteSenseGenerator {
	public static void main(String[] args) throws Exception{
		int[] tasksNumbers = {13, 53, 83, 103, 143};
		for(int taskNumber:tasksNumbers) {
			generateWorkflows(taskNumber);
		}
	}
	
	private static void generateWorkflows(int taskNumber) throws Exception{
		double[] runtime = {0.5,2,3};
		String[] args = { "-a", "RemoteSense", "-n", String.valueOf(taskNumber)};
		String[] newArgs = Arrays.copyOfRange(args, 2, args.length);
		
		Application app = new RemoteSense();
		
	}

}
