package com.example.demo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
@Component
public class JdbcTemp {
	
	
	private JdbcTemplate jdbcTemplateObject;
	
	@Autowired
	public void setJdbcTemplateObject(JdbcTemplate jdbcTemplateObject) {
		this.jdbcTemplateObject= jdbcTemplateObject;
	}
	
	public int insertUtente(String nome, String cognome, String mail, String password) {
		String query = "INSERT INTO utente (nome, cognome, mail, password) VALUES (?, ?, ?, ?)";
		return jdbcTemplateObject.update(query, nome, cognome, mail, password);
	}
	
	public boolean checkPass(String mail, String password) {
	    String query = "SELECT * FROM utente WHERE mail = ?";  // Cerca per mail, senza includere la password nella query
	    try {
	        // Esegui la query per ottenere l'utente con la mail specificata
	        SqlRowSet rs = jdbcTemplateObject.queryForRowSet(query, mail);

	        if (rs.next()) {
	            // Estrai la password salvata nel database
	            String storedPassword = rs.getString("password");

	            // Confronta la password inserita con quella salvata nel database (in chiaro o con hash)
	            if (password.equals(storedPassword)) {
	                return true;  // Login riuscito
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;  // Login fallito
	}
	
	public int insertStrumento(String nome, String marca, String tipologia, double prezzo, String url, int pezzi, int pezziV) {
		String query = "INSERT INTO strumentimusicali (nome, marca, tipologia, prezzo, url, pezzi, pezziV) VALUES (?, ?, ?, ?, ?, ?, ?)";
		return jdbcTemplateObject.update(query, nome, marca, tipologia, prezzo, url, pezzi, pezziV);
	}
	
	public int updatePezzi(String nome, int pezzi) {
		String query = "UPDATE strumentimusicali SET pezzi = pezzi + (?) WHERE nome = (?)";
		return jdbcTemplateObject.update(query, pezzi, nome);
	}
	
	public int updatePrezzo(String nome, double prezzo) {
		String query = "UPDATE strumentimusicali SET prezzo = (?) WHERE nome=(?)";
		return jdbcTemplateObject.update(query, prezzo, nome);
	}
	
	public int delete(String nome) {
        String query = "DELETE FROM strumentimusicali WHERE nome = ?";
        return jdbcTemplateObject.update(query, nome);
    }
	

	 public ArrayList<Strumenti> getLista() {
	        String query = "SELECT * FROM strumentimusicali";

	        return jdbcTemplateObject.query(query, new ResultSetExtractor<ArrayList<Strumenti>>() {
	        	@Override
	            public ArrayList<Strumenti> extractData(ResultSet rs) throws SQLException, DataAccessException {
	                ArrayList<Strumenti> listaS = new ArrayList<>();

	               
	                while (rs.next()) {
	                	Strumenti s1 = new Strumenti();
	                    s1.setNome(rs.getString("nome"));
	                    s1.setMarca(rs.getString("marca"));
	                    s1.setTipologia(rs.getString("tipologia"));
	                    s1.setPrezzo(rs.getDouble("prezzo"));
	                    s1.setUrl(rs.getString("url"));
	                	
	 
	                    listaS.add(s1);
	                }

	                return listaS;
	            }
	        });
	    }
	 


	 public ArrayList<Strumenti> getStrumentiByTipologia(String tipologia) {
		    String query = "SELECT * FROM strumentimusicali WHERE tipologia = ?";

		    JdbcTemplate jdbcTemplate = null;
			// Eseguiamo la query utilizzando JdbcTemplate, passando 'tipologia' come parametro
		    return (ArrayList<Strumenti>) jdbcTemplate.query(
		        query, 
		        new Object[]{tipologia},  // Passiamo 'tipologia' come parametro alla query
		        new RowMapper<Strumenti>() {
		            @Override
		            public Strumenti mapRow(ResultSet rs, int rowNum) throws SQLException {
		                // Mappiamo ogni riga del ResultSet in un oggetto Strumenti
		                Strumenti strumento = new Strumenti();
		                
		                strumento.setNome(rs.getString("nome"));  // Supponiamo che ci sia un campo 'nome'
		                strumento.setTipologia(rs.getString("tipologia"));  // Supponiamo che ci sia un campo 'tipologia'
		                // Aggiungi altri campi a seconda della struttura della tua tabella
		                return strumento;
		            }
		        });
		}

	 
	 public int updateUrl(String nome, String url) {
		 String query = "UPDATE strumentimusicali SET url = ? WHERE nome= ?";
		 return jdbcTemplateObject.update(query, url, nome);
	 }
	 
	 public int change(String nome, int pezzi) {
	    	String query= "UPDATE strumentimusicali SET pezzi = pezzi - (?) WHERE nome = (?)";
	    	jdbcTemplateObject.update(query, pezzi, nome);
	    	String query1 = "UPDATE strumenti SET pezziV = pezziV+ (?) WHERE nome=(?)";
	    	return jdbcTemplateObject.update(query1, pezzi, nome);
	    }
	    
}
