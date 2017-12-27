package org.dimdev.dimdoors.shared.pockets;

/**
 *
 * @author s101426
 */
public enum EnumPocketType {

    PRIVATE, PUBLIC, DUNGEON;

    public static EnumPocketType getFromInt(int integer) {
        switch (integer) {
            case 0:
                return EnumPocketType.PRIVATE;
            case 1:
                return EnumPocketType.PUBLIC;
            case 2:
            default:
                return EnumPocketType.DUNGEON;
        }
    }

    public int getIntValue() {
        switch (this) {
            case PRIVATE:
                return 0;
            case PUBLIC:
                return 1;
            case DUNGEON:
            default:
                return 2;
        }
    }
}
