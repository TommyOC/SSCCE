package main;

import java.io.FileWriter;
import java.io.IOException;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;

public class propStepHandler implements OrekitFixedStepHandler {
	Vector3D ones = new Vector3D(1,1,1);
	Sequencer sequencer;

	FileWriter writer = new FileWriter("AttitudeTest.csv");

	//System.out.println(writer.getAbsolutePath());
	
	public propStepHandler() throws IOException {
//		writer.append("Time");
//		writer.append(",");
//		//writer.append("X");
//		//writer.append(",");
//		//writer.append("Y");
//		//writer.append(",");
//		writer.append("Z");
//		writer.append(System.getProperty("line.separator"));
		System.out.println("Step handler initiated");
	}

	@Override
	public void handleStep(SpacecraftState currentState, boolean isLast) throws OrekitException {
		// TODO Auto-generated method stub
		
		System.out.println("propagator step");
		
		Vector3D pointing = currentState.getAttitude().getRotation().applyInverseTo(Vector3D.PLUS_K);

		try {
			writer.append(String.valueOf(currentState.getDate()));
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
//		try {
//			writer.append(String.valueOf(pointing.getX()));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			writer.append(",");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			writer.append(String.valueOf(pointing.getY()));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			writer.append(",");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			writer.append(String.valueOf(pointing.getZ()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.append(System.getProperty("line.separator"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (isLast) { 

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
			System.out.println("Last line");
			System.out.println();
			
			
		}
	}
}
