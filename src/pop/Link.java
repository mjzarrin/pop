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
    Action reciver = new Action();
    State condition = new State();
    
    public Object clone() {
        try {
            Link p = (Link) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
