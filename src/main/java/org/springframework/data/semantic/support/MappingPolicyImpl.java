/**
 * Copyright (C) 2014 Ontotext AD (info@ontotext.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.semantic.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.semantic.mapping.MappingPolicy;

public class MappingPolicyImpl implements MappingPolicy{
	
	public static final MappingPolicy DEFAULT_POLICY = new MappingPolicyImpl();
	
	public static final MappingPolicy ALL_POLICY = new MappingPolicyImpl(Arrays.asList(Cascade.ALL));
	
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

	public Set<Cascade> getCascades() {
		return cascades;
	}

	@Override
	public MappingPolicy combineWith(MappingPolicy other) {
		if(this.cascades.contains(Cascade.ALL) && !other.getCascades().contains(Cascade.ALL)){
			return other;
		}
		else if(!this.cascades.contains(Cascade.ALL) && other.getCascades().contains(Cascade.ALL)){
			return this;
		}
		else{
			Set<Cascade> intersection = new HashSet<Cascade>();
			intersection.addAll(this.cascades);
			intersection.retainAll(other.getCascades());
			return new MappingPolicyImpl(intersection);
		}
	}

}
