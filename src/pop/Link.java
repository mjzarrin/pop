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
public class Link {
    Action provider = new Action();
    Action receiver = new Action();
    State condition = new State();
    
    public Link(){
        
    }
    
    public Link(Link l){
        this.condition = l.condition;
        this.provider = l.provider;
        this.receiver = l.receiver;
        
    }
    
    public Object clone() {
        try {
            Link p = (Link) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
