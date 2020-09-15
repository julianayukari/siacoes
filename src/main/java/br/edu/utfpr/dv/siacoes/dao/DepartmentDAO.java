package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentDAO extends TemplateMethod<Department>{

    public Department findById(int id) throws SQLException{
        try(Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT department.*, campus.name AS campusName " +
                            "FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " + 
                            "WHERE idDepartment = ?");){
                
                 stmt.setInt(1, id);
                 try(ResultSet rs = stmt.executeQuery();){
                     if(rs.next()){
                         return this.loadObject(rs);
                     }else{
                            return null;
                    }
                 }
        }
    }
	
	public List<Department> listAll(boolean onlyActive) throws SQLException{
			
            try(Connection conn= ConnectionDAO.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement("SELECT department.*, campus.name AS campusName " +
			"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " + 
                            (onlyActive ? " WHERE department.active=1" : "") + " ORDER BY department.name");
                    ResultSet rs = stmt.executeQuery();){
                
                        List<Department> list = new ArrayList<Department>();
                        while(rs.next()){
                                list.add(this.loadObject(rs));
                        }

                        return list;
                    }			
	}
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
            try(Connection conn = ConnectionDAO.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement("SELECT department.*, campus.name AS campusName " +
                            "FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
                            "WHERE department.idCampus=" + String.valueOf(idCampus) + (onlyActive ? " AND department.active=1" : "") + " ORDER BY department.name");
                    ResultSet rs = stmt.executeQuery();){
                    List<Department> list = new ArrayList<Department>();
			
                    while(rs.next()){
                            list.add(this.loadObject(rs));
                    }
			
			return list;
            }   
	}
        
        //uso do metodo save para chamar as funções de insert e update, ao retira-la, o código que usa o método deixa de funcionar, já que o metodo utilizado/chamado é o save 
        public int save(int idUser, Department dept) throws SQLException{
            boolean insert = (dept.getIdDepartment() == 0);
            if(insert){
                return insert(idUser, dept);
            }else{
                return update(idUser, dept);
            }
        }
	
        //separação do metodo save que possuia insert e update com intuito de facilitar a compreensão e encontrar possíveis erros
	public int insert(int idUser, Department department) throws SQLException{
            try(Connection conn = ConnectionDAO.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);){
                
                    stmt.setInt(1, department.getCampus().getIdCampus());
                    stmt.setString(2, department.getName());
                    if(department.getLogo() == null){
                            stmt.setNull(3, Types.BINARY);
                    }else{
                            stmt.setBytes(3, department.getLogo());	
                    }
                    stmt.setInt(4, department.isActive() ? 1 : 0);
                    stmt.setString(5, department.getSite());
                    stmt.setString(6, department.getFullName());
                    stmt.setString(7, department.getInitials());	
                    stmt.execute();

                    try(ResultSet rs = stmt.getGeneratedKeys();){
                        if(rs.next()){
                            department.setIdDepartment(rs.getInt(1));
                        }
                    }
                    
                    new UpdateEvent(conn).registerInsert(idUser, department);
                    return department.getIdDepartment();

            }
	}
        
        public int update(int idUser, Department department) throws SQLException{
            
            try(Connection conn = ConnectionDAO.getInstance().getConnection();
                    PreparedStatement stmt= conn.prepareStatement("UPDATE department SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? WHERE idDepartment=?");){

                    stmt .setInt(1, department.getCampus().getIdCampus());
                    stmt.setString(2, department.getName());
                    if(department.getLogo() == null){
                            stmt.setNull(3, Types.BINARY);
                    }else{
                            stmt.setBytes(3, department.getLogo());	
                    }
                    stmt.setInt(4, department.isActive() ? 1 : 0);
                    stmt.setString(5, department.getSite());
                    stmt.setString(6, department.getFullName());
                    stmt.setString(7, department.getInitials());
                    stmt.setInt(8, department.getIdDepartment());
                    stmt.execute();

                    new UpdateEvent(conn).registerUpdate(idUser, department);
                    return department.getIdDepartment();

            }	
        }
        
	private Department loadObject(ResultSet rs) throws SQLException{
		Department department = new Department();
		
		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setInitials(rs.getString("initials"));
		
		return department;
	}

	@Override
	public List<Department> listAll() throws SQLException {
		throw new NotImplementedException("method not overridden");
	}

		
}
