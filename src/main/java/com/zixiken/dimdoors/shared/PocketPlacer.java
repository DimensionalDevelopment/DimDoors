/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

/**
 *
 * @author Robijnvogel
 */
class PocketPlacer { //there is exactly one pocket placer for each different schematic that is loaded into the game
    
    int size;
    //@todo about everything
    //this class should contain the actual schematic info, as well as some of the Json info (placement of Rifts and stuff)

    int getSize() {
        return size;
    }

    int place(int x, int y, int z, int dimID) { //actual coords
        //@todo generate a "bedrock" wall around the pocket and start generating the contents of the pocket at (1, 0, 1)
        //so pocket with size 1 is 14 * 14, size 2 is 30 * 30, size 3 is 46 * 46 etc.
        return 0;
    }
    
}
