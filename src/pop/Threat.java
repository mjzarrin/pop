/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pop;

/**
 *
 * @author vahid
 */
public class Threat {
    Link link = new Link();
    Action action = new Action();
    State state = new State();
    
    
    
 
    public Threat(){
        
    }
    
     
    public Threat(Threat t){
        this.action = t.action;
        this.link = t.link;
        this.state = t.state;
    }
    
    public Object clone() {
        try {
            Thread p = (Thread) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
    

    
}
