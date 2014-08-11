

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
public class Goal {
    ArrayList<Subgoal> subgoals = new ArrayList<>();

    public Goal() {
    }
    
    public Goal(Goal g) {
        this.subgoals = g.subgoals;
    }
     
    
    public Object clone() {
        try {
            Goal p = (Goal) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
