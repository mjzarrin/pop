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
public class Subgoal {
    State state = new State();
    Action action =new Action();
    
    public Object clone() {
        try {
            Subgoal p = (Subgoal) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
