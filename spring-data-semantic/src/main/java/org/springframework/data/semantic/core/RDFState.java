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
package org.springframework.data.semantic.core;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

public class RDFState {
	
	private Model currentStatements;
	
	private Model deleteStatements;
	
	public RDFState(){
		this.currentStatements = new LinkedHashModel();
		this.deleteStatements = new LinkedHashModel();
	}
	
	public RDFState(Model statements){
		this.currentStatements = statements;
		this.deleteStatements = new LinkedHashModel();
	}

	/**
	 * @return the currentStatements
	 */
	public Model getCurrentStatements() {
		return currentStatements;
	}

	/**
	 * @param currentStatements the currentStatements to set
	 */
	public void setCurrentStatements(Model currentStatements) {
		this.currentStatements = currentStatements;
	}

	/**
	 * @return the deleteStatements
	 */
	public Model getDeleteStatements() {
		return deleteStatements;
	}

	/**
	 * @param deleteStatements the deleteStatements to set
	 */
	public void setDeleteStatements(Model deleteStatements) {
		this.deleteStatements = deleteStatements;
	}
	
	public void addStatement(Statement st){
		this.currentStatements.add(st);
		this.deleteStatements.remove(st);
	}
	
	public void deleteStatement(Statement st){
		this.currentStatements.remove(st);
		this.deleteStatements.add(st);
	}
	
	public boolean isEmpty(){
		return (currentStatements == null || currentStatements.isEmpty()) && (deleteStatements == null || deleteStatements.isEmpty());
	}
	
	public void merge(RDFState state){
		this.deleteStatements.addAll(state.getDeleteStatements());
		this.currentStatements.addAll(state.getCurrentStatements());
	}

}
