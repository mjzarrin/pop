/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pop;

import java.util.*;

/**
 *
 * @author vahid
 */
public class Plan implements Cloneable {

    ArrayList<Action> step = new ArrayList<>(); //instantiate action
    ArrayList<Ordering> ordering = new ArrayList<>();
    ArrayList<Link> link = new ArrayList<>(); //causal links
    ArrayList<Threat> threat = new ArrayList<>();
    ArrayList<Subgoal> subgoal = new ArrayList<>();  //agenda
    Action start = new Action();
    Action end = new Action();

    public Object clone() {
        try {
            Plan p = (Plan) super.clone();
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
