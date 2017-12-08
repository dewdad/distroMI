package graph;

import java.util.List;

public class ValueElementDiscretization {
	
	
	public enum DiscretizationMethod {
		VALUE_VARIATION,
		LOGICAL_OP,
		CATEGORIES
	}

	
	public ValueElementDiscretization(DiscretizationMethod aMethod) {
		// TODO Auto-generated constructor stub
	}

	public static ValueElementDiscretization getDefault() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static DiscretizationMethod[] getDiscretizationMethodsName(){
		return DiscretizationMethod.values();
	}

	public static ValueElementDiscretization getValueVariationMethod(String text) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
