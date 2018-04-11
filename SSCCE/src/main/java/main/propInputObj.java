package main;

import java.io.IOException;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.AttitudesSequence;
import org.orekit.attitudes.BodyCenterPointing;
import org.orekit.attitudes.YawSteering;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.Ellipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.forces.ForceModel;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinatesProvider;

public class propInputObj {
	
	static int stepsize;
	static double minStep;
	static double maxStep;
	static double positionTol;
	static groundstation gs;
	static BodyShape earthBody;
	static PVCoordinatesProvider sun;
	static Vector3D vector;
	static Frame frameinertial;
	static ForceModel holmesFeatherstone;
	static Frame orbitFrame;
	
	ElevationDetector gsVis;
	BodyCenterPointing nadirLawZ;
	AttitudeProvider attProv;
	SpacecraftState state;
	Sequencer sequencer;
	OrbitType propagationType;
	double[][] tolerance;
	AdaptiveStepsizeIntegrator integrator;
	NumericalPropagator propagator;
	propStepHandler shandler;
	AttitudesSequence custSeq;
	
	propInputObj(int stepsize, groundstation gs, double minStep, double maxStep, double positionTol, BodyShape earthBody, Frame frameinertial, Frame orbitFrame) throws OrekitException {
		propInputObj.earthBody = earthBody;
		propInputObj.stepsize = stepsize;
		propInputObj.gs = gs;
		propInputObj.minStep = minStep;
		propInputObj.maxStep = maxStep;
		propInputObj.positionTol = positionTol;	
		propInputObj.orbitFrame = orbitFrame;
		propInputObj.sun = CelestialBodyFactory.getSun();
		propInputObj.vector = new Vector3D(1, 1, 0);
		NormalizedSphericalHarmonicsProvider provider = GravityFieldFactory.getNormalizedProvider(10, 10);
		propInputObj.holmesFeatherstone = new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010, true), provider);
	}
	
	//NumericalPropagator createPropagator(AbsoluteDate initialDate, Orbit orbit, propStepHandler shandler) throws OrekitException, IOException {
		NumericalPropagator createPropagator(AbsoluteDate initialDate, Orbit orbit) throws OrekitException, IOException {

		gsVis = new ElevationDetector(60, 0.001, gs.getFrame());
		nadirLawZ = new BodyCenterPointing(orbit.getFrame(), (Ellipsoid) earthBody);
		attProv = new YawSteering(orbitFrame, nadirLawZ, sun, vector.normalize());
		state = new SpacecraftState(orbit, attProv.getAttitude(orbit, orbit.getDate(), orbit.getFrame()));
		custSeq = new AttitudesSequence();
		sequencer = new Sequencer(attProv, custSeq);
		sequencer.targetPoint(earthBody.transform(gs.getLocation()), orbit.getFrame(), gs.getFrame(), earthBody, gsVis);
		propagationType = orbit.getType();
		tolerance = NumericalPropagator.tolerances(positionTol, orbit, propagationType);
		integrator = new DormandPrince853Integrator(minStep, maxStep, tolerance[0], tolerance[1]);
		propagator = new NumericalPropagator(integrator);
		gsVis.init(state, initialDate);
		propagator.setOrbitType(propagationType);
		propagator.addForceModel(holmesFeatherstone);
		shandler = new propStepHandler();
		propagator.setMasterMode(stepsize, shandler);
		propagator.setInitialState(state);
		propagator.setAttitudeProvider(sequencer.custSeq);
		propagator.setAttitudeProvider(attProv);
		custSeq = sequencer.getSeq();
		custSeq.registerSwitchEvents(propagator);
		return propagator;
	}
}
