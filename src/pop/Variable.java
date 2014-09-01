/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pop;

import java.util.ArrayList;

/**
 *
 * @author vahid
 */
public class Variable {
    String name = new String();
//    Object value = new Object();
        String value = null;

    ArrayList<Variable> constraints = new ArrayList<>();

    public Variable(Variable v) {
        this.name = v.name;
        this.value = v.value;
        this.constraints = v.constraints;
    }

    Variable() {
        value = null;
    }
    
    
    public Object clone() {
        try {
            Variable p = (Variable) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
    
    public Variable copy(Variable v){
        Variable w = new Variable();
        w.name = v.name;
        w.value = v.value;
        w.constraints = (ArrayList<Variable>) v.constraints.clone();
        return w;
    }
    
}
