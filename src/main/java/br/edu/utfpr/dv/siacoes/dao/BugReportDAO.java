package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;

public class BugReportDAO {
	
	public BugReport findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT bugreport.*, \"user\".name " + 
				"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
				"WHERE idBugReport = ?");
		
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			CloseConnection(conn,rs,stmt);
		}
	}
	
	public List<BugReport> listAll() throws SQLException{
		Connection conn = null;
                //uso do PreparedStatement já que o mesmo faz um pre-otimização do comando sql, sendo considerado mais rápido
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.PrepareStatement("SELECT bugreport.*, \"user\".name " +
					"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
					"ORDER BY status, reportdate");
			
			rs = stmt.executeQuery();
			List<BugReport> list = new ArrayList<BugReport>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			CloseConnection(conn,rs,stmt);
		}
	}
        
        //uso do metodo save para chamar as funções de insert e update, ao retira-la, o código que usa o método deixa de funcionar, já que o metodo utilizado/chamado é o save 
        public int save(int idUser, ActivityUnit unit) throws SQLException{
            boolean insert = (unit.getIdActivityUnit() == 0);
                if(insert){
                    return insert(idUser, unit);
                }else{
                    return update(idUser, unit)
                }
        }
        
	//separação do metodo save que possuia insert e update com intuito de facilitar a compreensão e encontrar possíveis erros
	public int insert(BugReport bug) throws SQLException{
		boolean insert = (bug.getIdBugReport() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("INSERT INTO bugreport(idUser, module, title, description, reportDate, type, status, statusDate, statusDescription) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			stmt.setInt(1, bug.getUser().getIdUser());
			stmt.setInt(2, bug.getModule().getValue());
			stmt.setString(3, bug.getTitle());
			stmt.setString(4, bug.getDescription());
			stmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
			stmt.setInt(6, bug.getType().getValue());
			stmt.setInt(7, bug.getStatus().getValue());
			if(bug.getStatus() == BugStatus.REPORTED){
				stmt.setNull(8, Types.DATE);
			}else{
				stmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
			}
			stmt.setString(9, bug.getStatusDescription());
			stmt.execute();
			rs = stmt.getGeneratedKeys();
			if(rs.next()){
                            bug.setIdBugReport(rs.getInt(1));
			}
			return bug.getIdBugReport();
                        
		}finally{
			CloseConnection(conn,rs,stmt);
		}
	}
        
        public int update(BugReport bug) throws SQLException{
		boolean insert = (bug.getIdBugReport() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
                        stmt = conn.prepareStatement("UPDATE bugreport SET idUser=?, module=?, title=?, description=?, reportDate=?, type=?, status=?, statusDate=?, statusDescription=? WHERE idBugReport=?");
                        stmt.setInt(1, bug.getUser().getIdUser());
			stmt.setInt(2, bug.getModule().getValue());
			stmt.setString(3, bug.getTitle());
			stmt.setString(4, bug.getDescription());
			stmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
			stmt.setInt(6, bug.getType().getValue());
			stmt.setInt(7, bug.getStatus().getValue());
			if(bug.getStatus() == BugStatus.REPORTED){
				stmt.setNull(8, Types.DATE);
			}else{
				stmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
			}
			stmt.setString(9, bug.getStatusDescription());
                        stmt.setInt(10, bug.getIdBugReport());
                        stmt.execute();
                        return bug.getIdBugReport();
                }finally{
			CloseConnection(conn,rs,stmt);
		}
        }
                        
        //criaçao do metodo CloseConnection com com intuito de reutilização do código, já que o mesmo código era usado outros métodos.
        public CloseConnection(Connection con, ResultSet rs, PreparedStatement stmt){
            if((rs != null) && !rs.isClosed()){
                rs.close();
            }
            if((stmt != null) && !stmt.isClosed()){
                stmt.close();
            }
            if((conn != null) && !conn.isClosed()){
                conn.close();
            }                  
        }
	
	private BugReport loadObject(ResultSet rs) throws SQLException{
		BugReport bug = new BugReport();
		
		bug.setIdBugReport(rs.getInt("idBugReport"));
		bug.setUser(new User());
		bug.getUser().setIdUser(rs.getInt("idUser"));
		bug.getUser().setName(rs.getString("name"));
		bug.setModule(Module.SystemModule.valueOf(rs.getInt("module")));
		bug.setTitle(rs.getString("title"));
		bug.setDescription(rs.getString("description"));
		bug.setReportDate(rs.getDate("reportDate"));
		bug.setType(BugReport.BugType.valueOf(rs.getInt("type")));
		bug.setStatus(BugReport.BugStatus.valueOf(rs.getInt("status")));
		bug.setStatusDate(rs.getDate("statusDate"));
		bug.setStatusDescription(rs.getString("statusDescription"));
		
		return bug;
	}

}
