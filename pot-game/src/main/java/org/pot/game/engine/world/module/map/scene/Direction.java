package org.pot.game.engine.world.module.map.scene;

public enum Direction {
    NORTH {
        @Override
        public int iMove(int i, int vector) {
            return i + vector;
        }

        @Override
        public int jMove(int j, int vector) {
            return j - vector;
        }

    },
    SOUTH {
        @Override
        public int iMove(int i, int vector) {
            return i - vector;
        }

        @Override
        public int jMove(int j, int vector) {
            return j + vector;
        }

    },
    WEST {
        @Override
        public int iMove(int i, int vector) {
            return i - vector;
        }

        @Override
        public int jMove(int j, int vector) {
            return j - vector;
        }

    },
    EAST {
        @Override
        public int iMove(int i, int vector) {
            return i + vector;
        }

        @Override
        public int jMove(int j, int vector) {
            return j + vector;
        }

    },
    ;

    public abstract int iMove(int i, int vector);

    public abstract int jMove(int j, int vector);
}
