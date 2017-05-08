package npou.hypersonic;

import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private final static boolean IS_DEBUG = true;

    private static int width;
    private static int height;
    private static int myId;
    private static int[][] map;
    private static int[][] map_pp;
    private static int still = 0;
    //private static int exRange = 3;
    //private static int bTimer = 8;
    private static int HorW;

    //private static int toThisCoord=0;

    private static int maxCell = 0;

    private ArrayList<Entity> bombs = new ArrayList<>();
    private ArrayList<Entity> mans = new ArrayList<>();
    private ArrayList<Entity> items = new ArrayList<>();

    private ArrayList<Turn> closeCell = new ArrayList<>();

    //ArrayList<Turn> turns = new ArrayList<>();

    private Entity my = new Entity();

    private boolean canStilBomb = false;

    private TurnBomb[] simBombs = new TurnBomb[8];


    public static void main(String args[]) {
        new Player().play();
    }

    void play() {
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

        map = new int[height][width];
        map_pp = new int[height][width];
        my.coord = new Coord(-1, -1);

        loop(in);
    }

    void loop(Scanner in) {
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
                        map[_y][_x] = 0;
                        map_pp[_y][_x] = 0;
                        continue;
                    }
                    if (r[_x].equals("X")) {
                        map[_y][_x] = -100;
                        map_pp[_y][_x] = -100;
                        continue;
                    }

                    map[_y][_x] = Integer.parseInt(r[_x])*-1-1;
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

    void setPP() {
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

    void setPPtoCell(int _y, int _x) {
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

    String getAction() {
        String action = "";

        Turn turn = getTurn();
        if (turn == null)
            action = "MOVE "+my.coord.x+" "+my.coord.y;
        else {
            if (turn.coord.x == my.coord.x && turn.coord.y == my.coord.y && canStilBomb) {
                action = "BOMB "+turn.coord.x+" "+turn.coord.y;
            } else {
                //if (turn.turns > 8 && map_pp[my.coord.y][my.coord.x] > 0 && canLife(turn))
                //    action = "BOMB "+turn.coord.x+" "+turn.coord.y;
                //else
                action = "MOVE "+turn.coord.x+" "+turn.coord.y;
            }
        }
        return action;
    }

    private Turn getTurn() {
        Turn turn;

        turn = getWave(my.coord);

        return turn;
    }

    private Coord getRound(int i) {
        Coord coord = new Coord(-1 , -1);

        int x1 = my.coord.x-i<0?0:my.coord.x-i;
        int x2 = my.coord.x+i>width-1?width-1:my.coord.x+i;
        int y1 = my.coord.y-i<0?0:my.coord.y-i;
        int y2 = my.coord.y+i>height-1?height-1:my.coord.y+i;

        for(int y=y1;y<y2+1;y++) {
            if (map_pp[y][x1] == maxCell) {
                coord.x = x1;
                coord.y = y;
            }
            if (map_pp[y][x2] == maxCell) {
                coord.x = x2;
                coord.y = y;
            }
        }

        for(int x=x1;x<x2+1;x++) {
            if (map_pp[y1][x] == maxCell) {
                coord.x = x;
                coord.y = y1;
            }
            if (map_pp[y2][x] == maxCell) {
                coord.x = x;
                coord.y = y2;
            }
        }

        return coord;
    }

    private Turn getWave(Coord _coord) {
        //Coord coord = new Coord(my.coord.x, my.coord.y);
        Turn turnTo = new Turn(new Coord(my.coord.x, my.coord.y), 0, null);

        //ArrayList<Turn> closeCell = new ArrayList<>();
        ArrayList<Turn> openCell = new ArrayList<>();

        closeCell.clear();

        openCell.add(turnTo);
        Turn currentTurn;
        while (openCell.size() != 0) {
            currentTurn = openCell.get(0);

            addCells(currentTurn, openCell);
            closeCell.add(currentTurn);
            openCell.remove(currentTurn);
        }

        SysPtErr("closeCell.size() " + closeCell.size());
        ArrayList<Turn> canMove = closeCell;//canMove();

        canMove.sort((o1, o2) -> (o1.turns - o2.turns));
        //проверка нулевой ячейки
        if (canMove.size() != 0) {
            turnTo = canMove.get(0);
            if (!canTurn(turnTo.coord, turnTo.turns)) {
                canMove.remove(turnTo);
                turnTo = null;
            }
        }
        SysPtErr("canMove.size() " + canMove.size());
        if (canMove.size() != 0) {
            turnTo = canMove.get(0);
            if (canLife(turnTo)) {
                canStilBomb = true;
            }
            SysPtErr("turnTo " + turnTo.coord.x+" "+turnTo.coord.y);
        }

        boolean isOkCell = canMove.size() != 0;//!bombBoomThis(turnTo, false) && canLife(turnTo);

        SysPtErr("isOkCell " + isOkCell);
        SysPtErr("canStilBomb " + canStilBomb);

        /*for (Turn turn:closeCell) {
            SysPtErr("coord " + turn.coord.x + " " + turn.coord.y);
            if (!isOkCell) {
                if (!bombBoomThis(turn, false) && !bombBoomThis(getStart(turn), true) && canLife(turn)) {
                    SysPtErr("ok coord " + turn.coord.x + " " + turn.coord.y);
                    turnTo = turn;
                    isOkCell = true;
                }
            } else {
                if (map_pp[turnTo.coord.y][turnTo.coord.x] < map_pp[turn.coord.y][turn.coord.x]) {
                    SysPtErr("coord " + turn.coord.x + " " + turn.coord.y);
                    if (!bombBoomThis(turn, false) && !bombBoomThis(getStart(turn), true) && canLife(turn)) {
                        SysPtErr("ok coord " + turn.coord.x + " " + turn.coord.y);
                        turnTo = turn;
                    }
                }
            }
        }*/

        for (Turn turn:canMove) {
            //SysPtErr("coord " + turn.coord.x + " " + turn.coord.y);
            if (map_pp[turnTo.coord.y][turnTo.coord.x] < map_pp[turn.coord.y][turn.coord.x]) {
                SysPtErr("ok coord " + turn.coord.x + " " + turn.coord.y);
                //if (!bombBoomThis(getStart(turn), true)) {
                //    SysPtErr("ok coord " + turn.coord.x + " " + turn.coord.y);
                //turnTo = turn;
                //if (canLife(turn)) {
                //    canStilBomb = true;
                //}

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

        if (!isOkCell)
            turnTo = null;

        return turnTo;
    }

    /////

    private void addCells(Turn turn, ArrayList<Turn> openCell) {
        Coord testCoord;

        //up
        if (turn.coord.y-1>=0) {
            testCoord = new Coord(turn.coord.x, turn.coord.y-1);
            if (!contain(closeCell, testCoord)) {
                if (canTurn(testCoord, turn.turns+1)) {
                    openCell.add(new Turn(new Coord(testCoord.x, testCoord.y), turn.turns+1, turn));
                }
            }
        }

        //down
        if (turn.coord.y+1<height) {
            testCoord = new Coord(turn.coord.x, turn.coord.y+1);
            if (!contain(closeCell, testCoord)) {
                if (canTurn(testCoord, turn.turns+1)) {
                    openCell.add(new Turn(new Coord(testCoord.x, testCoord.y), turn.turns+1, turn));
                }
            }
        }

        //left
        if (turn.coord.x-1>=0) {
            testCoord = new Coord(turn.coord.x-1, turn.coord.y);
            if (!contain(closeCell, testCoord)) {
                if (canTurn(testCoord, turn.turns+1)) {
                    openCell.add(new Turn(new Coord(testCoord.x, testCoord.y), turn.turns+1, turn));
                }
            }
        }

        //right
        if (turn.coord.x+1<width) {
            testCoord = new Coord(turn.coord.x+1, turn.coord.y);
            if (!contain(closeCell, testCoord)) {
                if (canTurn(testCoord, turn.turns+1)) {
                    openCell.add(new Turn(new Coord(testCoord.x, testCoord.y), turn.turns+1, turn));
                }
            }
        }

    }

    boolean contain(ArrayList<Turn> closeCell, Coord coord) {
        boolean in = false;
        for (Turn turn:closeCell) {
            if (turn.coord.x == coord.x && turn.coord.y == coord.y) {
                in = true;
                break;
            }
        }

        return in;
    }

    ArrayList<Turn> canMove() {
        ArrayList<Turn> turns = new ArrayList<>(closeCell);
        ArrayList<Turn> del = new ArrayList<>();
        int _x;
        int _y;

        for (Turn t:turns) {
            if (bombBoomThis(getStart(t), true))
                del.add(t);
        }

        for (Entity bomb:bombs) {
            _x = bomb.coord.x;
            _y = bomb.coord.y;

            for (Turn t:turns) {
                if (_x == t.coord.x && _y == t.coord.y) {
                    del.add(t);
                    break;
                }
            }

            for (int x=1; x < my.param2; x++) {
                if (_x + x < width && map_pp[_y][_x + x] < 0) {
                    break;
                }
                for (Turn t:turns) {
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
                for (Turn t:turns) {
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
                for (Turn t:turns) {
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
                for (Turn t:turns) {
                    if (_x == t.coord.x && _y - y == t.coord.y){
                        del.add(t);
                        break;
                    }
                }
            }
        }
        turns.removeAll(del);
        return turns;
    }

    private boolean canTurn(Coord coord, int turn) {
        boolean can = true;
        if (map_pp[coord.y][coord.x] < 0)
            can = false;
        if (can) {
            for(Entity bomb:bombs) {
                if (bomb.coord.x == coord.x && bomb.coord.y == coord.y) {
                    can = false;
                    break;
                }
            }
        }
        if (can) {
            ArrayList<Coord> currentTurnSimBombs = simBombs[turn+1].cells;
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

    Turn getStart(Turn turn) {
        if (turn.pTurn == null)
            return turn;
        if (turn.pTurn.coord.x == my.coord.x && turn.pTurn.coord.y == my.coord.y)
            return turn;
        return getStart(turn.pTurn);
    }

    boolean bombBoomThis(Turn turn, boolean nextTurn) {
        //SysPtErr("t b coord" + turn.coord.x+" "+turn.coord.y);
        boolean isBoom = false;

        for (Entity bomb:bombs) {
            if (true || bomb.param1 == turn.turns) { //пока проверяем не зависимо от хода
                if (nextTurn && bomb.param1 != 2)
                    continue;

                //SysPtErr("b b coord" + bomb.coord.x+" "+bomb.coord.y);
                int _x = bomb.coord.x;
                int _y = bomb.coord.y;

                if (_x == turn.coord.x && _y == turn.coord.y) {
                    isBoom = true;
                }

                if (!isBoom) {
                    for (int x=1; x < bomb.param2; x++) {
                        if (_x + x < width && map_pp[_y][_x + x] < 0) {
                            break;
                        }
                        if (_x + x == turn.coord.x && _y == turn.coord.y) {
                            isBoom = true;
                            break;
                        }
                    }
                }

                if (!isBoom) {
                    for (int x = 1; x < bomb.param2; x++) {
                        if (_x - x > 0 && map_pp[_y][_x - x] < 0) {
                            break;
                        }
                        if (_x - x == turn.coord.x && _y == turn.coord.y) {
                            isBoom = true;
                            break;
                        }
                    }
                }

                if (!isBoom) {
                    for (int y = 1; y < bomb.param2; y++) {
                        if (_y + y < height && map_pp[_y + y][_x] < 0) {
                            break;
                        }
                        if (_x == turn.coord.x && _y + y == turn.coord.y) {
                            isBoom = true;
                            break;
                        }
                    }
                }

                if (!isBoom) {
                    for (int y = 1; y < bomb.param2; y++) {
                        if (_y - y > 0 && map_pp[_y - y][_x] < 0) {
                            break;
                        }
                        if (_x == turn.coord.x && _y - y == turn.coord.y) {
                            isBoom = true;
                            break;
                        }
                    }
                }


            }
        }

        return isBoom;
    }

    boolean canLife(Turn turn) {
        boolean can = true;

        ArrayList<Turn> turns = new ArrayList<>(closeCell);
        ArrayList<Turn> del = new ArrayList<>();
        int _x = turn.coord.x;
        int _y = turn.coord.y;

        for (Turn t:closeCell) {
            if (t.turns>8)
                del.add(t);
        }

        turns.removeAll(del);

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

        turns.removeAll(del);
        for (Entity bomb:bombs) {
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

        turns.removeAll(del);

        SysPtErr("turns.size() " + turns.size());
        if (turns.size() == 0)
            can = false;

        return can;
    }

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
            for (int x=1; x < my.param2; x++) {
                if (e.coord.x + x >= width) {
                    break;
                } else if (map_pp[e.coord.y][e.coord.x + x] < 0) {
                    break;
                }
                t.cells.add(new Coord(e.coord.x+x, e.coord.y));
            }
            SysPtErr(t.cells.size());
            for (int x=1; x < my.param2; x++) {
                if (e.coord.x - x < 0) {
                    break;
                } else if (map_pp[e.coord.y][e.coord.x - x] < 0) {
                    break;
                }
                t.cells.add(new Coord(e.coord.x-x, e.coord.y));
            }
            SysPtErr(t.cells.size());
            for (int y = 1; y < my.param2; y++) {
                if (e.coord.y + y >= height) {
                    break;
                } else if (map_pp[e.coord.y + y][e.coord.x] < 0) {
                    break;
                }
                t.cells.add(new Coord(e.coord.x, e.coord.y+y));
            }
            SysPtErr(t.cells.size());
            for (int y = 1; y < my.param2; y++) {
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

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Coord() {

        }
    }

    private class Turn {
        Coord coord;
        int turns;
        Turn pTurn;

        public Turn(Coord coord, int turns, Turn pTurn) {
            this.coord = coord;
            this.turns = turns;
            this.pTurn = pTurn;
        }
    }

    private class TurnBomb {
        ArrayList<Coord> cells = new ArrayList<>();

    }
}
