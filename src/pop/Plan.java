/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pop;

import java.util.*;

/**
 *
 * @author vahid
 */
public class Plan implements Cloneable {

    ArrayList<Action> step = new ArrayList<>(); //instantiate action
//    ArrayList<Ordering> ordering = new ArrayList<>();
    Set<Ordering> ordering = new HashSet<>();
    ArrayList<Link> link = new ArrayList<>(); //causal links
    ArrayList<Threat> threat = new ArrayList<>();
    ArrayList<Subgoal> subgoal = new ArrayList<>();  //agenda
    Action start = new Action();
    Action end = new Action();

    public Plan(){
        
    }
//    public Plan(Plan p){
//        this.end = p.end;
//        this.link = p.link;
//        this.ordering = p.ordering;
//        this.start = p.start;
//        this.step = p.step;
//        this.subgoal = p.subgoal;
//        this.threat = p.threat;
//        
//    }
    
    public Object clone() {
        try {
            Plan p = (Plan) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
