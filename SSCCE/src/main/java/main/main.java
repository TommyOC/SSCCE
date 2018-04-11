package main;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.propagation.sampling.MultiSatStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.propagation.PropagatorsParallelizer;
import org.orekit.propagation.SpacecraftState;

public class main {
	public static void main(String args[]) throws Exception {
		 // configure Orekit
        File home       = new File(System.getProperty("user.home"));
        File orekitData = new File(home, "orekit-data");
        if (!orekitData.exists()) {
            System.err.format(Locale.US, "Failed to find %s folder%n",
                              orekitData.getAbsolutePath());
            System.err.format(Locale.US, "You need to download %s from the %s page and unzip it in %s for this tutorial to work%n",
                              "orekit-data.zip", "https://www.orekit.org/forge/projects/orekit/files",
                              home.getAbsolutePath());
            System.exit(1);
        }
        DataProvidersManager manager = DataProvidersManager.getInstance();
        manager.addProvider(new DirectoryCrawler(orekitData));
        
		
		AbsoluteDate initialDate = new AbsoluteDate(2003, 03, 03, 15, 50, 55.000, TimeScalesFactory.getUTC());
		AbsoluteDate target = new AbsoluteDate(2003, 03, 03, 19, 35, 55.000, TimeScalesFactory.getUTC());
		//AbsoluteDate target = new AbsoluteDate(2003, 03, 03, 15, 35, 55.000, TimeScalesFactory.getUTC());
		mainProp mainprop = new mainProp(initialDate);
		mainprop.startSim();
		//mainprop.propagator.propagate(target);
		List<SpacecraftState> states = mainprop.getStates();
		ocMultiStepHandler multihandler = new ocMultiStepHandler(states.size());
		multihandler.init(states, initialDate);
		
		System.out.println("Number of propagators: " + String.valueOf(mainprop.getPropagators().size()));
		
		PropagatorsParallelizer parallelizer = new PropagatorsParallelizer(mainprop.getPropagators(), multihandler);
		
		parallelizer.propagate(initialDate, target);
		//mainprop.getPropagators().get(0).propagate(initialDate, target);
		//mainprop.getPropagators().get(0).propagate(target);
	}

}
