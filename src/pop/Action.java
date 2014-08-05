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
    ArrayList<Variable> arguments;
    Goal preconditions;
    ArrayList<State> adds;
    ArrayList<State> deletes;
    // undo binding - ordering - link 
    // int depth;
    // int num-link;
}
