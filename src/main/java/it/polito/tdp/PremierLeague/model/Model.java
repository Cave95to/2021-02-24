package it.polito.tdp.PremierLeague.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	
	private Graph<Player, DefaultWeightedEdge> grafo;
	
	private Map<Integer, Player> idMap;
	
	public Model() {
		
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<>();
		this.dao.listAllPlayers(this.idMap);
		
	}
	
	public void creaGrafo(Match m) {
		
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo vertici
		
		Graphs.addAllVertices(this.grafo, this.dao.getPlayersByMatch(m, this.idMap));
		
		// aggiungo archi
		for(Adiacenza a : this.dao.getAdiacenze(m, idMap)) {
			if(this.grafo.containsVertex(a.getP1()) && this.grafo.containsVertex(a.getP2()))
				if(!this.grafo.containsEdge(a.getP1(), a.getP2()) && !this.grafo.containsEdge(a.getP2(), a.getP1()))
					Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
		}
	}
	
	public int getNVertici() {
		if(this.grafo!=null)
			return this.grafo.vertexSet().size();
		return 0;
	}
	
	public int getNArchi() {
		if(this.grafo!=null)
			return this.grafo.edgeSet().size();
		return 0;
	}
	
	public List<Match> getMatch() {
		List<Match> list = this.dao.listAllMatches();
		Collections.sort(list);
		return list;
	}
	
	public GiocatoreMigliore getGiocatoreMigliore() {
		
		if(this.grafo == null)
			return null;
		
		double deltaMax = -1000;
		Player pMax = null;
		
		for(Player p : this.grafo.vertexSet()) {
			
			double delta = 0.0;
			
			for (DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(p))
				delta = delta + this.grafo.getEdgeWeight(e);
			
			for (DefaultWeightedEdge ed : this.grafo.incomingEdgesOf(p))
				delta = delta - this.grafo.getEdgeWeight(ed);
			
			if(delta>deltaMax) {
				pMax = p;
				deltaMax = delta;
			}	
		}
		
		return new GiocatoreMigliore(pMax, deltaMax);
	}
}
