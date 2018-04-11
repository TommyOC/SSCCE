package main;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.AttitudesSequence;
import org.orekit.attitudes.TargetPointing;
import org.orekit.bodies.BodyShape;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.utils.AngularDerivativesFilter;

public class Sequencer {
	AttitudeProvider initialAttProv;

	AttitudesSequence custSeq;
	
	Sequencer(AttitudeProvider attProv, AttitudesSequence custSeq){
		this.initialAttProv = attProv;
		this.custSeq = custSeq;
	}
	
	void targetPoint(Vector3D targetPosition, Frame inertial, Frame body, BodyShape bodyShape, AbstractDetector detector) throws OrekitException {
		
		//creates 2 attitude providers that sets the 2 modes, the first points to a target, the second is a nadir pointing mode
		AttitudeProvider targetPoint = new TargetPointing(inertial, body, targetPosition);
		
/*		AttitudeProvider nadir = new NadirPointing(inertial, bodyShape);
		YawSteering yaw = new YawSteering(inertial, (GroundPointing) nadir, sun, getMainPanelSide());*/
		
		//sets up 2 handlers to change the mode of the satellite when the event is triggered. This is primarily used in calculating the power of the AOCS system
		AttitudesSequence.SwitchHandler targetHandlerPlus = new AttitudesSequence.SwitchHandler() {
			@Override
			public void switchOccurred(AttitudeProvider preceding, AttitudeProvider following, SpacecraftState state)
					throws OrekitException {
				// TODO Auto-generated method stub
				if( preceding == initialAttProv) {
					System.out.println("SWITCH:");
					System.out.println(String.valueOf(state.getDate()));
				}
			}
		};	
		AttitudesSequence.SwitchHandler targetHandlerMinus = new AttitudesSequence.SwitchHandler() {
			@Override
			public void switchOccurred(AttitudeProvider preceding, AttitudeProvider following, SpacecraftState state)
					throws OrekitException {
				// TODO Auto-generated method stub
				if( preceding == targetPoint) {
					System.out.println("switchback");
					System.out.println(String.valueOf(state.getDate()));
				}
			}
		};
		//Adds the switching condition, in this it changes the attitude when the the event in the detector is triggered
		custSeq.addSwitchingCondition(initialAttProv, targetPoint, detector, true, false, 10, AngularDerivativesFilter.USE_RR, targetHandlerPlus);
		custSeq.addSwitchingCondition(targetPoint, initialAttProv, detector, false, true, 10, AngularDerivativesFilter.USE_RR, targetHandlerMinus);
		//custSeq.addSwitchingCondition(initialAttProv, targetPoint, detector.withHandler(new ContinueOnEvent()), true, false, 0.01, AngularDerivativesFilter.USE_RR, targetHandlerPlus);
		//custSeq.addSwitchingCondition(targetPoint, initialAttProv, detector.withHandler(new ContinueOnEvent()), false, true, 0.01, AngularDerivativesFilter.USE_RR, targetHandlerMinus);
	}
	
	AttitudesSequence getSeq() {
		return custSeq;
	}
}
