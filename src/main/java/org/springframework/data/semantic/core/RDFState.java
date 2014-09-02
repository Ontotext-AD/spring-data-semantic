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
		if((currentStatements == null || currentStatements.isEmpty()) && (deleteStatements == null || deleteStatements.isEmpty())){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void merge(RDFState state){
		this.deleteStatements.addAll(state.getDeleteStatements());
		this.currentStatements.addAll(state.getCurrentStatements());
	}

}
