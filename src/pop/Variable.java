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
    Object value = new Object();
    ArrayList<Variable> constraints = new ArrayList<>();
    
    public Object clone() {
        try {
            Variable p = (Variable) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
    
}
