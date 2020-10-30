package workflow.simulation.app;

import java.io.OutputStream;

import org.griphyn.vdl.dax.ADAG;

public interface Application {
	    public ADAG getDAX();
	    public void generateWorkflow(String... args) throws Exception;
	    public void printWorkflow(OutputStream os) throws Exception;
}
