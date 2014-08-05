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
public class Plan implements Cloneable{

    ArrayList<Action> step; //instantiate action
    ArrayList<Ordering> ordering;
    ArrayList<Link> link; //causal links
    ArrayList<Thread> threat;
    ArrayList<Subgoal> subgoal;  //agenda
    Action start;
    Action end;
    
    public Object clone(){  
    try{  
        return super.clone();  
    }catch(Exception e){ 
        return null; 
    }
}
}
