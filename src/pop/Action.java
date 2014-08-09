/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pop;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author vahid
 */
public class Action {
    String type;
    ArrayList<Variable> arguments = new ArrayList<>();
    Goal preconditions = new Goal();
    ArrayList<State> adds =new ArrayList<>();
    ArrayList<State> deletes = new ArrayList<>();
    // undo binding - ordering - link 
    // int depth;
    // int num-link;
    
    public Object clone() {
        try {
            Action p = (Action) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
