package org.colomoto.logicalmodel.tool.reverse;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.LogicalModelImpl;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDManagerFactory;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.junit.Test;

/**
 * 
 * @author Pedro T. Monteiro
 */
public class TestReversion extends TestCase {

	private static LogicalModel loadSimpleModelA() {
		List<NodeInfo> variables = new ArrayList<NodeInfo>();
		byte max = 1;
		variables.add(new NodeInfo("A", max));
		variables.add(new NodeInfo("B", max));

		MDDManager ddmanager = MDDManagerFactory.getManager(variables, 5);
		MDDVariable[] ddVariables = ddmanager.getAllVariables();
		int[] functions = new int[variables.size()];

		int vA = ddVariables[0].getNode(0, 1);
		int vB = ddVariables[1].getNode(0, 1);
		functions[0] = vA;
		functions[1] = vB;

		return new LogicalModelImpl(ddmanager, variables, functions,
				new ArrayList<NodeInfo>(), new int[0]);
	}

	private static LogicalModel loadSimpleModelB() {
		List<NodeInfo> variables = new ArrayList<NodeInfo>();
		byte max = 1;
		variables.add(new NodeInfo("A", max));
		variables.add(new NodeInfo("B", max));
		variables.add(new NodeInfo("C", max));

		MDDManager ddmanager = MDDManagerFactory.getManager(variables, 5);
		MDDVariable[] ddVariables = ddmanager.getAllVariables();
		int[] functions = new int[variables.size()];

		int tA = ddVariables[0].getNode(0, 1);
		int fA = ddVariables[0].getNode(1, 0);
		int tB = ddVariables[1].getNode(0, 1);
		int fB = ddVariables[1].getNode(1, 0);
		int tC = ddVariables[2].getNode(0, 1);
		int fC = ddVariables[2].getNode(1, 0);
		int tBtC = MDDBaseOperators.AND.combine(ddmanager, tB, tC);

		functions[0] = MDDBaseOperators.OR.combine(ddmanager,
				MDDBaseOperators.AND.combine(ddmanager, fB, fC), tBtC);
		functions[1] = MDDBaseOperators.OR.combine(ddmanager,
				MDDBaseOperators.OR.combine(ddmanager, fC, fA),
				MDDBaseOperators.AND.combine(ddmanager, tA, tBtC));
		functions[2] = fA;

		return new LogicalModelImpl(ddmanager, variables, functions,
				new ArrayList<NodeInfo>(), new int[0]);
	}

	private void testDoubleReversion(LogicalModel mOrig) {
		ModelReverser modelRev1 = new ModelReverser(mOrig);
		modelRev1.reverse();
		ModelReverser modelRev2 = new ModelReverser(modelRev1.getModel());
		modelRev2.reverse();
		LogicalModel mRev = modelRev2.getModel();

		int[] fOrig = mOrig.getLogicalFunctions();
		int[] fRev = mRev.getLogicalFunctions();
		for (int i = 0; i < fOrig.length; i++) {
			assertEquals(fOrig[i], fRev[i]);
		}
	}

	@Test
	public void testSimpleModelA() {
		LogicalModel mOrig = TestReversion.loadSimpleModelA();
		this.testDoubleReversion(mOrig);
	}

	@Test
	public void testSimpleModelB() {
		LogicalModel mOrig = TestReversion.loadSimpleModelB();
		this.testDoubleReversion(mOrig);
	}
}
