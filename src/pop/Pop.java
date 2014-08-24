/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pop;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author vahid
 */
public class Pop {

    /**
     * @param args the command line aiguments System.out.piintln();
     */
    ArrayList<State> states = new ArrayList<>();
    ArrayList<Action> operators = new ArrayList<>();
    ArrayList<String> objects = new ArrayList<>();
    ArrayList<Variable> bounded = new ArrayList<>();
    Action init = new Action();
    Action goal = new Action();
//    Subgoal subgoal = new Subgoal();
    Plan plan;
    boolean withConstrain = false;
    ArrayList<ConstraintThread> consthread = new ArrayList<>();
    public static PrintWriter writer;

    public static void main(String[] args) throws IOException {
        // read readDomain
        Pop popObject = new Pop();

        popObject.readDomain();
        popObject.readProblem();
        Plan plan = popObject.makeInitialPlan();
        writer = new PrintWriter("src/pop/output.txt", "UTF-8");
        writer.println("POP START \n \n \n");
        System.out.println("POP Started");
        Plan lastPlan = popObject.pop(plan);
        // list of all predicators                                                                          Done
        // list of all actions                                                                              Done
        // read readProblem                 //done
        // list of all object           //done
        // make null plan from initial va end actoion    
//        System.err.println(lastPlan.toString());
        writer.close();
        //     plan.start= init;
        // call popObject
    }

//    public void choice(){
//        
//        subgoal = plan.subgoal.get(0);
//        plan.subgoal.remove(0);  
//    }
    public Action operatorsOfAction(Action ac) {
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).type.equalsIgnoreCase(ac.type)) {
                return operators.get(i);
            }
        }

        return null;

    }

    public void writeToFile(String s) throws IOException {
        writer.println(s);

    }

    public Plan pop(Plan p) throws IOException {

        //1- terminatrin //size subgoal ==0
        if (p.subgoal.isEmpty()) {

            System.out.println("Finish");
            writeToFile("Plan Finished");

//            order(p);
            return p;

        } else {
//            get back up of p
            Plan backUpPlan = clone(p);

            Subgoal subgoal = leastCommitmentStrategy(p.subgoal);
            writeToFile("Subgoal Selected : " + subgoal.state.predicate + " for action " + subgoal.action.type);

            p.threat = new ArrayList<Threat>();
//            Subgoal subgoal = p.subgoal.get(0);
//            p.subgoal.remove(0);

//            }
            System.out.println("    Subgoal : " + subgoal.state.predicate + " in action " + subgoal.action.type);

            Action ac = new Action();

            boolean notFoundAction = true; // for reorderActionsInPlan select or reorderOperators
            ArrayList<Variable> localbound = new ArrayList<>();

            // reorderActionsInPlan choose action
            ArrayList<Action> internalSelect = reorderActionsInPlan(p);
            ArrayList<Action> posibleActions;
            int numOfNull;
            // opertators and steps unordering
            boolean internalFound = false;

            writeToFile("Start Searching in internal actions.");
//            Internal
            if (notFoundAction) {

                for (int i = 0; i < internalSelect.size(); i++) {
                    numOfNull = 0;
                    if (notFoundAction) {
                        for (int j = 0; j < internalSelect.get(i).adds.size(); j++) {
                            if (internalSelect.get(i).adds.get(j).predicate.equalsIgnoreCase(subgoal.state.predicate)) {

                                int temp = 0;
                                int k;
                                ArrayList<Variable> arvar = new ArrayList<>();
                                //       ArrayList<Object> forbidValue = new ArrayList<>();
                                for (k = 0; k < subgoal.state.numberOfArg; k++) {
                                    if (subgoal.state.arguments.get(k).value != null && internalSelect.get(i).adds.get(j).arguments.get(k).value != null) {
                                        if (internalSelect.get(i).adds.get(j).arguments.get(k).value.equals(subgoal.state.arguments.get(k).value)) {
                                            temp++;
                                        }

                                    } else if (subgoal.state.arguments.get(k).value == null && internalSelect.get(i).adds.get(j).arguments.get(k).value != null) {

                                        Variable v = new Variable();
                                        if (operatorsOfAction(internalSelect.get(i)) != null) {
                                            v.name = operatorsOfAction(internalSelect.get(i)).adds.get(j).arguments.get(k).name;

                                        } else {
                                            v.name = subgoal.state.arguments.get(k).name;

                                        }
                                        v.value = internalSelect.get(i).adds.get(j).arguments.get(k).value;

                                        //                 if(!forbidValue.contains(v.value)){
                                        arvar.add(v);
                                        temp++;
                                        //                      forbidValue.add(v.value);
                                        //                 }

                                    } else if (internalSelect.get(i).adds.get(j).arguments.get(k).value == null && subgoal.state.arguments.get(k).value != null) {
                                        Variable v = new Variable();
                                        if (operatorsOfAction(internalSelect.get(i)) != null) {
                                            v.name = operatorsOfAction(internalSelect.get(i)).adds.get(j).arguments.get(k).name;

                                        } else {
                                            v.name = subgoal.state.arguments.get(k).name;

                                        }

                                        v.value = subgoal.state.arguments.get(k).value;
                                        //                    if(!forbidValue.contains(v.value)){
                                        arvar.add(v);
                                        temp++;
                                        //                        forbidValue.add(v.value);
                                        //                   }
                                    } else if (internalSelect.get(i).adds.get(j).arguments.get(k).value == null && subgoal.state.arguments.get(k).value == null) {
                                        // inja bayad codi bezanam ke dorost bashe
                                        temp++;
                                        // fekr mikonam bayad constarin tarif konam faghat
                                    } else {
                                        break;
                                    }
                                }
                                boolean canDone = linkAndOrderTest(p, internalSelect.get(i), subgoal); // check loop condition in plan
                                if ((temp == k && temp != 0) || subgoal.state.numberOfArg == 0 && canDone) {
//                                Action ac = null;

                                    ac = internalSelect.get(i);
                                    writeToFile(" Internal Action Selected : " + ac.type + " Arguments :" + ActionsArgumnetsToString(ac));

                                    if (arvar.size() > 0) {
                                        localbound = arvar;
                                        if (canBoundAction(ac, localbound)) {
                                            boundAction(p, ac, localbound, true);
                                            writeToFile("bounding operation for action is start for " + localboundArgumentToString(localbound));
                                        } else {
                                            // find in weakthread
                                            for (ConstraintThread consthread : this.consthread) {
                                                for (Variable localbound1 : localbound) {
                                                    if ((consthread.variable.name == null ? localbound1.name == null : consthread.variable.name.equals(localbound1.name)) && consthread.thread.action == ac) {
//                                                        
                                                        p.threat.add(consthread.thread);
                                                    }
                                                }

                                            }

                                        }
                                    }
                                    System.out.println("add Internal Action: " + ac.type + " for state  " + subgoal.state.predicate + " - state args :" + (subgoal.state.arguments.size() > 0 ? subgoal.state.arguments.get(0).value : "") + " , " + (subgoal.state.arguments.size() > 1 ? subgoal.state.arguments.get(1).value : "") + " for : " + subgoal.state.predicate);

                                    Link link = new Link();
                                    link.provider = ac;
                                    link.receiver = subgoal.action;
                                    link.condition = subgoal.state;

                                    p.link.add(link);
                                    writeToFile(" Link Added : ");
                                    writeToFile("              " + "link provider : " + link.provider.type);
                                    writeToFile("              " + "link receiver  : " + link.receiver.type);
                                    writeToFile("              " + "link condition: " + link.condition.predicate);
                                    internalFound = true;
                                    notFoundAction = false;
                                    writeToFile("Start Finding Thread for Internal Actions: ");
                                    findThread(p, backUpPlan, ac, link, internalFound);

                                    break;
                                }
//                                else if( subgoal.state.numberOfArg == 0 && canDone) {
//                                
//                                }

                            }
                        }
                    }
                }
            }
            //  reorderOperators choose

            reorderOperators();
            //External
            if (notFoundAction) {
                writeToFile("Start Searching an operators Satisfies the subgoal.");
                for (int i = 0; i < operators.size(); i++) {
                    for (int j = 0; j < operators.get(i).adds.size(); j++) {
                        if (notFoundAction) {
                            if (operators.get(i).adds.get(j).predicate.equalsIgnoreCase(subgoal.state.predicate)) {

                                //                            ac = copyAction(operators.get(i));
                                //                            ac = new Action(operators.get(i));
                                ac = copyAction(operators.get(i));
                                writeToFile("operators selected and goes for bounding. the operator is: " + ac.type);

                                ArrayList<Variable> arrvar = new ArrayList<>();
                                for (int k = 0; k < ac.adds.get(j).numberOfArg; k++) { // tedade argument ha
                                    Variable v = new Variable();
                                    v.value = subgoal.state.arguments.get(k).value;
                                    v.name = ac.adds.get(j).arguments.get(k).name;
                                    arrvar.add(v);
                                    ac.arguments.get(k).value = v.value;

                                }

                                localbound.addAll(arrvar);
                                bounded.addAll(ac.arguments);

                                notFoundAction = false;
                                break;
                            }
                        }
                    }
                }

                writeToFile("bounding operation for action is start for " + localboundArgumentToString(localbound));
                ac = boundAction(p, ac, localbound, false);

                // action inja bound shoode ast 
                Link link = new Link();
//                link.provider = new Action(ac);
                link.provider = ac;
                link.receiver = subgoal.action;
                link.condition = subgoal.state;

                p.link.add(link);
                writeToFile(" Link Added : ");
                writeToFile("              " + "link provider : " + link.provider.type);
                writeToFile("              " + "link receiver  : " + link.receiver.type);
                writeToFile("              " + "link condition: " + link.condition.predicate);

                p.subgoal.addAll(ac.preconditions.subgoals); // age subgoali bashe ke bound nabashe chi mishe?????
//                        p.subgoal
                writeToFile("new Subgoals added as bellow: ");
                writeToFile(subgoalToString(ac.preconditions.subgoals));
                p.step.add(ac);
//                System.out.println("add External Action: " + ac.type + " - args :" + ac.arguments.get(0).value + " , " + (ac.arguments.size() > 1 ? ac.arguments.get(1).value : "") + " for : " + subgoal.state.predicate);
                notFoundAction = false;
                writeToFile("find thread for new action : " + ac.type);
                findThread(p, backUpPlan, ac, link, internalFound);
            }
            resolvThread(p);
            pop(p);
        }
        return p;
    }

    public boolean stateEquality(State st1, State st2) {
        if (st1.predicate.equalsIgnoreCase(st2.predicate) && st1.numberOfArg == st2.numberOfArg) {
            int eqcheck = 0;
            for (int i = 0; i < st1.numberOfArg; i++) {
                if (st1.arguments.get(i).value == st1.arguments.get(i).value) {
                    eqcheck++;
                }
            }
            if (eqcheck == st1.numberOfArg) {
                return true;
            }
        }

        return false;
    }

    public String subgoalToString(ArrayList<Subgoal> sublist) {
        String s = "";
        for (int i = 0; i < sublist.size(); i++) {
            s = s.concat(sublist.get(i).state.predicate);
            s = s.concat(" for action " + sublist.get(i).action.type + " ");
        }

        return s;

    }

    public void findThread(Plan p, Plan backUpPlan, Action ac, Link link, boolean internalfound) throws IOException {
        if (internalfound) {
            writeToFile("Internal Action found and must find actions that is thread for link between " + link.provider.type + " to " + link.receiver.type + " for state " + link.condition.predicate);
            for (int i = 0; i < p.step.size(); i++) {
                for (int j = 0; j < p.step.get(i).deletes.size(); j++) {
                    if (stateEquality(p.step.get(i).deletes.get(j), link.condition)) {
                        writeToFile(" Thread find :");
                        writeToFile("Thread Link :" + link.provider.type + " to " + link.receiver.type);
                        writeToFile("thread action is :" + ac.type);
                        writeToFile("thread condition : " + link.condition.predicate);
                        writeToFile("Ignore all conditions on this link and action ");
                        Threat thread = new Threat();
                        thread.link = link;
                        thread.action = ac;
                        thread.state = link.condition;
                        System.out.println("        Thread found : in link between " + thread.link.provider.type + " to " + thread.link.receiver.type + "for state " + thread.state.predicate + " in action " + thread.action.type);
                        p.threat.add(thread);
                        break;

                    }

                }
            }

        } else { // for externall add
            writeToFile("external action found and this actions must check to not delete any conditions of all links in plan. our action is :" + ac.type + " Arguments " + ActionsArgumnetsToString(ac));

            for (int j = 0; j < backUpPlan.link.size(); j++) {
                for (int i = 0; i < ac.deletes.size(); i++) {

                    if (p.link.get(j).condition.predicate.equals(ac.deletes.get(i).predicate)) {
                        int temp = 0;
                        int k = 0;
                        int indexOfVariable = 0;
                        ArrayList<Variable> constrain = new ArrayList<>();
                        for (; k < ac.deletes.get(i).numberOfArg; k++) {
                            Variable v = null;
                            if (ac.deletes.get(i).arguments.get(k).value != null && p.link.get(j).condition.arguments.get(k).value != null && ac.deletes.get(i).arguments.get(k).value.equals(p.link.get(j).condition.arguments.get(k).value)) {
                                temp++;
                            } else if (ac.deletes.get(i).arguments.get(k).value == null || p.link.get(j).condition.arguments.get(k).value != null) {
                                temp = temp++;
//                                inja ye flag set mikonam ke age in halata etefagh oftad dakheleresolver kari kone ke constarinesh taghir peyda kone.
                                v = p.link.get(j).condition.arguments.get(k);
                                indexOfVariable = ac.arguments.indexOf(v);
                                writeToFile("Constrain added.");
                                
                                
                                
//                                ac.arguments.get(i).constraints.add(v);
                            } else if (ac.deletes.get(i).arguments.get(k).value != null || p.link.get(j).condition.arguments.get(k).value == null) {
                                temp = temp++;
//                                inja ye flag set mikonam ke age in halata etefagh oftad dakheleresolver kari kone ke constarinesh taghir peyda kone.
//                                v = ac.deletes.get(i).arguments.get(k);
//                                indexOfVariable = ac.arguments.indexOf(v);
                                // in constrain male p.link.get(j) mishe **************************************************************8 ino hanoz nemitonam manage konam
                                
//                                ac.arguments.get(i).constraints.add(v);
                            } else if (ac.deletes.get(i).arguments.get(k).value == null || p.link.get(j).condition.arguments.get(k).value == null) {
                                // 2 tash null hast ya moteghayer haye mnokhalef mizarim ya jabe jaee ke dovomi ason tare.
                                temp++;
                            } else {
                            }

                            if (v != null) {
                                constrain.add(v);
                                Threat thread = new Threat();
                                thread.link = link;
                                thread.action = ac;
                                thread.state = link.condition;
                                ConstraintThread ct = new ConstraintThread();
                                ct.thread = thread;
                                ct.variable = v;
                                this.consthread.add(ct);
                                p.threat.remove(thread);
                            }
                        }
                        if (k == temp && temp != 0 || ac.deletes.get(i).numberOfArg == 0) {
                            if (constrain.isEmpty()) {

                                Threat thread = new Threat();
//                            writeToFile("Hard Thread Found ( with null check ) :");
                                thread.link = p.link.get(j);
                                thread.action = ac;
                                thread.state = ac.deletes.get(i);
                                writeToFile(" Thread find :");
                                writeToFile("Thread Link :" + thread.link.provider.type + " to " + thread.link.receiver.type);
                                writeToFile("thread action is :" + thread.action.type);
                                writeToFile("thread condition : " + thread.state.predicate);
                                writeToFile("mikham bebinam chiye  : " + thread.link.condition.predicate);

                                writeToFile("Ignore all conditions on this link and action ");
                                System.out.println("        Thread found : in link between " + thread.link.provider.type + " to " + thread.link.receiver.type + "for state " + thread.state.predicate + " in action " + thread.action.type);
                                p.threat.add(thread);
                                break; // age chand ta thread az ye action roye ye link rokh dad , faghat yekisho mizare to thread ha , badan mishe behtaresh kard.
                            } else {
                                ac.arguments.get(indexOfVariable).constraints.addAll(constrain);
                                
                                writeToFile("weak Thread find : Resolv by constarin");
                                writeToFile("Thread Link :" + p.link.get(j).provider.type + " to " + p.link.get(j).receiver.type);
                                writeToFile("thread action is :" + ac.type);
                                writeToFile("thread condition : " + ac.deletes.get(i).predicate + " must not equals to " + constrainToString(ac.arguments.get(i)));

                            }
                        }

                    }

                }

            }

        }

    }

    public void resolvThread(Plan p) throws IOException {
        // resolve threads 
        writeToFile("Thread Resolver Starts and new order created as bellow : ");
      
        for (Threat thread : p.threat) {
            Ordering o = new Ordering();
            if (thread.link.receiver == goal) {
                o.before = thread.action;
                o.after = thread.link.provider;

            } else if (thread.link.provider == init) {
                o.before = thread.link.receiver;
                o.after = thread.action;
            } else {  // inja bayad ezafe kard taghire constarin
                Random r = new Random();
                double rand = r.nextDouble();
//                rand = 0.25;
                if (rand < 0.5) {
                    o.before = thread.action;
                    o.after = thread.link.provider;

                } else {
                    o.before = thread.link.receiver;
                    o.after = thread.action;
                }
            }

            writeToFile(" action " + o.before.type + " comes before action " + o.after.type);

            p.ordering.add(o);
      
        }
        

    }

    public String constrainToString(Variable v) {
        String s = "";
        for (int i = 0; i < v.constraints.size(); i++) {
            s = s.concat(v.constraints.get(i).value + " ");
        }
        return s;
    }

    public boolean linkAndOrderTest(Plan p, Action ac, Subgoal subgoal) {

        boolean canDone = true;

        ArrayList<Action> forbiddenAction = new ArrayList<>();
        ArrayList<Boolean> check = new ArrayList<>();
        forbiddenAction.add(subgoal.action);
        check.add(false);

        while (check.contains(false)) {
            int ind = check.indexOf(false);
            check.set(ind, true);
            Action a = forbiddenAction.get(ind);

            for (int i = 0; i < p.link.size(); i++) {

                if (p.link.get(i).provider == a) {

                    forbiddenAction.add(p.link.get(i).receiver);
                    check.add(true);
                }

            }

        }
        check.set(0, false); // khode action dobare false mishe vase check kardane order ha
        while (check.contains(false)) {
            int ind = check.indexOf(false);
            check.set(ind, true);
            Action a = forbiddenAction.get(ind);

            for (Ordering ord : p.ordering) {
                if (ord.before == a) {
                    forbiddenAction.add(ord.after);
                    check.add(true);
                }
            }

//            for (int i = 0; i < p.ordering.size(); i++) {
//
//                if (p.ordering.get(i).before == a) {
//                    p.ordering.
//                    forbiddenAction.add(p.ordering.get(i).after);
//                    check.add(true);
//                }
//
//            }
        }
        canDone = !forbiddenAction.contains(ac);

        return canDone;
    }

    public Plan clone(Plan p) {

        Plan backUp = new Plan();
//        backUp.start = (Action) p.start.clone();
//        backUp.end = (Action) p.end.clone();

        backUp.start = copyAction(init);
        backUp.end = copyAction(goal);
        backUp.link = (ArrayList<Link>) p.link.clone();
        backUp.ordering = (Set<Ordering>) p.ordering;
        backUp.step = (ArrayList<Action>) p.step.clone();
        backUp.subgoal = (ArrayList<Subgoal>) p.subgoal.clone();
        backUp.threat = (ArrayList<Threat>) p.threat.clone();

        return backUp;
    }

    public Subgoal leastCommitmentStrategy(ArrayList<Subgoal> subgoals) {

        //     int[] numberOfPredicates;
        //    numberOfPredicates = new int[subgoals.size()];
        Subgoal s = subgoals.get(0);
        int index = 0;
        for (int i = 1; i < subgoals.size(); i++) {
            if (subgoals.get(i).state.numberOfArg < subgoals.get(i - 1).state.numberOfArg) {
                s = subgoals.get(i);
                index = i;
            }
        }
        subgoals.remove(index);
        return s;
    }

    public Action boundAction(Plan p, Action ac, ArrayList<Variable> localbound, boolean internalselect) {
        // meghdar dehi kardan be Ation
        if (!canBoundAction(ac, localbound)) {
            return null;
        }
        for (int r = 0; r < ac.arguments.size(); r++) {

            for (int y = 0; y < localbound.size(); y++) {
                if (localbound.get(y).name.equalsIgnoreCase(ac.arguments.get(r).name)) {
                    ac.arguments.get(r).value = localbound.get(y).value;
                }
            }

        }

        for (int r = 0; r < ac.adds.size(); r++) {
            for (int a = 0; a < ac.adds.get(r).numberOfArg; a++) {
                for (int y = 0; y < localbound.size(); y++) {
                    if (localbound.get(y).name.equalsIgnoreCase(ac.adds.get(r).arguments.get(a).name)) {
                        ac.adds.get(r).arguments.get(a).value = localbound.get(y).value;
                    }
                }
            }
        }

        for (int r = 0; r < ac.deletes.size(); r++) {
            for (int a = 0; a < ac.deletes.get(r).numberOfArg; a++) {
                for (int y = 0; y < localbound.size(); y++) {
                    if (localbound.get(y).name.equalsIgnoreCase(ac.deletes.get(r).arguments.get(a).name)) {
                        ac.deletes.get(r).arguments.get(a).value = localbound.get(y).value;
                    }
                }
            }
        }
        for (int r = 0; r < ac.preconditions.subgoals.size(); r++) {
            for (int a = 0; a < ac.preconditions.subgoals.get(r).state.numberOfArg; a++) {

                for (int y = 0; y < localbound.size(); y++) {
                    if (localbound.get(y).name.equalsIgnoreCase(ac.preconditions.subgoals.get(r).state.arguments.get(a).name)) {
                        ac.preconditions.subgoals.get(r).state.arguments.get(a).value = localbound.get(y).value;
                    }
                }
            }
        }

        if (internalselect) {
            for (int i = 0; i < p.link.size(); i++) {
                if (p.link.get(i).provider == ac) {
                    Action ac2 = p.link.get(i).receiver;
                    for (int j = 0; j < ac2.preconditions.subgoals.size(); j++) {
                        for (int k = 0; k < ac2.preconditions.subgoals.get(j).state.numberOfArg; k++) {
                            for (int l = 0; l < localbound.size(); l++) {
                                if (ac2.preconditions.subgoals.get(j).state.arguments.get(k).equals(localbound.get(l))) {
                                    boundAction(p, ac2, localbound, true);
                                }
                            }

                        }
                    }
                } else {
                    continue;
                }

            }
        }
        return ac;

    }

    public String ActionsArgumnetsToString(Action ac) {
        String s = "";
        for (int i = 0; i < ac.arguments.size(); i++) {
            s = s.concat("  ");
            s = s.concat(ac.arguments.get(i).name);
            s = s.concat(": ");
            if (ac.arguments.get(i).value != null) {
                s = s.concat(ac.arguments.get(i).value.toString());
            } else {
                s = s.concat("null");
            }
            s = s.concat("  ");

        }
        return s;
    }

    public void readDomain() throws FileNotFoundException, IOException {
        try (BufferedReader domain = new BufferedReader(new FileReader("src/pop/domain.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = domain.readLine();

            while (line != null) {
//                sb.append(line);
//                sb.append(System.lineSeparator());
                if (line.contains("PREDICATES")) {

                    int numOfPredicat = findLineNumber(line);
//                    System.out.println("predictore : " + numOfPredicat+ " " );
                    for (int i = 0; i < numOfPredicat; i++) {
                        line = domain.readLine();
                        //////////////// make states here
                        State s = new State();
                        s.predicate = findLineName(line);

                        s.numberOfArg = findLineNumber(line);
                        // for argument declare variable
                        ArrayList<Variable> arguments = new ArrayList<>();
                        for (int p = 0; p < s.numberOfArg; p++) {
                            String st = Integer.toString(p + 1);
                            Variable v = new Variable();
                            v.name = s.predicate.toString().concat("-" + st);

                            arguments.add(v);
                        }
                        s.arguments = arguments;

//                        s.arguments = null;
                        states.add(s);
                    }
                }
//                System.out.println(line);

                if (line.contains("OPERATORS")) {

                    int numOfOperator = findLineNumber(line);
//                    System.out.println("operator : " + numOfOperator);
                    for (int i = 0; i < numOfOperator; i++) {
                        line = domain.readLine();
                        Action ac = new Action(); // action ro meghdar dehi mikoni be operators.add mikonim
                        ac.type = line;
                        line = domain.readLine();
                        // dorost kardane action argument ac.argument

                        if (line.contains("Parameters")) {
                            int numParam = findLineNumber(line);
                            ArrayList<Variable> arguments = new ArrayList<>();
                            for (int j = 0; j < numParam; j++) {
                                line = domain.readLine();
                                Variable v = new Variable();
                                v.name = line;
                                //      v.value = line;
                                arguments.add(v);
                                //                            ac.arguments.add(v);
                            }
                            ac.arguments = arguments;
                            line = domain.readLine();
                        }

                        // ac.preconditions
                        if (line.contains("Preconditions")) {
                            int numPrecond = findLineNumber(line);
                            Goal g = new Goal(); // type precondition Goal ast
//                              ArrayList<Subgoal> subgoals = null ;
                            ArrayList<Subgoal> subgoals = new ArrayList<>();
                            // peyda kardane precondition ha
                            for (int k = 0; k < numPrecond; k++) {
                                Subgoal sbgoal = new Subgoal();

                                line = domain.readLine();
                                State s = new State();
                                s.predicate = line;
                                s.numberOfArg = predicateSize(line);
                                ArrayList<Variable> arguments = new ArrayList<>();
                                for (int n = 0; n < s.numberOfArg; n++) {
                                    line = domain.readLine();
                                    Variable v = new Variable();
                                    v.name = line;
                                    //          v.value = line; //I Dont know yet why

                                    arguments.add(v);
                                    //           ac.arguments.add(v);
                                    //              ac.preconditions.subgoals.add(sbgoal);
                                }
                                s.arguments = arguments;
                                // ta inja s sakhte shoode hala mishe subgoal ro besazim
                                sbgoal.state = s;

                                sbgoal.action = ac; // ac mishe  // hamin action ke dakhelesh hastimo bayad benevisam

// subgoal ham sakhte shood. hala bayad be g addesh konim
                                subgoals.add(sbgoal);
//                                System.out.println();

                                //                 ac.preconditions.subgoals.add(sbgoal);
                            }
                            g.subgoals = subgoals;
                            ac.preconditions = g;

//                         
                            // ta inja inam sakhte shood .berim soraghe add list. check konam line ham
                        }
                        line = domain.readLine();
//*************************************************************************
                        if (line.contains("Add-Effects")) {
                            ArrayList<State> effects = new ArrayList<>();
                            int numAdds = findLineNumber(line);

                            for (int k = 0; k < numAdds; k++) {
                                line = domain.readLine();
                                State s = new State();
                                s.predicate = line;
                                s.numberOfArg = predicateSize(line);
                                ArrayList<Variable> arguments = new ArrayList<>();
                                for (int n = 0; n < s.numberOfArg; n++) {
                                    line = domain.readLine();
                                    Variable v = new Variable();
                                    v.name = line;
//                                    v.value = line; //I Dont know yet why
                                    arguments.add(v);

                                }
                                s.arguments = arguments;
                                effects.add(s);
                                //     ac.adds.add(s);
                            }
                            ac.adds = effects;
                        }
                        line = domain.readLine();

                        if (line.contains("Delete-Effects")) {
                            int numDelletes = findLineNumber(line);
                            ArrayList<State> deletes = new ArrayList<>();

                            for (int k = 0; k < numDelletes; k++) {
                                line = domain.readLine();
                                State s = new State();
                                s.predicate = line;
                                s.numberOfArg = predicateSize(line);
                                ArrayList<Variable> arguments = new ArrayList<>();
                                for (int n = 0; n < s.numberOfArg; n++) {
                                    line = domain.readLine();
                                    Variable v = new Variable();
                                    v.name = line;
//                                    v.value = line; //I Dont know yet why
                                    arguments.add(v);

                                }
                                s.arguments = arguments;
                                deletes.add(s);
                            }
                            ac.deletes = deletes;
                        }

                        line = domain.readLine(); // never used
                        //// make actions here
                        operators.add(ac);
                    }
                }
                line = domain.readLine();
            }
//            String everything = sb.toString();
//            System.out.println(everything);
        }
    }

    public int predicateSize(String line) {

        int predicateSize = 0;
        // gashtan toye liste states vase fahmidane tedad parameter haee ke bayad khonde beshe
        for (int m = 0; m < states.size(); m++) {
            //           System.out.println(states.get(m).predicate);
            if (states.get(m).predicate.equalsIgnoreCase(line)) {
                predicateSize = states.get(m).numberOfArg;
                //               System.out.println(states.get(m).predicate+ " find ");

            }
            //        break;
        }
        //       System.out.println(predicateSize+ " find ");
        return predicateSize;
    }

    public int findLineNumber(String line) {

        String[] part = line.split(":");
        int num = (int) Integer.parseInt(part[1]);

        return num;
    }

    public String findLineName(String line) {

        String[] part = line.split(":");

        return part[0];
    }

    public Plan makeInitialPlan() {
        Plan plan = new Plan();
        plan.start = init;
        plan.end = goal;
        ArrayList<Subgoal> list = new ArrayList<>(goal.preconditions.subgoals);
        plan.subgoal = list;
        plan.threat = new ArrayList<>();
        plan.link = new ArrayList<>();
        Set<Ordering> ordering = new HashSet<>();
        Ordering order = new Ordering();
        order.before = init;
        order.after = goal;
        ordering.add(order);
        plan.ordering = ordering;
        plan.step.add(init);
        plan.step.add(goal);
//        popObject(plan);
        return plan;
    }

    public Action copyAction(Action action) {
        Action ac = new Action();
        ac.type = action.type.toString();

        // arguments 
        ArrayList<Variable> arva = new ArrayList<>();
        for (int i = 0; i < action.arguments.size(); i++) {
            Variable v = new Variable();
            v = action.arguments.get(i);
            arva.add(v);

        }
        ac.arguments.addAll(arva);

//        deletelist
        ArrayList<State> delarr = new ArrayList<>();
        for (int i = 0; i < action.deletes.size(); i++) {
            State s = new State();
            s.numberOfArg = action.deletes.get(i).numberOfArg;
            s.predicate = action.deletes.get(i).predicate;
            ArrayList<Variable> arv = new ArrayList<>();
            for (int j = 0; j < action.deletes.get(i).arguments.size(); j++) {
                Variable v = new Variable();
                v = action.deletes.get(i).arguments.get(j);
                arv.add(v);
            }
            s.arguments.addAll(arv);
            delarr.add(s);
        }
        ac.deletes = delarr;

        // addlist
        ArrayList<State> addarr = new ArrayList<>();
        for (int i = 0; i < action.adds.size(); i++) {
            State s = new State();
            s.numberOfArg = action.adds.get(i).numberOfArg;
            s.predicate = action.adds.get(i).predicate;
            ArrayList<Variable> arv = new ArrayList<>();
            for (int j = 0; j < action.adds.get(i).arguments.size(); j++) {
                Variable v = new Variable();
                v = action.adds.get(i).arguments.get(j);
                arv.add(v);
            }
            s.arguments.addAll(arv);
            addarr.add(s);
        }
        ac.adds = addarr;

        // preconditions
        Goal g = new Goal();
        ArrayList<Subgoal> arrsub = new ArrayList<>();
        for (int i = 0; i < action.preconditions.subgoals.size(); i++) {
            Subgoal subg = new Subgoal();
//            Action a = new Action();
//            a = action.preconditions.subgoals.get(i).action;

            State s = new State();
            s.numberOfArg = action.preconditions.subgoals.get(i).state.numberOfArg;
            s.predicate = action.preconditions.subgoals.get(i).state.predicate;

            ArrayList<Variable> var = new ArrayList<>();
            for (int j = 0; j < action.preconditions.subgoals.get(i).state.arguments.size(); j++) {
                Variable v = new Variable();
                v = action.preconditions.subgoals.get(i).state.arguments.get(j);
                var.add(v);
            }
            s.arguments.addAll(var);

//            subg.action = action.preconditions.subgoals.get(i).action;
            subg.action = ac;
            subg.state = s;
            arrsub.add(subg);

        }
        g.subgoals = arrsub;

        ac.preconditions = g;
//        ac.preconditions = action.preconditions;
        return ac;

    }

    public ArrayList<Action> reorderActionsInPlan(Plan p) {
        ArrayList<Action> selectAction = new ArrayList<>();
        ArrayList<Double> random = new ArrayList<>();
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < p.step.size(); i++) {
            Random r = new Random();
            double d = r.nextDouble();
            d = d * p.step.get(i).preconditions.subgoals.size();
            random.add(d);
            index.add(i);

            selectAction.add(p.step.get(i));
        }

        int j;
        boolean flag = true;   // set notFoundAction to true to begin first pass
        double temp;   //holding variable
//        int ind;
        Action actemp;
        while (flag) {
            flag = false;    //set notFoundAction to false awaiting a possible swap
            for (j = 1; j < random.size() - 1; j++) {
                if (random.get(j) < random.get(j + 1)) // change to > for ascending sort
                {
                    temp = random.get(j);                //swap elements
                    random.set(j, random.get(j + 1));
                    random.set(j + 1, temp);

//                    ind = index.get(j);
//                    index.set(j, index.get(j+1));
//                    index.set(j+1, ind);
                    actemp = selectAction.get(j);
                    selectAction.set(j, selectAction.get(j + 1));
                    selectAction.set(j + 1, actemp);

                    flag = true;              //shows a swap occurred  
                }
            }
        }
        return selectAction;

    }

    public void reorderOperators() {
        ArrayList<Action> ext = new ArrayList<>();
        ArrayList<Double> random = new ArrayList<>();

        for (int i = 0; i < this.operators.size(); i++) {

            Random r = new Random();
            double d = r.nextDouble();
            d = d * this.operators.get(i).preconditions.subgoals.size();
            random.add(d);

        }

        int j;
        boolean flag = true;   // set notFoundAction to true to begin first pass
        double temp;   //holding variable
//        int ind;
        Action actemp;
        while (flag) {
            flag = false;    //set notFoundAction to false awaiting a possible swap
            for (j = 0; j < random.size() - 1; j++) {
                if (random.get(j) < random.get(j + 1)) // change to > for ascending sort
                {
                    temp = random.get(j);                //swap elements
                    random.set(j, random.get(j + 1));
                    random.set(j + 1, temp);

//                    ind = index.get(j);
//                    index.set(j, index.get(j+1));
//                    index.set(j+1, ind);
                    actemp = this.operators.get(j);
                    this.operators.set(j, this.operators.get(j + 1));
                    this.operators.set(j + 1, actemp);

                    flag = true;              //shows a swap occurred  
                }
            }
        }

    }

    public void readProblem() throws FileNotFoundException, IOException {

        try (BufferedReader problem = new BufferedReader(new FileReader("src/pop/simple.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = problem.readLine();

            while (line != null) {

                if (line.contains("OBJECTS")) {
                    int numOfObjects = findLineNumber(line);
                    for (int i = 0; i < numOfObjects; i++) {
                        line = problem.readLine();
                        objects.add(line);

                    }
                    // i can readline 2 times or no
                }

                if (line.contains("INITIAL-STATE")) {

                    init.type = findLineName(line);
                    int numOfState = findLineNumber(line);

                    ArrayList<State> adds = new ArrayList<>();
                    for (int i = 0; i < numOfState; i++) {
                        line = problem.readLine();
                        State s = new State();
                        s.predicate = line;
                        ArrayList<Variable> arguments = new ArrayList<>();
                        //           line = readProblem.readLine();
                        int numberOfArgs = 0;
                        int index = 0;
                        // find number of argument in state
                        for (int j = 0; j < states.size(); j++) {
                            if (states.get(j).predicate.equalsIgnoreCase(line)) {
                                numberOfArgs = states.get(j).numberOfArg;
                                index = j;
                                break;
                            }
                        }
                        for (int k = 0; k < numberOfArgs; k++) {
                            line = problem.readLine();
                            Variable v = new Variable();
                            v.name = states.get(index).arguments.get(k).name;
                            v.value = line;
                            arguments.add(v);

//                                    line = readProblem.readLine();
//                            init.arguments.add(null)
                        }
                        s.arguments = arguments;
                        s.numberOfArg = numberOfArgs;

//                        testing 
//                        Variable p = new Variable();
//                        p.name = "ON";
//                        p.value = 
//                        s.arguments.add(new Variable())
                        adds.add(s);
                    }
//                    State fakes = new State();
//                    Variable v = new Variable();
//                    v.name = "on-table-1";
//                    v.value = null;
//                    ArrayList<Variable> t = new ArrayList<>();
//                    t.add(v);
//                    fakes.predicate = "ON-TABLE";
//                    fakes.numberOfArg = 1;
//                    fakes.arguments = t;

                    init.adds = adds;
//                    init.adds.add(fakes);

//                    operators.add(init);
                }
//                operators.add(init);
                if (line.contains("GOALS")) {
                    goal.type = findLineName(line);
                    int numOfSubGoal = findLineNumber(line);
                    Goal goals = new Goal();
                    ArrayList<Subgoal> subgoals = new ArrayList<>();

                    for (int i = 0; i < numOfSubGoal; i++) {

                        Subgoal subgoal = new Subgoal();
                        line = problem.readLine();
                        int numOfArgs = 0;
                        int index = 0; // index of state in list of readDomain state
                        for (int j = 0; j < states.size(); j++) {
                            if (states.get(j).predicate.equalsIgnoreCase(line)) {
                                numOfArgs = states.get(j).numberOfArg;
                                index = j;
                                break;
                            }
                        }
                        State s = new State();
                        s.predicate = line;
                        s.numberOfArg = numOfArgs;
                        ArrayList<Variable> arguments = new ArrayList<>();
                        for (int k = 0; k < numOfArgs; k++) {
                            line = problem.readLine();

                            Variable v = new Variable();
                            v.name = states.get(index).arguments.get(k).name;
                            v.value = line;
                            arguments.add(v);

//                                    line = readProblem.readLine();
//                            init.arguments.add(null)
                        }
                        s.arguments = arguments;
                        subgoal.state = s;
                        subgoal.action = goal;      // IIIIIIIIIInja barrasi mikhad
                        subgoals.add(subgoal);

                    }
                    goals.subgoals = subgoals;

                    goal.preconditions = goals;
//                    operators.add(goal);

                }

//                sb.append(line);
//                sb.append(System.lineSeparator());
                line = problem.readLine();
            }
//            String everything = sb.toString();
//            System.out.println(everything);
        }
    }

    public String localboundArgumentToString(ArrayList<Variable> localbound) {
        String s = "";
        for (int i = 0; i < localbound.size(); i++) {
            s = s.concat(localbound.get(i).name);
            s = s.concat(": ");
            s = s.concat(localbound.get(i).value.toString());
            s = s.concat(" ");
        }
        return s;
    }

    private boolean canBoundAction(Action ac, ArrayList<Variable> localbound) {
        boolean f = true;
        if (localbound == null) {
            return true;
        }
        for (Variable argument : ac.arguments) {
            for (Variable constraint : argument.constraints) {
                for (Variable localbound1 : localbound) {
                    if (localbound1.name.equals(argument.name)) {
                        if (constraint == localbound1) {
                            f = false;
                            return f;
                        }
                    }
                }
            }
        }

        return f;
    }

    public boolean variableEquality(Variable v1, Variable v2) {
        boolean f1 = false, f2 = false, f3;
        if (v1.name == null ? v2.name == null : v1.name.equals(v2.name)) {
            f1 = true;
        }
        if ((v1.value == null && v2.value == null) || (v1.value != null && v2.value != null && v1.value == v2.value)) {
            f2 = true;
        }
        f3 = true;
        if (v1.constraints.size() == v2.constraints.size()) {
            for (int i = 0; i < v1.constraints.size(); i++) {
                if (v1.constraints.get(i) == v2.constraints.get(i)) {
                    f3 = f3 && true;
                } else {
                    f3 = false;
                    break;
                }
            }
        }
        boolean f = f1 && f2;
        return f;
    }
}
