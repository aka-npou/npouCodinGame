package npou.ghostInTheCell;

//для каждой своей базы
//смотрим
//если нет своих баз ближе то копим силы если есть  нейтралы в тылу то захватываем
//если есть базы ближе то шлем туда войска

//


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by npou on 01.03.2017.
 */
class Player {

    private final static boolean IS_DEBUG = true;

    private List<Factory> myFactories = new ArrayList<>();
    private List<Factory> opponentFactories = new ArrayList<>();
    private List<Factory> neutralFactories = new ArrayList<>();

    private List<Factory> neutralZeroFactories = new ArrayList<>();

    private List<Troop> myTroops = new ArrayList<>();
    private List<Troop> opponentTroops = new ArrayList<>();

    private List<SendTroops> sendTroops = new ArrayList<>();

    private List<Factory> factories = new ArrayList<>();
    private List<Integer> moves = new ArrayList<>();

    private List<Bomb> bombs = new ArrayList<>();

    private int step = 1;
    private int bomb = 2;

    private boolean isFirstStep = true;

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);
        Player player = new Player();
        player.start(in);
        player.gameLoop(in);
    }

    private void start(Scanner in) {
        int factoryCount = in.nextInt(); // the number of factories
        for (int i = 0; i < factoryCount; i++) {
            factories.add(new Factory(i));
        }
        int linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();
            //тут бы сохранить дистанции между на будущее
            factories.get(factory1).distance.add(new FactDist(factory2, distance));
            factories.get(factory2).distance.add(new FactDist(factory1, distance));
            //System.err.println(factory1 + " " + factory2 + " " + distance);
        }

        for (int i = 0; i < factoryCount; i++) {
            factories.get(i).distance.sort((o1, o2) -> (o1.distance - o2.distance));
        }

    }
    private void gameLoop(Scanner in) {
        while (true) {
            step++;
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            //на первом ходу свалить на ту что производит
            //расползаться по тем что производят

            myFactories = new ArrayList<>();
            opponentFactories = new ArrayList<>();
            neutralFactories = new ArrayList<>();

            neutralZeroFactories = new ArrayList<>();

            myTroops = new ArrayList<>();
            opponentTroops = new ArrayList<>();

            sendTroops = new ArrayList<>();

            moves = new ArrayList<>();

            bombs = new ArrayList<>();

            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();
                if (entityType.equals("FACTORY")) {
                    Factory f = factories.get(entityId);
                    f.arg1 = arg1;
                    f.arg2 = arg2;
                    f.arg3 = arg3;
                    f.arg4 = arg4;
                    if (arg1 == 1) myFactories.add(f);
                    if (arg1 == -1) opponentFactories.add(f);
                    if (arg1 == 0 && arg3 != 0) neutralFactories.add(f);
                    if (arg1 == 0 && arg3 == 0) neutralZeroFactories.add(f);
                    //System.err.println(entityId);

                }
                if (entityType.equals("TROOP")) {
                    if (arg1 == 1) myTroops.add(new Troop(entityId,arg1,arg2,arg3,arg4,arg5));
                    if (arg1 == -1) opponentTroops.add(new Troop(entityId,arg1,arg2,arg3,arg4,arg5));
                }

                if (entityType.equals("BOMB")) {
                    if (arg1 == 1) {
                        bombs.add(new Bomb(entityId,arg1,arg2,arg3,arg4));
                    }
                }
            }


            if (isFirstStep) {
                firstStep();
                continue;
            }

            /*for (Troop troop:opponentTroops) {
                if (troop.arg5 <3) {
                    factories.get(troop.arg3).arg2 -= troop.arg4;
                }
            }

            for (Troop troop:myTroops) {
                if (troop.arg5 <3) {
                    factories.get(troop.arg3).arg2 += troop.arg4;
                }
            }*/

            for(Factory f:myFactories) {
                SysPtErr(f.arg2);
            }


            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            boolean activeG = false;
            String action = "";
            boolean active = false;
            for (Factory mf:myFactories) {
                active = false;
                SysPtErr("////////////////");
                /*if (neutralFactories.size() > 0) {
                    neutralFactories.sort((o1, o2) -> (o2.arg3 - o1.arg3));
                    for (Factory nf:neutralFactories) {
                        if (active || activeG) action += ";";
                        action += "MOVE " + mf.entityId + " " + nf.entityId + " 2";
                        active = true;
                    }
                }*/
                String currentAction = getFactoryGo(mf, 2);
                if (!currentAction.equals("")) {
                    if (active || activeG) action += ";";
                    action += currentAction;
                    active = true;
                }

                if ((mf.arg2 > 14 && mf.arg3 == 0) || (mf.arg2 > 14 && mf.arg3 <3)) {
                    if (canUpdate(mf)) {
                        if (active || activeG) action += ";";
                        action += "INC " + mf.entityId;
                        active = true;
                        mf.arg2 -=10;
                    }
                }

                if(!active && canAttack(mf) && opponentFactories.size() > 0) {
                    currentAction = getFactoryAttack(mf, 2, false);
                    if (!currentAction.equals("")) {
                        if (active || activeG) action += ";";
                        action += currentAction;
                        active = true;
                    }
                }

                if(!active && canAttack(mf) && opponentFactories.size() > 0) {
                    currentAction = getFactoryAttack(mf, 2, true);
                    if (!currentAction.equals("")) {
                        if (active || activeG) action += ";";
                        action += currentAction;
                        active = true;
                    }
                }

                /*if (!active && mf.arg2 > 40 && opponentFactories.size() > 0) {
                    opponentFactories.sort((o1, o2) -> (o2.arg3 - o1.arg3));
                    *//*for (Factory nf:opponentFactories) {
                        if (active || activeG) action += ";";
                        action += "MOVE " + mf.entityId + " " + nf.entityId + " 20";
                        active = true;
                    }*//*
                    if (active || activeG) action += ";";
                    action += "MOVE " + mf.entityId + " " + opponentFactories.get(0).entityId + " 5";
                    active = true;
                }*/
                activeG = activeG || active;
            }

            if (step > 4 && bomb == 2) {
                String goBomb = goBomb();
                if (!goBomb.equals("")) {
                    if (active || activeG) action += ";";
                    action += goBomb;
                    bomb--;
                }
            }

            if (step > 15 && bomb == 1) {
                String goBomb = goBomb();
                if (!goBomb.equals("")) {
                    if (active || activeG) action += ";";
                    action += goBomb;
                    bomb--;
                }
            }

            if (activeG) System.out.println(action); else System.out.println("WAIT");

            // Any valid action, such as "WAIT" or "MOVE source destination cyborgs"
            //System.out.println("WAIT");
        }
    }

    private void firstStep() {
        isFirstStep = false;
        SysPtErr("f s");
        Factory currentFactory = myFactories.get(0);
        //if (currentFactory.arg3 == 0) {
        String action = getFactoryGo(currentFactory, 3);
        if (action.equals("")) System.out.println("WAIT"); else System.out.println(action);
        //}
    }

    private String getFactoryGo(Factory currentFactory, int cyborg) {
        String action = "";
        System.err.println(currentFactory.arg2);
        List<ToFact> toFacts = new ArrayList<>();
        for(FactDist factDist:currentFactory.distance) {
            Factory f = factories.get(factDist.entityId);
            System.err.println("needGoToF(f) "+currentFactory.entityId+"/"+factDist.entityId+" "+needGoToF(f));
            if (f.arg1 == 0 && f.arg3 != 0 && needGoToF(f) > 0) {
                toFacts.add(new ToFact(factDist.entityId, (factDist.distance + f.arg2+1)/f.arg3, needGoToF(f)+2));
            }

            //if (f.arg1 == 0 && f.arg2 >=0 && f.arg3 == 0 && needGoToF(f) > 0) {
            //    toFacts.add(new ToFact(factDist.entityId, factDist.distance + f.arg2+1 + 15, f.arg2+2));
            //}
            //помощь  своим
            if (f.arg1 == 1 && needToHelp(f) < 0) {
                System.err.println("needHelp");
                int a = canIHelp(currentFactory, f);
                if (a>0) toFacts.add(new ToFact(factDist.entityId, 0, a));
            }
        }
        System.err.println(currentFactory.arg2);
        toFacts.sort((o1, o2) -> (o1.toZero - o2.toZero));

        //nt cyborg1 = currentFactory.arg2;
        System.err.println("toFacts.size() "+toFacts.size());
        for (ToFact toFact : toFacts) {
            System.err.println(toFact.entityId+" "+toFact.toZero);
        }
        if (toFacts.size() != 0) {
            //action = "";
            System.err.println("action");
            boolean active = false;
            for (ToFact toFact : toFacts) {
                System.err.println("id: "+toFact.entityId);
                System.err.println(""+currentFactory.arg2+"/"+toFact.arg2+" "+cyborg);
                //if (currentFactory.arg2 - toFact.arg2 < cyborg) break;
                //сравнение что при учете атакующих  можно  слать
                int cyborgGo = currentFactory.arg2 - toFact.arg2 < cyborg?currentFactory.arg2 - cyborg:toFact.arg2;
                if (cyborgGo <= 0) continue;
                System.err.println("cyborgGo "+cyborgGo);
                if (active) action += ";";
                System.err.println("id: "+toFact.entityId);
                action += "MOVE " + currentFactory.entityId + " " + toFact.entityId + " " + cyborgGo;
                currentFactory.arg2 -= cyborgGo;
                Factory f = factories.get(toFact.entityId);
                if (f.arg1 == 1) f.arg2 += cyborgGo;
                if (f.arg1 != 1) f.arg2 -= cyborgGo;
                active = true;
            }
        }
        System.err.println(action);
        return action;
    }

    private String getFactoryAttack(Factory currentFactory, int cyborg, boolean all) {
        String action = "";
        System.err.println(currentFactory.arg2);
        List<ToFact> toFacts = new ArrayList<>();
        for(FactDist factDist:currentFactory.distance) {
            Factory f = factories.get(factDist.entityId);
            int currentArg4 = getCurrentArg4(f) + factDist.distance * f.arg3;
            if (currentArg4 <=0) continue;
            if (f.arg1 == -1 && (f.arg3 != 0 || all)) {
                toFacts.add(new ToFact(factDist.entityId, factDist.distance, currentArg4+2));
            }
        }
        //System.err.println(currentFactory.arg2);
        toFacts.sort((o1, o2) -> (o2.toZero - o1.toZero));

        //System.err.println("toFacts.size() "+toFacts.size());
        //for (ToFact toFact : toFacts) {
        //    System.err.println(toFact.entityId+" "+toFact.toZero);
        //}
        if (toFacts.size() != 0) {
            System.err.println("action");
            boolean active = false;
            for (ToFact toFact : toFacts) {
                //сравнение что при учете атакующих  можно  слать
                int cyborgGo = currentFactory.arg2 - toFact.arg2 < cyborg?currentFactory.arg2 - cyborg:toFact.arg2;
                if (cyborgGo <= 0) continue;
                System.err.println("cyborgGo "+cyborgGo);
                if (active) action += ";";
                action += "MOVE " + currentFactory.entityId + " " + toFact.entityId + " " + cyborgGo;
                currentFactory.arg2 -= cyborgGo;
                Factory f = factories.get(toFact.entityId);
                if (f.arg1 == 1) f.arg2 += cyborgGo;
                if (f.arg1 != 1) f.arg2 -= cyborgGo;
                sendTroops.add(new SendTroops(toFact.entityId, cyborgGo));
                active = true;
            }
        }
        System.err.println(action);
        return action;
    }

    private int needGoToF(Factory f) {
        //boolean allIsNeed = true;
        int zero = f.arg2+1;
        for (Troop ot:opponentTroops) {
            if (ot.arg3 == f.entityId) zero += ot.arg4;
        }

        for (Troop mt:myTroops) {
            if (mt.arg3 == f.entityId) zero -= mt.arg4;
        }
        //if (zero >= 0) allIsNeed = false;
        //return allIsNeed;
        return zero;
    }

    private int needToHelp(Factory f) {
        int zero = f.arg2;
        for (Troop ot:opponentTroops) {
            if (ot.arg3 == f.entityId && ot.arg5 < 5) zero -= ot.arg4;
        }
        System.err.println("z "+zero);
        for (Troop mt:myTroops) {
            if (mt.arg3 == f.entityId && mt.arg5 < 5) zero += mt.arg4;
        }
        //if (zero >= 0) allIsNeed = false;
        //return allIsNeed;
        return zero;
    }

    private boolean canUpdate(Factory f) {
        boolean iCan = true;
        int arg2 = f.arg2;
        for(Troop troop:opponentTroops) {
            if (troop.arg3 == f.entityId) arg2 -=troop.arg4;
        }
        if (arg2 <=0) iCan = false;
        return iCan;
    }

    private boolean canAttack(Factory f) {
        boolean iCan = true;
        int arg2 = f.arg2;
        for(Troop troop:opponentTroops) {
            if (troop.arg3 == f.entityId) arg2 -=troop.arg4;
        }
        if (arg2 <=0) iCan = false;
        return iCan;
    }

    private int canIHelp(Factory cf, Factory f) {
        int cyborg = cf.arg2;

        int a = -1 * needToHelp(f);
        System.err.println(a);
        for (Troop ot:opponentTroops) {
            if (ot.arg3 == cf.entityId && ot.arg5 < 5) cyborg -= ot.arg4;
        }

        for (Troop mt:myTroops) {
            if (mt.arg3 == cf.entityId && mt.arg5 < 5) cyborg += mt.arg4;
        }
        System.err.println(cyborg);
        cyborg = Math.min(a, cyborg);

        return cyborg;
    }

    private int getCurrentArg4(Factory f) {
        int currentArg4 = f.arg2;

        for (Troop t:myTroops) {
            if (t.arg3 == f.entityId) currentArg4 -=t.arg4;
        }

        for (Troop t:opponentTroops) {
            if (t.arg3 == f.entityId) currentArg4 +=t.arg4;
        }

        return currentArg4;
    }

    private String goBomb() {
        String s = "";
        List<ToFact> l = new ArrayList<>();
        for (Factory f:opponentFactories) {
            if (bombGoTo(f)) continue;
            l.add(new ToFact(f.entityId, f.arg3*1000 + f.arg2, 0));
        }
        l.sort((o1, o2) -> (o2.toZero - o1.toZero));
        if (l.size() != 0) {
            Factory f = factories.get(l.get(0).entityId);
            int id = -1;
            for (FactDist fd:f.distance) {
                if (factories.get(fd.entityId).arg1 == 1) {
                    id = fd.entityId;
                    break;
                }
            }
            s = "BOMB " + id + " " + l.get(0).entityId;
        }

        return s;
    }

    private boolean bombGoTo(Factory f) {
        boolean bgt = false;
        for (Bomb b:bombs) {
            if (b.arg3 == f.entityId) bgt = true;
        }

        return bgt;
    }



    private class Entity {
        int entityId;
        int arg1;
        int arg2;
        int arg3;
        int arg4;
        int arg5;
        Entity(int entityId,
              int arg1,
              int arg2,
              int arg3,
              int arg4,
              int arg5) {
            this.entityId = entityId;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
            this.arg4 = arg4;
            this.arg5 = arg5;
        }

    }

    private class Factory extends Entity{
        List<FactDist> distance = new ArrayList<>();

        Factory(int entityId,
                int arg1,
                int arg2,
                int arg3,
                int arg4) {
            super(entityId,
            arg1,
            arg2,
            arg3,
            arg4,
            0);

        }

        Factory(int entityId) {
            super(entityId,
                    0,
                    0,
                    0,
                    0,
                    0);
        }
    }

    private class Troop extends Entity{

        Troop(int entityId,
              int arg1,
              int arg2,
              int arg3,
              int arg4,
              int arg5) {
            super(entityId,
                    arg1,
                    arg2,
                    arg3,
                    arg4,
                    arg5);
        }
    }

    private class Bomb extends Entity{
        Bomb(int entityId,
             int arg1,
             int arg2,
             int arg3,
             int arg4) {
            super(entityId,
                    arg1,
                    arg2,
                    arg3,
                    arg4,
                    0);
        }
    }

    private class FactDist {
        int entityId;
        int distance;

        public FactDist(int entityId, int distance) {
            this.entityId = entityId;
            this.distance = distance;
        }
    }

    private class ToFact {
        int entityId;
        int toZero;
        int arg2;

        public ToFact(int entityId, int toZero, int arg2) {
            this.entityId = entityId;
            this.toZero = toZero;
            this.arg2 = arg2;
        }
    }

    private class SendTroops {
        int entityId;
        int arg4;

        public SendTroops(int entityId, int arg4) {
            this.entityId = entityId;
            this.arg4 = arg4;
        }
    }



    private void SysPtErr(String text) {
        if (IS_DEBUG)
            System.err.println(text);
    }

    private void SysPtErr(boolean text) {
        if (IS_DEBUG)
            System.err.println(text);
    }

    private void SysPtErr(int text) {
        if (IS_DEBUG)
            System.err.println(text);
    }
}

