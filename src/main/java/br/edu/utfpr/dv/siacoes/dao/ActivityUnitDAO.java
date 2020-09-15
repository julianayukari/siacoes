package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;

public class ActivityUnitDAO extends TemplateMethod<ActivityUnit>{
    public List<ActivityUnit> listAll() throws SQLException{
  
        try(Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt =  conn.prepareStatement("SELECT * FROM activityunit ORDER BY description");
                ResultSet rs = stmt.executeQuery(); ){
         
            List<ActivityUnit> list = new ArrayList<ActivityUnit>();

            while(rs.next()){
                    list.add(this.loadObject(rs));
            }
            
            return list;
        }
    }
	
    public ActivityUnit findById(int id) throws SQLException{
       
        try( Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activityunit WHERE idActivityUnit=?");){
           
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
	
    //uso do metodo save para chamar as funções de insert e update, ao retira-la, o código que usa o método deixa de funcionar, já que o metodo utilizado/chamado é o save 
    public int save(int idUser, ActivityUnit unit) throws SQLException{
	boolean insert = (unit.getIdActivityUnit() == 0);
        if(insert){
            return insert(idUser, unit);
	}else{
            return update(idUser, unit);
        }
    }
    
    //separação do metodo save que possuia insert e update com intuito de facilitar a compreensão e encontrar possíveis erros
    public int insert(int idUser, ActivityUnit unit) throws SQLException{

        try(Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO activityunit(description, fillAmount, amountDescription) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);){
            
            stmt.setString(1, unit.getDescription());
            stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
            stmt.setString(3, unit.getAmountDescription());
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys(); ){
                if(rs.next()){
                    unit.setIdActivityUnit(rs.getInt(1));
                }
   
                new UpdateEvent(conn).registerInsert(idUser, unit);
                return unit.getIdActivityUnit();
            }
        }
    }
        
    public int update(int idUser, ActivityUnit unit) throws SQLException{

        try( Connection conn =  ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement("UPDATE activityunit SET description=?, fillAmount=?, amountDescription=? WHERE idActivityUnit=?");){
           
            stmt.setString(1, unit.getDescription());
            stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
            stmt.setString(3, unit.getAmountDescription());
            stmt.setInt(4, unit.getIdActivityUnit());
            stmt.executeUpdate();

            new UpdateEvent(conn).registerUpdate(idUser, unit);
            return unit.getIdActivityUnit();
        }
    }   
        
    private ActivityUnit loadObject(ResultSet rs) throws SQLException{
            ActivityUnit unit = new ActivityUnit();

            unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
            unit.setDescription(rs.getString("Description"));
            unit.setFillAmount(rs.getInt("fillAmount") == 1);
            unit.setAmountDescription(rs.getString("amountDescription"));

            return unit;
    }

	@Override
	public List<ActivityUnit> listAll(boolean onlyActive) throws SQLException {
		throw new NotImplementedException("method not overridden");
	}
}
