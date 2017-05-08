package npou.skynetRevolutionEpisode1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private final static boolean IS_DEBUG = true;

    private HashMap<Integer, Node> nodes = new HashMap<>();
    ArrayList<Node> gateways = new ArrayList<>();
    //ArrayList<BfsNode> openList = new ArrayList<>();
    ArrayList<Integer> closeList = new ArrayList<>();
    ArrayList<BfsNode> reachableList = new ArrayList<>();
    ArrayList<Severed> severedLinks = new ArrayList<>();

    int minTurns = 0;
    int n_nodes = 0;

    public static void main(String args[]) {
        new Player().go();
    }

    private void go() {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the total number of nodes in the level, including the gateways

        n_nodes = N;

        for (int i = 0; i < N; i++) {
            nodes.put(i, new Node(i));
        }

        int L = in.nextInt(); // the number of links
        int E = in.nextInt(); // the number of exit gateways
        for (int i = 0; i < L; i++) {
            int N1 = in.nextInt(); // N1 and N2 defines a link between these nodes
            int N2 = in.nextInt();
            nodes.get(N1).nodes.add(nodes.get(N2));
            nodes.get(N2).nodes.add(nodes.get(N1));
            SysPtErr(N1+" "+N2);
        }

        for (int i = 0; i < E; i++) {
            int EI = in.nextInt(); // the index of a gateway node
            gateways.add(nodes.get(EI));
            SysPtErr(EI);
        }

        // game loop
        while (true) {
            int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // Example: 0 1 are the indices of the nodes you wish to sever the link between
            /*System.out.println("11 5");
            System.out.println("6 0");
            System.out.println("11 7");*/

            //openList.clear();
            closeList.clear();
            reachableList.clear();

            String outPrintln = getOutPrintln(SI);
            System.out.println(outPrintln);
        }
    }


    private String getOutPrintln(int SI) {
        String outPrintln = "";
        ArrayList<BfsNode> ways = new ArrayList<>();
        for(Node node:gateways) {
            minTurns = n_nodes;
            BfsNode bfsNode = getBestWay(nodes.get(SI), node.n);
            if (bfsNode != null)
                ways.add(bfsNode);
        }

        ways.sort((o1, o2) -> (o1.turns - o2.turns));

        BfsNode bfsNode = ways.get(0);
        while (bfsNode.from != null && bfsNode.from.n != SI)
            bfsNode = bfsNode.from;

        //nodes.get(bfsNode.n).setSevered(true);
        addSevered(SI, bfsNode.n);

        SysPtErr("b/w "+bfsNode.n);
        outPrintln = SI + " "+bfsNode.n;
        return outPrintln;
    }

    private BfsNode getBestWay(Node node, int goalNode) {
        BfsNode bfsNode = null;
        reachableList.clear();
        closeList.clear();
        bfs(node, goalNode, 1, null);
        if (reachableList.size() != 0) {
            reachableList.sort((o1, o2) -> (o1.turns - o2.turns));
            bfsNode = reachableList.get(0);
        }
        return bfsNode;
    }

    private void bfs(Node startNode, int goalNode, int turn, BfsNode from) {
        closeList.add(startNode.n);
        if (turn > minTurns)
            return;

        SysPtErr("////");
        SysPtErr(startNode.n+" t-"+turn);
        ArrayList<Node> c = new ArrayList<>();
        for (Node node:startNode.nodes) {
            SysPtErr("n "+startNode.n+" "+node.n);
            if (isSevered(startNode.n, node.n))
                continue;
            //BfsNode currentNode = new BfsNode(from, turn, node.n);
            if (node.n == goalNode) {
                reachableList.add(new BfsNode(from, turn, node.n));
                if (minTurns > turn)
                    minTurns = turn;
                SysPtErr("r/l "+from+" "+turn);
            } else
            if (!closeList.contains(node.n)) {
                c.add(node);
                //bfs(node, goalNode, turn + 1, currentNode);
            }

        }

        for(Node n:c) {
            BfsNode currentNode = new BfsNode(from, turn, n.n);
            bfs(n, goalNode, turn + 1, currentNode);
        }
        SysPtErr("\\\\");
    }

    private boolean isSevered(int _N1, int _N2) {
        boolean isS = false;
        int N1;
        int N2;
        if (_N1>_N2) {
            N1 = _N2;
            N2 = _N1;
        } else {
            N1 = _N1;
            N2 = _N2;
        }

        for (Severed severed:severedLinks) {
            if (severed.N1 == N1 && severed.N2 == N2) {
                isS = true;
                break;
            }
        }

        return isS;
    }

    private void addSevered(int N1, int N2) {
        if (N1 > N2) {
            severedLinks.add(new Severed(N2, N1));
        } else {
            severedLinks.add(new Severed(N1, N2));
        }
    }


    private class Node {
        int n;
        //boolean severed=false;
        ArrayList<Node> nodes = new ArrayList<>();

        public Node(int n) {
            this.n = n;
        }

        public void addNode(Node node) {
            nodes.add(node);
        }

        public ArrayList<Node> getNodes() {
            return nodes;
        }

        /*public boolean isSevered() {
            return severed;
        }

        public void setSevered(boolean severed) {
            this.severed = severed;
        }*/
    }

    private class BfsNode {
        BfsNode from;
        int n;
        int turns;

        public BfsNode(BfsNode from, int turns, int n) {
            this.from = from;
            this.turns = turns;
            this.n = n;
        }
    }

    private class Severed {
        int N1;
        int N2;

        public Severed(int n1, int n2) {
            N1 = n1;
            N2 = n2;
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
