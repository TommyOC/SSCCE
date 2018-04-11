package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.orekit.bodies.BodyShape;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

public class mainProp {

	private static final double mu = Constants.WGS84_EARTH_MU;
	double a0 = 6878000;
	double e0 = 0.0000000001;
	double i0 = 0.0000000001;
	double raan0 = 0.0;
	double omega0 = 0;
	double trueAnomaly0 = 0;
	double a1 = 6878000;
	double e1 = 0.0000000001;
	double i1 = 20;
	double raan1 = 20.0;
	double omega1 = 20;
	double trueAnomaly1 = 20;
	
	int stepsize = 5;
	AbsoluteDate initialDate;
	BodyShape earthBody;
	Frame frameinertial;
	Frame orbitFrame;

	Orbit orbit0;
	Orbit orbit1;
	private List<Orbit> orbits = new ArrayList<>(0);

	groundstation gs;
	
	double minStep = 0.001;
	double maxStep = 100;
	double positionTol = 10;
	double[][] tolerance;
	
	List<Propagator> propagators = new ArrayList<>(0);
	List<SpacecraftState> states = new ArrayList<>(0);
	//ocMultiStepHandler multihandler;
	
	public mainProp(AbsoluteDate initial) throws OrekitException {
		this.initialDate = initial;
		frameinertial = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
		earthBody = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
				Constants.WGS84_EARTH_FLATTENING, frameinertial);
		orbitFrame = FramesFactory.getEME2000();
		
		orbit0 = new KeplerianOrbit(a0, e0, i0, omega0, raan0, trueAnomaly0, PositionAngle.TRUE, orbitFrame, initialDate, mu);	
		orbits.add(orbit0);
		orbit1 = new KeplerianOrbit(a1, e1, i1, omega1, raan1, trueAnomaly1, PositionAngle.TRUE, orbitFrame, initialDate, mu);
		orbits.add(orbit1);
		
		gs = new groundstation( 0.3, 3.1415, 0.0, 0.087);
	}

	public void startSim() throws OrekitException, IOException {
		gs.initFrame(earthBody);
		propInputObj propFactory = new propInputObj(stepsize, gs, minStep, maxStep, positionTol, earthBody, frameinertial, orbitFrame);
		
		for(int i=0; i<orbits.size(); i++) {
			Orbit orbit = orbits.get(i);
			//propStepHandler shandler = new propStepHandler();
			//propagators.add(propFactory.createPropagator(initialDate, orbit, shandler));
			NumericalPropagator prop = propFactory.createPropagator(initialDate, orbit);
			
			propagators.add(prop);
			states.add(propagators.get(i).getInitialState());
		}	
	}
	
	public List<SpacecraftState> getStates() {
		return states;
	}
	
	public List<Propagator> getPropagators(){
		return propagators;
	}
}
