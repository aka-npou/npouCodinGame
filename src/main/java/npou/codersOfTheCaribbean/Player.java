package npou.codersOfTheCaribbean;

//просчет на 2 хода
//фолл=получаемый урон
//лучший маршрут на 2 хода


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    private static final boolean DEBUG = false;
    private static final int MAP_WIDTH = 23;
    private static final int MAP_HEIGHT = 21;

    private HashMap<Integer, PointOfGraf> pointOfGrafs;

    private HashMap<Integer, ArrayList<BasePoint>> mapOfCBs = new HashMap<>();

    private int numberTurn = 0;

    private Barrel goToBarrel;
    private Ship goToEnemyShip;
    private Ship fireEnemyShip;
    private Mine goToMine;

    private Barrel shootBarrel;

    private Barrel oldBarrel;

    private int numberShip;

    private static  final int DEEP_NUMBER = 5;
    private static  final int MAX_DEEP_NUMBER_ENEMY = 3;

    private ArrayList<Barrel> barrels = new ArrayList<>();
    private ArrayList<Ship> enemyShips = new ArrayList<>();
    private ArrayList<Ship> enemyShipsTurn2 = new ArrayList<>();
    private ArrayList<Ship> enemyShipsTurn3 = new ArrayList<>();
    private ArrayList<Ship> enemyShipsTurn4 = new ArrayList<>();
    private ArrayList<Ship> enemyShipsTurn5 = new ArrayList<>();
    private ArrayList<Mine> mines = new ArrayList<>();
    private ArrayList<CannonBall> cannonBalls = new ArrayList<>();

    private ArrayList<Barrel> shootBarrels = new ArrayList<>();

    HashMap<Integer, Ship> myShips = new HashMap<>();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        Player player = new Player();
        //player.initGame();
        player.gameLoop(in);
    }

    void  initGame() {
        pointOfGrafs = new HashMap<>();
        for (int i=0; i <MAP_HEIGHT; i++) {
            for (int j=0; j<MAP_WIDTH; j++) {
                pointOfGrafs.put(i*100+j, new PointOfGraf(j, i));
            }
        }

        PointOfGraf p;
        PointOfGraf p1;
        for (int i=0; i <MAP_HEIGHT; i++) {
            for (int j=0; j<MAP_WIDTH; j++) {
                p1 = pointOfGrafs.get(i*100+j);

                //0
                if (j+1 < MAP_WIDTH) {
                    p = pointOfGrafs.get(i*100+(j+1));
                    p1.edges[0] = p;
                    //p.edges.add(p1);
                }

                //1 2
                if (i-1 > -1) {
                    if (i%2 == 0) {
                        p = pointOfGrafs.get((i-1)*100+j);
                        p1.edges[2] = p;
                        //p.edges.add(p1);

                        if (j-1>-1) {
                            p = pointOfGrafs.get((i-1)*100+(j-1));
                            p1.edges[1] = p;
                            //p.edges.add(p1);
                        }
                    } else {
                        p = pointOfGrafs.get((i-1)*100+j);
                        p1.edges[1] = p;
                        //p.edges.add(p1);

                        if (j+1<MAP_WIDTH) {
                            p = pointOfGrafs.get((i-1)*100+(j+1));
                            p1.edges[2] = p;
                            //p.edges.add(p1);
                        }
                    }
                }

                //3
                if (j-1 >-1) {
                    p = pointOfGrafs.get(i*100+(j-1));
                    p1.edges[3] = p;
                    //p.edges.add(p1);
                }

                //4 5
                if (i+1 < MAP_HEIGHT) {
                    if (i%2 == 0) {
                        p = pointOfGrafs.get((i+1)*100+j);
                        p1.edges[5] = p;
                        //p.edges.add(p1);

                        if (j-1>-1) {
                            p = pointOfGrafs.get((i+1)*100+(j-1));
                            p1.edges[4] = p;
                            //p.edges.add(p1);
                        }
                    } else {
                        p = pointOfGrafs.get((i+1)*100+j);
                        p1.edges[4] = p;
                        //p.edges.add(p1);

                        if (j+1<MAP_WIDTH) {
                            p = pointOfGrafs.get((i+1)*100+(j+1));
                            p1.edges[5] = p;
                            //p.edges.add(p1);
                        }
                    }
                }
            }
        }

    }

    private void gameLoop(Scanner in) {
        // game loop
        while (true) {
            numberTurn++;

            barrels.clear();
            //myShips.clear();
            enemyShips.clear();
            mines.clear();
            cannonBalls.clear();

            shootBarrels.clear();

            goToBarrel = null;
            goToEnemyShip = null;
            goToMine = null;

            shootBarrel = null;

            for (Map.Entry<Integer, Ship> entry:myShips.entrySet()) {
                entry.getValue().isDead = true;
            }
            int myShipCount = in.nextInt(); // the number of remaining ships
            int entityCount = in.nextInt(); // the number of entities (e.g. ships, mines or cannonballs)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int x = in.nextInt();
                int y = in.nextInt();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                if (entityType.equals("BARREL")) barrels.add(new Barrel(x, y, arg1));
                if (entityType.equals("SHIP")) {
                    if (arg4 == 1) {
                        Ship myShip = myShips.get(entityId);
                        if (myShip == null) {
                            myShips.put(entityId, new Ship(x, y, arg1, arg2, arg3, arg4, entityId));
                        } else myShip.set(x, y, arg1, arg2, arg3, arg4, false);
                    }
                    if (arg4 == 0) {
                        enemyShips.add(new Ship(x, y, arg1, arg2, arg3, arg4, entityId));
                    }
                }
                if (entityType.equals("MINE")) mines.add(new Mine(x, y));
                if (entityType.equals("CANNONBALL")) {
                    cannonBalls.add(new CannonBall(x, y, arg1, arg2));
                    SysPrErr(x+" "+y+" "+arg2);
                }
            }

            setMapCBs();

            //SysPrErr(oldBarrel);

            /* mineCount=7
            seed=438864521
            barrelCount=21
            shipsPerPlayer=1 */

            getShootBarrels();
            SysPrErr("shootBarrels "+shootBarrels.size());

            numberShip = 0;
            for (Map.Entry<Integer, Ship> entry:myShips.entrySet()) {
                numberShip++;
                Ship ship = entry.getValue();
                SysPrErr(ship.coord.x + " " + ship.coord.y + " " + ship.arg1 + " " + ship.arg2);
                if (ship.isDead) continue;
                SysPrErr("//////////////////////////////////////////////ship "+numberShip);

                ship.turn = false;

                fireEnemyShip = getNearestShipToFire(ship);
                goToEnemyShip = getNearestShipToMove(ship);

                if (barrels.size() != 0) {
                    barrels.sort((o1, o2) -> (ship.coord.distanceTo(o1.coord)-ship.coord.distanceTo(o2.coord)));
                    if (barrels.size() > 1 && oldBarrel != null && barrels.get(1).coord.x == oldBarrel.coord.x && barrels.get(1).coord.y == oldBarrel.coord.y) {
                        goToBarrel = barrels.get(1);
                        //SysPrErr("b1");
                    } else
                        goToBarrel = barrels.get(0);
                }

                if (mines.size() != 0) {
                    mines.sort((o1, o2) -> (ship.coord.distanceTo(o1.coord)-ship.coord.distanceTo(o2.coord)));
                    goToMine = mines.get(0);
                }

                if (shootBarrels.size() != 0) {
                    shootBarrels.sort((o1, o2) -> (ship.coord.toCubeCoordinate().distanceTo(o1.coord.toCubeCoordinate())-ship.coord.toCubeCoordinate().distanceTo(o2.coord.toCubeCoordinate())));
                    if (ship.coord.toCubeCoordinate().distanceTo(shootBarrels.get(0).coord.toCubeCoordinate())< 10)
                        shootBarrel = shootBarrels.get(0);
                }
                boolean canFire;
                //надо б увернуться
                //if (needDodge(ship)) {
                if (1==2) {
                    dodge(ship);
                    ship.oldTurnFire = false;
                    ship.turn = true;
                } else {
                    if (!ship.oldTurnFire && shootBarrel != null && canDodge(ship, Action.FIRE)) {
                        System.out.println("FIRE " + shootBarrel.coord.x + " " + shootBarrel.coord.y);
                        ship.oldTurnFire = true;
                        ship.turn = true;

                        //тогда плывем к бочке
                    }
                    //можем выстрелить в противника
                    else if (!ship.oldTurnFire && fireEnemyShip != null && ship.coord.distanceTo(fireEnemyShip.coord) < 5 && canDodge(ship, Action.FIRE)) {
                        //SysPrErr("f1");
                        Coord coord = getCoordEnemyShip(ship, fireEnemyShip);
                        System.out.println("FIRE " + coord.x + " " + coord.y);
                        ship.oldTurnFire = true;
                        ship.turn = true;
                    /*} else if (!ship.oldTurnFire && shootBarrel != null && canDodge(ship, Action.FIRE)) {
                        System.out.println("FIRE " + shootBarrel.coord.x + " " + shootBarrel.coord.y);
                        ship.oldTurnFire = true;
                        ship.turn = true;*/

                        //тогда плывем к бочке
                    } else if(goToBarrel != null) {
                        //SysPrErr("m");
                        canFire = move(ship, goToBarrel.coord, 0);
                        if (canFire) {
                            //SysPrErr("cf1");
                            if (!ship.oldTurnFire && goToMine != null && ship.coord.distanceTo(goToMine.coord) < 10) {
                                System.out.println("FIRE " + goToMine.coord.x + " " + goToMine.coord.y);
                                ship.oldTurnFire = true;
                                ship.turn = true;
                            }

                            if (!ship.oldTurnFire && goToEnemyShip != null && ship.coord.distanceTo(goToEnemyShip.coord) < 10) {
                                Coord coord = getCoordEnemyShip(ship, goToEnemyShip);
                                System.out.println("FIRE " + coord.x + " " + coord.y);
                                ship.oldTurnFire = true;
                                ship.turn = true;
                            }

                        } else {
                            ship.oldTurnFire = false;
                        }
                        //нет бочек, плывем к противнику
                    } else if (goToEnemyShip != null) {
                        //SysPrErr("ge");
                        canFire = move(ship, goToEnemyShip.coord, 1);
                        if (canFire) {
                            //SysPrErr("cf2");
                            if (!ship.oldTurnFire && goToMine != null && ship.coord.distanceTo(goToMine.coord) < 10) {
                                System.out.println("FIRE " + goToMine.coord.x + " " + goToMine.coord.y);
                                ship.oldTurnFire = true;
                                ship.turn = true;
                            }

                            if (!ship.oldTurnFire && fireEnemyShip != null && ship.coord.distanceTo(fireEnemyShip.coord) < 10) {
                                Coord coord = getCoordEnemyShip(ship, fireEnemyShip);
                                System.out.println("FIRE " + coord.x + " " + coord.y);
                                ship.oldTurnFire = true;
                                ship.turn = true;
                            }

                        } else {
                            ship.oldTurnFire = false;
                        }
                    }
                }

                //надо как-то смотреть что и так норм плывем, тогда пальнуть просто куда-то
                if (!ship.turn) {
                    System.out.println("MINE");
                }
                oldBarrel = goToBarrel;

            }
        }
    }


    void dodge(Ship ship) {

    }

    private void checkPosition(ArrayList<ToGo> avaibleCoords, Ship ship, int deep, Action action, int max_deep) {
        Coord bowCoord = getBowCoord(ship.arg1, ship.coord);
        Coord sternCoord = getSternCoord(ship.arg1, ship.coord);

        if (isOkCoord(ship, deep)) {
            //SysPrErr("d a"+deep+" "+action);
            //SysPrErr("s c/c p"+ship.coord.x+" "+ship.coord.y+" "+ship.arg1);
            //SysPrErr("s c/c p"+bowCoord.x+" "+bowCoord.y);
            //SysPrErr("s c/c p"+sternCoord.x+" "+sternCoord.y);
            ArrayList<BasePoint> arrayList = mapOfCBs.get(deep);

            int fool=0;

            if (arrayList != null) {
                for (BasePoint basePoint : arrayList) {
                    if (basePoint.coord.x == ship.coord.x && basePoint.coord.y == ship.coord.y) {
                        fool +=50;
                    }
                    if (basePoint.coord.x == bowCoord.x && basePoint.coord.y == bowCoord.y) {
                        fool +=25;
                    }
                    if (basePoint.coord.x == sternCoord.x && basePoint.coord.y == sternCoord.y) {
                        fool +=25;
                    }
                }
            }

            for (Mine mine:mines) {
                if (mine.coord.x == ship.coord.x && mine.coord.y == ship.coord.y) {
                    fool +=10;
                }
                if (mine.coord.x == bowCoord.x && mine.coord.y == bowCoord.y) {
                    fool +=10;
                }
                if (mine.coord.x == sternCoord.x && mine.coord.y == sternCoord.y) {
                    fool +=10;
                }
            }

            //SysPrErr("fool "+fool);

            if (deep < max_deep) {
                //Ship ship = myShips.get(entityId);
                //int arg2=ship.arg2;
                //if (action == Action.FASTER) arg2++;
                //if (action == Action.SLOWER) arg2--;
                if (action == Action.FASTER) ship.arg2++;
                if (action == Action.SLOWER) ship.arg2--;

                //avaibleCoords.addAll(checkDeep(new Ship(shipCoord.x, shipCoord.y, arg1, arg2, ship.arg3, ship.arg4, entityId), deep+1));
                ToGo toGo = new ToGo(ship.coord, action, fool);
                //SysPrErr("deep "+ (deep+1));
                //надо просимулировать мир на  ход, а то корабли  остаются на месте
                simEnemyShip(deep+1);
                toGo.nextTurns = checkDeep(ship, deep+1, max_deep);
                //SysPrErr("d t "+ toGo.nextTurns.size());
                avaibleCoords.add(toGo);
            } else {
                avaibleCoords.add(new ToGo(ship.coord, action, fool));
            }
        } else {
            avaibleCoords.add(new ToGo(ship.coord, action, (DEEP_NUMBER-deep+1)*1000));
        }
    }

    private ArrayList<ToGo> checkDeep(Ship ship, int deep, int max_deep) {
        //SysPrErr("s c"+ship.coord.x+" "+ship.coord.y);
        ArrayList<ToGo> avaibleCoords = new ArrayList<>();

        Ship testShip = new Ship(ship);

        //int arg1;
        if (ship.arg2 == 0) {
            SysPrErr("s0");
            //faster
            //arg1 = ship.arg1;
            testShip.coord = getCoordToMove(testShip, 1);
            checkPosition(avaibleCoords, testShip, deep, Action.FASTER, max_deep);

            //port
            testShip.set(ship);
            SysPrErr("t s a1"+ship.arg1);
            testShip.arg1++;
            //arg1 = ship.arg1;
            if (testShip.arg1 > 5) testShip.arg1=0;

            checkPosition(avaibleCoords, testShip, deep, Action.PORT, max_deep);

            //starboard
            testShip.set(ship);
            //arg1 = ship.arg1;
            testShip.arg1--;
            if (testShip.arg1 < 0) testShip.arg1=5;
            checkPosition(avaibleCoords, testShip, deep, Action.STARBOARD, max_deep);

            //wait
            testShip.set(ship);
            //arg1 = ship.arg1;
            checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
        }

        if (ship.arg2 == 1) {
            SysPrErr("s1");
            //slower
            testShip.set(ship);
            //arg1 = ship.arg1;
            checkPosition(avaibleCoords, testShip, deep, Action.SLOWER, max_deep);

            //faster
            testShip.set(ship);
            //arg1 = ship.arg1;
            testShip.coord = getCoordToMove(testShip, 1);
            if (isOkCoord(testShip, deep)) {
                testShip.coord = getCoordToMove(testShip, 1);
                if (isOkCoord(testShip, deep)) {
                    checkPosition(avaibleCoords, testShip, deep, Action.FASTER, max_deep);
                } else {
                    testShip.coord.set(ship.coord);
                    testShip.coord = getCoordToMove(testShip, 1);
                    testShip.arg2 = 0;
                    checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                }
            } else {
                testShip.coord.set(ship.coord);
                testShip.arg2 = 0;
                checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
            }
            //checkPosition(ship.entityId, avaibleCoords, getCoordToMove(ship, 2), arg1, deep, Action.FASTER);

            //port
            testShip.set(ship);
            //arg1 = ship.arg1;
            //testShip.arg1++;
            //if (testShip.arg1 > 5) testShip.arg1=0;
            testShip.coord = getCoordToMove(testShip, 1);
            if (isOkCoord(testShip, deep)) {
                testShip.arg1++;
                if (testShip.arg1 > 5) testShip.arg1=0;
                if (isOkCoord(testShip, deep)) {
                    checkPosition(avaibleCoords, testShip, deep, Action.PORT, max_deep);
                } else {
                    testShip.arg1--;
                    if (testShip.arg1 < 0) testShip.arg1=5;
                    testShip.arg2 = 0;
                    checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                }
            } else {
                testShip.coord.set(ship.coord);
                testShip.arg2 = 0;
                testShip.arg1++;
                if (testShip.arg1 > 5) testShip.arg1=0;
                if (isOkCoord(testShip, deep)) {
                    checkPosition(avaibleCoords, testShip, deep, Action.PORT, max_deep);
                } else {
                    testShip.arg1--;
                    if (testShip.arg1 < 0) testShip.arg1=5;
                    checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                }
            }
            //checkPosition(ship.entityId, avaibleCoords, getCoordToMove(ship, 1), arg1, deep, Action.PORT);

            //starboard
            testShip.set(ship);
            //arg1 = ship.arg1;
            //arg1--;
            //if (arg1 < 0) arg1=5;
            testShip.coord = getCoordToMove(testShip, 1);
            if (isOkCoord(testShip, deep)) {
                testShip.arg1--;
                if (testShip.arg1 < 0) testShip.arg1=5;
                if (isOkCoord(testShip, deep)) {
                    checkPosition(avaibleCoords, testShip, deep, Action.STARBOARD, max_deep);
                } else {
                    testShip.arg1++;
                    if (testShip.arg1 > 5) testShip.arg1=0;
                    testShip.arg2 = 0;
                    checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                }
            } else {
                testShip.coord.set(ship.coord);
                testShip.arg2 = 0;
                testShip.arg1--;
                if (testShip.arg1 < 0) testShip.arg1=5;
                if (isOkCoord(testShip, deep)) {
                    checkPosition(avaibleCoords, testShip, deep, Action.STARBOARD, max_deep);
                } else {
                    testShip.arg1++;
                    if (testShip.arg1 > 5) testShip.arg1=0;
                    checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                }
            }
            //checkPosition(ship.entityId, avaibleCoords, getCoordToMove(ship, 1), arg1, deep, Action.STARBOARD);

            //wait
            testShip.set(ship);
            //arg1 = ship.arg1;
            testShip.coord = getCoordToMove(testShip, 1);
            if (isOkCoord(testShip, deep)) {
                checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
            } else {
                testShip.coord.set(ship.coord);
                testShip.arg2 = 0;
                checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
            }
            //checkPosition(ship.entityId, avaibleCoords,getCoordToMove(ship, 1), arg1, deep, Action.WAIT);
        }

        if (ship.arg2 == 2) {
            SysPrErr("s2");
            //slower
            testShip.set(ship);
            //arg1 = ship.arg1;
            testShip.coord = getCoordToMove(testShip, 1);
            if (isOkCoord(testShip, deep)) {
                checkPosition(avaibleCoords, testShip, deep, Action.SLOWER, max_deep);
            } else {
                testShip.coord.set(ship.coord);
                testShip.arg2 = 0;
                checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
            }
            //checkPosition(ship.entityId, avaibleCoords,getCoordToMove(ship, 1), arg1, deep, Action.SLOWER);

            //port
            testShip.set(ship);
            //arg1 = ship.arg1;
            //arg1++;
            //if (arg1 > 5) arg1=0;
            testShip.coord = getCoordToMove(testShip, 1);
            if (isOkCoord(testShip, deep)) {
                //2
                testShip.coord = getCoordToMove(testShip, 1);
                if (isOkCoord(testShip, deep)) {
                    testShip.arg1++;
                    if (testShip.arg1 > 5) testShip.arg1=0;
                    if (isOkCoord(testShip, deep)) {
                        checkPosition(avaibleCoords, testShip, deep, Action.PORT, max_deep);
                    } else {
                        testShip.arg1--;
                        if (testShip.arg1 < 0) testShip.arg1=5;
                        testShip.arg2 = 0;
                        checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                    }
                } else {
                    testShip.coord.set(ship.coord);
                    testShip.coord = getCoordToMove(testShip, 1);
                    testShip.arg1++;
                    if (testShip.arg1 > 5) testShip.arg1=0;
                    if (isOkCoord(testShip, deep)) {
                        checkPosition(avaibleCoords, testShip, deep, Action.PORT, max_deep);
                    } else {
                        testShip.arg1--;
                        if (testShip.arg1 < 0) testShip.arg1=5;
                        testShip.arg2 = 0;
                        checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                    }
                }
            } else {
                testShip.coord.set(ship.coord);
                testShip.arg2 = 0;
                testShip.arg1++;
                if (testShip.arg1 > 5) testShip.arg1=0;
                if (isOkCoord(testShip, deep)) {
                    checkPosition(avaibleCoords, testShip, deep, Action.PORT, max_deep);
                } else {
                    testShip.arg1--;
                    if (testShip.arg1 < 0) testShip.arg1=5;
                    checkPosition(avaibleCoords, testShip, deep, Action.SLOWER, max_deep);
                }
            }
            //checkPosition(ship.entityId, avaibleCoords,getCoordToMove(ship, 2), arg1, deep, Action.PORT);

            //starboard
            testShip.set(ship);
            //arg1 = ship.arg1;
            //arg1--;
            //if (arg1 < 0) arg1=5;
            testShip.coord = getCoordToMove(testShip, 1);
            if (isOkCoord(testShip, deep)) {
                //2
                testShip.coord = getCoordToMove(testShip, 1);
                if (isOkCoord(testShip, deep)) {
                    testShip.arg1--;
                    if (testShip.arg1 < 0) testShip.arg1 = 5;
                    if (isOkCoord(testShip, deep)) {
                        checkPosition(avaibleCoords, testShip, deep, Action.STARBOARD, max_deep);
                    } else {
                        testShip.arg1++;
                        if (testShip.arg1 > 5) testShip.arg1 = 0;
                        testShip.arg2 = 0;
                        checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                    }
                } else {
                    testShip.coord.set(ship.coord);
                    testShip.coord = getCoordToMove(testShip, 1);
                    testShip.arg1--;
                    if (testShip.arg1 < 0) testShip.arg1 = 5;
                    if (isOkCoord(testShip, deep)) {
                        checkPosition(avaibleCoords, testShip, deep, Action.STARBOARD, max_deep);
                    } else {
                        testShip.arg1++;
                        if (testShip.arg1 > 5) testShip.arg1 = 0;
                        testShip.arg2 = 0;
                        checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                    }
                }
            } else {
                testShip.coord.set(ship.coord);
                testShip.arg2 = 0;
                testShip.arg1--;
                if (testShip.arg1 < 0) testShip.arg1=5;
                if (isOkCoord(testShip, deep)) {
                    checkPosition(avaibleCoords, testShip, deep, Action.STARBOARD, max_deep);
                } else {
                    testShip.arg1++;
                    if (testShip.arg1 > 5) testShip.arg1=0;
                    checkPosition(avaibleCoords, testShip, deep, Action.SLOWER, max_deep);
                }
            }
            //checkPosition(ship.entityId, avaibleCoords,getCoordToMove(ship, 2), arg1, deep, Action.STARBOARD);

            //wait
            testShip.set(ship);
            //arg1 = ship.arg1;
            testShip.coord = getCoordToMove(testShip, 1);
            if (isOkCoord(testShip, deep)) {
                testShip.coord = getCoordToMove(testShip, 1);
                SysPrErr(testShip.coord.x + " " + testShip.coord.y);
                if (isOkCoord(testShip, deep)) {
                    checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                } else {
                    testShip.coord.set(ship.coord);
                    testShip.coord = getCoordToMove(testShip, 1);
                    testShip.arg2 = 0;
                    checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
                }
            } else {
                testShip.coord.set(ship.coord);
                testShip.arg2 = 0;
                checkPosition(avaibleCoords, testShip, deep, Action.WAIT, max_deep);
            }
            //checkPosition(ship.entityId, avaibleCoords,getCoordToMove(ship, 2), arg1, deep, Action.WAIT);
        }
        return avaibleCoords;
    }

    private boolean move(Ship ship, Coord coord, int minDist) {
        SysPrErr("to coord "+coord.x+" "+coord.y);
        boolean canFire = true;

        ArrayList<ToGo> avaibleCoords = checkDeep(ship,1, DEEP_NUMBER);

        SysPrErr("a c "+avaibleCoords.size());
        ToGo currentAction = null;
        int bestDist=-1;
        int currentDist = -1;
        ToGo nextAction = null;
        ToGo nextAction1 = null;
        ToGo nextAction2 = null;
        ToGo nextAction3 = null;
        //avaibleCoords.sort((o1, o2) -> (coord.distanceTo(o1.coord)-coord.distanceTo(o2.coord)));
        for (ToGo a:avaibleCoords) {
            nextAction = null;
            //nextAction1 = null;
            //nextAction2 = null;
            if (a.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate()) < minDist) continue;
            SysPrErr("//");
            SysPrErr("1 "+a.action + " " + a.coord.x + " " + a.coord.y);
            SysPrErr("1 "+(a.fool+a.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())));

            currentDist = a.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+a.fool;
            for (ToGo toGo:a.nextTurns) {
                nextAction1 = null;
                SysPrErr("2 "+toGo.action + " " + toGo.coord.x + " " + toGo.coord.y);
                SysPrErr("2 "+(toGo.fool+a.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())));

                for (ToGo toGo1:toGo.nextTurns) {
                    nextAction2 = null;
                    for (ToGo toGo2:toGo1.nextTurns) {
                        nextAction3 = null;
                        for (ToGo toGo3:toGo2.nextTurns) {
                            if (nextAction3 == null) {
                                nextAction3 = toGo3;
                                continue;
                            }
                            if (nextAction3.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction3.fool > toGo3.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+toGo3.fool) {
                                nextAction3 = toGo3;
                            }
                        }
                        if (nextAction2 == null) {
                            nextAction2 = toGo2;
                            continue;
                        }
                        if (nextAction2.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction2.fool > toGo2.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+toGo2.fool) {
                            nextAction2 = toGo2;
                        }
                    }
//                    if (nextAction2 != null) {
//                        currentDist += nextAction2.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction2.fool;
//                    }
                    if (nextAction1 == null) {
                        nextAction1 = toGo1;
                        continue;
                    }
                    if (nextAction1.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction1.fool > toGo1.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+toGo1.fool) {
                        nextAction1 = toGo1;
                    }
                }
//                if (nextAction1 != null) {
//                    currentDist += nextAction1.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction1.fool;
//                }
                if (nextAction == null) {
                    nextAction = toGo;
                    continue;
                }
                if (nextAction.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction.fool > toGo.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+toGo.fool) {
                    nextAction = toGo;
                }
            }
            if (nextAction != null) {
                currentDist += nextAction.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction.fool;
            }
            if (nextAction1 != null) {
                currentDist += nextAction1.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction1.fool;
            }
            if (nextAction2 != null) {
                currentDist += nextAction2.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction2.fool;
            }
            if (nextAction3 != null) {
                currentDist += nextAction3.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction3.fool;
            }

            SysPrErr("b/c "+bestDist + " " + currentDist);
            if (currentAction == null) {
                currentAction = a;
               /* bestDist = a.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+a.fool;
                for (ToGo toGo:a.nextTurns) {
                    if (nextAction == null) {
                        nextAction = toGo;
                        continue;
                    }
                    if (nextAction.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+nextAction.fool > toGo.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+toGo.fool) {
                        nextAction = toGo;
                    }
                }
                if (nextAction != null) {
                    bestDist += nextAction.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+currentAction.fool;
                }*/
                bestDist = currentDist;
                continue;
            }

            //if (currentAction.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+currentAction.fool > a.coord.toCubeCoordinate().distanceTo(coord.toCubeCoordinate())+a.fool) currentAction = a;
            if (bestDist > currentDist) {
                currentAction = a;
                bestDist = currentDist;
            }
        }
        if (currentAction != null && currentAction.action != Action.WAIT) {
            canFire = false;
            //SysPrErr(currentAction.action);
            System.out.println(currentAction.action.toString());
            ship.turn = true;
        }
        return canFire;
    }

    private boolean isOkCoord(Ship ship, int deep) {
        boolean isOk = true;
        if (ship.coord.x < 0 || ship.coord.x>22 || ship.coord.y < 0 || ship.coord.y>20) isOk = false;

        Coord bowCoord = getBowCoord(ship.arg1, ship.coord);
        Coord sternCoord = getSternCoord(ship.arg1, ship.coord);

        //SysPrErr(ship.coord.x+" "+ship.coord.y);
        //SysPrErr(bowCoord.x+" "+bowCoord.y);
        //SysPrErr(sternCoord.x+" "+sternCoord.y);

        if (isOk) {
            //SysPrErr("ok1");
            ArrayList<Ship> enemyShipsCurrentTurn = new ArrayList<>();
            if (deep == 1)
                enemyShipsCurrentTurn.addAll(enemyShips);
            else if (deep == 2)
                enemyShipsCurrentTurn.addAll(enemyShipsTurn2);
            else if (deep == 3)
                enemyShipsCurrentTurn.addAll(enemyShipsTurn3);
            else if (deep == 4)
                enemyShipsCurrentTurn.addAll(enemyShipsTurn4);
            else
                enemyShipsCurrentTurn.addAll(enemyShipsTurn5);

            for (Ship enemyShip : enemyShipsCurrentTurn) {
                Coord bowEnemyCoord = getBowCoord(enemyShip.arg1, enemyShip.coord);
                Coord sternEnemyCoord = getSternCoord(enemyShip.arg1, enemyShip.coord);
                //SysPrErr(enemyShip.coord.x+" "+enemyShip.coord.y);
                //SysPrErr(bowEnemyCoord.x+" "+bowEnemyCoord.y);
                //SysPrErr(sternEnemyCoord.x+" "+sternEnemyCoord.y);
                if (ship.coord.x == enemyShip.coord.x && ship.coord.y == enemyShip.coord.y) isOk = false;
                if (ship.coord.x == bowEnemyCoord.x && ship.coord.y == bowEnemyCoord.y) isOk = false;
                if (ship.coord.x == sternEnemyCoord.x && ship.coord.y == sternEnemyCoord.y) isOk = false;

                if (bowCoord.x == enemyShip.coord.x && bowCoord.y == enemyShip.coord.y) isOk = false;
                if (bowCoord.x == bowEnemyCoord.x && bowCoord.y == bowEnemyCoord.y) isOk = false;
                if (bowCoord.x == sternEnemyCoord.x && bowCoord.y == sternEnemyCoord.y) isOk = false;

                if (sternCoord.x == enemyShip.coord.x && sternCoord.y == enemyShip.coord.y) isOk = false;
                if (sternCoord.x == bowEnemyCoord.x && sternCoord.y == bowEnemyCoord.y) isOk = false;
                if (sternCoord.x == sternEnemyCoord.x && sternCoord.y == sternEnemyCoord.y) isOk = false;

            }
        }

        Ship otherShip;
        if (isOk) {
            //SysPrErr("ok2");
            for (Map.Entry<Integer, Ship> entry:myShips.entrySet()) {
                //if (entry.getKey().equals(entityId)) continue;
                otherShip = entry.getValue();
                if (otherShip.entityId == ship.entityId) continue;
                Coord bowMyCoord = getBowCoord(otherShip.arg1, otherShip.coord);
                Coord sternMyCoord = getSternCoord(otherShip.arg1, otherShip.coord);

                //SysPrErr(otherShip.coord.x+" "+otherShip.coord.y);
                //SysPrErr(bowMyCoord.x+" "+bowMyCoord.y);
                //SysPrErr(sternMyCoord.x+" "+sternMyCoord.y);

                if (ship.coord.x == otherShip.coord.x && ship.coord.y == otherShip.coord.y) isOk = false;
                if (ship.coord.x == bowMyCoord.x && ship.coord.y == bowMyCoord.y) isOk = false;
                if (ship.coord.x == sternMyCoord.x && ship.coord.y == sternMyCoord.y) isOk = false;

                if (bowCoord.x == otherShip.coord.x && bowCoord.y == otherShip.coord.y) isOk = false;
                if (bowCoord.x == bowMyCoord.x && bowCoord.y == bowMyCoord.y) isOk = false;
                if (bowCoord.x == sternMyCoord.x && bowCoord.y == sternMyCoord.y) isOk = false;

                if (sternCoord.x == otherShip.coord.x && sternCoord.y == otherShip.coord.y) isOk = false;
                if (sternCoord.x == bowMyCoord.x && sternCoord.y == bowMyCoord.y) isOk = false;
                if (sternCoord.x == sternMyCoord.x && sternCoord.y == sternMyCoord.y) isOk = false;
            }
        }

        //SysPrErr(isOk);
        return isOk;
    }

    private boolean canDodge(Ship ship, Action action) {
        boolean canDodge = true;
        Ship newShip;
        Coord bowCoord;
        Coord sternCoord;
        ArrayList<BasePoint> arrayList = mapOfCBs.get(1);
        if (arrayList != null) {
            SysPrErr("cd 1");
            newShip = getShipToNewCoord(ship, 1, action);
            bowCoord = getBowCoord(newShip.arg1, newShip.coord);
            sternCoord = getSternCoord(newShip.arg1, newShip.coord);
            SysPrErr(newShip.coord.x+" "+newShip.coord.y);
            SysPrErr(bowCoord.x+" "+bowCoord.y);
            SysPrErr(sternCoord.x+" "+sternCoord.y);
            for (BasePoint basePoint:arrayList) {
                if (basePoint.coord.x == newShip.coord.x && basePoint.coord.y == newShip.coord.y) {
                    canDodge = false;
                    break;
                }
                if (basePoint.coord.x == bowCoord.x && basePoint.coord.y == bowCoord.y) {
                    canDodge = false;
                    break;
                }
                if (basePoint.coord.x == sternCoord.x && basePoint.coord.y == sternCoord.y) {
                    canDodge = false;
                    break;
                }
            }
        }

        arrayList = mapOfCBs.get(2);
        if (arrayList != null) {
            SysPrErr("cd 2");
            newShip = getShipToNewCoord(ship, 1, Action.WAIT);
            bowCoord = getBowCoord(newShip.arg1, newShip.coord);
            sternCoord = getSternCoord(newShip.arg1, newShip.coord);

            SysPrErr(newShip.coord.x+" "+newShip.coord.y);
            SysPrErr(bowCoord.x+" "+bowCoord.y);
            SysPrErr(sternCoord.x+" "+sternCoord.y);

            for (BasePoint basePoint:arrayList) {
                if (basePoint.coord.x == newShip.coord.x && basePoint.coord.y == newShip.coord.y) {
                    canDodge = false;
                    break;
                }
                if (basePoint.coord.x == bowCoord.x && basePoint.coord.y == bowCoord.y) {
                    canDodge = false;
                    break;
                }
                if (basePoint.coord.x == sternCoord.x && basePoint.coord.y == sternCoord.y) {
                    canDodge = false;
                    break;
                }
            }
        }
        return canDodge;
    }

    private Ship getShipToNewCoord(Ship ship, int turn, Action action) {
        Ship newShip = new Ship(ship.coord.x, ship.coord.y, ship.arg1, ship.arg2, ship.arg3, ship.arg4, ship.entityId);
        for (int i = 0; i < turn; i++) {
            if (ship.arg2 == 0) {

                if (action == Action.MOVE) {
                    newShip.coord = getCoordToMove(ship, 1);
                }
                if (action == Action.PORT) {
                    newShip.arg1++;
                    if (newShip.arg1 > 5) newShip.arg1 = 0;
                }
                if (action == Action.STARBOARD) {
                    newShip.arg1--;
                    if (newShip.arg1 < 0) newShip.arg1 = 5;
                }
                if (action == Action.FASTER) {
                    newShip.coord = getCoordToMove(ship, 1);
                }
            }
            if (ship.arg2 == 1) {
                if (action == Action.MOVE) {
                    newShip.coord = getCoordToMove(ship, 1);
                }
                if (action == Action.PORT) {
                    newShip.coord = getCoordToMove(ship, 1);
                    newShip.arg1++;
                    if (newShip.arg1 > 5) newShip.arg1 = 0;
                }
                if (action == Action.STARBOARD) {
                    newShip.coord = getCoordToMove(ship, 1);
                    newShip.arg1--;
                    if (newShip.arg1 < 0) newShip.arg1 = 5;
                }
                if (action == Action.SLOWER) {

                }
                if (action == Action.FASTER) {
                    newShip.coord = getCoordToMove(ship, 2);
                }
                if (action == Action.WAIT) {
                    newShip.coord = getCoordToMove(ship, 1);
                }
                if (action == Action.FIRE) {
                    newShip.coord = getCoordToMove(ship, 1);
                }
            }
            if (ship.arg2 == 2) {
                if (action == Action.MOVE) {
                    newShip.coord = getCoordToMove(ship, 2);
                }
                if (action == Action.PORT) {
                    newShip.coord = getCoordToMove(ship, 2);
                    newShip.arg1++;
                    if (newShip.arg1 > 5) newShip.arg1 = 0;
                }
                if (action == Action.STARBOARD) {
                    newShip.coord = getCoordToMove(ship, 2);
                    newShip.arg1--;
                    if (newShip.arg1 < 0) newShip.arg1 = 5;
                }
                if (action == Action.SLOWER) {
                    newShip.coord = getCoordToMove(ship, 1);
                }
                if (action == Action.WAIT) {
                    newShip.coord = getCoordToMove(ship, 2);
                }
                if (action == Action.FIRE) {
                    newShip.coord = getCoordToMove(ship, 2);
                }
            }
        }
        return newShip;
    }

    private void setMapCBs() {
        mapOfCBs.clear();
        for(CannonBall cannonBall:cannonBalls) {
            ArrayList<BasePoint> arrayList = mapOfCBs.get(cannonBall.arg2);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                mapOfCBs.put(cannonBall.arg2, arrayList);
            }
            arrayList.add(new BasePoint(cannonBall.coord.x, cannonBall.coord.y, EntityType.CANNONBALL));

        }
        //SysPrErr(mapOfCBs.size());
    }

    private Coord getCoordEnemyShip(Ship ship, Ship _enemyShip) {
        //не стрелять по себе
        Coord coord = new Coord(_enemyShip.coord.x, _enemyShip.coord.y);
        int distance = ship.coord.neighbor(ship.arg1).distanceTo(_enemyShip.coord);
        int travelTime = (int) (1 + Math.round(distance / 3.0));

        Ship enemyShip = new Ship(_enemyShip);

        if (enemyShip.arg2 ==2) {
            travelTime = 1 + (int) Math.round(travelTime / 2.0);
        }


        int deep = travelTime;
        if (deep > MAX_DEEP_NUMBER_ENEMY) deep = MAX_DEEP_NUMBER_ENEMY;

        ArrayList<ToGo> avaibleEnemyCoord = checkDeep(enemyShip, 1, deep);
        SysPrErr("aec "+avaibleEnemyCoord.size());
        avaibleEnemyCoord.sort((o1, o2) -> (enemyShip.coord.toCubeCoordinate().distanceTo(o2.coord.toCubeCoordinate())-enemyShip.coord.toCubeCoordinate().distanceTo(o1.coord.toCubeCoordinate())));
        if (avaibleEnemyCoord.size() != 0 && avaibleEnemyCoord.get(0).fool<1000)
            coord = avaibleEnemyCoord.get(0).coord;
        /*HashMap<Integer, ToGo> enemyCoord = getEnemyCoord(avaibleEnemyCoord, ship, 1);

        if (enemyCoord.size() != 0) {
            ArrayList<ToGo> toGos = new ArrayList<>(enemyCoord.values());
            toGos.sort((o1, o2) -> (o2.fool - o1.fool));

            coord = new Coord(toGos.get(0).coord.x, toGos.get(0).coord.y);
        } else {
            coord = new Coord(enemyShip.coord.x, enemyShip.coord.y);
        }*/

        //Coord coord = new Coord(enemyShip.coord.x, enemyShip.coord.y);

        /*SysPrErr("t t"+travelTime);
        SysPrErr("d"+distance);
        SysPrErr("s c"+coord.x+" "+coord.y);
        if (enemyShip.arg2 != 0) {
            int dx = 0;
            if (enemyShip.coord.y % 2 == 0) {
                if (enemyShip.arg1 == 2 || enemyShip.arg1 == 4)
                    dx = (int) Math.round(travelTime / 2.0);
            } else {
                if (enemyShip.arg1 == 1 || enemyShip.arg1 == 5)
                    dx = 1 + (int) Math.round(travelTime / 2.0);
            }

            if (enemyShip.arg2 ==2) {
                travelTime = 1 + (int) Math.round(travelTime / 2.0);
                dx = 1 + (int) Math.round(dx / 2.0);
            }

            if (enemyShip.arg1 == 0) {
                coord = new Coord(enemyShip.coord.x + travelTime, enemyShip.coord.y);
            }
            if (enemyShip.arg1 == 1) {
                coord = new Coord(enemyShip.coord.x + dx, enemyShip.coord.y - travelTime);
            }
            if (enemyShip.arg1 == 2) {
                coord = new Coord(enemyShip.coord.x - dx, enemyShip.coord.y - travelTime);
            }
            if (enemyShip.arg1 == 3) {
                coord = new Coord(enemyShip.coord.x - travelTime, enemyShip.coord.y);
            }
            if (enemyShip.arg1 == 4) {
                coord = new Coord(enemyShip.coord.x - dx, enemyShip.coord.y + travelTime);
            }
            if (enemyShip.arg1 == 5) {
                coord = new Coord(enemyShip.coord.x + dx, enemyShip.coord.y + travelTime);
            }
        }*/

        if (coord.x < 0) coord.x = 0;
        if (coord.x > 22) coord.x = 22;
        if (coord.y < 0) coord.y = 0;
        if (coord.y > 20) coord.y = 20;

        return coord;
    }

    private HashMap<Integer, ToGo> getEnemyCoord(ArrayList<ToGo> avaibleEnemyCoord, Ship ship, int deep) {
        HashMap<Integer, ToGo> enemyCoord = new HashMap<>();
        int distance;
        int travelTime;
        Coord bowCoord;
        Coord sternCoord;
        ToGo t;
        int i;
        for (ToGo toGo:avaibleEnemyCoord) {
            distance = ship.coord.neighbor(ship.arg1).distanceTo(toGo.coord);
            travelTime = (int) (1 + Math.round(distance / 3.0));
            if (travelTime == deep) {
                i = toGo.coord.y * 100 + toGo.coord.x;
                if (enemyCoord.get(i) == null) {
                    t = new ToGo(toGo.coord, Action.WAIT, 1);
                    enemyCoord.put(i, t);
                } else {
                    t = enemyCoord.get(i);
                    t.fool++;
                }
            }

            distance = ship.coord.neighbor(ship.arg1).distanceTo(toGo.coord);
            travelTime = (int) (1 + Math.round(distance / 3.0));
            if (travelTime == deep) {
                bowCoord = getBowCoord(toGo.arg1, ship.coord);
                i = bowCoord.y * 100 + bowCoord.x;
                if (enemyCoord.get(i) == null) {
                    t = new ToGo(bowCoord, Action.WAIT, 1);
                    enemyCoord.put(i, t);
                } else {
                    t = enemyCoord.get(i);
                    t.fool++;
                }
            }

            distance = ship.coord.neighbor(ship.arg1).distanceTo(toGo.coord);
            travelTime = (int) (1 + Math.round(distance / 3.0));
            if (travelTime == deep) {
                sternCoord = getSternCoord(toGo.arg1, ship.coord);
                i = sternCoord.y * 100 + sternCoord.x;
                if (enemyCoord.get(i) == null) {
                    t = new ToGo(sternCoord, Action.WAIT, 1);
                    enemyCoord.put(i, t);
                } else {
                    t = enemyCoord.get(i);
                    t.fool++;
                }
            }
            if (toGo.nextTurns.size() != 0) {
                HashMap<Integer, ToGo> _enemyCoord = getEnemyCoord(toGo.nextTurns, ship, deep+1);
                for(Map.Entry<Integer, ToGo> entry:_enemyCoord.entrySet()) {
                    if (enemyCoord.get(entry.getKey()) == null) {
                        enemyCoord.put(entry.getKey(), entry.getValue());
                    } else {
                        entry.getValue().fool++;
                    }
                }
            }
        }
        return enemyCoord;
    }

    private void getShootBarrels() {
        //ArrayList<Barrel> sb = new ArrayList<>();
        int m1;
        int m2;
        for (Barrel barrel:barrels) {
            m1 = getMinDist(barrel, new ArrayList<>(myShips.values()));
            m2 = getMinDist(barrel, enemyShips);
            SysPrErr("m1 m2"+m1+" "+m2);
            if (m2 < m1) {
                boolean needShoot = true;
                for(CannonBall cannonBall:cannonBalls) {
                    if (cannonBall.coord.x == barrel.coord.x && cannonBall.coord.y == barrel.coord.y) {
                        needShoot = false;
                        break;
                    }
                }
                if (needShoot)
                    shootBarrels.add(barrel);
            }
        }

        //return sb;
    }

    private int getMinDist(Barrel barrel, ArrayList<Ship> ships) {
        int i=25;
        for (Ship ship:ships) {
            if (ship.coord.toCubeCoordinate().distanceTo(barrel.coord.toCubeCoordinate()) < i)
                i = ship.coord.toCubeCoordinate().distanceTo(barrel.coord.toCubeCoordinate());
        }
        return i;
    }

    Mine getNearestMine(Ship ship) {
        Mine nearestMine = null;
        for (Mine mine:mines) {
            //SysPrErr(mine.coord.x+" "+mine.coord.y);
            if (nearestMine == null) {
                nearestMine = mine;
                continue;
            }

            if (ship.coord.distanceTo(mine.coord) < ship.coord.distanceTo(nearestMine.coord)) nearestMine = mine;
        }
        return nearestMine;
    }

    Barrel getNearestBarrel(Ship ship) {
        //SysPrErr(barrels.get(0).coord.x+" "+barrels.get(0).coord.y);
        //SysPrErr(barrels.get(barrels.size()-1).coord.x+" "+barrels.get(barrels.size()-1).coord.y);
        Barrel nearestBarrel = null;
        for (Barrel barrel:barrels) {
            //SysPrErr(barrel.coord.x+" "+barrel.coord.y);
            if (nearestBarrel == null) {
                nearestBarrel = barrel;
                continue;
            }

            if (ship.coord.distanceTo(barrel.coord) < ship.coord.distanceTo(nearestBarrel.coord)) nearestBarrel = barrel;
        }
        return nearestBarrel;
    }

    private Ship getNearestShipToFire(Ship ship) {
        //сделать предсказание хоть какое
        Ship nearestShip = null;
        for (Ship enemyShip:enemyShips) {
            //SysPrErr(enemyShip.coord.x+" "+enemyShip.coord.y);
            if (nearestShip == null) {
                nearestShip = enemyShip;
                continue;
            }

            if (ship.coord.distanceTo(enemyShip.coord) < ship.coord.distanceTo(nearestShip.coord)) nearestShip = enemyShip;
        }
        return nearestShip;
    }

    private Ship getNearestShipToMove(Ship ship) {
        Ship nearestShip = null;
        for (Ship enemyShip:enemyShips) {
            //SysPrErr(enemyShip.coord.x+" "+enemyShip.coord.y);
            if (nearestShip == null) {
                nearestShip = enemyShip;
                continue;
            }

            if (ship.coord.distanceTo(enemyShip.coord) < ship.coord.distanceTo(nearestShip.coord)) nearestShip = enemyShip;
        }
        return nearestShip;
    }

    boolean needDodge(Ship ship) {
        boolean need = false;

        for (CannonBall cannonBall:cannonBalls) {
            if (ship.coord.x == cannonBall.coord.x && ship.coord.y == cannonBall.coord.y) {
                need = true;
                break;
            }
            Coord coord = getBowCoord(ship.arg1, ship.coord);
            if (coord.x == cannonBall.coord.x && coord.y == cannonBall.coord.y) {
                need = true;
                break;
            }

            coord = getSternCoord(ship.arg1, ship.coord);
            if (coord.x == cannonBall.coord.x && coord.y == cannonBall.coord.y) {
                need = true;
                break;
            }

            //Coord coord = getCoordToTime(ship, cannonBall.arg2);
        }

        return need;
    }

    private Coord getCoordToMove(Ship ship, int cells) {
        Coord coord = new Coord(ship.coord.x, ship.coord.y);
        int dx = 0;
        if (ship.coord.y % 2 == 0) {
            if (ship.arg1 == 2 || ship.arg1 == 4) dx = 1;
            if (cells == 2 && (ship.arg1 == 1 || ship.arg1 == 5)) dx = 1;
            //dx = Math.round(travelTime / 2);
        } else {
            if (ship.arg1 == 1 || ship.arg1 == 5) dx = 1;
            if (cells == 2 && (ship.arg1 == 2 || ship.arg1 == 4)) dx = 1;
            //dx = 1 + Math.round(travelTime / 2);
        }
        if (ship.arg1 == 0) {
            coord = new Coord(ship.coord.x + cells, ship.coord.y);
        }
        if (ship.arg1 == 1) {
            coord = new Coord(ship.coord.x + dx, ship.coord.y - cells);
        }
        if (ship.arg1 == 2) {
            coord = new Coord(ship.coord.x - dx, ship.coord.y - cells);
        }
        if (ship.arg1 == 3) {
            coord = new Coord(ship.coord.x - cells, ship.coord.y);
        }
        if (ship.arg1 == 4) {
            coord = new Coord(ship.coord.x - dx, ship.coord.y + cells);
        }
        if (ship.arg1 == 5) {
            coord = new Coord(ship.coord.x + dx, ship.coord.y + cells);
        }
        return coord;
    }

    Coord getCoordToTime(Ship ship, int time) {
        Coord coord = new Coord(ship.coord.x, ship.coord.y);

        return coord;
    }

    Coord getBowCoord(int arg1, Coord _coord) {
        Coord coord = new Coord(_coord.x, _coord.y);
        int dx = 0;
        if (coord.y % 2 == 0) {
            if (arg1 == 2 || arg1 == 4) dx = 1;
            //dx = Math.round(travelTime / 2);
        } else {
            if (arg1 == 1 || arg1 == 5) dx = 1;
            //dx = 1 + Math.round(travelTime / 2);
        }
        if (arg1 == 0) {
            coord = new Coord(coord.x + 1, coord.y);
        }
        if (arg1 == 1) {
            coord = new Coord(coord.x + dx, coord.y - 1);
        }
        if (arg1 == 2) {
            coord = new Coord(coord.x - dx, coord.y - 1);
        }
        if (arg1 == 3) {
            coord = new Coord(coord.x - 1, coord.y);
        }
        if (arg1 == 4) {
            coord = new Coord(coord.x - dx, coord.y + 1);
        }
        if (arg1 == 5) {
            coord = new Coord(coord.x + dx, coord.y + 1);
        }

        return coord;

    }

    private Coord getSternCoord(int arg1, Coord _coord) {
        Coord coord = new Coord(_coord.x, _coord.y);
        int dx = 0;
        if (coord.y % 2 == 0) {
            if (arg1 == 1 || arg1 == 5) dx = 1;
            //dx = Math.round(travelTime / 2);
        } else {
            if (arg1 == 2 || arg1 == 4) dx = 1;
            //dx = 1 + Math.round(travelTime / 2);
        }
        if (arg1 == 0) {
            coord = new Coord(coord.x - 1, coord.y);
        }
        if (arg1 == 1) {
            coord = new Coord(coord.x - dx, coord.y + 1);
        }
        if (arg1 == 2) {
            coord = new Coord(coord.x + dx, coord.y + 1);
        }
        if (arg1 == 3) {
            coord = new Coord(coord.x + 1, coord.y);
        }
        if (arg1 == 4) {
            coord = new Coord(coord.x + dx, coord.y - 1);
        }
        if (arg1 == 5) {
            coord = new Coord(coord.x - dx, coord.y - 1);
        }

        return coord;
    }

    private void simEnemyShip(int deep) {
        if (deep == 2) {
            enemyShipsTurn2.clear();
            for (Ship enemyShip : enemyShips) {
                enemyShipsTurn2.add(getShipToNewCoord(enemyShip, 1, enemyShip.arg2 == 0 ? Action.FASTER : Action.WAIT));
            }
        }
        if (deep == 3) {
            enemyShipsTurn3.clear();
            for (Ship enemyShip : enemyShipsTurn2) {
                enemyShipsTurn3.add(getShipToNewCoord(enemyShip, 1, enemyShip.arg2 == 0 ? Action.FASTER : Action.WAIT));
            }
        }
        if (deep == 4) {
            enemyShipsTurn4.clear();
            for (Ship enemyShip : enemyShipsTurn3) {
                enemyShipsTurn4.add(getShipToNewCoord(enemyShip, 1, enemyShip.arg2 == 0 ? Action.FASTER : Action.WAIT));
            }
        }
        if (deep == 5) {
            enemyShipsTurn5.clear();
            for (Ship enemyShip : enemyShipsTurn4) {
                enemyShipsTurn5.add(getShipToNewCoord(enemyShip, 1, enemyShip.arg2 == 0 ? Action.FASTER : Action.WAIT));
            }
        }
    }




    private void SysPrErr(String text) {
        if (DEBUG) System.err.println(text);
    }

    private void SysPrErr(int text) {
        if (DEBUG) System.err.println(text);
    }

    private void SysPrErr(boolean text) {
        if (DEBUG) System.err.println(text);
    }

    private class BasePoint {
        Coord coord;
        EntityType entityType;

        public BasePoint(int x, int y, EntityType entityType) {
            coord = new Coord(x,y);
            this.entityType = entityType;
        }
    }

    private class Barrel extends BasePoint{
        int arg1;

        public Barrel(int x, int y, int arg1) {
            super(x,y, EntityType.BARREL);
            this.arg1 = arg1;
            //pointOfGrafs.get(y*100+x).set(this, numberTurn);
        }
    }

    private class Ship  extends  BasePoint{
        int arg1;
        int arg2;
        int arg3;
        int arg4;
        boolean turn = false;
        boolean oldTurnFire = false;
        boolean isDead = false;
        int entityId;

        public Ship(int x, int y, int arg1, int arg2, int arg3, int arg4, int entityId) {
            super(x,y, EntityType.SHIP);
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
            this.arg4 = arg4;
            this.entityId = entityId;
            //pointOfGrafs.get(y*100+x).set(this, numberTurn);
        }

        public Ship(Ship ship) {
            super(ship.coord.x,ship.coord.y, EntityType.SHIP);
            this.arg1 = ship.arg1;
            this.arg2 = ship.arg2;
            this.arg3 = ship.arg3;
            this.arg4 = ship.arg4;
            this.entityId = ship.entityId;
            //pointOfGrafs.get(y*100+x).set(this, numberTurn);
        }

        public void set(int x, int y, int arg1, int arg2, int arg3, int arg4, boolean isDead) {
            this.coord = new Coord(x,y);
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
            this.arg4 = arg4;
            this.isDead = isDead;
            //pointOfGrafs.get(y*100+x).set(this, numberTurn);
        }

        public void set(Ship ship) {
            this.coord = new Coord(ship.coord.x,ship.coord.y);
            this.arg1 = ship.arg1;
            this.arg2 = ship.arg2;
            this.arg3 = ship.arg3;
            this.arg4 = ship.arg4;
            this.isDead = ship.isDead;
            //pointOfGrafs.get(y*100+x).set(this, numberTurn);
        }

    }

    private class Mine extends BasePoint {
        public Mine(int x, int y) {
            super(x,y, EntityType.MINE);
            //pointOfGrafs.get(y*100+x).set(this, numberTurn);
        }
    }

    private class CannonBall extends BasePoint {
        int arg1;
        int arg2;
        public CannonBall(int x, int y, int arg1, int arg2) {
            super(x,y, EntityType.CANNONBALL);
            this.arg1 = arg1;
            this.arg2 = arg2;
            //pointOfGrafs.get(y*100+x).set(this, numberTurn);
        }
    }

    private class ToGo extends BasePoint{
        Action action;
        int fool;
        ArrayList<ToGo> nextTurns = new ArrayList<>();
        //Ship ship;
        int arg1;

        public ToGo(Coord coord, Action action, int fool) {
            super(coord.x, coord.y, EntityType.SHIP);
            this.action = action;
            //this.ship = ship;
            this.fool = fool;
        }
    }

    public static class Coord {
        private final static int[][] DIRECTIONS_EVEN = new int[][] { { 1, 0 }, { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 } };
        private final static int[][] DIRECTIONS_ODD = new int[][] { { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, 0 }, { 0, 1 }, { 1, 1 } };
        private int x;
        private int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Coord(Coord other) {
            this.x = other.x;
            this.y = other.y;
        }

        public void set(Coord coord) {
            this.x = coord.x;
            this.y = coord.y;
        }
        public double angle(Coord targetPosition) {
            double dy = (targetPosition.y - this.y) * Math.sqrt(3) / 2;
            double dx = targetPosition.x - this.x + ((this.y - targetPosition.y) & 1) * 0.5;
            double angle = -Math.atan2(dy, dx) * 3 / Math.PI;
            if (angle < 0) {
                angle += 6;
            } else if (angle >= 6) {
                angle -= 6;
            }
            return angle;
        }

        CubeCoordinate toCubeCoordinate() {
            int xp = x - (y - (y & 1)) / 2;
            int zp = y;
            int yp = -(xp + zp);
            return new CubeCoordinate(xp, yp, zp);
        }

        Coord neighbor(int orientation) {
            int newY, newX;
            if (this.y % 2 == 1) {
                newY = this.y + DIRECTIONS_ODD[orientation][1];
                newX = this.x + DIRECTIONS_ODD[orientation][0];
            } else {
                newY = this.y + DIRECTIONS_EVEN[orientation][1];
                newX = this.x + DIRECTIONS_EVEN[orientation][0];
            }

            return new Coord(newX, newY);
        }

        boolean isInsideMap() {
            return x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT;
        }

        int distanceTo(Coord dst) {
            return this.toCubeCoordinate().distanceTo(dst.toCubeCoordinate());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Coord other = (Coord) obj;
            return y == other.y && x == other.x;
        }

    }

    private static class CubeCoordinate {
        static int[][] directions = new int[][] { { 1, -1, 0 }, { +1, 0, -1 }, { 0, +1, -1 }, { -1, +1, 0 }, { -1, 0, +1 }, { 0, -1, +1 } };
        int x, y, z;

        public CubeCoordinate(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Coord toOffsetCoordinate() {
            int newX = x + (z - (z & 1)) / 2;
            int newY = z;
            return new Coord(newX, newY);
        }

        CubeCoordinate neighbor(int orientation) {
            int nx = this.x + directions[orientation][0];
            int ny = this.y + directions[orientation][1];
            int nz = this.z + directions[orientation][2];

            return new CubeCoordinate(nx, ny, nz);
        }

        int distanceTo(CubeCoordinate dst) {
            return (Math.abs(x - dst.x) + Math.abs(y - dst.y) + Math.abs(z - dst.z)) / 2;
        }

    }

    private static enum EntityType {
        SHIP, BARREL, MINE, CANNONBALL
    }

    public static enum Action {
        FASTER, SLOWER, PORT, STARBOARD, FIRE, MINE, MOVE, WAIT
    }

    private class  PointOfGraf {
        int x;
        int y;
        int numberTurn;
        BasePoint basePoint;
        PointOfGraf[] edges = new PointOfGraf[6];

        public PointOfGraf(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setBP(BasePoint basePoint) {
            this.basePoint = basePoint;
        }

        public void setNumberTurn(int numberTurn) {
            this.numberTurn = numberTurn;
        }

        public void set(BasePoint basePoint, int numberTurn) {
            this.basePoint = basePoint;
            this.numberTurn = numberTurn;
        }
    }

}