package org.springframework.data.semantic.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.semantic.mapping.MappingPolicy;

public class MappingPolicyImpl implements MappingPolicy{
	
	public static final MappingPolicy DEFAULT_POLICY = new MappingPolicyImpl();
	
	private Set<Cascade> cascades;
	
	public MappingPolicyImpl(){
		this.cascades = new HashSet<Cascade>();
	}
	
	public MappingPolicyImpl(Collection<Cascade> cascades) {
		this.cascades = new HashSet<Cascade>();
		this.cascades.addAll(cascades);
	}
	
	
	
	@Override
	public boolean useDirty() {
		//TODO
		return true;
	}

	@Override
	public boolean shouldCascade(Cascade cascade) {
		return this.cascades.contains(Cascade.ALL) || this.cascades.contains(cascade);
	}

	

	@Override
	public MappingPolicy combineWith(MappingPolicy other) {
		//TODO
		return this;
	}

}
