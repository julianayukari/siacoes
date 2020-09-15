package br.edu.utfpr.dv.siacoes.dao;

import java.sql.SQLException;
import java.util.List;


public abstract class TemplateMethod<T> {
	
	public abstract List<T> listAll() throws SQLException;
	public abstract List<T> listAll(boolean onlyActive) throws SQLException;
	public abstract T findById(int id) throws SQLException;
	public abstract int save(int idUser, T unit) throws SQLException;
}