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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author vahid
 */
public class Pop {

    ArrayList<State> states = new ArrayList<>();
    ArrayList<Action> operators = new ArrayList<>();
    ArrayList<String> objects = new ArrayList<>();
    ArrayList<Variable> bounded = new ArrayList<>();
    Action init = new Action();
    Action goal = new Action();
    boolean CYCLECHECK = true;
//    Subgoal subgoal = new Subgoal();
    Plan plan;
    boolean withConstrain = false;
    ArrayList<ConstraintThread> consthread = new ArrayList<>();
    public static PrintWriter writer;
    Subgoal subgoal;
    ArrayList<Subgoal> addedSubgoal;
    Plan backUpPlan;
    int numOfTry = 0;
    boolean endOfRecursive = true;

    public static void main(String[] args) throws IOException {
        // read readDomain
        Pop popObject = new Pop();

        popObject.readDomain();
        popObject.readProblem();
        Plan plan = popObject.makeInitialPlan();
        Date d = new Date();
        long fileNumber = d.getTime();

        writer = new PrintWriter("src/pop/output/output" + fileNumber + ".txt", "UTF-8");

        System.out.println("POP START \n \n \n");

        popObject.pop(plan);

//    writer.close ();
    }

    public ArrayList<String> availObject(Action ac, int index) {
        ArrayList<String> avail = new ArrayList<>();
        if (ac.arguments.isEmpty()) {
            avail = null;
        } else if (ac.arguments.size() == 1) {
            if (ac.arguments.get(0).constraints.isEmpty()) {
                avail = objects;
            } else {
                avail = (ArrayList<String>) objects.clone();
                for (int i = 0; i < ac.arguments.get(0).constraints.size(); i++) {
                    avail.remove(ac.arguments.get(0).constraints.get(i));
                }

            }
        } else if (ac.arguments.size() == 2) {
            if (index == 0) {
                avail = (ArrayList<String>) objects.clone();
                for (int i = 0; i < ac.arguments.get(0).constraints.size(); i++) {
                    avail.remove(ac.arguments.get(0).constraints.get(i));
                }
                if (ac.arguments.get(1).value != null) {
                    avail.remove(ac.arguments.get(1).value);
                }

            } else {
                avail = (ArrayList<String>) objects.clone();
                for (int i = 0; i < ac.arguments.get(1).constraints.size(); i++) {
                    avail.remove(ac.arguments.get(1).constraints.get(i));
                }
                if (ac.arguments.get(1).value != null) {
                    avail.remove(ac.arguments.get(0).value);
                }
            }

        }
        return avail;

    }

    public void pop(Plan p) throws IOException {

        if (numOfTry > 100) {

            System.err.println(" num of try " + numOfTry);
            numOfTry = 0;
            main(null);
        }
        numOfTry++;
        if (p.subgoal.isEmpty()) {
            System.out.println(" num of execution of pop: " + numOfTry);
            this.endOfRecursive = false;
            printLastPlan(p);
            findSolution(p);
//            writer.close();
            System.exit(0);
            return;

        } else {
//            System.out.println(" num of try " + numOfTry);

            if (endOfRecursive) {
//            get back up of p
                backUpPlan = clone(p);

                subgoal = leastCommitmentStrategy(p.subgoal);
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("Subgoal Selected : ");
                System.out.println("---------- " + subgoal.state.predicate + StateArgumenttoString(subgoal.state) + " for action " + subgoal.action.type + ActionsArgumnetsToString(subgoal.action));

                p.threat = new ArrayList<Threat>();
//            Subgoal subgoal = p.subgoal.get(0);
//            p.subgoal.remove(0);

//            }
                Action ac = new Action();

                boolean notFoundAction = true; // for reorderActionsInPlan select or reorderOperators
                ArrayList<Variable> localbound = new ArrayList<>();

                // reorderActionsInPlan choose action
//                ArrayList<Action> internalSelect = reorderActionsInPlan(p);
                ArrayList<Action> internalSelect = p.step;

                ArrayList<Action> posibleActions;
                int numOfNull;
                // opertators and steps unordering
                boolean internalFound = false;
//            boolean canDone = true;
                System.out.println("Start Searching in internal actions ...");

//            Internal
                ArrayList<Action> nonPermit = forwardChecking(p, subgoal.action);

                if (notFoundAction && CYCLECHECK) {

                    for (int i = 0; i < internalSelect.size(); i++) {
                        numOfNull = 0;
                        if (notFoundAction && !nonPermit.contains(internalSelect.get(i))) {
                            for (int j = 0; j < internalSelect.get(i).adds.size(); j++) {
                                if (internalSelect.get(i) != subgoal.action) {

                                    if (internalSelect.get(i).adds.get(j).predicate.equalsIgnoreCase(subgoal.state.predicate)) {

                                        int temp = 0;
                                        int k;
                                        ArrayList<Variable> boundAc = new ArrayList<>();
                                        ArrayList<Variable> boundSubgoalAction = new ArrayList<>();
                                        //       ArrayList<Object> forbidValue = new ArrayList<>();
                                        for (k = 0; k < subgoal.state.numberOfArg; k++) {
                                            if (subgoal.state.arguments.get(k).value != null && internalSelect.get(i).adds.get(j).arguments.get(k).value != null) {
                                                if (internalSelect.get(i).adds.get(j).arguments.get(k).value.equals(subgoal.state.arguments.get(k).value)) {
                                                    temp++;
                                                }

                                            } else if (subgoal.state.arguments.get(k).value == null && internalSelect.get(i).adds.get(j).arguments.get(k).value != null) {

                                                Variable v = new Variable();
//                                            if (operatorsOfAction(internalSelect.get(i)) != null) {
//                                                v.name = operatorsOfAction(internalSelect.get(i)).adds.get(j).arguments.get(k).name;
//
//                                            } else {
//                                                v.name = subgoal.state.arguments.get(k).name;
//
//                                            }
                                                v.name = subgoal.state.arguments.get(k).name;

                                                v.value = internalSelect.get(i).adds.get(j).arguments.get(k).value;

                                                //                 if(!forbidValue.contains(v.value)){
                                                boundSubgoalAction.add(v);
                                                temp++;
                                                //                      forbidValue.add(v.value);
                                                //                 }

                                            } else if (internalSelect.get(i).adds.get(j).arguments.get(k).value == null && subgoal.state.arguments.get(k).value != null) {
                                                Variable v = new Variable();
//                                            if (operatorsOfAction(internalSelect.get(i)) != null) {
//                                                v.name = operatorsOfAction(internalSelect.get(i)).adds.get(j).arguments.get(k).name;
//
//                                            } else {
//                                                v.name = subgoal.state.arguments.get(k).name;
//
//                                            }
                                                v.name = operatorsOfAction(internalSelect.get(i)).adds.get(j).arguments.get(k).name;

                                                v.value = subgoal.state.arguments.get(k).value;
                                                //                    if(!forbidValue.contains(v.value)){
                                                boundAc.add(v);
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
                                            CYCLECHECK = cycleCheck(p, ac, subgoal.action);
                                            if (CYCLECHECK == false) {
                                            } else {
                                                System.out.println(" Internal Action Selected : ");
                                                System.out.println("---------- " + ac.type + ActionsArgumnetsToString(ac));

                                                if (boundAc.size() > 0) {
                                                    localbound = boundAc;

                                                    if (canBoundAction(ac, localbound)) {
                                                        System.out.println("bounding operation for starting ... ");
                                                        System.out.println("---------- " + ac.type + ActionsArgumnetsToString(ac) + " to " + localboundArgumentToString(localbound));

                                                        boundAction(p, ac, localbound, true);

                                                        System.out.println("actin bounds : ");
                                                        System.out.println("--------- " + ac.type + ActionsArgumnetsToString(ac));

//                                            System.out.println("bounding operation for " + ac.type + "And "+subgoal.action.type+ ActionsArgumnetsToString(ac) + " is start for " + localboundArgumentToString(localbound));
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

                                                if (boundSubgoalAction.size() > 0) {
                                                    localbound = boundSubgoalAction;

                                                    if (canBoundAction(subgoal.action, localbound)) {
                                                        System.out.println("bounding operation starts... ");
                                                        System.out.println("---------- " + subgoal.action.type + ActionsArgumnetsToString(subgoal.action) + " to " + localboundArgumentToString(localbound));

                                                        boundAction(p, subgoal.action, localbound, true);
                                                        System.out.println("actin bounds : ");
                                                        System.out.println("----------" + subgoal.action.type + ActionsArgumnetsToString(subgoal.action));

//                                            System.out.println("bounding operation for " + ac.type + "And "+subgoal.action.type+ ActionsArgumnetsToString(ac) + " is start for " + localboundArgumentToString(localbound));
                                                    } else {
                                                        // find in weakthread
                                                        for (ConstraintThread consthread : this.consthread) {
                                                            for (Variable localbound1 : localbound) {
                                                                if ((consthread.variable.name == null ? localbound1.name == null : consthread.variable.name.equals(localbound1.name)) && consthread.thread.action == subgoal.action) {
//                                                        
                                                                    p.threat.add(consthread.thread);
                                                                }
                                                            }

                                                        }

                                                    }
                                                }

                                                CYCLECHECK = cycleCheck(p, ac, subgoal.action);
                                                if (CYCLECHECK == false) {
                                                    System.out.println("program find a cycle if we add the link so try another ways.");
                                                } else {
                                                    Link link = new Link();
                                                    link.provider = ac;
                                                    link.receiver = subgoal.action;
                                                    link.condition = subgoal.state;
                                                    subgoalRemover(p, link.provider, link.receiver);
                                                    p.link.add(link);
                                                    System.out.println(" Link Added : ");
                                                    System.out.println("              " + "link provider : " + link.provider.type + ActionsArgumnetsToString(link.provider));
                                                    System.out.println("              " + "link receiver  : " + link.receiver.type + ActionsArgumnetsToString(link.receiver));
                                                    System.out.println("              " + "link condition: " + link.condition.predicate + StateArgumenttoString(link.condition));
                                                    internalFound = true;
                                                    notFoundAction = false;
                                                    System.out.println("Start Finding Thread for Internal Actions: ");
                                                    findThread(p, backUpPlan, ac, link, internalFound);

                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //  reorderOperators choose

                reorderOperators();
                //External
                if (notFoundAction) {
                    System.out.println("Internal Action Rejected.");
                    System.out.println("");
                    System.out.println("Start Searching an operators Satisfies the subgoal...");
                    for (int i = 0; i < operators.size(); i++) {
                        for (int j = 0; j < operators.get(i).adds.size(); j++) {
                            if (notFoundAction) {
                                if (operators.get(i).adds.get(j).predicate.equalsIgnoreCase(subgoal.state.predicate)) {

                                    //                            ac = copyAction(operators.get(i));
                                    //                            ac = new Action(operators.get(i));
                                    ac = copyAction(operators.get(i));
                                    orderingOperators(i);
                                    System.out.println("operators selected and goes for bounding. the operator is: ");
                                    System.out.println("---------- " + ac.type + ActionsArgumnetsToString(ac));

                                    ArrayList<Variable> arrvar = new ArrayList<>();
                                    for (int k = 0; k < ac.adds.get(j).numberOfArg; k++) { // tedade argument ha
                                        Variable v = new Variable();
                                        v.value = subgoal.state.arguments.get(k).value;
                                        v.name = ac.adds.get(j).arguments.get(k).name;
                                        arrvar.add(v);
//                                    ac.arguments.get(k).value = v.value;

                                    }

                                    localbound.addAll(arrvar);
                                    bounded.addAll(ac.arguments);

                                    notFoundAction = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (localbound.isEmpty()) {
                        System.out.println("Nothing for Bounding");

                    } else {
                        System.out.println("bounding operation for " + ac.type + ActionsArgumnetsToString(ac) + " to " + localboundArgumentToString(localbound));

//                    System.out.println("bounding operation for action is start for " + localboundArgumentToString(localbound));
                        boundAction(p, ac, localbound, false);
                        System.out.println("actin bounds : ");
                        System.out.println("---------- " + ac.type + ActionsArgumnetsToString(ac));

//                    boundAction(p, subgoal.action, localbound, false);
                    }
                    // action inja bound shoode ast 
                    Link link = new Link();
//                link.provider = new Action(ac);
                    link.provider = ac;
                    link.receiver = subgoal.action;
                    link.condition = subgoal.state;
                    subgoalRemover(p, link.provider, link.receiver);
                    p.link.add(link);
                    System.out.println(" Link Added : ");
                    System.out.println("---------- " + "link provider : " + link.provider.type + ActionsArgumnetsToString(link.provider));
                    System.out.println("---------- " + "link receiver  : " + link.receiver.type + ActionsArgumnetsToString(link.receiver));
                    System.out.println("---------- " + "link condition: " + link.condition.predicate + StateArgumenttoString(link.condition));

                    p.subgoal.addAll(ac.preconditions.subgoals); // age subgoali bashe ke bound nabashe chi mishe?????
                    addedSubgoal = ac.preconditions.subgoals;
                    System.out.println(ac.preconditions.subgoals.size() + " new Subgoals added as bellow: ");
                    System.out.println(subgoalToString(ac.preconditions.subgoals));
                    p.step.add(ac);

                    notFoundAction = false;
                    System.out.println("find thread for new action start... : ");
                    findThread(p, backUpPlan, ac, link, internalFound);
                    CYCLECHECK = true;
                }
                resolvThread(p);
                pop(p);
                return;
            }
        }
    }

    public boolean stateEquality(State st1, State st2) throws IOException {
        if (st1.predicate.equalsIgnoreCase(st2.predicate) && st1.numberOfArg == st2.numberOfArg) {
            int eqcheck = 0;
            for (int i = 0; i < st1.numberOfArg; i++) {
                if (st1.arguments.get(i).value != null && st1.arguments.get(i).value != null && st1.arguments.get(i).value == st2.arguments.get(i).value) {
                    eqcheck++;
                } else if (st1.arguments.get(i).value == null && st2.arguments.get(i).value == null) {
                    eqcheck++;
                } else if (st1.arguments.get(i).value == null) {
                    st1.arguments.get(i).constraints.add(st2.arguments.get(i));
                    System.out.println("Constrain added for predict thread " + st1.arguments.get(i).name + " not to be " + st2.arguments.get(i).value.toString());
                } else if (st2.arguments.get(i).value == null) {
                    st2.arguments.get(i).constraints.add(st1.arguments.get(i));
                    System.out.println("Constrain added for predict thread " + st2.arguments.get(i).name + " not to be " + st1.arguments.get(i).value.toString());
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
            s = s.concat(StateArgumenttoString(sublist.get(i).state));
//            s = s.concat(" for action " + sublist.get(i).action.type + " ");
//            s = s.concat(ActionsArgumnetsToString(sublist.get(i).action));
            s = s.concat("\n\r");
        }

        return s;

    }

    public void findThread(Plan p, Plan backUpPlan, Action ac, Link link, boolean internalfound) throws IOException {
        System.out.println("Thread finding Starts ....");
        if (internalfound) {
//            System.out.println("Internal link from" + link.provider.type + ActionsArgumnetsToString(link.provider) + " to " + link.receiver.type + ActionsArgumnetsToString(link.receiver) + " for state " + link.condition.predicate + StateArgumenttoString(link.condition));
            ArrayList<Action> notPermit = track(p, link);
            for (Action non : notPermit) {
//                System.out.println(non.type + ActionsArgumnetsToString(non) + " has Relation to link  from  " + link.provider.type + ActionsArgumnetsToString(link.provider) + " to " + link.receiver.type + ActionsArgumnetsToString(link.receiver));
            }
            for (int i = 0; i < p.step.size(); i++) {

                if (!notPermit.contains(p.step.get(i))) {

                    if (p.step.get(i) != link.provider && p.step.get(i) != link.receiver) {
                        for (int j = 0; j < p.step.get(i).deletes.size(); j++) {
                            if (stateEquality(p.step.get(i).deletes.get(j), link.condition) && p.step.get(i) != link.provider && p.step.get(i) != link.receiver && link.provider != link.receiver) {
                                ac = p.step.get(i);
                                System.out.println("Thread find :");
                                System.out.println("Thread Link :" + link.provider.type + ActionsArgumnetsToString(link.provider) + " to " + link.receiver.type + ActionsArgumnetsToString(link.receiver));
                                System.out.println("thread action is :" + ac.type + ActionsArgumnetsToString(ac));
                                System.out.println("thread condition : " + link.condition.predicate + StateArgumenttoString(link.condition));
//                                System.out.println("Ignore all conditions on this link and action ");
                                Threat thread = new Threat();
                                thread.link = link;
                                thread.action = ac;
                                thread.state = link.condition;

                                p.threat.add(thread);
                                break;

                            }

                        }
                    }
                }
            }

        } else { // for externall add
//            System.out.println("external action  :" + ac.type + ActionsArgumnetsToString(ac));
            ArrayList<Action> notPermit = forwardChecking(p, ac);

            for (int j = 0; j < backUpPlan.link.size(); j++) {
                if (!notPermit.contains(p.link.get(j).provider)) {
                    for (int i = 0; i < ac.deletes.size(); i++) {

                        if (p.link.get(j).condition.predicate.equals(ac.deletes.get(i).predicate) && ac != p.link.get(j).provider && ac != p.link.get(j).receiver && p.link.get(j).provider != p.link.get(j).receiver) {
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
                                    v.name = ac.deletes.get(i).arguments.get(k).name;
                                    indexOfVariable = ac.arguments.indexOf(v);
                                    System.out.println("Constrain added for action :" + ac.type + " " + ActionsArgumnetsToString(ac) + " Constrain is not equlity for " + variableToString(v));
//                                    System.out.println("but now i dont consider constrain");
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
//                            System.out.println("Hard Thread Found ( with null check ) :");
                                    thread.link = p.link.get(j);
                                    thread.action = ac;
                                    thread.state = ac.deletes.get(i);
                                    System.out.println("Thread find :");
                                    System.out.println("Thread Link :" + thread.link.provider.type + ActionsArgumnetsToString(thread.link.provider) + " to " + thread.link.receiver.type + ActionsArgumnetsToString(thread.link.receiver));
                                    System.out.println("thread action is :" + thread.action.type + ActionsArgumnetsToString(thread.action));
                                    System.out.println("thread condition : " + thread.state.predicate + StateArgumenttoString(thread.state));
//                                System.out.println("mikham bebinam chiye  : " + thread.link.condition.predicate + StateArgumenttoString(thread.link.condition));

                                    System.out.println("Ignore all conditions on this link and action ");

                                    p.threat.add(thread);
                                    break; // age chand ta thread az ye action roye ye link rokh dad , faghat yekisho mizare to thread ha , badan mishe behtaresh kard.
                                } else {
                                    ac.arguments.get(indexOfVariable).constraints.addAll(constrain);

                                    System.out.println("weak Thread find : Resolv by constarin");
                                    System.out.println("Thread Link :" + p.link.get(j).provider.type + ActionsArgumnetsToString(p.link.get(j).provider) + " to " + p.link.get(j).receiver.type + ActionsArgumnetsToString(p.link.get(j).receiver));
                                    System.out.println("thread action is :" + ac.type + ActionsArgumnetsToString(ac));
                                    System.out.println("thread condition : " + ac.deletes.get(i).predicate + " must not equals to " + constrainToString(ac.arguments.get(i)));

                                }
                            }

                        }

                    }
                }
            }

        }
        if (p.threat.isEmpty()) {
            System.out.println("no thread found");
        }

    }

    public void resolvThread(Plan p) throws IOException {
        // resolve threads 
        System.out.println("Thread Resolver Starts ....  ");
        if (p.threat.isEmpty()) {
            System.out.println("There is no thread to Resolve");
        } else {
//            System.out.println("Thread Resolve as below");
            for (Threat thread : p.threat) {
                Ordering o = new Ordering();
                if (thread.action != init && thread.action != goal) {
                    if (thread.link.receiver == goal) {
                        o.before = thread.action;
                        o.after = thread.link.provider;

                    } else if (thread.link.provider == init) {
                        o.before = thread.link.receiver;
                        o.after = thread.action;
                    } else {  // inja bayad ezafe kard taghire constarin
                        Random r = new Random();
                        double rand = r.nextDouble();
                        rand = 0.25;
                        if (rand < 0.5) {
                            o.before = thread.action;
                            o.after = thread.link.provider;

                        } else {
                            o.before = thread.link.receiver;
                            o.after = thread.action;
                        }
                    }
                } else if (thread.action == init) {

                    o.before = init;
                    o.after = thread.link.provider;

                }

                System.out.println(" action " + o.before.type + ActionsArgumnetsToString(o.before) + " comes before action " + o.after.type + ActionsArgumnetsToString(o.after));
                CYCLECHECK = cycleCheck(p, o.before, o.after);
                if (CYCLECHECK) {
                    if (!p.ordering.contains(o)) {
                        p.ordering.add(o);
                    }

                } else {
                    p.step.remove(p.step.size() - 1);
                    p.link.remove(p.link.size() - 1);
                    p.subgoal.removeAll(addedSubgoal);
                    if (!p.subgoal.contains(subgoal)) {
                        p.subgoal.add(subgoal);
                    }
                    System.out.println("All Operation Failed in this turn of loop becuase adding order cause a loop.");
                    System.out.println("the plan back to previous state and try another way to find solution.");
                    pop(p);
                    return;
                }

            }
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
//        Random r = new Random();
//        for (int i = 1; i < subgoals.size(); i++) {
//            int numOfNull = 1;
//            for (int j = 0; j < subgoals.get(i).state.numberOfArg; j++) {
//                if (subgoals.get(i).state.arguments.get(j).value == null) {
//                    numOfNull++;
//                }
//
//            }
//
//            if (subgoals.get(i).state.numberOfArg * r.nextInt() * numOfNull < subgoals.get(i - 1).state.numberOfArg * r.nextInt() * numOfNull) {
//
//                s = subgoals.get(i);
//                index = i;
//
//            }
//        }
        subgoals.remove(index);
        return s;
    }

    public Action boundAction(Plan p, Action ac, ArrayList<Variable> localbound, boolean internalselect) throws IOException {
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
                                    System.out.println("actin bounds : " + ac2.type + ActionsArgumnetsToString(ac2));

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
        String s = "(";
        for (int i = 0; i < ac.arguments.size(); i++) {
            s = s.concat("");
            s = s.concat(ac.arguments.get(i).name);
            s = s.concat(": ");
            if (ac.arguments.get(i).value != null) {
                s = s.concat(ac.arguments.get(i).value.toString());
            } else {
                s = s.concat("null");
            }
            if (!ac.arguments.get(i).constraints.isEmpty()) {
                for (int j = 0; j < ac.arguments.get(i).constraints.size(); j++) {
                    s = s.concat(ac.arguments.get(i).constraints.get(j).name + " can not be equall to :");
                    s = s.concat(": ");

                    s = s.concat(ac.arguments.get(i).value.toString());
                }
            }
            if (i != ac.arguments.size() - 1) {
                s = s.concat(",");
            }

        }
        s = s.concat(")  ");
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

                if (line.contains("OPERATORS")) {

                    int numOfOperator = findLineNumber(line);

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

        }
    }

    public int predicateSize(String line) {

        int predicateSize = 0;
        // gashtan toye liste states vase fahmidane tedad parameter haee ke bayad khonde beshe
        for (int m = 0; m < states.size(); m++) {

            if (states.get(m).predicate.equalsIgnoreCase(line)) {
                predicateSize = states.get(m).numberOfArg;

            }
            //        break;
        }

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
//            v = action.arguments.get(i);
            v.name = action.arguments.get(i).name;
            v.value = action.arguments.get(i).value;
//            for(int j=0; j< action.arguments.get(i).constraints.size(); j++){
//                Variable t = new Variable();
//                 t.name = action.arguments.get(i).constraints.get(j).name;
//                 t.value = action.arguments.get(i).constraints.get(j).value;
//                 v.constraints.add(t);
//            }
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
//                v = action.deletes.get(i).arguments.get(j);
                v.name = action.deletes.get(i).arguments.get(j).name;
                v.value = action.deletes.get(i).arguments.get(j).value;
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
//                v = action.adds.get(i).arguments.get(j);
                v.name = action.adds.get(i).arguments.get(j).name;
                v.value = action.adds.get(i).arguments.get(j).value;
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
//                v = action.preconditions.subgoals.get(i).state.arguments.get(j);
                v.name = action.preconditions.subgoals.get(i).state.arguments.get(j).name;
                v.value = action.preconditions.subgoals.get(i).state.arguments.get(j).value;
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
            for (j = 0; j < random.size() - 1; j++) {
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
            d = d * this.operators.get(i).preconditions.subgoals.size() * (i + 4);
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

        try (BufferedReader problem = new BufferedReader(new FileReader("src/pop/sussman-anomaly.txt"))) {
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

        }
    }

    public String localboundArgumentToString(ArrayList<Variable> localbound) {
        String s = "";
        for (int i = 0; i < localbound.size(); i++) {
            s = s.concat(localbound.get(i).name);
            s = s.concat(": ");
            if (localbound.get(i).value != null) {
                s = s.concat(localbound.get(i).value.toString());
            } else {
                s = s.concat(" null ");
            }
            s = s.concat(" ");
        }
        return s;
    }

    private boolean canBoundAction(Action ac, ArrayList<Variable> localbound) {
        boolean f = true;
        if (localbound == null) {
            return true;
        }
//        for (Variable argument : ac.arguments) {
//            for (Variable constraint : argument.constraints) {
//                for (Variable localbound1 : localbound) {
//                    if (localbound1.name.equals(argument.name)) {
//                        if (constraint == localbound1) {
//                            f = false;
//                            return f;
//                        }
//                    }
//                }
//            }
//        }

        if (ac.arguments.isEmpty()) {
            ArrayList<String> avail = availObject(ac, 0);
            return true;
        } else {
            for (int i = 0; i < ac.arguments.size(); i++) {
                ArrayList<String> avail = availObject(ac, i);
                for (int j = 0; j < localbound.size(); j++) {
                    if (ac.arguments.get(i).name.equalsIgnoreCase(localbound.get(j).name)) {
                        if (!avail.contains(localbound.get(j).value)) {
                            return false;
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

    public void printLastPlan(Plan p) throws FileNotFoundException, UnsupportedEncodingException, IOException {
//        PrintWriter writer = new PrintWriter("src/pop/output/last.txt", "UTF-8");
        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("The Last Plan is : ");

        System.out.println("STEPS :");
//        System.out.print(p.step.get(0).type + "()" + " -> ");
        for (int i = 0; i < p.step.size(); i++) {
            if (i == p.step.size()) {
                System.out.print(p.step.get(i).type + ActionsArgumnetsToString(p.step.get(i)));

            }
            System.out.print(p.step.get(i).type + ActionsArgumnetsToString(p.step.get(i)) + " -> ");
        }
//        System.out.println(p.step.get(1).type + "()");
        System.out.println("");

        System.out.println("Links :");
        for (int i = 0; i < p.link.size(); i++) {
            if (i > 1 && p.link.get(i).provider == p.link.get(i - 1).provider && p.link.get(i).receiver == p.link.get(i - 1).receiver) {
                System.out.print(" and " + p.link.get(i).condition.predicate + StateArgumenttoString(p.link.get(i).condition));

            } else {
                System.out.println("");
                System.out.print("            " + p.link.get(i).provider.type + ActionsArgumnetsToString(p.link.get(i).provider) + " -> " + p.link.get(i).receiver.type + ActionsArgumnetsToString(p.link.get(i).receiver) + "for state : " + p.link.get(i).condition.predicate + StateArgumenttoString(p.link.get(i).condition));
            }
        }
        System.out.println("");
        System.out.println("ORDERING :");
        Object[] ord = p.ordering.toArray();
        for (int i = 0; i < ord.length; i++) {
            Ordering o = (Ordering) ord[i];
            System.out.println("            " + o.before.type + "(" + ActionsArgumnetsToString(o.before) + ")" + " -> " + o.after.type + "(" + ActionsArgumnetsToString(o.after) + ")");
        }

    }

    public String StateArgumenttoString(State state) {
        String s = "(";
        for (int i = 0; i < state.numberOfArg; i++) {
            s = s.concat("");
            s = s.concat(state.arguments.get(i).name);
            s = s.concat(": ");
            if (state.arguments.get(i).value != null) {
                s = s.concat(state.arguments.get(i).value.toString());
            } else {
                s = s.concat("null");
            }
            if (!state.arguments.get(i).constraints.isEmpty()) {
                for (int j = 0; j < state.arguments.get(i).constraints.size(); j++) {
                    s = s.concat(state.arguments.get(i).constraints.get(j).name + " can not be equall to :");
                    s = s.concat(": ");
                    if (state.arguments.get(i).value == null) {
                        s = s.concat("null");
                    } else {
                        s = s.concat(state.arguments.get(i).value.toString());
                    }
                }
            }
            if (i != state.numberOfArg - 1) {
                s = s.concat(" , ");
            }

        }
        s = s.concat(")  ");
        return s;
    }

    public String variableToString(Variable v) {

        String s = "(";
        s = s.concat(v.name);
        s = s.concat(" ");
        if (v.value != null) {
            s = s.concat(v.value.toString());
        } else {
            s = s.concat("null");
        }
        s = s.concat(")");
        return s;
    }

    public ArrayList<Action> track(Plan p, Link link) throws IOException {
        Object[] order = p.ordering.toArray();
        ArrayList<Boolean> selectedOrder = new ArrayList<>();
        ArrayList<Boolean> selected = new ArrayList<>();
        ArrayList<Boolean> selectedForward = new ArrayList<>();
        ArrayList<Boolean> selectedBackward = new ArrayList<>();
        ArrayList<Action> forward = new ArrayList<>();
        ArrayList<Action> backward = new ArrayList<>();
        ArrayList<Action> inTrackOfLink = new ArrayList<>();

        for (int i = 0; i < p.step.size(); i++) {
            selectedOrder.add(false);
            selectedForward.add(false);
            selectedBackward.add(false);
            selected.add(false);
        }

        Action ac1 = link.provider;
        Action ac2 = link.receiver;

        selected.set(p.step.indexOf(ac1), true);
        selected.set(p.step.indexOf(ac2), true);

//        }
        try {
            for (int i = 0; i < p.ordering.size(); i++) {
                Ordering o = (Ordering) order[i];
                if (o.after == ac1 || o.after == ac2) {

                    selectedOrder.set(p.step.indexOf(o.before), true);

                }
                if (o.before == ac1 || o.before == ac2) {

                    selectedOrder.set(p.step.indexOf(o.after), true);

                }
            }

            forward.add(ac2);
            for (int j = 0; j < p.link.size(); j++) {   // hadaxar tedad level haee ke mitone now roshd kone
                ArrayList<Action> now = new ArrayList<>();

                for (Action a : forward) {
                    for (int i = 0; i < p.link.size(); i++) {

                        if (p.link.get(i).provider == a) {

                            if (selectedForward.get(p.step.indexOf(p.link.get(i).receiver)) == false) {
                                selectedForward.set(p.step.indexOf(p.link.get(i).receiver), true);

                                now.add(p.link.get(i).receiver);
                            }

                        }

                    }
//            for (Action at : now) {
//                if (!forward.contains(at)) {
//                    forward.add(at);
//                }
//            }

                }
                forward = now;
            }
            backward.add(ac1);
            for (int j = 0; j < p.link.size(); j++) {
                ArrayList<Action> now = new ArrayList<>();

                for (Action a : backward) {

                    for (int i = 0; i < p.link.size(); i++) {

                        if (p.link.get(i).provider == a) {

                            if (selectedBackward.get(p.step.indexOf(p.link.get(i).receiver)) == false) {
                                selectedBackward.set(p.step.indexOf(p.link.get(i).receiver), true);

                                now.add(p.link.get(i).receiver);
                            }

                        }

                    }
//            for (Action at : now) {
//                if (!backward.contains(at)) {
//                    backward.add(at);
//                }
//            }

                }
                backward = now;
            }
            for (int i = 0; i < p.step.size(); i++) {
                if (selected.get(i) == true || selectedOrder.get(i) == true || selectedBackward.get(i) == true || selectedForward.get(i) == true) {
                    inTrackOfLink.add(p.step.get(i));
                }
            }
        } catch (Exception e) {
            main(null);

        }
        return inTrackOfLink;
    }

    public boolean cycleCheck(Plan p, Action search, Action start) { // first time last just contain search action 
        ArrayList<Ordering> allord = new ArrayList<>();
        Object[] order = p.ordering.toArray();

        for (int i = 0; i < p.ordering.size(); i++) {
            allord.add((Ordering) order[i]);
        }
        for (int i = 0; i < p.link.size(); i++) {
            Ordering o = new Ordering();
            o.before = p.link.get(i).provider;
            o.after = p.link.get(i).receiver;
            if (!allord.contains(o)) {
                allord.add(o);
            }
        }

        int limit = p.step.size();
        ArrayList<Action> last = new ArrayList<>();
        last.add(search);
        for (int i = 0; i < limit; i++) { // hadaxar size halghe
            ArrayList<Action> now = new ArrayList<>();
            for (Ordering ord : allord) {
                for (Action ac : last) {
                    if (ord.before == ac) {
                        now.add(ord.after);
                    }
                }
            }
            if (now.contains(search)) {
                return false;

            }
            last = now;
        }

        return true;
    }

    public Action operatorsOfAction(Action ac) {
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).type.equalsIgnoreCase(ac.type)) {
                return operators.get(i);
            }
        }

        return null;

    }

    public void writeToFile(String s) throws IOException {
        System.out.println(s);

    }

    public void writeToFileInLine(String s) throws IOException {
        System.out.println(s);

    }

    public void subgoalRemover(Plan p, Action ac1, Action ac2) throws IOException {
        System.out.println("Clear other Subgoals for " + ac2.type + " that satisfy by this link...");
        for (int i = 0; i < ac1.adds.size(); i++) {
            for (int k = 0; k < ac2.preconditions.subgoals.size(); k++) {

                if (ac1.adds.get(i).predicate.equalsIgnoreCase(ac2.preconditions.subgoals.get(k).state.predicate)) {
                    int equality1 = 0;
                    int equality = 0;
                    ArrayList<Variable> localboundac1 = new ArrayList<>();
                    ArrayList<Variable> localboundac2 = new ArrayList<>();

                    for (int j = 0; j < ac1.adds.get(i).numberOfArg; j++) {
                        if (ac1.adds.get(i).arguments.get(j).value == null && ac2.preconditions.subgoals.get(k).state.arguments.get(j).value == null) {

//                            equality1++;
//                            2 tash null bashe sakht mishe constrain bezaram ke 2 tasho barabar kone , bikhialesh misham.
                        } else if (ac1.adds.get(i).arguments.get(j).value == null && ac2.preconditions.subgoals.get(k).state.arguments.get(j).value != null) {
                            Variable v = new Variable();
                            v = ac1.adds.get(i).arguments.get(j);
                            v.value = ac2.preconditions.subgoals.get(k).state.arguments.get(j).value;
                            localboundac1.add(v);

                            equality1++;

                        } else if (ac1.adds.get(i).arguments.get(j).value != null && ac2.preconditions.subgoals.get(k).state.arguments.get(j).value == null) {
                            Variable v = new Variable();
                            v = ac2.preconditions.subgoals.get(k).state.arguments.get(j);
                            v.value = ac1.adds.get(i).arguments.get(j).value;
                            localboundac2.add(v);

                            equality1++;
                        } else if (ac1.adds.get(i).arguments.get(j).value.equalsIgnoreCase(ac2.preconditions.subgoals.get(k).state.arguments.get(j).value)) {

                            equality1++;

//                            ye bounding mitone inja bashe
                        }
                        equality++;
                    }
                    Action tempActionac1 = init; //har actioni mitone bashe, faghat mikhastam  null nabashe 
                    Action tempActionac2 = init; //har actioni mitone bashe, faghat mikhastam  null nabashe 

                    if (equality == equality1) {
                        if (!localboundac1.isEmpty()) {
                            tempActionac1 = boundAction(p, ac1, localboundac1, true);

                            System.out.println("actin bounds : " + ac1.type + ActionsArgumnetsToString(ac1));
                        }
                        if (!localboundac2.isEmpty()) {
                            tempActionac2 = boundAction(p, ac2, localboundac2, true);
                            System.out.println("actin bounds : " + ac2.type + ActionsArgumnetsToString(ac2));
                        }
                        if (tempActionac1 != null && tempActionac2 != null) {
                            for (int w = 0; w < p.subgoal.size(); w++) { // shak darammmmmmmmmmmmmmmmmmmmmmmmmmmmmm be inke peyda beshe
                                if (p.subgoal.get(w).action == ac2 && p.subgoal.get(w).state == ac2.preconditions.subgoals.get(k).state) {
                                    Link link = new Link();
                                    link.condition = p.subgoal.get(w).state;
                                    link.provider = ac1;
                                    link.receiver = ac2;
                                    p.link.add(link);

                                    System.out.println(" Link Added in aditional subgoal remover: ");
                                    System.out.println("              " + "link provider : " + link.provider.type + ActionsArgumnetsToString(link.provider));
                                    System.out.println("              " + "link receiver  : " + link.receiver.type + ActionsArgumnetsToString(link.receiver));
                                    System.out.println("              " + "link condition: " + link.condition.predicate + StateArgumenttoString(link.condition));

                                    System.out.println("Subgoal removed : " + p.subgoal.get(w).state.predicate + " in action : " + ac2.type + ActionsArgumnetsToString(p.subgoal.get(w).action));
                                    p.subgoal.remove(w);
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        }
        System.out.println("end of removing subgoals.");

    }

    public boolean inSubgoalRemove(ArrayList<Variable> localbound) {
        for (int i = 0; i < localbound.size(); i++) {
            for (int j = 0; j < localbound.size(); j++) {
                if (i != j && localbound.get(i).name == localbound.get(j).name) {
                    return false;
                }
            }
        }
        return true;

    }

    public void orderingOperators(int i) {
        Action last = this.operators.get(i);
        for (int j = i; j < operators.size() - 1; j++) {
            this.operators.set(j, this.operators.get(j + 1));
        }
        this.operators.set(operators.size() - 1, last);

    }

    public ArrayList<Action> forwardChecking(Plan p, Action ac) {
        Object[] order = p.ordering.toArray();
        ArrayList<Boolean> selectedOrder = new ArrayList<>();
        ArrayList<Boolean> selected = new ArrayList<>();
        ArrayList<Boolean> selectedForward = new ArrayList<>();

        ArrayList<Action> forward = new ArrayList<>();

        ArrayList<Action> inTrackOfLink = new ArrayList<>();

        for (int i = 0; i < p.step.size(); i++) {
            selectedOrder.add(false);
            selectedForward.add(false);

            selected.add(false);
        }

        selected.set(p.step.indexOf(ac), true);

//        }
        for (int i = 0; i < p.ordering.size(); i++) {
            Ordering o = (Ordering) order[i];

            if (o.before == ac) {

                selectedOrder.set(p.step.indexOf(o.after), true);

            }
        }

        forward.add(ac);
        for (int j = 0; j < p.link.size(); j++) {
            ArrayList<Action> now = new ArrayList<>();

            for (Action a : forward) {
                for (int i = 0; i < p.link.size(); i++) {

                    if (p.link.get(i).provider == a) {

                        if (selectedForward.get(p.step.indexOf(p.link.get(i).receiver)) == false) {
                            selectedForward.set(p.step.indexOf(p.link.get(i).receiver), true);

                            now.add(p.link.get(i).receiver);
                        }

                    }

                }

            }
            forward = now;
        }
        for (int i = 0; i < p.step.size(); i++) {
            if (selected.get(i) == true || selectedOrder.get(i) == true || selectedForward.get(i) == true) {
                inTrackOfLink.add(p.step.get(i));
            }
        }
        return inTrackOfLink;
    }

    public void findSolution(Plan p) throws IOException {
        ArrayList<Integer> freeAction = new ArrayList<>();
//        ArrayList<Boolean> usedLink = new ArrayList<>();
        ArrayList<Action> actions = new ArrayList<>();
//        Plan p = clone(p);
        ArrayList<Action> planSteps = new ArrayList<>();

        for (int i = 0; i < p.link.size(); i++) {
            for (int j = 0; j < p.link.size(); j++) {
                if (i != j && p.link.get(i).provider == p.link.get(j).provider && p.link.get(i).receiver == p.link.get(j).receiver) {
//                    p.link.remove(j);
                }
            }
        }

        Object[] order = p.ordering.toArray();
        for (int i = 0; i < p.step.size(); i++) {
            freeAction.add(0);
        }
//        for (int i = 0; i < p.link.size(); i++) {
//            usedLink.add(false);
//        }
        int w=0;
        for (int i = 0; i < p.link.size(); i++) {

            int k = p.step.indexOf(p.link.get(i).receiver);
            
            try {

                w = freeAction.get(k) + 1;
                freeAction.set(k, w);
            } catch (Exception e) {
                printLastPlan(p);
                System.out.println("k: "+k + " - type: " +p.link.get(i).receiver.type + ActionsArgumnetsToString(p.link.get(i).receiver)+ " - w: "+ w);;
                System.exit(0);
            }
        }

        for (int i = 0; i < p.ordering.size(); i++) {
            Ordering o = (Ordering) order[i];
            int k = p.step.indexOf(o.after);
            try {

                w = freeAction.get(k) + 1;
                freeAction.set(k, w);
            } catch (Exception e) {
                 printLastPlan(p);
                System.out.println("k: "+k + " - "+o.after.type+ActionsArgumnetsToString(o.after) + " - w: "+ w);;
                System.exit(0);
            }
        }
//        Action ac = new Action();
//        ArrayList<Link> availLink = new ArrayList<>();
        int limit = 0;
        actions.add(init);
//         for (int i = 1; i < p.step.size(); i++) {
//            if (freeAction.get(i) == 0) {
//                actions.add(p.step.get(i));
//            }
//        }
//        System.out.println("one of solution is \n\n");

        outer:

        while (!actions.isEmpty()) {
//            if (limit == 0) {
//                ac = p.step.get(0);
//            } else {
//                for (int i = 0; i < p.step.size(); i++) {
//                    if(freeAction.get(i) <= 0){
//                        ac = p.step.get(i);
//                    }
//                }
//            }
            Action ac = actions.get(0);
            planSteps.add(ac);
            actions.remove(0);
//            limit++;
            for (int i = 0; i < p.link.size(); i++) {
//                if (usedLink.get(i) == true) {
//                    continue;
//                }
                if (ac == goal) {
//                    break outer;
                }
                int indexOfStep = p.step.indexOf(p.link.get(i).receiver);
                if (p.link.get(i).provider == ac && freeAction.get(indexOfStep) != 0) {
//                    usedLink.set(i, true);
//                    System.out.println(ac.type + ActionsArgumnetsToString(ac));
//                    planSteps.add(ac);

                    int q = freeAction.get(indexOfStep) - 1;
                    freeAction.set(indexOfStep, q);

                    if (freeAction.get(indexOfStep) == 0) {
                        actions.add(p.link.get(i).receiver);
//                        System.out.println(ac.type + ActionsArgumnetsToString(ac));

//                        planSteps.add(ac);
                    }

//                    ac = p.link.get(i).receiver;
//                    if (freeAction.get(p.step.indexOf(p.link.get(i).receiver)) == 0) {
//                        actions.add(p.link.get(i).receiver);
//                    }
                }

            }
            for (int m = 0; m < p.ordering.size(); m++) {
                Ordering o = (Ordering) order[m];
                if (ac == o.before) {
                    int k = p.step.indexOf(o.after);
                    w = freeAction.get(k) - 1;
                    freeAction.set(k, w);

                    if (freeAction.get(k) == 0) {
                        actions.add(o.after);
                    }
                }

            }
        }
//if(planSteps.size() != p.step.size()){
//    main(null);
//}

        System.out.println(" One Of Solution is  :");

        for (int i = 0; i < planSteps.size(); i++) {
                            System.out.println(planSteps.get(i).type + ActionsArgumnetsToString(planSteps.get(i)));

//            if (i == 0) {
//                System.out.println(planSteps.get(i).type + ActionsArgumnetsToString(planSteps.get(i)));
//            } else if (i > 1 && planSteps.get(i) != planSteps.get(i - 1)) {
//                System.out.println(planSteps.get(i).type + ActionsArgumnetsToString(planSteps.get(i)));
//                if (planSteps.get(i) == goal) {
////                    break;
//                }
//            } else {
//                continue;
//            }

        }
        writer.close();
    }

}
