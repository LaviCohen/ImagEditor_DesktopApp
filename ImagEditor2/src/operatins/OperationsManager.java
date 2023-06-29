package operatins;

import java.util.LinkedList;

public class OperationsManager {

	
	private static LinkedList<Operation> operations = new LinkedList<Operation>();
	
	public static void addOperation(Operation operation) {
		operations.add(operation);
	}
	
	public static void undo() {
		operations.removeLast().undo();
	}
}
