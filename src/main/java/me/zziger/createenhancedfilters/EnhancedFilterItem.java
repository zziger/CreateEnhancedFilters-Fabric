package me.zziger.createenhancedfilters;

public interface EnhancedFilterItem {
	public enum EnhancedType {
		NONE,
		DURABILITY
	}

	default void setEnhancedType(EnhancedType type) {}
	default EnhancedType getEnhancedType() { return EnhancedType.NONE; }
}
