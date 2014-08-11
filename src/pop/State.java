/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pop;

import java.util.ArrayList;



public class State {
    String predicate;
    int numberOfArg;
    ArrayList<Variable> arguments = new ArrayList<>();
    
    public State(){
        
    }
    public State(State s){
        this.arguments = s.arguments;
        this.numberOfArg = s.numberOfArg;
        this.predicate = s.predicate;
        
    }
}
