package com.example.demo;

public class Utente {
	int id;
	String nome;
	String cognome;
	String mail;
	String password;
	boolean idCheck;
	public Utente(String nome, String cognome, String mail, String password) {
		super();
		this.nome = nome;
		this.cognome = cognome;
		this.mail = mail;
		this.password = password;
	}
	
	public int getId() {
		return id;
	}
	
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isIdCheck() {
		return idCheck;
	}
	public void setIdCheck(boolean idCheck) {
		this.idCheck = idCheck;
	}
	@Override
	public String toString() {
		return "utente [nome=" + nome + ", cognome=" + cognome + ", mail=" + mail + ", password=" + password
				+ ", idCheck=" + idCheck + "]";
	}
	

}