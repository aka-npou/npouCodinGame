package npou.hypersonic;

import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private final static boolean IS_DEBUG = false;

    private static int width;
    private static int height;
    private static int myId;
    private static int[][] map_pp;
    private static int still = 0;
    private static int HorW;

    private static int maxCell = 0;

    private ArrayList<Entity> bombs = new ArrayList<>();
    private ArrayList<Entity> mans = new ArrayList<>();
    private ArrayList<Entity> items = new ArrayList<>();

    private ArrayList<Turn> closeCell = new ArrayList<>();

    private Entity my = new Entity();

    private boolean canStilBomb = false;

    private TurnBomb[] simBombs = new TurnBomb[8];

    private Turn oldTurn = null;


    public static void main(String args[]) {
        new Player().play();
    }

    private void play() {
        simBombs[0] = new TurnBomb();
        simBombs[1] = new TurnBomb();
        simBombs[2] = new TurnBomb();
        simBombs[3] = new TurnBomb();
        simBombs[4] = new TurnBomb();
        simBombs[5] = new TurnBomb();
        simBombs[6] = new TurnBomb();
        simBombs[7] = new TurnBomb();

        Scanner in = new Scanner(System.in);
        width = in.nextInt();
        height = in.nextInt();
        myId = in.nextInt();

        if (height>width)
            HorW = height;
        else
            HorW = width;

        map_pp = new int[height][width];
        my.coord = new Coord(-1, -1);

        loop(in);
    }

    private void loop(Scanner in) {
        while (true) {
            still -=1;
            if (still <0)
                still = 0;

            bombs.clear();
            mans.clear();
            items.clear();

            maxCell = 0;
            canStilBomb = false;

            for (int _y = 0; _y < height; _y++) {
                String row = in.next();
                String[] r = row.split("");
                for (int _x=0; _x<width; _x++) {
                    if (r[_x].equals(".")) {
                        map_pp[_y][_x] = 0;
                        continue;
                    }
                    if (r[_x].equals("X")) {
                        map_pp[_y][_x] = -100;
                        continue;
                    }

                    map_pp[_y][_x] = Integer.parseInt(r[_x])*-1-1;
                }
            }
            int entities = in.nextInt();
            for (int i = 0; i < entities; i++) {
                Entity entity = new Entity();
                entity.coord = new Coord();
                entity.entityType = in.nextInt();
                entity.owner = in.nextInt();
                entity.coord.x = in.nextInt();
                entity.coord.y = in.nextInt();
                entity.param1 = in.nextInt();
                entity.param2 = in.nextInt();
                if (entity.entityType == 0) {
                    mans.add(entity);
                }
                if (entity.entityType == 1) {
                    bombs.add(entity);
                    map_pp[entity.coord.y][entity.coord.x] = -200;
                    SysPtErr("b p1 p2 x y "+entity.param1+" "+entity.param2+" "+entity.coord.x+" "+entity.coord.y);
                }
                if (entity.entityType == 2) {
                    items.add(entity);
                }

                if (entity.entityType == 0 && entity.owner == myId) {
                    my = entity;
                }
            }

            simBombs();

            setPP();

            SysPtErr(maxCell);
            SysPtErr(my.coord.x+" "+my.coord.y);
            if (IS_DEBUG) {
                for (int _y = 0; _y < height; _y++) {
                    for (int _x = 0; _x < width; _x++) {
                        if (map_pp[_y][_x] >= 0)
                            System.err.print(map_pp[_y][_x]);
                        else {
                            if (map_pp[_y][_x] == -100)
                                System.err.print("w");
                            else if (map_pp[_y][_x] == -200)
                                System.err.print("B");
                            else
                                System.err.print("x");
                        }
                    }
                    System.err.println("");
                }
            }

            String action = getAction();
            System.out.println(action);
        }
    }

    /////

    private void setPP() {
        for (int _y = 0; _y < height; _y++) {
            for (int _x=0; _x<width; _x++) {
                if (map_pp[_y][_x] >= 0) {
                    if (notSetBomb(_y, _x)) {
                        setPPtoCell(_y, _x);
                    }
                }
            }
        }
    }

    private void setPPtoCell(int _y, int _x) {
        //проверка на бомбы
        for (int x=1; x < my.param2; x++) {
            if (_x + x < width && map_pp[_y][_x + x] < 0) {
                if (map_pp[_y][_x + x] > -99)
                    map_pp[_y][_x] += 1;
                break;
            }
        }

        for (int x=1; x < my.param2; x++) {
            if (_x - x > 0 && map_pp[_y][_x - x] < 0) {
                if (map_pp[_y][_x - x] > -99)
                    map_pp[_y][_x] += 1;
                break;
            }
        }

        for(int y=1;y<my.param2;y++) {
            if (_y + y < height && map_pp[_y + y][_x] < 0) {
                if (map_pp[_y + y][_x] > -99)
                    map_pp[_y][_x] += 1;
                break;
            }
        }

        for(int y=1;y<my.param2;y++) {
            if (_y - y > 0 && map_pp[_y - y][_x] < 0) {
                if (map_pp[_y - y][_x] > -99)
                    map_pp[_y][_x] += 1;
                break;
            }
        }


        if (maxCell < map_pp[_y][_x])
            maxCell = map_pp[_y][_x];

    }

    private boolean notSetBomb(int _y, int _x) {
        boolean isSetBomb = false;
        for(Entity entity:bombs) {
            if (entity.coord.x == _x && entity.coord.y == _y) {
                isSetBomb = true;
                break;
            }
        }
        return !isSetBomb;
    }

    /////

    private String getAction() {
        String action;

        Turn turn = getTurn();
        if (turn == null)
            action = "MOVE "+my.coord.x+" "+my.coord.y;
        else {
            if (turn.coord.x == my.coord.x && turn.coord.y == my.coord.y && canStilBomb) {
                action = "BOMB "+turn.coord.x+" "+turn.coord.y;
            } else {
                action = "MOVE "+turn.coord.x+" "+turn.coord.y;
            }
        }
        return action;
    }

    private Turn getTurn() {
        Turn turn;

        turn = getWave();

        return turn;
    }

    private Turn getWave() {
        Turn turnTo = new Turn(new Coord(my.coord.x, my.coord.y), 0, null);

        ArrayList<Turn> openCell = new ArrayList<>();

        closeCell.clear();

        openCell.add(turnTo);
        Turn currentTurn;
        while (openCell.size() != 0) {
            currentTurn = openCell.get(0);

            if (currentTurn.turns>7) {
                closeCell.add(currentTurn);
                openCell.remove(currentTurn);
                continue;
            }

            addCells(currentTurn, openCell);
            closeCell.add(currentTurn);
            openCell.remove(currentTurn);
        }

        SysPtErr("//closeCell.size() " + closeCell.size());
        ArrayList<Turn> canMove = closeCell;//canMove();

        ArrayList<Turn> del = new ArrayList<>();
        for (Turn turn:canMove) {
            if (turn.turns<8)
                del.add(turn);
        }


        canMove.removeAll(del);


        //canMove.sort((o1, o2) -> (o1.turns - o2.turns));
        //проверка нулевой ячейки
        if (canMove.size() != 0) {
            turnTo = canMove.get(0);
            if (!canTurn(turnTo.coord, turnTo.turns)) {
                canMove.remove(turnTo);
                turnTo = null;
            } else {
                if (canLife(turnTo)) {
                    canStilBomb = true;
                }
            }
        }

        if (oldTurn != null) {
            for (Turn turn:canMove)
                if (turn.coord.x == oldTurn.coord.x && turn.coord.y == oldTurn.coord.y) {
                    turnTo = turn;
                    SysPtErr("oldTurn ok"+turnTo.coord.x+" "+turnTo.coord.y);
                    SysPtErr("oldTurn ok"+oldTurn.coord.x+" "+oldTurn.coord.y);
                    break;
                }
        }

        SysPtErr("//canMove.size() " + canMove.size());
        /*if (canMove.size() != 0) {
            turnTo = canMove.get(0);
            if (canLife(turnTo)) {
                canStilBomb = true;
            }
            SysPtErr("turnTo " + turnTo.coord.x+" "+turnTo.coord.y);
        }*/

        boolean isOkCell = canMove.size() != 0;//!bombBoomThis(turnTo, false) && canLife(turnTo);

        SysPtErr("isOkCell " + isOkCell);
        SysPtErr("canStilBomb " + canStilBomb);

        //for (Turn turn:canMove)
        //    SysPtErr("coord to" + turn.coord.x + " " + turn.coord.y + getStart(turn).coord.x + " " + getStart(turn).coord.y);

        for (Turn turn:canMove) {
            //SysPtErr("coord " + turn.coord.x + " " + turn.coord.y);
            if (turnTo == null) {
                turnTo = turn;
                if (canLife(turnTo)) {
                    canStilBomb = true;
                }
                continue;
            }
            if (map_pp[turnTo.coord.y][turnTo.coord.x] < map_pp[turn.coord.y][turn.coord.x]) {
                SysPtErr("ok coord " + turn.coord.x + " " + turn.coord.y);

                if (!canStilBomb) {
                    turnTo = turn;
                    if (canLife(turn)) {
                        canStilBomb = true;
                    }
                } else {
                    if (canLife(turn)) {
                        turnTo = turn;
                        canStilBomb = true;
                    }
                }
            } else {
                if (!canStilBomb && canLife(turn)) {
                    turnTo = turn;
                    canStilBomb = true;
                }
            }
            //SysPtErr("canStilBomb " + canStilBomb);
        }

        if (!isOkCell) {
            turnTo = null;
            oldTurn = turnTo;
        } else {
            SysPtErr("turnTo " + turnTo.coord.x+" "+turnTo.coord.y);
            oldTurn = turnTo;
            turnTo = getStart(turnTo);
        }

        return turnTo;
    }

    /////

    private void addCells(Turn turn, ArrayList<Turn> openCell) {
        Coord testCoord;

        //SysPtErr("//openCell "+openCell.size());
        //SysPtErr("//closeCell "+closeCell.size());
        SysPtErr("from t "+turn.coord.x+" "+turn.coord.y+" "+turn.turns);


        //up
        if (turn.coord.y-1>=0) {
            testCoord = new Coord(turn.coord.x, turn.coord.y-1);
            if (!contain(closeCell, testCoord)) {
                if (canTurn(testCoord, turn.turns+1)) {
                    openCell.add(new Turn(new Coord(testCoord.x, testCoord.y), turn.turns+1, turn));
                    SysPtErr("add u "+testCoord.x+" "+testCoord.y);
                }
            }
        }

        //down
        if (turn.coord.y+1<height) {
            testCoord = new Coord(turn.coord.x, turn.coord.y+1);
            if (!contain(closeCell, testCoord)) {
                if (canTurn(testCoord, turn.turns+1)) {
                    openCell.add(new Turn(new Coord(testCoord.x, testCoord.y), turn.turns+1, turn));
                    SysPtErr("add d "+testCoord.x+" "+testCoord.y);
                }
            }
        }

        //left
        if (turn.coord.x-1>=0) {
            testCoord = new Coord(turn.coord.x-1, turn.coord.y);
            if (!contain(closeCell, testCoord)) {
                if (canTurn(testCoord, turn.turns+1)) {
                    openCell.add(new Turn(new Coord(testCoord.x, testCoord.y), turn.turns+1, turn));
                    SysPtErr("add l "+testCoord.x+" "+testCoord.y);
                }
            }
        }

        //right
        if (turn.coord.x+1<width) {
            testCoord = new Coord(turn.coord.x+1, turn.coord.y);
            if (!contain(closeCell, testCoord)) {
                if (canTurn(testCoord, turn.turns+1)) {
                    openCell.add(new Turn(new Coord(testCoord.x, testCoord.y), turn.turns+1, turn));
                    SysPtErr("add r "+testCoord.x+" "+testCoord.y);
                }
            }
        }

        //stay
        if (canTurn(turn.coord, turn.turns+1)) {
            openCell.add(new Turn(new Coord(turn.coord.x, turn.coord.y), turn.turns+1, turn));
            SysPtErr("add s "+turn.coord.x+" "+turn.coord.y);
        }

        //SysPtErr("//openCell "+openCell.size());

    }

    private boolean contain(ArrayList<Turn> closeCell, Coord coord) {
        boolean in = false;
        for (Turn turn:closeCell) {
            if (turn.coord.x == coord.x && turn.coord.y == coord.y) {
                in = true;
                break;
            }
        }

        return in;
    }

    private boolean canTurn(Coord coord, int turn) {
        boolean can = true;
        if (map_pp[coord.y][coord.x] < 0)
            can = false;

        if (can && turn<8) {
            ArrayList<Coord> currentTurnSimBombs = simBombs[turn].cells;
            SysPtErr("currentTurnSimBombs " + currentTurnSimBombs.size());
            for(Coord coordBomb:currentTurnSimBombs) {
                if (coord.x == coordBomb.x && coord.y == coordBomb.y) {
                    can = false;
                    break;
                }
            }
        }

        return can;
    }

    /////

    private Turn getStart(Turn turn) {
        if (turn.pTurn != null && turn.pTurn.pTurn == null)
            return turn;
        return getStart(turn.pTurn);
    }

    private boolean canLife(Turn turn) {
        boolean can = true;

        SysPtErr("turn canLife " + turn.coord.x+" "+turn.coord.y);

        ArrayList<Turn> turns = new ArrayList<>(closeCell);
        ArrayList<Turn> del = new ArrayList<>();
        int _x = turn.coord.x;
        int _y = turn.coord.y;

        for (Turn t:turns) {
            if (t.turns>8)
                del.add(t);
        }

        turns.removeAll(del);
        del.clear();

        for (Turn t:turns) {
            if (_x == t.coord.x && _y == t.coord.y) {
                del.add(t);
                //break;
            }
        }

        turns.removeAll(del);
        del.clear();

        for (int x=1; x < my.param2; x++) {
            if (_x + x >= width) {
                break;
            } else if (map_pp[_y][_x + x] < 0) {
                break;
            }

            for (Turn t:turns) {
                if (_x + x == t.coord.x && _y == t.coord.y){
                    del.add(t);
                    //break;
                }
            }
        }

        turns.removeAll(del);
        del.clear();

        for (int x = 1; x < my.param2; x++) {
            if (_x - x < 0) {
                break;
            } else if (map_pp[_y][_x - x] < 0) {
                break;
            }
            for (Turn t:turns) {
                if (_x - x == t.coord.x && _y == t.coord.y){
                    del.add(t);
                    //break;
                }
            }
        }

        turns.removeAll(del);
        del.clear();

        for (int y = 1; y < my.param2; y++) {
            if (_y + y >= height) {
                break;
            } else if (map_pp[_y + y][_x] < 0) {
                break;
            }
            for (Turn t:turns) {
                if (_x == t.coord.x && _y + y == t.coord.y){
                    del.add(t);
                    //break;
                }
            }
        }

        turns.removeAll(del);
        del.clear();

        for (int y = 1; y < my.param2; y++) {
            if (_y - y < 0) {
                break;
            } else if (map_pp[_y - y][_x] < 0) {
                break;
            }
            for (Turn t:turns) {
                if (_x == t.coord.x && _y - y == t.coord.y){
                    del.add(t);
                    //break;
                }
            }
        }

        turns.removeAll(del);
        /*for (Entity bomb:bombs) {
            _x = bomb.coord.x;
            _y = bomb.coord.y;

            for (Turn t:closeCell) {
                if (_x == t.coord.x && _y == t.coord.y) {
                    del.add(t);
                    break;
                }
            }

            for (int x=1; x < my.param2; x++) {
                if (_x + x < width && map_pp[_y][_x + x] < 0) {
                    break;
                }
                for (Turn t:closeCell) {
                    if (_x + x == t.coord.x && _y == t.coord.y){
                        del.add(t);
                        break;
                    }
                }
            }

            for (int x = 1; x < my.param2; x++) {
                if (_x - x > 0 && map_pp[_y][_x - x] < 0) {
                    break;
                }
                for (Turn t:closeCell) {
                    if (_x - x == t.coord.x && _y == t.coord.y){
                        del.add(t);
                        break;
                    }
                }
            }

            for (int y = 1; y < my.param2; y++) {
                if (_y + y < height && map_pp[_y + y][_x] < 0) {
                    break;
                }
                for (Turn t:closeCell) {
                    if (_x == t.coord.x && _y + y == t.coord.y){
                        del.add(t);
                        break;
                    }
                }
            }

            for (int y = 1; y < my.param2; y++) {
                if (_y - y > 0 && map_pp[_y - y][_x] < 0) {
                    break;
                }
                for (Turn t:closeCell) {
                    if (_x == t.coord.x && _y - y == t.coord.y){
                        del.add(t);
                        break;
                    }
                }
            }
        }

        turns.removeAll(del);*/

        SysPtErr("turns.size() " + turns.size());
        //SysPtErr("del.size() " + del.size());
        if (turns.size() == 0)
            can = false;

        return can;
    }


    /////

    private void simBombs() {
        for(TurnBomb t:simBombs) {
            t.cells.clear();
        }

        bombs.sort((o1, o2) -> (o1.param1 - o2.param1));
        for(Entity e:bombs) {
            SysPtErr("e c "+e.coord.x+" "+e.coord.y);
            TurnBomb t = simBombs[e.param1-1];

            t.cells.add(new Coord(e.coord.x, e.coord.y));

            //проверку на бомбы и предметы
            for (int x=1; x < e.param2; x++) {
                if (e.coord.x + x >= width) {
                    break;
                } else if (map_pp[e.coord.y][e.coord.x + x] < 0) {
                    break;
                }
                t.cells.add(new Coord(e.coord.x+x, e.coord.y));
            }
            SysPtErr(t.cells.size());
            for (int x=1; x < e.param2; x++) {
                if (e.coord.x - x < 0) {
                    break;
                } else if (map_pp[e.coord.y][e.coord.x - x] < 0) {
                    break;
                }
                t.cells.add(new Coord(e.coord.x-x, e.coord.y));
            }
            SysPtErr(t.cells.size());
            for (int y = 1; y < e.param2; y++) {
                if (e.coord.y + y >= height) {
                    break;
                } else if (map_pp[e.coord.y + y][e.coord.x] < 0) {
                    break;
                }
                t.cells.add(new Coord(e.coord.x, e.coord.y+y));
            }
            SysPtErr(t.cells.size());
            for (int y = 1; y < e.param2; y++) {
                if (e.coord.y - y < 0) {
                    break;
                } else if (map_pp[e.coord.y - y][e.coord.x] < 0) {
                    break;
                }
                t.cells.add(new Coord(e.coord.x, e.coord.y-y));
            }
            SysPtErr(t.cells.size());
        }
        SysPtErr("/////sb");
        for(TurnBomb t:simBombs) {
            SysPtErr(t.cells.size());
        }
        SysPtErr("/////sb");
    }

    /////


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



    private class Entity {
        int entityType;
        int owner;
        Coord coord;
        int param1;
        int param2;
    }

    private class Coord {
        int x = -1;
        int y = -1;

        Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Coord() {

        }
    }

    private class Turn {
        Coord coord;
        int turns;
        Turn pTurn;

        Turn(Coord coord, int turns, Turn pTurn) {
            this.coord = coord;
            this.turns = turns;
            this.pTurn = pTurn;
        }
    }

    private class TurnBomb {
        ArrayList<Coord> cells = new ArrayList<>();

    }
}
