package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;

public class ActivityUnitDAO {
    
    public List<ActivityUnit> listAll() throws SQLException{
        Connection conn = null;
        //uso do PreparedStatement já que o mesmo faz um pre-otimização do comando sql, sendo considerado mais rápido 
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.prepareStatement("SELECT * FROM activityunit ORDER BY description");
            rs = stmt.executeQuery();

            List<ActivityUnit> list = new ArrayList<ActivityUnit>();

            while(rs.next()){
                    list.add(this.loadObject(rs));
            }

            return list;
        }finally{
            CloseConnection(conn,rs,stmt);
        }
    }
	
    public ActivityUnit findById(int id) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.prepareStatement("SELECT * FROM activityunit WHERE idActivityUnit=?");

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
	
    
    //separação do metodo save que possuia insert e update com intuito de facilitar a compreensão e encontrar possíveis erros
    public int insert(int idUser, ActivityUnit unit) throws SQLException{

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.prepareStatement("INSERT INTO activityunit(description, fillAmount, amountDescription) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, unit.getDescription());
            stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
            stmt.setString(3, unit.getAmountDescription());
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if(rs.next()){
                unit.setIdActivityUnit(rs.getInt(1));
            }

            new UpdateEvent(conn).registerInsert(idUser, unit);
            return unit.getIdActivityUnit();

        }finally{
            CloseConnection(conn,rs,stmt);
        }
    }
        
    
        
    public int update(int idUser, ActivityUnit unit) throws SQLException{

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = ConnectionDAO.getInstance().getConnection();
            stmt = conn.prepareStatement("UPDATE activityunit SET description=?, fillAmount=?, amountDescription=? WHERE idActivityUnit=?");

            stmt.setString(1, unit.getDescription());
            stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
            stmt.setString(3, unit.getAmountDescription());
            stmt.setInt(4, unit.getIdActivityUnit());
            stmt.executeUpdate();

            new UpdateEvent(conn).registerUpdate(idUser, unit);
            return unit.getIdActivityUnit();

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

    private ActivityUnit loadObject(ResultSet rs) throws SQLException{
            ActivityUnit unit = new ActivityUnit();

            unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
            unit.setDescription(rs.getString("Description"));
            unit.setFillAmount(rs.getInt("fillAmount") == 1);
            unit.setAmountDescription(rs.getString("amountDescription"));

            return unit;
    }

}
