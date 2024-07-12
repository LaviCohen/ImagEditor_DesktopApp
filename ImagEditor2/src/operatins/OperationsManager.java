package operatins;

import java.util.LinkedList;

import install.Preferences;

public class OperationsManager {

	
	private static LinkedList<Operation> operations = new LinkedList<Operation>();
	
	private static int indexFromLastOperation;
	
	
	public static void operate(Operation operation) {
		operation.redo();
		addOperation(operation);
	}
	
	public static void addOperation(Operation operation) {
		System.out.println("Adding " + operation);
		for (int i = 0; i < indexFromLastOperation; i++) {
			operations.removeLast();
		}
		indexFromLastOperation = 0;
		operations.add(operation);
		if (Preferences.numOfBackOperations != -1 && 
				Preferences.numOfBackOperations < operations.size()) {
			operations.removeFirst();
		}
	}
	
	public static void undo() {
		if (!operations.isEmpty()) {
			System.out.println("Undoing " + operations.get(operations.size() - indexFromLastOperation - 1));
			Operation operation = operations.get(operations.size() - indexFromLastOperation - 1);
			operation.undo();
			if (operation instanceof ChangesOperation) {
				((ChangesOperation)operation).getShape().invalidate();
			}
			indexFromLastOperation++;
		}
	}
	
	public static void redo() {
		if (indexFromLastOperation > 0) {
			System.out.println("Redoing " + operations.get(operations.size() - indexFromLastOperation));
			operations.get(operations.size() - indexFromLastOperation).redo();
			indexFromLastOperation--;
		}
	}
}
