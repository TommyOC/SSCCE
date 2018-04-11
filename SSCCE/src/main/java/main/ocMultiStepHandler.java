/**
 * 
 */
package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.sampling.MultiSatStepHandler;
import org.orekit.propagation.sampling.OrekitStepInterpolator;

/**
 * @author tommy
 *
 */
public class ocMultiStepHandler implements MultiSatStepHandler {

	FileWriter writer = new FileWriter("AttitudeTestMulti.csv");

	public ocMultiStepHandler(int propNum) throws IOException {		
		for(int i=0; i<(propNum); i++) {
			writer.append("Time of orbit: ");
			writer.append(Integer.toString(i));
			writer.append(",");
			writer.append("Z-axis of orbit: ");
			writer.append(Integer.toString(i));
			if(i != propNum-1) {
				writer.append(",");
			}
		}
		writer.append(System.getProperty("line.separator"));
		System.out.println("Paralleliser init");
	}

	/* (non-Javadoc)
	 * @see org.orekit.propagation.sampling.MultiSatStepHandler#handleStep(java.util.List, boolean)
	 */
	@Override
	public void handleStep(List<OrekitStepInterpolator> interpolators, boolean isLast) throws OrekitException {
		// TODO Auto-generated method stub
		//System.out.println("paralleliser step, time is: ");
		System.out.println(String.valueOf(interpolators.get(0).getCurrentState().getDate()));
		System.out.println("Parelleliser step");
		for(int i=0; i<interpolators.size(); i++) {
			try {
				writer.append(String.valueOf(interpolators.get(i).getCurrentState().getDate()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.append(",");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Vector3D pointing = interpolators.get(i).getCurrentState().getAttitude().getRotation().applyInverseTo(Vector3D.PLUS_K);
			try {
				writer.append(String.valueOf(pointing.getZ()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(i != interpolators.size()-1) {
				try {
					writer.append(",");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			writer.append(System.getProperty("line.separator"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(isLast) {
			try {
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println();
			System.out.println("Last Line");
			System.out.println();
		}
	}

}
