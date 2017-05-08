package npou.pokerChipRace;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * It's the survival of the biggest!
 * Propel your chips across a frictionless table top to avoid getting eaten by bigger foes.
 * Aim for smaller oil droplets for an easy size boost.
 * Tip: merging your chips will give you a sizeable advantage.
 **/
class Player {

    private final static boolean IS_DEBUG = true;

    //private HashMap<Integer, Entity> entitys = new HashMap<>();
    ArrayList<Entity> my = new ArrayList<>();
    ArrayList<Entity> enemy = new ArrayList<>();
    ArrayList<Entity> neutral = new ArrayList<>();

    public static void main(String args[]) {
        new Player().go();
    }

    void go() {
        Scanner in = new Scanner(System.in);
        int playerId = in.nextInt(); // your id (0 to 4)

        // game loop
        while (true) {
            my.clear();
            enemy.clear();
            neutral.clear();

            int playerChipCount = in.nextInt(); // The number of chips under your control
            int entityCount = in.nextInt(); // The total number of entities on the table, including your chips
            for (int i = 0; i < entityCount; i++) {
                int id = in.nextInt(); // Unique identifier for this entity
                int player = in.nextInt(); // The owner of this entity (-1 for neutral droplets)
                float radius = in.nextFloat(); // the radius of this entity
                float x = in.nextFloat(); // the X coordinate (0 to 799)
                float y = in.nextFloat(); // the Y coordinate (0 to 514)
                float vx = in.nextFloat(); // the speed of this entity along the X axis
                float vy = in.nextFloat(); // the speed of this entity along the Y axis
                if (player == -1)
                    neutral.add(new Entity(id, player, radius, x, y, vx ,vy));
                else {
                    if (player == playerId) my.add(new Entity(id, player, radius, x, y, vx, vy));
                    if (player != playerId) enemy.add(new Entity(id, player, radius, x, y, vx, vy));
                }

            }
            /*for (int i = 0; i < playerChipCount; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");


                // One instruction per chip: 2 real numbers (x y) for a propulsion, or 'WAIT'.
                System.out.println("0 0");
            }*/

            for(Entity entity:my) {
                boolean big = true;
                for (Entity n_e:enemy) {
                    if (entity.radius <= n_e.radius)
                        big = false;
                }
                if (!big) {
                    Entity eatEntity = getEat(entity);
                    if (eatEntity != null) {
                        System.out.println(eatEntity.x + " " + eatEntity.y);

                    } else {

                        System.out.println("WAIT");
                    }
                } else
                    System.out.println("WAIT");
            }
        }
    }

    private Entity getEat(Entity myEntity) {
        Entity eatEntity = null;
        for (Entity n_e:neutral) {
            if (myEntity.radius > n_e.radius) {
                if (eatEntity == null)
                    eatEntity = n_e;
                if (myEntity.getDist(n_e) < myEntity.getDist(eatEntity))
                    eatEntity = n_e;
            }
        }

        return eatEntity;
    }



    private class Entity {
        int id; // Unique identifier for this entity
        int player; // The owner of this entity (-1 for neutral droplets)
        float radius; // the radius of this entity
        float x; // the X coordinate (0 to 799)
        float y; // the Y coordinate (0 to 514)
        float vx; // the speed of this entity along the X axis
        float vy;

        public Entity(int id, int player, float radius, float x, float y, float vx, float vy) {
            this.id = id;
            this.player = player;
            this.radius = radius;
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        float getDist(Entity entity) {
            return (float) Math.sqrt((x-entity.x)*(x-entity.x)+(y-entity.y)*(y-entity.y));
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
