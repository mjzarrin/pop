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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
        System.out.println("POP Started");
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
//            order(p);

            return p;

        } else {
//            get back up of p
            Plan backUpPlan = clone(p);

     
            Subgoal subgoal = leastCommitmentStrategy(p.subgoal);
//            }

            System.out.println("    Subgoal : " + subgoal.state.predicate + " in action " + subgoal.action.type);

            Action ac = new Action();

            boolean notFoundAction = true; // for internal select or external
            ArrayList<Variable> localbound = new ArrayList<>();

            // internal choose action
            ArrayList<Action> internalSelect = internal(p);
            ArrayList<Action> posibleActions;
            int numOfNull;
            // opertators and steps unordering
            boolean internalFound = false;
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
                                    if (internalSelect.get(i).adds.get(j).arguments.get(k).value == (subgoal.state.arguments.get(k).value) && subgoal.state.arguments.get(k).value != null) {
                                        temp++;
                                        
                                    } else if (subgoal.state.arguments.get(k).value == null && internalSelect.get(i).adds.get(j).arguments.get(k).value != null) {

                                        Variable v = new Variable();
                                        v.name = subgoal.state.arguments.get(k).name;
                                        v.value = internalSelect.get(i).adds.get(j).arguments.get(k).value;
                                       
                       //                 if(!forbidValue.contains(v.value)){
                                            arvar.add(v);
                                             temp++;
                       //                      forbidValue.add(v.value);
                       //                 }
                                         

                                    } else if (internalSelect.get(i).adds.get(j).arguments.get(k).value == null && subgoal.state.arguments.get(k).value != null) {
                                        Variable v = new Variable();
                                        v.name = subgoal.state.arguments.get(k).name;
                                        v.value = subgoal.state.arguments.get(k).value;
                    //                    if(!forbidValue.contains(v.value)){
                                            arvar.add(v);
                                            temp++;
                     //                        forbidValue.add(v.value);
                     //                   }
                                    }else if(internalSelect.get(i).adds.get(j).arguments.get(k).value == null && subgoal.state.arguments.get(k).value == null){
                                        // inja bayad codi bezanam ke dorost bashe
                                    }                              
                                    else {
                                        break;
                                    }
                                }
                                boolean canDone = linkAndOrderTest(p, internalSelect.get(i), subgoal);
                                if ((temp == k && temp != 0) || subgoal.state.numberOfArg == 0  && canDone) {
//                                Action ac = null;

                                    ac = internalSelect.get(i);

                                    if (arvar.size() > 0) {
                                        localbound = arvar;
//                                        for (int t = 0; t < p.step.size(); t++) {
//                                            if (p.step.get(t).type.equals(subgoal.action.type)) {
//                                                int tt = 0;
//                                                int u;
//                                                for (u = 0; u < subgoal.action.preconditions.subgoals.size(); u++) {
//                                                    for (int y = 0; y < p.step.get(t).preconditions.subgoals.get(u).state.arguments.size(); y++) {
//
//                                                        if (p.step.get(t).preconditions.subgoals.get(u).state.arguments.get(y) == subgoal.action.preconditions.subgoals.get(u).state.arguments.get(y)) {
//                                                            
////                                                            shak daram #############################################################################################################################################################################################
//                                                            tt++;
//                                                        }
//                                                    }
//
//                                                }
//                                                if (tt == u) {
////                                                    boundAction(p.step.get(t), localbound);
//                                                }
//
//                                            }
//
//                                        }

                                        boundAction(p,ac, localbound);
                                    }
                                    System.out.println("add Internal Action: " + ac.type + " for state  " + subgoal.state.predicate + " - state args :" + (subgoal.state.arguments.size() > 0 ? subgoal.state.arguments.get(0).value : "") + " , " + (subgoal.state.arguments.size() > 1 ? subgoal.state.arguments.get(1).value : "") + " for : " + subgoal.state.predicate);

                                    Link link = new Link();
                                    link.provider = ac;
                                    link.reciver = subgoal.action;
                                    link.condition = subgoal.state;

                                    p.link.add(link);

//                                    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//                                    Ordering order = new Ordering();
//                                    order.before = ac;
//                                    order.after = subgoal.action;
//                                    p.ordering.add(order);
                                    internalFound = true;
                                    notFoundAction = false;
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
            //  external choose

            external();
            //
            if (notFoundAction) {
                for (int i = 0; i < operators.size(); i++) {
                    for (int j = 0; j < operators.get(i).adds.size(); j++) {
                        if (notFoundAction) {
                            if (operators.get(i).adds.get(j).predicate.equalsIgnoreCase(subgoal.state.predicate)) {

                                //                            ac = copyAction(operators.get(i));
                                //                            ac = new Action(operators.get(i));
                                ac = copyAction(operators.get(i));
                         
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

                ac = boundAction(p,ac, localbound);

                // action inja bound shoode ast 
                Link link = new Link();
                link.provider = new Action(ac);
                link.reciver = subgoal.action;
                link.condition = subgoal.state;

                p.link.add(link);

//                Ordering order = new Ordering();
//                order.before = ac;
//                order.after = subgoal.action;
//                p.ordering.add(order);
//                for(int i=0; i<ac.preconditions.subgoals.size();i++){
//                    for(int j=0; j<ac.preconditions.subgoals.get(j).state.numberOfArg;j++){
//                        if(ac.preconditions.subgoals.get(j).state.arguments.get(j).value != null){
//                            p.subgoal.add(ac.preconditions.subgoals.get(i).state);
//                        }
//               
//                    }
//                }
                p.subgoal.addAll(ac.preconditions.subgoals); // age subgoali bashe ke bound nabashe chi mishe?????
//                        p.subgoal
                p.step.add(ac);
                System.out.println("add External Action: " + ac.type + " - args :" + ac.arguments.get(0).value + " , " + (ac.arguments.size() > 1 ? ac.arguments.get(1).value : "") + " for : " + subgoal.state.predicate);
                notFoundAction = false;
                findThread(p, backUpPlan, ac, link, internalFound);

//                          Action ac = subgoal.action;
                // be ezaye har chi moteghayere x hast meghdaresho az zir dar miarim
                // moteghayer felan 2 jast add va precondition 2 ta for lazem darim
                // subgoale state argument variable 
            }
// find threads 

//            if (internalFound == false) {
//                for (int i = 0;
//                        i < ac.deletes.size();
//                        i++) {
//                    for (int j = 0; j < backUpPlan.link.size(); j++) {
//
//                        for (int k = 0; k < backUpPlan.link.get(j).reciver.preconditions.subgoals.size(); k++) {
//                            if (backUpPlan.link.get(j).reciver.preconditions.subgoals.get(k).state.predicate.equalsIgnoreCase(ac.deletes.get(i).predicate)) {
//                                for (int l = 0; l < ac.deletes.get(i).numberOfArg; l++) {
//                                    if (ac.deletes.get(i).arguments.get(l).value == backUpPlan.link.get(j).reciver.preconditions.subgoals.get(k).state.arguments.get(l).value) {
//                                        Threat thread = new Threat();
//
//                                        thread.link = backUpPlan.link.get(j);
//                                        thread.action = ac;
//                                        thread.state = ac.deletes.get(i);
//                                        System.out.println("        Thread found : in link between " + thread.link.provider.type + " to " + thread.link.reciver.type + "for state " + thread.state.predicate + " in action " + thread.action.type);
//                                        p.threat.add(thread);
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//                }
//
//                // resolve threads 
//                for (int i = 0;
//                        i < p.threat.size();
//                        i++) {
//                    Ordering o = new Ordering();
//                    if (p.threat.get(i).link.reciver == goal) {
//                        o.before = p.threat.get(i).action;
//                        o.after = p.threat.get(i).link.provider;
//                    } else if (p.threat.get(i).link.provider == init) {
//                        o.before = p.threat.get(i).link.reciver;
//                        o.after = p.threat.get(i).action;
//                    } else {  // inja bayad ezafe kard taghire constarin
//                        Random r = new Random();
//                        double rand = r.nextDouble();
//                        rand = 0.25;
//                        if (rand < 0.5) {
//                            o.before = p.threat.get(i).action;
//                            o.after = p.threat.get(i).link.provider;
//
//                        } else {
//                            o.before = p.threat.get(i).link.reciver;
//                            o.after = p.threat.get(i).action;
//                        }
//                    }
//                    p.ordering.add(o);
//                }
//            }
            //6- recall popObject
            pop(p);
        }
        return p;
    }

    public void findThread(Plan p, Plan backUpPlan, Action ac, Link link, boolean internalfound) {
        if (internalfound) {

            for (int i = 0; i < p.step.size(); i++) {
                for (int j = 0; j < p.step.get(i).deletes.size(); j++) {
                    if (p.step.get(i).deletes.get(j) == link.condition) {
                        Threat thread = new Threat();
                        thread.link = link;
                        thread.action = p.step.get(i);
                        thread.state = link.condition;
                        System.out.println("        Thread found : in link between " + thread.link.provider.type + " to " + thread.link.reciver.type + "for state " + thread.state.predicate + " in action " + thread.action.type);
                        p.threat.add(thread);
                        break;

                    }

                }
            }

        } else { // for externall add

            for (int j = 0; j < backUpPlan.link.size(); j++) {
                for (int i = 0; i < ac.deletes.size(); i++) {

                    if (backUpPlan.link.get(j).condition.predicate.equals(ac.deletes.get(i).predicate)) {
                        int temp=0;
                        int k=0;
                        for(  ; k<ac.deletes.get(i).numberOfArg; k++){
                            if(ac.deletes.get(i).arguments.get(k).value == backUpPlan.link.get(j).condition.arguments.get(k).value){
                               temp++; 
                            }
                        }
                        if( k == temp && temp != 0 || ac.deletes.get(i).numberOfArg == 0){
                            Threat thread = new Threat();

                            thread.link = backUpPlan.link.get(j);
                            thread.action = ac;
                            thread.state = ac.deletes.get(i);
                            System.out.println("        Thread found : in link between " + thread.link.provider.type + " to " + thread.link.reciver.type + "for state " + thread.state.predicate + " in action " + thread.action.type);
                            p.threat.add(thread);
                              break; // age chand ta thread az ye action roye ye link rokh dad , faghat yekisho mizare to thread ha , badan mishe behtaresh kard.
                        }
                        

                    }

//                    for (int k = 0; k < backUpPlan.link.get(j).reciver.preconditions.subgoals.size(); k++) {
//                        if (backUpPlan.link.get(j).reciver.preconditions.subgoals.get(k).state.predicate.equalsIgnoreCase(ac.deletes.get(i).predicate)) {
//                            for (int l = 0; l < ac.deletes.get(i).numberOfArg; l++) {
//                                if (ac.deletes.get(i).arguments.get(l).value == backUpPlan.link.get(j).reciver.preconditions.subgoals.get(k).state.arguments.get(l).value) {
//                                    Threat thread = new Threat();
//
//                                    thread.link = backUpPlan.link.get(j);
//                                    thread.action = ac;
//                                    thread.state = ac.deletes.get(i);
//                                    System.out.println("        Thread found : in link between " + thread.link.provider.type + " to " + thread.link.reciver.type + "for state " + thread.state.predicate + " in action " + thread.action.type);
//                                    p.threat.add(thread);
//                                    break;
//                                }
//                            }
//                        }
//                    }
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

                    forbiddenAction.add(p.link.get(i).reciver);
                    check.add(true);
                }

            }

        }
        check.set(0, false); // khode action dobare false mishe vase check kardane order ha
        while (check.contains(false)) {
            int ind = check.indexOf(false);
            check.set(ind, true);
            Action a = forbiddenAction.get(ind);
            
            for(Ordering ord : p.ordering ){
               if(ord.before == a){
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

    public Action boundAction(Plan p, Action ac, ArrayList<Variable> localbound) {
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
        
        for(int i=0; i<p.link.size();i++)
        {
            if(p.link.get(i).provider == ac){
                Action ac2 = p.link.get(i).reciver;
                for(int j=0; j<ac2.preconditions.subgoals.size();j++){
                    for(int k=0;k < ac2.preconditions.subgoals.get(j).state.numberOfArg; k++){
                        for(int l=0 ; l<localbound.size();l++){
                            if(ac2.preconditions.subgoals.get(j).state.arguments.get(k).equals(localbound.get(l))){
                                boundAction(p, ac2, localbound);
                            }
                        }
                        
                        
                    }
                }
            }
            else{
                continue;
            }
            
        }
        
        
        return ac;

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
            Action a = new Action();
            a = action.preconditions.subgoals.get(i).action;

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

            subg.action = a;
            subg.state = s;
            arrsub.add(subg);

        }
        g.subgoals = arrsub;

        ac.preconditions = g;

        return ac;

    }

    public ArrayList<Action> internal(Plan p) {
        ArrayList<Action> selectAction = new ArrayList<>();
        ArrayList<Double> random = new ArrayList<>();
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < p.step.size(); i++) {
            Random r = new Random();
            double d = r.nextDouble();
            d = d*p.step.get(i).preconditions.subgoals.size();
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

    public void external() {
        ArrayList<Action> ext = new ArrayList<>();
        ArrayList<Double> random = new ArrayList<>();

        for (int i = 0; i < this.operators.size(); i++) {

            Random r = new Random();
            double d = r.nextDouble();
            d = d*this.operators.get(i).preconditions.subgoals.size();
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

//                    operators.add(init);
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

    public void order(Plan p) {
        ArrayList<Action> ord = null;
        for (int i = 1; i < p.link.size(); i++) {
            if (p.link.get(i).provider.type == "INITIAL-STATE") {
                ord.add(p.link.get(i).provider);
                ord.add(p.link.get(i).reciver);
            }
            for (int j = 0; j < ord.size(); j++) {
                if (ord.get(j) == p.link.get(i).provider) {
                    ord.add(p.link.get(i).reciver);
                }
            }
        }
    }
}
