/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

/**
 *
 * @author s101426
 */
public enum EnumPocketType {
    PRIVATE, PUBLIC, DUNGEON;

    static EnumPocketType getFromInt(int integer) {
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

    int getIntValue() {
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
