/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org>
 */

package org.dimdev.dimdoors.datagen;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.cloth.api.datagen.v1.LootTableData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;

public class LootTableConsumer implements DataProvider, LootTableData {
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator dataGenerator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final Table<LootContextType, Identifier, LootTable.Builder> lootTables = HashBasedTable.create();
    
    public LootTableConsumer(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }
    
    @Override
    public void register(LootContextType type, Identifier identifier, LootTable.Builder lootTable) {
        this.lootTables.put(type, identifier, lootTable);
    }

    @Override
    public void run(DataCache cache) {
        Path path = this.dataGenerator.getOutput();
        Map<Identifier, LootTable> map = Maps.newHashMap();
		this.lootTables.rowMap().forEach((type, tableMap) -> tableMap.forEach((identifier, builder) -> {
            if (map.put(identifier, builder.type(type).build()) != null) {
                throw new IllegalStateException("Duplicate loot table " + identifier);
            }
        }));
        
        map.forEach((identifier, lootTable) -> {
            Path outputPath = getOutput(path, identifier);
            
            try {
                DataProvider.writeToPath(GSON, cache, LootManager.toJson(lootTable), outputPath);
            } catch (IOException var6) {
                LOGGER.error("Couldn't save loot table {}", outputPath, var6);
            }
        });
    }
    
    private static Path getOutput(Path rootOutput, Identifier lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/loot_tables/" + lootTableId.getPath() + ".json");
    }
    
    @Override
    public String getName() {
        return "Loot Table Provider";
    }
}
