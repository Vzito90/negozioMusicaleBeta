package com.example.demo;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;

import jakarta.mail.MessagingException;

@Controller
public class MyController {
	
	ArrayList<Strumenti> listaV= new ArrayList<>();
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	JdbcTemp s1;
	
	
	
	@Value("${stripe.currency}")
	private String currency;
	
	@Value("${stripe.secretKey}")
    private String stripeSecretKey;
	
	@GetMapping("/gestore")
	public String getGestore() {
		return "gestore";
	}
	
	@ResponseBody
	@PostMapping("/inserisci")
	public String setGestore(@RequestParam("nome") String nome, @RequestParam("marca") String marca, 
			@RequestParam("tipologia") String tipologia,
			@RequestParam("prezzo") double prezzo, @RequestParam("url") String url) {
		s1.insertStrumento(nome, marca, tipologia, prezzo, url, 50, 0);
		return nome + " aggiunto con successo";
	}
	
	@ResponseBody
	@PostMapping("/aggiornaPezzi")
	public String setPezzi(@RequestParam("nome") String nome, @RequestParam("pezzi") int pezzi) {
		s1.updatePezzi(nome, pezzi);
		return nome + " quantità aggiornata";
	}
	
	@ResponseBody
	@PostMapping("/aggiornaPrezzo")
	public String setPrezzo(@RequestParam("nome") String nome, @RequestParam("prezzo") double prezzo) {
		s1.updatePrezzo(nome, prezzo);
		return nome + " prezzo aggiornato";
	}
	
	@GetMapping("/store")
	public String store(@RequestParam(value = "instrumentType", required = false) String instrumentType, Model model) {
	    ArrayList<Strumenti> listaS = s1.getLista();

	    // Ottieni tutte le tipologie uniche
	    Set<String> tipologieUniche = listaS.stream()
	                                        .map(Strumenti::getTipologia)  // Supponendo che la classe Strumenti abbia il metodo getTipologia()
	                                        .collect(Collectors.toSet());

	    model.addAttribute("listaS", listaS);
	    model.addAttribute("tipologieUniche", tipologieUniche);

	    return "store";
	}

	
	@GetMapping("/select")
	public String select(@RequestParam("instrumentType") String tipologia, Model model) {
		ArrayList<Strumenti> listaS = s1.getStrumentiByTipologia(tipologia);
		model.addAttribute("listaS ",listaS);
		model.addAttribute("instrumentType", tipologia);
		return "store";
		
	}
	
	
	
	
	@ResponseBody
	@PostMapping("/aggiornaUrl")
	public String setUrl(@RequestParam("nome") String nome, @RequestParam("url") String url) {
		s1.updateUrl(nome, url);
		return nome + " url aggiornato";
		
	}
	
	@PostMapping("/check")
	public String loginCheck(@RequestParam("mail") String mail, @RequestParam("password") String password, Model model) {
		if(s1.checkPass(mail, password)) {
			ArrayList<Strumenti> listaS = s1.getLista();
			model.addAttribute("listaS", listaS);
			return "store";
		} else {
			return "login";
		}
		
	}
	@ResponseBody
	@PostMapping("/registrazione")
	public String registrati(@RequestParam("nome") String nome, @RequestParam("cognome") String cognome,
			@RequestParam("mail") String mail, @RequestParam("password") String password, Model model) {
		s1.insertUtente(nome, cognome, mail, password);
		return nome + " aggiunto con successo";
	}
	

	@GetMapping("/submit")
	public String submit() {
		return "registrazione";
	}
	
	@GetMapping("/login")
	public String login() {
	
			return "login";
		
		
	}
	
	@PostMapping("/buy")
	public String recap(@RequestParam("selected") ArrayList<Integer> selected, @RequestParam("mail") String mail, 
			@RequestParam("token") String token, Model model) throws MessagingException {
		int somma=0;
		ArrayList<Strumenti> lista = s1.getLista();
		ArrayList<Strumenti> listaAc = new ArrayList<>();
		ArrayList<String> listaU = new ArrayList<>();
		
		String oggetto = "hai acquistato: ";
		
		for(int i=0; i<lista.size(); i++) {
			
			if(selected.get(i)>0) {
				somma += selected.get(i)*lista.get(i).getPrezzo();
				Strumenti sm1= new Strumenti(lista.get(i).getNome(), lista.get(i).getMarca(), lista.get(i).getTipologia(), lista.get(i).getPrezzo(),
						lista.get(i).getUrl(),  selected.get(i));
				 listaAc.add(sm1);
				 s1.change( lista.get(i).getNome(), selected.get(i));
				 listaU.add(lista.get(i).getUrl());
				 
					 oggetto += lista.get(i).getNome() + ", ";
				
				 
			}
		}
		oggetto += " La somma totale da pagare è: " + somma + " euro";
		emailService.sendEmailWithImage(mail, "Mail da Strumenti Musicali", oggetto, listaU);
		model.addAttribute("lista", listaAc);
		model.addAttribute("somma", somma);
		
		 try {
	            // Imposta la chiave segreta di Stripe
	            Stripe.apiKey = stripeSecretKey;

	            // Crea una richiesta di pagamento
	            ChargeCreateParams params = ChargeCreateParams.builder()
	                    .setAmount((long)  somma * 100) // Importo in centesimi
	                    .setCurrency("eur")
	                    .setSource(token) // Usa il token di test fornito
	                    .build();
	            
	            
	            Charge charge = Charge.create(params);
	            
	            System.out.println("Pagamento completato: " + charge.toJson());
	            
	            System.out.println("Visualizza ricevuta: " +  charge.getReceiptUrl());
	            
	         //risult = "Pagamento andato a buon fine";
	         
	         model.addAttribute("urlRicevuta",charge.getReceiptUrl());
      //  ok = true;
	            
	            
	        } catch (StripeException e) {
	        	
	        	//risult = "Pagamento non riuscito, riprova";
	        	//ok = false;
	        	 
	        }
		
		return "recap";
	}
	
	

}
