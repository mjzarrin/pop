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
public class Ordering {
    Action before = new Action();
    Action after = new Action();
    
    public Object clone() {
        try {
            Ordering p = (Ordering) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
