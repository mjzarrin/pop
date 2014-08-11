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
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author vahid
 */
public class Pop {

    /**
     * @param args the command line arguments System.out.println();
     */
    ArrayList<State> states = new ArrayList<>();
    ArrayList<Action> operators = new ArrayList<>();
    ArrayList<String> objects = new ArrayList<>();
    ArrayList<Variable> bounded = new ArrayList<>();
    Action init = new Action();
    Action goal = new Action();
//    Subgoal subgoal = new Subgoal();
    Plan plan;

    public static void main(String[] args) throws IOException {
        // read domain
        Pop popObject = new Pop();

        popObject.domain();
        popObject.problem();
        Plan plan = popObject.makeinitialplan();
        Plan lastPlan = popObject.pop(plan);
        // list of all predicators                                                                          Done
        // list of all actions                                                                              Done
        // read problem                 //done
        // list of all object           //done
        // make null plan from initial va end actoion    
        System.err.println(lastPlan.toString());
        //     plan.start= init;
        // call popObject
    }

//    public void choice(){
//        
//        subgoal = plan.subgoal.get(0);
//        plan.subgoal.remove(0);  
//    }
    public Plan pop(Plan p) {

        //1- terminatrin //size subgoal ==0
        if (p.subgoal.isEmpty()) {

            System.out.println("Fuck");
            return p;

        } else {
//            get back up of p
            Plan backUpPlan = clone(p);

            //2- select from subgoals (choice)
            Subgoal subgoal = p.subgoal.get(0);
            p.subgoal.remove(0);

            Action ac = new Action();

            boolean flag = true; // for internal select or external

            // internal choose action
            if (flag) {

                for (int i = 0; i < p.step.size(); i++) {
                    for (int j = 0; j < p.step.get(i).adds.size(); j++) {
                        if (p.step.get(i).adds.get(j).predicate.equalsIgnoreCase(subgoal.state.predicate)) {
                            int temp = 0;
                            int k;
                            for (k = 0; k < subgoal.state.numberOfArg; k++) {
                                if (p.step.get(i).adds.get(j).arguments.get(k).value.equals(subgoal.state.arguments.get(k).value)) {
                                    temp++;
                                } else {
                                    break;
                                }
                            }
                            if (temp == k - 1) {
//                                Action ac = null;
                                ac = p.step.get(i);

                                Link link = new Link();
                                link.provider = ac;
                                link.reciver = subgoal.action;

                                p.link.add(link);

                                Ordering order = new Ordering();
                                order.before = ac;
                                order.after = subgoal.action;
                                p.ordering.add(order);

                                flag = false;
                            }

                        }
                    }
                }
            }
            //  external choose
            ArrayList<Variable> localbound = new ArrayList<>();
            if (flag) {
                for (int i = 0; i < operators.size(); i++) {
                    for (int j = 0; j < operators.get(i).adds.size(); j++) {
                        if (operators.get(i).adds.get(j).predicate.equalsIgnoreCase(subgoal.state.predicate) && flag) {

//                            ac = copyAction(operators.get(i));
                            ac = new Action(operators.get(i));
                            for (int k = 0; k < ac.adds.get(j).numberOfArg; k++) { // tedade argument ha
                                ac.adds.get(j).arguments.get(k).value = subgoal.state.arguments.get(k).value;

                            }

                            localbound.addAll(ac.adds.get(j).arguments);
                            bounded.addAll(ac.adds.get(j).arguments);
                            
                            flag = false;
                            break;

                        }
                    }
                }

                // meghdar dehi kardan be Ation
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

                // action inja bound shoode ast 
                Link link = new Link();
                link.provider = new Action(ac);
                link.reciver = subgoal.action;

                p.link.add(link);

                Ordering order = new Ordering();
                order.before = ac;
                order.after = subgoal.action;
                p.ordering.add(order);

                p.subgoal.addAll(ac.preconditions.subgoals); // age subgoali bashe ke bound nabashe chi mishe?????
//                        p.subgoal
                p.step.add(ac);
                flag = false;
//                          Action ac = subgoal.action;
                // be ezaye har chi moteghayere x hast meghdaresho az zir dar miarim
                // moteghayer felan 2 jast add va precondition 2 ta for lazem darim
                // subgoale state argument variable 
            }
// find threads 
            for (int i = 0;
                    i < ac.deletes.size();
                    i++) {
                for (int j = 0; j < backUpPlan.link.size(); j++) {

                    for (int k = 0; k < backUpPlan.link.get(j).reciver.preconditions.subgoals.size(); k++) {
                        if (backUpPlan.link.get(j).reciver.preconditions.subgoals.get(k).state.predicate.equalsIgnoreCase(ac.deletes.get(i).predicate)) {
                            for (int l = 0; l < ac.deletes.get(i).numberOfArg; l++) {
                                if (ac.deletes.get(i).arguments.get(l).value == backUpPlan.link.get(j).reciver.preconditions.subgoals.get(k).state.arguments.get(l).value) {
                                    Threat thread = new Threat();

                                    thread.link = backUpPlan.link.get(j);
                                    thread.action = ac;
                                    thread.state = ac.deletes.get(i);
                                    p.threat.add(thread);
                                    break;
                                }
                            }
                        }
                    }

                }

            }

            // resolve threads 
            for (int i = 0;
                    i < p.threat.size();
                    i++) {
                Ordering o = new Ordering();
                if (p.threat.get(i).link.reciver == goal) {
                    o.before = p.threat.get(i).action;
                    o.after = p.threat.get(i).link.provider;
                } else if (p.threat.get(i).link.provider == init) {
                    o.before = p.threat.get(i).link.reciver;
                    o.after = p.threat.get(i).action;
                } else {  // inja bayad ezafe kard taghire constarin
                    Random r = new Random();
                    double rand = r.nextDouble();
                    rand = 0.25;
                    if (rand < 0.5) {
                        o.before = p.threat.get(i).action;
                        o.after = p.threat.get(i).link.provider;

                    } else {
                        o.before = p.threat.get(i).link.reciver;
                        o.after = p.threat.get(i).action;
                    }
                }
                p.ordering.add(o);
            }

            //6- recall popObject
            pop(p);
        }
        return p;
    }

    public Plan clone(Plan p) {

        Plan backUp = new Plan();
//        backUp.start = (Action) p.start.clone();
//        backUp.end = (Action) p.end.clone();
        
        backUp.start = copyAction(init);
        backUp.end = copyAction(goal);
        backUp.link = (ArrayList<Link>) p.link.clone();
        backUp.ordering = (ArrayList<Ordering>) p.ordering.clone();
        backUp.step = (ArrayList<Action>) p.step.clone();
        backUp.subgoal = (ArrayList<Subgoal>) p.subgoal.clone();
        backUp.threat = (ArrayList<Threat>) p.threat.clone();

        return backUp;
    }

    public void domain() throws FileNotFoundException, IOException {
        try (BufferedReader domain = new BufferedReader(new FileReader("src/pop/domain.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = domain.readLine();

            while (line != null) {
//                sb.append(line);
//                sb.append(System.lineSeparator());
                if (line.contains("PREDICATES")) {

                    int numOfPredicat = findNumber(line);
//                    System.out.println("predictore : " + numOfPredicat+ " " );
                    for (int i = 0; i < numOfPredicat; i++) {
                        line = domain.readLine();
                        //////////////// make states here
                        State s = new State();
                        s.predicate = findName(line);

                        s.numberOfArg = findNumber(line);
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

                    int numOfOperator = findNumber(line);
//                    System.out.println("operator : " + numOfOperator);
                    for (int i = 0; i < numOfOperator; i++) {
                        line = domain.readLine();
                        Action ac = new Action(); // action ro meghdar dehi mikoni be operators.add mikonim
                        ac.type = line;
                        line = domain.readLine();
                        // dorost kardane action argument ac.argument

                        if (line.contains("Parameters")) {
                            int numParam = findNumber(line);
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
                            int numPrecond = findNumber(line);
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
                            int numAdds = findNumber(line);

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
                            int numDelletes = findNumber(line);
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

    public int findNumber(String line) {

//        char[] tt = line.toCharArray();
//        int size = tt.length - 1;
//         char t = tt[size];
//        int num = (int)Character.getNumericValue(t);
        String[] part = line.split(":");
        int num = (int) Integer.parseInt(part[1]);

        return num;
    }

    public String findName(String line) {

        String[] part = line.split(":");

        return part[0];
    }

    public Plan makeinitialplan() {
        Plan plan = new Plan();
        plan.start = init;
        plan.end = goal;
        ArrayList<Subgoal> list = new ArrayList<>(goal.preconditions.subgoals);
        plan.subgoal = list;
        plan.threat = new ArrayList<>();
        plan.link = new ArrayList<>();
        ArrayList<Ordering> ordering = new ArrayList<>();
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
        ac.arguments = (ArrayList<Variable>) action.arguments.clone();
        ac.deletes = (ArrayList<State>) action.deletes.clone();
        ac.preconditions = (Goal) action.preconditions.clone();
        ac.adds = (ArrayList<State>) action.adds.clone();
        return ac;

    }

    public void problem() throws FileNotFoundException, IOException {

        try (BufferedReader problem = new BufferedReader(new FileReader("src/pop/simple.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = problem.readLine();

            while (line != null) {

                if (line.contains("OBJECTS")) {
                    int numOfObjects = findNumber(line);
                    for (int i = 0; i < numOfObjects; i++) {
                        line = problem.readLine();
                        objects.add(line);

                    }
                    // i can readline 2 times or no
                }

                if (line.contains("INITIAL-STATE")) {

                    init.type = findName(line);
                    int numOfState = findNumber(line);

                    ArrayList<State> adds = new ArrayList<>();
                    for (int i = 0; i < numOfState; i++) {
                        line = problem.readLine();
                        State s = new State();
                        s.predicate = line;
                        ArrayList<Variable> arguments = new ArrayList<>();
                        //           line = problem.readLine();
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

//                                    line = problem.readLine();
//                            init.arguments.add(null)
                        }
                        s.arguments = arguments;
                        s.numberOfArg = numberOfArgs;
                        adds.add(s);
                    }
                    init.adds = adds;

                    operators.add(init);

                }
//                operators.add(init);
                if (line.contains("GOALS")) {
                    goal.type = findName(line);
                    int numOfSubGoal = findNumber(line);
                    Goal goals = new Goal();
                    ArrayList<Subgoal> subgoals = new ArrayList<>();

                    for (int i = 0; i < numOfSubGoal; i++) {

                        Subgoal subgoal = new Subgoal();
                        line = problem.readLine();
                        int numOfArgs = 0;
                        int index = 0; // index of state in list of domain state
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

//                                    line = problem.readLine();
//                            init.arguments.add(null)
                        }
                        s.arguments = arguments;
                        subgoal.state = s;
                        subgoal.action = goal;      // IIIIIIIIIInja barrasi mikhad
                        subgoals.add(subgoal);

                    }
                    goals.subgoals = subgoals;

                    goal.preconditions = goals;
                    operators.add(goal);

                }

//                sb.append(line);
//                sb.append(System.lineSeparator());
                line = problem.readLine();
            }
//            String everything = sb.toString();
//            System.out.println(everything);
        }
    }
}
