package Enums;

public enum LayerType {
	L1("Physical"),
	L2("Link"),
	L3("Network"),
	L4("Transport"),
	L5("Application");
	private String Name;
	LayerType(String Name) {
		this.Name = Name;
	}
	public String toString() {
		return this.Name;
	}
}
