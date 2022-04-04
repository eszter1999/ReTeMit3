package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.base.types.Event;
import org.yakindu.base.types.Property;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.InterfaceScope;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
	
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
//kezdés+SM setup
		System.out.println(
				"public class RunStatechart {\r\n\n" +
				"public static void main(String[] args) throws IOException {\r\n" + 
				"		ExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"		s.setTimer(new TimerService());\r\n" + 
				"		RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" + 
				"		s.init();\r\n" + 
				"		s.enter();");
		//parancsbeolvas
		System.out.println(
				"		String[] line;\r\n" + 
				"		Scanner scanner = new Scanner(System.in);\r\n" + 
				"		while((line = scanner.nextLine().split(\" \")) != null) {\r\n" + 
				"			String cmd = line[0].toLowerCase();\r\n" + 
				"			switch(cmd) {");
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if (content instanceof InterfaceScope) {
				Iterator<Event> iter = ((InterfaceScope) content).getEvents().iterator();
				while (iter.hasNext()) {
					String name = iter.next().getName();
					System.out.println(
							"			case \""+name.toLowerCase()+"\":\r\n" + 
							"				s.raise"+name.substring(0,1).toUpperCase()+name.substring(1)+"();\r\n" + 
							"				break;");
				}
			}
		}
		System.out.println(
				"			case \"exit\":\r\n" + 
				"				scanner.close();\r\n" + 
				"				System.exit(0);\r\n" + 
				"				break;\r\n" + 
				"			default:\r\n" + 
				"				break;\r\n" + 
				"			}");
		System.out.println(
				"			print(s);\r\n" + 
				"		}\r\n" + 
				"		\r\n" + 
				"	});");
		System.out.println();
		
		System.out.println(
				"	public static void print(IExampleStatemachine s) {");
		iterator = s.eAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if (content instanceof InterfaceScope) {
				Iterator<Property> varIter = ((InterfaceScope) content).getVariables().iterator();
				while (varIter.hasNext()) {
					String temp = varIter.next().getName();
					System.out.println(
					"		System.out.println(\""+temp.toUpperCase().charAt(0)+" = \" + s.getSCInterface().get"+temp+"());");
				}
			}
		}
		System.out.println("}");
		
		
//		while (iterator.hasNext()) {
//			EObject content = iterator.next();
//			if(content instanceof State) {
//				State state = (State) content;
//				Iterator<Transition> transitIter = state.getIncomingTransitions().iterator() ;
//				while (transitIter.hasNext())
//				{
//					Transition transit = transitIter.next();
//					System.out.println(transit.getSource().getName() + "->" + transit.getTarget().getName());
//				}
//			}
//			if (content instanceof InterfaceScope) {
//				Iterator<Property> varIter = ((InterfaceScope) content).getVariables().iterator();
//				while (varIter.hasNext()) {
//					Property var = varIter.next();
//					System.out.println(var.getName());
//				}
//			}
//		}
		
		
//		// Reading model
//		Statechart s = (Statechart) root;
//		TreeIterator<EObject> iterator = s.eAllContents();
//		int numOfSates = 0;
//		while (iterator.hasNext()) {
//			EObject content = iterator.next();
//			if(content instanceof Transition) {
//				System.out.println(((Transition) content).getSource().getName() + "->" + ((Transition) content).getTarget().getName());
//			}
//			if(content instanceof State) {
//				numOfSates++;
//				State state = (State) content;
//				if(state.getOutgoingTransitions().isEmpty())
//					System.out.println(state.getName());
//				if(state.getName().isEmpty())
//					System.out.println("New name: State" + numOfSates);
//			}
//		}
		
		
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	
	public static void print(IExampleStatemachine s) {
		System.out.println("W = " + s.getSCInterface().getWhiteTime());
		System.out.println("B = " + s.getSCInterface().getBlackTime());
		}
}
